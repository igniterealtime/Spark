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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.bookmark.BookmarkedURL;
import org.jivesoftware.smackx.bookmark.Bookmarks;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
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

                final JPopupMenu popup = new JPopupMenu();

                if (bookmarks != null) {
                    // Add to status bar
                    final JPanel commandPanel = SparkManager.getWorkspace().getStatusBar().getCommandPanel();
                    final RolloverButton bookmarkButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON));
                    bookmarkButton.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent mouseEvent) {
                            popup.show(bookmarkButton, mouseEvent.getX(), mouseEvent.getY());
                        }
                    });

                    bookmarkButton.setToolTipText(Res.getString("title.view.bookmarks"));
                    commandPanel.add(bookmarkButton);
                    SparkManager.getWorkspace().getStatusBar().invalidate();
                    SparkManager.getWorkspace().getStatusBar().validate();
                    SparkManager.getWorkspace().getStatusBar().repaint();


                    Collection bookmarkedConferences = bookmarks.getBookmarkedConferences();
                    final Collection bookmarkedLinks = bookmarks.getBookmarkedURLS();

                    final Iterator bookmarkLinks = bookmarkedLinks.iterator();
                    while (bookmarkLinks.hasNext()) {
                        final BookmarkedURL link = (BookmarkedURL)bookmarkLinks.next();

                        Action urlAction = new AbstractAction() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                try {
                                    BrowserLauncher.openURL(link.getURL());
                                }
                                catch (IOException e) {
                                    Log.error(e);
                                }
                            }
                        };

                        urlAction.putValue(Action.NAME, link.getName());
                        urlAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.LINK_16x16));
                        popup.add(urlAction);
                    }


                    final Iterator bookmarkConferences = bookmarkedConferences.iterator();
                    while (bookmarkConferences.hasNext()) {
                        final BookmarkedConference conferences = (BookmarkedConference)bookmarkConferences.next();

                        Action conferenceAction = new AbstractAction() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                SwingWorker worker = new SwingWorker() {
                                    public Object construct() {
                                        try {
                                            Thread.sleep(10);
                                        }
                                        catch (InterruptedException e1) {
                                            Log.error(e1);
                                        }
                                        return "ok";
                                    }

                                    public void finished() {
                                        ConferenceUtils.autoJoinConferenceRoom(conferences.getName(), conferences.getJid(), conferences.getPassword());
                                    }
                                };
                                worker.start();
                            }
                        };

                        conferenceAction.putValue(Action.NAME, conferences.getName());
                        conferenceAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
                        popup.add(conferenceAction);
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
