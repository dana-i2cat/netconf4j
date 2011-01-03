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

	public String sortErrors(Reply reply) {
		String msg = "";
		for (Error error : reply.getErrors()) {
			msg += error.getMessage() + '\n';
		}
		return msg;

	}

	@Test
	public void getGetConfigLogicalRouter() {
		URI lola;
		try {

			lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:bar@foo:22/netconf"));
			log.info("URI get: " + lola.toString());

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();

			Query query = QueryFactory.newSetLogicalRouter("cpe1");
			query.setMessageId("1");
			Reply reply = session.sendSyncQuery(query);
			/* check first messages */
			if (reply.containsErrors()) {
				String msg = sortErrors(reply);
				fail("It was impossible to access to the logical router: " + msg);
			}

			query = QueryFactory.newGetConfig("running", null, null);
			query.setMessageId("2");

			log.info(query.toXML());

			reply = session.sendSyncQuery(query);

			log.info(reply.getContain());

		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (TransportException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (NetconfProtocolException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (TransportNotImplementedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@Test
	public void getConfig() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:bar@foo:22/netconf"));
			log.info("URI get: " + lola.toString());

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
			log.info("URI get: " + lola.toString());

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
