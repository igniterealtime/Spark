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
	private final ChatRoom chatRoom;

    private static final DataFlavor[] flavors = {DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor};

    public ChatRoomTransferHandler(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
	public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
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
        if (comp instanceof TranscriptWindow) {
            return new TranscriptWindowTransferable((TranscriptWindow) comp);
        }

        return null;
    }

    @Override
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
            catch (UnsupportedFlavorException | IOException e) {
                Log.error(e);
            }
        }
        else if (t.isDataFlavorSupported(flavors[1])) {
            try {
                Object o = t.getTransferData(flavors[1]);
                if (o instanceof String) {
                    // Otherwise fire files dropped event.
                    chatRoom.getChatInputEditor().insertText((String)o);
                    return true;
                }
            }
            catch (Exception e) {
                Log.error(e);
            }

        }
        return false;
    }

    public static class TranscriptWindowTransferable implements Transferable {

        private final TranscriptWindow item;

        public TranscriptWindowTransferable(TranscriptWindow item) {
            this.item = item;
        }

        // Returns supported flavors
        @Override
		public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.stringFlavor};
        }

        // Returns true if flavor is supported
        @Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.stringFlavor.equals(flavor);
        }

        // Returns Selected Text
        @Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!DataFlavor.stringFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return item.getSelectedText();
        }
    }
}
