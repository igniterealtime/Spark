/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jivesoftware.spark.plugin.growl;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.GlobalMessageListener;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.*;

/**
 * {@link GrowlMessageListener} implements the {@link GlobalMessageListener} and
 * creates Growl Notifications on Message received
 *
 * @author Wolf.Posdorfer
 */
public class GrowlMessageListener implements GlobalMessageListener
{
    private GrowlTalker talker;

    public GrowlMessageListener( GrowlTalker talker )
    {
        if ( talker == null )
        {
            throw new IllegalArgumentException( "Argument 'talker' cannot be null." );
        }
        this.talker = talker;
    }

    public void messageReceived( final ChatRoom chatRoom, final Message message )
    {
        SwingUtilities.invokeLater( () ->
                                    {
                                        final ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();

                                        if ( !chatFrame.isInFocus() )
                                        {
                                            showGrowlNotification( message );
                                        }
                                    } );

    }

    /**
     * Show a global Growl Notification
     *
     * @param message
     *            , {@link Message} containing Body and Sender
     */
    private void showGrowlNotification( Message message )
    {
        try
        {
            String name = SparkManager.getUserManager().getUserNicknameFromJID( message.getFrom() );
            String jid = message.getFrom();

            if ( name == null )
            {
                name = XmppStringUtils.parseLocalpart( message.getFrom() );
            }

            talker.sendNotificationWithCallback( name, message.getBody(), jid );
        }
        catch ( Exception e )
        {
            Log.error( e.getMessage(), e );
        }

    }

    public void messageSent( ChatRoom room, Message message )
    {}
}
