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

import net.i2cat.netconf.errors.TransportNotRegisteredException;
import net.i2cat.netconf.errors.TransportRegistrationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class TransportFactory {

	private final Log log = LogFactory.getLog(TransportFactory.class);

    private Map<String, Class> registeredTransports = null;

    /**
     * Construct a transport factory with the following default transports:
     *
     * ssh: SSHTransport
     * virtual: VirtualTransport
     * mock: MockTransport
     */
    public TransportFactory() {
        log.info("Initializing transport factory");
        registeredTransports = new HashMap<String, Class>();

        // Register defaults
        try {
            registerTransport("ssh", SSHTransport.class);
            registerTransport("mock", MockTransport.class);
        } catch (TransportRegistrationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register the given transport, an instance of which can be created by calling
     * <code>getTransport(scheme)</code>. You must register a transport class before
     * you create a <code>NetconfSession</code> that will use that transport class.
     *
     * @param scheme the scheme parameter corresponds to the scheme specified in the uri.
     * @param transportClass the class of the transport to register. Must be instantiable and must implement the Transport interface.
     * @throws TransportRegistrationException
     */
    public void registerTransport(String scheme, Class transportClass) throws TransportRegistrationException {

        // Check that we have a valid transport instance.
        if (!Transport.class.isAssignableFrom(transportClass)) {
            throw new TransportRegistrationException("Cannot register class '" + transportClass.getName() +
                    "' as a transport because it doesn't implement the Transport interface.");
        }

        // TODO: Is there a better way to determine that we can create an instance of the given class?
        // We create a throwaway instance of the transport to verify that we can instantiate it.
        // This extra work is worth the ability to fail at registration rather than at getTransport.
        try {
            transportClass.newInstance();
        } catch (InstantiationException e) {
            throw new TransportRegistrationException("Cannot register class '" + transportClass.getName() + "'" +
                    " as a transport because it cannot be instantiated.", e);
        } catch (IllegalAccessException e) {
            throw new TransportRegistrationException("Cannot register class '" + transportClass.getName() +
                    "' as a transport.", e);
        }

        if (registeredTransports.containsKey(scheme)) {
            log.warn("Overwriting transport associated with '" + scheme + "'. Was " +
                    registeredTransports.get(scheme).getName() + ", now " + transportClass.getName() + ".");
        }

        registeredTransports.put(scheme, transportClass);
        log.debug("Transport " + transportClass.getName() + " registered under scheme '" + scheme + "'");
    }

    /**
     * Return an instance of the transport associated with <code>scheme</code>. You must register transports with
     * <code>registerTransport(scheme, transportClass)</code> before getting instances here.
     *
     * @param scheme the scheme associated with the transport class to instantiate.
     * @return an instance of the Transport associated with <code>scheme</code>. Returns <code>null</code> if the transport
     * cannot be instantiated (if, for example, the registered transport class was abstract).
     * @throws TransportNotRegisteredException thrown if no transport class has been registered for <code>scheme</code>.
     */
	public Transport getTransport(String scheme) throws TransportNotRegisteredException {

        if (!registeredTransports.containsKey(scheme)) {
            throw new TransportNotRegisteredException("Unknown transport: " + scheme);
        }

        try {
            return (Transport) registeredTransports.get(scheme).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

		return null;
	}

    public boolean isAwareOfScheme(String scheme) {
        return registeredTransports.containsKey(scheme);
    }
}
