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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static javax.swing.text.StyleConstants.Foreground;
import static org.jivesoftware.spark.ui.preview.ImagePreview.insertPicture;
import static org.jivesoftware.spark.ui.preview.LinkPreview.insertLink;
import static org.jivesoftware.spark.ui.preview.NetworkAddressPreview.insertAddress;

/**
 * An entry that represents a single (chat) message.
 *
 * An entry is displayed in this format: <tt>(timestamp) prefix: message</tt>,
 * for example: <tt>(10:03) Guus: Hello everyone!</tt>
 *
 * Messages are formatted according to <a href="https://xmpp.org/extensions/xep-0393.html">XEP-0393: Message Styling</a>
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class MessageEntry extends TimeStampedEntry
{
    public static final String DIRECTIVE_CHARS = "*_~`#[";
    private static final Predicate<String> CUSTOM_STYLE_NAME_MATCHER = Pattern.compile("_?[a-zA-Z]((-|_)?[a-zA-Z0-9]+)*").asMatchPredicate();
    private static final SimpleAttributeSet DEFAULT_HIGHLIGHT_STYLE = new SimpleAttributeSet();
    static {
        StyleConstants.setBackground(DEFAULT_HIGHLIGHT_STYLE, Color.YELLOW);
    }
    /**
     * Default background color (white/transparent)
     */
    private static final Color COLOR_MSG_BG = new Color(255, 255, 255, 0);
    protected final String prefix;
    protected final Color prefixColor;
    protected final String message;
    protected final Color messageColor;
    protected final Color backgroundColor;
    protected final Map<String, AttributeSet> customStyles;

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
     * Creates a new entry using a given background color (default is white/transparent).
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
        this(timestamp, isDelayed, prefix, prefixColor, message, messageColor, backgroundColor, Map.of());
    }

    /**
     * Creates a new entry using a given background color and optional custom styles to be applied like
     *      * <a href="https://docs.asciidoctor.org/asciidoc/latest/text/custom-inline-styles/">Asciidoc Custom Inline Styles</a>:
     *      * <p>{@code [.style-name]#my styled text#} will have the effect of applying the style corresponding to {@code style-name} in {@code customStyles} parameter, to the text between {@code #} characters.
     *
     * @param timestamp The timestamp of the entry (cannot be null).
     * @param isDelayed Set true if entry contain delayed delivery, historic timestamp.
     * @param prefix The prefix of the message (typically, the name of the author of the message.
     * @param prefixColor The color to be used for the timestamp and prefix text.
     * @param message The message text itself.
     * @param messageColor The color to be used for the message text.
     * @param customStyles custom styles to be applied to specific parts of the message in the same manner as Asciidoc custom inline styles.
     */
    public MessageEntry( ZonedDateTime timestamp, boolean isDelayed, String prefix, Color prefixColor, String message, Color messageColor, Color backgroundColor, Map<String, AttributeSet> customStyles )
    {
        super( timestamp, isDelayed );
        this.prefix = prefix == null ? "" : prefix;
        this.prefixColor = prefixColor;
        this.message = message;
        this.messageColor = messageColor;
        this.backgroundColor = backgroundColor != null ? backgroundColor : COLOR_MSG_BG;
        if(customStyles.keySet().stream().anyMatch(styleName -> !CUSTOM_STYLE_NAME_MATCHER.test(styleName))) {
            throw new IllegalArgumentException("One of the input custom style names is invalid: " + customStyles.keySet());
        }
        this.customStyles = customStyles;
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
                    boolean codeBlockLine = line.trim().startsWith("```");
                    doc.insertString(doc.getLength(), line, codeBlockLine ? directiveStyle : style );
                }

                doc.insertString( doc.getLength(), "\n", messageStyle );
            }
            else
            {
                // for now, we'll not visually differentiate between quotes and non-quotes.
                for ( final String line : block.lines)
                {
                    if (line.isEmpty()) {
                        continue;
                    }
                    int to = 0;
                    do
                    {
                        int from = to;

                        // Handle whitespace
                        if (Character.isWhitespace(line.charAt(to))) {
                            while (++to < line.length()) {
                                if (!Character.isWhitespace(line.charAt(to))) break;
                            }
                            doc.insertString(doc.getLength(), line.substring(from, to), messageStyle);
                            continue;
                        }

                        // Handle directives
                        char charAtFrom = line.charAt(from);
                        if (DIRECTIVE_CHARS.indexOf(charAtFrom) != -1) {
                            char directive = charAtFrom;
                            /*
                                If the matched directive char is [, check whether this is '[.style-name]#sometext#', where style-name is one of the names in customStyles, in which case apply that custom inline style (Asciidoc-like)
                                 */
                            final AttributeSet fragmentSpecificStyle;
                            if(directive == '[') {
                                if(line.charAt(from+1) == '.') {
                                    // style-name is at least one character, therefore look for ] from 'from+3' position
                                    final int closingSquareBracketIndex = line.indexOf(']', from + 3);
                                    if (closingSquareBracketIndex != -1 && line.charAt(closingSquareBracketIndex + 1) == '#') {
                                        final String appliedStyleName = line.substring(from + 2, closingSquareBracketIndex);
                                        if (!customStyles.containsKey(appliedStyleName)) {
                                            Log.error("The message text is using a custom inline style named '" + appliedStyleName + "' but no such style has been defined. Ignoring.");
                                            fragmentSpecificStyle = null;
                                        } else {
                                            fragmentSpecificStyle = customStyles.get(appliedStyleName);
                                            directive = '#';
                                            from = closingSquareBracketIndex + 1;
                                            Log.debug("Applying custom inline style '" + appliedStyleName + "' to the text between #");
                                        }
                                    } else {
                                        fragmentSpecificStyle = null;
                                    }
                                } else {
                                    fragmentSpecificStyle = null;
                                }
                            } else if(directive == '#') {
                                /*
                                    If the matched directive char is #, highlight (in yellow) the text between # (no custom inline style here, as it was already handled in previous case above).
                                */
                                fragmentSpecificStyle = DEFAULT_HIGHLIGHT_STYLE;
                            } else {
                                fragmentSpecificStyle = applyMessageStyle(directive, messageStyle);
                            }
                            if(fragmentSpecificStyle != null) {
                                to = line.indexOf(directive, from + 1); // directive closing
                                if (to != -1 && !Character.isWhitespace(line.charAt(to - 1)) && (to - from) > 1) {
                                    String fragment = line.substring(from + 1, to++);
                                    insertFragment(chatArea, fragment, applyMessageStyle(directive, messageStyle));
                                    continue;
                                }
                            }
                        }

                        // Handle quoted text ("", not >)
                        if (charAtFrom == '\"') {
                            to = line.indexOf('\"', from + 1); // quote closing
                            if (to != -1) {
                                doc.insertString(doc.getLength(), "\"", messageStyle);
                                insertFragment(chatArea, line.substring(from + 1, to++), messageStyle);
                                doc.insertString(doc.getLength(), "\"", messageStyle);
                                continue;
                            }
                        }

                        to = from;
                        while (++to < line.length()) {
                            if (Character.isWhitespace(line.charAt(to))) break;
                        }
                        insertFragment(chatArea, line.substring(from, to), messageStyle);
                    }
                    while (to < line.length());
                }
                    doc.insertString( doc.getLength(), "\n", messageStyle );
                }
            }

        // Enabling the 'setCaretPosition' line below causes Spark to freeze (often, not always) when trying to print the subject of a chatroom that's just being loaded.
        // chatArea.setCaretPosition( doc.getLength() );
    }

    protected void insertFragment(ChatArea chatArea, String fragment, AttributeSet style) throws BadLocationException {
        if (insertPicture(chatArea, fragment, style)) return;
        if (insertLink(chatArea.getDocument(), fragment, style)) return;
        if (insertAddress(chatArea.getDocument(), fragment, style)) return;
        if (insertEmoticon(chatArea, fragment)) return;
        chatArea.getDocument().insertString(chatArea.getDocument().getLength(), fragment, style);
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
        final StringTokenizer tokenizer = new StringTokenizer(text, "\n", true);
        Block block = null;
        while ( tokenizer.hasMoreTokens() )
        {
            final String line = tokenizer.nextToken();

            if ( block == null )
            {
                block = new Block(line);
            }
            else if ( !block.tryAppend( line ) )
            {
                // If this line does not belong to the block that's already being constructed, then that block is
                // done. Add it to the result, and create a new one.
                result.add( block );
                block = new Block(line);
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
    static class Block
    {
        final java.util.Deque<String> lines = new ArrayDeque<>();

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
     * Inserts an emotion icon into the current document.
     *
     * @param imageKey - the smiley representation of the image.( ex. :) )
     * @return true if the image was found, otherwise false.
     */
    public boolean insertEmoticon(ChatArea chatArea, String imageKey )
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
