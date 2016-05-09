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
package org.jivesoftware.spark.util;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

/**
 * WindowsFileSystemView
 *
 * @author Andrew Selkirk
 * @version 1.0
 */
public class WindowsFileSystemView extends FileSystemView {

    // TODO WindowsFileSystemView
    //-------------------------------------------------------------
    // Initialization ---------------------------------------------
    //-------------------------------------------------------------

    /**
     * Constructor WindowsFileSystemView
     */
    public WindowsFileSystemView() {
        // 
    } // WindowsFileSystemView()

    //-------------------------------------------------------------
    // Methods ----------------------------------------------------
    //-------------------------------------------------------------

    /**
     * isRoot
     *
     * @param value0 
     * @return boolean
     */
    public boolean isRoot(File value0) {
        return false; // 
    } // isRoot()

    /**
     * createNewFolder
     *
     * @param value0 
     * @throws IOException 
     * @return File
     */
    public File createNewFolder(File value0) throws IOException {
        return null; // 
    } // createNewFolder()

    /**
     * isHiddenFile
     *
     * @param value0 
     * @return boolean
     */
    public boolean isHiddenFile(File value0) {
        return false; // 
    } // isHiddenFile()

    /**
     * getRoots
     *
     * @return File[]
     */
    public File[] getRoots() {
        return null; // 
    } // getRoots()


} // WindowsFileSystemView


