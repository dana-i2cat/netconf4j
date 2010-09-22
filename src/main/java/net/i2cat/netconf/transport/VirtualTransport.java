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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Vector;

import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.rpc.RPCElement;

public class VirtualTransport implements Transport {

	private Log					log			= LogFactory.getLog(VirtualTransport.class);

	SessionContext				sessionContext;

	// fake connection
	PipedOutputStream			outStream;
	PipedInputStream			inStream;

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
		xmlHandler.setMessageQueue(queue); // set the queue this handler will add messages to
	}

	public void addListener(TransportListener handler) {
		listeners.add(handler);
	}

	public void connect(SessionContext sessionContext) throws TransportException {
		// let the transportId instance be GC'ed after connect();
		// this.transportId = transportId;
		log.info("Virtual transport");

		this.sessionContext = sessionContext;

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

		// this will fake the two endpoints of a tcp connection
		try {
			outStream = new PipedOutputStream();
			inStream = new PipedInputStream(outStream);

		} catch (IOException e) {
			throw new TransportException(e.getMessage());
		}

		closed = false;

		// notify handlers
		for (TransportListener handler : listeners)
			handler.transportOpenned();

		startParsing();
	}

	public void disconnect() {
		log.info("Virtual disconnection");

		stopParsing();

		closed = true;

		// notify handlers
		for (TransportListener handler : listeners)
			handler.transportClosed();
	}

	public void sendAsyncQuery(RPCElement query) {
		// chose response

		try {
			outStream.write(simHelper.generateReply(query).getBytes());
		} catch (IOException e) {
			// Can't throw:
			// throw new TransportException(e.getMessage());
			log.error(e.getMessage());
		}

		log.info("Operation sent, response text queued for parsing");
	}

	private void startParsing() {

		parserThread = new Thread("Parser") {

			// private Log log = LogFactory.getLog(parserThread.class);

			@Override
			public void run() {
				log.debug("Parsing thread start");

				while (!closed)
				{
					try {
						log.debug("Starting parser.");

						// flag to log server response
						if (sessionContext.isLogRespXML())
							parser.parse(new InputSource(new TeeInputStream(inStream, new FileOutputStream(sessionContext.getLogFileXML()))));
						else
							parser.parse(new InputSource(inStream));

					} catch (InterruptedIOException ie) {
						log.warn("Got and InterruptedIOException from inside the parser. If you are closing it may be normal.");
					} catch (IOException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						if (e.getMessage().contentEquals("Content is not allowed in trailing section.")) {
							log.debug("Detected netconf delimiter.");
							// Using shitty non-xml delimiters forces us to detect
							// end-of-frame delimiter by a SAX error.
							// Do nothing will just restart the parser.
							// Blame netconf
						}
						else {
							log.error(e.getMessage());
							e.printStackTrace();
							disconnect();
						}
						log.info("End of parsing.");
					}
					log.debug("Looping");
				}
				log.debug("Parsing thread ended");
			}
		};

		parserThread.start();
	}

	private void stopParsing() {
		parserThread.interrupt();
	}
}
