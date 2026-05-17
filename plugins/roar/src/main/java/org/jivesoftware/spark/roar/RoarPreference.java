/**
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
package org.jivesoftware.spark.roar;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.roar.gui.RoarPreferencePanel;

/**
 * Awesome Preference
 * 
 * @author Wolf Posdorfer
 */
public class RoarPreference implements Preference {
    private RoarPreferencePanel _prefPanel;
    private final RoarProperties _props = RoarProperties.getInstance();

    @Override
    public String getTitle() {
        return "ROAR";
    }

    @Override
    public Icon getIcon() {
        ClassLoader cl = getClass().getClassLoader();
        return new ImageIcon(cl.getResource("roar.png"));
    }

    @Override
    public String getTooltip() {
        return "ROAR";
    }

    @Override
    public String getListName() {
        return "ROAR";
    }

    @Override
    public String getNamespace() {
        return "roar";
    }

    @Override
    public JComponent getGUI() {
        _prefPanel = new RoarPreferencePanel();
        _prefPanel.initializeValues();
        return _prefPanel;
    }

    @Override
    public void load() {
    }

    @Override
    public void commit() {
        _prefPanel.storeValues();
    }

    @Override
    public boolean isDataValid() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "roooooooooaaaaaar";
    }

    @Override
    public Object getData() {
        return _props;
    }
}
