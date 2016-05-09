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
package org.jivesoftware.fastpath.workspace.assistants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.panes.BackgroundPane;
import org.jivesoftware.fastpath.workspace.util.RequestUtils;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.MessageEventManager;
import org.jivesoftware.smackx.workgroup.util.ModelUtil;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.browser.BrowserFactory;
import org.jivesoftware.spark.component.browser.BrowserListener;
import org.jivesoftware.spark.component.browser.BrowserViewer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;


/**
 * Creates a new CoBrowser component. The CoBrowser is ChatRoom specific and is used
 * to control the end users browser.  Using the CoBrowser allows you to assist end customers
 * by directing them to the appropriate site.
 */
public class CoBrowser extends JPanel implements ActionListener, BrowserListener {
	private static final long serialVersionUID = 1115198448380589259L;
	private boolean isShowing;
    private ChatRoom chatRoom;
    private final JTextField pushField = new JTextField();
    private JCheckBox followMeButton;
    private RolloverButton goButton;
    private RolloverButton pushCurrentPageButton;
    private RolloverButton backButton;
    private String lastLink;
    private boolean hasLoaded;
    private BrowserViewer browser;

    private JTextField urlField;
    private String sessionID;

    // Define Constants
    private final static String PUBLIC_TOOLTIP = FpRes.getString("tooltip.allow.cobrowsing");
    private final static String PRIVATE_TOOLTIP = FpRes.getString("tooltip.hide.cobrowsing");

    /**
     * Creates a new CoBrowser object to be used with the specifid ChatRoom.
     */
    public CoBrowser(String sessionID, ChatRoom chatRoom) {
        this.sessionID = sessionID;
        this.chatRoom = chatRoom;
        tabSelected();
    }

