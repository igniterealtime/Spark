/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.layout;

public class LayoutSettings {

    private int mainWindowX;
    private int mainWindowY;
    private int mainWindowWidth;
    private int mainWindowHeight;

    private int chatFrameX;
    private int chatFrameY;
    private int chatFrameWidth;
    private int chatFrameHeight;
    private int dividerLocation = -1;

    public int getMainWindowX() {
        return mainWindowX;
    }

    public void setMainWindowX(int mainWindowX) {
        this.mainWindowX = mainWindowX;
    }

    public int getMainWindowY() {
        return mainWindowY;
    }

    public void setMainWindowY(int mainWindowY) {
        this.mainWindowY = mainWindowY;
    }

    public int getMainWindowWidth() {
        if (mainWindowWidth < 100) {
            mainWindowWidth = 100;
        }
        return mainWindowWidth;
    }

    public void setMainWindowWidth(int mainWindowWidth) {
        this.mainWindowWidth = mainWindowWidth;
    }

    public int getMainWindowHeight() {
        if (mainWindowHeight < 200) {
            mainWindowHeight = 200;
        }
        return mainWindowHeight;
    }

    public void setMainWindowHeight(int mainWindowHeight) {
        this.mainWindowHeight = mainWindowHeight;
    }

    public int getChatFrameX() {
        return chatFrameX;
    }

    public void setChatFrameX(int chatFrameX) {
        this.chatFrameX = chatFrameX;
    }

    public int getChatFrameY() {
        return chatFrameY;
    }

    public void setChatFrameY(int chatFrameY) {
        this.chatFrameY = chatFrameY;
    }

    public int getChatFrameWidth() {
        return chatFrameWidth;
    }

    public void setChatFrameWidth(int chatFrameWidth) {
        this.chatFrameWidth = chatFrameWidth;
    }

    public int getChatFrameHeight() {
        return chatFrameHeight;
    }

    public void setChatFrameHeight(int chatFrameHeight) {
        this.chatFrameHeight = chatFrameHeight;
    }

    public void setSplitPaneDividerLocation(int dividerLocation) {
        this.dividerLocation = dividerLocation;

    }

    public int getSplitPaneDividerLocation() {
        return dividerLocation;
    }


}
