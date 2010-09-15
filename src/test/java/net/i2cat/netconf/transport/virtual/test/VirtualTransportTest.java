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
package net.i2cat.netconf.transport.virtual.test;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Hello;
import net.i2cat.netconf.rpc.Operation;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.transport.VirtualTransport;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

public class VirtualTransportTest {
	MessageQueue		messageQueue;
	VirtualTransport	virtualTransport;

	private void initVirtualTransport() {
		messageQueue = new MessageQueue();
		virtualTransport = new VirtualTransport();
		virtualTransport.setMessageQueue(messageQueue);

	}

	@Test
	public void getHelloResponse() {
		initVirtualTransport();

		try {
			URI helloURI = new URI("ssh://foouser:foopass@fooserver:22/helloServer");
			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(helloURI);
			virtualTransport.connect(sessionContext);
			Hello clientHello = new Hello();
			clientHello.setCapabilities(Capability.getSupportedCapabilities());
			virtualTransport.sendAsyncQuery(clientHello);
			virtualTransport.disconnect();

		} catch (TransportException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getOkResponse() {
		initVirtualTransport();

		try {
			URI okURI = new URI("ssh://foouser:foopass@fooserver:22/okServer");
			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(okURI);
			virtualTransport.connect(sessionContext);
			Query queryEditConfig = newEditConfig();
			virtualTransport.sendAsyncQuery(queryEditConfig);
			virtualTransport.disconnect();
		} catch (TransportException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void getInformationResponse() {
		initVirtualTransport();

		try {
			URI infoURI = new URI("ssh://foouser:foopass@fooserver:22/infoServer");
			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(infoURI);
			virtualTransport.connect(sessionContext);
			Query queryGetConfig = newGetConfig();
			virtualTransport.sendAsyncQuery(queryGetConfig);
			virtualTransport.disconnect();
		} catch (TransportException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void getErrorResponse() {
		initVirtualTransport();

		try {
			URI errorURI = new URI("ssh://foouser:foopass@fooserver:22/errorServer");
			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(errorURI);

			virtualTransport.connect(sessionContext);
			Query queryGetConfig = newGetConfig();
			virtualTransport.sendAsyncQuery(queryGetConfig);
			virtualTransport.disconnect();
		} catch (TransportException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void personalOperation1() {
		initVirtualTransport();

		try {
			URI uri = new URI("ssh://foouser:foopass@fooserver:22/okServer");

			URI errorURI = new URI("ssh://foouser:foopass@fooserver:22/errorServer");
			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(uri);

			virtualTransport.connect(sessionContext);

			Query queryPers = new Query();
			queryPers.setMessageId("1");
			Operation personalOper = new Operation("route-information",
					Capability.BASE);
			queryPers.setOperation(personalOper);

			virtualTransport.sendAsyncQuery(queryPers);
			virtualTransport.disconnect();
		} catch (TransportException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			fail(e.getMessage());
		}

	}

	private Query newGetConfig() {
		Query queryGetConfig = new Query();
		queryGetConfig.setMessageId("1");
		queryGetConfig.setOperation(Operation.GET_CONFIG);
		return queryGetConfig;
	}

	private Query newEditConfig() {
		Query queryEditConfig = new Query();
		queryEditConfig.setOperation(Operation.EDIT_CONFIG);
		queryEditConfig.setMessageId("1");
		return queryEditConfig;
	}

}