    public void showDialog() {
        JFrame frame = new JFrame(FpRes.getString("title.cobrowsing.for", sessionID));
        frame.setIconImage(SparkManager.getMainWindow().getIconImage());
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this);
        frame.pack();
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(SparkManager.getChatManager().getChatContainer().getChatFrame());
        frame.setVisible(true);
    }


    private void showSpecifiedPage() {
        String url = urlField.getText();
        if (ModelUtil.hasLength(url)) {
            startFollowMeSession(url);
        }
    }

    /**
     * Loads a URL page( probably an html page ).
     *
     * @param url the url to load(ex.http://www.yahoo.com)
     */
    private void load(String url) {
        if (url.startsWith("www")) {
            url = "http://" + url;
        }

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }

        browser.loadURL(url);
        hasLoaded = true;
    }


    private void buildUI() {
        if (!isShowing) {
            update(getGraphics());
            isShowing = true;

            final JToolBar toolbar = new JToolBar();
            toolbar.setFloatable(false);
            toolbar.add(backButton);
            toolbar.add(urlField);
            toolbar.add(goButton);

            toolbar.setOpaque(false);


            final BackgroundPane titlePanel = new BackgroundPane();
            titlePanel.setLayout(new GridBagLayout());

            JLabel cobrowsingLabel = new JLabel();
            cobrowsingLabel.setText(FpRes.getString("cobrowsing.session"));
            cobrowsingLabel.setFont(new Font("Dialog", Font.BOLD, 11));

            titlePanel.add(cobrowsingLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
            titlePanel.add(pushCurrentPageButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
            titlePanel.add(followMeButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

            titlePanel.add(toolbar, new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 5, 0), 0, 0));

            add(titlePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

            updateLinkLabel(getStartLocation());
            goButton.addActionListener(this);
            backButton.addActionListener(this);
            pushCurrentPageButton.addActionListener(this);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == goButton) {
            showSpecifiedPage();
        }
        else if (e.getSource() == pushCurrentPageButton) {
            if (ModelUtil.hasLength(urlField.getText())) {
                pushPage(urlField.getText());
                followMeButton.setSelected(true);
            }
        }
        else if (e.getSource() == backButton) {
            if (ModelUtil.hasLength(lastLink)) {
                browser.goBack();
            }
        }
        else if (e.getSource() == followMeButton) {
            boolean isSelected = followMeButton.isSelected();
            if (!isSelected) {
                //followMeButton.setIcon(lockIcon);
                followMeButton.setToolTipText(PRIVATE_TOOLTIP);
            }
            else {
                //followMeButton.setIcon(unlockIcon);
                followMeButton.setToolTipText(PUBLIC_TOOLTIP);
            }
        }
    }

    private void startFollowMeSession(String url) {
        updateLinkLabel(url);

        // If the disable box is selected, only goto new page.
        // Do not update customer.
        if (!followMeButton.isSelected()) {
            load(url);
            return;
        }

        // If the disable box is not selected, update customer with
        // page push.
        /*
        final Message mes = new Message();
        mes.setProperty("PUSH_URL", link);
        mes.setBody("Start a Co-Browsing session with [b]" + link + "[/b]");

        // Update to insert into room
        chatRoom.getChatWindow().insertNotificationMessage("Sent a Co-Browsing invitation with page [b]" + link + "[/b]");


        chatRoom.sendMessage(mes);
        */
        pushField.setText("");
        load(url);
    }

    private void pushPage(String link) {
        updateLinkLabel(link);

        // If the disable box is not selected, update customer with
        // page push.
        final Message mes = new Message();
        mes.setProperty("PUSH_URL", link);
        mes.setBody(FpRes.getString("message.start.cobrowsing", link));

        chatRoom.getTranscriptWindow().insertNotificationMessage(FpRes.getString("message.send.cobrowsing.message", link), ChatManager.NOTIFICATION_COLOR);

        send(mes);
        pushField.setText("");
        load(link);
    }


    private void updateLinkLabel(String text) {
        lastLink = urlField.getText();
        urlField.setText(text);
        urlField.setToolTipText(GraphicUtils.createToolTip(text));
    }

    /**
     * Loads a page without notifiying of a change.
     *
     * @param url the url to load(ex.http://www.yahoo.com)
     */
    public void loadWithoutNotification(String url) {
        hasLoaded = false;
        load(url);
    }

    private void navigateUser(String href) {
        if (followMeButton.isSelected() && hasLoaded) {
            final Message mes = new Message();
            mes.setProperty("PUSH_URL", href);
            mes.setBody("");
            send(mes);
            updateLinkLabel(href);
            hasLoaded = false;
        }
        else {
            updateLinkLabel(href);
        }
        hasLoaded = true;
    }


    public void tabSelected() {

        goButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.PUSH_URL_16x16));
        pushCurrentPageButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.DOWNLOAD_16x16));
        followMeButton = new JCheckBox(FpRes.getString("checkbox.allow.user.to.follow"), false);
        followMeButton.setToolTipText(GraphicUtils.createToolTip(PUBLIC_TOOLTIP));
        followMeButton.addActionListener(this);

        final JPanel mainPanel = new JPanel();
        browser = BrowserFactory.getBrowser();

        browser.addBrowserListener(this);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(browser, BorderLayout.CENTER);

        urlField = new JTextField();


        setLayout(new GridBagLayout());

        backButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.SMALL_PIN_BLUE));
        backButton.setText(FpRes.getString("back"));
        backButton.setToolTipText(GraphicUtils.createToolTip(FpRes.getString("tooltip.back.one.page")));
        goButton.setToolTipText(GraphicUtils.createToolTip(FpRes.getString("tooltip.push.url")));
        goButton.setText(FpRes.getString("go"));

        pushCurrentPageButton.setToolTipText(GraphicUtils.createToolTip(FpRes.getString("tooltip.push.current.page")));
        pushCurrentPageButton.setText(FpRes.getString("button.start.cobrowsing.session"));


        load(getStartLocation());
        buildUI();

        add(mainPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        urlField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    showSpecifiedPage();
                }
            }

            public void keyReleased(KeyEvent e) {

            }

            public void keyTyped(KeyEvent e) {

            }
        });

        followMeButton.setOpaque(false);

    }

    public String getTabTitle() {
        return FastpathRes.getString(FastpathRes.CO_BROWSER_TAB_TITLE);
    }

    public Icon getTabIcon() {
        return FastpathRes.getImageIcon(FastpathRes.EARTH_VIEW_16x16);
    }

    public String getTabToolTip() {
        return FastpathRes.getString(FastpathRes.CO_BROWSER_TAB_TITLE);
    }

    public JComponent getGUI() {
        return this;
    }


    /**
     * Let's make sure that the panel doesn't strech past the
     * scrollpane view pane.
     *
     * @return the preferred dimension
     */
    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }


    public void documentLoaded(String documentURL) {
        urlField.setText(documentURL);
        navigateUser(documentURL);
    }

    private String getStartLocation() {
        Map metadata = FastpathPlugin.getLitWorkspace().getMetadata(sessionID);
        RequestUtils utils = new RequestUtils(metadata);
        String location = utils.getRequestLocation();
        if (location == null) {
            location = "";
        }
        return location;
    }

    private void send(Message message) {
        GroupChatRoom groupChatRoom = (GroupChatRoom)chatRoom;
        try {
            message.setTo(groupChatRoom.getRoomname());
            message.setType(Message.Type.groupchat);
            MessageEventManager.addNotificationsRequests(message, true, true, true, true);


            groupChatRoom.getMultiUserChat().sendMessage(message);
        }
        catch (XMPPException ex) {
            Log.error("Unable to send message in conference chat.", ex);
        }

    }
}
