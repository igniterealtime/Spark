/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/

package com.jtattoo.plaf;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.plaf.UIResource;

/**
 * This class is a modified copy of the javax.swing.plaf.metal.MetalTitlePaneUI
 *
 * Class that manages a JLF awt.Window-descendant class's title bar.
 * <p>
 * This class assumes it will be created with a particular window
 * decoration style, and that if the style changes, a new one will
 * be created.
 *
 * @version 1.12 01/23/03
 * @author Terry Kellerman
 * @author Michael Hagen
 *
 * @since 1.4
 */
public class BaseTitlePane extends JComponent {

    public static final String PAINT_ACTIVE = "paintActive";
    public static final String ICONIFY = "Iconify";
    public static final String MAXIMIZE = "Maximize";
    public static final String CLOSE = "Close";
    protected PropertyChangeListener propertyChangeListener;
    protected Action closeAction;
    protected Action iconifyAction;
    protected Action restoreAction;
    protected Action maximizeAction;
    protected JMenuBar menuBar;
    protected JPanel customTitlePanel;
    protected JButton iconifyButton;
    protected JButton maxButton;
    protected JButton closeButton;
    protected Icon iconifyIcon;
    protected Icon maximizeIcon;
    protected Icon minimizeIcon;
    protected Icon closeIcon;
    protected WindowListener windowListener;
    protected Window window;
    protected JRootPane rootPane;
    protected BaseRootPaneUI rootPaneUI;
    protected int buttonsWidth;
    protected int state;
    // This flag is used to avoid a bug with OSX and java 1.7. The call to setExtendedState
    // with both flags ICONIFY and MAXIMIZED_BOTH throws an illegal state exception, so we
    // have to switch off the MAXIMIZED_BOTH flag in the iconify() method. If frame is deiconified
    // we use the wasMaximized flag to restore the maximized state.
    protected boolean wasMaximized;
    protected BufferedImage backgroundImage = null;
    protected float alphaValue = 0.85f;
    protected boolean useMaximizedBounds = true;

    public BaseTitlePane(JRootPane root, BaseRootPaneUI ui) {
        rootPane = root;
        rootPaneUI = ui;
        state = -1;
        wasMaximized = false;
        iconifyIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
        maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
        minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
        closeIcon = UIManager.getIcon("InternalFrame.closeIcon");

        installSubcomponents();
        installDefaults();
        setLayout(createLayout());
    }


    protected void uninstall() {
        uninstallListeners();
        window = null;
        removeAll();
    }

    protected void installListeners() {
        if (window != null) {
            windowListener = createWindowListener();
            window.addWindowListener(windowListener);
            propertyChangeListener = createWindowPropertyChangeListener();
            window.addPropertyChangeListener(propertyChangeListener);
        }
    }

    protected void uninstallListeners() {
        if (window != null) {
            window.removeWindowListener(windowListener);
            window.removePropertyChangeListener(propertyChangeListener);
        }
    }

    protected WindowListener createWindowListener() {
        return new WindowHandler();
    }

    protected PropertyChangeListener createWindowPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    public JRootPane getRootPane() {
        return rootPane;
    }

    protected Frame getFrame() {
        if (window instanceof Frame) {
            return (Frame) window;
        }
        return null;
    }
    
    protected Window getWindow() {
        return window;
    }

    protected int getWindowDecorationStyle() {
        return DecorationHelper.getWindowDecorationStyle(rootPane);
    }
    
    protected boolean isMacStyleWindowDecoration() {
        return AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn();
    }

    protected Image getFrameIconImage() {
        // try to find icon for dialog windows
        if (getFrame() == null && JTattooUtilities.getJavaVersion() >= 1.6) {
            java.util.List icons = getWindow().getIconImages();
            // No icon found ? search in window chain for an icon
            if (icons == null || icons.isEmpty()) {
                Window owner = getWindow().getOwner();
                while (owner != null) {
                    icons = owner.getIconImages();
                    // found ? return the icon
                    if (icons != null && !icons.isEmpty()) {
                        return (Image)(icons.get(0));
                    }
                    owner = owner.getOwner();
                }
            } else {
                return (Image)(icons.get(0));
            }
            // No icon found ?  return icon of the first frame
            if (icons == null || icons.isEmpty()) {
                if (Frame.getFrames() != null && Frame.getFrames().length > 0) {
                    return Frame.getFrames()[0].getIconImage();
                }
            }
            return null;
        } else {
            if (getFrame() != null) {
                return getFrame().getIconImage();
            }
        }
        return null;
    }

