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
package net.i2cat.netconf.transport;


public class TransportFactory {
	private static enum TypeTransport {
		SSH, VIRTUAL, UNKNOWN;
	}

	private static TypeTransport determineTransportType(String scheme) {
		if (scheme.equalsIgnoreCase("ssh"))
			return TypeTransport.SSH;
		else if (scheme.equalsIgnoreCase("virtual"))
			return TypeTransport.VIRTUAL;
		else
			return TypeTransport.UNKNOWN;
	}

	public static boolean isTransport(String scheme) {
		return determineTransportType(scheme) != TypeTransport.UNKNOWN;
	}

	public static Transport getTransport(String scheme) {
		TypeTransport type = determineTransportType(scheme);
		switch (type) {
			case SSH:
				return new SSHTransport();
			case VIRTUAL:
				return new VirtualTransport();
		}
		return null;
	}

}
