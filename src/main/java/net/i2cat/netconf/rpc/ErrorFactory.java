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

/**
 * Factory class to create {@link Error}'s for Netconf {@link Reply}'s
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class ErrorFactory {

	/**
	 * Constructs a valid error using parameters
	 * 
	 * @param type
	 *            {@link ErrorType}
	 * @param tag
	 *            {@link ErrorTag}
	 * @param severity
	 *            {@link ErrorSeverity}
	 * @param appTag
	 *            Application tag (optional)
	 * @param path
	 *            Error path (optional)
	 * @param message
	 *            Error message (optional)
	 * @param info
	 *            Error info (optional)
	 * @return
	 */
	public static Error newError(ErrorType type, ErrorTag tag, ErrorSeverity severity, String appTag, String path, String message, String info) {
		Error error = new Error();
		error.setType(type);
		error.setTag(tag);
		error.setSeverity(severity);
		error.setAppTag(appTag);
		error.setPath(path);
		error.setMessage(message);
		error.setInfo(info);
		return error;
	}

}
