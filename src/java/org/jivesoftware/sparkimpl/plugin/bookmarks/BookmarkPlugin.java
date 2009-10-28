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

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
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

    public void initialize() {
        final SwingWorker bookmarkThreadWorker = new SwingWorker() {
            public Object construct() {
                return this; 
                }

            /**
             * Installing menu into spark menu and adding events to bookmarks
             */
            @Override
            public void finished() {
                
                final JMenu bookmarkMenu = new JMenu(Res.getString("menuitem.bookmarks"));

                createMenu(bookmarkMenu);

                if (bookmarkMenu.getMenuComponentCount() > 0) {
                    int menuCount = SparkManager.getMainWindow().getMenu().getMenuCount();
                    SparkManager.getMainWindow().getMenu().add(bookmarkMenu, menuCount - 1);
                }

                BookmarksUI bookmarksUi = ConferenceServices.getBookmarkedConferences();
                if ( bookmarksUi != null ) {
                    bookmarksUi.addBookmarksListener(new BookmarksListener() {

                        public void bookmarkAdded(String roomJID) {
                            rescan(bookmarkMenu);
                        }

                        public void bookmarkRemoved(String roomJID) {
                            rescan(bookmarkMenu);
                        }
                    });
                } else {
                    /**
                     * IF our plugin loaded earlier than BookmarkUI we have to reskan bookmarks every time we open this menu
                     * And BookmarksListener event wouldn't work
                     */
                    Log.error("Bookmark plugin loaded earlier then BookmarkUI. BookmarksListener Events wouldn't work!");
                    bookmarkMenu.addMenuListener(new MenuListener() {

                        public void menuSelected(MenuEvent menuEvent) {
                            rescan(bookmarkMenu);
                        }

                        public void menuDeselected(MenuEvent arg0) {
                            //ignore
                        }

                        public void menuCanceled(MenuEvent arg0) {
                            //ignore
                        }
                    });
                }
            }

            /**
             * Rescaning our bookmarks and remaking menu items
             *
             * @param Bookmark menu Jmenu
             */
            public void rescan(JMenu bookmarkMenu)
            {
                bookmarkMenu.removeAll(); // removing old menus
                try {
                    setBookmarks(bookmarkMenu); // making new 
                    int onPanel = SparkManager.getMainWindow().getMenu().getComponentIndex(bookmarkMenu);

                    if (onPanel < 0) {
                        if (bookmarkMenu.getMenuComponentCount() > 0) {
                            int menuCount = SparkManager.getMainWindow().getMenu().getMenuCount();
                            SparkManager.getMainWindow().getMenu().add(bookmarkMenu, menuCount - 1);
                        }
                    }
                    
                    if (onPanel >= 0) {
                        if (bookmarkMenu.getMenuComponentCount() <= 0) {
                            SparkManager.getMainWindow().getMenu().remove(bookmarkMenu);
                        }
                    }
                    // Refreshing menu panel
                    SparkManager.getWorkspace().getStatusBar().invalidate();
                    SparkManager.getWorkspace().getStatusBar().validate();
                    SparkManager.getWorkspace().getStatusBar().repaint();
                } catch (XMPPException ex) {
                    Log.error(ex);
                }
            }

            /**
             * Updating statusbar and generating menu items
             *
             * @param Bookmark menu Jmenu
             */
            public void createMenu(JMenu bookmarkMenu)
            {
                SparkManager.getWorkspace().getStatusBar().invalidate();
                SparkManager.getWorkspace().getStatusBar().validate();
                SparkManager.getWorkspace().getStatusBar().repaint();
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
            public void setBookmarks(JMenu bookmarkMenu ) throws XMPPException
            {
                BookmarkManager manager = BookmarkManager.getBookmarkManager(SparkManager.getConnection());

                if (manager != null) {

                    Collection<BookmarkedConference> bookmarkedConferences = manager.getBookmarkedConferences();
                    final Collection<BookmarkedURL> bookmarkedLinks = manager.getBookmarkedURLs();

                    for (Object bookmarkedLink : bookmarkedLinks) {
                        final BookmarkedURL link = (BookmarkedURL) bookmarkedLink;

                        Action urlAction = new AbstractAction() {
							private static final long serialVersionUID = 4246574779205966917L;

							public void actionPerformed(ActionEvent actionEvent) {
                                try {
                                    BrowserLauncher.openURL(link.getURL());
                                }
                                catch (Exception e) {
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

							public void actionPerformed(ActionEvent actionEvent) {
                                final TimerTask task = new SwingTimerTask() {
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

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
    }
}
