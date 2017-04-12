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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An entry that is prefixed with a time stamp.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public abstract class TimeStampedEntry extends TranscriptWindowEntry
{
    private final DateTimeFormatter format;

    protected TimeStampedEntry( ZonedDateTime timestamp, boolean isDelayed )
    {
        super( timestamp, isDelayed );
        format = DateTimeFormatter.ofPattern( SettingsManager.getLocalPreferences().getTimeFormat() );
    }

    protected String getFormattedTimestamp()
    {
        // Convert the zoned timestamp to a timestamp in the local zone.
        final LocalDateTime localDateTime = getTimestamp().withZoneSameInstant( ZoneId.systemDefault() ).toLocalDateTime();

        // Use the local timestamp to format a message that will be displayed to the end-user.
        return "(" + format.format( localDateTime ) + ") ";
    }
}
