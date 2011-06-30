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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.messageQueue.MessageQueueListener;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.RPCElement;
import net.i2cat.netconf.rpc.Reply;

public class BaseNetconfTest {
	private Log				log	= LogFactory.getLog(BaseNetconfTest.class);

	static SessionContext	sessionContext;
	static NetconfSession	session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		sessionContext = new SessionContext();
		sessionContext.setURI(new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
															"mock://foo:bar@foo:22/okServer")));

		session = new NetconfSession(sessionContext);
		session.connect();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionContext = null;
		session.disconnect();
		session = null;
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSessionContext() throws Exception {
		SessionContext sessionContext = new SessionContext();
		sessionContext.newConfiguration(new PropertiesConfiguration("test-default.properties"));
		log.info("LOG FILE: " + sessionContext.getLogFileXML());
		log.info("LOG RESPONSE: " + sessionContext.isLogRespXML());
		log.info("NUMBER CONFIGS: " + sessionContext.getNumberOfConfigurations());

	}

	@Test
	public void testCapabilities() {

		ArrayList<Capability> activeCapabilities = session.getActiveCapabilities();
		ArrayList<Capability> clientCapabilities = session.getClientCapabilities();
		ArrayList<Capability> serverCapabilities = session.getServerCapabilities();

		assertTrue("Base capability must be supported and active", activeCapabilities.contains(Capability.BASE));
		assertTrue("There is an active capability that we don't support", clientCapabilities.containsAll(activeCapabilities));
		assertTrue("There is an active capability that the server doesn't support", serverCapabilities.containsAll(activeCapabilities));

		ArrayList<Capability> commonCapabilities = new ArrayList<Capability>(clientCapabilities);
		commonCapabilities.retainAll(serverCapabilities);

		assertTrue("Active capabilities equal common client/server capabilities", commonCapabilities.containsAll(activeCapabilities) &&
																					activeCapabilities.containsAll(commonCapabilities));
	}

	@Test
	public void testMessageId() {

		Query query = QueryFactory.newKeepAlive();

		try {
			Reply reply = session.sendSyncQuery(query);

			assertTrue("Query/Reply message id not the same", query.getMessageId().contentEquals(reply.getMessageId()));

		} catch (TransportException e) {
			fail("Got a TransportException: " + e.getMessage());
			e.printStackTrace();
		}
	}

	class TestMessageQueueListener implements MessageQueueListener {

		boolean	isReceived;

		public TestMessageQueueListener() {
			this.isReceived = false;
		}

		public void receiveRPCElement(RPCElement element) {
			isReceived = true;

		}

		public boolean isReceived() {
			return isReceived;
		}
	}

	// 2 second timeout
	@Test(timeout = 2000)
	public void testSendAsyncQuery() {

		Query query = QueryFactory.newKeepAlive();

		TestMessageQueueListener testMsgQueueList = new TestMessageQueueListener();
		session.registerMessageQueueListener(testMsgQueueList);

		try {
			session.sendAsyncQuery(query);
			while (!testMsgQueueList.isReceived()) {
				Thread.sleep(1000);
			}

		} catch (TransportException e) {
			fail(e.getMessage());
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void checkCapabilitiesTest() {
		ArrayList<Capability> capabilityVector = session
				.getActiveCapabilities();
		ArrayList<Capability> baseVectorCapability = new ArrayList<Capability>();
		baseVectorCapability.add(Capability.BASE);
		/*
		 * If it is not contain the base capability, it is impossible to check capabilities
		 */
		// boolean isChecked = capabilityVector.equals(baseVectorCapability);
		// Assert.assertTrue(isChecked);

		Assert.assertTrue(session.getActiveCapabilities().contains(Capability.BASE));

	}

	@Test
	public void testLoadConfiguration() {
		try {
			session.loadConfiguration(new PropertiesConfiguration("netconf-default.properties"));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

}
