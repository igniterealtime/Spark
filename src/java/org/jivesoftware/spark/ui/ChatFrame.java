/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettings;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * The Window used to display the ChatRoom container.
 */
public class ChatFrame extends JFrame implements WindowFocusListener {

    private static final long serialVersionUID = -7789413067818105293L;
    private long inactiveTime;
    private boolean focused;
    
    private JMenuBar chatWindowBar;
    private JMenu optionMenu; 
    private JCheckBoxMenuItem alwaysOnTopItem;
    private ChatFrame chatFrame;
    /**
     * Creates default ChatFrame.
     */
    public ChatFrame() {
	
	chatFrame = this;
	chatWindowBar = new JMenuBar();
	optionMenu = new JMenu();
	ResourceUtils.resButton(optionMenu, Res.getString("menuitem.chatframe.option"));
	
	alwaysOnTopItem = new JCheckBoxMenuItem();
        ResourceUtils.resButton(alwaysOnTopItem, Res.getString("menuitem.always.on.top"));
        alwaysOnTopItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                	if (alwaysOnTopItem.isSelected())
                	{
                		SettingsManager.getLocalPreferences().setChatWindowAlwaysOnTop(true);
                		chatFrame.setAlwaysOnTop(true);
                	}
                	else
                	{
                		SettingsManager.getLocalPreferences().setChatWindowAlwaysOnTop(false);
                		chatFrame.setAlwaysOnTop(false);
                	}
                }
        });
        
        if (SettingsManager.getLocalPreferences().isChatWindowAlwaysOnTop())
        {
        	alwaysOnTopItem.setSelected(true);
        	chatFrame.setAlwaysOnTop(true);
        }
        
        optionMenu.add(alwaysOnTopItem);
        chatWindowBar.add(optionMenu);
	setJMenuBar(chatWindowBar);
        setIconImage(SparkManager.getApplicationImage().getImage());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(SparkManager.getChatManager().getChatContainer(), BorderLayout.CENTER);

        LayoutSettings settings = LayoutSettingsManager.getLayoutSettings();
        if (settings.getChatFrameX() == 0 && settings.getChatFrameY() == 0) {
            // Use default settings.
            setSize(500, 400);
            GraphicUtils.centerWindowOnScreen(this);
        }
        else {
            setBounds(settings.getChatFrameX(), settings.getChatFrameY(), settings.getChatFrameWidth(), settings.getChatFrameHeight());
        }

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
            public void windowActivated(WindowEvent e) {
                inactiveTime = 0;
                if (Spark.isMac()) {
                    setJMenuBar(MainWindow.getInstance().getMenu());
                }
            }

            /**
             * Invoked when a window is de-activated.
             */
            public void windowDeactivated(WindowEvent e) {
                inactiveTime = System.currentTimeMillis();
            }

            /**
             * This event fires whenever a user minimizes the window
             * from the toolbar.
             *
             * @param e WindowEvent is not used.
             */
            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
                setFocusableWindowState(true);
            }
        });

        // Adding a Resize Listener to validate component sizes in a Chat Room.
        addComponentListener(new ComponentAdapter() {
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

    public void windowGainedFocus(WindowEvent e) {
        focused = true;

        if(this instanceof MainWindow){
            return;
        }

        
        SparkManager.getChatManager().getChatContainer().focusChat();
    }

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
     * Saves the layout on closing of the chat frame.
     */
    public void saveLayout() {
        LayoutSettings settings = LayoutSettingsManager.getLayoutSettings();
        settings.setChatFrameHeight(getHeight());
        settings.setChatFrameWidth(getWidth());
        settings.setChatFrameX(getX());
        settings.setChatFrameY(getY());
        LayoutSettingsManager.saveLayoutSettings();
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

}
