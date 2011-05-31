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
 * Factory class to create different netconf queries. It allows to create queries for the base standard
 * 
 * @author carlos
 * 
 */
public class QueryFactory {

	public static Query newGetConfig(String source, String filter, String attrFilter) {
		Query query = new Query();
		query.setOperation(Operation.GET_CONFIG);
		query.setSource(source);
		query.setFilter(filter);
		query.setFilterType(attrFilter);
		return query;
	}

	public static Query newGet(String filter) {
		Query query = new Query();
		query.setOperation(Operation.GET);
		query.setFilter(filter);

		return query;
	}

	public static Query newEditConfig(String target,
										String defaultOperation,
										String testOption,
										String errorOption,
										String config) {
		Query query = new Query();
		query.setOperation(Operation.EDIT_CONFIG);
		query.setTarget(target);
		query.setDefaultOperation(defaultOperation);
		query.setTestOption(testOption);
		query.setErrorOption(errorOption);
		query.setConfig(config);

		return query;
	}

	public static Query newCloseSession() {
		Query query = new Query();
		query.setOperation(Operation.CLOSE_SESSION);

		return query;
	}

	public static Query newCopyConfig(String target, String source) {
		Query query = new Query();
		query.setOperation(Operation.COPY_CONFIG);
		query.setTarget(target);
		query.setSource(source);

		return query;
	}

	public static Query newDeleteConfig(String target) {
		Query query = new Query();
		query.setOperation(Operation.DELETE_CONFIG);
		query.setTarget(target);

		return query;
	}

	public static Query newKillSession() {
		Query query = new Query();
		query.setOperation(Operation.KILL_SESSION);

		return query;
	}

	public static Query newLock(String target) {
		Query query = new Query();
		query.setOperation(Operation.LOCK);
		query.setTarget(target);

		return query;
	}

	public static Query newKeepAlive() {

		Query query = new Query();
		query.setOperation(Operation.GET);
		query.setFilter("");
		query.setFilterType("subtree");

		return query;
	}

	public static Query newUnlock(String target) {
		Query query = new Query();
		query.setOperation(Operation.UNLOCK);
		query.setTarget(target);

		return query;
	}

	public static Query newCommit() {
		Query query = new Query();
		query.setOperation(Operation.COMMIT);

		return query;
	}

	/* Extra queries */

	public static Query newSetLogicalRouter(String idLogicalRouter) {
		Query query = new Query();
		query.setOperation(Operation.SET_LOGICAL_ROUTER);
		query.setIdLogicalRouter(idLogicalRouter);
		return query;
	}

	public static Query newGetRouteInformation() {
		Query query = new Query();
		query.setOperation(Operation.GET_ROUTE_INFO);
		return query;
	}

	public static Query newGetInterfaceInformation() {
		Query query = new Query();
		query.setOperation(Operation.GET_INTERFACE_INFO);
		return query;
	}

	public static Query newGetSoftwareInformation() {
		Query query = new Query();
		query.setOperation(Operation.GET_SOFTWARE_INFO);
		return query;
	}

	public static Query newGetRollbackInformation(String rollback) {
		Query query = new Query();
		query.setOperation(Operation.GET_ROLLBACK_INFO);
		query.setRollback(rollback);
		return query;
	}

	public static Query newValidate(String source) {
		Query query = new Query();
		query.setOperation(Operation.VALIDATE);
		query.setSource(source);
		return query;
	}

}
