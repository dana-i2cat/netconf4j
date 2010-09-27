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

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.messageQueue.MessageQueueListener;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.RPCElement;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BaseNetconf {

	static SessionContext	sessionContext;
	static NetconfSession	session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		sessionContext = new SessionContext();
		sessionContext.setURI(new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
															"virtual://foo:bar@foo:22/okServer")));

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

	// 2 second timeout
	@Test(timeout = 2000)
	public void testSendAsyncQuery() {

		final Object monitor = new Object();
		Query query = QueryFactory.newKeepAlive();

		session.registerMessageQueueListener(new MessageQueueListener() {

			public void receiveRPCElement(RPCElement element) {
				synchronized (monitor) {
					monitor.notifyAll();
				}
			}
		});

		try {
			session.sendAsyncQuery(query);

			synchronized (monitor) {
				monitor.wait();
			}

		} catch (TransportException e) {
			fail(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testLoadConfiguration() {
		try {
			session.loadConfiguration(new
					PropertiesConfiguration("netconf-default.properties"));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
