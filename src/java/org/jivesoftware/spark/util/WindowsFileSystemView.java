/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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
     * @return boolean
     */
    public boolean isRoot(File value0) {
        return false; // TODO
    } // isRoot()

    /**
     * createNewFolder
     *
     * @param value0 TODO
     * @throws IOException TODO
     * @return File
     */
    public File createNewFolder(File value0) throws IOException {
        return null; // TODO
    } // createNewFolder()

    /**
     * isHiddenFile
     *
     * @param value0 TODO
     * @return boolean
     */
    public boolean isHiddenFile(File value0) {
        return false; // TODO
    } // isHiddenFile()

    /**
     * getRoots
     *
     * @return File[]
     */
    public File[] getRoots() {
        return null; // TODO
    } // getRoots()


} // WindowsFileSystemView


