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
package org.jivesoftware.sparkimpl.plugin.privacy.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;
import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyList;
import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyListListener;

/**
 * @author Bergunde Holger
 */
public class PrivacyListTree extends JPanel implements SparkPrivacyListListener {

    private static final long serialVersionUID = 1885262127050966627L;
    private DefaultTreeModel _model;
    private JTree _tree;
    private PrivacyManager _pManager;
    private PrivacyTreeNode _top = new PrivacyTreeNode(Res.getString("privacy.root.node"));
    private JComponent _comp;
    private JPanel treeandInfo = new JPanel(new GridBagLayout());
   
    private RolloverButton _actList;
    private RolloverButton _defList;
   
   
   
    public PrivacyListTree() {

        _comp = this;
        _pManager = PrivacyManager.getInstance();
        this.setLayout(new GridBagLayout());
        _model = new DefaultTreeModel(_top);
        _tree = new JTree(_model);
        _tree.setCellRenderer(new PrivacyTreeCellRenderer());
        JScrollPane _scrollPane = new JScrollPane(_tree);
        
        treeandInfo.setBorder(BorderFactory.createTitledBorder(Res.getString("privacy.title.preferences")));
           this.add(treeandInfo, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        createInfoPanel();
        if (_pManager.isPrivacyActive())
        {
            treeandInfo.add(_scrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            
        initializeTree();
        createCurrentListInfoPanel();
        } else
        {
            JLabel notActive = new JLabel(Res.getString("privacy.label.not.supported"));
            treeandInfo.add(notActive, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0));
        }
    }

    private void createInfoPanel() {
        _pManager.addListListener(this);
        JPanel info = new JPanel(new GridBagLayout());
        JLabel infolabel = new JLabel(Res.getString("privacy.label.information"));
        info.setBorder(BorderFactory.createTitledBorder(Res.getString("privacy.border.information")));
        JLabel iq = new JLabel(Res.getString("privacy.label.iq.desc"), SparkRes.getImageIcon("PRIVACY_QUERY_ALLOW"), SwingConstants.LEFT);
        JLabel msg = new JLabel(Res.getString("privacy.label.msg.desc"), SparkRes.getImageIcon("PRIVACY_MSG_ALLOW"), SwingConstants.LEFT);
        JLabel pin = new JLabel(Res.getString("privacy.label.pin.desc"), SparkRes.getImageIcon("PRIVACY_PIN_ALLOW"), SwingConstants.LEFT);
        JLabel pout = new JLabel(Res.getString("privacy.label.pout.desc"), SparkRes.getImageIcon("PRIVACY_POUT_ALLOW"), SwingConstants.LEFT);
        info.add(infolabel, new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 0), 0, 0));
        info.add(iq, new GridBagConstraints(0, 1, 1, 1, 0.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        info.add(msg, new GridBagConstraints(1, 1, 1, 1, 0.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        info.add(pin, new GridBagConstraints(2, 1, 1, 1, 0.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        info.add(pout, new GridBagConstraints(3, 1, 1, 1, 0.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

       
        this.add(info, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }

    
    private void createCurrentListInfoPanel() {
        JPanel listInfo = new JPanel(new GridBagLayout());
        _actList = new RolloverButton();
        _actList.addActionListener( e -> _pManager.declineActiveList() );
        _defList = new RolloverButton();
        _defList.addActionListener( e -> _pManager.declineDefaultList() );
        _actList.setHorizontalTextPosition(SwingConstants.LEFT);
        _defList.setHorizontalTextPosition(SwingConstants.LEFT);
        if (_pManager.hasActiveList()) {
            _actList.setIcon(SparkRes.getImageIcon("PRIVACY_DEACTIVATE_LIST"));
            _actList.setText(_pManager.getActiveList().getListName());
            _actList.setIcon(SparkRes.getImageIcon("PRIVACY_DEACTIVATE_LIST"));

        } else {
            _actList.setText(Res.getString("privacy.button.no.list.selected"));
            _actList.setEnabled(false);
            _actList.setIcon(null);
        }

        if (_pManager.hasDefaultList()) {
            _defList.setText(_pManager.getDefaultList().getListName());
            _defList.setEnabled(true);
            _defList.setIcon(SparkRes.getImageIcon("PRIVACY_DEACTIVATE_LIST"));

        } else {
            _defList.setText(Res.getString("privacy.button.no.list.selected"));
            _defList.setEnabled(false);
            _defList.setIcon(null);
        }

        JLabel actListLabel = new JLabel(Res.getString("privacy.label.list.is.active"));
        JLabel defListLabel = new JLabel(Res.getString("privacy.label.list.is.default"));

        JPanel actPanel = new JPanel(new FlowLayout());
        actPanel.add(actListLabel);
        actPanel.add(_actList);
        JPanel defPanel = new JPanel(new FlowLayout());
        defPanel.add(defListLabel);
        defPanel.add(_defList);

        listInfo.add(actPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 00, 00, 0), 0, 0));
        listInfo.add(defPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 00, 00, 0), 0, 0));
        treeandInfo.add(listInfo, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        // treeandInfo.add(listInfo);
    }
    
    /**
     * Initialize the jtree for the UI. Sets the model and adds the PrivacyLists
     * to the model using loadPrivacyLists()
     * 
     */
    private void initializeTree() {
        _model = new DefaultTreeModel(_top);
        _tree.setModel(_model);
        loadPrivacyLists();
        _tree.expandRow(0);

    }

    /**
     * If selected Node is a Leaf Node and it is a PrivacyItem add remove
     * jmenuitem
     * 
     * @param menu
     *            where the method should add the remove option
     * @param node
     *            the node what is selected
     */
    private void addMenuForLeaf(JPopupMenu menu, final PrivacyTreeNode node) {
        JMenuItem remUser;
        if (_tree.getSelectionPaths().length > 1) {
            remUser = new JMenuItem(Res.getString("privacy.menu.add.rem.items", _tree.getSelectionPaths().length));
        } else {
            remUser = new JMenuItem(Res.getString("privacy.menu.remove") + node.getPrivacyItem().getValue());
        }
        remUser.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        menu.add(remUser);
        remUser.addActionListener( e -> {

            for (TreePath path : _tree.getSelectionPaths()) {

                PrivacyTreeNode node1 = (PrivacyTreeNode) path.getLastPathComponent();
                // Getting privacy List where we want to remove
                PrivacyTreeNode parent1 = (PrivacyTreeNode) path.getPathComponent(1);
                SparkPrivacyList list = parent1.getPrivacyList();
                // Remove contact or group
                try
                {
                    list.removeItem( node1.getPrivacyItem().getValue());
                    //list.removePrivacyItem(node.getPrivacyItem().getType(), node.getPrivacyItem().getValue());
                    list.save();
                    _model.removeNodeFromParent( node1 );
                }
                catch ( SmackException.NotConnectedException e1 )
                {
                    Log.warning( "Unable to remove item for privacly list.", e1 );
                }
            }

        } );
    }

    /**
     * If the selected Node is a GroupNode (TreeItems "Contacts" and "Groups"
     * are GroupNodes) add the specified options to the menu
     * 
     * @param menu
     *            where the method should add the remove option
     * @param node
     *            the node what is selected
     */
    private void addMenuForGroupNodes(JPopupMenu menu, final PrivacyTreeNode node) {
        String showStringforAdd;
        if (node.isContactGroup()) {
            showStringforAdd = Res.getString("privacy.menu.add.contacts");
        } else {
            showStringforAdd = Res.getString("privacy.menu.add.groups");
        }
        PrivacyTreeNode listnode = (PrivacyTreeNode) _tree.getSelectionPath().getPathComponent(1);
        final SparkPrivacyList list = listnode.getPrivacyList();
        final PrivacyTreeNode parent = (PrivacyTreeNode) _tree.getSelectionPath().getPathComponent(2);
        JMenuItem addContact = new JMenuItem(showStringforAdd);
        addContact.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_ADD_IMAGE));
        addContact.addActionListener( e -> {
            PrivacyAddDialogUI browser = new PrivacyAddDialogUI();
            Collection<PrivacyItem> col = browser.showRoster(_comp, node.isContactGroup() ? false : true);
            try
            {
                for (PrivacyItem pI : col) {
                    final PrivacyItem clone = new PrivacyItem( pI.getType(), pI.getValue(), pI.isAllow(), list.getNewItemOrder() );
                    list.addItem(clone);
                    PrivacyTreeNode newChild = new PrivacyTreeNode(clone);
                    _model.insertNodeInto(newChild, parent, 0);
                }
                list.save();
            }
            catch ( SmackException.NotConnectedException e1 )
            {
                Log.warning( "Unable to add item to privacy list.", e1 );
            }


        } );

        menu.add(addContact);
    }

    /**
     * If the selced node is a note that contains the listname of a privacy list
     * add the specified options to the menu
     * 
     * @param menu
     *            where the method should add the remove option
     * @param node
     *            the node what is selected
     */
    private void addMenuForListNodes(JPopupMenu menu, final PrivacyTreeNode node) {
        JMenuItem addList = new JMenuItem(Res.getString("privacy.menu.add.list"));
        addList.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_ADD_IMAGE));
        JMenuItem rem = new JMenuItem(Res.getString("privacy.menu.remove.list"));
        JMenuItem act = new JMenuItem(Res.getString("privacy.menu.activate.list"));
        act.setIcon(SparkRes.getImageIcon("PRIVACY_LIGHTNING"));
        JMenuItem def = new JMenuItem(Res.getString("privacy.menu.default.list"));
        def.setIcon(SparkRes.getImageIcon("PRIVACY_CHECK"));
        act.addActionListener( e -> node.setListAsActive() );

        def.addActionListener( e -> node.setListAsDefault() );

        addList.addActionListener( e -> {

            String s = JOptionPane.showInputDialog(_comp, Res.getString("privacy.dialog.add.list"), Res.getString("privacy.menu.add.list"), JOptionPane.PLAIN_MESSAGE);
            if ((s != null) && (s.length() > 0)) {
                _pManager.createPrivacyList(s);
                addListNode(new PrivacyTreeNode(_pManager.getPrivacyList(s)), _top);
            }

        } );

        rem.addActionListener( e -> {
            int n = JOptionPane.showOptionDialog(_comp, Res.getString("privacy.dialog.rem.list", node.getPrivacyList().getListName()), Res.getString("privacy.menu.remove.list"), JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, // do
                    // //
                    // Icon
                    null, // the titles of buttons
                    null); // default button title

            if (n == JOptionPane.YES_OPTION) {
                _pManager.removePrivacyList(node.getPrivacyList().getListName());
                _model.removeNodeFromParent(node);
            }
        } );
        rem.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_DELETE));

