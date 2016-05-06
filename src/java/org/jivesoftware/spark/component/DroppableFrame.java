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
package org.jivesoftware.spark.component;

import org.jivesoftware.spark.util.log.Log;

import javax.swing.JFrame;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
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
import java.util.List;

/**
 * Creates a DroppableFrame. A droppable frame allows for DnD of file objects from the OS
 * onto the actual frame via <code>File</code> object.
 */
public abstract class DroppableFrame extends JFrame implements DropTargetListener, DragSourceListener, DragGestureListener {
    private static final long serialVersionUID = -4250762326200861757L;

    /**
     * Creates an Instance of the Droppable Frame.
     */
    protected DroppableFrame() {
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
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

    public void drop(DropTargetDropEvent dropTargetDropEvent) {
        try {
            Transferable transferable = dropTargetDropEvent.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                for (File aFileList : fileList) {
                    File file = aFileList;
                    if (file.isFile()) {
                        fileDropped(file);
                    }

                    if (file.isDirectory()) {
                        directoryDropped(file);
                    }
                }
                dropTargetDropEvent.getDropTargetContext().dropComplete(true);
            }
            else {
                dropTargetDropEvent.rejectDrop();
            }
        }
        catch (Exception io) {
            Log.error(io);
            dropTargetDropEvent.rejectDrop();
        }

    }

    public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {

    }

    /**
     * Notified when a file has been dropped onto the frame.
     *
     * @param file the file that has been dropped.
     */
    public abstract void fileDropped(File file);

    /**
     * Notified when a directory has been dropped onto the frame.
     *
     * @param file the directory that has been dropped.
     */
    public abstract void directoryDropped(File file);
}