    public void addNotify() {
        super.addNotify();
        uninstallListeners();
        window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            if (window instanceof Frame) {
                setState(DecorationHelper.getExtendedState((Frame) window));
            } else {
                setState(0);
            }
            setActive(JTattooUtilities.isWindowActive(window));
            installListeners();
        }
    }

    public void removeNotify() {
        super.removeNotify();
        uninstallListeners();
        window = null;
    }

    protected void installSubcomponents() {
        createActions();
        createButtons();
        if (getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
            if (!isMacStyleWindowDecoration()) {
                createMenuBar();
                add(menuBar);
            }
            add(iconifyButton);
            add(maxButton);
        }
        add(closeButton);
    }

    protected void installDefaults() {
        setFont(UIManager.getFont("InternalFrame.titleFont"));
        if (rootPane.getClientProperty("customTitlePanel") instanceof JPanel) {
            setCustomizedTitlePanel((JPanel)rootPane.getClientProperty("customTitlePanel"));
        }
    }

    protected void uninstallDefaults() {
    }

    protected void createMenuBar() {
        menuBar = new SystemMenuBar();
        if (getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
            JMenu menu = new JMenu("   ");

            JMenuItem mi = menu.add(restoreAction);
            int mnemonic = getInt("MetalTitlePane.restoreMnemonic", -1);
            if (mnemonic != -1) {
                mi.setMnemonic(mnemonic);
            }
            mi = menu.add(iconifyAction);
            mnemonic = getInt("MetalTitlePane.iconifyMnemonic", -1);
            if (mnemonic != -1) {
                mi.setMnemonic(mnemonic);
            }

            if (DecorationHelper.isFrameStateSupported(Toolkit.getDefaultToolkit(), BaseRootPaneUI.MAXIMIZED_BOTH)) {
                mi = menu.add(maximizeAction);
                mnemonic = getInt("MetalTitlePane.maximizeMnemonic", -1);
                if (mnemonic != -1) {
                    mi.setMnemonic(mnemonic);
                }
            }
            menu.addSeparator();
            mi = menu.add(closeAction);
            mnemonic = getInt("MetalTitlePane.closeMnemonic", -1);
            if (mnemonic != -1) {
                mi.setMnemonic(mnemonic);
            }

            menuBar.add(menu);
        }
    }

    public void setCustomizedTitlePanel(JPanel panel) {
        if (customTitlePanel != null) {
            remove(customTitlePanel);
            customTitlePanel = null;
        }
        if (panel != null) {
            customTitlePanel = panel;
            add(customTitlePanel);
        }
        rootPane.putClientProperty("customTitlePanel", customTitlePanel);
        revalidate();
        repaint();
    }

    public void createButtons() {
        iconifyButton = new BaseTitleButton(iconifyAction, ICONIFY, iconifyIcon, 1.0f);
        maxButton = new BaseTitleButton(restoreAction, MAXIMIZE, maximizeIcon, 1.0f);
        closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 1.0f);
    }

    public LayoutManager createLayout() {
        return new TitlePaneLayout();
    }

    protected void close() {
        if (window != null) {
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        }
    }

    protected void iconify() {
        Frame frame = getFrame();
        if (frame != null) {
            if (JTattooUtilities.isMac() && JTattooUtilities.getJavaVersion() >= 1.7) {
                // Workarround to avoid a bug within OSX and Java 1.7
                DecorationHelper.setExtendedState(frame, state & ~BaseRootPaneUI.MAXIMIZED_BOTH | Frame.ICONIFIED);
            } else {
                DecorationHelper.setExtendedState(frame, state | Frame.ICONIFIED);
            }
        }
    }

    protected void maximize() {
        Frame frame = getFrame();
        if (frame != null) {
            PropertyChangeListener[] pcl = frame.getPropertyChangeListeners();
            for (int i = 0; i < pcl.length; i++) {
                pcl[i].propertyChange(new PropertyChangeEvent(this, "windowMaximize", Boolean.FALSE, Boolean.FALSE));
            }
            DecorationHelper.setExtendedState(frame, state | BaseRootPaneUI.MAXIMIZED_BOTH);
            for (int i = 0; i < pcl.length; i++) {
                pcl[i].propertyChange(new PropertyChangeEvent(this, "windowMaximized", Boolean.FALSE, Boolean.FALSE));
            }
        }
    }

    protected void restore() {
        Frame frame = getFrame();
        if (frame != null) {
            PropertyChangeListener[] pcl = frame.getPropertyChangeListeners();
            for (int i = 0; i < pcl.length; i++) {
                pcl[i].propertyChange(new PropertyChangeEvent(this, "windowRestore", Boolean.FALSE, Boolean.FALSE));
            }
            if ((state & Frame.ICONIFIED) != 0) {
                DecorationHelper.setExtendedState(frame, state & ~Frame.ICONIFIED);
            } else {
                DecorationHelper.setExtendedState(frame, state & ~BaseRootPaneUI.MAXIMIZED_BOTH);
            }
            for (int i = 0; i < pcl.length; i++) {
                pcl[i].propertyChange(new PropertyChangeEvent(this, "windowRestored", Boolean.FALSE, Boolean.FALSE));
            }
        }
    }

    protected void createActions() {
        closeAction = new CloseAction();
        iconifyAction = new IconifyAction();
        restoreAction = new RestoreAction();
        maximizeAction = new MaximizeAction();
    }

    static int getInt(Object key, int defaultValue) {
        Object value = UIManager.get(key);
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException nfe) {
            }
        }
        return defaultValue;
    }

    protected void setActive(boolean flag) {
        if (getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
            Boolean active = flag ? Boolean.TRUE : Boolean.FALSE;
            iconifyButton.putClientProperty(PAINT_ACTIVE, active);
            closeButton.putClientProperty(PAINT_ACTIVE, active);
            maxButton.putClientProperty(PAINT_ACTIVE, active);
        }
        getRootPane().repaint();
    }

    protected boolean isActive() {
        return (window == null) ? true : JTattooUtilities.isWindowActive(window);
    }

    protected boolean isLeftToRight() {
        return (window == null) ? getRootPane().getComponentOrientation().isLeftToRight() : window.getComponentOrientation().isLeftToRight();
    }

    public void setBackgroundImage(BufferedImage bgImage) {
        backgroundImage = bgImage;
    }

    public void setAlphaTransparency(float alpha) {
        alphaValue = alpha;
    }

    protected void setState(int state) {
        setState(state, false);
    }

    protected void setState(int state, boolean updateRegardless) {
        if (window != null && getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
            if (this.state == state && !updateRegardless) {
                return;
            }

            Frame frame = getFrame();
            if (frame != null) {

                if (((state & BaseRootPaneUI.MAXIMIZED_BOTH) != 0) && (rootPane.getBorder() == null || (rootPane.getBorder() instanceof UIResource)) && frame.isShowing()) {
                    rootPane.setBorder(null);
                } else if ((state & BaseRootPaneUI.MAXIMIZED_BOTH) == 0) {
                    rootPaneUI.installBorder(rootPane);
                }

                if (frame.isResizable()) {
                    if ((state & BaseRootPaneUI.MAXIMIZED_BOTH) != 0) {
                        updateMaxButton(restoreAction, minimizeIcon);
                        maximizeAction.setEnabled(false);
                        restoreAction.setEnabled(true);
                    } else {
                        updateMaxButton(maximizeAction, maximizeIcon);
                        maximizeAction.setEnabled(true);
                        restoreAction.setEnabled(false);
                    }
                    if (maxButton.getParent() == null || iconifyButton.getParent() == null) {
                        add(maxButton);
                        add(iconifyButton);
                        revalidate();
                        repaint();
                    }
                    maxButton.setText(null);
                } else {
                    maximizeAction.setEnabled(false);
                    restoreAction.setEnabled(false);
                    if (maxButton.getParent() != null) {
                        remove(maxButton);
                        revalidate();
                        repaint();
                    }
                }
            } else {
                // Not contained in a Frame
                maximizeAction.setEnabled(false);
                restoreAction.setEnabled(false);
                iconifyAction.setEnabled(false);
                remove(maxButton);
                remove(iconifyButton);
                revalidate();
                repaint();
            }
            closeAction.setEnabled(true);
            this.state = state;
        }
    }

    protected void updateMaxButton(Action action, Icon icon) {
        maxButton.setAction(action);
        maxButton.setIcon(icon);
    }

    protected int getHorSpacing() {
        return 3;
    }

    protected int getVerSpacing() {
        return 3;
    }
    
    protected boolean centerButtons() {
        return true;
    }

    protected String getTitle() {
        if (window instanceof Frame) {
            return ((Frame) window).getTitle();
        } else if (window instanceof Dialog) {
            return ((Dialog) window).getTitle();
        }
        return null;
    }

    public void paintBackground(Graphics g) {
        if (isActive()) {
            Graphics2D g2D = (Graphics2D) g;
            Composite savedComposite = g2D.getComposite();
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, null);
                AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue);
                g2D.setComposite(alpha);
            }
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, getWidth(), getHeight());
            g2D.setComposite(savedComposite);
        } else {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, getWidth(), getHeight());
        }
    }

    protected int paintIcon(Graphics g, int x, int y) {
        if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn() || (getWindow() instanceof JDialog)) {
            Image image = getFrameIconImage();
            if (image != null) {
                Graphics2D g2D = (Graphics2D)g;
                Object savedHint = g2D.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                }
                int h = getHeight();
                int ih = image.getHeight(null);
                int iw = image.getWidth(null);
                if (ih <= h) {
                    g2D.drawImage(image, x, (h - ih) / 2, iw, ih, null);
                } else {
                    double fac = (double)iw / (double)ih;
                    ih = h;
                    iw = (int)(fac * (double)ih);
                    g2D.drawImage(image, x, 0, iw, ih, null);
                }
                if (savedHint != null) {
                    g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, savedHint);
                }
                return iw + 4;
            }
        }
        return 0;
    }
    
    public void paintText(Graphics g, int x, int y, String title) {
        x += paintIcon(g, x, y);
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
        }
        JTattooUtilities.drawString(rootPane, g, title, x, y);
    }

    public void paintComponent(Graphics g) {
        if (getFrame() != null) {
            setState(DecorationHelper.getExtendedState(getFrame()));
        }

        paintBackground(g);

        boolean leftToRight = isLeftToRight();
        int width = getWidth();
        int height = getHeight();
        int titleWidth = width - buttonsWidth - 4;
        int xOffset = leftToRight ? 2 : width - 2;
        if (menuBar != null) {
            int mw = menuBar.getWidth() + 2;
            xOffset += leftToRight ? mw : -mw;
            titleWidth -= height;
        }

        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        String frameTitle = JTattooUtilities.getClippedText(getTitle(), fm, titleWidth);
        int titleLength = fm.stringWidth(frameTitle);
        int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent() - 1;
        if (!leftToRight) {
            xOffset -= titleLength;
        }
        if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
            xOffset = Math.max(buttonsWidth + 5, (width - titleLength) / 2);
        }
        paintText(g, xOffset, yOffset, frameTitle);
    }

    protected class CloseAction extends AbstractAction {

        public CloseAction() {
            super(UIManager.getString("MetalTitlePane.closeTitle"));
        }

        public void actionPerformed(ActionEvent e) {
            close();
        }
    }

    protected class IconifyAction extends AbstractAction {

        public IconifyAction() {
            super(UIManager.getString("MetalTitlePane.iconifyTitle"));
        }

        public void actionPerformed(ActionEvent e) {
            iconify();
        }
    }

    protected class RestoreAction extends AbstractAction {

        public RestoreAction() {
            super(UIManager.getString("MetalTitlePane.restoreTitle"));
        }

        public void actionPerformed(ActionEvent e) {
            restore();
        }
    }

    protected class MaximizeAction extends AbstractAction {

        public MaximizeAction() {
            super(UIManager.getString("MetalTitlePane.maximizeTitle"));
        }

        public void actionPerformed(ActionEvent e) {
            maximize();
        }
    }

