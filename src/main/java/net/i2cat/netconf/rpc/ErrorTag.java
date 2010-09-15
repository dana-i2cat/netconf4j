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

public enum ErrorTag {

	IN_USE("in-use"),
	INVALID_VALUE("invalid-value"),
	TOO_BIG("too-big"),
	MISSING_ATTRIBUTE("missing-attribute"),
	BAD_ATTRIBUTE("bad-attribute"),
	UNKNOWN_ATTRIBUTE("unknown-attribute"),
	MISSING_ELEMENT("missing-element"),
	BAD_ELEMENT("bad-element"),
	UNKNOWN_ELEMENT("unknown-element"),
	UNKNOWN_NAMESPACE("unkown-namespace"),
	ACCESS_DENIED("access-denied"),
	LOCK_DENIED("lock-denied"),
	RESOURCE_DENIED("resource-denied"),
	ROLLBACK_FAILED("rollback-failed"),
	DATA_EXISTS("data-exists"),
	DATA_MISSING("data-missing"),
	OPERATION_NOT_SUPPORTED("operation-not-supported"),
	OPERATION_FAILED("operation-failed"),
	PARTIAL_OPERATION("partial-operation");

	String	tag;

	ErrorTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
