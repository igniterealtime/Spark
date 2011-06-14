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
package org.jivesoftware.spark.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.spark.SparkManager;

/**
 * Class to Send Raw packets useful when debugging
 * 
 * @author wolf.posdorfer
 * 
 */
public class RawPacketSender implements ActionListener {

    private JFrame _mainframe;
    private JPanel _mainpanel;

    private JScrollPane _textscroller;

    private JTextArea _textarea;
    private JTextArea _inputarea;
    private JButton _sendButton;
    private JButton _clear;

    public RawPacketSender() {

	_mainframe = new JFrame("Send Raw Packets");
	_mainframe.setIconImage(SparkRes.getImageIcon(SparkRes.MAIN_IMAGE)
		.getImage());
	_mainpanel = new JPanel();
	_mainpanel.setLayout(new GridLayout(2, 1));
	_textarea = new JTextArea();
	_inputarea = new JTextArea();
	_textscroller = new JScrollPane(_textarea);
	_sendButton = new JButton("Send",
		SparkRes.getImageIcon(SparkRes.SMALL_CHECK));
	_clear = new JButton("Clear",
		SparkRes.getImageIcon(SparkRes.SMALL_DELETE));

	createGUI();

    }

    private void createGUI() {

	JPanel southpanel = new JPanel(new BorderLayout());
	southpanel.add(_inputarea, BorderLayout.CENTER);

	JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	buttonpanel.add(_clear);
	buttonpanel.add(_sendButton);
	southpanel.add(buttonpanel, BorderLayout.SOUTH);

	_sendButton.addActionListener(this);
	_clear.addActionListener(this);

	_textarea.setBackground(Color.LIGHT_GRAY);
	_mainpanel.add(_textscroller);
	_mainpanel.add(southpanel);

	_mainframe.add(_mainpanel);
	_mainframe.setSize(500, 500);
	_mainframe.setLocationRelativeTo(null);
	_mainframe.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

	if (e.getSource().equals(_sendButton)) {
	    Packet p = new Packet() {

		@Override
		public String toXML() {
		    return _inputarea.getText();
		}
	    };

	    try {
		SparkManager.getConnection().sendPacket(p);
		_textarea.append("\n" + _inputarea.getText());
	    } catch (Exception exc) {

	    }
	}
	else if (e.getSource().equals(_clear)) {
	    _textarea.setText("");
	}

    }
    
//    <message to="manfred.mustermann@test">
//    <body>BUUUUUZ and Attention</body>
//    <buzz xmlns="http://www.jivesoftware.com/spark"/>
//    <attention xmlns='urn:xmpp:attention:0'/>
//    </message>

}
