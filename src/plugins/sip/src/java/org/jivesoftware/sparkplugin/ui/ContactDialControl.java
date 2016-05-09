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
package org.jivesoftware.sparkplugin.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import net.java.sipmack.common.Log;
import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.softphone.SoftPhoneManager;
import net.java.sipmack.softphone.listeners.InterlocutorListener;

import org.jivesoftware.resource.Default;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.phone.Phone;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.ui.ContactInfoWindow;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.sparkplugin.callhistory.HistoryCall;
import org.jivesoftware.sparkplugin.phonebook.ui.PhonebookUI;
import org.jivesoftware.sparkplugin.ui.call.CallHistoryUI;
import org.jivesoftware.sparkplugin.ui.call.CallManager;

/**
 *
 */
public class ContactDialControl extends JPanel implements InterlocutorListener, Phone {
	private static final long serialVersionUID = 8848248512306073866L;

	private TelephoneTextField callField;

    private RolloverButton callButton;
    private RolloverButton callHistoryButton;
    private RolloverButton voiceMailButton;

    private boolean incomingCall;
    private static final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    
    public ContactDialControl() {  
        setLayout(new GridBagLayout());

        // Add to PhoneManager.
        PhoneManager.getInstance().addPhone(this);

        callField = new TelephoneTextField();
        voiceMailButton = new RolloverButton(PhoneRes.getImageIcon("VOICEMAIL_IMAGE"));

        // make a button for the phonebook
  			RolloverButton phonebookButton = new RolloverButton(PhoneRes.getImageIcon("BOOKICON"));
  			phonebookButton.setToolTipText(PhoneRes.getIString("frame.title"));
  		
        callButton = new RolloverButton(PhoneRes.getImageIcon("DIAL_BUTTON_IMAGE"));
        callButton.setMargin(new Insets(0, 0, 0, 0));
        callButton.setDisabledIcon(PhoneRes.getImageIcon("DIAL_BUTTON_DISABLED_IMAGE"));

        callHistoryButton = new RolloverButton(PhoneRes.getImageIcon("HISTORY_IMAGE"));

        // Create Dial Section
        final JLabel callLabel = new JLabel(PhoneRes.getIString("phone.call")+":");
        callLabel.setForeground(new Color(64, 103, 162));
        callLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(callLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));

        add(callField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
        add(callButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));

        buttonPanel.add(phonebookButton);
        buttonPanel.add(new JLabel(PhoneRes.getImageIcon("DIVIDER_IMAGE")));
        buttonPanel.add(callHistoryButton);
        buttonPanel.add(voiceMailButton);
        buttonPanel.setOpaque(false);


