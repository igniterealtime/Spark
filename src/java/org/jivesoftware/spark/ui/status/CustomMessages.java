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
package org.jivesoftware.spark.ui.status;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.JiveTreeCellRenderer;
import org.jivesoftware.spark.component.JiveTreeNode;
import org.jivesoftware.spark.component.Tree;
import org.jivesoftware.spark.component.renderer.ListIconRenderer;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

import com.thoughtworks.xstream.XStream;

public class CustomMessages {
    private static File customMessages = new File(SparkManager.getUserDirectory(), "custom_messages.xml");
    private static XStream xstream = new XStream();

    private CustomMessages() {

    }

    static {
        xstream.alias("custom-items", List.class);
        xstream.alias("custom-status", CustomStatusItem.class);
    }


    // Handle Custom Messages
    public static List<CustomStatusItem> load() {
        List<CustomStatusItem> list = null;

        if (customMessages.exists()) {
            try {
                list = (List<CustomStatusItem>)xstream.fromXML(new FileReader(customMessages));
            }
            catch (Exception e) {
                xstream.alias("list", List.class);
                xstream.alias("com.jivesoftware.workspaces.CustomStatusItem", CustomStatusItem.class);
                try {
                    list = (List<CustomStatusItem>)xstream.fromXML(new FileReader(customMessages));
                }
                catch (Exception e1) {
                    Log.error(e1);
                }
            }
        }

        if (list == null) {
            list = new ArrayList<>();
        }

        // Sort Custom Messages
        Collections.sort( list, ( a, b ) -> ( a.getStatus().compareToIgnoreCase( b.getStatus() ) ) );

        return list;
    }

    public static void save(List<CustomStatusItem> list) {
        xstream.alias("custom-items", List.class);
        xstream.alias("custom-status", CustomStatusItem.class);

        try {
            xstream.toXML(list, new FileWriter(customMessages));
        }
        catch (IOException e) {
            Log.error("Could not save custom messages.", e);
        }
    }

    public static void addCustomMessage() {
        CustomStatus status = new CustomStatus();
        status.invoke(null);
    }

    public static void editCustomMessages() {
        final JiveTreeNode rootNode = new JiveTreeNode(Res.getString("status.custom.messages"));
        final Tree tree = new Tree(rootNode);
        tree.setCellRenderer(new JiveTreeCellRenderer());

        final List<CustomStatusItem> customItems = load();

        final StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
        Iterator<StatusItem> statusItems = statusBar.getStatusList().iterator();
        while (statusItems.hasNext()) {
            StatusItem item = statusItems.next();
            JiveTreeNode node = new JiveTreeNode(item.getText(), false, item.getIcon());
            Iterator<CustomStatusItem> cMessages = customItems.iterator();

            node.setAllowsChildren(true);

            while (cMessages.hasNext()) {
                CustomStatusItem csi = cMessages.next();
                if (csi.getType().equals(item.getText())) {
                    JiveTreeNode subNode = new JiveTreeNode(csi.getStatus(), false);
                    node.add(subNode);

                }
            }


            rootNode.add(node);

        }

        final JScrollPane pane = new JScrollPane(tree);
        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("close")};
        final JOptionPane optionPane = new JOptionPane(pane, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(optionPane, BorderLayout.CENTER);

        final JDialog optionsDialog = new JDialog(SparkManager.getMainWindow(), Res.getString("title.edit.custom.message"), true);
        optionsDialog.setContentPane(mainPanel);
        optionsDialog.pack();
        optionsDialog.setLocationRelativeTo(SparkManager.getMainWindow());

        optionPane.addPropertyChangeListener( propertyChangeEvent -> {
            String value = (String)optionPane.getValue();
            if (Res.getString("close").equals(value)) {
                optionsDialog.setVisible(false);
            }
        } );

        for (int i = 0; i <= tree.getRowCount(); i++) {
            tree.expandPath(tree.getPathForRow(i));
        }

        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                checkPopup(mouseEvent);
            }

            public void mouseReleased(MouseEvent mouseEvent) {
                checkPopup(mouseEvent);
            }

