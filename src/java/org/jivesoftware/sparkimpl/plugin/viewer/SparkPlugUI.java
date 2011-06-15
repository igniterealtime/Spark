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
package org.jivesoftware.sparkimpl.plugin.viewer;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.PluginManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.PublicPlugin;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;

public class SparkPlugUI extends JPanel {

	private static final long serialVersionUID = -4206533328807591854L;
	private PublicPlugin _plugin;
    private final JButton installButton = new JButton();
    private JLabel imageIcon = new JLabel();

    public SparkPlugUI(PublicPlugin plugin) {
        _plugin = plugin;

        setLayout(new GridBagLayout());
        setBackground(Color.white);

        JLabel titleLabel = new JLabel();
        JLabel versionLabel = new JLabel();
        JLabel descriptionLabel = new JLabel();

	imageIcon.setIcon(SparkRes.getImageIcon(SparkRes.PLUGIN_IMAGE));
        

        add(imageIcon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
        titleLabel.setFont(new Font("dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(80, 93, 198));

        add(versionLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        titleLabel.setText(plugin.getName());
	if (plugin.getVersion() != null && plugin.getAuthor() != null) {
	    versionLabel.setText(_plugin.getVersion() + " by "
		    + _plugin.getAuthor());
	}
        descriptionLabel.setText(plugin.getDescription());


        add(installButton, new GridBagConstraints(4, 0, 1, 2, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));


        if (_plugin.getChangeLog()!=null && _plugin.getReadMeURL() != null) {
            RolloverButton changeLogButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.CHANGELOG_IMAGE));
            RolloverButton readMeButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.README_IMAGE));


            changeLogButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                	BrowserLauncher.openURL(_plugin.getChangeLog());
                    }
                    catch (Exception e1) {
                        Log.error(e1);
                    }
                }
            });

            readMeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        BrowserLauncher.openURL(_plugin.getReadMeURL());
                    }
                    catch (Exception e1) {
                        Log.error(e1);
                    }
                }
            });


            final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setOpaque(false);
            buttonPanel.add(changeLogButton);
            buttonPanel.add(readMeButton);

            changeLogButton.setToolTipText(Res.getString("tooltip.view.changelog"));
            readMeButton.setToolTipText(Res.getString("tooltip.view.readme"));
            add(descriptionLabel, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
            add(buttonPanel, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        else {
            add(descriptionLabel, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
        }

        installButton.setVisible(false);
    }

    public void showOperationButton() {
        final PluginManager pluginManager = PluginManager.getInstance();
        if (!pluginManager.isInstalled(_plugin)) {
            installButton.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_ADD_IMAGE));
        }
        else {
            installButton.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        }
        installButton.setVisible(true);
    }

    public void setSelected(boolean isSelected) {
        if (isSelected) {
            setBackground(new Color(234, 230, 212));
            showOperationButton();
            setBorder(BorderFactory.createEtchedBorder());
	    if (Default.getBoolean(Default.DEINSTALL_PLUGINS_DISABLED)) {
		installButton.setVisible(false);
	    }
        }
        else {
            setBackground(Color.white);
            installButton.setVisible(false);
            setBorder(null);
        }
    }

    public void updateState() {
        showOperationButton();
    }


    public PublicPlugin getPlugin() {
        return _plugin;
    }

    public JButton getInstallButton() {
        return installButton;
    }

    public void useLocalIcon() {
        File pluginDIR = _plugin.getPluginDir();
        try {
            File smallIcon = new File(pluginDIR, "logo_small.gif");
            File largeIcon = new File(pluginDIR, "logo_large.gif");
            if (largeIcon.exists()) {
                setIcon(new ImageIcon(largeIcon.toURI().toURL()));
            }
            else if (smallIcon.exists()) {
                setIcon(new ImageIcon(smallIcon.toURI().toURL()));
            }
        }
        catch (MalformedURLException e) {
            Log.error(e);
        }

    }

    /**
     * Returns the Filename of provided Download URL
     * @return
     */
    public String getFilename() {
        String filename = null;
        try {
            URL downloadURL = new URL(_plugin.getDownloadURL());
            filename = URLFileSystem.getFileName(downloadURL);
        }
        catch (MalformedURLException e) {
            // Nothing to do
        }
        return filename;
    }

    public void setIcon(Icon icon) {
        imageIcon.setIcon(icon);
    }
}
