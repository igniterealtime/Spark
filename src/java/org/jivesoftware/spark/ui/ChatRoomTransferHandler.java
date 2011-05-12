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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.jivesoftware.spark.util.log.Log;

/**
 * Handler for drag and dropping of files unto a ChatWindow.
 */
public class ChatRoomTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 6941570710627039031L;
	private ChatRoom chatRoom;

    private static final DataFlavor flavors[] = {DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor};

    public ChatRoomTransferHandler(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
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
        if (comp instanceof TranscriptWindow) {
            return new TranscriptWindowTransferable((TranscriptWindow)comp);
        }

        return null;
    }

    public boolean importData(JComponent comp, Transferable t) {
        if (t.isDataFlavorSupported(flavors[0])) {
            try {
                Object o = t.getTransferData(flavors[0]);
                if (o instanceof Collection) {
                    Collection<File> files = (Collection<File>)o;

                    // Otherwise fire files dropped event.
                    chatRoom.fireFileDropListeners(files);
                    return true;
                }
            }
            catch (UnsupportedFlavorException e) {
                Log.error(e);
            }
            catch (IOException e) {
                Log.error(e);
            }
        }
        else if (t.isDataFlavorSupported(flavors[1])) {
            try {
                Object o = t.getTransferData(flavors[1]);
                if (o instanceof String) {
                    // Otherwise fire files dropped event.
                    chatRoom.getChatInputEditor().insert((String)o);
                    return true;
                }
            }
            catch (Exception e) {
                Log.error(e);
            }

        }
        return false;
    }

    public class TranscriptWindowTransferable implements Transferable {

        private TranscriptWindow item;

        public TranscriptWindowTransferable(TranscriptWindow item) {
            this.item = item;
        }

        // Returns supported flavors
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.stringFlavor};
        }

        // Returns true if flavor is supported
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.stringFlavor.equals(flavor);
        }

        // Returns Selected Text
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.stringFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return item.getSelectedText();
        }
    }
}