//-----------------------------------------------------------------------------------------------
    protected class SystemMenuBar extends JMenuBar {

        public SystemMenuBar() {
            setOpaque(false);
        }
        
        public void paint(Graphics g) {
            Image image = getFrameIconImage();
            if (image != null) {
                Graphics2D g2D = (Graphics2D)g;
                Object savedHint = g2D.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
                g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int x = 0;
                int y = 0;
                int iw = image.getWidth(null);
                int ih = image.getHeight(null);
                if (ih > getHeight()) {
                    double scale = (double)(getHeight() - 2) / (double)ih;
                    iw = (int)(scale * iw);
                    ih = (int)(scale * ih);
                } else {
                    y = (getHeight() - ih) / 2;
                }
                g2D.drawImage(image, x, y, iw, ih, null);
                if (savedHint != null) {
                    g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, savedHint);
                }
            } else {
                Icon icon = UIManager.getIcon("InternalFrame.icon");
                if (icon != null) {
                    icon.paintIcon(this, g, 2, 2);
                }
            }
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        protected int computeHeight() {
            FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
            return fm.getHeight() + 6;
        }

        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            Image image = getFrameIconImage();
            if (image != null) {
                int iw = image.getWidth(null);
                int ih = image.getHeight(null);
                int th = computeHeight();
                if (ih > th) {
                    double scale = (double)th / (double)ih;
                    iw = (int)(scale * iw);
                    ih = (int)(scale * ih);
                }
                return new Dimension(Math.max(iw, size.width), Math.max(ih, size.height));
            } else {
                return size;
            }
        }
    }

