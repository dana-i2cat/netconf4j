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

import java.util.ArrayList;
import java.util.Vector;

import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.ErrorSeverity;
import net.i2cat.netconf.rpc.ErrorTag;
import net.i2cat.netconf.rpc.ErrorType;
import net.i2cat.netconf.rpc.Hello;
import net.i2cat.netconf.rpc.Operation;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.RPCElement;
import net.i2cat.netconf.rpc.Reply;
import net.i2cat.netconf.utils.FileHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class was implemented to be a dummy transport which could simulate a real connection with a device. This connection via SSH with netconf
 */

public class MockTransport implements Transport {

	private Log					log								= LogFactory.getLog(MockTransport.class);

	Vector<TransportListener>	listeners						= new Vector<TransportListener>();

	SessionContext				context;
	ArrayList<Capability>		supportedCapabilities;
	MessageQueue				queue;

	int							lastMessageId					= 0;

	String						subsystem						= "";

	boolean						modeErrors						= false;

	private static String		path							= "/mock/";

	public static final String	fileIPConfiguration				= path + "ipconfiguration.xml";

	/* Extra capabilities */
	public static final String	fileCPE1IPLogicalRouterConfig	= path + "cpe1configuration.xml";
	public static final String	fileCPE2IPLogicalRouterConfig	= path + "cpe2configuration.xml";

	public static final String	fileShowInterfaceInformation	= path + "showinterfaceinformation.xml";
	public static final String	fileShowRollbackInformation		= path + "showrollbackinformation.xml";
	public static final String	fileShowRouteInformation		= path + "showrouteinformation.xml";
	public static final String	fileShowSoftwareInformation		= path + "showsoftwareinformation.xml";

	String						logicalRouterName				= null;

	// boolean insideLogicalRouter = false;

	public void addListener(TransportListener handler) {
		listeners.add(handler);
	}

	public void setMessageQueue(MessageQueue queue) {
		this.queue = queue;
	}

	public void connect(SessionContext sessionContext) throws TransportException {

		context = sessionContext;

		if (!context.getScheme().equals("mock"))
			throw new TransportException("Mock transport initialized with other scheme: " + context.getScheme());

		subsystem = sessionContext.getSubsystem();

		Hello hello = new Hello();

		hello.setMessageId(String.valueOf(lastMessageId));
		hello.setSessionId("1234");

		supportedCapabilities = new ArrayList<Capability>();
		// FIXME more capabilities?
		supportedCapabilities.add(Capability.BASE);

		hello.setCapabilities(supportedCapabilities);

		queue.put(hello);

		for (TransportListener listener : listeners)
			listener.transportOpened();
	}

	public void disconnect() {
		for (TransportListener listener : listeners)
			listener.transportClosed();
	}

