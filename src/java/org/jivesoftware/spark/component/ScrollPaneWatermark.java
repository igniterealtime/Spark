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
