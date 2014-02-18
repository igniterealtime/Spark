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

import javax.swing.Icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

/**
 * Displays a bar graph based on a percentage or relevance up to 100%.
 *
 * @author Derek DeMoro
 */
public class ProgressIcon implements Icon {
    private int percent;

    /**
     * Create new ProgressIcon.
     *
     * @param percent the percentage to display.
     */
    public ProgressIcon(int percent) {
        this.percent = percent;
    }

    public int getIconHeight() {
        return 10;
    }

    public int getIconWidth() {
        return percent;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.blue);
        g.fillRect(x, y, getIconWidth(), getIconHeight());//To change body of implemented methods use File | Settings | File Templates.
    }
}
