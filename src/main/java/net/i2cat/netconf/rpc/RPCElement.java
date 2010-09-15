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

import org.apache.commons.configuration.CompositeConfiguration;

public class RPCElement implements java.io.Serializable {

	String					messageId;

	// extra parameters from
	CompositeConfiguration	ctx;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public CompositeConfiguration getCtx() {
		return ctx;
	}

	public void setCtx(CompositeConfiguration ctx) {
		this.ctx = ctx;
	}

	public boolean existCtx() {
		return (ctx != null);
	}

	public String toXML() {
		return "NOT IMPLEMENTED";
	}

}
