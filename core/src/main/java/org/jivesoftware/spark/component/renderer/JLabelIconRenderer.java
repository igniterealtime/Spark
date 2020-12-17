/**
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
package org.jivesoftware.spark.component.renderer;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;

/**
 * The <code>JLabelIconRenderer</code> is the an implementation of
 * ListCellRenderer to add icons w/ associated text in JComboBox and JList.
 *
 * @author Derek DeMoro
 */
public class JLabelIconRenderer extends JLabel implements ListCellRenderer<Object> {

    private static final long serialVersionUID = -694803906607554443L;
    private int mindex = -1;

    /**
     * Construct Default JLabelIconRenderer.
     */
    public JLabelIconRenderer() {
        setOpaque(true);
        this.setVerticalTextPosition(JLabel.CENTER);
        this.setHorizontalTextPosition(JLabel.RIGHT);
        this.setHorizontalAlignment(JLabel.LEFT);
    }

    @Override
    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        this.setVerticalTextPosition(JLabel.CENTER);
        this.setHorizontalTextPosition(JLabel.RIGHT);

        list.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent me) {
                Point p = new Point(me.getX(), me.getY());
                int index = list.locationToIndex(p);
                if (index != mindex) {
                    mindex = index;
                    list.repaint();
                }
            }
        });
        Color backgroundColor = mindex == index ? list.getSelectionBackground() : list.getBackground();

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                mindex = -1;
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(backgroundColor);
                    setForeground(list.getForeground());
                }
                list.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

                if (isSelected) {
                    setBackground(list.getSelectionBackground().darker());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(backgroundColor);
                    setForeground(list.getForeground());
                }
            }

        });

        if (isSelected) {
            setBackground(list.getSelectionBackground().darker());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(backgroundColor);
            setForeground(list.getForeground());
        }
        JLabel label = (JLabel) value;
        setIcon(label.getIcon());
        setText(label.getText());
        setBorder(new EmptyBorder(0, 15, 0, 15));
        setIconTextGap(5);

        return this;
    }
}
