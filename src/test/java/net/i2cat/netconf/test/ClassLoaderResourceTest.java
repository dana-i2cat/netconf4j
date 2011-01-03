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
package net.i2cat.netconf.test;

import java.io.File;

import net.i2cat.netconf.utils.FileHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class ClassLoaderResourceTest {
	private Log	log	= LogFactory.getLog(ClassLoaderResourceTest.class);

	@Test
	public void readMockFiles() {
		String file = "mock" + File.separator + "ipconfiguration.xml";

		String information = FileHelper.readStringFromFile(file);

		// log.info("info: " + '\n' + information + '\n');
	}

}
