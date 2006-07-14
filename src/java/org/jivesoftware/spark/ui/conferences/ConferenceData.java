/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.smackx.packet.PrivateData;
import org.jivesoftware.smackx.provider.PrivateDataProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConferenceData implements PrivateData {

    private final Map serviceMap = new HashMap();

    public static final String ELEMENT = "conference-data";
    public static final String NAMESPACE = "http://www.jivesoftware.com/communicator";

    public void addService(String service) {
        serviceMap.put(service, "");
    }

    public void addBookmark(Bookmark bookmark) {
        List bookmarks = (List)serviceMap.get(bookmark.getServiceName());
        if (bookmarks == null) {
            bookmarks = new ArrayList();
        }

        bookmarks.add(bookmark);
        serviceMap.put(bookmark.getServiceName(), bookmarks);
    }

    public Collection getBookmarks() {
        List list = new ArrayList();

        Iterator iter = serviceMap.values().iterator();
        while (iter.hasNext()) {
            List l = (List)iter.next();
            Iterator bookmarks = l.iterator();
            while (bookmarks.hasNext()) {
                Bookmark bookmark = (Bookmark)bookmarks.next();
                list.add(bookmark);
            }
        }
        return list;
    }


    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String toXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        Iterator iter = serviceMap.keySet().iterator();
        buf.append("<services>");

        while (iter.hasNext()) {
            String serviceName = (String)iter.next();
            List bookmarks = (List)serviceMap.get(serviceName);

            if (bookmarks != null) {
                Iterator rooms = bookmarks.iterator();
                while (rooms.hasNext()) {
                    Bookmark bookmark = (Bookmark)rooms.next();
                    buf.append("<service>");
                    buf.append("<name>").append(serviceName).append("</name>");
                    buf.append("<roomJID>").append(bookmark.getRoomJID()).append("</roomJID>");
                    buf.append("<roomName>").append(bookmark.getRoomName()).append("</roomName>");
                    buf.append("</service>");
                }
            }
            else {
                buf.append("<service><name>").append(serviceName).append("</name></service>");
            }


        }
        buf.append("</services>");


        buf.append("</").append(getElementName()).append(">");
        return buf.toString();
    }

    public static class ConferencePrivateDataProvider implements PrivateDataProvider {

        public ConferencePrivateDataProvider() {
        }

        public PrivateData parsePrivateData(XmlPullParser parser) throws Exception {
            ConferenceData conference = new ConferenceData();

            boolean done = false;

            boolean isInstalled = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("services")) {
                    isInstalled = true;
                }

                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("service")) {
                    Bookmark bookmark = getBookmark(parser);
                    conference.addBookmark(bookmark);
                }

                else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("services")) {
                    done = true;
                }
                else if (!isInstalled) {
                    done = true;
                }
            }
            return conference;
        }
    }

    private static Bookmark getBookmark(XmlPullParser parser) throws Exception {
        final Bookmark bookmark = new Bookmark();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("name")) {
                bookmark.setServiceName(parser.nextText());
            }
            else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("roomJID")) {
                bookmark.setRoomJID(parser.nextText());
            }
            else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("roomName")) {
                bookmark.setRoomName(parser.nextText());
            }

            else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("service")) {
                done = true;
            }
        }
        return bookmark;
    }
}
