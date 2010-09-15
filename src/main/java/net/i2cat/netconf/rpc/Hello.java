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
package net.i2cat.netconf.rpc;

import java.util.ArrayList;

public class Hello extends RPCElement {

	ArrayList<Capability>	capabilities;
	String					sessionId;

	public Hello() {
		messageId = "0";
	}

	public String toXML() {

		String xml = "<hello xmlns=\"" + Capability.BASE + "\">\n";

		xml += "\t<capabilities>\n";

		for (Capability capability : capabilities) {
			xml += "\t\t<capability>" + capability + "</capability>\n";
		}

		xml += "\t</capabilities>\n";

		if (sessionId != null)
			xml += "\t<session-id>" + sessionId + "</session-id>\n";

		xml += "</hello>";

		return xml;
	}

	public ArrayList<Capability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(ArrayList<Capability> capabilities) {
		this.capabilities = capabilities;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
