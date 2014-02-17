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

import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;

import javax.swing.JViewport;
import javax.swing.JComponent;

/**
 * Allows for background and foreground image handling of text components.
 * 
 */
public class ScrollPaneWatermark extends JViewport {
    private static final long serialVersionUID = 316017203726925013L;
    private Image foregroundImage;

    public void setBackgroundImage(Image backgroundImage) {
        Rectangle rect = new Rectangle(0, 0, backgroundImage.getWidth(null), backgroundImage.getHeight(null));
        try {
            new TexturePaint(GraphicUtils.convert(backgroundImage), rect);
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    public void setForegroundImage(Image image) {
        this.foregroundImage = image;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(foregroundImage != null){
            g.drawImage(foregroundImage, getWidth() - foregroundImage.getWidth(null), 0, null);
        }
    }



    public void setView(JComponent view){
        view.setOpaque(false);
        super.setView(view);
    }
}
