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
package org.jivesoftware.sparkimpl.plugin.bookmarks;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.bookmark.BookmarkManager;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.bookmark.BookmarkedURL;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.conferences.BookmarksListener;
import org.jivesoftware.spark.ui.conferences.BookmarksUI;
import org.jivesoftware.spark.ui.conferences.ConferenceServices;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;

/**
 * Allows for adding and removal of Bookmarks within Spark.
 */
public class BookmarkPlugin implements Plugin {

    @Override
    public void initialize() {

        final SwingWorker bookmarkThreadWorker = new SwingWorker() {

            @Override
            public Object construct() {
                return this;
            }

            /**
             * Installing menu into spark menu and adding events to bookmarks
             */
            @Override
            public void finished() {
            	try {
            		initialize();
            	} catch (Exception e) {
            		Log.error(e);
            	}
            }

            /**
             *
             */
            public void initialize() {
                final JMenu bookmarkMenu = new JMenu(Res.getString("menuitem.bookmarks"));

                createMenu(bookmarkMenu);

                if (bookmarkMenu.getMenuComponentCount() > 0) {
                    int menuCount = SparkManager.getMainWindow().getMenu().getMenuCount();
                    SparkManager.getMainWindow().getMenu().add(bookmarkMenu, menuCount - 1);
                }

                BookmarksUI bookmarksUi = ConferenceServices.getBookmarkedConferences();
                bookmarksUi.addBookmarksListener(new BookmarksListener() {

                        @Override
                        public void bookmarkAdded(String roomJID) {
                            rescan(bookmarkMenu);
                        }

                        @Override
                        public void bookmarkRemoved(String roomJID) {
                            rescan(bookmarkMenu);
                        }
                    });
            }

            /**
             * Rescaning our bookmarks and remaking menu items
             *
             * @param Bookmark menu Jmenu
             */
            public void rescan(JMenu bookmarkMenu) {
                bookmarkMenu.removeAll(); // removing old menus
                try {
                    setBookmarks(bookmarkMenu); // making new 
                    int onPanel = SparkManager.getMainWindow().getMenu().getComponentIndex(bookmarkMenu);

                    if (onPanel < 0) {
                        if (bookmarkMenu.getMenuComponentCount() > 0) {
                            int menuCount = SparkManager.getMainWindow().getMenu().getMenuCount();
                            SparkManager.getMainWindow().getMenu().add(bookmarkMenu, menuCount - 2);
                        }
                    }

                    if (onPanel >= 0) {
                        if (bookmarkMenu.getMenuComponentCount() <= 0) {
                            SparkManager.getMainWindow().getMenu().remove(bookmarkMenu);
                        }
                    }
                    SparkManager.getMainWindow().getMenu().invalidate();
                    SparkManager.getMainWindow().getMenu().validate();
                    SparkManager.getMainWindow().getMenu().repaint();
                } catch (XMPPException ex) {
                    Log.error(ex);
                }
            }

            /**
             * Updating statusbar and generating menu items
             *
             * @param Bookmark menu Jmenu
             */
            public void createMenu(JMenu bookmarkMenu) {
                try {
                    setBookmarks(bookmarkMenu);
                } catch (XMPPException ex) {
                    Log.error(ex);
                }

            }

            /**
             * loading menu items and setting bookmarks listeners
             *
             * @param Bookmark menu Jmenu
             */
            public void setBookmarks(JMenu bookmarkMenu) throws XMPPException {
                BookmarkManager manager = BookmarkManager.getBookmarkManager(SparkManager.getConnection());

                if (manager != null) {

                    Collection<BookmarkedConference> bookmarkedConferences = manager.getBookmarkedConferences();
                    final Collection<BookmarkedURL> bookmarkedLinks = manager.getBookmarkedURLs();

                    for (Object bookmarkedLink : bookmarkedLinks) {
                        final BookmarkedURL link = (BookmarkedURL) bookmarkedLink;

                        Action urlAction = new AbstractAction() {

                            private static final long serialVersionUID = 4246574779205966917L;

                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                try {
                                    BrowserLauncher.openURL(link.getURL());
                                } catch (Exception e) {
                                    Log.error(e);
                                }
                            }
                        };

                        urlAction.putValue(Action.NAME, link.getName());
                        urlAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.LINK_16x16));
                        bookmarkMenu.add(urlAction);
                    }


                    for (Object bookmarkedConference : bookmarkedConferences) {
                        final BookmarkedConference conferences = (BookmarkedConference) bookmarkedConference;

                        Action conferenceAction = new AbstractAction() {

                            private static final long serialVersionUID = 5964584172262968704L;

                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                final TimerTask task = new SwingTimerTask() {

                                    @Override
                                    public void doRun() {
                                        ConferenceUtils.joinConferenceOnSeperateThread(conferences.getName(), conferences.getJid(), conferences.getPassword());
                                    }
                                };

                                TaskEngine.getInstance().schedule(task, 10);
                            }
                        };

                        conferenceAction.putValue(Action.NAME, conferences.getName());
                        conferenceAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
                        bookmarkMenu.add(conferenceAction);
                    }
                }
            }
        };

        bookmarkThreadWorker.start();

    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public void uninstall() {
    }
}
