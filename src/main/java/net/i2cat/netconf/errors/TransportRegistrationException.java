package net.i2cat.netconf.errors;

/**
 * Thrown by TransportFactory if someone attempts to register
 * something other than a transport.
 */
public class TransportRegistrationException extends Exception {
    public TransportRegistrationException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TransportRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
