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

package org.jivesoftware.sparkimpl.preference.notifications;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Represents the UI for handling notification preferences within Spark.
 *
 * @author Derek DeMoro
 */
public class NotificationsUI extends JPanel {

	private static final long serialVersionUID = -3372199803443605883L;
	private JCheckBox toasterBox;
    private JCheckBox asteriskToasterBox;
    private JCheckBox windowFocusBox;
    private JCheckBox offlineNotificationBox;
    private JCheckBox onlineNotificationBox;
    private JCheckBox betaCheckBox;
    private JCheckBox SystemTrayNotificationBox;
    private JCheckBox showTypingNotificationBox;
    
    private JSpinner notificationDelay;

    public NotificationsUI() {

    	Integer[] spinnerDelay = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
    	SpinnerListModel delayModel = new SpinnerListModel(spinnerDelay);
    	notificationDelay = new JSpinner(delayModel);
    	notificationDelay.setPreferredSize(new Dimension(40,20));
    	        
    	setLayout(new FlowLayout(FlowLayout.LEFT));
    	        
    	JPanel pn = new JPanel();
    	pn.setLayout(new GridBagLayout());
    	        
    	JPanel pn_spinner = new JPanel();
    	pn_spinner.setLayout(new FlowLayout(FlowLayout.RIGHT));
    	pn_spinner.add(new JLabel(Res.getString("label.display.time")));
    	pn_spinner.add(notificationDelay);
    	pn_spinner.add(new JLabel(Res.getString("label.seconds")));
    	pn_spinner.setPreferredSize(new Dimension(190,25));
    	 
    	JPanel pn_OnOffNotifications = new JPanel();
    	pn_OnOffNotifications.setLayout(new GridBagLayout());
    	        
    	offlineNotificationBox = new JCheckBox();
    	ResourceUtils.resButton(offlineNotificationBox, Res.getString("checkbox.notify.user.goes.offline"));
    	pn_OnOffNotifications.add(offlineNotificationBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    	        
    	onlineNotificationBox = new JCheckBox();
    	ResourceUtils.resButton(onlineNotificationBox, Res.getString("checkbox.notify.user.comes.online"));
    	pn_OnOffNotifications.add(onlineNotificationBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    	        
    	pn_OnOffNotifications.add(pn_spinner, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    	pn_OnOffNotifications.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray.brighter()));

        setBorder(BorderFactory.createTitledBorder(Res.getString("group.notification.options")));

        toasterBox = new JCheckBox();
        ResourceUtils.resButton(toasterBox, Res.getString("checkbox.show.toaster"));
        pn.add(toasterBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        asteriskToasterBox = new JCheckBox();
        ResourceUtils.resButton(asteriskToasterBox, Res.getString("checkbox.disable.asterisk.toaster"));
        pn.add(asteriskToasterBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        windowFocusBox = new JCheckBox();
        ResourceUtils.resButton(windowFocusBox, Res.getString("checkbox.window.to.front"));
        pn.add(windowFocusBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                
        pn.add(pn_OnOffNotifications, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        
        SystemTrayNotificationBox = new JCheckBox();
        ResourceUtils.resButton(SystemTrayNotificationBox, Res.getString("checkbox.notify.systemtray"));
        pn.add(SystemTrayNotificationBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
           
        showTypingNotificationBox = new JCheckBox();
        ResourceUtils.resButton(showTypingNotificationBox, Res.getString("checkbox.notify.typing.systemtray"));
        pn.add(showTypingNotificationBox, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        
        betaCheckBox = new JCheckBox();
        ResourceUtils.resButton(betaCheckBox, Res.getString("menuitem.check.for.updates"));
        if(!Default.getBoolean(Default.DISABLE_UPDATES)){
        	pn.add(betaCheckBox, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        }   
        	add(pn);	

        windowFocusBox.addChangeListener( ce -> {
            if(shouldWindowPopup()) {
                setSystemTrayNotification(false);
                setSystemTrayNotificationEnabled(false);
            }
            else
                setSystemTrayNotificationEnabled(true);
        } );
        
        SystemTrayNotificationBox.addChangeListener( ce -> {
            if(isSystemTrayNotificationEnabled()) {
                setShowWindowPopup(false);
                setShowWindowPopupEnabled(false);
            }
            else
                setShowWindowPopupEnabled(true);
        } );
    }

    public void setShowToaster(boolean show) {
        toasterBox.setSelected(show);
    }

    public boolean showToaster() {
        return toasterBox.isSelected();
    }

    public void setDisableAsteriskToaster(boolean disable) {
        asteriskToasterBox.setSelected(disable);
    }

    public boolean disableAsteriskToaster() {
        return asteriskToasterBox.isSelected();
    }

    public void setShowWindowPopup(boolean popup) {
        windowFocusBox.setSelected(popup);
    }
    
    public void setShowWindowPopupEnabled(boolean popup) {
        windowFocusBox.setEnabled(popup);
    }

    public boolean shouldWindowPopup() {
        return windowFocusBox.isSelected();
    }
    
    public void setNotificationsDisplayTime(int DisplayTime)
    {
        notificationDelay.setValue(DisplayTime);
    }
       
    public Integer getNotificationsDisplayTime()
    {
        return Integer.valueOf(notificationDelay.getValue().toString())*1000;
    }   

    public void setOfflineNotification(boolean notify) {
        offlineNotificationBox.setSelected(notify);
    }

    public boolean isOfflineNotificationOn() {
        return offlineNotificationBox.isSelected();
    }

    public void setOnlineNotification(boolean notify) {
        onlineNotificationBox.setSelected(notify);
    }

    public boolean isOnlineNotificationOn() {
        return onlineNotificationBox.isSelected();
    }
    
    public void setTypingNotification(boolean notify) {
    	showTypingNotificationBox.setSelected(notify);
    }
    
    public boolean isTypingNotification() {
    	return showTypingNotificationBox.isSelected();
    }
    
    public void setSystemTrayNotification(boolean notify) {
    	SystemTrayNotificationBox.setSelected(notify);
    }
    
    public void setSystemTrayNotificationEnabled(boolean enable) {
    	SystemTrayNotificationBox.setEnabled(enable);
    }
    
    public boolean isSystemTrayNotificationEnabled() {
    	return SystemTrayNotificationBox.isSelected();
    }
    
    public void setCheckForBeta(boolean check) {
        betaCheckBox.setSelected(check);
    }

    public boolean isBetaCheckingEnabled() {
        return betaCheckBox.isSelected();
    }
    
}
