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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Vector;

import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.SessionContext.AuthType;
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
import ch.ethz.ssh2.ConnectionMonitor;
import ch.ethz.ssh2.Session;

public class SSHTransport implements Transport, ConnectionMonitor {

	private Log					log			= LogFactory.getLog(SSHTransport.class);

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

	SessionContext				sessionContext;

	final String				delimiter	= "]]>]]>";

	public SSHTransport() {
		listeners = new Vector<TransportListener>();

		xmlHandler = new TransportContentParser();

		// populate
		try {
			parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(xmlHandler);
			parser.setErrorHandler(xmlHandler);
		} catch (SAXException e) {
			log.error(e.getMessage());
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
		try {
			// let the transportId instance be GC'ed after connect();
			// this.transportId = transportId;

			// TODO CHECK IF IT WORKS
			this.sessionContext = sessionContext;
			user = sessionContext.getUser();
			password = sessionContext.getPass();

			host = sessionContext.getHost();
			port = sessionContext.getPort();
			if (port <= 0)
				port = 22;

			subsystem = sessionContext.getSubsystem();
			if (subsystem == null || subsystem.contentEquals("")) {
				subsystem = "netconf"; // default subsystem for juniper devices.
			}

			log.debug("user: " + user);
			log.debug("pass: " + (password != null ? "yes" : "no"));
			log.debug("hostname: " + host + ":" + port);
			log.debug("subsystem: " + subsystem);

			connection = new Connection(host, port);

			connection.addConnectionMonitor(this);

			log.info("Connecting to " + host);
			connection.connect();

			log.debug("Authentication");
			authenticate(connection, sessionContext);

			/* Opening session */
			log.debug("Opening session");
			session = connection.openSession();

			log.debug("Starting subsystem");
			session.startSubSystem(subsystem);

			closed = false;
			log.info("Connected");

			// notify handlers
			for (TransportListener handler : listeners)
				handler.transportOpenned();

			startParsing();

		} catch (IOException e) {
			TransportException te = new TransportException("IOException: " + e.getMessage());
			te.initCause(e);
			throw te;
		}
	}

	public void disconnect() {
		log.info("Disconnecting");

		log.debug("Closing session - exit status: " + session.getExitStatus());
		session.close();

		log.debug("Closing connection");
		closed = true;
		connection.close();

		log.info("Disconnected");

		// notify handlers
		for (TransportListener handler : listeners)
			handler.transportClosed();
	}

	public void connectionLost(Throwable t) {
		log.error("Connection with " + host + " lost:" + t.getMessage());

		// notify handlers
		for (TransportListener handler : listeners)
			handler.transportClosed();
	}

	public void sendAsyncQuery(RPCElement query) {

		PrintWriter writer = new PrintWriter(session.getStdin());
		String op = query.toXML() + "\n" + delimiter;

		writer.println(op);
		writer.flush();

		log.debug("Operation sent:\n " + op);
	}

	public boolean isParsingActive() {
		return parserThread.isAlive();
	}
	
	
	private boolean authenticate(Connection connection, SessionContext sessionContext) throws TransportException, IOException {
		
		SessionContext.AuthType authType = sessionContext.getAuthenticationType();
		boolean authenticated = false;
		if (authType.equals(AuthType.PASSWORD)){
			authenticated = authenticateWithPassword(connection, sessionContext);
		} else if (authType.equals(AuthType.PUBLICKEY)){
			authenticated = authenticateWithPublicKey(connection, sessionContext);
		} else {
			throw new TransportException("Unsupported authentication mechanism " + authType);
		}
		
		if (! authenticated) {
			throw new TransportException("Authentication Error)");
		}
		
		return authenticated;
	}

	private boolean authenticateWithPassword(Connection connection,
			SessionContext sessionContext) throws IOException {
		
		String user = sessionContext.getUser();
		String password = sessionContext.getPass();
		
		log.debug("Authenticate with password");
		log.debug("user: " + user);
		log.debug("pass: " + (password != null ? "yes" : "no"));
		
		return connection.authenticateWithPassword(user, password);
	}
	
	private boolean authenticateWithPublicKey(Connection connection,
			SessionContext sessionContext) throws IOException {
		
		String user = sessionContext.getKeyUsername();
		String keyPEMFileLocation = sessionContext.getKeyLocation();
		String keyPassword = sessionContext.getKeyPassword();
		
		log.debug("Authenticate with public key");
		log.debug("user: " + user);
		log.debug("keyLocation: " + keyPEMFileLocation);
		log.debug("keyPass: " + (keyPassword != null ? "yes" : "no"));
		
		File pemFile = new File(keyPEMFileLocation);
		
		return connection.authenticateWithPublicKey(user, pemFile, keyPassword);
	}

	private void startParsing() {

		parserThread = new Thread("Parser") {

			// private Log log = LogFactory.getLog(parserThread.class);

			@Override
			public void run() {
				log.debug("Parsing thread start");
				BufferedReader reader = null;
				while (!closed)
				{
					try {

						String buffer = "";
						reader = new BufferedReader(new InputStreamReader(session.getStdout()));

						do {
							buffer += reader.readLine();
						} while (!buffer.endsWith(delimiter) && !closed);

						parser.parse(new InputSource(new StringReader(buffer)));

						/*
						 * // flag to log server response if (sessionContext.isLogRespXML()) { log.debug("Logging to " +
						 * sessionContext.getLogFileXML()); parser.parse(new InputSource(new BufferedReader(new InputStreamReader(new
						 * TeeInputStream(session.getStdout(), new FileOutputStream(sessionContext.getLogFileXML()), true))))); } else {
						 * parser.parse(new InputSource(new BufferedReader(new InputStreamReader(session.getStdout()) { public int read(char[] cbuf,
						 * int offset, int length) throws IOException { log.debug("char:" + new String(cbuf, offset, length)); return super.read(cbuf,
						 * offset, length); } }))); }
						 */
					} catch (IOException e) {
						log.error(e.getMessage());
					} catch (SAXException e) {
						if (e.getMessage().contentEquals("Content is not allowed in trailing section.")) {
							// Using shitty non-xml delimiters forces us to detect
							// end-of-frame by a SAX error.
							// Do nothing will just restart the parser.
							// Blame netconf
						}
						else {
							log.error(e.getMessage());
							disconnect();
						}
						log.info("End of parsing.");
					} finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException e) {
								log.error(e.getMessage());
							}
						}
					}
					log.debug("Looping");
				}
				log.debug("Parsing thread ended");
			}
		};
		parserThread.start();
	}
}
