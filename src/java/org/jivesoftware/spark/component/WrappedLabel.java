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

import javax.swing.JTextArea;

import java.awt.Dimension;

/**
 * Creates a simple Wrappable label to display Multi-Line Text.
 *
 * @author Derek DeMoro
 */
public class WrappedLabel extends JTextArea {
    private static final long serialVersionUID = 177528477205607705L;

    /**
     * Create a simple Wrappable label.
     */
    public WrappedLabel() {
        this.setEditable(false);
        this.setWrapStyleWord(true);
        this.setLineWrap(true);
        this.setOpaque(false);
    }

    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }
}