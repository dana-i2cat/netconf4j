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
import java.util.ArrayList;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import net.i2cat.netconf.rpc.Capability;

public class SessionContext extends CompositeConfiguration {
	public final static String	LASTMESSAGEID		= "lastMessageId";
	public final static String	CAPABILITIES_CLIENT	= "clientCapabilities";
	public final static String	CAPABILITIES_SERVER	= "serverCapabilities";
	public final static String	CAPABILITIES_ACTIVE	= "activeCapabilities";

	public final static String	URI					= "uri";
	public final static String	KEEPALIVE			= "keepalive";
	public final static String	LOGRESPXML			= "logrespxml";

	public SessionContext() throws ConfigurationException {
		this.addConfiguration(new PropertiesConfiguration("netconf-default.properties"));
	}

	public void setActiveCapabilities(ArrayList<Capability> capabilities) {
		this.setProperty(CAPABILITIES_ACTIVE, capabilities);

	}

	public ArrayList<Capability> getActiveCapabilities() {
		return (ArrayList<Capability>) this.getProperty(CAPABILITIES_ACTIVE);
	}

	public void setClientCapabilities(ArrayList<Capability> capabilities) {
		this.setProperty(CAPABILITIES_ACTIVE, capabilities);

	}

	public ArrayList<Capability> getClientCapabilities() {
		return (ArrayList<Capability>) this.getProperty(CAPABILITIES_ACTIVE);
	}

	public void setServerCapabilities(ArrayList<Capability> capabilities) {
		this.setProperty(CAPABILITIES_ACTIVE, capabilities);

	}

	public ArrayList<Capability> getServerCapabilities() {
		return (ArrayList<Capability>) this.getProperty(CAPABILITIES_ACTIVE);
	}

	/* message id management */
	public void setLastMessageId(int lastMessageId) {
		this.setProperty(LASTMESSAGEID, lastMessageId);
	}

	public int getLastMessageId() {
		return this.getInt(LASTMESSAGEID);
	}

	public URI getURI() {
		return (URI) this.getProperty(URI);
	}

	public void setURI(URI uRI) {
		this.setProperty(URI, uRI);
	}

	public boolean isKeepAlive() {
		return this.getBoolean(KEEPALIVE);
	}

	public void setKeepAlive(boolean keepAlive) {
		this.setProperty(KEEPALIVE, keepAlive);
	}

	public boolean isLogRespXML() {
		return this.getBoolean(LOGRESPXML);
	}

	public void setLogRespXML(boolean logRespXML) {
		this.setProperty(KEEPALIVE, logRespXML);
	}

	public String getUser() {
		return ((URI) this.getProperty(URI)).getUserInfo().split(":")[0];
	}

	public String getPass() {
		return ((URI) this.getProperty(URI)).getUserInfo().split(":")[1];
	}

	public String getHost() {
		return ((URI) this.getProperty(URI)).getHost();
	}

	public int getPort() {
		return ((URI) this.getProperty(URI)).getPort();
	}

	public String getSubsystem() {
		return ((URI) this.getProperty(URI)).getPath().replaceFirst("/", "");

	}

}
