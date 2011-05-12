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
package org.jivesoftware.spark.plugin;


import org.jivesoftware.spark.search.Searchable;

import java.net.URL;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class GoogleSearchable implements Searchable {

    public Icon getIcon() {
        ClassLoader cl = getClass().getClassLoader();
        URL url = cl.getResource("images/google.gif");
        return new ImageIcon(url);
    }

    public String getName() {
        return "Google";
    }

    public String getDefaultText() {
        return "Find Documents on your computer.";
    }

    public String getToolTip() {
        return "Search for documents on your computer using Google Desktop.";
    }

    public void search(String query) {
        GoogleSearch search = new GoogleSearch();
        Collection<GoogleSearchResult> list = search.searchDocuments(query);

        GoogleFileViewer viewer = new GoogleFileViewer();
        viewer.viewFiles(list, true);
    }
}
