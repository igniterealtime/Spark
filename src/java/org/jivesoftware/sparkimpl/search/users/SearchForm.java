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

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.DataFormUI;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class SearchForm extends JPanel {
    private UserSearchResults searchResults;
    private DataFormUI questionForm;
    private UserSearchManager searchManager;
    private String serviceName;
    private Form searchForm;

    public SearchForm(String service) {
        this.serviceName = service;

        searchManager = new UserSearchManager(SparkManager.getConnection());
        setLayout(new GridBagLayout());

        // Load searchForm

        try {
            searchForm = searchManager.getSearchForm(service);
        }
        catch (XMPPException e) {
            Log.error("Unable to load search services.", e);
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Unable to contact search service.", "Search Service Not Available", JOptionPane.ERROR_MESSAGE);
            return;
        }

        searchManager = new UserSearchManager(SparkManager.getConnection());
        questionForm = new DataFormUI(searchForm);
        questionForm.setBorder(BorderFactory.createTitledBorder("Search Form"));

        add(questionForm, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        // Add User DataForm
        final JButton searchButton = new JButton();
        ResourceUtils.resButton(searchButton, "&Search");
        add(searchButton, new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        String enterString = org.jivesoftware.spark.util.StringUtils.keyStroke2String(enter);

        // Handle Left Arrow
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(enterString), "enter");

        getActionMap().put("enter", new AbstractAction("enter") {
            public void actionPerformed(ActionEvent evt) {
                performSearch();
            }
        });

        // Add searchResults
        searchResults = new UserSearchResults();
        searchResults.setBorder(BorderFactory.createTitledBorder("Search Results"));
        add(searchResults, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    }

    public DataFormUI getQuestionForm() {
        return questionForm;
    }

    public Form getSearchForm() {
        return searchForm;
    }

    /**
     * Starts a search based on the Answered form.
     */
    public void performSearch() {
        searchResults.clearTable();

        Form answerForm = questionForm.getFilledForm();
        try {
            ReportedData data = searchManager.getSearchResults(answerForm, serviceName);
            if (data != null) {
                searchResults.showUsersFound(data);
            }
        }
        catch (XMPPException e1) {
            Log.error("Unable to load search service.", e1);
            JOptionPane.showMessageDialog(searchResults, "No results found!", "No Results", JOptionPane.ERROR_MESSAGE);
        }

        searchResults.invalidate();
        searchResults.validate();
        searchResults.repaint();
    }
}
