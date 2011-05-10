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
package org.jivesoftware.spark.roar;


import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.GlobalMessageListener;

/**
 * Message Listener
 * @author wolf.posdorfer
 *
 */
public class RoarMessageListener implements GlobalMessageListener{

    
    private int _lastusedXpos;
    private int _lastusedYpos;
    private Dimension _screensize;
    
    private final int WIDTH = 300;
    private final int HEIGHT = 80;

    public RoarMessageListener() {
	_screensize = Toolkit.getDefaultToolkit().getScreenSize();

	_lastusedXpos = _screensize.width - 5;
	_lastusedYpos = 5;

    }


    @Override
    public void messageReceived(ChatRoom room, Message message) {

	ImageIcon icon = SparkRes.getImageIcon(SparkRes.SPARK_IMAGE_32x32);
	
	String nickname = StringUtils.parseName(message.getFrom());
	
	RoarPanel.popupWindow(this, icon, nickname,  message.getBody(), _lastusedXpos, _lastusedYpos);
	
	_lastusedYpos += HEIGHT+5;

	
	if(_lastusedYpos>= _screensize.height-90)
	{
	    _lastusedXpos -= WIDTH+5;
	    _lastusedYpos = 5;
	}
	
    }
    
    public void closingRoarPanel(int x, int y)
    {
	if(_lastusedYpos>(y-5))
	{
	    _lastusedYpos=y-5;
	}
	
	if(_lastusedXpos<(x+5))
	{
	    _lastusedXpos=x+WIDTH+5;
	}
	
    }

    @Override
    public void messageSent(ChatRoom room, Message message) {
	// who cares?
    }

}
