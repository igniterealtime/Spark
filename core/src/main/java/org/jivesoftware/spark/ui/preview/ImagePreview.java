package org.jivesoftware.spark.ui.preview;

import org.jivesoftware.spark.filetransfer.HttpDownloader;
import org.jivesoftware.spark.ui.ChatArea;
import org.jivesoftware.spark.util.log.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URLConnection;

import static org.jivesoftware.spark.ui.preview.LinkPreview.addLink;

public class ImagePreview {

    /**
     * Inserts a picture into the current document.
     *
     * @param url - the link to the content to insert e.g., https://example.org/hello.gif
     * @throws BadLocationException if the location is not available for insertion.
     */
    public static boolean insertPicture(ChatArea chatArea, String url, MutableAttributeSet messageStyle) {
        // TODO: instead of operating on message text content, operate on message stanza metadata.
        // TODO: do not download each time. Cache downloaded data.
        // TODO: make resized image clickable (open in unresized size).
        if (url.startsWith("https://") || url.startsWith("http://")) {
            // url with #anchor is definitely not an image
            if (url.contains("#")) {
                return false;
            }
            URI uri;
            try {
                uri = URI.create(url);
            } catch (IllegalArgumentException ignored) {
                Log.debug("Bad url " + url);
                return false;
            }
            // check if this is a file
            String path = uri.getPath();
            if (path == null || path.isEmpty()) {
                return false;
            }
            // Check if the file extension is a known image type
            String mimeType = URLConnection.getFileNameMap().getContentTypeFor(path);
            if (mimeType == null || !mimeType.startsWith("image/")) {
                return false;
            }

            byte[] content = HttpDownloader.downloadContent(uri);
            if (content == null) {
                return false;
            }
            BufferedImage img;
            try {
                img = ImageIO.read(new java.io.ByteArrayInputStream(content));
            } catch (Exception e) {
                Log.warning("Unable to load picture from " + url, e);
                return false;
            }
            try {
                if (img != null) {
                    SimpleAttributeSet center = new SimpleAttributeSet();
                    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                    SimpleAttributeSet left = new SimpleAttributeSet();
                    StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);

                    final StyledDocument doc = (StyledDocument) chatArea.getDocument();

                    doc.insertString(doc.getLength(), "\n", messageStyle);

                    final int width = Math.max(60, Math.round(chatArea.getParent().getWidth() * 0.70f));
                    final int height = Math.max(60, Math.round(chatArea.getParent().getHeight() * 0.40f));
                    ImageIcon image = scaleImage(new ImageIcon(img), width, height);

                    int start = doc.getLength();

                    MutableAttributeSet inputAttributes = chatArea.getInputAttributes();
                    inputAttributes.removeAttributes(inputAttributes);
                    StyleConstants.setIcon(inputAttributes, image);
                    chatArea.getDocument().insertString(doc.getLength(), " ", chatArea.getInputAttributes());
                    inputAttributes.removeAttributes(inputAttributes);
                    doc.insertString(doc.getLength(), "\n", messageStyle);

                    final MutableAttributeSet linkStyle = new SimpleAttributeSet(messageStyle.copyAttributes());
                    addLink(doc, url, linkStyle);
                    int end = doc.getLength();
                    final int length = end - start + 1;
                    doc.setParagraphAttributes(start, length, center, false);

                    // No longer center.
                    //System.out.println("text: " + doc.getText(start, length));
                    doc.setParagraphAttributes(doc.getLength() + 2, 0, left, false);
                    return true;
                }
            } catch (Throwable e) {
                Log.warning("Unable to download content from " + url, e);
                return false;
            }
        }

        return false;
    }

    public static ImageIcon scaleImage(ImageIcon icon, int w, int h) {
        try {
            int nw = icon.getIconWidth();
            int nh = icon.getIconHeight();
            if (icon.getIconWidth() > w) {
                nw = w;
                nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
            }
            if (nh > h) {
                nh = h;
                nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
            }
            return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_DEFAULT));
        } catch (Exception e) {
            Log.warning("Unable to scale an image", e);
            return null;
        }
    }

}