            public void checkPopup(MouseEvent event) {
                if (!event.isPopupTrigger()) {
                    return;
                }

                TreePath path = tree.getPathForLocation(event.getX(), event.getY());
                if (path != null) {
                    tree.setSelectionPath(path);
                }

                final JiveTreeNode selectedNode = (JiveTreeNode)tree.getLastSelectedPathComponent();

                if (selectedNode == null || selectedNode.getParent() == null) {
                    return;
                }
                else if (selectedNode.getParent() == rootNode) {
                    JPopupMenu popup = new JPopupMenu();
                    Action addAction = new AbstractAction() {
						private static final long serialVersionUID = 2187174931315380754L;

						public void actionPerformed(ActionEvent actionEvent) {
                            CustomStatus status = new CustomStatus();
                            String type = (String)selectedNode.getUserObject();
                            status.invoke(type);
                            reloadTree(rootNode, tree);
                        }
                    };

                    addAction.putValue(Action.NAME, Res.getString("menuitem.add"));
                    popup.add(addAction);
                    popup.show(tree, event.getX(), event.getY());
                    return;
                }


                final JiveTreeNode parentNode = (JiveTreeNode)selectedNode.getParent();
                final String messageStatus = (String)selectedNode.getUserObject();
                final String messageType = (String)parentNode.getUserObject();

                if (event.isPopupTrigger()) {
                    JPopupMenu popup = new JPopupMenu();
                    Action deleteAction = new AbstractAction() {
						private static final long serialVersionUID = -4421868467918912876L;

						public void actionPerformed(ActionEvent actionEvent) {
                            List<CustomStatusItem> list = new ArrayList<>();
                            //Refresh customItems list
                            List<CustomStatusItem> customItems = load();
                            Iterator<CustomStatusItem> iter = customItems.iterator();
                            while (iter.hasNext()) {
                                CustomStatusItem item = iter.next();
                                if (item.getType().equals(messageType) && item.getStatus().equals(messageStatus)) {

                                }
                                else {
                                    list.add(item);
                                }
                            }

                            parentNode.remove(selectedNode);
                            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                            model.nodeStructureChanged(parentNode);
                            save(list);
                        }
                    };
                    deleteAction.putValue(Action.NAME, Res.getString("menuitem.delete"));
                    popup.add(deleteAction);


                    Action editAction = new AbstractAction() {
						private static final long serialVersionUID = 39916149252596354L;

						public void actionPerformed(ActionEvent actionEvent) {
                            List<CustomStatusItem> newItems = load();
                            Iterator<CustomStatusItem> iter = newItems.iterator();
                            while (iter.hasNext()) {
                                CustomStatusItem item = iter.next();
                                if (item.getType().equals(messageType) && item.getStatus().equals(messageStatus)) {
                                    CustomStatus customStatus = new CustomStatus();
                                    customStatus.showEditDialog(item);

                                    // Reload tree.
                                    reloadTree(rootNode, tree);
                                    break;
                                }

                            }

                        }
                    };

                    editAction.putValue(Action.NAME, Res.getString("menuitem.edit"));
                    popup.add(editAction);
                    popup.show(tree, event.getX(), event.getY());
                }
            }
        });

        optionsDialog.setVisible(true);
        optionsDialog.toFront();
        optionsDialog.requestFocus();
    }

    private static void reloadTree(JiveTreeNode rootNode, Tree tree) {
        StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
        rootNode.removeAllChildren();
        Iterator<StatusItem> statusItems = statusBar.getStatusList().iterator();
        while (statusItems.hasNext()) {
            StatusItem statusItem = statusItems.next();
            JiveTreeNode node = new JiveTreeNode(statusItem.getText(), false, statusItem.getIcon());

            List<CustomStatusItem> newItems = load();
            Iterator<CustomStatusItem> cMessages = newItems.iterator();

            node.setAllowsChildren(true);

            while (cMessages.hasNext()) {
                CustomStatusItem csi = cMessages.next();
                if (csi.getType().equals(statusItem.getText())) {
                    JiveTreeNode subNode = new JiveTreeNode(csi.getStatus(), false);
                    node.add(subNode);

                }
            }


            rootNode.add(node);
        }

        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.nodeStructureChanged(rootNode);
        tree.expandTree();
    }

    private static class CustomStatus extends JPanel {
		private static final long serialVersionUID = 1117350001209641469L;
		private JLabel typeLabel = new JLabel();
        private JComboBox typeBox = new JComboBox();

        private JLabel statusLabel = new JLabel();
        private JTextField statusField = new JTextField();

        private JLabel priorityLabel = new JLabel();
        private JTextField priorityField = new JTextField();

        private JCheckBox persistBox = new JCheckBox();

        public CustomStatus() {
            StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();

            // Add Mnemonics
            ResourceUtils.resLabel(typeLabel, typeBox, Res.getString("label.presence"));
            ResourceUtils.resLabel(statusLabel, statusField, Res.getString("label.message"));
            ResourceUtils.resLabel(priorityLabel, priorityField, Res.getString("label.priority"));
            ResourceUtils.resButton(persistBox, Res.getString("button.save.for.future.use"));

            setLayout(new GridBagLayout());

            add(typeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(statusLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(priorityLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

            add(typeBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 150, 0));
            add(statusField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            add(priorityField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            add(persistBox, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

            persistBox.setSelected(true);

            typeBox.setRenderer(new ListIconRenderer());
            // Add Types
            Iterator<StatusItem> statusIterator = statusBar.getStatusList().iterator();
            while (statusIterator.hasNext()) {
                final StatusItem statusItem = statusIterator.next();
                if (!PresenceManager.isOnPhone(statusItem.getPresence())) {
	                ImageIcon icon = (ImageIcon)statusItem.getIcon();

	                ImageIcon newIcon = new ImageIcon(icon.getImage());
	                newIcon.setDescription(statusItem.getText());

	                typeBox.addItem(newIcon);
                }
            }

            priorityField.setText("1");
            statusField.setText(Res.getString("status.online"));

        }

        public String getType() {
            ImageIcon icon = (ImageIcon)typeBox.getSelectedItem();
            return icon.getDescription();
        }

        public String getStatus() {
            return statusField.getText();
        }

        public int getPriority() {
            try {
                return Integer.parseInt(priorityField.getText());
            }
            catch (NumberFormatException e) {
                return 1;
            }
        }

        public void showEditDialog(final CustomStatusItem item) {
            // Construct main panel w/ layout.
            final JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            // The user should only be able to close this dialog.
            Object[] options = {Res.getString("ok"), Res.getString("cancel")};
            final JOptionPane optionPane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

            mainPanel.add(optionPane, BorderLayout.CENTER);

            final JDialog optionsDialog = new JDialog(SparkManager.getMainWindow(), Res.getString("title.edit.custom.message"), true);
            optionsDialog.setContentPane(mainPanel);
            optionsDialog.pack();

            persistBox.setVisible(false);

            priorityField.setText(Integer.toString(item.getPriority()));
            statusField.setText(item.getStatus());

            String type = item.getType();
            int count = typeBox.getItemCount();
            for (int i = 0; i < count; i++) {
                ImageIcon icon = (ImageIcon)typeBox.getItemAt(i);
                if (icon.getDescription().equals(type)) {
                    typeBox.setSelectedIndex(i);
                    break;
                }
            }

            optionsDialog.setLocationRelativeTo(SparkManager.getMainWindow());
            optionPane.addPropertyChangeListener( propertyChangeEvent -> {
                String value = (String)optionPane.getValue();
                if (Res.getString("cancel").equals(value)) {
                    optionsDialog.setVisible(false);
                }
                else if (Res.getString("ok").equals(value)) {
                    List<CustomStatusItem> list = load();
                    Iterator<CustomStatusItem> iter = list.iterator();

                    CustomStatusItem changeItem = null;
                    while (iter.hasNext()) {
                        CustomStatusItem customItem = iter.next();
                        if (customItem.getType().equals(item.getType()) &&
                                customItem.getStatus().equals(item.getStatus()) &&
                                customItem.getPriority() == item.getPriority() ) {

                            changeItem = customItem;
                            break;
                        }
                    }


                    Iterator<CustomStatusItem> customListIterator = list.iterator();
                    boolean exists = false;
                    while (customListIterator.hasNext()) {
                        CustomStatusItem customItem = customListIterator.next();
                        String type1 = customItem.getType();
                        String status = customItem.getStatus();
                        int priority = customItem.getPriority();

                        if ( type1.equals(getType()) && status.equals(getStatus()) && priority == getPriority()) {
                            exists = true;
                        }
                    }

                    if (changeItem != null) {
                        changeItem.setPriority(getPriority());
                        changeItem.setStatus(getStatus());
                        changeItem.setType(getType());

                    }

                    // Otherwise save.
                    if (!exists) {
                        save(list);
                    }
                    optionsDialog.setVisible(false);
                }
            } );

            optionsDialog.setVisible(true);
            optionsDialog.toFront();
            optionsDialog.requestFocus();
        }


        public void invoke(String selectedType) {
            final StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();

            // Construct main panel w/ layout.
            final JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            // The user should only be able to close this dialog.
            Object[] options = {Res.getString("ok"), Res.getString("cancel")};
            final JOptionPane optionPane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

            mainPanel.add(optionPane, BorderLayout.CENTER);

            final JDialog optionsDialog = new JDialog(SparkManager.getMainWindow(), Res.getString("title.set.status.message"), true);
            optionsDialog.setContentPane(mainPanel);
            optionsDialog.pack();

            if (selectedType != null) {
                int count = typeBox.getItemCount();
                for (int i = 0; i < count; i++) {
                    ImageIcon icon = (ImageIcon)typeBox.getItemAt(i);
                    if (icon.getDescription().equals(selectedType)) {
                        typeBox.setSelectedIndex(i);
                        break;
                    }
                }
                persistBox.setSelected(true);
            }


            optionsDialog.setLocationRelativeTo(SparkManager.getMainWindow());
            optionPane.addPropertyChangeListener( propertyChangeEvent -> {
                String value = (String)optionPane.getValue();
                if (Res.getString("cancel").equals(value)) {
                    optionsDialog.setVisible(false);
                }
                else if (Res.getString("ok").equals(value)) {

                    if (!ModelUtil.hasLength(getStatus())) {
                        JOptionPane.showMessageDialog(optionsDialog, Res.getString("message.invalid.status"));
                        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        return;
                    }


                    if (!persistBox.isSelected()) {
                        // Change presence and quit.
                        StatusItem item = statusBar.getStatusItem(getType());
                        Presence oldPresence = item.getPresence();

                        Presence presence = StatusBar.copyPresence(oldPresence);
                        presence.setStatus(getStatus());
                        presence.setPriority(getPriority());

                        SparkManager.getSessionManager().changePresence(presence);
                        statusBar.setStatus(getStatus());
                        optionsDialog.setVisible(false);

                        return;
                    }

                    List<CustomStatusItem> list = load();

                    CustomStatusItem customStatusItem = new CustomStatusItem();
                    customStatusItem.setPriority(getPriority());
                    customStatusItem.setStatus(getStatus());
                    customStatusItem.setType(getType());


                    Iterator<CustomStatusItem> customListIterator = list.iterator();
                    boolean exists = false;
                    while (customListIterator.hasNext()) {
                        CustomStatusItem customItem = customListIterator.next();
                        String type = customItem.getType();
                        String status = customItem.getStatus();

                        if (type.equals(customStatusItem.getType()) && status.equals(customStatusItem.getStatus())) {
                            exists = true;
                        }
                    }

                    // Otherwise save.
                    if (!exists) {
                        list.add(customStatusItem);

                        // Update current status.
                        StatusItem item = statusBar.getStatusItem(getType());
                        Presence oldPresence = item.getPresence();
                        Presence presence = StatusBar.copyPresence(oldPresence);
                        presence.setStatus(getStatus());
                        presence.setPriority(getPriority());

                        SparkManager.getSessionManager().changePresence(presence);
                        statusBar.setStatus(getStatus());

                        // Persist new item.
                        save(list);
                    }
                    optionsDialog.setVisible(false);
                }
            } );

            optionsDialog.setVisible(true);
            optionsDialog.toFront();
            optionsDialog.requestFocus();
        }
    }


}