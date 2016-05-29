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

import javax.swing.JComponent;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class JMultilineLabel extends JComponent {
    private static final long serialVersionUID = 3061233171993075054L;
    private String text;
    private Insets margin = new Insets(1, 1, 1, 1);
    private int maxWidth = Integer.MAX_VALUE;
    private boolean justify;
    private final FontRenderContext frc = new FontRenderContext(null, false, false);

    private void morph() {
        revalidate();
        repaint();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String old = this.text;
        this.text = text;
        firePropertyChange("text", old, this.text);
        if ((old == null) ? text != null : !old.equals(text))
            morph();
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        if (maxWidth <= 0)
            throw new IllegalArgumentException();
        int old = this.maxWidth;
        this.maxWidth = maxWidth;
        firePropertyChange("maxWidth", old, this.maxWidth);
        if (old != this.maxWidth)
            morph();
    }

    public boolean isJustified() {
        return justify;
    }

    public void setJustified(boolean justify) {
        boolean old = this.justify;
        this.justify = justify;
        firePropertyChange("justified", old, this.justify);
        if (old != this.justify)
            repaint();
    }

    public Dimension getPreferredSize() {
        return paintOrGetSize(null, getMaxWidth());
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintOrGetSize((Graphics2D)g, getWidth());
    }

    private Dimension paintOrGetSize(Graphics2D g, int width) {
        Insets insets = getInsets();
        width -= insets.left + insets.right + margin.left + margin.right;
        float w = insets.left + insets.right + margin.left + margin.right;
        float x = insets.left + margin.left, y = insets.top + margin.top;
        if (width > 0 && text != null && text.length() > 0) {
            AttributedString as = new AttributedString(getText());
            as.addAttribute(TextAttribute.FONT, getFont());
            AttributedCharacterIterator aci = as.getIterator();
            LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
            float max = 0;
            while (lbm.getPosition() < aci.getEndIndex()) {
                TextLayout textLayout = lbm.nextLayout(width);
                if (g != null && isJustified() && textLayout.getVisibleAdvance() > 0.80 * width)
                    textLayout = textLayout.getJustifiedLayout(width);
                if (g != null)
                    textLayout.draw(g, x, y + textLayout.getAscent());
                y += textLayout.getDescent() + textLayout.getLeading() + textLayout.getAscent();
                max = Math.max(max, textLayout.getVisibleAdvance());
            }
            w += max;
        }
        return new Dimension((int)Math.ceil(w), (int)Math.ceil(y) + insets.bottom + margin.bottom);
    }
}

