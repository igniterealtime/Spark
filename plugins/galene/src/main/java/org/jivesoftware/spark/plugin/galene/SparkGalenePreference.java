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
package org.jivesoftware.spark.plugin.galene;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.xml.bind.DatatypeConverter;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.ResourceUtils;

public class SparkGalenePreference implements Preference {
	public static final String NAMESPACE = "galene";
	
	private SparkGalenePlugin plugin;
	private final GalenePanel panel = new GalenePanel();

	public SparkGalenePreference(SparkGalenePlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void commit() {
		plugin.commit(panel.getUrl(), panel.getHost());	
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
		panel.setHost(plugin.props.getProperty("hostname"));		
		return panel;
	}

	@Override
	public Icon getIcon() {
		byte[] imageByte = DatatypeConverter.parseBase64Binary(ChatRoomDecorator.ICON_STRING);
		return new ImageIcon(imageByte);		
	}

	@Override
	public String getListName() {
		return SparkGaleneResource.getString("name");
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String getTitle() {
		return SparkGaleneResource.getString("name");
	}

	@Override
	public String getTooltip() {
		return SparkGaleneResource.getString("name");
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
	
    private static class GalenePanel extends JPanel {
 	private static final long serialVersionUID = -5992704440953686499L;
	private final JTextArea txtUrl = new JTextArea();
	private final JTextArea txtHost = new JTextArea();	
    private JLabel url = new JLabel(SparkGaleneResource.getString("preference.url"));
    private JLabel host = new JLabel(SparkGaleneResource.getString("preference.host"));
	
        GalenePanel() {
            txtUrl.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            txtUrl.setLineWrap(true);
            txtHost.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            txtHost.setLineWrap(true);
			
            setLayout(new VerticalFlowLayout());
            setBorder(BorderFactory.createTitledBorder(SparkGaleneResource.getString("preference.title")));
            add(url, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(txtUrl, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
			
            add(host, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(txtHost, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
			
        }

        public void setUrl(String message) {
            txtUrl.setText(message);
        }

        public String getUrl() {
            return txtUrl.getText().trim();
        }
		
        public void setHost(String message) {
            txtHost.setText(message);
        }

        public String getHost() {
            return txtHost.getText().trim();
        }
    }	

}
