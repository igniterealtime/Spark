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

package org.jivesoftware.spark;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.Smack;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.sparkimpl.settings.JiveInfo;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import java.awt.Font;

public class AboutDialog {

    private AboutDialog() {
    }

    /**
     * Displays the About Box for Spark.
     */
    public static void showAboutBox() {
        JOptionPane.showMessageDialog(SparkManager.getMainWindow(), getAboutBoxPane(),
            Res.getString("title.about"), JOptionPane.INFORMATION_MESSAGE, SparkRes.getImageIcon(SparkRes.Icon.MAIN_IMAGE));
    }

    /**
     * Returns About Box Pane.
     */
    private static JEditorPane getAboutBoxPane() {
        // Get values from default.properties file
        String applicationInfo1 = Default.getString(Default.APPLICATION_INFO1);
        String applicationInfo2 = Default.getString(Default.APPLICATION_INFO2);
        String applicationInfo3 = Default.getString(Default.APPLICATION_INFO3);
        String applicationLicenseLink = Default.getString(Default.APPLICATION_LICENSE_LINK);
        String applicationLicenseLinkTxt = Default.getString(Default.APPLICATION_LICENSE_LINK_TXT);
        String applicationInfo4 = Default.getString(Default.APPLICATION_INFO4);
        String applicationLink = Default.getString(Default.APPLICATION_LINK);
        String applicationLinkTxt = Default.getString(Default.APPLICATION_LINK_TXT);
        boolean displayDevInfo = Default.getBoolean(Default.DISPLAY_DEV_INFO);
        String javaVersion = Default.getString(Default.JAVA_VERSION);

        // Construct About Box text
        StringBuilder aboutBoxText = new StringBuilder();
        aboutBoxText.append(JiveInfo.getName()).append(" ").append(JiveInfo.getVersion());
        if (!("".equals(applicationInfo1))) {
            aboutBoxText.append("<br/>").append(applicationInfo1);
        }
        if (!("".equals(applicationInfo2))) {
            aboutBoxText.append("<br/>").append(applicationInfo2);
        }
        if (!("".equals(applicationInfo3))) {
            aboutBoxText.append("<br/>").append(applicationInfo3);
        }
        if (!("".equals(applicationLicenseLink))) {
            aboutBoxText.append("<br/><a href=\"").append(applicationLicenseLink).append("\">")
                .append(applicationLicenseLinkTxt).append("</a>");
        }
        if (!("".equals(applicationLink))) {
            aboutBoxText.append("<br/><a href=\"").append(applicationLink).append("\">")
                .append(applicationLinkTxt).append("</a>");
        }
        if (!("".equals(applicationInfo4))) {
            aboutBoxText.append("<br/>").append(applicationInfo4);
        }
        aboutBoxText.append("<br/>Smack Version: ").append(Smack.getVersion());
        if (displayDevInfo) {
            if ("".equals(javaVersion)) {
                aboutBoxText.append("<br/>JRE Version: ").append(System.getProperty("java.version"));
                aboutBoxText.append(" ").append(System.getProperty("os.arch"));
            }
        }

        JPanel p = new JPanel();
        Font font = p.getFont();

        String style = "font-family:" + font.getFamily() + ";" +
            "font-weight:" + (font.isBold() ? "bold" : "normal") + ";" +
            "font-size:" + font.getSize() + "pt;";

        JEditorPane ep = new JEditorPane("text/html",
            "<html><body style=\"" + style + "\">" + aboutBoxText + "</body></html>");
        ep.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                BrowserLauncher.openURL(e.getURL().toString());
            }
        });
        ep.setEditable(false);
        ep.setBackground(p.getBackground());
        return ep;
    }
}
