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

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.time.ZonedDateTime;

/**
 * A entry that is displayed as a horizontal line.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class HorizontalLineEntry extends TranscriptWindowEntry
{
    public HorizontalLineEntry()
    {
        super( ZonedDateTime.now() );
    }

    public HorizontalLineEntry( ZonedDateTime timestamp )
    {
        super( timestamp );
    }

    @Override
    protected void addTo( ChatArea chatArea ) throws BadLocationException
    {
        final Document doc = chatArea.getDocument();

        chatArea.insertComponent( new JSeparator() );
        doc.insertString(doc.getLength(), "\n", null );
        chatArea.setCaretPosition(doc.getLength());
    }
}
