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
package org.jivesoftware.spark.ui;

import javax.swing.JComponent;

import java.util.HashMap;
import java.util.Map;

public class SparklerDecorator {

    private Map<String,String> urls = new HashMap<>();
    private Map<String,JComponent> popups = new HashMap<>();


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
