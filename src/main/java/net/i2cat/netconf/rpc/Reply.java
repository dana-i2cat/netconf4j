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
import java.util.Vector;

public class Reply extends RPCElement {

	Operation				operation;

	Vector<Error>			errors;

	boolean					ok	= false;

	String					containName;

	String					contain;

	HashMap<String, String>	attributes;

	public Reply() {
		errors = new Vector<Error>();
		attributes = new HashMap<String, String>();
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public Operation getOperation() {
		return operation;
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

}
