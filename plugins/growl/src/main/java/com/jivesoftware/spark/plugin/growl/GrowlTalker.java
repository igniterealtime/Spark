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

import com.google.code.jgntp.*;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.util.XmppStringUtils;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * GrowlTalker Class to send Messages to the GrowlInstance
 *
 * @author Wolf.Posdorfer
 * @author Guus der Kinderen, guus@gmail.com
 */
public class GrowlTalker implements GntpListener
{
    private final GntpApplicationInfo info;

    private GntpClient client = null;

    public GrowlTalker()
    {
        info = Gntp.appInfo( "Spark" ).build();
    }

    public synchronized void init()
    {
        Log.debug( "Initializing..." );
        if ( client != null )
        {
            Log.warning( "Already initialized before! Destroying previous instance before re-initializing..." );
            destroy();
        }

        client = Gntp.client( info )
                .listener( this )
                .build();
    }

    public synchronized void destroy()
    {
        Log.debug( "Tearing down..." );
        if ( client != null )
        {
            try
            {
                client.shutdown( 5, SECONDS );
            }
            catch ( InterruptedException e )
            {
                Log.warning( "An interruption occurred while shutting down.", e );
            }
            client = null;
        }
    }

    /**
     * Sends a notification with a CallBackContext
     *
     * @param title
     *            the title to display
     * @param body
     *            the body to display
     * @param callbackContext
     *            a callback context
     */
    public void sendNotificationWithCallback( String title, String body, String callbackContext )
    {
        final GntpNotificationInfo notificationInfo = Gntp.notificationInfo( info, "Notification" ).build();
        final GntpNotification notification = Gntp.notification( notificationInfo, title )
                .text( body )
                .withCallback()
                .context( callbackContext )
                .build();

        try
        {
            final boolean wasShown = client.notify( notification, 5, SECONDS );
            if (!wasShown )
            {
                Log.warning( "Notification did not show within the configured time-out: " + notification );
            }
        }
        catch ( Exception e )
        {
            Log.warning( "An exception occurred while trying to send a notification: " + notification, e );
        }
    }

    @Override
    public void onRegistrationSuccess()
    {
        Log.debug( "Registration success." );
    }

    @Override
    public void onNotificationSuccess( GntpNotification notification )
    {
        Log.debug( "Notification success." );
    }

    @Override
    public void onClickCallback( GntpNotification notification )
    {
        Log.debug( "Callback clicked: " + notification );
        final String jid = XmppStringUtils.parseBareJid( (String) notification.getContext() );
        final ChatRoom room = SparkManager.getChatManager().getChatRoom( jid );
        SparkManager.getChatManager().getChatContainer().activateChatRoom( room );
        SparkManager.getChatManager().getChatContainer().requestFocusInWindow();
    }

    @Override
    public void onCloseCallback( GntpNotification notification )
    {
        Log.debug( "Callback closed: " + notification );
    }

    @Override
    public void onTimeoutCallback( GntpNotification notification )
    {
        Log.debug( "Callback timed out: " + notification );
    }

    @Override
    public void onRegistrationError( GntpErrorStatus status, String description )
    {
        Log.error( "Registration error: " + description + " (status: " + status + ")" );
    }

    @Override
    public void onNotificationError( GntpNotification notification, GntpErrorStatus status, String description )
    {
        Log.error( "Notification error: " + description + " (status: " + status + ") for notification: " + notification );
    }

    @Override
    public void onCommunicationError( Throwable t )
    {
        Log.error( "Communication error.", t );
    }
}
