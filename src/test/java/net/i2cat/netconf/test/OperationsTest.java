package net.i2cat.netconf.test;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.errors.TransportNotImplementedException;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class OperationsTest {

	private Log	log	= LogFactory.getLog(OperationsTest.class);

	@Test
	public void getConfig() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:bar@foo:22/netconf"));

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();

			Query queryGetConfig = QueryFactory.newGetConfig("running", null, null);

			log.debug(queryGetConfig.toXML());

			Reply reply = session.sendSyncQuery(queryGetConfig);

			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}

			reply = session.sendSyncQuery(QueryFactory.newCloseSession());

			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}

			session.disconnect();

		} catch (TransportException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (NetconfProtocolException e) {
			fail(e.getMessage());
			e.printStackTrace();

		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (TransportNotImplementedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void keepAlive() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:bar@foo:22/netconf"));

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();
			Query queryKeepAlive = QueryFactory.newKeepAlive();
			queryKeepAlive.setMessageId("1");

			log.debug(queryKeepAlive.toXML());

			Reply reply = session.sendSyncQuery(queryKeepAlive);
			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}

			reply = session.sendSyncQuery(QueryFactory.newCloseSession());

			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}

			session.disconnect();

		} catch (TransportException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (NetconfProtocolException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (TransportNotImplementedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void printErrors(Vector<Error> errors) {
		for (Error error : errors) {
			log.error("Error: " + error.getMessage());
		}

	}

}
