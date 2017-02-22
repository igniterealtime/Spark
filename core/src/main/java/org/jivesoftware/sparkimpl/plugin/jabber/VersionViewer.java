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
package org.jivesoftware.sparkimpl.plugin.jabber;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.IQReplyFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.time.packet.Time;
import org.jivesoftware.smackx.iqversion.packet.Version;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;

import java.awt.*;
import java.text.SimpleDateFormat;

public class VersionViewer {

    private VersionViewer() {

    }

    public static void viewVersion(String jid) {
        final JPanel loadingCard = new JPanel();
        final ImageIcon icon = new ImageIcon( VersionViewer.class.getClassLoader().getResource( "images/ajax-loader.gif"));
        loadingCard.add(new JLabel("loading... ", icon, JLabel.CENTER));
        loadingCard.setVisible(true);

        final JPanel dataCard = new JPanel();
        dataCard.setVisible( false );
        dataCard.setLayout(new GridBagLayout());

        JLabel timeLabel = new JLabel();
        JLabel softwareLabel = new JLabel();
        JLabel versionLabel = new JLabel();
        JLabel osLabel = new JLabel();

        final JTextField timeField = new JTextField();
        final JTextField softwareField = new JTextField();
        final JTextField versionField = new JTextField();
        final JTextField osField = new JTextField();

        // Add resources
        ResourceUtils.resLabel(timeLabel, timeField, Res.getString("label.local.time") + ":");
        ResourceUtils.resLabel(softwareLabel, softwareField, Res.getString("label.software") + ":");
        ResourceUtils.resLabel(versionLabel, versionField, Res.getString("label.version") + ":");
        ResourceUtils.resLabel(osLabel, osField, Res.getString("label.os") + ":");

        // Add Time Label
        dataCard.add(timeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        dataCard.add(timeField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        dataCard.add(softwareLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        dataCard.add(softwareField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));


        dataCard.add(versionLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        dataCard.add(versionField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));


        dataCard.add(osLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        dataCard.add(osField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        osField.setEditable(false);
        versionField.setEditable(false);
        softwareField.setEditable(false);
        timeField.setEditable(false);

        final JPanel cards = new JPanel(new CardLayout());
        cards.add(loadingCard);
        cards.add(dataCard);

        final XMPPConnection connection = SparkManager.getConnection();
        try
        {
            // Load Version
            final Version versionRequest = new Version();
            versionRequest.setType(IQ.Type.get);
            versionRequest.setTo(jid);

            connection.sendStanzaWithResponseCallback( versionRequest, new IQReplyFilter( versionRequest, connection ), stanza -> {
                final Version versionResult = (Version) stanza;
                softwareField.setText(versionResult.getName());
                versionField.setText(versionResult.getVersion());
                osField.setText(versionResult.getOs());
                ((CardLayout)(cards.getLayout())).last( cards );
            } );

            // Time
            final Time time = new Time();
            time.setType(IQ.Type.get);
            time.setTo(jid);

            connection.sendStanzaWithResponseCallback( time, new IQReplyFilter( time, connection ), stanza -> {;
                timeField.setText( new SimpleDateFormat( ).format( ((Time)stanza).getTime()));
                ((CardLayout)(cards.getLayout())).last( cards );
            } );
        }
        catch ( SmackException.NotConnectedException e )
        {
            Log.warning( "Unable to query for version.", e );
            ((CardLayout)(cards.getLayout())).last( cards );
        }

        MessageDialog.showComponent(Res.getString("title.version.and.time"), Res.getString("message.client.information", UserManager.unescapeJID(jid)), SparkRes.getImageIcon(SparkRes.PROFILE_IMAGE_24x24), cards, SparkManager.getMainWindow(), 400, 300, false);
    }

}
