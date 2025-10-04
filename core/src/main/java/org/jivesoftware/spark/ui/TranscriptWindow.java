/*
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
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

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.history.HistoryWindow;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

/**
 * Provides a window in which a chat is presented to the end user, by listing all chat messages in order. This
 * implementation is usable for both one-on-one chats, as well as multi-user chats.
 *
 * The class primary responsibility is to maintain an (time-based) ordered list of entries, and provides various methods
 * to add new entries. Responsibility for the visual representation of each entry is delegated to the implementation of
 * that entry.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class TranscriptWindow extends ChatArea implements ContextMenuListener
{
    /**
     * Unless specifically documented otherwise, content is stored in an in-memory cache of {@link TranscriptWindowEntry}s.
     * This cache is used to recompose the UI when needed (which typically occurs when entries are added out-of-order).
     */
    private final LinkedList<TranscriptWindowEntry> entries = new LinkedList<>();

    /**
     * Creates a default instance of <code>TranscriptWindow</code>.
     */
    public TranscriptWindow()
    {
        setEditable( false );

        Collection<String> emoticonPacks;
        emoticonPacks = EmoticonManager.getInstance().getEmoticonPacks();

        if ( emoticonPacks == null )
        {
            emoticonsAvailable = false;
        }

        addMouseListener( this );
        addMouseMotionListener( this );
        setDragEnabled( true );
        addContextMenuListener( this );

        // Make sure ctrl-c works
        getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( "Ctrl c" ), "copy" );

        getActionMap().put( "copy", new AbstractAction( "copy" )
        {
            private static final long serialVersionUID = 1797491846835591379L;

            @Override
			public void actionPerformed( ActionEvent evt )
            {
                StringSelection stringSelection = new StringSelection( getSelectedText() );
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents( stringSelection, null );
            }
        } );

    }

    protected synchronized void add( TranscriptWindowEntry entry )
    {
        final TranscriptWindow transcriptWindow = this;
        //boolean reorderEverything = false;
        if ( !entries.isEmpty() )
        {
            if ( entry.getTimestamp().isBefore( entries.getLast().getTimestamp() ) && !(entries.getLast() instanceof CustomTextEntry) )
            {
                Log.warning( "A chat entry appears to have been delivered out of order. The transcript window must be reordered!" );
                //reorderEverything = true;

                // This is an alternative approach to the 'reorderEverything boolean + routine. The idea behind using the
                // SwingUtilities is to schedule redrawing after all currently queued messages (loads of which are
                // probably also out of order), are processed, which should reduce the amount of redraws.
                SwingUtilities.invokeLater( () ->
                                            {
                                                // Clear and refill the UI component.
                                                try
                                                {
                                                    entries.sort( Comparator.comparing(TranscriptWindowEntry::isDelayed).thenComparing( TranscriptWindowEntry::getTimestamp ) );
                                                    clear();
                                                    for ( TranscriptWindowEntry e : entries )
                                                    {
                                                        e.addTo( transcriptWindow );
                                                    }
                                                }
                                                catch ( BadLocationException ex )
                                                {
                                                    Log.error( "An exception prevented chat content to be redrawn in the user interface!", ex );
                                                }
                                            } );
            }
            if ( !entry.getTimestamp().withZoneSameInstant( ZoneId.systemDefault() ).toLocalDate().isEqual( entries.getLast().getTimestamp().withZoneSameInstant( ZoneId.systemDefault() ).toLocalDate() ) )
            {
                // The date appeared to have rolled over, since the last entry. Add a 'start-of-day' entry before we add
                // the new entry, unless we're already in the process of adding exactly that 'start-of-day' entry.
                final StartOfDayEntry startOfDayEntry = new StartOfDayEntry( entry.getTimestamp() );
                if ( !entry.equals( startOfDayEntry ) )
                {
                    add( startOfDayEntry );
                }
            }
        }

        entries.add( entry );

        try
        {
//            if ( reorderEverything )
//            {
//                // Clear and refill the UI component.
//                entries.sort( Comparator.comparing( TranscriptWindowEntry::getTimestamp ) );
//                clear();
//                for ( TranscriptWindowEntry e : entries )
//                {
//                    e.addTo( this );
//                }
//            }
//            else
            {
                entry.addTo( this );
            }
        }
        catch ( BadLocationException ex )
        {
            Log.error( "An exception prevented chat content to be displayed in the user interface!", ex );
        }
    }


    /**
     * Inserts a component into the transcript window.
     *
     * @param component the component to insert.
     */
    public void addComponent( Component component )
    {
        final StyledDocument doc = (StyledDocument) getDocument();

        // The image must first be wrapped in a style
        Style style = doc.addStyle( "StyleName", null );


        StyleConstants.setComponent( style, component );

        // Insert the image at the end of the text
        try
        {
            doc.insertString( doc.getLength(), "ignored text", style );
            doc.insertString( doc.getLength(), "\n", null );
        }
        catch ( BadLocationException e )
        {
            Log.error( e );
        }
    }

    /**
     * Adds a text message this transcript window.
     *
     * Unless the provided message defines a timestamp (for instance, when a 'delay' extension is present), the message
     * timestamp is assumed to be 'now'.
     *
     * @param nickname   the nickname of the author of the message.
     * @param message    the message to insert.
     * @param foreground the color to use for the nickname (excluding the message text) foreground.
     */
    public void insertMessage( CharSequence nickname, Message message, Color foreground )
    {
        insertMessage( nickname, message, foreground, null );
    }

    /**
     * Adds a text message this transcript window.
     *
     * @param nickname   the nickname of the author of the message.
     * @param message    the message to insert.
     * @param foreground the color to use for the nickname (excluding the message text) foreground.
     * @param background the color to use for the entire background (eg, to highlight).
     */
    public void insertMessage( CharSequence nickname, Message message, Color foreground, Color background )
    {
        for ( TranscriptWindowInterceptor interceptor : SparkManager.getChatManager().getTranscriptWindowInterceptors() )
        {
            try
            {
                boolean handled = interceptor.isMessageIntercepted( this, nickname.toString(), message );
                if ( handled )
                {
                    // Do nothing.
                    return;
                }
            }
            catch ( Exception e )
            {
                Log.error( "A TranscriptWindowInterceptor ('" + interceptor + "') threw an exception while processing a chat message (current user: '" + nickname + "').", e );
            }
        }

        String body = message.getBody();

        // Verify the timestamp of this message. Determine if it is a 'live' message, or one that was sent earlier.
        final DelayInformation inf = message.getExtension(DelayInformation.class);
        final ZonedDateTime sentDate;
        final boolean isDelayed;
        if ( inf != null )
        {
            sentDate = inf.getStamp().toInstant().atZone( ZoneOffset.UTC );
            body = "(" + Res.getString( "offline" ) + ") " + body;
            isDelayed = true;
        }
        else
        {
            sentDate = ZonedDateTime.now();
            isDelayed = false;
        }
        add( new MessageEntry( sentDate, isDelayed, nickname.toString(), foreground, body, (Color) UIManager.get( "Message.foreground" ), background ) );
    }


    /**
     * Adds a notification message this transcript window. A notification message generally is a presence update, but
     * can be used for most anything related to the room.
     *
     * The message timestamp is assumed to be 'now'.
     *
     * @param message         the information message to insert.
     * @param foregroundColor the foreground color to use.
     */
    public synchronized void insertNotificationMessage( String message, Color foregroundColor )
    {
        add( new CustomTextEntry( ZonedDateTime.now(), message, foregroundColor ) );
    }

    /**
     * Adds a custom text message this transcript window.
     *
     * The message timestamp is assumed to be 'now'.
     *
     * @param text       the text to insert.
     * @param bold       true to use bold text.
     * @param underline  true to have text underlined.
     * @param foreground the foreground color.
     */
    public synchronized void insertCustomText( String text, boolean bold, boolean underline, Color foreground )
    {
        add( new CustomTextEntry( ZonedDateTime.now(), text, foreground, bold, false, underline, false ) );
    }

    /**
     * Adds a custom text message this transcript window.
     *
     * The message timestamp is assumed to be 'now'.
     *
     * @param text          the text to insert.
     * @param bold          true to use bold text.
     * @param italic        true to use italic text.
     * @param underline     true to have text underlined.
     * @param strikeThrough true to have text strike through.
     * @param foreground    the foreground color.
     */
    public synchronized void insertCustomText( String text, boolean bold, boolean italic, boolean underline, boolean strikeThrough, Color foreground )
    {
        add( new CustomTextEntry( ZonedDateTime.now(), text, foreground, bold, italic, underline, strikeThrough ) );
    }

    /**
     * Return the timestamp of the last entry that was added to this transcript window.
     *
     * If there is no entries in this window, the 'epoch' date, January 1, 1970, 00:00:00 GMT, is returned.
     *
     * @return the timestamp of the last entry in this window, or 'epoch' when there are no entries.
     */
    public Date getLastUpdated()
    {
        if ( entries.isEmpty() )
        {
            return new Date( 0 );
        }
        return Date.from( entries.getLast().getTimestamp().toInstant() );
    }

    /**
     * Adds a historic text message to this transcript window. These typically are messages that were added to a chat
     * before the local user joined the chat.
     *
     * @param userid  the userid of the sender.
     * @param message the message to insert.
     * @param date    the timestamp of the message.
     */
    public void insertHistoryMessage( String userid, String message, Date date )
    {
        final ZonedDateTime sentDate = date.toInstant().atZone( ZoneOffset.UTC );
        final Color historyColor = (Color) UIManager.get( "History.foreground" );

        add( new MessageEntry( sentDate, true, userid, historyColor, message, historyColor ) );
    }

    public void insertHorizontalLine()
    {
        add( new HorizontalLineEntry() );
    }

    /**
     * Disable the entire <code>TranscriptWindow</code> and visually represent
     * it as disabled.
     */
    public void showWindowDisabled()
    {
        final Document document = getDocument();
        final SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground( attrs, Color.LIGHT_GRAY );

        final int length = document.getLength();
        StyledDocument styledDocument = getStyledDocument();
        styledDocument.setCharacterAttributes( 0, length, attrs, false );
    }

    /**
     * Persist a current transcript.
     *
     * @param fileName   the name of the file to save the transcript as. Note: This can be modified by the user.
     * @param transcript the collection of transcript.
     * @param headerData the string to prepend to the transcript.
     * @see ChatRoom#getTranscripts()
     */
    public void saveTranscript( String fileName, List<Message> transcript, String headerData )
    {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();

        try
        {
            SimpleDateFormat formatter;

            File defaultSaveFile = new File( Spark.getSparkUserHome() + "/" + fileName );
            final JFileChooser fileChooser = new JFileChooser( defaultSaveFile );
            fileChooser.setSelectedFile( defaultSaveFile );

            // Show save dialog; this method does not return until the dialog is closed
            int result = fileChooser.showSaveDialog( this );
            final File selFile = fileChooser.getSelectedFile();

            if ( selFile != null && result == JFileChooser.APPROVE_OPTION )
            {
                final StringBuilder buf = new StringBuilder();
                final Iterator<Message> transcripts = transcript.iterator();
                buf.append( "<html><body>" );
                if ( headerData != null )
                {
                    buf.append( headerData );
                }

                buf.append( "<table width=600>" );
                while ( transcripts.hasNext() )
                {
                    final Message message = transcripts.next();
                    String from = null;
                    if (message.getFrom() != null) {
                        from = message.getFrom().toString();
                    }
                    if ( from == null )
                    {
                        from = pref.getNickname().toString();
                    }

                    if ( Message.Type.groupchat == message.getType() )
                    {
                        if ( ModelUtil.hasLength( XmppStringUtils.parseResource( from ) ) )
                        {
                            from = XmppStringUtils.parseResource( from );
                        }
                    }

                    final String body = message.getBody();

                    final JivePropertiesExtension extension = ( (JivePropertiesExtension) message.getExtension( JivePropertiesExtension.NAMESPACE ) );
                    Date insertionDate = null;
                    if ( extension != null )
                    {
                        insertionDate = (Date) extension.getProperty( "insertionDate" );
                    }

                    formatter = new SimpleDateFormat( "hh:mm:ss" );

                    String value = "";
                    if ( insertionDate != null )
                    {
                        value = "(" + formatter.format( insertionDate ) + ") ";
                    }
                    buf.append( "<tr><td nowrap><font size=2>" ).append( value ).append( "<strong>" ).append( from ).append( ":</strong>&nbsp;" ).append( body ).append( "</font></td></tr>" );

                }
                buf.append( "</table></body></html>" );
                final BufferedWriter writer = new BufferedWriter( new FileWriter( selFile ) );
                writer.write( buf.toString() );
                writer.close();
                UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );
                JOptionPane.showMessageDialog( SparkManager.getMainWindow(), "Chat transcript has been saved.",
                                               "Chat Transcript Saved", JOptionPane.INFORMATION_MESSAGE );
            }
        }
        catch ( Exception ex )
        {
            Log.error( "Unable to save chat transcript.", ex );
            UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );
            JOptionPane.showMessageDialog( SparkManager.getMainWindow(), "Could not save transcript.", "Error", JOptionPane.ERROR_MESSAGE );
        }

    }

    public void cleanup()
    {
        super.releaseResources();

        clear();

        removeMouseListener( this );
        removeMouseMotionListener( this );

        removeContextMenuListener( this );
        getActionMap().remove( "copy" );
    }

    /**
     * Adds Print and Clear actions.
     *
     * @param object the TransferWindow
     * @param popup  the popup menu to add to.
     */
    @Override
	public void poppingUp( final Object object, JPopupMenu popup )
    {
        popup.addSeparator();

        popup.add( new AbstractAction( Res.getString( "action.print" ), SparkRes.getImageIcon( SparkRes.PRINTER_IMAGE_16x16 ) )
        {
            @Override
			public void actionPerformed( ActionEvent actionEvent )
            {
                SparkManager.printChatTranscript( (TranscriptWindow) object );
            }
        } );

        if (!Default.getBoolean(Default.HIDE_HISTORY_SETTINGS) && Enterprise.containsFeature(Enterprise.HISTORY_SETTINGS_FEATURE)
        		&& !Default.getBoolean(Default.HISTORY_DISABLED) && Enterprise.containsFeature(Enterprise.HISTORY_TRANSCRIPTS_FEATURE)) {
            popup.add( new AbstractAction( Res.getString( "action.clear" ), SparkRes.getImageIcon( SparkRes.ERASER_IMAGE ) )
            {
                @Override
				public void actionPerformed( ActionEvent actionEvent )
                {
                    String user = null;
                    try
                    {
                        ChatManager manager = SparkManager.getChatManager();
                        ChatRoom room = manager.getChatContainer().getActiveChatRoom();
                        user = room.getBareJid().toString();

                        int ok = JOptionPane.showConfirmDialog( (TranscriptWindow) object,
                                                                Res.getString( "delete.permanently" ),
                                                                Res.getString( "delete.log.permanently" ),
                                                                JOptionPane.YES_NO_OPTION,
                                                                JOptionPane.QUESTION_MESSAGE );
                        if ( ok == JOptionPane.YES_OPTION )
                        {
                            // This actions must be move into Transcript Plugin!
                            File transcriptDir = new File( SparkManager.getUserDirectory(), "transcripts" );
                            File transcriptFile = new File( transcriptDir, user + ".xml" );
                            transcriptFile.delete();
                            transcriptFile = new File( transcriptDir, user + "_current.xml" );
                            transcriptFile.delete();
                            clear();
                        }
                    }
                    catch ( Exception ex )
                    {
                        Log.error( "An exception occurred while trying to clear history for a chat room " + user, ex );
                    }
                }
            } );
        }
        ChatRoom room = null;
        try{
            room = SparkManager.getChatManager().getChatContainer().getActiveChatRoom();
        }catch (Exception e){
            Log.error( "An exception occurred while trying to get active chat room " + room, e );
        }


        // History window
        if (!Default.getBoolean(Default.HISTORY_DISABLED) && Enterprise.containsFeature(Enterprise.HISTORY_TRANSCRIPTS_FEATURE)) {
            if(room != null && room.getChatType() == Message.Type.chat){
                ChatRoom finalRoom = room;
                popup.add(new AbstractAction( Res.getString( "action.viewlog" ) )
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        try
                        {
                            HistoryWindow hw = new HistoryWindow( SparkManager.getUserDirectory(), finalRoom.getJid().toString() );
                            hw.showWindow();
                        }
                        catch ( Exception ex )
                        {
                            Log.error( "An exception occurred while trying to open history window for room " + finalRoom, ex );
                        }
                    }
                } );
            }
        }
    }

    @Override
	public void poppingDown( JPopupMenu popup )
    {
    }

    @Override
	public boolean handleDefaultAction( MouseEvent e )
    {
        return false;
    }
}
