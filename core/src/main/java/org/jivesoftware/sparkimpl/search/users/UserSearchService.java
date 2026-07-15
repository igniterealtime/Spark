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
package org.jivesoftware.sparkimpl.search.users;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.spark.SessionManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.search.Searchable;
import org.jivesoftware.spark.ui.DataFormUI;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserSearchService implements Searchable {
    private Collection<DomainBareJid> searchServices;

    public UserSearchService() {
        loadSearchServices();
    }

    @Override
	public void search(final String query) {
        SwingWorker worker = new SwingWorker() {
            @Override
			public Object construct() {
                // On initialization, find search service.
                if (searchServices == null) {
                    loadSearchServices();
                }
                return true;
            }

            @Override
			public void finished() {
                processQuery(query);
            }
        };

        worker.start();

    }

    private void processQuery(String query) {
        if (searchServices == null || searchServices.isEmpty()) {
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.search.service.not.available"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserSearchForm searchForm;
        DataFormUI dataFormUI;
        try {
            searchForm = new UserSearchForm(searchServices);
            dataFormUI = searchForm.getQuestionForm();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.search.service.not.available"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean querySet = dataFormUI.setFieldValue("search", query);
        if (!querySet) {
            querySet = dataFormUI.setFieldValue("last", query);
        }
        if (!querySet) {
            querySet = dataFormUI.setFieldValue("userName", query);
        }

        if (querySet) {
            searchForm.performSearch();
        }

        JFrame frame = new JFrame();
        frame.setIconImage(SparkRes.getImageIcon(SparkRes.Icon.VIEW_IMAGE).getImage());
        final JDialog dialog = new JDialog(frame, Res.getString("title.person.search"), false);
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
        try {
            searchServices = getServices();
        }
        catch (Exception e) {
            Log.error("Unable to load search services.", e);
        }
    }

    /**
     * Returns a collection of search services found on the server.
     */
    private Collection<DomainBareJid> getServices() {
        List<DomainBareJid> searchServices = new ArrayList<>(2);
        SessionManager sessionManager = SparkManager.getSessionManager();
        var discoInfos = sessionManager.getDiscoveredInfos();
        for (var entry : discoInfos.entrySet()) {
            Jid serviceJid = entry.getKey();
            DiscoverInfo info =  entry.getValue();;
            if (info == null) {
                continue;
            }
                if (info.containsFeature("jabber:iq:search")) {
                    // Check that the search service belongs to user searches (and not room searches or other searches)
                    if (info.hasIdentity("directory", "user")) {
                            searchServices.add(serviceJid.asDomainBareJid());
                    }
                }
        }
        return searchServices;
    }

    /**
     * Return the Search Services discovered by the client.
     *
     * @return the discovered search services.
     */
    public Collection<DomainBareJid> getSearchServices() {
        return searchServices;
    }

    @Override
	public String getToolTip() {
        return Res.getString("message.search.for.other.people");
    }

    @Override
	public String getDefaultText() {
        return Res.getString("message.search.for.other.people");
    }

    @Override
	public String getName() {
        return Res.getString("title.person.search");
    }

    @Override
	public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.Icon.SEARCH_USER_16x16);
    }


}
