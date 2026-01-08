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

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.sparkimpl.updater.AcceptAllCertsConnectionManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static javax.swing.text.StyleConstants.Foreground;

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
    public static final List<Character> DIRECTIVE_CHARS = Arrays.asList( '*', '_', '~', '`' );
    protected final String prefix;
    protected final Color prefixColor;
    protected final String message;
    protected final Color messageColor;
    protected final Color backgroundColor;

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
                    doc.insertString(doc.getLength(), line, line.trim().startsWith( "```" ) ? directiveStyle : style );
                }

                doc.insertString( doc.getLength(), "\n", messageStyle );
            }
            else
            {
                // for now, we'll not visually differentiate between quotes and non-quotes.
                for ( final String line : block.lines)
                {
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
                        if (DIRECTIVE_CHARS.contains(line.charAt(from))) {
                            char directive = line.charAt(from);
                            to = line.indexOf(directive, from + 1);
                            if (to != -1 && !Character.isWhitespace(line.charAt(to - 1)) && (to - from) > 1) {
                                insertFragment(chatArea, line.substring(from + 1, to++), applyMessageStyle(directive, messageStyle));
                                continue;
                            }
                        }

                        // Handle quoted text ("", not >)
                        if (line.charAt(from) == '\"') {
                            to = line.indexOf('\"', from + 1);
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

    protected void insertFragment(ChatArea chatArea, String fragment, MutableAttributeSet style) throws BadLocationException {
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
     * Inserts a picture into the current document.
     *
     * @param url - the link to the content to insert e.g., https://example.org/hello.gif
     * @throws BadLocationException if the location is not available for insertion.
     */
    public boolean insertPicture(ChatArea chatArea, String url, MutableAttributeSet messageStyle) throws BadLocationException
    {
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

            try (final CloseableHttpClient httpClient =
                     HttpClients.custom().useSystemProperties()
                         .setConnectionManager(AcceptAllCertsConnectionManager.getInstance()) // FIXME: do not use acceptallcdertsconnectionmanager! It is unsafe. Only use trusted certificates!
                         .setDefaultRequestConfig(RequestConfig.custom().setResponseTimeout(SmackConfiguration.getDefaultReplyTimeout()/10, TimeUnit.MILLISECONDS).build())
                         .build()
            ) {
                final ClassicHttpRequest request = ClassicRequestBuilder.get(uri)
                    .setHeader("Accept", "image/*")
                    .setHeader("User-Agent", "Spark HttpFileUpload")
                    .build();

                BufferedImage img = httpClient.execute(request, httpResponse -> {
                    if (httpResponse.getCode() != 200 || httpResponse.getEntity() == null) {
                        return null;
                    }
                    byte[] content;
                    try {
                        // First, read the content fully to avoid broken images
                        content = EntityUtils.toByteArray(httpResponse.getEntity());
                    } catch (IOException e) {
                        Log.warning("Network error while loading picture from " + uri, e);
                        return null;
                    } finally {
                        // if the connection is "prematurely closed," clean up the local resources
                        EntityUtils.consumeQuietly(httpResponse.getEntity());
                    }
                    try {
                        return ImageIO.read(new java.io.ByteArrayInputStream(content));
                    } catch (Throwable t) {
                        Log.warning("Unable to load picture from " + url, t);
                        return null;
                    }
                });

                if (img != null) {
                    SimpleAttributeSet center = new SimpleAttributeSet();
                    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                    SimpleAttributeSet left = new SimpleAttributeSet();
                    StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);

                    final StyledDocument doc = (StyledDocument) chatArea.getDocument();

                    doc.insertString( doc.getLength(), "\n", messageStyle );

                    final int width = Math.max(60, Math.round(chatArea.getParent().getWidth()*0.70f));
                    final int height = Math.max(60, Math.round(chatArea.getParent().getHeight()*0.40f));
                    ImageIcon image = scaleImage(new ImageIcon(img), width, height);

                    int start = doc.getLength();

                    MutableAttributeSet inputAttributes = chatArea.getInputAttributes();
                    inputAttributes.removeAttributes(inputAttributes);
                    StyleConstants.setIcon(inputAttributes, image);
                    chatArea.getDocument().insertString(doc.getLength(), " ", chatArea.getInputAttributes());
                    inputAttributes.removeAttributes(inputAttributes);
                    doc.insertString(doc.getLength(), "\n", messageStyle);

                    final MutableAttributeSet linkStyle = new SimpleAttributeSet( messageStyle.copyAttributes() );
                    insertLink(doc, url, linkStyle);
                    int end = doc.getLength();
                    final int length = end-start+1;
                    doc.setParagraphAttributes(start, length, center, false);

                    // No longer center.
                    //System.out.println("text: " + doc.getText(start, length));
                    doc.setParagraphAttributes(doc.getLength()+2, 0, left,false);
                    return true;
                }
            } catch (Throwable e) {
                Log.warning( "Unable to download content from " + url, e );
                return false;
            }
        }

        return false;
    }

    public static ImageIcon scaleImage(ImageIcon icon, int w, int h)
    {
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

    /**
     * Inserts a link into the current document.
     *
     * @param link - the link to insert e.g., https://example.org/
     * @throws BadLocationException if the location is not available for insertion.
     */
    public boolean insertLink(Document doc, String link, MutableAttributeSet style) throws BadLocationException
    {
        if ((link.startsWith("https://") || link.startsWith("http://") ||
            link.startsWith("ftp://") ||
            link.startsWith("www.") ||
            link.startsWith("xmpp:") ||
            link.startsWith("file:/"))
            && link.indexOf(".") > 1) {

            // Create a new style, based on the style used for generic text, for the link.
            final MutableAttributeSet linkStyle = new SimpleAttributeSet(style.copyAttributes());
            StyleConstants.setForeground(linkStyle, (Color) UIManager.get("Link.foreground"));
            StyleConstants.setUnderline(linkStyle, true);
            linkStyle.addAttribute("link", link);
            doc.insertString(doc.getLength(), link, linkStyle);
            return true;
        }
        else { return false; }
    }

    /**
     * Inserts a network address into the current document.
     *
     * @param address - the address to insert( ex. \superpc\etc\file\ OR http://localhost/ )
     * @throws BadLocationException if the location is not available for insertion.
     */
    public Boolean insertAddress(Document doc, String address, MutableAttributeSet style) throws BadLocationException
    {
        if (address.startsWith("\\\\") ||
            (address.indexOf("://") > 0 && address.indexOf(".") < 1)) {

            // Create a new style, based on the style used for generic text, for the address.
            final MutableAttributeSet addressStyle = new SimpleAttributeSet(style.copyAttributes());
            StyleConstants.setForeground(addressStyle, (Color) UIManager.get("Address.foreground"));
            StyleConstants.setUnderline(addressStyle, true);
            addressStyle.addAttribute("link", address);
            doc.insertString(doc.getLength(), address, addressStyle);
            return true;
        }
        else { return false; }
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
