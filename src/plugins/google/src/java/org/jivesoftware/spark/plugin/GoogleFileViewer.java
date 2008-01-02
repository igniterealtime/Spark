/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date:  $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */
package org.jivesoftware.spark.plugin;


import org.jivesoftware.spark.component.Table;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.browser.BrowserViewer;
import org.jivesoftware.spark.component.browser.BrowserFactory;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.resource.SparkRes;

import java.awt.BorderLayout;
import java.awt.Color;
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

public class GoogleFileViewer {
    DocumentTable table = new DocumentTable();

    public void viewFiles(Collection col, boolean showFiles) {

        TitlePanel titlePanel = new TitlePanel("Google Search", "Results from your search.", null, true);

        // Build Viewer
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JScrollPane pane = new JScrollPane(table);
        pane.getViewport().setBackground(Color.white);

        mainPanel.add(pane, BorderLayout.CENTER);

        for (Object aCol : col) {
            GoogleSearchResult result = (GoogleSearchResult) aCol;
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


    private final class DocumentTable extends Table {

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
