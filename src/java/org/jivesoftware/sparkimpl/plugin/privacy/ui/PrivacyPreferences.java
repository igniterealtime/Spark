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
package org.jivesoftware.sparkimpl.plugin.privacy.ui;

import javax.swing.Icon;

import javax.swing.JComponent;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;

/**
 * @author Bergunde Holger
 */
public class PrivacyPreferences implements Preference {

    String _title = Res.getString("privacy.label.preferences");
    String _toolTip = Res.getString("pricacy.tooltip.preferences");

    public PrivacyPreferences() {
    }

    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    public Icon getIcon() {

        return SparkRes.getImageIcon("PRIVACY_ICON");
    }

    @Override
    public String getTooltip() {
        return _toolTip;
    }

    @Override
    public String getListName() {
        return _title;
    }

    @Override
    public String getNamespace() {
        return "jabber:iq:privacy";
    }

    @Override
    public JComponent getGUI() {
        return new PrivacyListTree();
    }

    @Override
    public void load() {
    }

    @Override
    public void commit() {
    }

    @Override
    public boolean isDataValid() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "error in privacy plugin?";
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void shutdown() {
    }
}
