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
package org.jivesoftware.sparkimpl.plugin.alerts;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.CheckNode;
import org.jivesoftware.spark.component.CheckTree;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.ui.ChatInputEditor;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;

/**
 * Allows for better selective broadcasting.
 *
 * @author Derek DeMoro
 */
public class BroadcastDialog extends JPanel {

    private static final long serialVersionUID = -8998994627855985137L;
    private ChatInputEditor messageBox;
    private JCheckBox OfflineUsers = new JCheckBox(Res.getString("checkbox.broadcast.hide.offline.user"));
    private JRadioButton normalMessageButton;
    
    private ArrayList<ArrayList<Object>> NodesGroups = new ArrayList<>();
    private List<CheckNode> nodes = new ArrayList<>();
    private List<CheckNode> groupNodes = new ArrayList<>();
    private CheckNode rosterNode; 
    private CheckTree checkTree; 
    private Integer OfflineGroup;
    
    public BroadcastDialog() {
        setLayout(new GridBagLayout());
        rosterNode = new CheckNode(Res.getString("title.roster"));
        checkTree = new CheckTree(rosterNode);
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        
        // creates the List for the Online Users
        String groupName = Res.getString("status.online");
        CheckNode groupNode = new CheckNode(groupName);
        groupNodes.add(groupNode);
        rosterNode.add(groupNode);
        List<String> onlineJIDs = new ArrayList<>();
        //ContactGroup groupp;
        for(ContactGroup group : contactList.getContactGroups())
	        for (ContactItem item : group.getContactItems()) 
	        {
	      	  if(item.isAvailable() && !onlineJIDs.contains(item.getJID()))
	      	  {
	           CheckNode itemNode = new CheckNode(item.getDisplayName(), false, item.getIcon());
	           itemNode.setAssociatedObject(item.getJID());
	           groupNode.add(itemNode);
	           nodes.add(itemNode);
	           onlineJIDs.add(item.getJID());
	      	  }
	        }
	        
        // Build out from Roster
       
        for (ContactGroup group : contactList.getContactGroups()) {
            groupName = group.getGroupName();
            if (!group.hasAvailableContacts()) {
                continue;
            }
            groupNode = new CheckNode(groupName);
            groupNodes.add(groupNode);
            rosterNode.add(groupNode);
            
            // Now add contact items from contact group.
            for (ContactItem item : group.getContactItems()) {
                CheckNode itemNode = new CheckNode(item.getDisplayName(), false, item.getIcon());
                itemNode.setAssociatedObject(item.getJID());
                groupNode.add(itemNode);
                nodes.add(itemNode);
            }

            final List<ContactItem> offlineContacts = new ArrayList<>( group.getOfflineContacts() );
            Collections.sort(offlineContacts, ContactList.ContactItemComparator);

            for (ContactItem item : offlineContacts) {
                CheckNode itemNode = new CheckNode(item.getDisplayName(), false, item.getIcon());
                itemNode.setAssociatedObject(item.getJID());
                groupNode.add(itemNode);
                nodes.add(itemNode);
            }
        }

        messageBox = new ChatInputEditor();
        normalMessageButton = new JRadioButton(Res.getString("message.normal"));
        JRadioButton alertMessageButton = new JRadioButton(Res.getString("message.alert.notify"));

        ButtonGroup group = new ButtonGroup();
        group.add(normalMessageButton);
        group.add(alertMessageButton);

        final JScrollPane pane = new JScrollPane(messageBox);
        pane.setBorder(BorderFactory.createTitledBorder(Res.getString("label.message").replace("&", "")));

        final JScrollPane treePane = new JScrollPane(checkTree);
        treePane.setBorder(BorderFactory.createTitledBorder(Res.getString("message.send.to.these.people")));
        treePane.getVerticalScrollBar().setBlockIncrement(200);
        treePane.getVerticalScrollBar().setUnitIncrement(20);
        // Add to UI
        add(pane, new GridBagConstraints(0, 0, 1, 1, 0.5, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        add(normalMessageButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(alertMessageButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        add(treePane, new GridBagConstraints(1, 0, 1, 3, 0.5, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
        add(OfflineUsers, new GridBagConstraints(1, 3, 1, 0, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
        
        OfflineUsers.addActionListener( e -> hideOfflineUsers() );
        
        normalMessageButton.setSelected(true);
        checkTree.expandTree();

        // get list of selected users
        java.util.Collection<ContactItem> selectedUsers = contactList.getSelectedUsers();
        // if selected users is 1 or less, 
        //    don't per-select in dialog window (see SPARK-1088)
        if (selectedUsers.size() > 1) {
            // Iterate through selected users.
            for (ContactItem item : selectedUsers) {
                for (CheckNode node : nodes) {
                    if (node.getAssociatedObject().toString().equals(item.getJID())) {
                        node.setSelected(true);
                    }
                }
            }
        }
    }

    public void invokeDialog(ContactGroup group) {
        for (CheckNode node : groupNodes) {
            if (node.getUserObject().toString().equals(group.getGroupName())) {
                node.setSelected(true);
            }
        }

        invokeDialog();
    }

    /**
     * Displays the broadcast dialog.
     */
    public void invokeDialog() {
        final JDialog dlg;

        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.broadcast.message"), Res.getString("message.enter.broadcast.message"), null, true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        JButton okButton = new JButton(Res.getString("ok"));
        JButton closeButton = new JButton(Res.getString("close"));
        
        mainPanel.add(this,BorderLayout.CENTER);
        
        JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonpanel.add(okButton);
        buttonpanel.add(closeButton);
        
        mainPanel.add(buttonpanel,BorderLayout.SOUTH);

        dlg = new JDialog(SparkManager.getMainWindow(), Res.getString("broadcast"));
        dlg.setContentPane(mainPanel);
        dlg.pack();
        dlg.setSize(800, 600);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(SparkManager.getMainWindow());
        
        // Add listener
        okButton.addActionListener( e -> {
try
{
if (sendBroadcasts(dlg)) {
dlg.setVisible(false);
}
}
catch ( SmackException.NotConnectedException e1 )
{
Log.warning( "Unable to broadcast.", e1 );
}

} );

        closeButton.addActionListener( e -> dlg.setVisible(false) );

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        messageBox.requestFocus();        
        
    }

    private void hideOfflineUsers() {

	int i;
	if (OfflineUsers.isSelected()) {
	    final ContactList contactList = SparkManager.getWorkspace()
		    .getContactList();
	    i = 0;
	    for (CheckNode node : nodes) {
		if (contactList.getContactItemByDisplayName(node.toString())
			.getPresence().getType() == Presence.Type.unavailable) {
		    if (node.getParent() != null) {
			TreeNode parent = node.getParent();
			TreeNode[] path = ((DefaultTreeModel) checkTree
				.getTree().getModel()).getPathToRoot(parent);
			((DefaultTreeModel) checkTree.getTree().getModel())
				.removeNodeFromParent(node);
			checkTree.getTree()
				.setSelectionPath(new TreePath(path));
			NodesGroups.add( new ArrayList<>());
			NodesGroups.get(i).add(parent);
			NodesGroups.get(i).add(node);
			i++;
		    }
		}
	    }
	    for (int x = 0; x < groupNodes.size(); x++) {
		if (groupNodes.get(x).toString()
			.equals(Res.getString("group.offline"))) {
		    OfflineGroup = x;
		    TreeNode parent = groupNodes.get(x).getParent();
		    TreeNode[] path = ((DefaultTreeModel) checkTree.getTree()
			    .getModel()).getPathToRoot(parent);
		    ((DefaultTreeModel) checkTree.getTree().getModel())
			    .removeNodeFromParent(groupNodes.get(x));
		    checkTree.getTree().setSelectionPath(new TreePath(path));
		}
	    }
	} else {
	    i = 0;
	    DefaultMutableTreeNode child = groupNodes.get(OfflineGroup);
	    ((DefaultTreeModel) checkTree.getTree().getModel()).insertNodeInto(
		    child, rosterNode, rosterNode.getChildCount());
	    TreeNode[] path = ((DefaultTreeModel) checkTree.getTree()
		    .getModel()).getPathToRoot(rosterNode);
	    checkTree.getTree().expandPath(new TreePath(path));
	    checkTree.expandTree();
	    for (CheckNode node : nodes) {
		if (node.getParent() == null) {
		    child = (CheckNode) NodesGroups.get(i).get(1);
		    ((DefaultTreeModel) checkTree.getTree().getModel())
			    .insertNodeInto(child, ((CheckNode) NodesGroups
				    .get(i).get(0)), ((CheckNode) NodesGroups
				    .get(i).get(0)).getChildCount());
		    path = ((DefaultTreeModel) checkTree.getTree().getModel())
			    .getPathToRoot(node);
		    checkTree.getTree().expandPath(new TreePath(path));
		    checkTree.expandTree();
		    i++;
		}
	    }
	}
    }
    
    /**
     * Sends a broadcast message to all users selected.
     * @param dlg 
     */
    private boolean sendBroadcasts(JDialog dlg) throws SmackException.NotConnectedException
    {
        final Set<String> jids = new HashSet<>();
        
        UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
        
        for (CheckNode node : nodes) {
            if (node.isSelected()) {
                String jid = (String)node.getAssociatedObject();
                jids.add(jid);
            }
        }
        
        if(jids.size() == 0)
        {
            JOptionPane.showMessageDialog(dlg, Res.getString("message.broadcast.no.user.selected"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String text = messageBox.getText();
        if (!ModelUtil.hasLength(text)) {
            JOptionPane.showMessageDialog(dlg, Res.getString("message.broadcast.no.text"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        for (String jid : jids) {
            final Message message = new Message();
            message.setTo(jid);
            message.setBody(text);

            if (normalMessageButton.isSelected()) {
                message.setType(Message.Type.normal);
            }
            else {
                message.setType(Message.Type.headline);
            }
            SparkManager.getConnection().sendStanza(message);
        }
       
        return true;
    }
}
