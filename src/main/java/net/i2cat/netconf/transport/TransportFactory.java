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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2cat.netconf.errors.TransportNotImplementedException;

public class TransportFactory {

	static private Log	log	= LogFactory.getLog(TransportFactory.class);

	private static enum TypeTransport {
		SSH, VIRTUAL, MOCK, UNKNOWN;
	}

	public static TypeTransport checkTransportType(String scheme) throws TransportNotImplementedException {

		TypeTransport type;

		if (scheme == null)
			throw new TransportNotImplementedException("Unknown transport: " + scheme);

		if (scheme.equalsIgnoreCase("ssh"))
			type = TypeTransport.SSH;
		else if (scheme.equalsIgnoreCase("virtual"))
			type = TypeTransport.VIRTUAL;
		else if (scheme.equalsIgnoreCase("mock"))
			type = TypeTransport.MOCK;
		else
			type = TypeTransport.UNKNOWN;

		switch (type) {
			case SSH:
			case VIRTUAL:
			case MOCK:
				// TODO extra checks
				break;
			default:
				throw new TransportNotImplementedException("Unknown transport: " + scheme);
		}
		log.info("TYPE: " + type);
		return type;
	}

	public static Transport getTransport(String scheme) throws TransportNotImplementedException {

		TypeTransport type = checkTransportType(scheme);

		return getTransport(type);
	}

	public static Transport getTransport(TypeTransport type) throws TransportNotImplementedException {

		switch (type) {
			case SSH:
				return new SSHTransport();
			case VIRTUAL:
				return new VirtualTransport();
			case MOCK:
				return new MockTransport();
		}
		return null;
	}
}
