/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
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
package com.jivesoftware.spark.plugin.growl;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;

/**
 * 
 * Simple Class for GrowlPreference Panel <br>
 * currently not in use
 * 
 * @author wolf.posdorfer
 * 
 */
public class GrowlPreference implements Preference {

    JPanel _panel;
    private static final long serialVersionUID = 7485099198887907166L;

    public GrowlPreference() {

    }

    @Override
    public void commit() {

    }

    @Override
    public Object getData() {
	return "";
    }

    @Override
    public String getErrorMessage() {
	return "error with growlplugin";
    }

    @Override
    public JComponent getGUI() {
	_panel = new JPanel();
	JButton button = new JButton("Test");
	JButton button2 = new JButton("test2");

	_panel.add(button);
	_panel.add(button2);

	return _panel;
    }

    @Override
    public Icon getIcon() {
	return SparkRes.getImageIcon(SparkRes.DUMMY_CONTACT_IMAGE);
    }

    @Override
    public String getListName() {
	return "growl";
    }

    @Override
    public String getNamespace() {

	return "growler";
    }

    @Override
    public String getTitle() {

	return "GROWL";
    }

    @Override
    public String getTooltip() {

	return "tooltip";
    }

    @Override
    public boolean isDataValid() {

	return true;
    }

    @Override
    public void load() {

    }

    @Override
    public void shutdown() {

    }

}
