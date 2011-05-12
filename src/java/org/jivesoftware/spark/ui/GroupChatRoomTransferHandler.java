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
package org.jivesoftware.spark.ui;

import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.log.Log;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Handler for drag and dropping of files unto a ChatWindow.
 */
public class GroupChatRoomTransferHandler extends TransferHandler {
	private static final long serialVersionUID = -192689038331188379L;
	private GroupChatRoom groupChatRoom;
    private static final DataFlavor flavors[] = {DataFlavor.imageFlavor};

    /**
     * Creates a transfer handler for the given GroupChatRoom
     *
     * @param chatRoom the GroupChatRoom.
     */
    public GroupChatRoomTransferHandler(GroupChatRoom chatRoom) {
        this.groupChatRoom = chatRoom;
    }

    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }


    public boolean canImport(JComponent comp, DataFlavor flavor[]) {
        for (int i = 0, n = flavor.length; i < n; i++) {
            for (int j = 0, m = flavors.length; j < m; j++) {
                if (flavor[i].equals(flavors[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void exportDone(JComponent c, Transferable data, int action) {
    }


    public Transferable createTransferable(JComponent comp) {
        return null;
    }

    public boolean importData(JComponent comp, Transferable t) {
        if (t.isDataFlavorSupported(flavors[0])) {
            try {
                final Object o = t.getTransferData(flavors[0]);
                if (o instanceof ContactItem) {
                    // Invite User
                    final ContactItem contactItem = (ContactItem)o;
                    String jid = contactItem.getJID();
                    groupChatRoom.inviteUser(jid, "Please join the conference room.");

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
