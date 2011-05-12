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
package org.jivesoftware.spark.plugin;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.Table;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.browser.BrowserFactory;
import org.jivesoftware.spark.component.browser.BrowserViewer;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

public class GoogleFileViewer {
	DocumentTable table;

    public void viewFiles(final Collection<GoogleSearchResult> col, final boolean showFiles) {
    	
    	EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				table = new DocumentTable();
				TitlePanel titlePanel = new TitlePanel("Google Search", "Results from your search.", null, true);
				
				final JPanel mainPanel = new JPanel();
		        mainPanel.setLayout(new BorderLayout());
		        mainPanel.add(titlePanel, BorderLayout.NORTH);

		        JScrollPane pane = new JScrollPane(table);
		        pane.getViewport().setBackground(Color.white);

		        mainPanel.add(pane, BorderLayout.CENTER);
		        
		     // Build Viewer
		        

		        for (GoogleSearchResult aCol : col) {
		            GoogleSearchResult result = aCol;
		            Icon icon = result.getIcon();
		            if (icon.getIconWidth() == -1) {
		                icon = SparkRes.getImageIcon(SparkRes.SMALL_DOCUMENT_VIEW);
		            }
		            JLabel label = new JLabel(icon);
		            label.setName(result.getCachedURL());
		            String url = result.getURL();

		            boolean isFile = new File(url).exists();

		            if (isFile && showFiles) {
		                Object[] obj = {label, result.getSubject(), result.getURL()};
		                table.getTableModel().addRow(obj);
		            } else if (!isFile) {
		                Object[] obj = {label, result.getSubject(), result.getURL()};
		                table.getTableModel().addRow(obj);
		            }

		        }

		        // Create Frame
		        JFrame frame = new JFrame("Google Search");


		        frame.setIconImage(SparkRes.getImageIcon(SparkRes.SMALL_DOCUMENT_VIEW).getImage());
		        frame.getContentPane().setLayout(new BorderLayout());
		        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		        frame.pack();
		        frame.setSize(600, 400);
		        GraphicUtils.centerWindowOnScreen(frame);
		        frame.setVisible(true);
			}
		});
    	
        

        
    }


    private final class DocumentTable extends Table {
		private static final long serialVersionUID = 2740929154486852378L;

		public DocumentTable() {
            super(new String[]{" ", "Document Title", "Location"});
            getColumnModel().setColumnMargin(0);
            getColumnModel().getColumn(0).setMaxWidth(30);

            setSelectionBackground(Table.SELECTION_COLOR);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setRowSelectionAllowed(true);


            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int row = getSelectedRow();
                        JLabel label = (JLabel)getValueAt(row, 0);
                        String title = (String)getValueAt(row, 1);
                        JFrame frame = new JFrame("Viewing Document - " + title);
                        frame.setIconImage(SparkRes.getImageIcon(SparkRes.SMALL_DOCUMENT_VIEW).getImage());
                        BrowserViewer viewer = BrowserFactory.getBrowser();

                        frame.getContentPane().setLayout(new BorderLayout());
                        frame.getContentPane().add(viewer, BorderLayout.CENTER);

                        frame.pack();
                        frame.setSize(600, 400);

                        frame.setLocationRelativeTo(table);
                        frame.setVisible(true);
                        try {
                            viewer.loadURL(label.getName());
                        }
                        catch (Exception e1) {
                            Log.error(e1);
                        }


                    }
                }

                public void mouseReleased(MouseEvent e) {
                    checkPopup(e);
                }

                public void mousePressed(MouseEvent e) {
                    checkPopup(e);
                }
            });

        }

        // Handle image rendering correctly
        public TableCellRenderer getCellRenderer(int row, int column) {
            Object o = getValueAt(row, column);
            if (o != null) {
                if (o instanceof JLabel) {
                    return new JLabelRenderer(false);
                }
            }
            return super.getCellRenderer(row, column);
        }

        private void checkPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                final JPopupMenu popupMenu = new JPopupMenu();

                Action action = new AbstractAction() {
					private static final long serialVersionUID = 5747717080614743622L;

					public void actionPerformed(ActionEvent e) {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow != -1) {
                            String location = (String)table.getValueAt(selectedRow, 2);
                            File file = new File(location);
                            try {
                                Runtime.getRuntime().exec("explorer \"" + file.getParentFile().getAbsolutePath() + "\"");
                            }
                            catch (IOException e1) {
                                Log.error(e1);
                            }

                        }
                    }
                };

                action.putValue(Action.NAME, "Open Location");
                popupMenu.add(action);
                popupMenu.show(table, e.getX(), e.getY());


            }
        }

    }
}
