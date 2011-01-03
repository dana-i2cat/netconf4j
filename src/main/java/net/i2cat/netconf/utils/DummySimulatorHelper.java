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
package net.i2cat.netconf.utils;

import java.io.File;

import net.i2cat.netconf.rpc.Hello;
import net.i2cat.netconf.rpc.Operation;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.RPCElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
public class DummySimulatorHelper {

	private Log		log				= LogFactory.getLog(DummySimulatorHelper.class);

	private String	errorFile		= "mock" + File.separator + "responseError.xml";
	private String	okFile			= "mock" + File.separator + "responseOk.xml";
	private String	infoFile		= "mock" + File.separator + "responseInfo.xml";
	private String	helloFile		= "mock" + File.separator + "responseHello.xml";

	private boolean	responseError	= false;

	public void setResponseError(boolean respError) {
		this.responseError = respError;
	}

	public String generateReply(RPCElement request) {

		String pathFile = "";
		String strResponse = "";
		String messageId = "-1";

		log.debug("Request: ");
		log.debug(request.toXML());

		if (request instanceof Hello) {
			log.debug("Crafting a Hello response.");
			pathFile = helloFile;
		} else if (request instanceof Query) {

			// Check if we want to response errors
			if (responseError) {

				log.debug("Crafting an Error response.");
				pathFile = errorFile;

			} else {
				/* get file configuration */
				Operation oper = ((Query) request).getOperation();
				/* get message ID */
				messageId = ((Query) request).getMessageId();

				if (oper.equals(Operation.GET) || oper.equals(Operation.GET_CONFIG)) {
					log.debug("Crafting an Info response.");
					pathFile = infoFile;
				} else {
					log.debug("Crafting an Ok response.");
					pathFile = okFile;
				}

			}

		}

		log.info("Trying to open " + pathFile);
		strResponse = FileHelper.readStringFromFile(pathFile);
		// strResponse = deleteStringEndNETCONF(strResponse);
		/* change message ID */
		if (!messageId.equals("-1"))
			strResponse = changeMessageIdNETCONF(strResponse, messageId);

		return strResponse;

	}

	private String changeMessageIdNETCONF(String strResponse, String messageId) {
		strResponse = strResponse.replaceAll("message-id=\"###\"", "message-id=\"" + messageId + "\"");
		return strResponse;
	}

	private static String deleteStringEndNETCONF(String str) {
		if (str.indexOf("]]>]]>") != -1) {
			return str.split("]]>]]>")[0];
		}
		return str;
	}

}
