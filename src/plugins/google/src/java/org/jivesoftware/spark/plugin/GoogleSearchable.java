/**
 * $RCSfile: ,v $
 * $Revision: 1.0 $
 * $Date: 2005/05/25 04:20:03 $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software. Use is
 subject to license terms.
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
        Collection list = search.searchDocuments(query);

        GoogleFileViewer viewer = new GoogleFileViewer();
        viewer.viewFiles(list, true);
    }
}
