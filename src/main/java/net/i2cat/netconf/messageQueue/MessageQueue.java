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

import net.i2cat.netconf.rpc.RPCElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageQueue {

	static Log							log	= LogFactory.getLog(MessageQueue.class);
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
			log.debug("Received new message (" + value.getMessageId() + ")(waking up waiting threats)");
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
			RPCElement element = queue.remove(queue.keySet().iterator().next()); // get first (older)
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

	public RPCElement blockingConsume() {

		RPCElement element;

		synchronized (queue) {
			while ((element = consume()) == null) {
				try {
					log.debug("Waiting...");
					queue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return element;
	}

	public RPCElement blockingConsumeById(String messageId) {

		RPCElement element;

		synchronized (queue) {
			while ((element = consumeById(messageId)) == null) {
				try {
					log.debug("Waiting (" + messageId + ")...");
					queue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return element;
	}
}
