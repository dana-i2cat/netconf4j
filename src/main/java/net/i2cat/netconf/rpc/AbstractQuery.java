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
package net.i2cat.netconf.rpc;

import net.i2cat.netconf.IQuery;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Base class for constructing a non-trivial query.
 */
public abstract class AbstractQuery extends RPCElement implements IQuery {
	private String		messageId;
	private Operation	operation;

	protected AbstractQuery(Operation operation) {
		this.operation = operation;
	}

	public String toXML() {
		StringBuilder str = new StringBuilder();

		str.append("<rpc message-id=\"" + messageId + "\">");

		StringWriter out = new StringWriter();
		try {
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(innerXml()), new StreamResult(out));
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}

		str.append(out.toString());
		str.append("</rpc>");

		return str.toString();
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Operation getOperation() {
		return operation;
	}

	public RPCElement getRpcElement() {
		return this;
	}

	protected abstract Node innerXml();
}
