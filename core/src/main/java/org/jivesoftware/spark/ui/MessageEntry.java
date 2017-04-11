/*
 * Copyright (C) 2017 Ignite Realtime Foundation. All rights reserved.
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
package org.jivesoftware.spark.ui;

import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.util.StringTokenizer;

/**
 * An entry that represents a single (chat) message.
 *
 * An entry is displayed in this format: <tt>(timestamp) prefix: message</tt>,
 * for example: <tt>(10:03) Guus: Hello everyone!</tt>
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class MessageEntry extends TimeStampedEntry
{
    private final String prefix;
    private final Color prefixColor;
    private final String message;
    private final Color messageColor;
    private final Color backgroundColor;

    /**
     * Creates a new entry using the default background color (white/transparent).
     *
     * @param timestamp The timestamp of the entry (cannot be null).
     * @param prefix The prefix of the message (typically, the name of the author of the message.
     * @param prefixColor The color to be used for the timestamp and prefix text.
     * @param message The message text itself.
     * @param messageColor The color to be used for the message text.
     */
    public MessageEntry( ZonedDateTime timestamp, String prefix, Color prefixColor, String message, Color messageColor )
    {
        this( timestamp, prefix, prefixColor, message, messageColor, null );
    }

    /**
     * Creates a new entry using the default background color (white/transparent).
     *
     * @param timestamp The timestamp of the entry (cannot be null).
     * @param prefix The prefix of the message (typically, the name of the author of the message.
     * @param prefixColor The color to be used for the timestamp and prefix text.
     * @param message The message text itself.
     * @param messageColor The color to be used for the message text.
     * @param backgroundColor The color to be used for the entire entry (prefix as well ass message text).
     */
    public MessageEntry( ZonedDateTime timestamp, String prefix, Color prefixColor, String message, Color messageColor, Color backgroundColor )
    {
        super( timestamp );
        this.prefix = prefix == null ? "" : prefix;
        this.prefixColor = prefixColor;
        this.message = message;
        this.messageColor = messageColor;
        this.backgroundColor = backgroundColor != null ? backgroundColor : new Color( 255, 255, 255, 0);
    }

    protected AttributeSet getPrefixStyle()
    {
        final MutableAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setFontFamily( style, "Dialog" );
        StyleConstants.setFontSize( style, SettingsManager.getLocalPreferences().getChatRoomFontSize() );
        StyleConstants.setForeground( style, prefixColor );
        StyleConstants.setBackground( style, backgroundColor );
        return style;
    }

    protected AttributeSet getMessageStyle()
    {
        final MutableAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setFontFamily( style, "Dialog" );
        StyleConstants.setFontSize( style, SettingsManager.getLocalPreferences().getChatRoomFontSize() );
        StyleConstants.setForeground( style, messageColor );
        StyleConstants.setBackground( style, backgroundColor );
        return style;
    }

    @Override
    protected void addTo( ChatArea chatArea ) throws BadLocationException
    {
        final AttributeSet prefixStyle = getPrefixStyle();
        final AttributeSet messageStyle = getMessageStyle();

        // First, add the message prefix.
        final Document doc = chatArea.getDocument();
        doc.insertString( doc.getLength(), getFormattedTimestamp() + prefix + ": ", prefixStyle );

        // Next, process the message, bit by bit.
        final StringTokenizer tokenizer = new StringTokenizer( message, " \n\t", true );
        while ( tokenizer.hasMoreTokens() )
        {
            String textFound = tokenizer.nextToken();

            if ( ( textFound.startsWith( "http://" ) || textFound.startsWith( "ftp://" ) || textFound.startsWith( "https://" ) || textFound.startsWith( "www." ) || textFound.startsWith( "file:/" ) ) && textFound.indexOf( "." ) > 1 )
            {
                insertLink( doc, textFound );
            }
            else if ( textFound.startsWith( "\\\\" ) || ( textFound.indexOf( "://" ) > 0 && textFound.indexOf( "." ) < 1 ) )
            {
                insertAddress( doc, textFound );
            }
            else if ( !insertImage( chatArea, textFound ) )
            {
                doc.insertString( doc.getLength(), textFound, messageStyle );
            }
        }

        doc.insertString( doc.getLength(), "\n", messageStyle );
        chatArea.setCaretPosition( doc.getLength() );
    }

    /**
     * Inserts a link into the current document.
     *
     * @param link - the link to insert( ex. http://www.javasoft.com )
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertLink( Document doc, String link ) throws BadLocationException
    {
        // Create a new style, based on the style used for generic text, for the link.
        final MutableAttributeSet linkStyle = new SimpleAttributeSet( getMessageStyle().copyAttributes() );
        StyleConstants.setForeground( linkStyle, (Color) UIManager.get( "Link.foreground" ) );
        StyleConstants.setUnderline( linkStyle, true );
        linkStyle.addAttribute( "link", link );

        doc.insertString( doc.getLength(), link, linkStyle );
    }

    /**
     * Inserts a network address into the current document.
     *
     * @param address - the address to insert( ex. \superpc\etc\file\ OR http://localhost/ )
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertAddress( Document doc, String address ) throws BadLocationException
    {
        // Create a new style, based on the style used for generic text, for the address.
        final MutableAttributeSet addressStyle = new SimpleAttributeSet( getMessageStyle().copyAttributes() );
        StyleConstants.setForeground( addressStyle, (Color) UIManager.get( "Address.foreground" ) );
        StyleConstants.setUnderline( addressStyle, true );
        addressStyle.addAttribute( "link", address );

        doc.insertString( doc.getLength(), address, addressStyle );
    }

    /**
     * Inserts an emotion icon into the current document.
     *
     * @param imageKey - the smiley representation of the image.( ex. :) )
     * @return true if the image was found, otherwise false.
     */
    public boolean insertImage( ChatArea chatArea, String imageKey )
    {
        if ( !chatArea.getForceEmoticons() && !SettingsManager.getLocalPreferences().areEmoticonsEnabled() || !chatArea.emoticonsAvailable )
        {
            return false;
        }

        final Icon emotion = EmoticonManager.getInstance().getEmoticonImage( imageKey.toLowerCase() );
        if ( emotion == null )
        {
            return false;
        }

        final Document doc = chatArea.getDocument();
        chatArea.select( doc.getLength(), doc.getLength() );

        chatArea.insertIcon( emotion );
        return true;
    }
}
