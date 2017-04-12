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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A text entry that denotes the start of a (new) day.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class StartOfDayEntry extends TranscriptWindowEntry
{
    private static final MutableAttributeSet STYLE;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern( "EEEE, dd MMMM yyyy" );

    static
    {
        STYLE = new SimpleAttributeSet();
        StyleConstants.setFontFamily( STYLE, "Dialog" );
        StyleConstants.setFontSize( STYLE, SettingsManager.getLocalPreferences().getChatRoomFontSize() );
        StyleConstants.setBold( STYLE, true );
        StyleConstants.setUnderline( STYLE, true );
        StyleConstants.setForeground( STYLE, Color.BLACK );
    }

    protected StartOfDayEntry( ZonedDateTime timestamp )
    {
    	//delay here is true because StartOfDayEntry can appear among MessageEntries with delay
        super( timestamp,true );
    }

    @Override
    protected void addTo( ChatArea chatArea ) throws BadLocationException
    {
        // Get the instant that represents the start of the day in the local time-zone.
        final LocalDateTime startOfDay = getTimestamp().withZoneSameInstant( ZoneId.systemDefault() ).toLocalDate().atStartOfDay();

        final String startOfDayMessage = FORMAT.format( startOfDay );

        final Document doc = chatArea.getDocument();
        doc.insertString(doc.getLength(), startOfDayMessage + '\n', STYLE );
        chatArea.setCaretPosition(doc.getLength());
    }
}
