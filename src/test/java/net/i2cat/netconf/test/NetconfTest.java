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
package net.i2cat.netconf.test;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.errors.TransportNotImplementedException;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

public class NetconfTest {

	private Log	log	= LogFactory.getLog(NetconfTest.class);

	private NetconfSession initNetconf() {
		URI lola;
		NetconfSession netconf = null;
		try {
			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(new URI("ssh://root:mant6WWe@lola.hea.net:22/netconf"));
			netconf = new NetconfSession(sessionContext);

			netconf.loadConfiguration(new PropertiesConfiguration("netconf-default.properties"));

			/* these errors can not happen */
		} catch (URISyntaxException e) {

			log.error("Error with a syntaxis");
			fail(e.getMessage());
		} catch (TransportNotImplementedException e) {
			log.error("Error with the transport not implemented");
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return netconf;
	}

	@Test
	public void connectGetConfDisconnect() {
		try {
			URI lola = new URI("ssh://root:mant6WWe@lola.hea.net:22/netconf");

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(new URI("ssh://root:mant6WWe@lola.hea.net:22/netconf"));

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();

			Reply reply = session.sendSyncQuery(QueryFactory.newGetConfig("running", null,
					null));

			reply = session.sendSyncQuery(QueryFactory.newCloseSession());

			session.disconnect();

		} catch (TransportException e) {
			e.printStackTrace();
		} catch (NetconfProtocolException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (TransportNotImplementedException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void checkReply(Reply reply, String messageId) {
		/* Check message id */
		if (!reply.getMessageId().equals(messageId)) {
			fail("message id " + reply.getMessageId() + "=" + messageId + '\n');
		}

		if (reply.getErrors().size() > 0) {
			for (net.i2cat.netconf.rpc.Error error : reply.getErrors()) {
				log.error("Message" + '\n');
				log.error("Info: " + error.getInfo() + '\n');
				log.error("Message: " + error.getMessage() + '\n');
				log.error("Path: " + error.getPath() + '\n');
				log.error("Severity: " + error.getSeverity() + '\n');
				log.error("Tag: " + error.getTag() + '\n');
				log.error("Type: " + error.getType() + '\n');
				log.error("-----------------------------" + '\n');
			}
			fail("It received errors from our router");
		}

		log.info("The message received is: " + '\n');
		log.info(reply.toXML() + '\n');

	}

	public NetconfTest() {
		log.info("Netconf test");
	}

	@Test
	public void connectDisconnect() {
		NetconfSession netconfSession = initNetconf();
		try {
			netconfSession.connect();
			netconfSession.disconnect();
		} catch (TransportException e) {
			log.error("Error with transport");
			fail(e.getMessage());
		} catch (NetconfProtocolException e) {
			log.error("Error with netconf protocol");
			fail(e.getMessage());
		}
	}

	public void sendSyncQueryTest() {
		NetconfSession netconfSession = initNetconf();
		try {
			netconfSession.connect();
			Query query = new Query();
			checkReply(netconfSession.sendSyncQuery(query), query
					.getMessageId());
			// Check received messages
			netconfSession.disconnect();
		} catch (TransportException e) {
			log.error("Error with transport");
			fail(e.getMessage());
		} catch (NetconfProtocolException e) {
			log.error("Error with netconf protocol");
			fail(e.getMessage());
		}

	}

	public void sendSyncQueryTestBad() {
		NetconfSession netconfSession = initNetconf();
		try {
			netconfSession.connect();
			Query query = new Query();
			checkReply(netconfSession.sendSyncQuery(query), query
					.getMessageId());
			// TODO Force to receive a bad message, message-id is incorrect
			// Check received messages
			netconfSession.disconnect();
		} catch (TransportException e) {
			log.error("Error with transport");
			fail(e.getMessage());
		} catch (NetconfProtocolException e) {
			log.error("Error with netconf protocol");
			fail(e.getMessage());
		}
	}

	//
	// public void AsyncQueryAndReceiveReply() {
	// NetconfSession netconfSession = initNetconf();
	//
	// }
	//
	// public void AsyncQueryAndReceiveReplyBad() {
	// NetconfSession netconfSession = initNetconf();
	//
	// }

	public void CheckCapabilitiesTest() {
		NetconfSession netconfSession = initNetconf();
		try {
			netconfSession.connect();
			Vector<Capability> capabilityVector = netconfSession
					.getActiveCapabilities();
			Vector<Capability> baseVectorCapability = new Vector<Capability>();
			baseVectorCapability.add(Capability.BASE);
			// Check received messages
			netconfSession.disconnect();
			/*
			 * If it is not contain the base capability, it is impossible to check capabilities
			 */
			boolean isChecked = capabilityVector.contains(baseVectorCapability);
			Assert.assertTrue(isChecked);

		} catch (TransportException e) {
			log.error("Virtual transport error");
			fail(e.getMessage());
		} catch (NetconfProtocolException e) {
			log.error("Virtual netconf protocol error");
			fail(e.getMessage());
		}

	}

	public void CheckMessageConsistency() {

		/*
		 * Check message consistencies
		 */

		NetconfSession netconfSession = initNetconf();
		try {
			netconfSession.connect();
			Query query = new Query();
			checkReply(netconfSession.sendSyncQuery(query), query
					.getMessageId());
			// TODO Force to receive a bad message, message-id is incorrect
			// Check received messages
			netconfSession.disconnect();
		} catch (TransportException e) {
			log.error("Error with transport");
			fail(e.getMessage());
		} catch (NetconfProtocolException e) {
			log.error("Error with netconf protocol");
			fail(e.getMessage());
		}

	}

}
