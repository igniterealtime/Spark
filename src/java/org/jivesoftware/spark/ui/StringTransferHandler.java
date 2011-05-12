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

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Used for String Drag and Drop functionality.
 */
public abstract class StringTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 4783002180033288533L;

	protected abstract String exportString(JComponent c);

    protected abstract void importString(JComponent c, String str);

    protected abstract void cleanup(JComponent c, boolean remove);

    protected Transferable createTransferable(JComponent c) {
        return new StringSelection(exportString(c));
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                String str = (String)t.getTransferData(DataFlavor.stringFlavor);
                importString(c, str);
                return true;
            }
            catch (UnsupportedFlavorException ufe) {
                // Nothing to do
            }
            catch (IOException ioe) {
                // Nothing to do
            }
        }
        return false;
    }

    protected void exportDone(JComponent c, Transferable data, int action) {
        cleanup(c, action == MOVE);
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (DataFlavor.stringFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
}