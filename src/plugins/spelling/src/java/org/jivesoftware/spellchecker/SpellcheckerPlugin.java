package org.jivesoftware.spellchecker;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;

public class SpellcheckerPlugin implements Plugin
{
	private SpellcheckChatRoomListener listener;
	
	public boolean canShutDown() {
		return true;
	}

	public void initialize() {
		
		try
		{	            
            SpellcheckerPreference preference = SpellcheckManager.getInstance().getSpellcheckerPreference();
            SparkManager.getPreferenceManager().addPreference(preference);	      
			listener = new SpellcheckChatRoomListener();			
			SparkManager.getChatManager().addChatRoomListener(listener);		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void shutdown() {
	
	}

	public void uninstall() {
		SparkManager.getChatManager().removeChatRoomListener(listener);
	}

}
