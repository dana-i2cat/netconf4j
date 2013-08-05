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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import net.i2cat.netconf.rpc.Capability;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionContext extends CompositeConfiguration {

	public enum AuthType {
		PASSWORD ("password"),
		PUBLICKEY ("publickey");
		
		String value;
		
		AuthType(String value) {
			this.value = value;
		}
		
		public static AuthType getByValue(String value) {
			for (AuthType authType : values()) {
				if (authType.value.equals(value)) {
					return authType;
				}
			}
			throw new IllegalArgumentException("No enum const class " + AuthType.class.getName() + "." + value);
		}
	}
	
	public final static String	BASE				= "net.i2cat.netconf.";

	public final static String	LASTMESSAGEID		= BASE + "session.lastMessageId";
	public final static String	CAPABILITIES_CLIENT	= BASE + "session.clientCapabilities";
	public final static String	CAPABILITIES_SERVER	= BASE + "session.serverCapabilities";
	public final static String	CAPABILITIES_ACTIVE	= BASE + "session.activeCapabilities";
	public final static String	URI					= BASE + "session.uri";
	/* Add keepalive sessions to control connection */
	public final static String	KEEPALIVE			= BASE + "session.keepalive";
	public final static String  TIMEOUT             = BASE + "session.timeout";

	public final static String	LOGRESPXML			= BASE + "transport.logXMLStream";
	public final static String	LOGFILEXML			= BASE + "transport.logXMLOutputFile";

	public final static String	LOG_STREAM			= "transport.logXMLStream";
	public final static String	LOG_FILE			= "transport.logXMLOutputFile";

	public final static String	AUTH_TYPE			= BASE + "session.auth.type";
	public final static String	USERNAME			= BASE + "session.auth.username";
	public final static String	PASSWORD			= BASE + "session.auth.password";
	public static final String 	KEY_USERNAME		= BASE + "session.auth.key.username";
	public final static String	KEY_LOCATION		= BASE + "session.auth.key.location";
	public final static String	KEY_PASSWORD		= BASE + "session.auth.key.password";
	
	public final static Log		log					= LogFactory.getLog(SessionContext.class);

	

	private Configuration createDefaultConfiguration() {

		BaseConfiguration baseConfiguration = new BaseConfiguration();
		baseConfiguration.addProperty(LOG_STREAM, "false");
		baseConfiguration.addProperty(LOG_FILE, "server.xml.log");

		/* FIXME WHAT IT IS THE BETTER METHOD PASS STRING OR BOOL */
		baseConfiguration.addProperty(KEEPALIVE, true);
		
		return baseConfiguration;

	}

	public SessionContext() throws ConfigurationException {

		this.addConfiguration(createDefaultConfiguration());
		
		//Default Auth type is password
		this.setProperty(AUTH_TYPE, AuthType.PASSWORD);

		try {
			String path = new java.io.File(".").getCanonicalPath();
			log.info("Current path: " + path);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setActiveCapabilities(ArrayList<Capability> capabilities) {
		this.setProperty(CAPABILITIES_ACTIVE, capabilities);
	}

	private ArrayList<Capability> getCapability(String key) {

		ArrayList<Capability> capabilities = new ArrayList<Capability>();
		capabilities.add((Capability) getProperty(key));
		return capabilities;
	}

	public ArrayList<Capability> getActiveCapabilities() {

		// FIXME A BETTER METHOD TO DO THIS
		if (this.getProperty(CAPABILITIES_ACTIVE) instanceof Capability) {
			return getCapability(CAPABILITIES_ACTIVE);
		}
		return (ArrayList<Capability>) this.getList(CAPABILITIES_ACTIVE);
	}

	public void setClientCapabilities(ArrayList<Capability> capabilities) {
		this.setProperty(CAPABILITIES_CLIENT, capabilities);
	}

	public ArrayList<Capability> getClientCapabilities() {

		// FIXME A BETTER METHOD TO DO THIS
		if (this.getProperty(CAPABILITIES_CLIENT) instanceof Capability) {
			return getCapability(CAPABILITIES_CLIENT);
		}

		return (ArrayList<Capability>) this.getList(CAPABILITIES_CLIENT);
	}

	public void setServerCapabilities(ArrayList<Capability> capabilities) {
		this.setProperty(CAPABILITIES_SERVER, capabilities);
	}

	public ArrayList<Capability> getServerCapabilities() {
		// FIXME A BETTER METHOD TO DO THIS
		if (this.getProperty(CAPABILITIES_SERVER) instanceof Capability) {
			return getCapability(CAPABILITIES_SERVER);
		}
		return (ArrayList<Capability>) this.getList(CAPABILITIES_SERVER);
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

	public long getTimeout() {
		return this.getLong(TIMEOUT);
	}

	public void setTimeout(long timeout){
		this.setProperty(TIMEOUT, timeout);
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
		this.setProperty(LOGRESPXML, logRespXML);
	}

	public String getLogFileXML() {
		return this.getString(LOGFILEXML);
	}

	public void setLogFileXML(String logRespXML) {
		this.setProperty(LOGFILEXML, logRespXML);
	}

	public String getUser() {
		return ((URI) this.getProperty(URI)).getUserInfo().split(":")[0];
	}

	public String getPass() {
		if (((URI) this.getProperty(URI)).getUserInfo().contains(":"))
			return ((URI) this.getProperty(URI)).getUserInfo().split(":")[1];
		else
			return null;
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

	public String getScheme() {
		return ((URI) this.getProperty(URI)).getScheme();

	}
	
	public AuthType getAuthenticationType() {
		return (AuthType) this.getProperty(AUTH_TYPE);
	}
	
	public String getKeyLocation() {
		return (String) this.getProperty(KEY_LOCATION);
	}
	
	public String getKeyPassword() {
		return (String) this.getProperty(KEY_PASSWORD);
	}
	
	public String getKeyUsername() {
		return (String) this.getProperty(KEY_USERNAME);
	}
	
	public void setKeyPassword(String password){
		this.setProperty(KEY_PASSWORD, password);
	}
	
	public void setKeyLocation(String location){
		this.setProperty(KEY_LOCATION, location);
	}
	
	public void setKeyUsername(String username) {
		this.setProperty(KEY_USERNAME, username);
	}
	
	public void setAuthenticationType(AuthType type){
		this.setProperty(AUTH_TYPE, type);
	}
	
	// Override method to reset the configuration before add a new
	public void newConfiguration(Configuration source) {
		this.clear();
		this.addConfiguration(source);

	}

}
