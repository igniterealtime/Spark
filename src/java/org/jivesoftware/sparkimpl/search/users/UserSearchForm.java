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

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.ui.DataFormUI;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException; 


/**
 * UserSearchForm is used to do explicit searching of users using the JEP 55 Search Service.
 */
public class UserSearchForm extends JPanel {
    private static final long	serialVersionUID	= -9192188543673595941L;
    private JComboBox servicesBox;
    private UserSearchManager searchManager;

    private Collection<String> searchServices;

    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel();

    private TitlePanel titlePanel;

    private Map<String,SearchForm> serviceMap = new HashMap<String,SearchForm>();

    
    private static File pluginsettings = new File(Spark.getSparkUserHome() + File.separator + "search.properties"); //new
    
    /**
     * Initializes the UserSearchForm with all available search services.
     *
     * @param searchServices a Collection of all search services found.
     */
    public UserSearchForm(Collection<String> searchServices) {
        setLayout(new GridBagLayout());

        cardPanel.setLayout(cardLayout);

        this.searchServices = searchServices;

        searchManager = new UserSearchManager(SparkManager.getConnection());

        addSearchServices();

        showService(getSearchService());
    }

    
    
    
    
    
    private void addSearchServices() {
        // Populate with Search Services
        servicesBox = new JComboBox();
    
        for (String searchService : searchServices) {
            String service = searchService;
            servicesBox.addItem(service);
        }

        
        // Load the property file and add the search services that are read
        final Properties props = new Properties();
        String nextprop;
        boolean numbprop_bool=true;
        int numbprop;
        if (pluginsettings.exists()) { 
	    // Log.warning("Search-service Properties-file does exist= " + pluginsettings.getPath()); 
            try { 
                numbprop=0;
                props.load(new FileInputStream(pluginsettings)); 
               	String testsearch; 
               	numbprop_bool=true;
               	while (numbprop_bool) {
               		nextprop = "search"+numbprop;
               		testsearch = props.getProperty(nextprop);
               		if (null != testsearch) {
               			Log.warning("Search-Info: SearchService-" + numbprop + " from properties-file is " + nextprop + " : " + testsearch); 
               			servicesBox.addItem(testsearch); 
                   		numbprop++;
               			} 
               		else 
               			numbprop_bool=false; 
               		}

               	} catch (IOException ioe) {
                 System.err.println(ioe); 
                
               } 
           } 
           else { 
	    // Log.error("Search-Searvice-Error: Properties-file does not exist= " + pluginsettings.getPath()); 
           }        


           if (servicesBox.getItemCount() > 0) {
               servicesBox.setSelectedIndex(0);
           }
           
        
        titlePanel = new TitlePanel("", "", SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);
        add(titlePanel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        // Add Search Service ComboBox
        final JLabel serviceLabel = new JLabel("");
        ResourceUtils.resLabel(serviceLabel, servicesBox, Res.getString("label.search.service") + ":");
        add(serviceLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(servicesBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 150, 0));

        final JButton addService = new JButton();
        ResourceUtils.resButton(addService, Res.getString("button.add.service"));
        add(addService, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        addService.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                final String serviceName = JOptionPane.showInputDialog(getRootPane(), Res.getString("message.name.of.search.service.question"), Res.getString("title.add.search.service"), JOptionPane.QUESTION_MESSAGE);
                if (ModelUtil.hasLength(serviceName)) {

                    SwingWorker findServiceThread = new SwingWorker() {
                        Form newForm;

                        public Object construct() {
                            try {
                                newForm = searchManager.getSearchForm(serviceName);
                            }
                            catch (XMPPException e) {
                                // Nothing to do
                            }
                            return newForm;
                        }

                        public void finished() {
                            if (newForm == null) {
                                JOptionPane.showMessageDialog(getGUI(), Res.getString("message.search.service.not.available"), Res.getString("title.notification"), JOptionPane.ERROR_MESSAGE);
                            }
                            else {
                                servicesBox.addItem(serviceName);
                                servicesBox.setSelectedItem(serviceName);

                                int numbprop=0;
                                boolean numbprop_bool = true;
                                String nextprop, testsearch;
                                while (numbprop_bool)
                            	{
                                nextprop = "search"+numbprop;
                               	testsearch = props.getProperty(nextprop);
                               	if (testsearch != null)
                               		numbprop++;
                               	else 
                               		{
                               		Log.warning("Search-Service: " + nextprop + " : " + serviceName + " added");
                               		props.setProperty(nextprop, serviceName);
                               		numbprop_bool = false;
                               		}
                               	}   
                                try {
                                    props.store(new FileOutputStream(pluginsettings), null);
                                } catch (IOException e) {
                                	 System.err.println(e);
                                }          

                            }
                            
                        }

                    };
                    findServiceThread.start();
                }
            }
        });

        servicesBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        try {
                            Thread.sleep(50);
                        }
                        catch (Exception e) {
                            Log.error("Problem sleeping thread.", e);
                        }
                        return "ok";
                    }

                    public void finished() {
                        showService(getSearchService());
                    }
                };
                worker.start();
            }
        });

        add(cardPanel, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
 
    }

    /**
     * Displays the specified search service.
     *
     * @param service the search service to display.
     */
    public void showService(String service) {
        if (serviceMap.containsKey(service)) {
            cardLayout.show(cardPanel, service);
        }
        else {
            // Create new Form
            SearchForm searchForm = new SearchForm(service);
            cardPanel.add(searchForm, service);
            serviceMap.put(service, searchForm);
            cardLayout.show(cardPanel, service);
        }

        SearchForm searchForm = serviceMap.get(service);
        Form form = searchForm.getSearchForm();
        String description = form.getInstructions(); //XXX maybe add a Spark String instead
        titlePanel.setTitle(Res.getString("title.person.search"));
        titlePanel.setDescription(description);
    }


    /**
     * Returns the selected search service.
     *
     * @return the selected search service.
     */
    public String getSearchService() {
        return (String)servicesBox.getSelectedItem();
    }

    /**
     * Return the QuestionForm retrieved by the search service.
     *
     * @return the QuestionForm retrieved by the search service.
     */
    public DataFormUI getQuestionForm() {
        SearchForm searchForm = serviceMap.get(getSearchService());
        return searchForm.getQuestionForm();
    }

    /**
     * Performs a search on the specified search service.
     */
    public void performSearch() {
        SearchForm searchForm = serviceMap.get(getSearchService());
        searchForm.performSearch();
    }

    /**
     * Returns the UI that represent the UserSearchForm
     *
     * @return the UserSearchForm
     */
    public Component getGUI() {
        return this;
    }

}
