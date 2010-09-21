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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.ErrorSeverity;
import net.i2cat.netconf.rpc.ErrorTag;
import net.i2cat.netconf.rpc.ErrorType;
import net.i2cat.netconf.rpc.Hello;
import net.i2cat.netconf.rpc.Reply;

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
	String					capabilityTagContent	= "";
	ArrayList<Capability>	capabilities;

	boolean					insideSessionIdTag		= false;
	String					sessionIdTagContent		= "";

	boolean					insideDataTag			= false;
	String					dataTagContent			= "";

	boolean					insideErrorTypeTag		= false;
	String					errorTypeTagContent		= "";

	boolean					insideErrorTagTag		= false;
	String					errorTagTagContent		= "";

	boolean					insideErrorSeverityTag	= false;
	String					errorSeverityTagContent	= "";

	boolean					insideErrorAppTagTag	= false;
	String					errorAppTagTagContent	= "";

	boolean					insideErrorPathTag		= false;
	String					errorPathTagContent		= "";

	boolean					insideErrorMessageTag	= false;
	String					errorMessageTagContent	= "";

	boolean					insideErrorInfoTag		= false;
	String					errorInfoTagContent		= "";

	public void setMessageQueue(MessageQueue queue) {
		this.messageQueue = queue;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (insideDataTag && !localName.equalsIgnoreCase("data"))
			return;

		log.debug("startElement <" + uri + "::" + localName + ">");

		// if (insideDataTag)
		// return;

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
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);

		// log.info(new String(ch, start, length));
		// log.info(new String(ch));

		if (insideCapabilityTag) {
			capabilityTagContent += new String(ch, start, length);
			// log.debug("capability content:" + capabilityTagContent);
		}
		if (insideSessionIdTag) {
			sessionIdTagContent += new String(ch, start, length);
		}
		if (insideDataTag) {
			dataTagContent += new String(ch, start, length);
		}
		if (insideErrorAppTagTag) {
			errorAppTagTagContent += new String(ch, start, length);
		}
		if (insideErrorInfoTag) {
			errorInfoTagContent += new String(ch, start, length);
		}
		if (insideErrorMessageTag) {
			errorMessageTagContent += new String(ch, start, length);
		}
		if (insideErrorPathTag) {
			errorPathTagContent += new String(ch, start, length);
		}
		if (insideErrorSeverityTag) {
			errorSeverityTagContent += new String(ch, start, length);
		}
		if (insideErrorTagTag) {
			errorTagTagContent += new String(ch, start, length);
		}
		if (insideErrorTypeTag) {
			errorTypeTagContent += new String(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);

		// log.debug("endElement </" + localName + ">");

		// if (insideDataTag && !localName.equalsIgnoreCase("data"))
		// return;

		if (localName.equalsIgnoreCase("hello")) {
			messageQueue.put(hello);
			hello = null;
		}
		if (localName.equalsIgnoreCase("capabilities")) {
			hello.setCapabilities(capabilities);
		}
		if (localName.equalsIgnoreCase("capability")) {
			insideCapabilityTag = false;
			capabilities.add(Capability.getCapabilityByNamespace(capabilityTagContent));
			capabilityTagContent = "";
		}
		if (localName.equalsIgnoreCase("session-id")) {
			insideSessionIdTag = false;
			hello.setSessionId(sessionIdTagContent);
		}
		if (localName.equalsIgnoreCase("rpc-reply")) {
			messageQueue.put(reply);
			reply = null;
		}
		if (localName.equalsIgnoreCase("data")) {
			insideDataTag = false;
			reply.setData(dataTagContent);
			dataTagContent = "";
		}
		if (localName.equalsIgnoreCase("rpc-error")) {
			reply.addError(error);
		}
		if (localName.equalsIgnoreCase("error-type")) {
			insideErrorTypeTag = false;
			error.setType(ErrorType.valueOf(errorTypeTagContent.toUpperCase()));
			errorTypeTagContent = "";
		}
		if (localName.equalsIgnoreCase("error-tag")) {
			insideErrorTagTag = false;
			error.setTag(ErrorTag.valueOf(errorTagTagContent));
			errorTagTagContent = "";
		}
		if (localName.equalsIgnoreCase("error-severity")) {
			insideErrorSeverityTag = false;
			error.setSeverity(ErrorSeverity.valueOf(errorSeverityTagContent.toUpperCase()));
			errorSeverityTagContent = "";
		}
		if (localName.equalsIgnoreCase("error-app-tag")) {
			insideErrorAppTagTag = false;
			error.setAppTag(errorAppTagTagContent);
			errorAppTagTagContent = "";
		}
		if (localName.equalsIgnoreCase("error-path")) {
			insideErrorPathTag = false;
			error.setPath(errorPathTagContent);
			errorPathTagContent = "";
		}
		if (localName.equalsIgnoreCase("error-message")) {
			insideErrorMessageTag = false;
			error.setMessage(errorMessageTagContent);
			errorMessageTagContent = "";
		}
		if (localName.equalsIgnoreCase("error-info")) {
			insideErrorInfoTag = false;
			error.setInfo(errorInfoTagContent);
			errorInfoTagContent = "";
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
