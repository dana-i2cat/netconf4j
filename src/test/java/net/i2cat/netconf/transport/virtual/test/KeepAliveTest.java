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

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.errors.TransportNotImplementedException;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.transport.VirtualTransport;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

public class KeepAliveTest {

	MessageQueue		messageQueue;
	VirtualTransport	virtualTransport;

	private void initVirtualTransport() {
		messageQueue = new MessageQueue();
		virtualTransport = new VirtualTransport();
		virtualTransport.setMessageQueue(messageQueue);

	}

	@Test
	public void getHelloResponseWithKeepAlive() {
		initVirtualTransport();
		try {
			URI helloURI = new URI("virtual://foouser:foopass@fooserver:22/helloServer");
			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(helloURI);
			sessionContext.setLogRespXML(true);
			sessionContext.setKeepAlive(true);
			NetconfSession netconf = new NetconfSession(sessionContext);

			netconf.connect();

			int sec = 1000;
			int min = 60;

			Thread.sleep(sec * min * 10);
			netconf.disconnect();

		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			fail(e.getMessage());
		} catch (InterruptedException e) {
			fail(e.getMessage());
		} catch (TransportNotImplementedException e) {
			fail(e.getMessage());
		} catch (TransportException e) {
			fail(e.getMessage());
		} catch (NetconfProtocolException e) {
			fail(e.getMessage());
		}

	}

}
