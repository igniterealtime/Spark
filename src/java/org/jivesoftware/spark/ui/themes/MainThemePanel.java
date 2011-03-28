/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.jivesoftware.spark.ui.themes;

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
	    tabs.addTab(Res.getString("lookandfeel.color.label"),SparkRes.getImageIcon(SparkRes.COLOR_ICON),_colorpanel);
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
