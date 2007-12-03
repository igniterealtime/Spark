/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import org.jivesoftware.spark.util.log.Log;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Allows for dragging of a file from the label to the desktop.
 */
public class FileDragLabel extends JLabel implements DropTargetListener, DragSourceListener, DragGestureListener {
    final DragSource dragSource = DragSource.getDefaultDragSource();

    private File file;

    public FileDragLabel() {
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent) {
    }

    public void dragEnter(DragSourceDragEvent DragSourceDragEvent) {
    }

    public void dragExit(DragSourceEvent DragSourceEvent) {
    }

    public void dragOver(DragSourceDragEvent DragSourceDragEvent) {
    }

    public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent) {
    }

    public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
        dropTargetDragEvent.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public void dragExit(DropTargetEvent dropTargetEvent) {
    }

    public void dragOver(DropTargetDragEvent dropTargetDragEvent) {
    }

    public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {
    }

    public synchronized void drop(DropTargetDropEvent dropTargetDropEvent) {
        try {
            final Transferable transferable = dropTargetDropEvent.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY);
                dropTargetDropEvent.getDropTargetContext().dropComplete(true);
            }
            else {
                dropTargetDropEvent.rejectDrop();
            }
        }
        catch (Exception ex) {
            Log.error(ex);
            dropTargetDropEvent.rejectDrop();
        }
    }

    public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
        if (file == null) {
            // Nothing selected, nothing to drag
            getToolkit().beep();
        }
        else {
            FileSelection transferable = new FileSelection(file);
            dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this);
        }
    }

    private class FileSelection extends Vector<File> implements Transferable {
        final static int FILE = 0;
        final static int STRING = 1;
        final static int PLAIN = 2;
        DataFlavor flavors[] = {DataFlavor.javaFileListFlavor,
            DataFlavor.stringFlavor,
            DataFlavor.plainTextFlavor};

        public FileSelection(File file) {
            addElement(file);
        }


        public synchronized DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }


        public boolean isDataFlavorSupported(DataFlavor flavor) {
            boolean b = false;
            b = b | flavor.equals(flavors[FILE]);
            b |= flavor.equals(flavors[STRING]);
            b |= flavor.equals(flavors[PLAIN]);
            return (b);
        }


        public synchronized Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
            if (flavor.equals(flavors[FILE])) {
                return this;
            }
            else if (flavor.equals(flavors[PLAIN])) {
                return new StringReader(file.getAbsolutePath());
            }
            else if (flavor.equals(flavors[STRING])) {
                return (file.getAbsolutePath());
            }
            else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

    public static void main(String args[]) {
        JFrame f = new JFrame();
        FileDragLabel p = new FileDragLabel();
        f.add(p);
        f.setVisible(true);
    }

}
