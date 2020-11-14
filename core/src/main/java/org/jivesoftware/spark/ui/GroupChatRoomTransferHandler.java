/**
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
package org.jivesoftware.spark.ui;

import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.BareJid;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Handler for drag and dropping of files unto a ChatWindow.
 */
public class GroupChatRoomTransferHandler extends TransferHandler {
	private static final long serialVersionUID = -192689038331188379L;
	private final GroupChatRoom groupChatRoom;
    private static final DataFlavor[] flavors = {DataFlavor.imageFlavor};

    /**
     * Creates a transfer handler for the given GroupChatRoom
     *
     * @param chatRoom the GroupChatRoom.
     */
    public GroupChatRoomTransferHandler(GroupChatRoom chatRoom) {
        this.groupChatRoom = chatRoom;
    }

    @Override
	public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }


    @Override
	public boolean canImport(JComponent comp, DataFlavor[] flavor) {
        for (DataFlavor dataFlavor : flavor) {
            for (DataFlavor value : flavors) {
                if (dataFlavor.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
	protected void exportDone(JComponent c, Transferable data, int action) {
    }


    @Override
	public Transferable createTransferable(JComponent comp) {
        return null;
    }

    @Override
	public boolean importData(JComponent comp, Transferable t) {
        if (t.isDataFlavorSupported(flavors[0])) {
            try {
                final Object o = t.getTransferData(flavors[0]);
                if (o instanceof ContactItem) {
                    // Invite User
                    final ContactItem contactItem = (ContactItem)o;
                    BareJid jid = contactItem.getJid();
                    groupChatRoom.inviteUser(jid.asEntityBareJidOrThrow(), "Please join the conference room.");

                    return true;
                }
            }
            catch (Exception e) {
                Log.error(e);
            }
        }

        return false;
    }
}
