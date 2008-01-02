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

import com.jniwrapper.win32.registry.RegistryKey;
import com.jniwrapper.win32.registry.RegistryKeyValues;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GoogleSearch {
    final ImageIcon icon;
    private DocumentBuilder db;

    String searchUrl;
    String searchBase;

    public GoogleSearch() {
        // Initialize icon to use.
        icon = SparkRes.getImageIcon(SparkRes.SEARCH_IMAGE_32x32);

        // Google Desktop API to search
        try {
            boolean exists = RegistryKey.CURRENT_USER.exists("Software\\Google\\Google Desktop\\API");
            if (!exists) {
                return;
            }
            RegistryKeyValues values = RegistryKey.CURRENT_USER.openSubKey("Software").openSubKey("Google").openSubKey("Google Desktop").openSubKey("API").values();
            for (Iterator iterator = values.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry)iterator.next();
                String key = (String)entry.getKey();
                if ("search_url".equals(key)) {
                    searchUrl = (String)entry.getValue();
                }
            }
            searchBase = searchUrl.substring(0, searchUrl.indexOf('/', 8));

            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            // Nothing to do
        }
    }

    /**
     * Return the name of the title to use on the Search Tab.
     *
     * @return the name of the title to use on the Search Tab.
     */
    public String getTabTitle() {
        return "Google Desktop";
    }

    /**
     * Return the icon to use on the Search Tab.
     *
     * @return the icon to use.
     */
    public Icon getTabIcon() {
        return icon;
    }

    /**
     * Executed when a search has been started.
     *
     * @param query the query to search on.
     * @param showFiles Show the files
     */
    public void search(String query, boolean showFiles) {
        final List<GoogleSearchResult> list = new ArrayList<GoogleSearchResult>();

        if (query == null || "".equals(query)) {
            return;
        }

        Document doc = null;
        try {
            URL url = new URL(searchUrl + URLEncoder.encode(query, "UTF-8") + "&format=xml&num=50");
            String content = URLFileSystem.getContents(url);
            doc = db.parse(new ByteArrayInputStream(content.getBytes()));
        }
        catch (IOException e) {
            Log.error(e);
        }
        catch (SAXException e) {
            Log.error(e);
        }

        if (doc == null) {
            return;
        }

        try {
            Element e = doc.getDocumentElement();
            int count = Integer.parseInt(e.getAttribute("count"));
            NodeList elems = e.getElementsByTagName("result");
            for (int i = 0; i < elems.getLength(); i++) {
                int relevance = (int)((double)(count - i) / count * 100);
                GoogleSearchResult result = new GoogleSearchResult(searchBase, query, relevance, (Element)elems.item(i));
                list.add(result);
            }
        }
        catch (Exception e1) {
            Log.error(e1);
        }

        if (list.size() > 0) {
            new GoogleFileViewer().viewFiles(list, showFiles);
        }


    }

    /**
     * Executed when a search has been started.
     *
     * @param query the query to search on.
     * @param maxDocuments Max documents to return
     * @return List containing search resilts.
     */
    public List<GoogleSearchResult> searchText(String query, int maxDocuments) {
        final List<GoogleSearchResult> list = new ArrayList<GoogleSearchResult>();

        if (query == null || "".equals(query)) {
            return null;
        }

        Document doc = null;
        try {
            URL url = new URL(searchUrl + URLEncoder.encode(query, "UTF-8") + "&format=xml&num=50");
            String content = URLFileSystem.getContents(url);
            doc = db.parse(new ByteArrayInputStream(content.getBytes()));
        }
        catch (IOException e) {
            Log.error(e);
        }
        catch (SAXException e) {
            Log.error(e);
        }

        try {
            Element e = doc.getDocumentElement();
            int count = Integer.parseInt(e.getAttribute("count"));
            NodeList elems = e.getElementsByTagName("result");
            for (int i = 0; i < elems.getLength(); i++) {
                int relevance = (int)((double)(count - i) / count * 100);
                GoogleSearchResult result = new GoogleSearchResult(searchBase, query, relevance, (Element)elems.item(i));
                if (result.getURL().indexOf("googlemail") == -1) {
                    list.add(result);
                }
                if (list.size() == maxDocuments) {
                    break;
                }
            }
        }
        catch (Exception e1) {
            Log.error(e1);
        }

        return list;
    }

    /**
     * Executed when a search has been started.
     *
     * @param query the query to search on.
     */
    public void searchConversations(String query) {
        final List<GoogleSearchResult> list = new ArrayList<GoogleSearchResult>();

        if (query == null || "".equals(query)) {
            return;
        }

        Document doc = null;
        try {
            URL url = new URL(searchUrl + URLEncoder.encode(query, "UTF-8") + "&format=xml&num=50");
            String content = URLFileSystem.getContents(url);
            doc = db.parse(new ByteArrayInputStream(content.getBytes()));
        }
        catch (IOException e) {
            Log.error(e);
        }
        catch (SAXException e) {
            Log.error(e);
        }

        if (doc == null) {
            return;
        }

        try {
            Element e = doc.getDocumentElement();
            int count = Integer.parseInt(e.getAttribute("count"));
            NodeList elems = e.getElementsByTagName("result");
            for (int i = 0; i < elems.getLength(); i++) {
                int relevance = (int)((double)(count - i) / count * 100);
                GoogleSearchResult result = new GoogleSearchResult(searchBase, query, relevance, (Element)elems.item(i));
                list.add(result);
            }
        }
        catch (Exception e1) {
            Log.error(e1);
        }

        for (GoogleSearchResult result : list) {
            String url = result.getURL();
            System.out.println(url);
        }


    }

    /**
     * Executed when a search has been started.
     *
     * @param query the query to search on.
     * @return Collection of search documents retreived.
     */
    public Collection<GoogleSearchResult> searchDocuments(String query) {
        final Set<GoogleSearchResult> set = new HashSet<GoogleSearchResult>();

        if (query == null || "".equals(query)) {
            return null;
        }

        Document doc = null;
        try {
            URL url = new URL(searchUrl + URLEncoder.encode(query, "UTF-8") + "&format=xml&num=50");
            String content = URLFileSystem.getContents(url);
            doc = db.parse(new ByteArrayInputStream(content.getBytes()));
        }
        catch (IOException e) {
            Log.error(e);
        }
        catch (SAXException e) {
            Log.error(e);
        }

        if (doc == null) {
            return null;
        }

        try {
            Element e = doc.getDocumentElement();
            int count = Integer.parseInt(e.getAttribute("count"));
            NodeList elems = e.getElementsByTagName("result");
            for (int i = 0; i < elems.getLength(); i++) {
                int relevance = (int)((double)(count - i) / count * 100);
                GoogleSearchResult result = new GoogleSearchResult(searchBase, query, relevance, (Element)elems.item(i));
                String url = result.getURL();
                File file = new File(url);
                if (file.exists() && !file.getName().endsWith(".class")) {
                    boolean exists = false;
                    for (GoogleSearchResult r : set) {
                        if (r.getSubject().equals(result.getSubject())) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        set.add(result);
                    }
                }
            }
        }
        catch (Exception e1) {
            Log.error(e1);
        }


        return set;

    }


    /**
     * Return true if you wish this to be searched by the Chat Analyzer.
     *
     * @return true if you wish this to be searched by the Chat Analyzer.
     */
    public boolean isUsedForChatAnalysis() {
        return false;
    }

    /**
     * Return the title headers for the result table. Please note that the <code>SearchResult</code>
     * should return the values in the exact order as this method defines.
     *
     * @return the field headers to use.
     */
    public String[] getFieldHeaders() {
        return new String[]{"Title", "Subject"};
    }
}
