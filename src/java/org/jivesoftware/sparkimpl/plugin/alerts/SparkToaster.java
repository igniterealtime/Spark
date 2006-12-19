/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.alerts;

/**
 * SparkToaster is an improvement of Java Toaster which is a java utility class for your swing applications
 * that show an animate box coming from the bottom of your screen
 * with a notification message and/or an associated image
 * (like msn online/offline notifications).
 *
 * Toaster panel in windows system follow the taskbar; So if
 * the taskbar is into the bottom the panel coming from the bottom
 * and if the taskbar is on the top then the panel coming from the top.
 *
 * This is a simple example of utilization:
 *
 * import com.nitido.utils.toaster.*;
 * import javax.swing.*;
 *
 */


import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.util.log.Log;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

/**
 * Class to show tosters in multiplatform
 *
 * @author daniele piras
 */
public class SparkToaster {

    /**
     * The default Hand cursor.
     */
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    /**
     * The default Text Cursor.
     */
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    // Width of the toster
    private int toasterWidth = 200;

    // Height of the toster
    private int toasterHeight = 185;

    // Step for the toaster
    private int step = 20;

    // Step time
    private int stepTime = 20;

    // Show time
    private int displayTime = 3000;

    // Current number of toaster...
    private int currentNumberOfToaster = 0;

    // Last opened toaster
    private int maxToaster = 0;

    // Max number of toasters for the sceen
    private int maxToasterInSceen;

    // Font used to display message
    private Font font;

    // Color for border
    private Color borderColor;

    // Color for toaster
    private Color toasterColor;

    // Set message color
    private Color messageColor;

    // Set the margin
    int margin;

    // Flag that indicate if use alwaysOnTop or not.
    // method always on top start only SINCE JDK 5 !
    boolean useAlwaysOnTop = true;

    private static final long serialVersionUID = 1L;

    private String title;

    private Border border;


    private Action customAction;

    private Window window;

    private JPanel mainPanel = new JPanel();

    private JLabel titleLabel;
    private JLabel iconLabel;

    /**
     * Constructor to initialized toaster component...
     */
    public SparkToaster() {
        // Set default font...
        font = new Font("Dialog", Font.PLAIN, 11);

        titleLabel = new JLabel();
        iconLabel = new JLabel();

        // Border color
        borderColor = new Color(245, 153, 15);
        toasterColor = Color.WHITE;
        messageColor = Color.BLACK;
        useAlwaysOnTop = true;
    }

    /**
     * Class that rappresent a single toaster
     *
     * @author daniele piras
     */
    class SingleToaster extends javax.swing.JWindow {
        private static final long serialVersionUID = 1L;

        // Label to store Icon

        // Text area for the message
        private JTextArea message = new JTextArea();

        /**
         * Simple costructor that initialized components...
         */
        public SingleToaster() {
            initComponents();
        }

        /**
         * Function to initialized components
         */
        private void initComponents() {
            message.setFont(getToasterMessageFont());


            mainPanel.setBackground(Color.white);
            message.setOpaque(false);
            mainPanel.setLayout(new GridBagLayout());
            message.setMargin(new Insets(2, 2, 2, 2));
            message.setLineWrap(true);
            message.setWrapStyleWord(true);

            message.setForeground(getMessageColor());
            titleLabel.setText(getTitle());
            titleLabel.setForeground(new Color(87, 166, 211));
            titleLabel.setFont(new Font("Dialog", Font.BOLD, 13));

            RolloverButton closeButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.CLOSE_IMAGE));

