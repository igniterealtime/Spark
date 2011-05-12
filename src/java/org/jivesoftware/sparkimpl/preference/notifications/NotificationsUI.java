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
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Represents the UI for handling notification preferences within Spark.
 *
 * @author Derek DeMoro
 */
public class NotificationsUI extends JPanel {

	private static final long serialVersionUID = -3372199803443605883L;
	private JCheckBox toasterBox;
    private JCheckBox windowFocusBox;
    private JCheckBox offlineNotificationBox;
    private JCheckBox onlineNotificationBox;
    private JCheckBox betaCheckBox;
    private JCheckBox SystemTrayNotificationBox;
    private JCheckBox showTypingNotificationBox;

    public NotificationsUI() {
        setLayout(new VerticalFlowLayout());

        setBorder(BorderFactory.createTitledBorder(Res.getString("group.notification.options")));

        toasterBox = new JCheckBox();
        ResourceUtils.resButton(toasterBox, Res.getString("checkbox.show.toaster"));
        add(toasterBox);

        windowFocusBox = new JCheckBox();
        ResourceUtils.resButton(windowFocusBox, Res.getString("checkbox.window.to.front"));
        add(windowFocusBox);

        offlineNotificationBox = new JCheckBox();
        ResourceUtils.resButton(offlineNotificationBox, Res.getString("checkbox.notify.user.goes.offline"));
        add(offlineNotificationBox);

        onlineNotificationBox = new JCheckBox();
        ResourceUtils.resButton(onlineNotificationBox, Res.getString("checkbox.notify.user.comes.online"));
        add(onlineNotificationBox);

        SystemTrayNotificationBox = new JCheckBox();
        ResourceUtils.resButton(SystemTrayNotificationBox, Res.getString("checkbox.notify.systemtray"));
        add(SystemTrayNotificationBox);
        
        showTypingNotificationBox = new JCheckBox();
        ResourceUtils.resButton(showTypingNotificationBox, Res.getString("checkbox.notify.typing.systemtray"));
        add(showTypingNotificationBox);
        
        betaCheckBox = new JCheckBox();
        ResourceUtils.resButton(betaCheckBox, Res.getString("menuitem.check.for.updates"));
        if(!Default.getBoolean(Default.DISABLE_UPDATES)){
        add(betaCheckBox);
        }
        
        windowFocusBox.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent ce){
        		if(shouldWindowPopup()) {
        			setSystemTrayNotification(false);
        			setSystemTrayNotificationEnabled(false);
        		}
        		else
        			setSystemTrayNotificationEnabled(true);
        	}
        });
        
        SystemTrayNotificationBox.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent ce){
        		if(isSystemTrayNotificationEnabled()) {
        			setShowWindowPopup(false);
        			setShowWindowPopupEnabled(false);
        		}
        		else
        			setShowWindowPopupEnabled(true);
        	}
        });
    }

    public void setShowToaster(boolean show) {
        toasterBox.setSelected(show);
    }

    public boolean showToaster() {
        return toasterBox.isSelected();
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
