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
package org.jivesoftware.sparkimpl.plugin.jabber;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.Time;
import org.jivesoftware.smackx.packet.Version;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.resource.Res;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Date;


public class JabberVersion implements Plugin {

    public void initialize() {
        // Create IQ Filter
        PacketFilter packetFilter = new PacketTypeFilter(IQ.class);
        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                IQ iq = (IQ)packet;

                // Handle Version Request
                if (iq instanceof Version && iq.getType() == IQ.Type.GET) {
                    // Send Version
                    Version version = new Version();
                    version.setName(JiveInfo.getName());

                    version.setOs(JiveInfo.getOS());
                    version.setVersion(JiveInfo.getVersion());

                    // Send back as a reply
                    version.setPacketID(iq.getPacketID());
                    version.setType(IQ.Type.RESULT);
                    version.setTo(iq.getFrom());
                    version.setFrom(iq.getTo());
                    SparkManager.getConnection().sendPacket(version);
                }
                // Send time
                else if (iq instanceof Time && iq.getType() == IQ.Type.GET) {
                    Time time = new Time();
                    time.setPacketID(iq.getPacketID());
                    time.setFrom(iq.getTo());
                    time.setTo(iq.getFrom());
                    time.setTime(new Date());
                    time.setType(IQ.Type.RESULT);

                    // Send Time
                    SparkManager.getConnection().sendPacket(time);
                }
            }
        }, packetFilter);

        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control F11"), "viewClient");
        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(final Object component, JPopupMenu popup) {
                if (!(component instanceof ContactItem)) {
                    return;
                }

                ContactItem contactItem = (ContactItem)component;
                if(contactItem.getPresence() == null){
                    return;
                }

                Action versionRequest = new AbstractAction() {
					private static final long serialVersionUID = -5619737417315441711L;

					public void actionPerformed(ActionEvent e) {
                        viewClient();
                    }
                };

                versionRequest.putValue(Action.NAME, Res.getString("menuitem.view.client.version"));
                popup.add(versionRequest);
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });


        contactList.getActionMap().put("viewClient", new AbstractAction("viewClient") {
			private static final long serialVersionUID = 8282301357403753561L;

			public void actionPerformed(ActionEvent evt) {
                viewClient();
            }
        });


    }

    private void viewClient() {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        Collection<ContactItem> selectedUsers = contactList.getSelectedUsers();
        if (selectedUsers.size() == 1) {
            ContactItem item = (ContactItem)selectedUsers.toArray()[0];
            Presence presence = item.getPresence();
            final String jid = presence.getFrom();
            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e1) {
                        // Nothing to do
                    }
                    return jid;
                }

                public void finished() {
                    VersionViewer.viewVersion(jid);
                }
            };
            worker.start();
        }
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
        // Do nothing.
    }
}
