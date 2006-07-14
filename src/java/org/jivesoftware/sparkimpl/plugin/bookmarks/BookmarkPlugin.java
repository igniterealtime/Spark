/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.bookmarks;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.bookmark.BookmarkedURL;
import org.jivesoftware.smackx.bookmark.Bookmarks;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import java.util.Collection;
import java.util.Iterator;

/**
 * Allows for adding and removal of Bookmarks within Spark.
 */
public class BookmarkPlugin implements Plugin {

    public void initialize() {
        final SwingWorker bookmarkThreadWorker = new SwingWorker() {
            public Object construct() {
                // Register own provider for simpler implementation.
                PrivateDataManager.addPrivateDataProvider("storage", "storage:bookmarks", new Bookmarks.Provider());
                PrivateDataManager manager = new PrivateDataManager(SparkManager.getConnection());
                Bookmarks bookmarks = null;
                try {
                    bookmarks = (Bookmarks)manager.getPrivateData("storage", "storage:bookmarks");
                }
                catch (XMPPException e) {
                    Log.error(e);
                }
                return bookmarks;

            }

            public void finished() {
                final Bookmarks bookmarks = (Bookmarks)get();
                if (bookmarks != null) {
                    Collection bookmarkedConferences = bookmarks.getBookmarkedConferences();
                    final Collection bookmarkedLinks = bookmarks.getBookmarkedURLS();

                    BookmarkUI bookmarkUI = new BookmarkUI();

                    Iterator links = bookmarkedLinks.iterator();
                    while (links.hasNext()) {
                        final BookmarkedURL bookmarkedLink = (BookmarkedURL)links.next();
                        bookmarkUI.addURL(bookmarkedLink);
                    }

                    ContactList contactList = SparkManager.getWorkspace().getContactList();
                    contactList.getMainPanel().add(bookmarkUI);

                    Iterator conferences = bookmarkedConferences.iterator();
                    while (conferences.hasNext()) {
                        final BookmarkedConference bookmarkedConference = (BookmarkedConference)conferences.next();
                        bookmarkUI.addConference(bookmarkedConference);
                    }
                }
            }
        };

        bookmarkThreadWorker.start();

    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
    }
}
