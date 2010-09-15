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
package net.i2cat.netconf;

import java.net.URI;
import java.net.URISyntaxException;

import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.errors.TransportNotImplementedException;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.configuration.ConfigurationException;

public class App {
	public static void main(String[] args) {

		try {
			URI lola = new URI("ssh://root:mant6WWe@lola.hea.net:22/netconf");
			SessionContext sessionContext = new SessionContext();
			sessionContext.setURI(lola);

			NetconfSession session;
			Reply reply;

			try {

				session = new NetconfSession(sessionContext);

				session.connect();

				reply = session.sendSyncQuery(QueryFactory.newGetConfig("running", null, null));

				reply = session.sendSyncQuery(QueryFactory.newCloseSession());

				session.disconnect();

			} catch (TransportException e) {
				e.printStackTrace();
			} catch (NetconfProtocolException e) {
				e.printStackTrace();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}

		} catch (TransportNotImplementedException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		return;

		// String host = "lola.hea.net";
		// int port = 22;
		//
		// ChannelFactory factory = new NioClientSocketChannelFactory(
		// Executors.newCachedThreadPool(),
		// Executors.newCachedThreadPool());
		//
		// ClientBootstrap bootstrap = new ClientBootstrap(factory);
		//
		// bootstrap.setPipelineFactory(new NetconfPipeline());
		//
		// bootstrap.setOption("tcpNoDelay", true);
		// bootstrap.setOption("keepAlive", true);
		//
		// ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
		// port));
		//
		// future.awaitUninterruptibly();
		// if (!future.isSuccess()) {
		// future.getCause().printStackTrace();
		// }
		// future.getChannel().getCloseFuture().awaitUninterruptibly();
		// factory.releaseExternalResources();
	}
}
