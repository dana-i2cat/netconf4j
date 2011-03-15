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

import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.ErrorSeverity;
import net.i2cat.netconf.rpc.ErrorTag;
import net.i2cat.netconf.rpc.ErrorType;
import net.i2cat.netconf.rpc.Hello;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * This class intends to be a reusable (across transports) component that receives SAX events and returns instantiated Netconf's RPCElement objects.
 * 
 * @author Pau Minoves
 * 
 */
public class TransportContentParser extends DefaultHandler2 {

	private Log				log						= LogFactory.getLog(TransportContentParser.class);

	MessageQueue			messageQueue;

	Hello					hello;
	Reply					reply;
	String					messageId;
	Error					error;

	boolean					insideCapabilityTag		= false;
	StringBuffer			capabilityTagContent	= new StringBuffer();
	ArrayList<Capability>	capabilities;

	boolean					insideSessionIdTag		= false;
	StringBuffer			sessionIdTagContent		= new StringBuffer();

	boolean					insideDataTag			= false;
	StringBuffer			dataTagContent			= new StringBuffer();

	boolean					insideErrorTypeTag		= false;
	StringBuffer			errorTypeTagContent		= new StringBuffer();

	boolean					insideErrorTagTag		= false;
	StringBuffer			errorTagTagContent		= new StringBuffer();

	boolean					insideErrorSeverityTag	= false;
	StringBuffer			errorSeverityTagContent	= new StringBuffer();

	boolean					insideErrorAppTagTag	= false;
	StringBuffer			errorAppTagTagContent	= new StringBuffer();

	boolean					insideErrorPathTag		= false;
	StringBuffer			errorPathTagContent		= new StringBuffer();

	boolean					insideErrorMessageTag	= false;
	StringBuffer			errorMessageTagContent	= new StringBuffer();

	boolean					insideErrorInfoTag		= false;
	StringBuffer			errorInfoTagContent		= new StringBuffer();

	/* extra functionalities (out RFC) */

	boolean					insideInterfaceInfoTag	= false;
	StringBuffer			interfaceInfoTagContent	= new StringBuffer();

