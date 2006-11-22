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

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.SparkRes;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Button UI for handling of rollover buttons.
 *
 * @author Derek DeMoro
 */
public class RolloverButton extends JPanel {

    private JLabel innerLabel = new JLabel();

    private List<ActionListener> listeners = new ArrayList<ActionListener>();

    /**
     * Create a new RolloverButton.
     */
    public RolloverButton() {
        decorate();
    }

    public RolloverButton(String text) {
        innerLabel.setText(text);
        decorate();
    }


    /**
     * Create a new RolloverButton.
     *
     * @param icon the icon to use on the button.
     */
    public RolloverButton(Icon icon) {
        innerLabel.setIcon(icon);
        decorate();
    }

    public void setIcon(Icon icon) {
        innerLabel.setIcon(icon);
    }

    /**
     * Create a new RolloverButton.
     *
     * @param text the button text.
     * @param icon the button icon.
     */
    public RolloverButton(String text, Icon icon) {
        setText(text);
        setIcon(icon);
        decorate();
    }

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    public void removeActionListener(ActionListener listener){
        listeners.remove(listener);
    }

    public void setText(String text) {
        innerLabel.setText(text);
    }

    public JLabel getInnerLabel() {
        return innerLabel;
    }

    public void setMnemonic(int mnemonic) {
        innerLabel.setDisplayedMnemonic(mnemonic);
    }

    public void setVerticalTextPosition(int pos) {
        innerLabel.setVerticalTextPosition(pos);
    }

    public void setHorizontalTextPosition(int pos) {
        innerLabel.setHorizontalTextPosition(pos);
    }

    public void addMouseListener(MouseListener listener){
        innerLabel.addMouseListener(listener);
    }

    /**
     * Decorates the button with the approriate UI configurations.
     */
    private void decorate() {
        setLayout(new GridBagLayout());
        add(innerLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 3, 1, 3), 0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, SparkRes.getImageIcon(SparkRes.BLANK_IMAGE)));

        innerLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));
                setOpaque(true);

            }

            public void mouseReleased(MouseEvent e) {
                setOpaque(false);

                for (ActionListener listener : listeners) {
                    listener.actionPerformed(getActionEvent());
                }
            }

            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));
            }

            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, SparkRes.getImageIcon(SparkRes.BLANK_IMAGE)));
            }
        });
    }

    public ActionEvent getActionEvent() {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
        return e;
    }


    public void paintComponent(Graphics g) {
        if (!isOpaque()) {
            super.paintComponent(g);
            return;
        }
        final Image backgroundImage = Default.getImageIcon(Default.SECONDARY_BACKGROUND_IMAGE).getImage();
        double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
        double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
        AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
        ((Graphics2D)g).drawImage(backgroundImage, xform, this);
    }


}