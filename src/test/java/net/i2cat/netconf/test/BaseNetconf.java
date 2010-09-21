package net.i2cat.netconf.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

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
		session.disconnect();
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

		ArrayList<Capability> commonCapabilities = (ArrayList<Capability>) clientCapabilities.clone();
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
}
