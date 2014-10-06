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
 * @author Julio Carlos Barrera
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

	/* extra features from JUNOS (out RFC) */

	boolean					insideInterfaceInfoTag	= false;
	StringBuffer			interfaceInfoTagContent	= new StringBuffer();

	boolean					insideSoftwareInfoTag	= false;
	StringBuffer			softwareInfoTagContent	= new StringBuffer();

	// any other under <rpc-reply> tag
	boolean					insideRPCReplyTag		= false;
	String					underRPCReplyTagName;
	StringBuffer			underRPCReplyTagContent	= new StringBuffer();
	boolean					insideUnderRPCReplyTag	= false;

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

		else if (insideSoftwareInfoTag) {
			softwareInfoTagContent.append("<" + localName + ">");
		}

		// log.debug("startElement <" + uri + "::" + localName + ">");

		if (localName.equalsIgnoreCase("hello")) {
			hello = new Hello();
			capabilities = new ArrayList<Capability>();
		}
		// if (localName.equalsIgnoreCase("capabilities")) {
		// insideCapabilityTag = false;
		// }
		else if (localName.equalsIgnoreCase("capability")) {
			insideCapabilityTag = true;
		}
		else if (localName.equalsIgnoreCase("session-id")) {
			insideSessionIdTag = true;
		}
		else if (localName.equalsIgnoreCase("rpc-reply")) {
			reply = new Reply();

			messageId = attributes.getValue("message-id");
			if (messageId == null)
				throw new SAXException(new NetconfProtocolException("Received <rpc-reply> message without a messageId"));

			reply.setMessageId(messageId);
			reply.setOk(false); // defaults to false

			insideRPCReplyTag = true;
		}
		else if (localName.equalsIgnoreCase("data")) {
			insideDataTag = true;
		}
		else if (localName.equalsIgnoreCase("ok")) {
			reply.setOk(true);
		}
		else if (localName.equalsIgnoreCase("rpc-error")) {
			error = new Error();
		}
		else if (localName.equalsIgnoreCase("error-type")) {
			insideErrorTypeTag = true;
		}
		else if (localName.equalsIgnoreCase("error-tag")) {
			insideErrorTagTag = true;
		}
		else if (localName.equalsIgnoreCase("error-severity")) {
			insideErrorSeverityTag = true;
		}
		else if (localName.equalsIgnoreCase("error-app-tag")) {
			insideErrorAppTagTag = true;
		}
		else if (localName.equalsIgnoreCase("error-path")) {
			insideErrorPathTag = true;
		}
		else if (localName.equalsIgnoreCase("error-message")) {
			insideErrorMessageTag = true;
		}
		else if (localName.equalsIgnoreCase("error-info")) {
			insideErrorInfoTag = true;
		}

		/* extra features from JUNOS (out RFC) */
		else if (localName.equalsIgnoreCase("interface-information")) {
			insideInterfaceInfoTag = true;
		}
		else if (localName.equalsIgnoreCase("software-information")) {
			// software-information is the root node and leaving it in place
			// makes gives us a well-formed XML document rather than multiple
			// top-level nodes.
			softwareInfoTagContent.append("<" + localName + ">");
			insideSoftwareInfoTag = true;
		}
		/* any other under <rpc-reply> tag */
		else if (insideRPCReplyTag) {
			insideRPCReplyTag = false;
			underRPCReplyTagName = localName;
			underRPCReplyTagContent.append("<" + localName + ">");
			insideUnderRPCReplyTag = true;
		} else if (insideUnderRPCReplyTag) {
			underRPCReplyTagContent.append("<" + localName + ">");
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
		else if (insideSessionIdTag) {
			sessionIdTagContent.append(ch, start, length);
		}
		else if (insideDataTag) {
			dataTagContent.append(ch, start, length);
		}
		else if (insideErrorAppTagTag) {
			errorAppTagTagContent.append(ch, start, length);
		}
		else if (insideErrorInfoTag) {
			errorInfoTagContent.append(ch, start, length);
		}
		else if (insideErrorMessageTag) {
			errorMessageTagContent.append(ch, start, length);
		}
		else if (insideErrorPathTag) {
			errorPathTagContent.append(ch, start, length);
		}
		else if (insideErrorSeverityTag) {
			errorSeverityTagContent.append(ch, start, length);
		}
		else if (insideErrorTagTag) {
			errorTagTagContent.append(ch, start, length);
		}
		else if (insideErrorTypeTag) {
			errorTypeTagContent.append(ch, start, length);
		}

		/* extra features from JUNOS (out RFC) */
		else if (insideInterfaceInfoTag) {
			interfaceInfoTagContent.append(ch, start, length);
		}
		else if (insideSoftwareInfoTag) {
			softwareInfoTagContent.append(ch, start, length);
		}

		/* any other under <rpc-reply> tag */
		else if (insideUnderRPCReplyTag) {
			underRPCReplyTagContent.append(ch, start, length);
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

		else if (insideSoftwareInfoTag && !localName.equalsIgnoreCase("software-information")) {
			softwareInfoTagContent.append("</" + localName + ">");
		}

		if (localName.equalsIgnoreCase("hello")) {
			messageQueue.put(hello);
			hello = null;
		}
		else if (localName.equalsIgnoreCase("capabilities")) {
			hello.setCapabilities(capabilities);
		}
		else if (localName.equalsIgnoreCase("capability")) {
			insideCapabilityTag = false;
			capabilities.add(Capability.getCapabilityByNamespace(capabilityTagContent.toString()));
			capabilityTagContent = new StringBuffer();
		}
		else if (localName.equalsIgnoreCase("session-id")) {
			insideSessionIdTag = false;
			hello.setSessionId(sessionIdTagContent.toString());
			sessionIdTagContent = new StringBuffer();
		}
		else if (localName.equalsIgnoreCase("rpc-reply")) {
			messageQueue.put(reply);
			reply = null;
		}
		else if (localName.equalsIgnoreCase("data")) {
			insideDataTag = false;
			reply.setContain(dataTagContent.toString());
			reply.setContainName("data");
			dataTagContent = new StringBuffer();
		}

		else if (localName.equalsIgnoreCase("rpc-error")) {
			reply.addError(error);
		}
		else if (localName.equalsIgnoreCase("error-type")) {
			insideErrorTypeTag = false;
			error.setType(ErrorType.getErrorTypeByValue(errorTypeTagContent.toString()));
			errorTypeTagContent = new StringBuffer();
		}
		else if (localName.equalsIgnoreCase("error-tag")) {
			insideErrorTagTag = false;
			error.setTag(ErrorTag.getErrorTagByValue((errorTagTagContent.toString())));
			errorTagTagContent = new StringBuffer();
		}
		else if (localName.equalsIgnoreCase("error-severity")) {
			insideErrorSeverityTag = false;
			error.setSeverity(ErrorSeverity.getErrorSeverityByValue(errorSeverityTagContent.toString()));
			errorSeverityTagContent = new StringBuffer();
		}
		else if (localName.equalsIgnoreCase("error-app-tag")) {
			insideErrorAppTagTag = false;
			error.setAppTag(errorAppTagTagContent.toString());
			errorAppTagTagContent = new StringBuffer();
		}
		else if (localName.equalsIgnoreCase("error-path")) {
			insideErrorPathTag = false;
			error.setPath(errorPathTagContent.toString());
			errorPathTagContent = new StringBuffer();
		}
		else if (localName.equalsIgnoreCase("error-message")) {
			insideErrorMessageTag = false;
			error.setMessage(errorMessageTagContent.toString());
			errorMessageTagContent = new StringBuffer();
		}
		else if (localName.equalsIgnoreCase("error-info")) {
			insideErrorInfoTag = false;
			error.setInfo(errorInfoTagContent.toString());
			errorInfoTagContent = new StringBuffer();
		}

		/* get extrafunctionalities */
		else if (localName.equalsIgnoreCase("get-interface-information")) {
			insideInterfaceInfoTag = false;
			reply.setContain(interfaceInfoTagContent.toString());
			reply.setContainName("get-interface-information");
			interfaceInfoTagContent = new StringBuffer();
		}

		else if (localName.equalsIgnoreCase("software-information")) {
			insideSoftwareInfoTag = false;
			// software-information is the root node and leaving it in place
			// makes gives us a well-formed XML document rather than multiple
			// top-level nodes.
			softwareInfoTagContent.append("</" + localName + ">");
			reply.setContain(softwareInfoTagContent.toString());
			reply.setContainName("software-information");
			softwareInfoTagContent = new StringBuffer();
		}

		/* any other under <rpc-reply> tag */
		else if (insideUnderRPCReplyTag && localName.equals(underRPCReplyTagName)) {
			insideUnderRPCReplyTag = false;
			underRPCReplyTagContent.append("</" + localName + ">");
			reply.setContainName(underRPCReplyTagName);
			reply.setContain(underRPCReplyTagContent.toString());
			underRPCReplyTagContent = new StringBuffer();
		} else if (insideUnderRPCReplyTag) {
			underRPCReplyTagContent.append("</" + localName + ">");
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
