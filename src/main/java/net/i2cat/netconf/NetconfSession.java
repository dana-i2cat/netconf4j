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
package net.i2cat.netconf;

import java.net.URI;
import java.util.ArrayList;

import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.errors.TransportNotImplementedException;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.messageQueue.MessageQueueListener;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Hello;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.RPCElement;
import net.i2cat.netconf.rpc.Reply;
import net.i2cat.netconf.transport.Transport;
import net.i2cat.netconf.transport.TransportFactory;
import net.i2cat.netconf.transport.TransportListener;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetconfSession implements TransportListener, MessageQueueListener, INetconfSession {

	// TimerKeepAlive timerKeepAlive;

	public static final int	PERIOD	= 3;										// PERIOD
	// OF
	// 3
	// MINS

	private Log				log		= LogFactory.getLog(NetconfSession.class);

	//
	SessionContext			sessionContext;

	Transport				transport;
	// URI transportId;

	String					sessionId;

	MessageQueue			messageQueue;

	public NetconfSession(SessionContext sessionContext) throws TransportNotImplementedException, ConfigurationException {
		this.sessionContext = sessionContext;

		URI uri = sessionContext.getURI();

		if (uri.getScheme() == null ||
				uri.getHost() == null ||
				uri.getUserInfo() == null)
			throw new ConfigurationException("Insufficient information in session context's URI: " + uri);

		TransportFactory.checkTransportType(sessionContext.getURI().getScheme()); // throws TNIE
	}

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#connect()
	 */
	public void connect() throws TransportException, NetconfProtocolException {
		RPCElement reply;
		Hello clientHello;
		Hello serverHello;

		ArrayList<Capability> activeCapabilities;
		ArrayList<Capability> clientCapabilities;
		ArrayList<Capability> serverCapabilities;

		messageQueue = new MessageQueue();
		messageQueue.addListener(this);

		try {
			transport = TransportFactory.getTransport(sessionContext.getURI().getScheme());
		} catch (TransportNotImplementedException e) {
			TransportException te = new TransportException(e.getMessage());
			te.initCause(e);
			throw te;
		}

		transport.setMessageQueue(messageQueue);
		transport.addListener(this);

		// initialize message id
		sessionContext.setLastMessageId(0);

		transport.connect(sessionContext);
		log.info("Transport connected");

		clientHello = new Hello();
		clientCapabilities = Capability.getSupportedCapabilities();
		clientHello.setCapabilities(clientCapabilities);

		log.info("Sending hello");
		transport.sendAsyncQuery(clientHello);

		reply = messageQueue.blockingConsumeById("0"); // <hello> has no
		// message-id, it is
		// indexed under 0.

		if (!(reply instanceof Hello))
			throw new NetconfProtocolException("First element received from server is not a <hello> message.");
		else
			serverHello = (Hello) reply;

		log.info("Received server hello.");
		this.sessionId = serverHello.getSessionId();

		// trim to common capabilities.
		serverCapabilities = serverHello.getCapabilities();
		activeCapabilities = (ArrayList<Capability>) clientCapabilities.clone();
		activeCapabilities.retainAll(serverCapabilities);

		log.debug("ACT_CAP " + activeCapabilities);

		sessionContext.setActiveCapabilities(activeCapabilities);
		sessionContext.setClientCapabilities(clientCapabilities);
		sessionContext.setServerCapabilities(serverCapabilities);

		log.info("Session " + this.sessionId + " opened with:");
		for (Capability capability : activeCapabilities)
			log.info(" - Capability: " + capability);

		/* Activate flags */

		// Activate keep Alive command
		// timerKeepAlive = new TimerKeepAlive(this);
		// timerKeepAlive.start(PERIOD);

	}

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#disconnect()
	 */
	public void disconnect() throws TransportException {
		// if (timerKeepAlive != null)
		// timerKeepAlive.close();
		transport.disconnect();
	}

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#sendSyncQuery(net.i2cat.netconf.rpc.Query)
	 */
	public Reply sendSyncQuery(Query query) throws TransportException {

		log.info("Sending query (" + query.getOperation().getName() + ")");

		query.setMessageId(generateMessageId());
		// validate(query);
		log.debug("--------------------------------------------------");
		log.debug("sending QUERY");
		log.debug(query.toXML());
		log.debug("--------------------------------------------------");

		transport.sendAsyncQuery(query);

		log.info("Sent. Waiting for response...");
		Reply reply = (Reply) messageQueue.blockingConsumeById(query.getMessageId());
		log.debug("--------------------------------------------------");
		log.debug("receiving REPLY ");
		log.debug(reply.getContain());
		log.debug("--------------------------------------------------");

		log.info("Reply received");

		return reply;
	}

	// private void validate(Query query) {
	// // TODO, check that the content of this query object follow the base
	// // netconf rules.
	// // check active capabilities too, to see additional constrains.
	// }

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#sendAsyncQuery(net.i2cat.netconf.rpc.Query)
	 */
	public void sendAsyncQuery(Query query) throws TransportException {
		query.setMessageId(generateMessageId());
		log.debug("--------------------------------------------------");
		log.debug("sending QUERY");
		log.debug(query.toXML());
		log.debug("--------------------------------------------------");

		// timerKeepAlive.reset(); // Reset the time for the keep alive

		transport.sendAsyncQuery(query);

	}

	private String generateMessageId() {
		sessionContext.setLastMessageId(sessionContext.getLastMessageId() + 1);
		return Integer.toString(sessionContext.getLastMessageId());
	}

	/*
	 * Facade methods
	 */

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#loadConfiguration(org.apache.commons.configuration.Configuration)
	 */
	public void loadConfiguration(Configuration source) {
		sessionContext.newConfiguration(source);
	}

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#getActiveCapabilities()
	 */
	public ArrayList<Capability> getActiveCapabilities() {
		return sessionContext.getActiveCapabilities();
	}

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#getClientCapabilities()
	 */
	public ArrayList<Capability> getClientCapabilities() {
		return sessionContext.getClientCapabilities();
	}

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#getServerCapabilities()
	 */
	public ArrayList<Capability> getServerCapabilities() {
		return sessionContext.getServerCapabilities();
	}

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#registerTransportListener(net.i2cat.netconf.transport.TransportListener)
	 */
	public void registerTransportListener(TransportListener handler) {
		transport.addListener(handler);
	}

	/* (non-Javadoc)
	 * @see net.i2cat.netconf.INetconfSession#registerMessageQueueListener(net.i2cat.netconf.messageQueue.MessageQueueListener)
	 */
	public void registerMessageQueueListener(MessageQueueListener handler) {
		messageQueue.addListener(handler);
	}

	/*
	 * Listeners
	 */

	// TransportListener
	public void transportOpenned() {
		log.info("Transport openned event");
	}

	// TransportListener
	public void transportClosed() {
		log.info("Transport closed event");
		// no need to close the transport, it is already closed
	}

	// MessageQueueListener
	public void receiveRPCElement(RPCElement element) {
		log.info("receive RPCElement event");

	}
}
