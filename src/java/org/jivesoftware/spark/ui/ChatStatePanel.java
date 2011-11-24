package org.jivesoftware.spark.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smackx.ChatState;

public class ChatStatePanel extends JPanel{
	private JLabel label;
	
	public ChatStatePanel(ChatState state) {
		setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		label = new JLabel(Res.getString(state.name()));
		label.setFont(new Font("Courier New", Font.PLAIN, 9));
		label.setForeground(Color.gray);
		label.setHorizontalTextPosition(JLabel.LEFT);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		add(label);
	}
}
