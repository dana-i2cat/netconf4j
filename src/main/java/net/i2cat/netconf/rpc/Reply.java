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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

public class Reply extends RPCElement {

	private static final long	serialVersionUID	= -1583831174698205743L;

	Vector<Error>				errors;

	boolean						ok					= false;

	String						containName;

	String						contain;

	HashMap<String, String>		attributes;

	public Reply() {
		errors = new Vector<Error>();
		attributes = new HashMap<String, String>();
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public String getAttribute(Object key) {
		return attributes.get(key);
	}

	public String putAttribute(String key, String value) {
		return attributes.put(key, value);
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getContain() {
		return contain;
	}

	public void setContain(String contain) {
		this.contain = contain;
	}

	public String getContainName() {
		return containName;
	}

	public void setContainName(String containName) {
		this.containName = containName;
	}

	public Vector<Error> getErrors() {
		return errors;
	}

	public void setErrors(Vector<Error> errors) {
		this.errors = errors;
	}

	public boolean containsErrors() {
		return errors.size() > 0;
	}

	public void addError(Error error) {
		errors.add(error);
	}

	@Override
	public String toXML() {
		StringBuilder xmlBuilder = new StringBuilder();

		xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		// start rpc-reply beginning with message ID attribute
		xmlBuilder.append("<rpc-reply message-id=\"");
		xmlBuilder.append(getMessageId());
		xmlBuilder.append("\" xmlns:junos=\"http://xml.juniper.net/junos/11.2R1/junos\"");

		// write extra attributes
		if (getAttributes() != null && !getAttributes().isEmpty()) {
			for (Entry<String, String> attributteEntry : getAttributes().entrySet()) {
				xmlBuilder.append(attributteEntry.getKey());
				xmlBuilder.append("=\"");
				xmlBuilder.append(attributteEntry.getValue());
				xmlBuilder.append("\" ");
			}
		}

		// end rpc-reply tag beginning
		xmlBuilder.append(">");

		// write contain
		if (getContainName() != null && getContain() != null) {
			xmlBuilder.append("<" + getContainName() + ">");
			xmlBuilder.append(getContain());
			xmlBuilder.append("</" + getContainName() + ">");
		}

		// OK
		if (ok) {
			xmlBuilder.append("<ok />");
		}

		// errors
		if (getErrors() != null && getErrors().size() > 0) {
			for (Error error : getErrors()) {
				// begin tag
				xmlBuilder.append("<rpc-error>");

				// type
				xmlBuilder.append("<error-type>");
				xmlBuilder.append(error.getType());
				xmlBuilder.append("</error-type>");

				// tag
				xmlBuilder.append("<error-tag>");
				xmlBuilder.append(error.getTag());
				xmlBuilder.append("</error-tag>");

				// severity
				xmlBuilder.append("<error-severity>");
				xmlBuilder.append(error.getSeverity());
				xmlBuilder.append("</error-severity>");

				// app tag (optional)
				if (error.getAppTag() != null) {
					xmlBuilder.append("<error-app-tag>");
					xmlBuilder.append(error.getAppTag());
					xmlBuilder.append("</error-app-tag>");
				}

				// error path (optional)
				if (error.getPath() != null) {
					xmlBuilder.append("<error-path>");
					xmlBuilder.append(error.getPath());
					xmlBuilder.append("</error-path>");
				}

				// message (optional)
				if (error.getMessage() != null) {
					xmlBuilder.append("<error-message>");
					xmlBuilder.append(error.getMessage());
					xmlBuilder.append("</error-message>");
				}

				// error info (optional)
				if (error.getInfo() != null) {
					xmlBuilder.append("<error-info>");
					xmlBuilder.append(error.getInfo());
					xmlBuilder.append("</error-info>");
				}

				// end tag
				xmlBuilder.append("</rpc-error>");
			}
		}

		// close rpc-reply tag
		xmlBuilder.append("</rpc-reply>");
		return xmlBuilder.toString();
	}

}
