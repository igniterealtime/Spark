package org.jivesoftware.sparkimpl.plugin.idle;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

public class UserIdlePlugin extends TimerTask implements Plugin {

	private final int CHECKTIME = 5;
	private double x = 0;
	private double y = 0;
	private boolean hasChanged = false;
	private int counter = 0;
	private LocalPreferences pref = SettingsManager.getLocalPreferences();
	
	@Override
	public boolean canShutDown() {

		return false;
	}

	@Override
	public void initialize() {
		 Timer timer = new Timer();
		 // Check all 5 secounds
		 timer.schedule  ( this, (1000 * 10), (1000 * CHECKTIME)  );
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public void uninstall() {
		
	}
	
	private void setIdle()
	{
		Presence presence = new Presence(Presence.Type.available, Res.getString("status.away"), 1, Presence.Mode.away);			
		SparkManager.getSessionManager().changePresence(presence);
	}
	
	private void setOnline()
	{
		Presence presence = new Presence(Presence.Type.available, Res.getString("status.online"), 1, Presence.Mode.available);
		SparkManager.getSessionManager().changePresence(presence);
	}
	
	@Override
	public void run() {
		if(pref.isIdleOn()){
			PointerInfo info = MouseInfo.getPointerInfo();
			//DecimalFormat format = new DecimalFormat("0.00");
			//System.out.println(format.format(info.getLocation().getY()).toString() + "-" + 7.24288464E8 + "-" + (info.getLocation().getY() == 7.24288464E8));
			//System.out.println(format.format(info.getLocation().getX()).toString() + "-" + 7.24288464E8 + "-" + (info.getLocation().getX() == 7.24288464E8));
			int automaticIdleTime = (pref.getIdleTime() * 60) / CHECKTIME;
	
			// Windows Desktop Lock
			if (Spark.isWindows()) {
				
				
				if (info != null)
				{
					if (info.getLocation().getX() > 50000000 || 
					    info.getLocation().getY() > 50000000) {
						if (!hasChanged) {
							System.out.println("Desktop Locked .. ");
							hasChanged = true;
							setIdle();
							y = info.getLocation().getY();
							x = info.getLocation().getX();
						}
					}
				}
				else
				{
					if (!hasChanged) {
						System.out.println("Desktop Locked .. ");
						hasChanged = true;
						setIdle();
						y = -1;
						x = -1;
					}				
				}
			}
		
			// Default Idle
			if (info != null)
			{
				if (x == info.getLocation().getX() && 
					y == info.getLocation().getY()) {
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
}
