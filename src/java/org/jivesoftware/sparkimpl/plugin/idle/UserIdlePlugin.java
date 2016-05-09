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

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.phone.PhonePlugin;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class UserIdlePlugin extends TimerTask implements Plugin {

    private final int CHECKTIME = 2;
    private double x = 0;
    private double y = 0;
    private boolean hasChanged = false;
    private int counter = 0;
    public static LocalPreferences pref = SettingsManager.getLocalPreferences();
    public static Presence latestPresence;
	private KeyHook keyHook;
    private static boolean DesktopLockStatus;
	public static int IdlePresencePriority = -2;

	public static boolean getDesktopLockStatus() {

        return DesktopLockStatus;
    }
	private static boolean presencePriority() {
		if (PhonePlugin.offPhonePresence !=null) {
			int pp1 = PhonePlugin.offPhonePresence.getPriority();
			int pp2 = IdlePresencePriority;

			if (pp1 == pp2) {
				return true;
			} else {
				return false;
			}
		} else return false;
	}

    @Override
    public boolean canShutDown() {
	return false;
    }

    @Override
    public void initialize() {
	Timer timer = new Timer();
	// Check all 5 secounds
	timer.schedule(this, (1000 * 10), (1000 * CHECKTIME));

	if (Spark.isWindows()) {
	    keyHook = new KeyHook();
	    keyHook.initKeyHook();
	} else {
	    addGlobalListener();
	}
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void uninstall() {
	if (Spark.isWindows()) {
	    keyHook.quitKeyHook();
	}
    }

    private void setIdle() {

        latestPresence = SparkManager.getWorkspace().getStatusBar().getPresence();
        String statustext;
        
        if (latestPresence.getStatus().equals(Res.getString("status.online")) || latestPresence.getStatus().equals(Res.getString("status.free.to.chat"))) {
    		statustext = pref.getIdleMessage();
    	} else {
    		statustext = latestPresence.getStatus();
    	}
        
        if (latestPresence.isAway()) {
            Log.debug("UserIdlePlugin: Presence is already set to away");
        } else {
        	Presence statusPresence = new Presence(Presence.Type.available, StringUtils.modifyWildcards(statustext), IdlePresencePriority, Presence.Mode.away);
        	SparkManager.getSessionManager().changePresence(statusPresence);
            Log.debug("UserIdlePlugin: Setting idle presence");
        }
    }



    private void setOnline() {
		DesktopLockStatus = false;
		Log.debug("presencePriority returned " +presencePriority());
		if (PhonePlugin.onPhonePresence !=null) {
			SparkManager.getSessionManager().changePresence(PhonePlugin.onPhonePresence);
			Log.debug("UserIdlePlugin: Returning from idle/lock - On the Phone");

		} else if ((latestPresence.getStatus().contains("On the phone")) && ((PhonePlugin.offPhonePresence !=null)
				&& (PhonePlugin.offPhonePresence.getStatus().contentEquals(pref.getIdleMessage())))) {
			Presence presence = new Presence(Presence.Type.available, "Online", 1, Presence.Mode.available);
			SparkManager.getSessionManager().changePresence(presence);
			Log.debug("UserIdlePlugin: Setting presence to Online based on PhonePlugin last status");

		} else if ((latestPresence.getStatus().contains("On the phone")) && (PhonePlugin.offPhonePresence !=null)
				&& ((PhonePlugin.offPhonePresence.getMode().equals(Presence.Mode.dnd))
				|| (PhonePlugin.offPhonePresence.getMode().equals(Presence.Mode.xa)))) {
			SparkManager.getSessionManager().changePresence(PhonePlugin.offPhonePresence);
			Log.debug("UserIdlePlugin: Matched DND/XA - Setting presence from PhonePlugin");

		} else if (((latestPresence.getStatus().contains("On the phone")) && (PhonePlugin.offPhonePresence !=null)
				&& (presencePriority()))) {
			Presence presence = new Presence(Presence.Type.available, PhonePlugin.offPhonePresence.getStatus(), 1, Presence.Mode.available);
			SparkManager.getSessionManager().changePresence(presence);
			Log.debug("UserIdlePlugin: Setting presence from PhonePlugin based on presencePriority value");

		} else if ((latestPresence.getStatus().contains("On the phone")) && (PhonePlugin.offPhonePresence !=null)) {
				SparkManager.getSessionManager().changePresence(PhonePlugin.offPhonePresence);
				Log.debug("UserIdlePlugin: Setting presence from PhonePlugin");

		} else { SparkManager.getSessionManager().changePresence(latestPresence);
			Log.debug("UserIdlePlugin: Setting presence using latestPresence"); }

	}

	@Override
    public void run() {
	if (pref.isIdleOn()) {
	    PointerInfo info = MouseInfo.getPointerInfo();
	    // DecimalFormat format = new DecimalFormat("0.00");
	    // System.out.println(format.format(info.getLocation().getY()).toString()
	    // + "-" + 7.24288464E8 + "-" + (info.getLocation().getY() ==
	    // 7.24288464E8));
	    // System.out.println(format.format(info.getLocation().getX()).toString()
	    // + "-" + 7.24288464E8 + "-" + (info.getLocation().getX() ==
	    // 7.24288464E8));
	    int automaticIdleTime = (pref.getIdleTime() * 60) / CHECKTIME;

	    // Windows Desktop Lock
	    if (Spark.isWindows()) {

		if (info != null) {
		    if (info.getLocation().getX() > 50000000
			    || info.getLocation().getY() > 50000000) {
			if (!hasChanged) {
			    Log.debug("Desktop Locked .. ");
			    hasChanged = true;
			    setIdle();
                DesktopLockStatus = true;
			    y = info.getLocation().getY();
			    x = info.getLocation().getX();
			}
		    }
		} else {
		    if (!hasChanged) {
			Log.debug("Desktop Locked .. ");
			hasChanged = true;
			setIdle();
            DesktopLockStatus = true;
			y = -1;
			x = -1;
		    }
		}
	    }

	    // Default Idle
	    if (info != null) {
		if (x == info.getLocation().getX()
			&& y == info.getLocation().getY()) {
		    if (counter > automaticIdleTime) {
			if (!hasChanged) {
			    setIdle();
			}
			hasChanged = true;
		    }
		    counter++;
		} else {
		    if (hasChanged) {
			setOnline();
			hasChanged = false;
		    }
		    counter = 0;
		}

		y = info.getLocation().getY();
		x = info.getLocation().getX();
	    }
	}
    }

    private void addGlobalListener() {
	EventQueue e = Toolkit.getDefaultToolkit().getSystemEventQueue();
	e.push(new EventQueue() {

	    @Override
	    protected void dispatchEvent(AWTEvent event) {
		if (event instanceof KeyEvent) {
		    counter = 0;
		    if (hasChanged) {
			setOnline();
			hasChanged = false;
		    }
		}
		super.dispatchEvent(event);
	    }
	});
    }

    private void userActive() {
	counter = 0;
	if (hasChanged) {
	    setOnline();
	    hasChanged = false;
	}
    }

    /** Sample implementation of a low-level keyboard hook on W32. */
    class KeyHook {
	private HHOOK hhk;
	private LowLevelKeyboardProc keyboardHook;
	private Thread thread;

	public void initKeyHook() {
		System.setProperty( "jna.predictable_field_order","true");

	    thread = new Thread(new Runnable() {

		@Override
		public void run() {
		    final User32 lib = User32.INSTANCE;
		    HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		    keyboardHook = new LowLevelKeyboardProc() {
			public LRESULT callback(int nCode, WPARAM wParam,
				KBDLLHOOKSTRUCT info) {
			    if (nCode >= 0) {
				switch (wParam.intValue()) {
				// case WinUser.WM_KEYUP:
				case WinUser.WM_KEYDOWN:
				    // case WinUser.WM_SYSKEYUP:
				case WinUser.WM_SYSKEYDOWN:
				    // do active
				    userActive();
				}
			    }
			    return lib.CallNextHookEx(hhk, nCode, wParam,
				    info.getPointer());
			}
		    };
		    hhk = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL,
			    keyboardHook, hMod, 0);

		    // This bit never returns from GetMessage
		    int result;
		    MSG msg = new MSG();
		    while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
			if (result == -1) {
			    System.err.println("error in get message");
			    break;
			} else {
			    System.err.println("got message");
			    lib.TranslateMessage(msg);
			    lib.DispatchMessage(msg);
			}
		    }
		    lib.UnhookWindowsHookEx(hhk);
		}
	    });
	    thread.start();
	}

	@SuppressWarnings("deprecation")
	public void quitKeyHook() {
	    final User32 lib = User32.INSTANCE;
	    lib.UnhookWindowsHookEx(hhk);
	    thread.stop();
	}

    }
}
