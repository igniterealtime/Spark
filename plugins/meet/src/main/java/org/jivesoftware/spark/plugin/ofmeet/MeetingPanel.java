package org.jivesoftware.spark.plugin.ofmeet;

import org.jivesoftware.spark.component.VerticalFlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;

class MeetingPanel extends JPanel {
    private final JTextField txtMeetingsBaseUrl = new JTextField();

    MeetingPanel() {
//        txtMeetingsBaseUrl.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
        setLayout(new VerticalFlowLayout());
        setBorder(BorderFactory.createTitledBorder(SparkMeetResource.getString("preference.title")));
        JLabel lblMeetingsBaseUrl = new JLabel(SparkMeetResource.getString("preference.url"));
        Insets insets = new Insets(5, 5, 5, 5);
        add(lblMeetingsBaseUrl, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, NORTHWEST, NONE, insets, 0, 0));
        add(txtMeetingsBaseUrl, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, NORTHWEST, NONE, insets, 0, 0));
    }

    public void setMeetingsBaseUrl(String meetingsBaseUrl) {
        txtMeetingsBaseUrl.setText(meetingsBaseUrl);
    }

    public String getMeetingsBaseUrl() {
        return txtMeetingsBaseUrl.getText().trim();
    }
}
