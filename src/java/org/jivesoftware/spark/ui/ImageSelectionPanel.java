/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Allows for selection of panel images.
 */
public class ImageSelectionPanel extends JPanel {
	private static final long serialVersionUID = -2832575315956252059L;
	private BufferedImage image;
	private Rectangle clip;

    public ImageSelectionPanel() {
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        clip = new Rectangle();
        setBackground(Color.black);
        Selector selector = new Selector(this);
        addMouseListener(selector);
        addMouseMotionListener(selector);
    }

    protected void paintComponent(Graphics g) {
        if (image == null) {
            return;
        }

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int x = (w - imageWidth) / 2;
        int y = (h - imageHeight) / 2;
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        g2.drawRenderedImage(image, at);
        g2.setPaint(Color.LIGHT_GRAY);
        g2.draw(clip);
    }

    public void setClipFrame(Point start, Point end) {
        clip.setFrameFromDiagonal(start, end);
        repaint();
    }

    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    public Rectangle getClip() {
        return clip;
    }

    public void clear() {
        image = null;
    }

}

class Selector extends MouseInputAdapter {
    ImageSelectionPanel selectionPanel;
    Point start;
    boolean dragging,
            isClipSet;

    public Selector(ImageSelectionPanel isp) {
        selectionPanel = isp;
        dragging = false;
        isClipSet = false;
    }

    public void mousePressed(MouseEvent e) {
        if (isClipSet)             // clear existing clip
        {
            selectionPanel.setClipFrame(start, start);
            isClipSet = false;
        }
        else                      // or start new clip
        {
            start = e.getPoint();
            dragging = true;
            isClipSet = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging)
            selectionPanel.setClipFrame(start, e.getPoint());
    }


}
