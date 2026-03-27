package org.jivesoftware.spark.ui.preview;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class LinkPreview {

    /**
     * Inserts a link into the current document.
     *
     * @param link - the link to insert e.g., https://example.org/
     * @throws BadLocationException if the location is not available for insertion.
     */
    public static boolean insertLink(Document doc, String link, AttributeSet style)
    {
        if ((link.startsWith("https://") || link.startsWith("http://") ||
            link.startsWith("ftp://") ||
            link.startsWith("www.") ||
            (link.startsWith("xmpp:") && link.contains("?join")) ||
            link.startsWith("file:/"))
            && link.indexOf(".") > 1) {

            addLink(doc, link, style);
            return true;
        }
        else { return false; }
    }

    static void addLink(Document doc, String link, AttributeSet style)  {
        // Create a new style, based on the style used for generic text, for the link.
        final MutableAttributeSet linkStyle = new SimpleAttributeSet(style.copyAttributes());
        StyleConstants.setForeground(linkStyle, (Color) UIManager.get("Link.foreground"));
        StyleConstants.setUnderline(linkStyle, true);
        linkStyle.addAttribute("link", link);
        try {
            doc.insertString(doc.getLength(), link, linkStyle);
        } catch (BadLocationException ignored) {
        }
    }
}
