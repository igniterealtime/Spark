/*
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
package org.jivesoftware.sparkimpl.plugin.layout;

import java.awt.*;


public class LayoutSettings
{
    private Rectangle mainWindowBounds;
    private Rectangle chatFrameBounds;
    private Rectangle preferencesBounds;
    private Rectangle conferenceRoomBrowserBounds;
    private Rectangle pluginViewerBounds;
    private Rectangle broadcastHistoryBounds;
    private Rectangle broadcastMessageBounds;
    private Rectangle vCardEditorBounds;

    private int splitPaneDividerLocation;

    public Rectangle getMainWindowBounds()
    {
        return mainWindowBounds;
    }

    public void setMainWindowBounds( Rectangle mainWindowBounds )
    {
        this.mainWindowBounds = mainWindowBounds;
    }

    public Rectangle getChatFrameBounds()
    {
        return chatFrameBounds;
    }

    public void setChatFrameBounds( Rectangle chatFrameBounds )
    {
        this.chatFrameBounds = chatFrameBounds;
    }

    public Rectangle getPreferencesBounds()
    {
        return preferencesBounds;
    }

    public void setPreferencesBounds( Rectangle preferencesBounds )
    {
        this.preferencesBounds = preferencesBounds;
    }

    public int getSplitPaneDividerLocation()
    {
        return splitPaneDividerLocation;
    }

    public void setSplitPaneDividerLocation( int splitPaneDividerLocation )
    {
        this.splitPaneDividerLocation = splitPaneDividerLocation;
    }

    public Rectangle getConferenceRoomBrowserBounds()
    {
        return conferenceRoomBrowserBounds;
    }

    public void setConferenceRoomBrowserBounds( Rectangle conferenceRoomBrowserBounds )
    {
        this.conferenceRoomBrowserBounds = conferenceRoomBrowserBounds;
    }

    public Rectangle getPluginViewerBounds()
    {
        return pluginViewerBounds;
    }

    public void setPluginViewerBounds( Rectangle pluginViewerBounds )
    {
        this.pluginViewerBounds = pluginViewerBounds;
    }

    public Rectangle getBroadcastHistoryBounds()
    {
        return broadcastHistoryBounds;
    }

    public void setBroadcastHistoryBounds( Rectangle broadcastHistoryBounds )
    {
        this.broadcastHistoryBounds = broadcastHistoryBounds;
    }

    public Rectangle getBroadcastMessageBounds()
    {
        return broadcastMessageBounds;
    }

    public void setBroadcastMessageBounds( Rectangle broadcastMessageBounds )
    {
        this.broadcastMessageBounds = broadcastMessageBounds;
    }

    public Rectangle getVCardEditorBounds()
    {
        return vCardEditorBounds;
    }

    public void setVCardEditorBounds( Rectangle vCardEditorBounds )
    {
        this.vCardEditorBounds = vCardEditorBounds;
    }
}