            mainPanel.add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 0, 0, 0), 0, 0));
            mainPanel.add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 5, 0, 0), 0, 0));
            mainPanel.add(closeButton, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(1, 0, 0, 0), 0, 0));

            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    dispose();
                }
            });

            if (border != null) {
                mainPanel.setBorder(border);
            }

            message.setForeground(Color.BLACK);

            message.setOpaque(false);

            getContentPane().add(mainPanel);


            mainPanel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (customAction != null) {
                        customAction.actionPerformed(null);
                    }

                    setVisible(false);
                    dispose();
                }

                public void mouseEntered(MouseEvent e) {
                    message.setCursor(HAND_CURSOR);
                }

                public void mouseExited(MouseEvent e) {
                    message.setCursor(DEFAULT_CURSOR);
                }
            });


            pack();
            setSize(toasterWidth, toasterHeight);
            mainPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        }


        /**
         * Start toaster animation...
         */
        public void animate() {
            (new Animation(this)).start();
        }

    }

    /**
     * Class that manage the animation
     */
    class Animation extends Thread {
        SingleToaster toaster;

        public Animation(SingleToaster toaster) {
            this.toaster = toaster;
        }


        /**
         * Animate vertically the toaster. The toaster could be moved from bottom
         * to upper or to upper to bottom
         *
         * @param posx
         * @throws InterruptedException
         */
        protected void animateVertically(int posx, int fromY, int toY) throws InterruptedException {

            toaster.setLocation(posx, fromY);
            if (toY < fromY) {
                for (int i = fromY; i > toY; i -= step) {
                    toaster.setLocation(posx, i);
                    Thread.sleep(stepTime);
                }
            }
            else {
                for (int i = fromY; i < toY; i += step) {
                    toaster.setLocation(posx, i);
                    Thread.sleep(stepTime);
                }
            }
            toaster.setLocation(posx, toY);
        }

        public void run() {
            try {
                boolean animateFromBottom = true;
                GraphicsEnvironment ge = GraphicsEnvironment
                    .getLocalGraphicsEnvironment();
                Rectangle screenRect = ge.getMaximumWindowBounds();

                int screenHeight = (int)screenRect.height;

                int startYPosition;
                int stopYPosition;

                if (screenRect.y > 0) {
                    animateFromBottom = false; // Animate from top!
                }

                maxToasterInSceen = screenHeight / toasterHeight;


                int posx = (int)screenRect.width - toasterWidth - 1;

                toaster.setLocation(posx, screenHeight);
                toaster.setFocusableWindowState(false);
                toaster.setVisible(true);
                if (useAlwaysOnTop) {
                    toaster.setAlwaysOnTop(true);
                }

                toaster.setFocusableWindowState(true);

                if (animateFromBottom) {
                    startYPosition = screenHeight;
                    stopYPosition = startYPosition - toasterHeight - 1;
                    if (currentNumberOfToaster > 0) {
                        stopYPosition = stopYPosition - (maxToaster % maxToasterInSceen * toasterHeight);
                    }
                    else {
                        maxToaster = 0;
                    }
                }
                else {
                    startYPosition = screenRect.y - toasterHeight;
                    stopYPosition = screenRect.y;

                    if (currentNumberOfToaster > 0) {
                        stopYPosition = stopYPosition + (maxToaster % maxToasterInSceen * toasterHeight);
                    }
                    else {
                        maxToaster = 0;
                    }
                }

                currentNumberOfToaster++;
                maxToaster++;


                animateVertically(posx, startYPosition, stopYPosition);
                Thread.sleep(displayTime);
                animateVertically(posx, stopYPosition, startYPosition);

                currentNumberOfToaster--;
                toaster.setVisible(false);
                toaster.dispose();
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
    }


    /**
     * Show a toaster with the specified message and the associated icon.
     */
    public void showToaster(Icon icon, String msg) {
        SingleToaster singleToaster = new SingleToaster();
        final JScrollPane pane = new JScrollPane(singleToaster.message);
        pane.setOpaque(false);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.getViewport().setBackground(Color.white);
        mainPanel.add(pane, new GridBagConstraints(1, 2, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));


        if (icon != null) {
            iconLabel.setIcon(icon);
        }
        singleToaster.message.setText(msg);
        singleToaster.message.setCaretPosition(0);
        singleToaster.animate();
        window = singleToaster;
    }

    public void showToaster(Icon icon) {
        SingleToaster singleToaster = new SingleToaster();
        if (icon != null) {
            iconLabel.setIcon(icon);
        }
        singleToaster.animate();
        window = singleToaster;
    }

    /**
     * Show a toaster with the specified message.
     */
    public void showToaster(String msg) {
        showToaster(null, msg);
    }

    /**
     * @return Returns the font
     */
    public Font getToasterMessageFont() {
        // TODO Auto-generated method stub
        return font;
    }

    /**
     * Set the font for the message
     */
    public void setToasterMessageFont(Font f) {
        font = f;
    }


    /**
     * @return Returns the borderColor.
     */
    public Color getBorderColor() {
        return borderColor;
    }


    /**
     * @param borderColor The borderColor to set.
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }


    /**
     * @return Returns the displayTime.
     */
    public int getDisplayTime() {
        return displayTime;
    }


    /**
     * @param displayTime The displayTime to set.
     */
    public void setDisplayTime(int displayTime) {
        this.displayTime = displayTime;
    }


    /**
     * @return Returns the margin.
     */
    public int getMargin() {
        return margin;
    }


    /**
     * @param margin The margin to set.
     */
    public void setMargin(int margin) {
        this.margin = margin;
    }


    /**
     * @return Returns the messageColor.
     */
    public Color getMessageColor() {
        return messageColor;
    }


    /**
     * @param messageColor The messageColor to set.
     */
    public void setMessageColor(Color messageColor) {
        this.messageColor = messageColor;
    }


    /**
     * @return Returns the step.
     */
    public int getStep() {
        return step;
    }


    /**
     * @param step The step to set.
     */
    public void setStep(int step) {
        this.step = step;
    }


    /**
     * @return Returns the stepTime.
     */
    public int getStepTime() {
        return stepTime;
    }


    /**
     * @param stepTime The stepTime to set.
     */
    public void setStepTime(int stepTime) {
        this.stepTime = stepTime;
    }


    /**
     * @return Returns the toasterColor.
     */
    public Color getToasterColor() {
        return toasterColor;
    }


    /**
     * @param toasterColor The toasterColor to set.
     */
    public void setToasterColor(Color toasterColor) {
        this.toasterColor = toasterColor;
    }


    /**
     * @return Returns the toasterHeight.
     */
    public int getToasterHeight() {
        return toasterHeight;
    }


    /**
     * @param toasterHeight The toasterHeight to set.
     */
    public void setToasterHeight(int toasterHeight) {
        this.toasterHeight = toasterHeight;
    }


    /**
     * @return Returns the toasterWidth.
     */
    public int getToasterWidth() {
        return toasterWidth;
    }


    /**
     * @param toasterWidth The toasterWidth to set.
     */
    public void setToasterWidth(int toasterWidth) {
        this.toasterWidth = toasterWidth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    public void setCustomAction(Action action) {
        this.customAction = action;
    }

    public void setComponent(Component comp) {
        mainPanel.add(comp, new GridBagConstraints(1, 2, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
    }

    public void close() {
        if (window != null) {
            window.dispose();
        }
    }

    public Window getWindow() {
        return window;
    }

    public void hideTitle() {
        titleLabel.setVisible(false);
        iconLabel.setVisible(false);
    }

    /**
     * Simple Example...
     */
    public static void main(String[] args) {
        SparkToaster toaster = new SparkToaster();
        toaster.setDisplayTime(30000);
        toaster.setBorder(BorderFactory.createBevelBorder(0));
        toaster.setTitle(Res.getString("title.notification"));
        toaster.showToaster("JDLFJSLKDFJLSJFLSJFLSJDLFJLSDFJLJSDFLJSLDFJS");
    }

}

