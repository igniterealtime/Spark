package org.jivesoftware.spark.ui.preview;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class NetworkAddressPreview {

    /**
     * Inserts a network address into the current document.
     *
     * @param address - the address to insert( ex. \superpc\etc\file\ OR http://localhost/ )
     * @throws BadLocationException if the location is not available for insertion.
     */
    public static Boolean insertAddress(Document doc, String address, MutableAttributeSet style)
    {
        if (address.startsWith("\\\\") ||
            (address.indexOf("://") > 0 && address.indexOf(".") < 1)) {

            // Create a new style, based on the style used for generic text, for the address.
            final MutableAttributeSet addressStyle = new SimpleAttributeSet(style.copyAttributes());
            StyleConstants.setForeground(addressStyle, (Color) UIManager.get("Address.foreground"));
            StyleConstants.setUnderline(addressStyle, true);
            addressStyle.addAttribute("link", address);
            try {
                doc.insertString(doc.getLength(), address, addressStyle);
            } catch (BadLocationException ignored) {
            }
            return true;
        }
        else { return false; }
    }
}
