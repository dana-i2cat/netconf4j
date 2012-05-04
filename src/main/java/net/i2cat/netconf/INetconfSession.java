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

import java.util.ArrayList;

import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.messageQueue.MessageQueueListener;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.Reply;
import net.i2cat.netconf.transport.TransportListener;

import org.apache.commons.configuration.Configuration;

public interface INetconfSession {

	/**
	 * Establish a netconf connection to the URI specified in SessionContext.
	 * 
	 * @throws TransportException
	 *             If any problem occurs at transport level.
	 * @throws NetconfProtocolException
	 *             If any problem occurs at RPC level.
	 */
	public abstract void connect() throws TransportException, NetconfProtocolException;

	/**
	 * Destroy the netconf connection to the URI specified in SessionContext.
	 * 
	 * @throws TransportException
	 */
	public abstract void disconnect() throws TransportException;

	/**
	 * Send a Netconf Query and wait for the response.
	 * 
	 * Don't set message-id, it will be ignored and overridden by the session.
	 * 
	 * @param query
	 * @return
	 * @throws TransportException
	 */
	public abstract Reply sendSyncQuery(Query query) throws TransportException;

	/**
	 * Send a Netconf Query and return immediately. You will have to get the reply (if any) via a NetconfReplyHandler or polling receiveReply() for
	 * it.
	 * 
	 * Don't set message-id, it will be ignored and overridden by the session.
	 * 
	 * @param querys
	 * @throws TransportException
	 */
	public abstract void sendAsyncQuery(Query query) throws TransportException;

	/**
	 * Load a properties file with configuration parameters.
	 * 
	 * See netconf-default.properties at jar's root for an example.
	 * 
	 * @param source
	 *            properties file path, relative to jar's root.
	 */
	public abstract void loadConfiguration(Configuration source);

	/**
	 * Active capabilities are those declared both by the client and the server.
	 * 
	 * @return The active capabilities array.
	 */
	public abstract ArrayList<Capability> getActiveCapabilities();

	/**
	 * Client capabilities are those declared by the client. Current implementation is extensible but doesn't allow extension via the API.
	 * 
	 * @return The client capabilities array.
	 */
	public abstract ArrayList<Capability> getClientCapabilities();

	/**
	 * Server capabilities are those declared both by the server.
	 * 
	 * @return The server capabilities array.
	 */
	public abstract ArrayList<Capability> getServerCapabilities();

	/**
	 * Register a transport lifecycle listener.
	 * 
	 * @param handler
	 */
	public abstract void registerTransportListener(TransportListener handler);

	/**
	 * Register a RPC message listener. Use this to get replies to messages sent via sendAsyncQuery.
	 * 
	 * @param handler
	 */
	public abstract void registerMessageQueueListener(MessageQueueListener handler);
}