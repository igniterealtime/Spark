/*
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

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.iqversion.VersionManager;
import org.jivesoftware.smackx.time.packet.Time;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.resource.Res;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;


public class JabberVersion implements Plugin {

    @Override
	public void initialize() {

        // Populate Smack's Version Manager with Sparks version information.
        VersionManager.getInstanceFor( SparkManager.getConnection() ).setVersion(
            JiveInfo.getName(),
            JiveInfo.getVersion(),
            JiveInfo.getOS()
        );

        // Create IQ Filter
        StanzaFilter packetFilter = new StanzaTypeFilter(IQ.class);
        SparkManager.getConnection().addAsyncStanzaListener( stanza -> {
            IQ iq = (IQ)stanza;

            try
            {
                if (iq instanceof Time && iq.getType() == IQ.Type.get) {
                    Time time = Time.builder(iq.getStanzaId())
                        .ofType(IQ.Type.result)
                        .set(ZonedDateTime.now()).build();
                    time.setFrom(iq.getTo());
                    time.setTo(iq.getFrom());

                    // Send Time
                    SparkManager.getConnection().sendStanza(time);
                }
            }
            catch ( SmackException.NotConnectedException e )
            {
                Log.warning( "Unable to answer request: " + stanza, e);
            }
        }, packetFilter);

        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control F11"), "viewClient");
        contactList.addContextMenuListener(new ContextMenuListener() {
            @Override
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

					@Override
					public void actionPerformed(ActionEvent e) {
                        viewClient();
                    }
                };

                versionRequest.putValue(Action.NAME, Res.getString("menuitem.view.client.version"));
                popup.add(versionRequest);
            }

            @Override
			public void poppingDown(JPopupMenu popup) {

            }

            @Override
			public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });


        contactList.getActionMap().put("viewClient", new AbstractAction("viewClient") {
			private static final long serialVersionUID = 8282301357403753561L;

			@Override
			public void actionPerformed(ActionEvent evt) {
                viewClient();
            }
        });


    }

    private void viewClient() {
        final JTextField field = new JTextField();
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        Collection<ContactItem> selectedUsers = contactList.getSelectedUsers();
        if (selectedUsers.size() == 1) {
            final ContactItem item = (ContactItem)selectedUsers.toArray()[0];
            final Presence presence = item.getPresence();
            final String jid;
            try {
                 jid = presence.getFrom().toString();
            }catch (NullPointerException e){
                JOptionPane.showMessageDialog(field,
                    item.getAlias() + " " +Res.getString("user.has.signed.off"),
                    Res.getString("title.notification"),
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            SwingWorker worker = new SwingWorker() {
                @Override
				public Object construct() {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e1) {
                        // Nothing to do
                    }
                    return jid;
                }

                @Override
				public void finished() {
                    VersionViewer.viewVersion(jid);
                }
            };
            worker.start();
        }
    }

    @Override
	public void shutdown() {

    }

    @Override
	public boolean canShutDown() {
        return false;
    }

    @Override
	public void uninstall() {
        // Do nothing.
    }
}
