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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.bookmark.BookmarkedURL;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

/**
 *
 */
public class BookmarkItem extends JPanel {

	private static final long serialVersionUID = -3120765894005887305L;
	private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel descriptionLabel;

    public Action action;

    public BookmarkItem() {
        setLayout(new GridBagLayout());

        imageLabel = new JLabel();
        nameLabel = new JLabel();
        descriptionLabel = new JLabel();


        descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        descriptionLabel.setForeground((Color)UIManager.get("ContactItemDescription.foreground"));
        descriptionLabel.setHorizontalTextPosition(JLabel.LEFT);
        descriptionLabel.setHorizontalAlignment(JLabel.LEFT);


        this.setOpaque(true);

        add(imageLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
        add(nameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        add(descriptionLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 0), 0, 0));
    }

    public void addURL(final BookmarkedURL bookmark) {
        imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.LINK_16x16));
        nameLabel.setText(bookmark.getName());
        descriptionLabel.setText(bookmark.getURL());

        action = new AbstractAction() {
			private static final long serialVersionUID = 6986851628853679682L;

			public void actionPerformed(ActionEvent e) {
                try {
                    BrowserLauncher.openURL(bookmark.getURL());
                }
                catch (Exception e1) {
                    Log.error(e1);
                }
            }
        };
    }

    public void addConferenceRoom(final BookmarkedConference bookmark) {
        imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
        nameLabel.setText(bookmark.getName());
        descriptionLabel.setText(bookmark.getJid());
        action = new AbstractAction() {
			private static final long serialVersionUID = 4324785627112595384L;

			public void actionPerformed(ActionEvent e) {
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
                        ConferenceUtils.joinConferenceOnSeperateThread(bookmark.getName(), bookmark.getJid(), bookmark.getPassword());
                    }
                };
                worker.start();
            }
        };
    }

    public void invokeAction() {
        action.actionPerformed(null);
    }

}
