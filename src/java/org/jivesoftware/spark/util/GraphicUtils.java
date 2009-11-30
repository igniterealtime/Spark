/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.log.Log;

/**
 * <code>GraphicsUtils</code> class defines common user-interface related utility
 * functions.
 */
public final class GraphicUtils {
    private static final Insets HIGHLIGHT_INSETS = new Insets(1, 1, 1, 1);
    public static final Color SELECTION_COLOR = new java.awt.Color(166, 202, 240);
    public static final Color TOOLTIP_COLOR = new java.awt.Color(166, 202, 240);

    protected final static Component component = new Component() {
		private static final long serialVersionUID = -7556405112141454291L;
    };
    protected final static MediaTracker tracker = new MediaTracker(component);

    private static Map<String,Image> imageCache = new HashMap<String,Image>();

    /**
     * The default Hand cursor.
     */
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    /**
     * The default Text Cursor.
     */
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    private GraphicUtils() {
    }


    /**
     * Sets the location of the specified window so that it is centered on screen.
     *
     * @param window The window to be centered.
     */
    public static void centerWindowOnScreen(Window window) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension size = window.getSize();

        if (size.height > screenSize.height) {
            size.height = screenSize.height;
        }

        if (size.width > screenSize.width) {
            size.width = screenSize.width;
        }