	public void setMessageQueue(MessageQueue queue) {
		this.messageQueue = queue;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		// if (insideDataTag && !localName.equalsIgnoreCase("data"))
		// return;

		if (insideDataTag) {
			dataTagContent.append("<" + localName + ">");
		}

		// log.debug("startElement <" + uri + "::" + localName + ">");

		if (localName.equalsIgnoreCase("hello")) {
			hello = new Hello();
			capabilities = new ArrayList<Capability>();
		}
		// if (localName.equalsIgnoreCase("capabilities")) {
		// insideCapabilityTag = false;
		// }
		if (localName.equalsIgnoreCase("capability")) {
			insideCapabilityTag = true;
		}
		if (localName.equalsIgnoreCase("session-id")) {
			insideSessionIdTag = true;
		}
		if (localName.equalsIgnoreCase("rpc-reply")) {
			reply = new Reply();

			messageId = attributes.getValue("message-id");
			if (messageId == null)
				throw new SAXException(new NetconfProtocolException("Received <rpc-reply> message without a messageId"));

			reply.setMessageId(messageId);
			reply.setOk(false); // defaults to false
		}
		if (localName.equalsIgnoreCase("data")) {
			insideDataTag = true;
		}
		if (localName.equalsIgnoreCase("ok")) {
			reply.setOk(true);
		}
		if (localName.equalsIgnoreCase("rpc-error")) {
			error = new Error();
		}
		if (localName.equalsIgnoreCase("error-type")) {
			insideErrorTypeTag = true;
		}
		if (localName.equalsIgnoreCase("error-tag")) {
			insideErrorTagTag = true;
		}
		if (localName.equalsIgnoreCase("error-severity")) {
			insideErrorSeverityTag = true;
		}
		if (localName.equalsIgnoreCase("error-app-tag")) {
			insideErrorAppTagTag = true;
		}
		if (localName.equalsIgnoreCase("error-path")) {
			insideErrorPathTag = true;
		}
		if (localName.equalsIgnoreCase("error-message")) {
			insideErrorMessageTag = true;
		}
		if (localName.equalsIgnoreCase("error-info")) {
			insideErrorInfoTag = true;
		}

		/* extra functionalities (out RFC) */
		if (localName.equalsIgnoreCase("interface-information")) {
			insideInterfaceInfoTag = true;
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);

		log.info(new String(ch, start, length));
		// log.info(new String(ch));

		if (insideCapabilityTag) {
			capabilityTagContent.append(ch, start, length);
			// log.debug("capability content:" + capabilityTagContent);
		}
		if (insideSessionIdTag) {
			sessionIdTagContent.append(ch, start, length);
		}
		if (insideDataTag) {
			dataTagContent.append(ch, start, length);
		}
		if (insideErrorAppTagTag) {
			errorAppTagTagContent.append(ch, start, length);
		}
		if (insideErrorInfoTag) {
			errorInfoTagContent.append(ch, start, length);
		}
		if (insideErrorMessageTag) {
			errorMessageTagContent.append(ch, start, length);
		}
		if (insideErrorPathTag) {
			errorPathTagContent.append(ch, start, length);
		}
		if (insideErrorSeverityTag) {
			errorSeverityTagContent.append(ch, start, length);
		}
		if (insideErrorTagTag) {
			errorTagTagContent.append(ch, start, length);
		}
		if (insideErrorTypeTag) {
			errorTypeTagContent.append(ch, start, length);
		}

		/* extra functionalities (out RFC) */
		if (insideInterfaceInfoTag) {
			interfaceInfoTagContent.append(ch, start, length);
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);

		// log.debug("endElement </" + localName + ">");

		// if (insideDataTag && !localName.equalsIgnoreCase("data"))
		// return;

		if (insideDataTag && !localName.equalsIgnoreCase("data")) {
			dataTagContent.append("</" + localName + ">");
		}

		if (localName.equalsIgnoreCase("hello")) {
			messageQueue.put(hello);
			hello = null;
		}
		if (localName.equalsIgnoreCase("capabilities")) {
			hello.setCapabilities(capabilities);
		}
		if (localName.equalsIgnoreCase("capability")) {
			insideCapabilityTag = false;
			capabilities.add(Capability.getCapabilityByNamespace(capabilityTagContent.toString()));
			capabilityTagContent = new StringBuffer();
		}
		if (localName.equalsIgnoreCase("session-id")) {
			insideSessionIdTag = false;
			hello.setSessionId(sessionIdTagContent.toString());
			sessionIdTagContent = new StringBuffer();
		}
		if (localName.equalsIgnoreCase("rpc-reply")) {
			messageQueue.put(reply);
			reply = null;
		}
		if (localName.equalsIgnoreCase("data")) {
			insideDataTag = false;
			reply.setContain(dataTagContent.toString());
			reply.setContainName("data");
			dataTagContent = new StringBuffer();
		}

		if (localName.equalsIgnoreCase("rpc-error")) {
			reply.addError(error);
		}
		if (localName.equalsIgnoreCase("error-type")) {
			insideErrorTypeTag = false;
			error.setType(ErrorType.valueOf(errorTypeTagContent.toString().toUpperCase()));
			errorTypeTagContent = new StringBuffer();
		}
		if (localName.equalsIgnoreCase("error-tag")) {
			insideErrorTagTag = false;
			error.setTag(ErrorTag.valueOf(errorTagTagContent.toString()));
			errorTagTagContent = new StringBuffer();
		}
		if (localName.equalsIgnoreCase("error-severity")) {
			insideErrorSeverityTag = false;
			error.setSeverity(ErrorSeverity.valueOf(errorSeverityTagContent.toString().toUpperCase()));
			errorSeverityTagContent = new StringBuffer();
		}
		if (localName.equalsIgnoreCase("error-app-tag")) {
			insideErrorAppTagTag = false;
			error.setAppTag(errorAppTagTagContent.toString());
			errorAppTagTagContent = new StringBuffer();
		}
		if (localName.equalsIgnoreCase("error-path")) {
			insideErrorPathTag = false;
			error.setPath(errorPathTagContent.toString());
			errorPathTagContent = new StringBuffer();
		}
		if (localName.equalsIgnoreCase("error-message")) {
			insideErrorMessageTag = false;
			error.setMessage(errorMessageTagContent.toString());
			errorMessageTagContent = new StringBuffer();
		}
		if (localName.equalsIgnoreCase("error-info")) {
			insideErrorInfoTag = false;
			error.setInfo(errorInfoTagContent.toString());
			errorInfoTagContent = new StringBuffer();
		}

		/* get extrafunctionalities */
		if (localName.equalsIgnoreCase("get-interface-information")) {
			insideInterfaceInfoTag = false;
			reply.setContain(interfaceInfoTagContent.toString());
			reply.setContainName("get-interface-information");
			interfaceInfoTagContent = new StringBuffer();
		}

	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		// super.error(e);
		log.warn(e.getMessage());
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		// TODO Auto-generated method stub
		// super.fatalError(e);
		log.warn(e.getMessage());
	}
}
