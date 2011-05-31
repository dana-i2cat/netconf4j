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
import java.util.Vector;

import junit.framework.Assert;
import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

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
					"mock://foo:boo@testing.default.net:22"));
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

			log.info("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n<\\" + reply.getContainName() + ">");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@Test
	public void getConfig() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:boo@testing.default.net:22"));
			log.info("URI get: " + lola.toString());

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();

			Query queryGetConfig = QueryFactory.newGetConfig("running", null, null);

			log.info(queryGetConfig.toXML());

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
			log.info("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n<\\" + reply.getContainName() + ">");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void keepAlive() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:boo@testing.default.net:22"));
			log.info("URI get: " + lola.toString());

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();
			Query queryKeepAlive = QueryFactory.newKeepAlive();
			queryKeepAlive.setMessageId("1");

			log.info(queryKeepAlive.toXML());

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
			log.info("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n<\\" + reply.getContainName() + ">");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void Validate() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:boo@testing.default.net:22"));
			log.info("URI get: " + lola.toString());

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();
			Query queryValidate = QueryFactory.newValidate("candidate");
			queryValidate.setMessageId("1");

			log.info(queryValidate.toXML());

			Reply reply = session.sendSyncQuery(queryValidate);
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
			log.info("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n<\\" + reply.getContainName() + ">");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void getInterfaceInformation() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:boo@testing.default.net:22"));
			log.info("URI get: " + lola.toString());

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();
			Query getInterfaceInformation = QueryFactory.newGetInterfaceInformation();
			getInterfaceInformation.setMessageId("1");

			log.info(getInterfaceInformation.toXML());

			Reply reply = session.sendSyncQuery(getInterfaceInformation);
			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}
			log.info("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n</" + reply.getContainName() + ">");
			Assert.assertNotNull(reply.getContainName());
			Assert.assertNotNull(reply.getContain());

			reply = session.sendSyncQuery(QueryFactory.newCloseSession());

			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}

			session.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void getRouteInformation() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:boo@testing.default.net:22"));
			log.info("URI get: " + lola.toString());

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();
			Query getRouteInformation = QueryFactory.newGetRouteInformation();
			getRouteInformation.setMessageId("1");

			log.info(getRouteInformation.toXML());

			Reply reply = session.sendSyncQuery(getRouteInformation);
			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}
			log.info("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n</" + reply.getContainName() + ">");
			Assert.assertNotNull(reply.getContainName());
			Assert.assertNotNull(reply.getContain());

			reply = session.sendSyncQuery(QueryFactory.newCloseSession());

			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}

			session.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void getSoftwareInformation() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:boo@testing.default.net:22"));
			log.info("URI get: " + lola.toString());

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();
			Query getSoftwareInformation = QueryFactory.newGetSoftwareInformation();
			getSoftwareInformation.setMessageId("1");

			log.info(getSoftwareInformation.toXML());

			Reply reply = session.sendSyncQuery(getSoftwareInformation);
			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}
			log.info("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n</" + reply.getContainName() + ">");
			Assert.assertNotNull(reply.getContainName());
			Assert.assertNotNull(reply.getContain());

			reply = session.sendSyncQuery(QueryFactory.newCloseSession());

			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}

			session.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void getRollbackInformation() {
		try {

			URI lola = new URI(System.getProperty("net.i2cat.netconf.test.transportUri",
					"mock://foo:boo@testing.default.net:22"));
			log.info("URI get: " + lola.toString());

			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session = new NetconfSession(sessionContext);

			session.connect();
			Query getRollbackInformation = QueryFactory.newGetRollbackInformation("2");
			getRollbackInformation.setMessageId("1");

			log.info(getRollbackInformation.toXML());

			Reply reply = session.sendSyncQuery(getRollbackInformation);
			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}
			log.info("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n</" + reply.getContainName() + ">");
			Assert.assertNotNull(reply.getContainName());
			Assert.assertNotNull(reply.getContain());

			reply = session.sendSyncQuery(QueryFactory.newCloseSession());

			if (reply.containsErrors()) {
				printErrors(reply.getErrors());
				fail("The response received errors");
			}

			session.disconnect();

		} catch (Exception e) {
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
