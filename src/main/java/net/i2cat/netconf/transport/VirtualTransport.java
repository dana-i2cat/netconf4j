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
package net.i2cat.netconf.transport;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.rpc.RPCElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class VirtualTransport implements Transport {

	private Log					log			= LogFactory.getLog(VirtualTransport.class);

	// URI transportId;
	Connection					connection;
	Session						session;

	String						user;
	String						password;
	String						host;
	int							port;
	String						subsystem;

	boolean						closed		= true;

	Vector<TransportListener>	listeners;

	Thread						parserThread;
	XMLReader					parser;
	TransportContentParser		xmlHandler;

	MessageQueue				queue;

	DummySimulatorHelper		simHelper;

	final String				delimiter	= "]]>]]>";

	public VirtualTransport() {
		listeners = new Vector<TransportListener>();

		xmlHandler = new TransportContentParser();

		// populate
		try {
			parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(xmlHandler);
			parser.setErrorHandler(xmlHandler);
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public void setMessageQueue(MessageQueue queue) {
		this.queue = queue;
		xmlHandler.setMessageQueue(queue); // set the queue this handler will
	}

	public void addListener(TransportListener handler) {
		listeners.add(handler);
	}

	public void connect(SessionContext sessionContext) throws TransportException {
		// let the transportId instance be GC'ed after connect();
		// this.transportId = transportId;
		log.info("Virtual transport");

		// TODO CHECK IF IT WORKS
		user = sessionContext.getUser();
		password = sessionContext.getPass();

		host = sessionContext.getHost();
		port = sessionContext.getPort();
		if (port < 0)
			port = 22;

		subsystem = sessionContext.getSubsystem();

		log.debug("user: " + user);
		log.debug("pass: " + (password != null ? "yes" : "no"));
		log.debug("hostname: " + host + ":" + port);
		log.debug("subsystem: " + subsystem);

		simHelper = new DummySimulatorHelper();

		// Check the type of responses
		simHelper.setResponseError(subsystem.equals("errorServer"));

		// notify handlers
		for (TransportListener handler : listeners)
			handler.transportOpenned();

	}

	public void disconnect() {
		log.info("Virtual disconnection");

		// notify handlers
		for (TransportListener handler : listeners)
			handler.transportClosed();

	}

	public void sendAsyncQuery(RPCElement query) {
		log.info("Virtual send asynchronous query");
		// chose response
		log.info("Operation sent");
		String strResponse = simHelper.generateReply(query);

		try {
			parser.parse(new InputSource(new StringReader(strResponse)));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		log.info("Operation received");

	}

}
