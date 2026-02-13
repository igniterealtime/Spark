/**
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

import javax.swing.*;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.packet.id.StandardStanzaIdSource;
import org.jivesoftware.smack.util.XmlUtil;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.MessageDialog;

/**
 * Class to Send Raw packets useful when debugging
 *
 * @author wolf.posdorfer
 */
public class RawPacketSender {

    private static final String[] STANZA_SAMPLES = {
        "<presence xmlns=\"jabber:client\" id=\"\">\n" +
            "<show></show>\n" +
            "<status></status>\n" +
            "<priority></priority>\n" +
            "</presence>",
        "<message to=\"\" type=\"\" xmlns=\"jabber:client\" id=\"\">\n" +
            "<body></body>\n" +
            "</message>",
        "<iq to=\"\" type=\"\" xmlns=\"jabber:client\" id=\"\">\n" +
            "<query xmlns=\"\"></query>\n" +
            "</iq>",
        "<iq to=\"\" type=\"get\" xmlns=\"jabber:client\" id=\"\">\n" +
            "<query xmlns=\"http://jabber.org/protocol/disco#info\"></query>\n" +
            "</iq>",
        "<iq to=\"\" type=\"get\" xmlns=\"jabber:client\" id=\"\">\n" +
            "<query xmlns=\"jabber:iq:version\"></query>\n" +
            "</iq>"
    };
    private final JFrame _mainFrame;
    private final JPanel _mainPanel;

    private final JScrollPane _textScroller;

    private final JTextArea _textAreaLog;
    private final JTextArea _inputArea;
    private final JButton _btnSend;
    private final JButton _btnClearLog;
    private final JComboBox<String> _cbStanzas;

    public RawPacketSender() {
        _mainFrame = new JFrame("Send Raw Packets");
        _mainFrame.setIconImage(SparkRes.getImageIcon(SparkRes.MAIN_IMAGE).getImage());
        _mainPanel = new JPanel();
        _mainPanel.setLayout(new GridLayout(2, 1));
        _textAreaLog = new JTextArea();
        _inputArea = new JTextArea();
        _inputArea.setToolTipText("Leave the stanza id attribute empty to generate a new id");
        _textScroller = new JScrollPane(_textAreaLog);
        _btnSend = new JButton("Send", SparkRes.getImageIcon(SparkRes.SMALL_CHECK));
        _btnSend.addActionListener(e -> onBtnSendClick());
        _btnClearLog = new JButton("Clear", SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        _btnClearLog.addActionListener(e -> onBtnClearLogClick());
        _cbStanzas = new JComboBox<>(new String[] {
            "Presence",
            "Message",
            "IQ",
            "Discovery Info",
            "Get version",
        });
        _cbStanzas.addActionListener(e -> _inputArea.setText(STANZA_SAMPLES[_cbStanzas.getSelectedIndex()]));

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(_inputArea, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(_cbStanzas);
        buttonPanel.add(_btnClearLog);
        buttonPanel.add(_btnSend);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        _textAreaLog.setBackground(Color.LIGHT_GRAY);
        _mainPanel.add(_textScroller);
        _mainPanel.add(southPanel);

        _mainFrame.add(_mainPanel);
        _mainFrame.setSize(500, 500);
        _mainFrame.setLocationRelativeTo(null);
        _mainFrame.setVisible(true);
        // handle request and responses with the debugging it "id-X"
        StanzaFilter debugPacketFilter = stanza -> stanza.getStanzaId() != null && stanza.getStanzaId().endsWith("-X");
        SparkManager.getConnection().addAsyncStanzaListener(this::addRespToLog, debugPacketFilter);
    }

    private void addRespToLog(Stanza stanza) {
        SwingUtilities.invokeLater(() -> {
            _textAreaLog.append("\n" + XmlUtil.prettyFormatXml(stanza.toXML()));
        });
    }

    private void onBtnSendClick() {
        String reqText = _inputArea.getText();
        if (reqText.isBlank()) {
            return;
        }
        String debugStanzaId = StandardStanzaIdSource.DEFAULT.getNewStanzaId() + "-X";
        // replace empty id with generated id
        String rawXml = reqText.replace("id=\"\"", "id=\"" + debugStanzaId + "\"");
        AdHocPacket packetToSend = new AdHocPacket(rawXml);
        try {
            SparkManager.getConnection().sendStanza(packetToSend);
            _textAreaLog.append("\n" + XmlUtil.prettyFormatXml(rawXml));
        } catch (Exception exc) {
            MessageDialog.showErrorDialog(_mainFrame, exc);
        }
    }

    private void onBtnClearLogClick() {
        _textAreaLog.setText("");
    }

    /**
     * An ad-hoc stanza is like any regular stanza but with the exception that it's intention is
     * to be used only <b>to send packets</b>.<p>
     * <p/>
     * The whole text to send must be passed to the constructor. This implies that the client of
     * this class is responsible for sending a valid text to the constructor.
     */
    private static final class AdHocPacket extends Stanza {

        private final String text;

        /**
         * Create a new AdHocPacket with the text to send. The passed text must be a valid text to
         * send to the server, no validation will be done on the passed text.
         *
         * @param text the whole text of the stanza to send
         */
        private AdHocPacket(String text) {
            this.text = text;
        }

        @Override
        public String toXML(XmlEnvironment enclosingNamespace) {
            return text;
        }

        @Override
        public String toString() {
            return toXML((XmlEnvironment) null);
        }

        @Override
        public String getElementName() {
            return null;
        }
    }

}