        menu.add(addList);
        if (!node.equals(_top)) {
            menu.add(rem);
            menu.add(act);
            menu.add(def);

        }

    }

    /**
     * Adds a node to a parent on the jtree using the defaultModel
     * 
     * @param node
     *            the node that should be added. this is the childnode
     * @param parent
     *            the parent node, where the node should be added to
     */
    private void addListNode(PrivacyTreeNode node, DefaultMutableTreeNode parent) {


        _model.insertNodeInto(node, parent, 0);

        SparkPrivacyList plist = node.getPrivacyList();
      
        PrivacyTreeNode contacts = new PrivacyTreeNode(Res.getString("privacy.node.contacts"));
        contacts.setisContactGroup(true);
        _model.insertNodeInto(contacts, node, 0);
        PrivacyTreeNode groups = new PrivacyTreeNode(Res.getString("privacy.node.groups"));
        groups.setisGroupNode(true);
        _model.insertNodeInto(groups, node, 0);

        for (PrivacyItem pI : plist.getPrivacyItems()) {
            if (pI.getType().equals(PrivacyItem.Type.jid)) {
                _model.insertNodeInto(new PrivacyTreeNode(pI), contacts, 0);
            } else if (pI.getType().equals(PrivacyItem.Type.group)) {
                _model.insertNodeInto(new PrivacyTreeNode(pI), groups, 0);
            }
        }

    }

    /**
     * Loads the PrivacyLists for the first time and adds them to the tree
     */
    private void loadPrivacyLists() {

        for (SparkPrivacyList list : _pManager.getPrivacyLists()) {
            addListNode(new PrivacyTreeNode(list), _top);
        }

        _tree.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int row = _tree.getClosestRowForLocation(e.getX(), e.getY());

                if (SwingUtilities.isRightMouseButton(e) && _tree.getSelectionCount() == 1) {
                    _tree.setSelectionRow(row);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    boolean found = false;
                    if (_tree.getSelectionRows() != null) {
                        for (int i : _tree.getSelectionRows()) {
                            if (i == row) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        _tree.setSelectionRow(row);
                    }
                }



                final PrivacyTreeNode node = (PrivacyTreeNode) _tree.getLastSelectedPathComponent();
                JPopupMenu menu = new JPopupMenu("Menu");
                if (node == null) {
                    return;
                }
                if (node.isLeaf() && !node.isStructureNode() && node.isPrivacyItem()) {
                    addMenuForLeaf(menu, node);
                }

                if (node.isStructureNode() && !node.isRoot()) {
                    addMenuForGroupNodes(menu, node);
                }

                if (node.isPrivacyList() || node.isRoot()) {

                    addMenuForListNodes(menu, node);
                }

                if (SwingUtilities.isRightMouseButton(e)) {

                    menu.show(_tree, e.getX(), e.getY());

                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

    }

    @Override
    public void listActivated(String listname) {
        _actList.setText(listname);       
        _actList.setEnabled(true);
        _actList.setIcon(SparkRes.getImageIcon("PRIVACY_DEACTIVATE_LIST"));
    }

    @Override
    public void listDeActivated(String listname) {
       if (_actList.getText().equals(listname))
       {
           _actList.setText(Res.getString("privacy.button.no.list.selected"));
           _actList.setEnabled(false);
           _actList.setIcon(null);
       }     
    }

    @Override
    public void listSetAsDefault(String listname) {
       _defList.setText(listname);
       _defList.setIcon(SparkRes.getImageIcon("PRIVACY_DEACTIVATE_LIST"));
       _defList.setEnabled(true);
    }

    @Override
    public void listRemovedAsDefault(String listname) {
        if (_defList.getText().equals(listname))
        {
            _defList.setText(Res.getString("privacy.button.no.list.selected"));
            _defList.setEnabled(false);
            _defList.setIcon(null);
        }
    }
}
