/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * This plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jivesoftware.sparkimpl.plugin.idle;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.idle.linux.LinuxIdleTime;
import org.jivesoftware.sparkimpl.plugin.idle.mac.MacIdleTime;
import org.jivesoftware.sparkimpl.plugin.idle.windows.Win32IdleTime;
import org.jivesoftware.sparkimpl.plugin.idle.windows.WinLockListener;
import org.jivesoftware.sparkimpl.plugin.phone.PhonePlugin;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.util.Timer;
import java.util.TimerTask;

import static org.jivesoftware.sparkimpl.plugin.idle.linux.LinuxIdleTime.getIdleTimeMillisLinux;
import static org.jivesoftware.sparkimpl.plugin.idle.mac.MacIdleTime.getIdleTimeMillisMac;
import static org.jivesoftware.sparkimpl.plugin.idle.windows.Win32IdleTime.getIdleTimeMillisWin32;

public class UserIdlePlugin extends TimerTask implements Plugin {

	private final int CHECKTIME = 2;
	private double x = 0;
	private double y = 0;
	private boolean hasChanged = false;
	private int counter = 0;
	public static LocalPreferences pref = SettingsManager.getLocalPreferences();
	public static Presence latestPresence;
	private static boolean DesktopLockStatus;
	private static String statustext;
	private LockListener isLocked;
	private boolean IsLocked;
	private IdleTimer idleTimer;

	public boolean getFromLockListener() {
		return IsLocked;
	}

	public static boolean getDesktopLockStatus() {

		return DesktopLockStatus;
	}

	@Override
	public boolean canShutDown() {
		return false;
	}

	@Override
	public void initialize() {
		Timer timer = new Timer();
		// Check all 5 seconds
		timer.schedule(this, (1000 * 10), (1000 * CHECKTIME));

		if (Spark.isWindows()) {
			isLocked = new LockListener();
			isLocked.intWinLockListener();
			idleTimer = new IdleTimer();
			idleTimer.intWin32IdleTime();
		}
		if (Spark.isMac()) {
			idleTimer = new IdleTimer();
			idleTimer.intMacIdleTime();
		}
		if (Spark.isLinux()) {
			idleTimer = new IdleTimer();
			idleTimer.intLinuxIdleTime();
		}
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void uninstall() {
	}

	private void setIdle() {

		latestPresence = SparkManager.getWorkspace().getStatusBar().getPresence();

		if (latestPresence.getStatus().equals(Res.getString("status.online")) || latestPresence.getStatus().equals(Res.getString("status.free.to.chat"))) {
			statustext = pref.getIdleMessage();
		} else {
			statustext = latestPresence.getStatus();
		}

		if (latestPresence.isAway()) {
			Log.debug("UserIdlePlugin: Presence is already set to away");
		} else {
			Presence statusPresence = new Presence(Presence.Type.available, StringUtils.modifyWildcards(statustext), 0, Presence.Mode.away);
			SparkManager.getSessionManager().changePresence(statusPresence);
			Log.debug("UserIdlePlugin: Setting idle presence");
		}
	}


	private void setOnline() {
		DesktopLockStatus = false;

		if (PhonePlugin.onPhonePresence != null) {
			SparkManager.getSessionManager().changePresence(PhonePlugin.onPhonePresence);
			Log.debug("UserIdlePlugin: Returning from idle/lock - On the Phone");

		} else if ((latestPresence.getStatus().contains("On the phone")) && (PhonePlugin.offPhonePresence != null)
				&& ((PhonePlugin.offPhonePresence.getMode().equals(Presence.Mode.dnd))
				|| (PhonePlugin.offPhonePresence.getMode().equals(Presence.Mode.xa)))) {
			SparkManager.getSessionManager().changePresence(PhonePlugin.offPhonePresence);
			Log.debug("UserIdlePlugin: Matched DND/XA - Setting presence from PhonePlugin");

		} else if (((latestPresence.getStatus().contains("On the phone")) && (PhonePlugin.offPhonePresence != null)
				&& (PhonePlugin.offPhonePresence.getStatus().contentEquals(statustext)))) {
			Presence presence = new Presence(Presence.Type.available, PhonePlugin.offPhonePresence.getStatus(), 1, Presence.Mode.available);
			SparkManager.getSessionManager().changePresence(presence);
			Log.debug("UserIdlePlugin: Setting presence from PhonePlugin ....");

		} else if ((latestPresence.getStatus().contains("On the phone")) && (PhonePlugin.offPhonePresence != null)) {
			SparkManager.getSessionManager().changePresence(PhonePlugin.offPhonePresence);
			Log.debug("UserIdlePlugin: Setting presence from PhonePlugin");

		} else {
			SparkManager.getSessionManager().changePresence(latestPresence);
			Log.debug("UserIdlePlugin: Setting presence using latestPresence");
		}

	}

	@Override
	public void run() {
		if (pref.isIdleOn()) {

			// Windows Desktop Lock
			if (Spark.isWindows()) {

				if (getFromLockListener() && !getDesktopLockStatus()) {
						setIdle();
						hasChanged = true;
						DesktopLockStatus = true;
					} else if (!getFromLockListener() && getDesktopLockStatus()) {
						setOnline();
						hasChanged = false;
					}

					if ((getIdleTimeMillisWin32() / 1000 > (pref.getIdleTime() * 60)) && !hasChanged) {
						setIdle();
						hasChanged = true;
					} else if ((getIdleTimeMillisWin32() / 1000 < 10) && hasChanged && !getDesktopLockStatus()) {
						setOnline();
						hasChanged = false;
                    }

				}
			if (Spark.isMac()) {
				if ((getIdleTimeMillisMac() / 1000 > (pref.getIdleTime() * 60)) && !hasChanged) {
					setIdle();
					hasChanged = true;
				} else if ((getIdleTimeMillisMac() / 1000 < 10) && hasChanged) {
					setOnline();
					hasChanged = false;
				}
			}

			if (Spark.isLinux()) {
				if ((getIdleTimeMillisLinux() / 1000 > (pref.getIdleTime() * 60)) && !hasChanged) {
					setIdle();
					hasChanged = true;
				} else if ((getIdleTimeMillisLinux() / 1000 < 10) && hasChanged) {
					setOnline();
					hasChanged = false;
				}
			}
			
		}
	}


	public class LockListener {

		public void intWinLockListener() {
			new Thread(() -> {
				new WinLockListener() {
					@Override
					protected void onMachineLocked(int sessionId) {
						IsLocked = true;
					}

					@Override
					protected void onMachineUnlocked(int sessionId) {
						IsLocked = false;
					}
				};

			}).start();
		}

	}

	public class IdleTimer {

		public void intWin32IdleTime() {
			new Thread(() -> {
				new Win32IdleTime() {
					};

			}).start();
		}
		public void intMacIdleTime() {
			new Thread(() -> {
				new MacIdleTime() {
				};

			}).start();
		}
		public void intLinuxIdleTime() {
			new Thread(() -> {
				new LinuxIdleTime() {
				};

			}).start();
		}

	}

}

