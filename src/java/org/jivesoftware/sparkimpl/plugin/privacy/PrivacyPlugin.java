/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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
package org.jivesoftware.sparkimpl.plugin.privacy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PrivacyItem;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

/**
 *
 * @author Zolotarev Konstantin
 */
public class PrivacyPlugin implements Plugin {

    @Override
    public void initialize() {
        PrivacyManager.getInstance(); // Call for Init PrivacyLists
        SwingWorker thread = new SwingWorker() {
            @Override
            public Object construct() {
                try {
                    // Let's try and avoid any timing issues with the PrivacyManager presence.
                    Thread.sleep(5000);
                }
                catch (Exception e) {
                    Log.error(e);
                    return false;
                }

                return true;
            }

            @Override
            public void finished() {
                Boolean privacyListExist = (Boolean)get();
                if (!privacyListExist) {
                    return;
                }
                addMenuItemToContactItems();
                scanContactList();
                // add functional
            }
        };

        thread.start();
        
        
    }

    @Override
    public void shutdown() {
        // @todo remove Privacy List
    }

    @Override
    public boolean canShutDown() {
        return true;
    }

    @Override
    public void uninstall() {
        
    }

    /**
     * Set images to all blocked items 
     */
    protected void scanContactList() {
        PrivacyManager manager = PrivacyManager.getInstance();
        ArrayList<PrivacyItem> items = (ArrayList<PrivacyItem>) manager.getBlackList().getBlockedItems();
        for (PrivacyItem privacyItem : items ) {
            if ( privacyItem.getValue() != null && !privacyItem.getValue().isEmpty() ) {
                    PrivacyManager.getInstance().setBlockedIconToContact(privacyItem.getValue());
            }
        }
        
    }

    /**
     * Add block menu item to contact popupmenu
     */
    protected void addMenuItemToContactItems() {
        //SparkManager.getChatManager().addContactItemHandler(this);
        SparkManager.getContactList().addContextMenuListener(new ContextMenuListener() {

            @Override
            public void poppingUp(Object object, JPopupMenu popup) {
                final ContactItem item = (ContactItem) object;
                JMenuItem blockMenu;
                
                if ( PrivacyManager.getInstance().getBlackList().isBlockedItem(item.getJID()) ) {
                    blockMenu = new JMenuItem(Res.getString("menuitem.unblock.contact"), SparkRes.getImageIcon(SparkRes.UNBLOCK_CONTACT_16x16));
                    blockMenu.addActionListener(new ActionListener() { //unblock contact

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            if ( item != null ) {
                                try {
                                    PrivacyManager.getInstance().getBlackList().removeBlockedItem(((ContactItem) item).getJID()); //Add to block list}
                                } catch (XMPPException ex) {
                                    Log.error(ex); // @todo handle error
                                }
                            }
                        }
                    });
                } else {
                    blockMenu = new JMenuItem(Res.getString("menuitem.block.contact"), SparkRes.getImageIcon(SparkRes.BLOCK_CONTACT_16x16));
                    blockMenu.addActionListener(new ActionListener() { //Block contact

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            if ( item != null ) {
                                try {
                                    PrivacyManager.getInstance().getBlackList().addBlockedItem(item.getJID()); //Add to block list
                                } catch (XMPPException ex) {
                                    Log.error(ex); // @todo handle error
                                }
                            }
                        }
                    });
                }              
                
                popup.add(blockMenu);
            }

            @Override
            public void poppingDown(JPopupMenu popup) {
                
            }

            @Override
            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });


    }
//
//    @Override
//    public boolean handlePresence(ContactItem item, Presence presence) {
//        scanContactList();
//        return true;
//    }
//
//    @Override
//    public Icon getIcon(String jid) {
//        return null;
//    }
//
//    @Override
//    public Icon getTabIcon(Presence presence) {
//        return null;
//    }
//
//    @Override
//    public boolean handleDoubleClick(ContactItem item) {
//        return false;
//    }

}
