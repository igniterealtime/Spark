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
package com.jivesoftware.spark.plugin.apple;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.jivesoftware.spark.preference.Preference;

/**
 * Apple Preference<br>
 * Apple, the Apple logo, Mac, and Macintosh are trademarks of Apple Inc
 * 
 * @author wolf.posdorfer
 * 
 */
public class ApplePreference implements Preference {

    private AppleProperties _props;
    private ApplePreferencePanel _prefpanel;

    public ApplePreference(AppleProperties props) {
	_props = props;
	_prefpanel = new ApplePreferencePanel();
    }

    @Override
    public void commit() {
	_props.setBoolean(AppleProperties.DOCKBADGE, _prefpanel.getdockbadges());
	_props.setBoolean(AppleProperties.DOCKBOUNCE, _prefpanel.getdockbounce());
	_props.setBoolean(AppleProperties.REPEATDOCKBOUNCE, _prefpanel.getrepeatbouncing());
	_props.save();
    }

    @Override
    public Object getData() {
	return _prefpanel;
    }

    @Override
    public String getErrorMessage() {
	return "error with apple plugin";
    }

    @Override
    public JComponent getGUI() {
	return _prefpanel;
    }

    @Override
    public Icon getIcon() {
	ClassLoader cl = getClass().getClassLoader();
	ImageIcon icon = new ImageIcon(cl.getResource("images/X.png"));
	Image x = icon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_FAST);
	icon = new ImageIcon(x);
	return icon;
    }

    @Override
    public String getListName() {
	return "OSX";
    }

    @Override
    public String getNamespace() {
	return "OSX";
    }

    @Override
    public String getTitle() {
	return "OSX";
    }

    @Override
    public String getTooltip() {
	return "Apple Plugin Features";
    }

    @Override
    public boolean isDataValid() {
	return true;
    }

    @Override
    public void load() {
	_prefpanel.setdockbadges(_props.getDockBadges());
	_prefpanel.setdockbounce(_props.getDockBounce());
	_prefpanel.setrepeatbouncing(_props.getRepeatBounce());
    }

    @Override
    public void shutdown() {
    }

}
