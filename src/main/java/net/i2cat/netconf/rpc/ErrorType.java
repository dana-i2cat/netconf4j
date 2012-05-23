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

public enum ErrorType {
	TRANSPORT("transport"),
	RPC("rpc"),
	PROTOCOL("protocol"),
	APPLICATION("application"),
	OTHER("other");

	private String	value;
	String			other;

	ErrorType(String value) {
		this.value = value;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	@Override
	public String toString() {
		return value;
	}

	/**
	 * 
	 * @param value
	 * @return ErrorType constant matching given value
	 * @throws IllegalArgumentException
	 *             if given value does not match any allowed value.
	 */
	public static ErrorType getErrorTypeByValue(String value) {
		for (ErrorType errorType : values()) {
			if (errorType.value.equals(value)) {
				return errorType;
			}
		}
		throw new IllegalArgumentException("No enum const class " + ErrorType.class.getName() + "." + value);
	}
}
