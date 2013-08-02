/**
 * This file is part of Netconf4j.
 *
 * Netconf4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Netconf4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Netconf4j. If not, see <http://www.gnu.org/licenses/>.
 */
package net.i2cat.netconf.messageQueue;

import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.rpc.Abort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2cat.netconf.rpc.RPCElement;

public class MessageQueue {

	public static final String ABORT_MESSAGE_ID = "__ABORT_MESSAGE_ID__";
	static Log log = LogFactory.getLog(MessageQueue.class);
	Vector<MessageQueueListener>		listeners;
	LinkedHashMap<String, RPCElement>	queue;

	public MessageQueue() {
		listeners = new Vector<MessageQueueListener>();
		queue = new LinkedHashMap<String, RPCElement>();
	}

	/**
	 * put() element in (internal) queue, but triggers event to listeners before returning.
	 * 
	 * This methods is thread safe.
	 */
	public RPCElement put(String key, RPCElement value) {

		RPCElement element;

		synchronized (queue) {
			log.debug("Received new message (" + value.getMessageId() + ")(waking up waiting threads)");
			element = queue.put(key, value);
			queue.notifyAll();
		}

		log.debug("Notify listeners");
		for (MessageQueueListener listener : listeners)
			listener.receiveRPCElement(element);

		return element;
	}

	/**
	 * Commodity method. Same as put(k,v) but takes the key by calling getMessageId from the value.
	 * 
	 * @param value
	 * @return
	 */
	public RPCElement put(RPCElement value) {
		return put(value.getMessageId(), value);
	}

	public void addListener(MessageQueueListener listener) {
		log.debug("New listener");
		listeners.add(listener);
	}

	public RPCElement consume() {
		synchronized (queue) {
			RPCElement element = null;
			if (queue.keySet().iterator().hasNext()) {
				element = queue.remove(queue.keySet().iterator().next()); // get first (older)
			}
			if (element != null)
				log.debug("Consuming message");
			return element;
		}
	}

	public RPCElement consumeById(String messageId) {
		synchronized (queue) {
			RPCElement element = queue.remove(messageId); // get first (older)
			if (element != null)
				log.debug("Consuming message with id " + messageId);
			return element;
		}
	}

	// TODO: Create a version that takes a timeout.
	public RPCElement blockingConsume() {

		RPCElement element;

		synchronized (queue) {
			while ((element = consume()) == null) {
				try {
					log.debug("Waiting...");
					queue.wait();
				} catch (InterruptedException e) {
					log.warn("Interrupted exception");
				}
			}
		}
		return element;
	}

	public RPCElement blockingConsumeById(String messageId) throws Exception {
		return blockingConsumeById(messageId, 0);
	}

	/**
	 * Wait for a new message with the id <code>messageId</code> to arrive in the queue.
	 *
	 * @param messageId a string identifying the message to consume.
	 * @param timeout a long indicating the length of the timeout in milliseconds. If zero or less, no timeout.
	 * @throws Exception an UncheckedTimeoutException if there is no message with <code>messageId</code> after waiting for the specified timeout.
	 * @return
	 */
	public RPCElement blockingConsumeById(String messageId, long timeout) throws Exception {

		final String messageIdFinal = messageId;
		Callable<RPCElement> consumeCaller = new Callable<RPCElement>() {
			public RPCElement call() throws Exception {
				RPCElement element;
				RPCElement abortElement;
				synchronized (queue) {
					while ((element = consumeById(messageIdFinal)) == null) {
						if ((abortElement = consumeById(ABORT_MESSAGE_ID)) != null) {
							Abort abort = (Abort)abortElement;
							throw new TransportException("ABORTING: blockingConsumeById(" + messageIdFinal + "): "
								        		 + abort.getMessage(), abort.getException());
						}
						try {
							log.debug("Waiting (" + messageIdFinal + ")...");
							queue.wait();
						} catch (InterruptedException e) {
							// Do nothing. It's probably a timeout.
						}
					}
				}
				return element;
			}
		};

		if (timeout <= 0) {
			return consumeCaller.call();
		}

		SimpleTimeLimiter timeLimiter = new SimpleTimeLimiter();

		try {
			return timeLimiter.callWithTimeout(consumeCaller, timeout, TimeUnit.MILLISECONDS, true);
		} catch (UncheckedTimeoutException e) {
			log.debug("BlockingConsumeById(messageId=" + messageId + ") failed due to timeout.", e);
			throw new UncheckedTimeoutException(); 
		} catch (Exception e) {
			log.debug("BlockingConsumeById(messageId=" + messageId + ") failed.", e);
			throw e;
		}
	}
}
