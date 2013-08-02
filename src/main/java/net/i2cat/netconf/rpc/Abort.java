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

import net.i2cat.netconf.messageQueue.MessageQueue;

/**
 * An RPCElement to insert into the MessageQueue and trigger an abort.
 */
public class Abort extends RPCElement {
	private String message;
	private Exception exception;

	public Abort(String message, Exception exception){
		this.message = message;
		this.exception = exception;
		this.messageId = MessageQueue.ABORT_MESSAGE_ID;
	}

	public Abort(String message) {
		this(message, null);
	}

	public Abort(Exception exception) {
		this("Abort", exception);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
