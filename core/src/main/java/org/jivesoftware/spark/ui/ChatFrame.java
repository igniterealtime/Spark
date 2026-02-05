/*
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
package org.jivesoftware.spark.ui;

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Window used to display the ChatRoom container.
 */
public class ChatFrame extends JFrame implements WindowFocusListener {

    private static final long serialVersionUID = -7789413067818105293L;
    private long inactiveTime;
    private boolean focused;
    private final JCheckBox alwaysOnTopItem;
    private final ChatFrame chatFrame = this;
    private final CopyOnWriteArrayList<ChatFrameToFrontListener> _windowToFrontListeners = new CopyOnWriteArrayList<>();
   
    /**
     * Creates default ChatFrame.
     */
    public ChatFrame() {
    	this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	

	
	alwaysOnTopItem = new JCheckBox();
	alwaysOnTopItem.setToolTipText(Res.getString("menuitem.always.on.top"));
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        alwaysOnTopItem.addActionListener(actionEvent -> {
            if (alwaysOnTopItem.isSelected())
            {
                pref.setChatWindowAlwaysOnTop(true);
                chatFrame.setAlwaysOnTop(true);
            }
            else
            {
                pref.setChatWindowAlwaysOnTop(false);
                chatFrame.setAlwaysOnTop(false);
            }
        } );
        
        if (pref.isChatWindowAlwaysOnTop())
        {
        	alwaysOnTopItem.setSelected(true);
        	chatFrame.setAlwaysOnTop(true);
        }
        
        
        
        
        setIconImage(SparkManager.getApplicationImage().getImage());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(SparkManager.getChatManager().getChatContainer(), BorderLayout.CENTER);

        setMinimumSize( new Dimension( 300, 300 ) );
        final Rectangle chatFrameBounds = LayoutSettingsManager.getLayoutSettings().getChatFrameBounds();
        if (chatFrameBounds == null || chatFrameBounds.width <= 0 || chatFrameBounds.height <= 0)
        {
            // Use default settings.
            setSize(500, 400);
            GraphicUtils.centerWindowOnScreen(this);
        }
        else
        {
            setBounds( chatFrameBounds );
        }

        addComponentListener( new ComponentAdapter()
        {
            @Override
            public void componentResized( ComponentEvent e )
            {
                // Don't do this for subclasses.
                if ( e.getComponent().getClass().getSimpleName().equalsIgnoreCase( "ChatFrame" ) )
                {
                    LayoutSettingsManager.getLayoutSettings().setChatFrameBounds( getBounds() );
                }
            }

            @Override
            public void componentMoved( ComponentEvent e )
            {
                // Don't do this for subclasses.
                if ( e.getComponent().getClass().getSimpleName().equalsIgnoreCase( "ChatFrame" ) )
                {
                    LayoutSettingsManager.getLayoutSettings().setChatFrameBounds( getBounds() );
                }
            }
        } );

        addWindowFocusListener(this);

        // Setup WindowListener to be the proxy to the actual window listener
        // which cannot normally be used outside of the Window component because
        // of protected access.
        addWindowListener(new WindowAdapter() {

            /**
             * This event fires when the window has become active.
             *
             * @param e WindowEvent is not used.
             */
            @Override
			public void windowActivated(WindowEvent e) {
                inactiveTime = 0;
                if (Spark.isMac()) {
                    setJMenuBar(MainWindow.getInstance().getMenu());
                }
            }

            /**
             * Invoked when a window is de-activated.
             */
            @Override
			public void windowDeactivated(WindowEvent e) {
                inactiveTime = System.currentTimeMillis();
            }

            /**
             * This event fires whenever a user minimizes the window
             * from the toolbar.
             *
             * @param e WindowEvent is not used.
             */
            @Override
			public void windowIconified(WindowEvent e) {
            }

            @Override
			public void windowDeiconified(WindowEvent e) {
                setFocusableWindowState(true);
            }
        });

        // Adding a Resize Listener to validate component sizes in a Chat Room.
        addComponentListener(new ComponentAdapter() {
            @Override
			public void componentResized(ComponentEvent e) {
                try {
                    ChatRoom chatRoom = SparkManager.getChatManager().getChatContainer().getActiveChatRoom();
                    chatRoom.getVerticalSlipPane().setDividerLocation(-1);
                }
                catch (ChatRoomNotFoundException e1) {
                    // Ignore, because I don't care if it's not a chat room.
                }
            }
        });
         
        
    }
   	

    
    
    @Override
	public void windowGainedFocus(WindowEvent e) {
        focused = true;

        if(this instanceof MainWindow){
            return;
        }

        
        SparkManager.getChatManager().getChatContainer().focusChat();
    }

    @Override
	public void windowLostFocus(WindowEvent e) {
        focused = false;
    }

    /**
     * Returns true if the frame is in focus, otherwise returns false.
     *
     * @return true if the frame is in focus, otherwise returns false.
     */
    public boolean isInFocus() {
        return focused;
    }

    /**
     * Returns time the ChatFrame has not been in focus.
     *
     * @return the time in milliseconds.
     */
    public long getInactiveTime() {
        if (inactiveTime == 0) {
            return 0;
        }

        return System.currentTimeMillis() - inactiveTime;
    }

    /**
     * Brings the ChatFrame into focus on the desktop.
     */
    public void bringFrameIntoFocus() {
        if (!isVisible()) {
            setVisible(true);
        }

        if (getState() == Frame.ICONIFIED) {
            setState(Frame.NORMAL);
        }

        toFront();
        requestFocus();
    }

    /**
     * Shake it, come on now, shake that frame.
     */
    public void buzz() {
        ShakeWindow d = new ShakeWindow(this);
        d.startShake();
    }


    /**
     * set if the chatFrame should always stay on top
     * @param active
     */
    public void setWindowAlwaysOnTop(boolean active) {
	SettingsManager.getLocalPreferences().setChatWindowAlwaysOnTop(active);
	chatFrame.setAlwaysOnTop(active);
	this.fireWindowOnTopListeners(active);
    }


    private void fireWindowOnTopListeners( boolean active )
    {
        for ( ChatFrameToFrontListener listener : _windowToFrontListeners )
        {
            try
            {
                listener.updateStatus( active );
            }
            catch ( Exception e )
            {
                Log.error( "A ChatFrameToFrontListener (" + listener + ") threw an exception while processing a 'updateStatus' event with status: " + active, e );
            }
        }
    }

    /**
     * removes the Window to Front Listener for specified {@link ChatRoom}
     * @param chatRoom
     */
    public void removeWindowToFrontListener(ChatRoom chatRoom) {
	_windowToFrontListeners.remove(chatRoom);	
    }

    /**
     * Remove listeners from the "window-alway-on-top" information
     * @param chatRoom
     */
    public void addWindowToFronListener(ChatRoom chatRoom) {
	_windowToFrontListeners.addIfAbsent(chatRoom);
	fireWindowOnTopListeners(chatFrame.isAlwaysOnTop());
    }

}
