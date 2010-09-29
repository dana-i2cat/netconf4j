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

public class Capability implements java.io.Serializable {

	private static final long		serialVersionUID		= 2906619409518387155L;

	// RFC4741-8
	public static final Capability	BASE					= new Capability("urn:ietf:params:xml:ns:netconf:base:1.0");
	public static final Capability	WRITABLE_RUNNING		= new Capability("urn:ietf:params:xml:ns:netconf:capability:writable-running:1.0");
	public static final Capability	CANDIDATE				= new Capability("urn:ietf:params:xml:ns:netconf:capability:candidate:1.0");
	public static final Capability	CONFIRMED_COMMIT		= new Capability("urn:ietf:params:xml:ns:netconf:capability:confirmed-commit:1.0");
	public static final Capability	ROLLBACK_ON_ERROR		= new Capability("urn:ietf:params:xml:ns:netconf:capability:rollbach-on-error:1.0");
	public static final Capability	VALIDATE				= new Capability("urn:ietf:params:xml:ns:netconf:capability:validate:1.0");
	public static final Capability	DISTINCT_STARTUP		= new Capability("urn:ietf:params:xml:ns:netconf:capability:distinc-startup:1.0");
	public static final Capability	URL						= new Capability("urn:ietf:params:xml:ns:netconf:capability:url:1.0");
	public static final Capability	XPATH					= new Capability("urn:ietf:params:xml:ns:netconf:capability:xpath:1.0");

	private String					namespace;

	static ArrayList<Capability>	knownCapabilities		= new ArrayList<Capability>()
															{
																private static final long	serialVersionUID	= 1L;

																{
																	// RFC4741
																	// Mandatory
																	add(BASE);

																	// RFC4741
																	// Optional
																	add(WRITABLE_RUNNING);
																	add(CANDIDATE);
																	add(CONFIRMED_COMMIT);
																	add(ROLLBACK_ON_ERROR);
																	add(VALIDATE);
																	add(DISTINCT_STARTUP);
																	add(URL);
																	add(XPATH);
																}
															};

	static ArrayList<Capability>	supportedCapabilities	= new ArrayList<Capability>()
															{
																private static final long	serialVersionUID	= 1L;

																{
																	// RFC4741
																	// Mandatory
																	add(BASE);
																}
															};

	private Capability(String ns) {
		this.namespace = ns;
	}

	public String toString() {
		return namespace;
	}

	public static Capability getCapabilityByNamespace(String ns) {

		for (Capability capability : knownCapabilities) {
			if (capability.namespace.equalsIgnoreCase(ns))
				return capability;
		}
		// else, unknown capability: create an register.

		Capability newCapability = new Capability(ns);

		knownCapabilities.add(newCapability);

		return newCapability;

	}

	public static ArrayList<Capability> getSupportedCapabilities() {
		return supportedCapabilities;
	}

	public static ArrayList<Capability> getKnownCapabilities() {
		return knownCapabilities;
	}

	@Override
	public boolean equals(Object obj) {
		return namespace.equals(((Capability) obj).namespace);
	}

}