//-----------------------------------------------------------------------------------------------
    protected class TitlePaneLayout implements LayoutManager {

        public void addLayoutComponent(String name, Component c) {
        }

        public void removeLayoutComponent(Component c) {
        }

        public Dimension preferredLayoutSize(Container c) {
            int height = computeHeight();
            return new Dimension(height, height);
        }

        public Dimension minimumLayoutSize(Container c) {
            return preferredLayoutSize(c);
        }

        protected int computeHeight() {
            FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
            return fm.getHeight() + 6;
        }

        public void layoutContainer(Container c) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                layoutMacStyle(c);
            } else {
                layoutDefault(c);
            }
        }

        public void layoutDefault(Container c) {
            boolean leftToRight = isLeftToRight();

            int spacing = getHorSpacing();
            int w = getWidth();
            int h = getHeight();

            // assumes all buttons have the same dimensions these dimensions include the borders
            int btnHeight = h - getVerSpacing();
            int btnWidth = btnHeight;


            if (menuBar != null) {
                int mw = menuBar.getPreferredSize().width;
                int mh = menuBar.getPreferredSize().height;
                if (leftToRight) {
                    menuBar.setBounds(2, (h - mh) / 2, mw, mh);
                } else {
                    menuBar.setBounds(getWidth() - mw, (h - mh) / 2, mw, mh);
                }
            }
            
            int x = leftToRight ? w - spacing : 0;
            int y = Math.max(0, ((h - btnHeight) / 2) - 1);
            
            if (closeButton != null) {
                x += leftToRight ? -btnWidth : spacing;
                closeButton.setBounds(x, y, btnWidth, btnHeight);
                if (!leftToRight) {
                    x += btnWidth;
                }
            }

            if ((maxButton != null) && (maxButton.getParent() != null)) {
                if (DecorationHelper.isFrameStateSupported(Toolkit.getDefaultToolkit(), BaseRootPaneUI.MAXIMIZED_BOTH)) {
                    x += leftToRight ? -spacing - btnWidth : spacing;
                    maxButton.setBounds(x, y, btnWidth, btnHeight);
                    if (!leftToRight) {
                        x += btnWidth;
                    }
                }
            }

            if ((iconifyButton != null) && (iconifyButton.getParent() != null)) {
                x += leftToRight ? -spacing - btnWidth : spacing;
                iconifyButton.setBounds(x, y, btnWidth, btnHeight);
                if (!leftToRight) {
                    x += btnWidth;
                }
            }
            
            buttonsWidth = leftToRight ? w - x : x;

            if (customTitlePanel != null) {
                int maxWidth = w - buttonsWidth - spacing - 20;
                if (menuBar != null) {
                    maxWidth -= menuBar.getPreferredSize().width;
                    maxWidth -= spacing;
                }
                int cpw = Math.min(maxWidth, customTitlePanel.getPreferredSize().width);
                int cph = h;
                int cpx = leftToRight ? w - buttonsWidth - cpw : buttonsWidth;
                int cpy = 0;
                customTitlePanel.setBounds(cpx, cpy, cpw, cph);
                buttonsWidth += customTitlePanel.getPreferredSize().width;
            }
        }

        public void layoutMacStyle(Container c) {
            int spacing = getHorSpacing();
            int w = getWidth();
            int h = getHeight();

            // assumes all buttons have the same dimensions these dimensions include the borders
            int btnHeight = h - getVerSpacing() - 1;
            int btnWidth = btnHeight;

            int x = 2;
            int y = centerButtons() ? Math.max(0, ((h - btnHeight) / 2) - 1) : 0;

            if (closeButton != null) {
                closeButton.setBounds(x, y, btnWidth, btnHeight);
                x += btnWidth + spacing;
            }
            if ((iconifyButton != null) && (iconifyButton.getParent() != null)) {
                iconifyButton.setBounds(x, y, btnWidth, btnHeight);
                x += btnWidth + spacing;
            }
            if ((maxButton != null) && (maxButton.getParent() != null)) {
                if (DecorationHelper.isFrameStateSupported(Toolkit.getDefaultToolkit(), BaseRootPaneUI.MAXIMIZED_BOTH)) {
                    maxButton.setBounds(x, y, btnWidth, btnHeight);
                    x += btnWidth + spacing;
                }
            }

            buttonsWidth = x;

            if (customTitlePanel != null) {
                int cpx = buttonsWidth + 5;
                int cpy = 0;
                int cpw = customTitlePanel.getPreferredSize().width;
                int cph = h;
                customTitlePanel.setBounds(cpx, cpy, cpw, cph);
                buttonsWidth += cpw + 5;
            }
        }
    }

