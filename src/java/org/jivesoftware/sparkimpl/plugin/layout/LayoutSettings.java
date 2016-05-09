/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 *
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
package org.jivesoftware.sparkimpl.plugin.layout;

import java.awt.Toolkit;


public class LayoutSettings {

    private int mainWindowX;
    private int mainWindowY;
    private int mainWindowWidth;
    private int mainWindowHeight;

    private int chatFrameX;
    private int chatFrameY;
    private int chatFrameWidth;
    private int chatFrameHeight;

    private int preferencesFrameX;
    private int preferencesFrameY;
    private int preferencesFrameWidth;
    private int preferencesFrameHeight;
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
	} else if (mainWindowWidth > Toolkit.getDefaultToolkit()
		.getScreenSize().width) {
	    mainWindowWidth = Toolkit.getDefaultToolkit().getScreenSize().width - 50;
	}
	return mainWindowWidth;
    }

    public void setMainWindowWidth(int mainWindowWidth) {
        this.mainWindowWidth = mainWindowWidth;
    }

    public int getMainWindowHeight() {
	if (mainWindowHeight < 200) {
	    mainWindowHeight = 500;
	} else if (mainWindowHeight > Toolkit.getDefaultToolkit()
		.getScreenSize().height) {
	    mainWindowHeight = Toolkit.getDefaultToolkit().getScreenSize().height - 50;
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
        return chatFrameWidth < 300 ? 300 : chatFrameWidth;
    }

    public void setChatFrameWidth(int chatFrameWidth) {
        this.chatFrameWidth = chatFrameWidth;
    }

    public int getChatFrameHeight() {
        return chatFrameHeight < 300 ? 300 : chatFrameHeight;
    }

    public void setChatFrameHeight(int chatFrameHeight) {
        this.chatFrameHeight = chatFrameHeight;
    }

    public void setSplitPaneDividerLocation(int dividerLocation) {
        this.dividerLocation = dividerLocation;

    }

    public int getPreferencesFrameX() {
	return preferencesFrameX;
    }

    public void setPreferencesFrameX(int preferencesFrameX) {
	this.preferencesFrameX = preferencesFrameX;
    }

    public int getPreferencesFrameY() {
	return preferencesFrameY;
    }

    public void setPreferencesFrameY(int preferencesFrameY) {
	this.preferencesFrameY = preferencesFrameY;
    }

    public int getPreferencesFrameWidth() {
	return preferencesFrameWidth < 600 ? 600 : preferencesFrameWidth;
    }

    public void setPreferencesFrameWidth(int preferencesFrameWidth) {
	this.preferencesFrameWidth = preferencesFrameWidth;
    }

    public int getPreferencesFrameHeight() {
	return preferencesFrameHeight < 600 ? 600 : preferencesFrameHeight;
    }

    public void setPreferencesFrameHeight(int preferencesFrameHeight) {
	this.preferencesFrameHeight = preferencesFrameHeight;
    }

    public int getSplitPaneDividerLocation() {
	return dividerLocation;
   }


}
