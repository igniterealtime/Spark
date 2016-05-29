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

package org.jivesoftware.sparkimpl.search.users;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.DataFormUI;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SearchForm extends JPanel {
    private static final long serialVersionUID = -6935368899659597477L;
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
        catch (XMPPException | SmackException e) {
            Log.error("Unable to load search services.", e);
            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.search.service.not.available"), Res.getString("title.notification"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        searchManager = new UserSearchManager(SparkManager.getConnection());
        questionForm = new DataFormUI(searchForm);
        questionForm.setBorder(BorderFactory.createTitledBorder(Res.getString("group.search.form")));

        add(questionForm, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        // Add User DataForm
        final JButton searchButton = new JButton();
        ResourceUtils.resButton(searchButton, Res.getString("button.search"));
        add(searchButton, new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        searchButton.addActionListener( e -> performSearch() );

        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        String enterString = org.jivesoftware.spark.util.StringUtils.keyStroke2String(enter);

        // Handle Left Arrow
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(enterString), "enter");

        getActionMap().put("enter", new AbstractAction("enter") {
	    private static final long serialVersionUID = -7308854327447291219L;

	    public void actionPerformed(ActionEvent evt) {
                performSearch();
            }
        });

        // Add searchResults
        searchResults = new UserSearchResults();
        searchResults.setBorder(BorderFactory.createTitledBorder(Res.getString("group.search.results")));
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

        SwingWorker worker = new SwingWorker() {
            ReportedData data;

            public Object construct() {
                try {
                    Form answerForm = questionForm.getFilledForm();
                    data = searchManager.getSearchResults(answerForm, serviceName);
                }
                catch (XMPPException | SmackException e) {
                    Log.error("Unable to load search service.", e);
                }

                return data;
            }

            public void finished() {
                if (data != null) {
                    searchResults.showUsersFound(data);
                    searchResults.invalidate();
                    searchResults.validate();
                    searchResults.repaint();
                }
                else {
                	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                    JOptionPane.showMessageDialog(searchResults, Res.getString("message.no.results.found"), Res.getString("title.notification"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.start();


    }
}