//-----------------------------------------------------------------------------------------------
    protected class PropertyChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent pce) {
            String name = pce.getPropertyName();
            // Frame.state isn't currently bound.
            if ("resizable".equals(name) || "state".equals(name)) {
                Frame frame = getFrame();
                if (frame != null) {
                    setState(DecorationHelper.getExtendedState(frame), true);
                }
                if ("resizable".equals(name)) {
                    getRootPane().repaint();
                }
            } else if ("title".equals(name)) {
                repaint();
            } else if ("componentOrientation".equals(name)) {
                revalidate();
                repaint();
            // a call to setMaximizedBounds may cause an invalid frame size on multi screen environments
            // see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6699851
            // try and error to avoid the setMaximizedBounds bug
            } else if (!JTattooUtilities.isMac() && useMaximizedBounds && "windowMaximize".equals(name)) {
                Frame frame = getFrame();
                if (frame != null) {
                    GraphicsConfiguration gc = frame.getGraphicsConfiguration();
                    Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
                    Rectangle screenBounds = gc.getBounds();
                    int x = Math.max(0, screenInsets.left);
                    int y = Math.max(0, screenInsets.top);
                    int w = screenBounds.width - (screenInsets.left + screenInsets.right);
                    int h = screenBounds.height - (screenInsets.top + screenInsets.bottom);
                    // Keep taskbar visible
                    frame.setMaximizedBounds(new Rectangle(x, y, w, h));
                }
            } else if (!JTattooUtilities.isMac() && useMaximizedBounds && "windowMaximized".equals(name)) {
                Frame frame = getFrame();
                if (frame != null) {
                    GraphicsConfiguration gc = frame.getGraphicsConfiguration();
                    Rectangle screenBounds = gc.getBounds();
                    if (frame.getSize().width > screenBounds.width || frame.getSize().height > screenBounds.height) {
                        useMaximizedBounds = false;
                        frame.setMaximizedBounds(null);
                        restore();
                        maximize();
                    }
                }
            } else if (!JTattooUtilities.isMac() && "windowMoved".equals(name)) {
                useMaximizedBounds = true;
            }

            if (JTattooUtilities.isMac() && JTattooUtilities.getJavaVersion() >= 1.7) {
                if ("windowRestored".equals(name)) {
                    wasMaximized = false;
                } else if ("windowMaximized".equals(name)) {
                    wasMaximized = true;
                }
            }
        }
    }

//-----------------------------------------------------------------------------------------------
    protected class WindowHandler extends WindowAdapter {

        public void windowDeiconified(WindowEvent e) {
            if (JTattooUtilities.isMac() && JTattooUtilities.getJavaVersion() >= 1.7 && wasMaximized) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        maximize();
                    }
                });
            }
        }
        
        public void windowActivated(WindowEvent ev) {
            setActive(true);
        }

        public void windowDeactivated(WindowEvent ev) {
            setActive(false);
        }
    }
}
