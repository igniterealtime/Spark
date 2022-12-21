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
package org.jivesoftware.spark.plugin.ofmeet;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.xml.bind.DatatypeConverter;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.ResourceUtils;

public class SparkMeetPreference implements Preference {
	public static final String NAMESPACE = "ofmeet";
	
	private SparkMeetPlugin plugin;
	private final PadePanel panel = new PadePanel();

	public SparkMeetPreference(SparkMeetPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void commit() {
		plugin.commit(panel.getUrl());	
	}

	@Override
	public Object getData() {
		return plugin.props;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public JComponent getGUI() {
		panel.setUrl(plugin.props.getProperty("url"));
		return panel;
	}

	@Override
	public Icon getIcon() {
		byte[] imageByte = DatatypeConverter.parseBase64Binary(ChatRoomDecorator.ICON_STRING);
		return new ImageIcon(imageByte);		
	}

	@Override
	public String getListName() {
		return SparkMeetResource.getString("name");
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String getTitle() {
		return SparkMeetResource.getString("name");
	}

	@Override
	public String getTooltip() {
		return SparkMeetResource.getString("name");
	}

	@Override
	public boolean isDataValid() {
		return true;
	}

	@Override
	public void load() {
	}

	@Override
	public void shutdown() {

	}
	
    private static class PadePanel extends JPanel {
 	private static final long serialVersionUID = -5992704440953686499L;
	private final JTextArea txtMessage = new JTextArea();
    private JLabel url = new JLabel(SparkMeetResource.getString("preference.url"));

        PadePanel() {
            txtMessage.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            txtMessage.setLineWrap(true);
            setLayout(new VerticalFlowLayout());
            setBorder(BorderFactory.createTitledBorder(SparkMeetResource.getString("preference.title")));
            add(url, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(txtMessage, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        }

        public void setUrl(String message) {
            txtMessage.setText(message);
        }

        public String getUrl() {
            return txtMessage.getText().trim();
        }
    }	

}