	public void sendAsyncQuery(RPCElement elem) throws TransportException {

		Reply reply = new Reply();
		Vector<Error> errors = new Vector<Error>();

		if (elem instanceof Hello) {

			// Capability b
			ArrayList<Capability> capabilities = ((Hello) elem).getCapabilities();
			capabilities.retainAll(this.supportedCapabilities);
			context.setActiveCapabilities(capabilities);

		}
		if (elem instanceof Query) {
			Query query = (Query) elem;
			Operation op = query.getOperation();

			if (op.equals(Operation.COPY_CONFIG)) {
			} else if (op.equals(Operation.DELETE_CONFIG)) {
				reply.setOk(true);
				reply.setMessageId(query.getMessageId());
			} else if (op.equals(Operation.EDIT_CONFIG)) {
				reply.setOk(true);
				reply.setMessageId(query.getMessageId());
			} else if (op.equals(Operation.GET)) {

				if (query.getFilter() != null && query.getFilterType() != null) {
					if (context.getActiveCapabilities().contains(Capability.XPATH)) {
						if (!(query.getFilterType().equals("xpath") || query.getFilterType().equals("subtree")))
							errors.add(new Error() {
								{
									setTag(ErrorTag.BAD_ATTRIBUTE);
									setType(ErrorType.PROTOCOL);
									setSeverity(ErrorSeverity.ERROR);
									setInfo("<bad-attribute> : Wrong filter type. Neither xpath nor subtree.");
								}
							});
						else if (query.getFilterType().equals("subtree"))
							errors.add(new Error() {
								{
									setTag(ErrorTag.BAD_ATTRIBUTE);
									setType(ErrorType.PROTOCOL);
									setSeverity(ErrorSeverity.ERROR);
									setInfo("<bad-attribute> : Wrong filter type. Not subtree.");
								}
							});
					}
				}
				reply.setMessageId(query.getMessageId());

				reply.setContain(getDataFromFile(fileIPConfiguration));

			} else if (op.equals(Operation.GET_CONFIG)) {
				// it is not include any get config, it is an error
				if (query.getSource() == null)
					errors.add(new Error() {
						{
							setTag(ErrorTag.MISSING_ELEMENT);
							setType(ErrorType.PROTOCOL);
							setSeverity(ErrorSeverity.ERROR);
							setInfo("<bad-element> : No source configuration specified");
						}
					});
				if (query.getFilter() != null && query.getFilterType() != null) {
					if (context.getActiveCapabilities().contains(Capability.XPATH)) {
						if (!(query.getFilterType().equals("xpath") || query.getFilterType().equals("subtree")))
							errors.add(new Error() {
								{
									setTag(ErrorTag.BAD_ATTRIBUTE);
									setType(ErrorType.PROTOCOL);
									setSeverity(ErrorSeverity.ERROR);
									setInfo("<bad-attribute> : Wrong filter type. Neither xpath nor subtree.");
								}
							});
						else if (query.getFilterType().equals("subtree"))
							errors.add(new Error() {
								{
									setTag(ErrorTag.BAD_ATTRIBUTE);
									setType(ErrorType.PROTOCOL);
									setSeverity(ErrorSeverity.ERROR);
									setInfo("<bad-attribute> : Wrong filter type. Not subtree.");
								}
							});
					}
				}

				reply.setMessageId(query.getMessageId());
				if (logicalRouterName == null) {
					reply.setContain(getDataFromFile(fileIPConfiguration));
				} else {

					String path = createPathFileLogicalRouter(logicalRouterName);
					reply.setContain(getDataFromFile(path));
				}

			} else if (op.equals(Operation.KILL_SESSION)) {
				logicalRouterName = null;
				disconnect();
				return;
			} else if (op.equals(Operation.CLOSE_SESSION)) {
				reply.setMessageId(query.getMessageId());
				reply.setOk(true);
				logicalRouterName = null;
				disconnect();
			} // FIXME ADD ELSE IF FOR ROLLBACK
			/* include junos capabilities operations */
			else if (op.equals(Operation.SET_LOGICAL_ROUTER)) {
				reply.setMessageId(query.getMessageId());
				reply.setContain("<cli><logical-system>" + query.getIdLogicalRouter() + "</logical-system></cli>");
				logicalRouterName = query.getIdLogicalRouter();

			} else if (op.equals(Operation.GET_INTERFACE_INFO)) {
				reply.setMessageId(query.getMessageId());
				reply.setContainName("information-information");
				reply.setContain(getDataFromFile(fileShowInterfaceInformation));
			} else if (op.equals(Operation.GET_ROUTE_INFO)) {
				reply.setMessageId(query.getMessageId());
				reply.setContainName("route-information");
				reply.setContain(getDataFromFile(fileShowRouteInformation));

			} else if (op.equals(Operation.GET_ROLLBACK_INFO)) {
				reply.setMessageId(query.getMessageId());
				reply.setContainName("rollback-information");
				reply.setContain(getDataFromFile(fileShowRollbackInformation));

			} else if (op.equals(Operation.GET_SOFTWARE_INFO)) {
				reply.setMessageId(query.getMessageId());
				reply.setContainName("software-information");
				reply.setContain(getDataFromFile(fileShowSoftwareInformation));

			} else if (op.equals(Operation.COMMIT) || op.equals(Operation.DISCARD) || op.equals(Operation.VALIDATE) || op.equals(Operation.LOCK) || op.equals(Operation.UNLOCK)) {
				reply.setMessageId(query.getMessageId());
				reply.setOk(true);
			}

		}

		// force to add errors in the response message
		if (subsystem.equals("errorServer"))
			addErrors(errors);

		if (errors.size() > 0)
			reply.setErrors(errors);

		queue.put(reply);
	}

	private String createPathFileLogicalRouter(String idLogicalRouter) {
		return path + idLogicalRouter + "configuration.xml";
	}

	public String getDataFromFile(String fileConfig) throws TransportException {

		String str = "";

		log.info("Trying to open " + fileConfig);
		try {
			str = FileHelper.getInstance().readStringFromFile(fileConfig);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("error message: " + e.getLocalizedMessage());
			throw new TransportException(e.getMessage());
		}
		log.info("OK! the file was read");
		return str;

	}

	private void error(String msg) throws TransportException {
		throw new TransportException(msg);
	}

	private void addErrors(Vector<Error> errors) {
		errors.add(new Error() {
			{
				setTag(ErrorTag.INVALID_VALUE);
				setType(ErrorType.PROTOCOL);
				setSeverity(ErrorSeverity.ERROR);
				setInfo("");
			}
		});

		errors.add(new Error() {
			{
				setTag(ErrorTag.BAD_ATTRIBUTE);
				setType(ErrorType.PROTOCOL);
				setSeverity(ErrorSeverity.ERROR);
				setInfo("<bad-attribute> : ");
			}
		});

		errors.add(new Error() {
			{
				setTag(ErrorTag.MISSING_ATTRIBUTE);
				setType(ErrorType.PROTOCOL);
				setSeverity(ErrorSeverity.ERROR);
				setInfo("<bad-attribute> : ");
			}
		});

		errors.add(new Error() {
			{
				setTag(ErrorTag.BAD_ELEMENT);
				setType(ErrorType.PROTOCOL);
				setSeverity(ErrorSeverity.ERROR);
				setInfo("<bad-element> : ");
			}
		});

		errors.add(new Error() {
			{
				setTag(ErrorTag.MISSING_ELEMENT);
				setType(ErrorType.PROTOCOL);
				setSeverity(ErrorSeverity.ERROR);
				setInfo("<bad-element> : ");
			}
		});
	}
}
