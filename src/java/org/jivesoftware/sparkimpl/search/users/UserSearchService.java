/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.search.users;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.search.Searchable;
import org.jivesoftware.spark.ui.DataFormUI;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.util.Collection;

public class UserSearchService implements Searchable {
    private Collection searchServices;

    public UserSearchService() {
        // On initialization, find search service.
        loadSearchServices();
    }

    public void search(String query) {
        if (searchServices == null) {
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Unable to locate a search service.", "Search Service Not Available", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserSearchForm searchForm = null;
        DataFormUI dataFormUI = null;
        try {
            searchForm = new UserSearchForm(searchServices);
            dataFormUI = searchForm.getQuestionForm();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Unable to contact search service.", "Search Service Not Available", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField textField = (JTextField)dataFormUI.getComponent("search");
        if (textField != null) {
            textField.setText(query);
        }
        else {
            textField = (JTextField)dataFormUI.getComponent("last");
            if (textField != null) {
                textField.setText(query);
            }
        }

        if (textField == null) {
            textField = (JTextField)dataFormUI.getComponent("userName");
            if (textField != null) {
                textField.setText(query);
            }
        }

        if (textField != null) {
            searchForm.performSearch();
        }

        JFrame frame = new JFrame();
        frame.setIconImage(SparkRes.getImageIcon(SparkRes.VIEW_IMAGE).getImage());
        final JDialog dialog = new JDialog(frame, "User Search", false);
        dialog.getContentPane().add(searchForm);
        dialog.pack();
        dialog.setSize(500, 500);

        GraphicUtils.centerWindowOnScreen(dialog);
        dialog.setVisible(true);
    }

    /**
     * Load all Search Services.
     */
    private void loadSearchServices() {
        UserSearchManager userSearchManager = new UserSearchManager(SparkManager.getConnection());
        try {
            searchServices = userSearchManager.getSearchServices();
        }
        catch (XMPPException e) {
            Log.error("Unable to load search services.", e);
        }
    }

    /**
     * Return the Search Services discovered by the client.
     *
     * @return the discovered search services.
     */
    public Collection getSearchServices() {
        return searchServices;
    }

    public String getToolTip() {
        return "Search for other people on the server.";
    }

    public String getDefaultText() {
        return "Search for other people.";
    }

    public String getName() {
        return "User Search";
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.SEARCH_USER_16x16);
    }


}
