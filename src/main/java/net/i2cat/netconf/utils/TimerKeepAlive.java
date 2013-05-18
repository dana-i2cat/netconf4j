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

import net.i2cat.netconf.INetconfSession;
import net.i2cat.netconf.IQuery;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimerKeepAlive implements Runnable {
	public static final int		DELAY	= 1;
	ScheduledExecutorService	timer;
	private Log					log		= LogFactory.getLog(TimerKeepAlive.class);
	private IQuery				query	= QueryFactory.newKeepAlive();
	private INetconfSession		netconfSession;
	private int					period	= 0;
	private ScheduledFuture<?>	schedulerHandler;

	public TimerKeepAlive(INetconfSession netconfSession) {
		this.netconfSession = netconfSession;
		timer = Executors.newSingleThreadScheduledExecutor();
	}

	public void start(int period) {
		this.period = period;
		log.info("KeepAlive timer started");
		schedulerHandler = timer.scheduleAtFixedRate(this, period, period, TimeUnit.MINUTES);
	}

	public void reset() {
		close();
		schedulerHandler = timer.scheduleAtFixedRate(this, period, period, TimeUnit.MINUTES);
	}

	public void close() {
		if (schedulerHandler != null) {
			schedulerHandler.cancel(true);
			log.info("KeepAlive timer closed");
		}
	}

	/* Method to work */
	public void run() {
		try {
			log.info("Sending Keep Alive...");
			Reply reply = netconfSession.sendSyncQuery(query);
			if (reply.containsErrors()) {
				// there are errors in the response
				log.error("Problem in the reply: " + '\n' + reply.toXML() + '\n');
				netconfSession.disconnect();
			} else {
				// the reply doesn t contain errors, it is ok
				log.info("The reply is correct!");
			}
		} catch (TransportException e) {
			log.error(e.getMessage());
		}

	}

}
