/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.util;

/********************************************************************
 * Copyright (c) Open Java Extensions, Andrew Selkirk  LGPL License *
 ********************************************************************/

// Imports

import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * WindowsFileSystemView
 *
 * @author Andrew Selkirk
 * @version 1.0
 */
public class WindowsFileSystemView extends FileSystemView {

    //-------------------------------------------------------------
    // Variables --------------------------------------------------
    //-------------------------------------------------------------

    /**
     * noArgs
     */
    private static final Object[] noArgs = null; // TODO

    /**
     * noArgTypes
     */
    private static final Class[] noArgTypes = null; // TODO

    /**
     * listRootsMethod
     */
    private static Method listRootsMethod = null; // TODO

    /**
     * listRootsMethodChecked
     */
    private static boolean listRootsMethodChecked = false; // TODO

    //-------------------------------------------------------------
    // Initialization ---------------------------------------------
    //-------------------------------------------------------------

    /**
     * Constructor WindowsFileSystemView
     */
    public WindowsFileSystemView() {
        // TODO
    } // WindowsFileSystemView()

    //-------------------------------------------------------------
    // Methods ----------------------------------------------------
    //-------------------------------------------------------------

    /**
     * isRoot
     *
     * @param value0 TODO
     * @returns boolean
     */
    public boolean isRoot(File value0) {
        return false; // TODO
    } // isRoot()

    /**
     * createNewFolder
     *
     * @param value0 TODO
     * @throws IOException TODO
     * @returns File
     */
    public File createNewFolder(File value0) throws IOException {
        return null; // TODO
    } // createNewFolder()

    /**
     * isHiddenFile
     *
     * @param value0 TODO
     * @returns boolean
     */
    public boolean isHiddenFile(File value0) {
        return false; // TODO
    } // isHiddenFile()

    /**
     * getRoots
     *
     * @returns File[]
     */
    public File[] getRoots() {
        return null; // TODO
    } // getRoots()


} // WindowsFileSystemView