        add(buttonPanel, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));

        voiceMailButton.setToolTipText(PhoneRes.getIString("phone.call"));
        callHistoryButton.setToolTipText(PhoneRes.getIString("phone.viewcallhistory"));

        voiceMailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                final SoftPhoneManager phoneManager = SoftPhoneManager.getInstance();
                String voiceMailNumber = phoneManager.getSipAccount().getVoiceMailNumber();
                if (ModelUtil.hasLength(voiceMailNumber)) {
                    phoneManager.getDefaultGuiManager().dial(voiceMailNumber);
                }
            }
        });

        callButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                placeCall();
            }
        });

        phonebookButton.addActionListener(new ActionListener(){
	  			public void actionPerformed(ActionEvent e) {
	  				try {
	  					EventQueue.invokeLater(new Runnable(){
	  						public void run() {
	  							// open the UI
	  							PhonebookUI book = PhonebookUI.getInstance();
	  							book.invoke();
	  						}
	  					});
	  				}
	  				catch(Exception ex) {
	  					Log.error(ex);
	  				}
	  			}
	  		});

        callHistoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCallList();
            }
        });

        setOpaque(false);

        // Set Line Border
        setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230), 1));

        Font font = new Font("Dialog", Font.BOLD, 11);
        voiceMailButton.setFont(font);
        voiceMailButton.setHorizontalTextPosition(JButton.RIGHT);

        // Listen for creation of new Interlocutor
        SoftPhoneManager.getInstance().addInterlocutorListener(this);


        callField.getTextComponent().addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent caretEvent) {
                callButton.setEnabled(ModelUtil.hasLength(callField.getText()) && callField.isEdited());
                callField.validateTextField();
            }
        });

        callField.getTextComponent().addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                if (!callField.getTextComponent().isEnabled() || !callField.isEdited() || !ModelUtil.hasLength(callField.getText())) {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    placeCall();
                }
            }
        });

        callButton.setEnabled(false);


        // Initialize CallManager.
        CallManager.getInstance();
    }

    private void placeCall() {
        if (incomingCall) {
            SoftPhoneManager.getInstance().getDefaultGuiManager().answer();
            enableIt(false);
        }
        else {
            final SoftPhoneManager phoneManager = SoftPhoneManager.getInstance();
            if (phoneManager.getInterlocutors().size() > 0) {
                phoneManager.getDefaultGuiManager().hangupAll();
            }
            else {
                phoneManager.getDefaultGuiManager().dial(callField.getText());
            }
        }
    }

    public void setVoiceMailLabel(String text) {
        voiceMailButton.setText(text);
    }

    public void setVoiceMailDescription(String description) {
        voiceMailButton.setToolTipText(description);
    }

    public void paintComponent(Graphics g) {
        final Image backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage();
        double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
        double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
        AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
        ((Graphics2D)g).drawImage(backgroundImage, xform, this);
    }


    public void interlocutorAdded(InterlocutorUI ic) {
        String callState = ic.getCallState();
        if (!ModelUtil.hasLength(callState)) {
            incomingCall = true;
            callField.getTextComponent().setEnabled(false);
            callButton.setEnabled(true);
            callButton.setToolTipText("Place phone call.");
        }
        else {
            incomingCall = false;
            showOnCall();
        }

        if (!callField.isEdited()) {
            callField.setText(ic.getCall().getNumber());
        }
    }

    public void interlocutorRemoved(InterlocutorUI interlocutorUI) {
        enableIt(true);
        incomingCall = false;

        //callField.reset();
    }

    private void enableIt(boolean enable) {
        callField.getTextComponent().setEnabled(enable);
        callButton.setEnabled(enable);
        callButton.setIcon(PhoneRes.getImageIcon("DIAL_BUTTON_IMAGE"));
        if (enable) {
            callButton.setToolTipText(PhoneRes.getIString("phone.placecall"));
        }
    }

    private void showOnCall() {
        callField.getTextComponent().setEnabled(false);
        callButton.setIcon(PhoneRes.getImageIcon("HANG_UP_PHONE_IMAGE"));
        callButton.setToolTipText(PhoneRes.getIString("phone.tips.hangup"));
    }

    private void showCallList() {
        CallHistoryUI callHistory = new CallHistoryUI();
        callHistory.invoke();
    }

    final Comparator<HistoryCall> itemComparator = new Comparator<HistoryCall>() {
        public int compare(HistoryCall contactItemOne, HistoryCall contactItemTwo) {
            final HistoryCall time1 = contactItemOne;
            final HistoryCall time2 = contactItemTwo;
            if (time1.getTime() < time2.getTime()) {
                return 1;
            }
            else if (time1.getTime() > time2.getTime()) {
                return -1;
            }
            return 0;

        }
    };

    /**
     * Used as the action to place Calls.
     */
    private class CallAction extends AbstractAction {
		private static final long serialVersionUID = -6558494299123278779L;
		private String number;

        public CallAction(String label, String number, Icon icon) {
            this.number = number;
            putValue(Action.NAME, "<html><b>" + label + "</b>&nbsp;&nbsp;" + number + "</html>");
            putValue(Action.SMALL_ICON, icon);
        }

        public void actionPerformed(ActionEvent e) {
            callField.setText(number);
            placeCall();
        }

    }


    public void handleContactInfo(final ContactInfoWindow contactInfo) {
       
    }

    public Collection<Action> getPhoneActions(String jid) {
        if(!isVisible()){
            return Collections.emptyList();
        }
        
        final VCard vcard = SparkManager.getVCardManager().getVCardFromMemory(jid);

        final List<Action> actions = new ArrayList<Action>();
        final String workNumber = vcard.getPhoneWork("VOICE");
        final String homeNumber = vcard.getPhoneHome("VOICE");
        final String cellNumber = vcard.getPhoneWork("CELL");

        if (ModelUtil.hasLength(homeNumber)) {
            Action dialHomeAction = new CallAction(PhoneRes.getIString("phone.home")+":", homeNumber, PhoneRes.getImageIcon("HOME_IMAGE"));
            actions.add(dialHomeAction);
        }

        if (ModelUtil.hasLength(workNumber)) {
            final Action dialWorkAction = new CallAction(PhoneRes.getIString("phone.work")+":", workNumber, PhoneRes.getImageIcon("WORK_IMAGE"));
            actions.add(dialWorkAction);
        }

        if (ModelUtil.hasLength(cellNumber)) {
            final Action dialCellAction = new CallAction(PhoneRes.getIString("phone.cell")+":", cellNumber, PhoneRes.getImageIcon("MOBILE_IMAGE"));
            actions.add(dialCellAction);
        }

        return actions;
    }

    public TelephoneTextField getCallField() {
        return callField;
    }
    
    public static void addButton(JButton btn) {
   	 buttonPanel.add(btn);
    }
    
}
