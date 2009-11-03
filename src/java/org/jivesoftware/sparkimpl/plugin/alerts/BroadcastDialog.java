/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.alerts;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jivesoftware.resource.Res;
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

/**
 * Allows for better selective broadcasting.
 *
 * @author Derek DeMoro
 */
public class BroadcastDialog extends JPanel {

    /**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private ChatInputEditor messageBox;
	private JCheckBox OfflineUsers = new JCheckBox(Res.getString("checkbox.broadcast.hide.offline.user"));
    private JRadioButton normalMessageButton;
    
    private ArrayList<ArrayList<Object>> NodesGroups = new ArrayList<ArrayList<Object>>();
    private List<CheckNode> nodes = new ArrayList<CheckNode>();
    private List<CheckNode> groupNodes = new ArrayList<CheckNode>();
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
        //ContactGroup groupp;
        for(ContactGroup group : contactList.getContactGroups())
	        for (ContactItem item : group.getContactItems()) 
	        {
	      	  if(item.isAvailable())
	      	  {
	           CheckNode itemNode = new CheckNode(item.getDisplayName(), false, item.getIcon());
	           itemNode.setAssociatedObject(item.getJID());
	           groupNode.add(itemNode);
	           nodes.add(itemNode);
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

            final List<ContactItem> offlineContacts = new ArrayList<ContactItem>(group.getOfflineContacts());
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
        pane.setBorder(BorderFactory.createTitledBorder(Res.getString("label.message")));

        final JScrollPane treePane = new JScrollPane(checkTree);
        treePane.setBorder(BorderFactory.createTitledBorder(Res.getString("message.send.to.these.people")));

        // Add to UI
        add(pane, new GridBagConstraints(0, 0, 1, 1, 0.5, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        add(normalMessageButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(alertMessageButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        add(treePane, new GridBagConstraints(1, 0, 1, 3, 0.5, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
        add(OfflineUsers, new GridBagConstraints(1, 3, 1, 0, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
        
        OfflineUsers.addActionListener(new ActionListener()
 	     {
      	  public void actionPerformed(ActionEvent e)
      	  {
      		  hideOfflineUsers();
           }
 	     });
        
        normalMessageButton.setSelected(true);
        checkTree.expandTree();

        // Iterate through selected users.
        for (ContactItem item : contactList.getSelectedUsers()) {
            for (CheckNode node : nodes) {
                if (node.getAssociatedObject().toString().equals(item.getJID())) {
                    node.setSelected(true);
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
        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.broadcast.message"), Res.getString("message.enter.broadcast.message"), null, true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("ok"), Res.getString("close")};
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog(SparkManager.getMainWindow(), Res.getString("broadcast"));
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(800, 600);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(SparkManager.getMainWindow());

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String value = (String)pane.getValue();
                if(!dlg.isVisible()){
                    return;
                }

                if (Res.getString("close").equals(value)) {
                    dlg.setVisible(false);
                }
                else {
                    dlg.setVisible(false);
                    sendBroadcasts();
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        messageBox.requestFocus();
    }

    private void hideOfflineUsers()
    {
   	 
   	  int i = 0;
		  if(OfflineUsers.isSelected())
		  {
			  final ContactList contactList = SparkManager.getWorkspace().getContactList();
			  i = 0;
			  for(CheckNode node : nodes)
			  {
				  if(contactList.getContactItemByDisplayName(node.toString()).getPresence().getType() == Presence.Type.unavailable)
				  {
					  if(node.getParent() != null)
					  {
					  TreeNode parent = node.getParent();
					  TreeNode[] path = ((DefaultTreeModel)checkTree.getTree().getModel()).getPathToRoot(parent);
					  ((DefaultTreeModel)checkTree.getTree().getModel()).removeNodeFromParent(node);
					  checkTree.getTree().setSelectionPath(new TreePath(path));
					  NodesGroups.add(new ArrayList<Object>());
					  NodesGroups.get(i).add(parent);
					  NodesGroups.get(i).add(node);
					  i++;
					  }
				  }
			  }
			  for(int x = 0; x < groupNodes.size(); x++)
			  {
				  if(groupNodes.get(x).toString().equals("Offline Group"))
				  {
					  OfflineGroup = x;
					  TreeNode parent = groupNodes.get(x).getParent();
					  TreeNode[] path = ((DefaultTreeModel)checkTree.getTree().getModel()).getPathToRoot(parent);
					  ((DefaultTreeModel)checkTree.getTree().getModel()).removeNodeFromParent(groupNodes.get(x));
					  checkTree.getTree().setSelectionPath(new TreePath(path));
				  }
			  }
		  }
		  else
		  {
			  i = 0;
			  DefaultMutableTreeNode child = groupNodes.get(OfflineGroup);
			  ((DefaultTreeModel)checkTree.getTree().getModel()).insertNodeInto(child, rosterNode, rosterNode.getChildCount()); 
			  TreeNode[] path = ((DefaultTreeModel)checkTree.getTree().getModel()).getPathToRoot(rosterNode);
			  checkTree.getTree().expandPath(new TreePath(path));
			  checkTree.expandTree();
			  for(CheckNode node : nodes)
			  {
				  if(node.getParent() == null)
				  {
					  child = (CheckNode)NodesGroups.get(i).get(1);
					  ((DefaultTreeModel)checkTree.getTree().getModel()).insertNodeInto(child, ((CheckNode) NodesGroups.get(i).get(0)), ((CheckNode) NodesGroups.get(i).get(0)).getChildCount()); 
					  path = ((DefaultTreeModel)checkTree.getTree().getModel()).getPathToRoot(node);
					  checkTree.getTree().expandPath(new TreePath(path));
					  checkTree.expandTree();
					  i++;
				  }
			  }
		  }
    }
    
    /**
     * Sends a broadcast message to all users selected.
     */
    private void sendBroadcasts() {
        final Set<String> jids = new HashSet<String>();

        for (CheckNode node : nodes) {
            if (node.isSelected()) {
                String jid = (String)node.getAssociatedObject();
                jids.add(jid);
            }
        }

        String text = messageBox.getText();
        if (!ModelUtil.hasLength(text)) {
            return;
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
            SparkManager.getConnection().sendPacket(message);
        }
    }
}
