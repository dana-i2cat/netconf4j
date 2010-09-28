package net.i2cat.netconf.transport;

import java.util.ArrayList;
import java.util.Vector;

import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Hello;
import net.i2cat.netconf.rpc.Operation;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.RPCElement;

public class MockTransport implements Transport {

	Vector<TransportListener>	listeners		= new Vector<TransportListener>();

	SessionContext				context;
	ArrayList<Capability>		supportedCapabilities;
	ArrayList<Capability>		activeCapabilities;
	MessageQueue				queue;

	int							lastMessageId	= 0;

	public void addListener(TransportListener handler) {
		listeners.add(handler);
	}

	public void connect(SessionContext sessionContext) throws TransportException {

		context = sessionContext;

		if (context.getScheme().compareTo("mock") == 0)
			throw new TransportException("Mock transport initialized with other scheme: " + context.getScheme());

		Hello hello = new Hello();

		hello.setMessageId("mock-" + lastMessageId);
		hello.setSessionId("1234");

		supportedCapabilities = new ArrayList<Capability>();
		// FIXME more capabilities?
		supportedCapabilities.add(Capability.BASE);

		hello.setCapabilities(supportedCapabilities);

		queue.put(hello);

		for (TransportListener listener : listeners)
			listener.transportOpenned();
	}

	public void disconnect() throws TransportException {
		for (TransportListener listener : listeners)
			listener.transportClosed();
	}

	public void sendAsyncQuery(RPCElement elem) throws TransportException {

		RPCElement reply = null;

		if (elem instanceof Hello) {

			// FIXME calculate activeCapabilities
		}
		if (elem instanceof Query) {
			Query query = (Query) elem;
			Operation op = query.getOperation();

			if (op == Operation.CLOSE_SESSION) {
			}
			if (op == Operation.COPY_CONFIG) {
			}
			if (op == Operation.DELETE_CONFIG) {
			}
			if (op == Operation.EDIT_CONFIG) {
			}
			if (op == Operation.GET) {
			}
			if (op == Operation.GET_CONFIG) {
			}
			if (op == Operation.KILL_SESSION) {
			}
			if (op == Operation.LOCK) {
			}
			if (op == Operation.UNLOCK) {
			}

		}

		queue.put(reply);
	}

	public void setMessageQueue(MessageQueue queue) {
		this.queue = queue;
	}

}
