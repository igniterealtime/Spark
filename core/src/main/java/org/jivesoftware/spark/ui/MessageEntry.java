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

import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

import static javax.swing.text.StyleConstants.Foreground;

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
    public static final List<Character> DIRECTIVE_CHARS = Arrays.asList( '*', '_', '~', '`' );
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
        this( timestamp, false, prefix, prefixColor, message, messageColor, null );
    }
    
    /**
     * Creates a new entry using the default background color (white/transparent).
     *
     * @param timestamp The timestamp of the entry (cannot be null).
     * @param isDelayed Set true if entry contain delayed delivery, historic timestamp.
     * @param prefix The prefix of the message (typically, the name of the author of the message.
     * @param prefixColor The color to be used for the timestamp and prefix text.
     * @param message The message text itself.
     * @param messageColor The color to be used for the message text.
     */
    public MessageEntry( ZonedDateTime timestamp, boolean isDelayed, String prefix, Color prefixColor, String message, Color messageColor )
    {
        this( timestamp, isDelayed, prefix, prefixColor, message, messageColor, null );
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
        this( timestamp, false, prefix, prefixColor, message, messageColor,backgroundColor );

    }
    
    /**
     * Creates a new entry using the default background color (white/transparent).
     *
     * @param timestamp The timestamp of the entry (cannot be null).
     * @param isDelayed Set true if entry contain delayed delivery, historic timestamp.
     * @param prefix The prefix of the message (typically, the name of the author of the message.
     * @param prefixColor The color to be used for the timestamp and prefix text.
     * @param message The message text itself.
     * @param messageColor The color to be used for the message text.
     * @param backgroundColor The color to be used for the entire entry (prefix as well ass message text).
     */
    public MessageEntry( ZonedDateTime timestamp, boolean isDelayed, String prefix, Color prefixColor, String message, Color messageColor, Color backgroundColor )
    {
        super( timestamp, isDelayed );
        this.prefix = prefix == null ? "" : prefix;
        this.prefixColor = prefixColor;
        this.message = message;
        this.messageColor = messageColor;
        this.backgroundColor = backgroundColor != null ? backgroundColor : new Color( 255, 255, 255, 0);
    }

    protected MutableAttributeSet getPrefixStyle()
    {
        final MutableAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setFontFamily( style, "Dialog" );
        StyleConstants.setFontSize( style, SettingsManager.getLocalPreferences().getChatRoomFontSize() );
        StyleConstants.setForeground( style, prefixColor );
        StyleConstants.setBackground( style, backgroundColor );
        return style;
    }

    protected MutableAttributeSet getMessageStyle()
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
        final MutableAttributeSet prefixStyle = getPrefixStyle();
        final MutableAttributeSet messageStyle = getMessageStyle();

        // First, add the message prefix.
        final Document doc = chatArea.getDocument();
        doc.insertString( doc.getLength(), getFormattedTimestamp() + prefix + ": ", prefixStyle );

        final MutableAttributeSet directiveStyle = applyMessageStyle( 'K', messageStyle ); // Special style used on the directives themselves.

        // Next, process the message, bit by bit.
        for ( final Block block : asBlocks( message ) )
        {
            if ( block.isPreformattedCodeBlock() )
            {
                final MutableAttributeSet style = applyMessageStyle( '`', messageStyle );
                for ( final String line : block.lines )
                {
                    doc.insertString( doc.getLength(), line + "\n", line.trim().startsWith( "```" ) ? directiveStyle : style );
                }
            }
            else
            {
                // for now, we'll not visually differentiate between quotes and non-quotes.
                for ( final String line : block.lines)
                {
                    int i = 0;
                    while ( i < line.length() - 1 )
                    {
                        // Skip through prepending whitespace.
                        if ( Character.isWhitespace( line.charAt( i ) ) )
                        {
                            doc.insertString( doc.getLength(), line.substring( i, i+1 ), messageStyle );
                            i++;
                        }

                        // Find the next whitespace or line-ending (used for links, addresses, images, but not style)
                        int end = line.indexOf( ' ', i+1 );
                        if ( end == -1 ) end = line.indexOf(  '\t', i+1 );
                        if ( end == -1 ) end = line.length();
                        final String textFound = line.substring( i, end );
                        if ( ( textFound.startsWith( "http://" ) || textFound.startsWith( "ftp://" ) || textFound.startsWith( "https://" ) || textFound.startsWith( "www." ) || textFound.startsWith( "file:/" ) ) && textFound.indexOf( "." ) > 1 )
                        {
                            insertLink( doc, textFound );
                            i = end;
                            continue;
                        }

                        if ( textFound.startsWith( "\\\\" ) || ( textFound.indexOf( "://" ) > 0 && textFound.indexOf( "." ) < 1 ) )
                        {
                            insertAddress( doc, textFound );
                            i = end;
                            continue;
                        }

                        if ( insertImage( chatArea, textFound ) )
                        {
                            i = end;
                            continue;
                        }

                        // Opening directive (must be preceeded by a whitespace-character, or be the first character of the line)
                        if ( (i == 0 || Character.isWhitespace( line.charAt( i-1 ) ) ) && DIRECTIVE_CHARS.contains( line.charAt( i ) ) )
                        {
                            final char directive = line.charAt( i );
                            final int closing = line.indexOf( directive, i + 1 );

                            // Closing directive must not be preceeded by a whitespace character.
                            if ( closing != -1 && !Character.isWhitespace( line.charAt( closing-1 ) ) )
                            {
                                final MutableAttributeSet applied = applyMessageStyle( directive, messageStyle );
                                doc.insertString( doc.getLength(), line.substring( i, i+1 ), directiveStyle ); // Opening directive
                                doc.insertString( doc.getLength(), line.substring( i+1, closing ), applied ); // Styled text
                                doc.insertString( doc.getLength(), line.substring( closing, closing+1 ), directiveStyle ); // Closing directive
                                i = closing + 1;
                                continue;
                            }
                        }

                        doc.insertString( doc.getLength(), textFound, messageStyle );
                        i = end;
                    }

                    doc.insertString( doc.getLength(), "\n", messageStyle );
                }
            }
        }

        chatArea.setCaretPosition( doc.getLength() );
    }

    /**
     * Splits the provided text into a sequence of blocks.
     *
     * @param text The text to be parsed
     * @return the
     */
    protected java.util.List<Block> asBlocks( String text )
    {
        final java.util.List<Block> result = new ArrayList<>();

        // Process the text line-by-line
        final StringTokenizer tokenizer = new StringTokenizer( text, "\n", false );
        Block block = null;
        while ( tokenizer.hasMoreTokens() )
        {
            final String line = tokenizer.nextToken();

            if ( block == null )
            {
                block = new Block( line );
            }
            else if ( !block.tryAppend( line ) )
            {
                // If this line does not belong to the block that's already being constructed, then that block is
                // done. Add it to the resul    t, and create a new one.
                result.add( block );
                block = new Block( line );
            }
        }

        if (block != null)
        {
            result.add( block );
        }

        return result;
    }

    /**
     * A block is any chunk of text that can be parsed unambiguously in one pass.
     *
     * <ul>
     * <li>A single line of text comprising one or more spans</li>
     * <li>A block quotation</li>
     * <li>A preformatted code block</li>
     * </ul>
     */
    class Block
    {
        java.util.Deque<String> lines = new ArrayDeque<>();

        Block( String line )
        {
            lines.add( line );
        }

        boolean isBlockQuotation() {
            return !lines.isEmpty() && lines.getFirst().startsWith( ">" );
        }

        boolean isPreformattedCodeBlock() {
            return !lines.isEmpty() && lines.getFirst().startsWith( "```" );
        }

        boolean tryAppend( String line )
        {
            if ( isBlockQuotation() && line.startsWith( ">" ) )
            {
                // Only add when this is a new line in the same block quote.
                if ( !line.startsWith( ">" ) )
                {
                    return false;
                }

                lines.add( line );
                return true;
            }

            if ( isPreformattedCodeBlock() )
            {
                // Only add if the code block was not already closed.
                if ( lines.size() > 1 && lines.getLast().trim().endsWith( "```" ) )
                {
                    return false;
                }

                lines.add( line );
                return true;
            }

            if ( line.startsWith( ">" ) || line.startsWith( "```" ))
            {
                // Start a new block!
                return false;
            }
            lines.add( line );
            return true;
        }
    }

    public MutableAttributeSet applyMessageStyle( char directive, MutableAttributeSet messageStyle )
    {
        final MutableAttributeSet style = new SimpleAttributeSet( messageStyle.copyAttributes() );
        switch ( directive )
        {
            case '*':
                StyleConstants.setBold( style, true );
                break;
            case '_':
                StyleConstants.setItalic( style, true );
                break;
            case '~':
                StyleConstants.setStrikeThrough( style, true );
                break;
            case '`':
                StyleConstants.setFontFamily( style, "Monospaced" );
                break;
            case 'K': // Keyword
                StyleConstants.setForeground( style, ((Color) messageStyle.getAttribute( Foreground )).brighter().brighter().brighter() );
                break;

            default:
                Log.warning( "Cannot apply message style for unrecognized directive: " + directive );
        }
        return style;
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

        final Icon emotion = EmoticonManager.getInstance().getEmoticonImage( imageKey );
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
