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
package net.i2cat.netconf.test;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.Assert;
import net.i2cat.netconf.messageQueue.MessageQueue;
import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.ErrorSeverity;
import net.i2cat.netconf.rpc.ErrorTag;
import net.i2cat.netconf.rpc.ErrorType;
import net.i2cat.netconf.rpc.Reply;
import net.i2cat.netconf.transport.TransportContentParser;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TransportContentparserTest {

	XMLReader				parser;
	TransportContentParser	xmlHandler;
	MessageQueue			queue;

	@Before
	public void initParser() throws SAXException {
		queue = new MessageQueue();
		xmlHandler = new TransportContentParser();
		xmlHandler.setMessageQueue(queue);
		parser = XMLReaderFactory.createXMLReader();
		parser.setContentHandler(xmlHandler);
		parser.setErrorHandler(xmlHandler);
	}

	@Test
	public void parseReplyErrorTest() throws IOException, SAXException {

		String messageId = "1";
		String message = "Malformed XML!!!";

		for (ErrorType type : ErrorType.values()) {
			for (ErrorTag tag : ErrorTag.values()) {
				for (ErrorSeverity severity : ErrorSeverity.values()) {

					String replyMsg = buildErrorRepy(messageId, type.toString(), tag.toString(), severity.toString(), message);

					parseMessage(replyMsg);

					Reply reply = (Reply) queue.consumeById(messageId);
					Assert.assertNotNull(reply);
					Assert.assertTrue(reply.containsErrors());

					Error error = reply.getErrors().get(0);
					Assert.assertEquals(severity, error.getSeverity());
					Assert.assertEquals(type, error.getType());
					Assert.assertEquals(tag, error.getTag());
					Assert.assertEquals(message, error.getMessage());
				}
			}
		}
	}

	@Test
	public void unsupportedValuesInErrorsCausesFailTest() throws IOException, SAXException {

		String messageId = "1";
		String message = "Malformed XML!!!";
		String unsupported = "unsupported-:P";

		ErrorType type = ErrorType.PROTOCOL;
		ErrorTag tag = ErrorTag.OPERATION_FAILED;
		ErrorSeverity severity = ErrorSeverity.ERROR;

		String unsupportedTypeReply = buildErrorRepy(messageId, unsupported, tag.toString(), severity.toString(), message);
		String unsupportedTagReply = buildErrorRepy(messageId, type.toString(), unsupported, severity.toString(), message);
		String unsupportedSeverityReply = buildErrorRepy(messageId, type.toString(), tag.toString(), unsupported, message);

		try {
			parseMessage(unsupportedTypeReply);
			Assert.fail("Parsing should fail but didn't!");
		} catch (IllegalArgumentException e) {
		}

		try {
			parseMessage(unsupportedTagReply);
			Assert.fail("Parsing should fail but didn't!");
		} catch (IllegalArgumentException e) {
		}

		try {
			parseMessage(unsupportedSeverityReply);
			Assert.fail("Parsing should fail but didn't!");
		} catch (IllegalArgumentException e) {
		}
	}

	private String buildErrorRepy(String messageId, String type, String tag, String severity, String message) {

		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<rpc-reply xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + messageId + "\">");
		sb.append("<rpc-error>");
		sb.append("<error-type>" + type + "</error-type>");
		sb.append("<error-tag>" + tag + "</error-tag>");
		sb.append("<error-severity>" + severity + "</error-severity>");
		sb.append("<error-message>" + message + "</error-message>");
		sb.append("</rpc-error>");
		sb.append("</rpc-reply>");
		sb.append("]]>]]>");

		return sb.toString();
	}

	private void parseMessage(String message) throws IOException, SAXException {
		try {
			parser.parse(new InputSource(new StringReader(message)));
		} catch (SAXException e) {
			if (e.getMessage().contentEquals("Content is not allowed in trailing section.")) {
				// Using shitty non-xml delimiters forces us to detect
				// end-of-frame by a SAX error.
				// Blame netconf
			}
			else {
				throw e;
			}
		}
	}

}
