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

import org.jivesoftware.spark.component.browser.BrowserFactory;
import org.jivesoftware.spark.component.browser.BrowserViewer;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.log.Log;
import org.w3c.dom.Element;

import java.awt.BorderLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * The GoogleSearchResult is one instance of a found item from a Google Search.
 *
 * @author Derek DeMoro
 */
public class GoogleSearchResult {
    private String searchBase;
    private String query;
    private int relevance;
    String id;
    String title;
    String url;
    String time;
    String snippet;
    String icon;
    String cacheUrl;
    String from;

    public GoogleSearchResult(String searchBase, String query, int relevance, Element element) {
        this.searchBase = searchBase;
        this.query = query;
        this.relevance = relevance;
        this.id = getContent("id", element);
        this.title = getContent("title", element);
        this.url = getContent("url", element);
        this.time = getContent("time", element);
        this.snippet = getContent("snippet", element);
        this.icon = getContent("icon", element);
        this.cacheUrl = getContent("cache_url", element);
        this.from = getContent("from", element);
    }

    private String getContent(String field, Element element) {
        try {
            return (element.getElementsByTagName(field).item(0)).getChildNodes().item(0).getNodeValue();
        }
        catch (Exception e) {
            return null;
        }
    }

    public String getCachedURL() {
        return cacheUrl;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public String getUniqueID() {
        return id;
    }

    public String getSubject() {
        title = StringUtils.stripTags(title);

        return title.replaceAll("<[^>]+>", "");
    }

    public String getAuthor() {
        return from != null ? from : "unknown";
    }

    public String getToolTip() {
        return snippet;
    }

    /**
     * Return the relevance of this document pertaining to the query.
     *
     * @return the relevance of this document.
     */
    public int getRelevance() {
        return relevance;
    }

    public Date getPostedDate() {
        return new Date(Long.parseLong(time));
    }

    /**
     * Return the icon to be used to identify the type of search result.
     *
     * @return the icon to be use.
     */
    public Icon getIcon() {
        try {
            return new ImageIcon(new URL(searchBase + icon));
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Return the component to use to display this document.
     *
     * @return the component to use to display this document.
     */
    public JComponent getDocumentViewer() {
        BrowserViewer viewer = BrowserFactory.getBrowser();
        File file = new File(url);
        if (file.exists()) {
            try {
                viewer.loadURL(cacheUrl);
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
        else {
            try {
                viewer.loadURL(url);
            }
            catch (Exception e) {
                try {
                    viewer.loadURL(cacheUrl);
                }
                catch (Exception e1) {
                    Log.error(e1);
                }
            }
        }

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(viewer, BorderLayout.CENTER);

        return p;
    }

    public String getURL() {
        return url;
    }

    /**
     * Return the values to populate the table with. Please see
     * GoogleSearchPlugin#getFieldHeaders() for the type of data to return.
     *
     * @return the values to populate the table with.
     */
    public List<String> getFieldValues() {
        final List<String> returnList = new ArrayList<>();
        returnList.add(StringUtils.stripTags(snippet));
        returnList.add(getSubject());
        return returnList;
    }

    /**
     * Value returned if item is dragged.
     *
     * @return the value returned if this item is dragged.
     */
    public String getDraggableValue() {
        return "";
    }

    /**
     * Returns the date when this object was created.
     *
     * @return the date when this object was created.
     */
    public Date getCreationDate() {
        return null;
    }

    /**
     * Returns a summary of the search result item. This will be displayed in the "All" tab of a search result.
     *
     * @return the summary of the search result.
     */
    public String getSummary() {
        return getSubject();
    }

}
