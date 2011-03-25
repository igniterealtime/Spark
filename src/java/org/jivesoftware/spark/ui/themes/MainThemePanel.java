package org.jivesoftware.spark.ui.themes;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.VerticalFlowLayout;

public class MainThemePanel extends JPanel {
    
    /**
     * 
     */
    private static final long serialVersionUID = 6014253744953992190L;
    
    private ThemePanel _themepanel;
    private ColorPreferencePanel _colorpanel;
    
    public MainThemePanel()
    {

	setLayout(new VerticalFlowLayout());
	_themepanel = new ThemePanel();
	_colorpanel = new ColorPreferencePanel();
	
	JTabbedPane tabs = new JTabbedPane();
	
	tabs.addTab(Res.getString("title.appearance.preferences"),SparkRes.getImageIcon(SparkRes.PALETTE_24x24_IMAGE), _themepanel);	
	if(!Default.getBoolean("CHANGE_COLORS_DISABLED")){
	    tabs.addTab("Color",SparkRes.getImageIcon(SparkRes.COLOR_ICON),_colorpanel);
	}
       
	add(tabs);
	
    }
    
 
    public ThemePanel getThemePanel()
    {
	return _themepanel;
    }
    
    public ColorPreferencePanel getColorPanel()
    {
	return _colorpanel;
    }

}
