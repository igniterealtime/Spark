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

import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.text.*;
import java.awt.*;
import java.time.ZonedDateTime;

/**
 * Represents a custom text, which is rendered as a line of text in a particular style.
 *
 * Typical examples are notifications that someone is joining or leaving a multi-user chatroom.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class CustomTextEntry extends TimeStampedEntry
{
    private final String message;
    private final Color textColor;
    private final boolean bold;
    private final boolean italic;
    private final boolean underline;
    private final boolean strikeThrough;

    protected CustomTextEntry( ZonedDateTime timestamp, String message, Color textColor, boolean bold, boolean italic, boolean underline, boolean strikeThrough )
    {
        super( timestamp, false );
        this.message = message;
        this.textColor = textColor;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
    }

    protected CustomTextEntry( ZonedDateTime timestamp, String message, Color textColor )
    {
        this( timestamp, message, textColor, false, false, false, false );
    }

    protected AttributeSet getStyle()
    {
        final MutableAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setFontFamily( style, "Dialog" );
        StyleConstants.setFontSize( style, SettingsManager.getLocalPreferences().getChatRoomFontSize() );
        StyleConstants.setForeground( style, textColor );
        StyleConstants.setBold( style, bold );
        StyleConstants.setItalic( style, italic );
        StyleConstants.setUnderline( style, underline );
        StyleConstants.setStrikeThrough( style, strikeThrough );
        return style;
    }

    @Override
    protected void addTo( ChatArea chatArea ) throws BadLocationException
    {
        final Document doc = chatArea.getDocument();

        doc.insertString( doc.getLength(), getFormattedTimestamp() + message + "\n", getStyle() );
        chatArea.setCaretPosition( doc.getLength() );
    }
}
