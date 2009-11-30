/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import javax.swing.JComponent;

import java.util.HashMap;
import java.util.Map;

public class SparklerDecorator {

    private Map<String,String> urls = new HashMap<String,String>();
    private Map<String,JComponent> popups = new HashMap<String,JComponent>();


    public void setURL(String matchedText, String url) {
        urls.put(matchedText, url);
    }

    public void setPopup(String matchedText, JComponent gui) {
        popups.put(matchedText, gui);
    }

    public Map<String, String> getURLS() {
        return urls;
    }

    public Map<String, JComponent> getPopups() {
        return popups;
    }

}
