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

public class Operation implements java.io.Serializable {

	private static final long		serialVersionUID	= 403825246463035849L;

	private String					name				= "";

	private Capability				capability			= Capability.BASE;

	public static final Operation	GET_CONFIG			= new Operation("get-config", Capability.BASE);
	public static final Operation	EDIT_CONFIG			= new Operation("edit-config", Capability.BASE);
	public static final Operation	COPY_CONFIG			= new Operation("copy-config", Capability.BASE);
	public static final Operation	DELETE_CONFIG		= new Operation("delete-config", Capability.BASE);
	public static final Operation	LOCK				= new Operation("lock", Capability.BASE);
	public static final Operation	UNLOCK				= new Operation("unlock", Capability.BASE);
	public static final Operation	GET					= new Operation("get", Capability.BASE);
	public static final Operation	CLOSE_SESSION		= new Operation("close-session", Capability.BASE);
	public static final Operation	KILL_SESSION		= new Operation("kill-session", Capability.BASE);
	public static final Operation	COMMIT				= new Operation("commit", Capability.BASE);

	public static final Operation	SET_LOGICAL_ROUTER	= new Operation("set-logical-router", Capability.JUNOS);
	public static final Operation	GET_ROUTE_INFO		= new Operation("get-route-information", Capability.JUNOS);
	public static final Operation	GET_INTERFACE_INFO	= new Operation("get-interface-information", Capability.JUNOS);
	public static final Operation	GET_SOFTWARE_INFO	= new Operation("get-software-information", Capability.JUNOS);
	public static final Operation	GET_ROLLBACK_INFO	= new Operation("get-rollback-information", Capability.JUNOS);
	public static final Operation	DISCARD				= new Operation("discard-changes", Capability.JUNOS);
	public static final Operation	OPEN_CONFIG			= new Operation("open-configuration", Capability.JUNOS);
	public static final Operation	CLOSE_CONFIG		= new Operation("close-configuration", Capability.JUNOS);
	public static final Operation   LOAD_CONFIGURATION  = new Operation("load-configuration", Capability.JUNOS);
	public static final Operation	VALIDATE			= new Operation("validate", Capability.VALIDATE);

	protected Operation(String name, Capability capability) {
		this.name = name;
		this.capability = capability;
	}

	public boolean equals(Operation compareOperation) {
		return (this.name.equals(compareOperation.name) && this.capability
				.equals(compareOperation.capability));

	}

	public String getName() {
		return name;
	}

	// public static final Operation
}