        window.setLocation((screenSize.width - size.width) / 2,
            (screenSize.height - size.height) / 2);
    }

    /**
     * Draws a single-line highlight border rectangle.
     *
     * @param g         The graphics context to use for drawing.
     * @param x         The left edge of the border.
     * @param y         The top edge of the border.
     * @param width     The width of the border.
     * @param height    The height of the border.
     * @param raised    <code>true</code> if the border is to be drawn raised,
     *                  <code>false</code> if lowered.
     * @param shadow    The shadow color for the border.
     * @param highlight The highlight color for the border.
     * @see javax.swing.border.EtchedBorder
     */
    public static void drawHighlightBorder(Graphics g, int x, int y,
                                           int width, int height, boolean raised,
                                           Color shadow, Color highlight) {
        final Color oldColor = g.getColor();
        g.translate(x, y);

        g.setColor(raised ? highlight : shadow);
        g.drawLine(0, 0, width - 2, 0);
        g.drawLine(0, 1, 0, height - 2);

        g.setColor(raised ? shadow : highlight);
        g.drawLine(width - 1, 0, width - 1, height - 1);
        g.drawLine(0, height - 1, width - 2, height - 1);

        g.translate(-x, -y);
        g.setColor(oldColor);
    }

    /**
     * Return the amount of space taken up by a highlight border drawn by
     * <code>drawHighlightBorder()</code>.
     *
     * @return The <code>Insets</code> needed for the highlight border.
     * @see #drawHighlightBorder
     */
    public static Insets getHighlightBorderInsets() {
        return HIGHLIGHT_INSETS;
    }

    public static ImageIcon createImageIcon(Image image) {
        if (image == null) {
            return null;
        }

        synchronized (tracker) {
            tracker.addImage(image, 0);
            try {
                tracker.waitForID(0, 0);
            }
            catch (InterruptedException e) {
                System.out.println("INTERRUPTED while loading Image");
            }
            tracker.removeImage(image, 0);
        }

        return new ImageIcon(image);
    }

    /**
     * Returns a point where the given popup menu should be shown. The
     * point is calculated by adjusting the X and Y coordinates from the
     * given mouse event so that the popup menu will not be clipped by
     * the screen boundaries.
     *
     * @param popup the popup menu
     * @param event the mouse event
     * @return the point where the popup menu should be shown
     */
    public static Point getPopupMenuShowPoint(JPopupMenu popup, MouseEvent event) {
        Component source = (Component)event.getSource();
        Point topLeftSource = source.getLocationOnScreen();
        Point ptRet = getPopupMenuShowPoint(popup,
            topLeftSource.x + event.getX(),
            topLeftSource.y + event.getY());
        ptRet.translate(-topLeftSource.x, -topLeftSource.y);
        return ptRet;
    }

    /**
     * Returns a point where the given popup menu should be shown. The
     * point is calculated by adjusting the X and Y coordinates so that
     * the popup menu will not be clipped by the screen boundaries.
     *
     * @param popup the popup menu
     * @param x     the x position in screen coordinate
     * @param y     the y position in screen coordinates
     * @return the point where the popup menu should be shown in screen
     *         coordinates
     */
    public static Point getPopupMenuShowPoint(JPopupMenu popup, int x, int y) {
        Dimension sizeMenu = popup.getPreferredSize();
        Point bottomRightMenu = new Point(x + sizeMenu.width, y + sizeMenu.height);

        Rectangle[] screensBounds = getScreenBounds();
        int n = screensBounds.length;
        for (int i = 0; i < n; i++) {
            Rectangle screenBounds = screensBounds[i];
            if (screenBounds.x <= x && x <= (screenBounds.x + screenBounds.width)) {
                Dimension sizeScreen = screenBounds.getSize();
                sizeScreen.height -= 32;  // Hack to help prevent menu being clipped by Windows/Linux/Solaris Taskbar.

                int xOffset = 0;
                if (bottomRightMenu.x > (screenBounds.x + sizeScreen.width))
                    xOffset = -sizeMenu.width;

                int yOffset = 0;
                if (bottomRightMenu.y > (screenBounds.y + sizeScreen.height))
                    yOffset = sizeScreen.height - bottomRightMenu.y;

                return new Point(x + xOffset, y + yOffset);
            }
        }

        return new Point(x, y); // ? that would mean that the top left point was not on any screen.
    }

    /**
     * Centers the window over a component (usually another window).
     * The window must already have been sized.
     * @param window Window to center.
     * @param over Component to center over.
     */
    public static void centerWindowOnComponent(Window window, Component over) {
        if ((over == null) || !over.isShowing()) {
            centerWindowOnScreen(window);
            return;
        }


        Point parentLocation = over.getLocationOnScreen();
        Dimension parentSize = over.getSize();
        Dimension size = window.getSize();

        // Center it.
        int x = parentLocation.x + (parentSize.width - size.width) / 2;
        int y = parentLocation.y + (parentSize.height - size.height) / 2;

        // Now, make sure it's onscreen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // This doesn't actually work on the Mac, where the screen
        // doesn't necessarily start at 0,0
        if (x + size.width > screenSize.width)
            x = screenSize.width - size.width;

        if (x < 0)
            x = 0;

        if (y + size.height > screenSize.height)
            y = screenSize.height - size.height;

        if (y < 0)
            y = 0;

        window.setLocation(x, y);
    }

    /**
     * @param c Component to check on.
     * @return returns true if the component of one of its child has the focus
     */
    public static boolean isAncestorOfFocusedComponent(Component c) {
        if (c.hasFocus()) {
            return true;
        }
        else {
            if (c instanceof Container) {
                Container cont = (Container)c;
                int n = cont.getComponentCount();
                for (int i = 0; i < n; i++) {
                    Component child = cont.getComponent(i);
                    if (isAncestorOfFocusedComponent(child))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the first component in the tree of <code>c</code> that can accept
     * the focus.
     *
     * @param c the root of the component hierarchy to search
     * @see #focusComponentOrChild
     * @deprecated replaced by {@link #getFocusableComponentOrChild(Component,boolean)}
     * @return Component that was focused on.
     */
    public static Component getFocusableComponentOrChild(Component c) {
        return getFocusableComponentOrChild(c, false);
    }

    /**
     * Returns the first component in the tree of <code>c</code> that can accept
     * the focus.
     *
     * @param c       the root of the component hierarchy to search
     * @param deepest if <code>deepest</code> is true the method will return the first and deepest component that can accept the
     *                focus.  For example, if both a child and its parent are focusable and <code>deepest</code> is true, the child is
     *                returned.
     * @see #focusComponentOrChild
     * @return Component that was focused on.
     */
    public static Component getFocusableComponentOrChild(Component c, boolean deepest) {
        if (c != null && c.isEnabled() && c.isVisible()) {
            if (c instanceof Container) {
                Container cont = (Container)c;

                if (!deepest) { // first one is a good one
                    if (c instanceof JComponent) {
                        JComponent jc = (JComponent)c;
                        if (jc.isRequestFocusEnabled()) {
                            return jc;
                        }
                    }
                }

                int n = cont.getComponentCount();
                for (int i = 0; i < n; i++) {
                    Component child = cont.getComponent(i);
                    Component focused = getFocusableComponentOrChild(child, deepest);
                    if (focused != null) {
                        return focused;
                    }
                }

                if (c instanceof JComponent) {
                    if (deepest) {
                        JComponent jc = (JComponent)c;
                        if (jc.isRequestFocusEnabled()) {
                            return jc;
                        }
                    }
                }
                else {
                    return c;
                }
            }
        }

        return null;
    }

    /**
     * Puts the focus on the first component in the tree of <code>c</code> that
     * can accept the focus.
     *
     * @see #getFocusableComponentOrChild
     * @param c Component to focus on.
     * @return Component that was focused on.
     */
    public static Component focusComponentOrChild(Component c) {
        return focusComponentOrChild(c, false);
    }

    /**
     * Puts the focus on the first component in the tree of <code>c</code> that
     * can accept the focus.
     *
     * @param c       the root of the component hierarchy to search
     * @param deepest if <code>deepest</code> is true the method will focus the first and deepest component that can
     *                accept the focus.
     *                For example, if both a child and its parent are focusable and <code>deepest</code> is true, the child is focused.
     * @see #getFocusableComponentOrChild
     * @return Component that was focused on.
     */
    public static Component focusComponentOrChild(Component c, boolean deepest) {
        final Component focusable = getFocusableComponentOrChild(c, deepest);
        if (focusable != null) {
            focusable.requestFocus();
        }
        return focusable;
    }

    /**
     * Loads an {@link Image} named <code>imageName</code> as a resource
     * relative to the Class <code>cls</code>.  If the <code>Image</code> can
     * not be loaded, then <code>null</code> is returned.  Images loaded here
     * will be added to an internal cache based upon the full {@link URL} to
     * their location.
     * <p/>
     * <em>This method replaces legacy code from JDeveloper 3.x and earlier.</em>
     *
     * @see Class#getResource(String)
     * @see Toolkit#createImage(URL)
     * @param imageName Name of the resource to load.
     * @param cls Class to pull resource from.
     * @return Image loaded from resource.
     */
    public static Image loadFromResource(String imageName, Class cls) {
        try {
            final URL url = cls.getResource(imageName);

            if (url == null) {
                return null;
            }

            Image image = imageCache.get(url.toString());

            if (image == null) {
                image = Toolkit.getDefaultToolkit().createImage(url);
                imageCache.put(url.toString(), image);
            }

            return image;
        }
        catch (Exception e) {
            Log.error(e);
        }

        return null;
    }

    public static Rectangle[] getScreenBounds() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] screenDevices = graphicsEnvironment.getScreenDevices();
        Rectangle[] screenBounds = new Rectangle[screenDevices.length];
        for (int i = 0; i < screenDevices.length; i++) {
            GraphicsDevice screenDevice = screenDevices[i];
            final GraphicsConfiguration defaultConfiguration = screenDevice.getDefaultConfiguration();
            screenBounds[i] = defaultConfiguration.getBounds();
        }

        return screenBounds;
    }


    public static void makeSameSize(JComponent... comps) {
        if (comps.length == 0) {
            return;
        }

        int max = 0;
        for (JComponent comp1 : comps) {
            int w = comp1.getPreferredSize().width;
            max = w > max ? w : max;
        }

        Dimension dim = new Dimension(max, comps[0].getPreferredSize().height);
        for (JComponent comp : comps) {
            comp.setPreferredSize(dim);
        }
    }

    /**
     * Return the hexidecimal color from a java.awt.Color
     *
     * @param c Color to convert.
     * @return hexadecimal string
     */
    public static String toHTMLColor(Color c) {
        int color = c.getRGB();
        color |= 0xff000000;
        String s = Integer.toHexString(color);
        return s.substring(2);
    }

    public static String createToolTip(String text, int width) {
        final String htmlColor = toHTMLColor(TOOLTIP_COLOR);
        return "<html><table width=" + width + " bgColor=" + htmlColor + "><tr><td>" + text + "</td></tr></table></table>";
    }

    public static String createToolTip(String text) {
        final String htmlColor = toHTMLColor(TOOLTIP_COLOR);
        return "<html><table  bgColor=" + htmlColor + "><tr><td>" + text + "</td></tr></table></table>";
    }

    public static String getHighlightedWords(String text, String query) {
        final StringTokenizer tkn = new StringTokenizer(query, " ");
        int tokenCount = tkn.countTokens();
        String[] words = new String[tokenCount];
        for (int j = 0; j < tokenCount; j++) {
            String queryWord = tkn.nextToken();
            words[j] = queryWord;
        }

        String highlightedWords;
        try {
            highlightedWords = StringUtils.highlightWords(text, words, "<font style=background-color:yellow;font-weight:bold;>", "</font>");
        }
        catch (Exception e) {
            highlightedWords = text;
        }


        return highlightedWords;
    }

    public static ImageIcon createShadowPicture(Image buf) {
        buf = removeTransparency(buf);

        BufferedImage splash;

        JLabel label = new JLabel();
        int width = buf.getWidth(null);
        int height = buf.getHeight(null);
        int extra = 4;

        splash = new BufferedImage(width + extra, height + extra, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)splash.getGraphics();


        BufferedImage shadow = new BufferedImage(width + extra, height + extra, BufferedImage.TYPE_INT_ARGB);
        Graphics g = shadow.getGraphics();
        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.3f));
        g.fillRoundRect(0, 0, width, height, 12, 12);

        g2.drawImage(shadow, getBlurOp(7), 0, 0);
        g2.drawImage(buf, 0, 0, label);
        return new ImageIcon(splash);
    }

    public static BufferedImage removeTransparency(Image image) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi2.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bi2;
    }

    public static Image toImage(BufferedImage bufferedImage) {
        return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
    }

    private static ConvolveOp getBlurOp(int size) {
        float[] data = new float[size * size];
        float value = 1 / (float)(size * size);
        for (int i = 0; i < data.length; i++) {
            data[i] = value;
        }
        return new ConvolveOp(new Kernel(size, size, data));
    }

    public static BufferedImage convert(Image im) throws InterruptedException, IOException {
        load(im);
        BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public static void load(Image image) throws InterruptedException, IOException {
        MediaTracker tracker = new MediaTracker(new Label()); //any component will do
        tracker.addImage(image, 0);
        tracker.waitForID(0);
        if (tracker.isErrorID(0))
            throw new IOException("error loading image");
    }

    public static byte[] getBytesFromImage(Image image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(convert(image), "PNG", baos);
        }
        catch (IOException e) {
            Log.error(e);
        }
        catch (InterruptedException e) {
            Log.error(e);
        }

        return baos.toByteArray();
    }


    /**
     * Returns a scaled down image if the height or width is smaller than
     * the image size.
     *
     * @param icon      the image icon.
     * @param newHeight the preferred height.
     * @param newWidth  the preferred width.
     * @return the icon.
     */
    public static ImageIcon scaleImageIcon(ImageIcon icon, int newHeight, int newWidth) {
        Image img = icon.getImage();
        int height = icon.getIconHeight();
        int width = icon.getIconWidth();

        if (height > newHeight) {
            height = newHeight;
        }

        if (width > newWidth) {
            width = newWidth;
        }
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }


    /**
     * Returns a scaled down image if the height or width is smaller than
     * the image size.
     *
     * @param icon      the image icon.
     * @param newHeight the preferred height.
     * @param newWidth  the preferred width.
     * @return the icon.
     */
    public static ImageIcon scale(ImageIcon icon, int newHeight, int newWidth) {
        Image img = icon.getImage();
        img = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /**
     * Returns the native icon, if one exists for the filetype, otherwise
     * returns a default document icon.
     *
     * @param file the file to check icon type.
     * @return the native icon, otherwise default document icon.
     */
    public static Icon getIcon(File file) {
        try {
            sun.awt.shell.ShellFolder sf = sun.awt.shell.ShellFolder.getShellFolder(file);

            // Get large icon
            return new ImageIcon(sf.getIcon(true), sf.getFolderType());
        }
        catch (Exception e) {
            try {
                return new JFileChooser().getIcon(file);
            }
            catch (Exception e1) {
                // Do nothing.
            }
        }

        return SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32);
    }


    public static BufferedImage getBufferedImage(File file) {
        // Why wasn't this using it's code that pulled from the file?  Hrm.
        Icon icon = SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32);

        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.OPAQUE);
        Graphics bg = bi.getGraphics();

        ImageIcon i = (ImageIcon)icon;

        bg.drawImage(i.getImage(), 0, 0, null);
        bg.dispose();

        return bi;
    }
}
