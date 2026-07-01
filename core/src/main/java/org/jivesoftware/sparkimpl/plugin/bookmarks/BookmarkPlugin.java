/**
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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.bookmarks.BookmarkedURL;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.conferences.BookmarksListener;
import org.jivesoftware.spark.ui.conferences.BookmarksUI;
import org.jivesoftware.spark.ui.conferences.ConferenceServices;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.stream.Collectors.toList;

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
             */
            public void initialize() {
                final JMenu bookmarkMenu = new JMenu(Res.getString("menuitem.bookmarks"));
                createMenu(bookmarkMenu);
                if (bookmarkMenu.getMenuComponentCount() > 0) {
                    SparkManager.getMainWindow().getMenu().add(bookmarkMenu, 3);
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
             * Rescanning our bookmarks and remaking menu items
             *
             * @param bookmarkMenu menu Jmenu
             */
            public void rescan(JMenu bookmarkMenu) {
                bookmarkMenu.removeAll(); // removing old menus
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
            }

            /**
             * Updating statusbar and generating menu items
             */
            public void createMenu(JMenu bookmarkMenu) {
                setBookmarks(bookmarkMenu);
            }

            /**
             * loading menu items and setting bookmarks listeners
             */
            public void setBookmarks(JMenu bookmarkMenu) {
                BookmarkManager manager = BookmarkManager.getBookmarkManager(SparkManager.getConnection());
                if (manager == null) {
                    return;
                }
                try {
                    List<BookmarkedConference> bookmarkedConferences = manager.getBookmarkedConferences().stream()
                        .sorted(comparing(BookmarkedConference::getName, nullsFirst(naturalOrder())))
                        .collect(toList());
                    List<BookmarkedURL> bookmarkedLinks = manager.getBookmarkedURLs().stream()
                        .sorted(comparing(BookmarkedURL::getName, nullsFirst(naturalOrder())))
                        .collect(toList());

                    for (BookmarkedURL bookmarkedLink : bookmarkedLinks) {
                        String bookmarkedLinkURL = bookmarkedLink.getURL();
                        Action urlAction = new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                BrowserLauncher.openURL(bookmarkedLinkURL);
                            }
                        };
                        urlAction.putValue(Action.NAME, bookmarkedLink.getName());
                        urlAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.Icon.LINK_16x16));
                        bookmarkMenu.add(urlAction);
                    }
                    bookmarkMenu.addSeparator();
                    for (BookmarkedConference bookmarkedConference : bookmarkedConferences) {
                        Action conferenceAction = new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                ConferenceUtils.joinConferenceOnSeparateThread(bookmarkedConference.getName(), bookmarkedConference.getJid(), bookmarkedConference.getNickname(), bookmarkedConference.getPassword());
                            }
                        };
                        conferenceAction.putValue(Action.NAME, bookmarkedConference.getName() != null && !bookmarkedConference.getName().isEmpty() ? bookmarkedConference.getName() : bookmarkedConference.getJid().getLocalpart().asUnescapedString());
                        conferenceAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.Icon.CONFERENCE_IMAGE_16x16));
                        bookmarkMenu.add(conferenceAction);
                    }
                } catch (XMPPException | SmackException | InterruptedException ex) {
                    Log.error(ex);
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
