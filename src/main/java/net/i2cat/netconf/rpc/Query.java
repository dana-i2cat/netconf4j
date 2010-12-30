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

public class Query extends RPCElement implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -333770630825691741L;

	private Operation			operation;

	/* Standard parameters */
	private String				target;
	private String				source;

	private String				sessionId;
	private String				filter;
	private String				defaultOperation;

	/* Parameters for edit-config */
	private String				editOperation;
	private String				testOption;
	private String				errorOption;
	private String				config;

	/* id logical router for extra capabilities */
	private String				idLogicalRouter;

	/*
	 * Parameters for get filter. This attribute for netconf is 'subtree' by
	 * default
	 */
	private String				filterType;

	public Query() {
	}

	public void setIdLogicalRouter(String idLogicalRouter) {
		this.idLogicalRouter = idLogicalRouter;
	}

	public String getIdLogicalRouter() {
		return idLogicalRouter;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getEditOperation() {
		return editOperation;
	}

	public void setEditOperation(String editOperation) {
		this.editOperation = editOperation;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getDefaultOperation() {
		return defaultOperation;
	}

	public void setDefaultOperation(String defaultOperation) {
		this.defaultOperation = defaultOperation;
	}

	public String getTestOption() {
		return testOption;
	}

	public void setTestOption(String testOption) {
		this.testOption = testOption;
	}

	public String getErrorOption() {
		return errorOption;
	}

	public void setErrorOption(String errorOption) {
		this.errorOption = errorOption;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String attrFilter) {
		this.filterType = attrFilter;
	}

	public String toXML() {
		String xml = "";

		// TOFIX workaround for buggy netconf server, must be configurable
		xml += "<rpc message-id=\"" + messageId + "\">";
		// xml += "<rpc message-id=\"" + messageId + "\" xmlns=\"" +
		// operation.getNamespace() + "\">";

		xml += "<" + operation.getName() + ">";

		if (idLogicalRouter != null) {
			xml += "<logical-router>" + idLogicalRouter + "</logical-router>";
		}

		if (target != null)
			xml += "<target><" + target + "/></target>";

		if (source != null)
			xml += "<source><" + source + "/></source>";
		if (filter != null) {
			xml += "<filter";
			if (filterType != null)
				xml += " type=" + filterType;
			xml += ">" + filter + "</filter>";

		}

		if (sessionId != null)
			xml += "<session-id>" + sessionId + "</session-id>";

		if (defaultOperation != null)
			xml += "<default-operation>" + defaultOperation + "</default-operation>";

		if (editOperation != null)
			xml += "<edit-operation>" + defaultOperation + "</edit-operation>";

		if (testOption != null)
			xml += "<test-option>" + defaultOperation + "</test-option>";

		if (errorOption != null)
			xml += "<error-option>" + errorOption + "</error-option>";

		if (config != null)
			xml += "<config>" + config + "</config>";

		xml += "</" + operation.getName() + ">";

		xml += "</rpc>";

		return xml;
	}

}
