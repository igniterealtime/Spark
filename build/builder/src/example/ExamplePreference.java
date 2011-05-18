package example;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jivesoftware.spark.preference.Preference;

public class ExamplePreference implements Preference{

    @Override
    public String getTitle() {
	return "Example";
    }

    @Override
    public Icon getIcon() {
	return null;
    }

    @Override
    public String getTooltip() {
	return "tooltip of my preference";
    }

    @Override
    public String getListName() {
	return "Example";
    }

    @Override
    public String getNamespace() {
	return "EXAMPLE";
    }

    @Override
    public JComponent getGUI() {
	
	JPanel panel = new JPanel();
	panel.add(new JButton("Welcome to my Preferences"));
	return panel;
	// you would need to add your own JComponent class here
    }

    @Override
    public void load() {
	//initizialize the gui maybe
	// or load saved preferences
	
    }

    @Override
    public void commit() {
	// save changes in the preference gui
    }

    @Override
    public boolean isDataValid() {
	return false;
    }

    @Override
    public String getErrorMessage() {
	return "EEERROOOOORRR";
    }

    @Override
    public Object getData() {
	return null;
    }

    @Override
    public void shutdown() {
	// do something
    }

}
