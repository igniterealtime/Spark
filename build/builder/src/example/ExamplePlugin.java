package example;

import javax.swing.JOptionPane;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;

/**
 * Example Plugin to demonstrate the use of Plugins
 * @author wolf.posdorfer
 *
 */
public class ExamplePlugin implements Plugin{

    @Override
    public void initialize() {
	// Use this method to initialize your Plugin

	
	// The following will add an Entry into the Spark Preferences Window
	ExamplePreference mypreference = new ExamplePreference();
	SparkManager.getPreferenceManager().addPreference(mypreference);
	
	
	
	// Show a Message When my plugin is loaded
	JOptionPane.showMessageDialog(null, "Plugin has been successfully loaded");
    }

    @Override
    public void shutdown() {
	JOptionPane.showMessageDialog(null, "Plugin has been shutdown");	
    }

    @Override
    public boolean canShutDown() {
	
	return false;
    }

    @Override
    public void uninstall() {
	
	// use this method to remove stored preferences used by this plugin
	
	JOptionPane.showMessageDialog(null, "Plugin has been uninstalled");
	
    }

}
