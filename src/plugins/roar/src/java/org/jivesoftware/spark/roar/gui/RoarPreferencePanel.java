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
package org.jivesoftware.spark.roar.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.roar.RoarProperties;
import org.jivesoftware.spark.roar.RoarResources;
import org.jivesoftware.spark.roar.displaytype.BottomRight;
import org.jivesoftware.spark.roar.displaytype.SparkToasterHandler;
import org.jivesoftware.spark.roar.displaytype.TopRight;
import org.jivesoftware.spark.util.ColorPick;

/**
 * Super Awesome Preference Panel
 * 
 * @author wolf.posdorfer
 * 
 */
public class RoarPreferencePanel extends JPanel {

    private static final long serialVersionUID = -5334936099931215962L;

    private Image _backgroundimage;

    private JTextField _duration;
    private JTextField _amount;
    private JCheckBox _enabledCheckbox;

    private JComboBox<String> _typelist;

    private JList<ColorTypes> _singleColorlist;
    private ColorPick _singleColorpicker;

    private HashMap<ColorTypes, Color> _colormap;

    private HashMap<String, Object> _components;

    private String[] _typelistdata;

    private Insets INSETS = new Insets(5, 5, 5, 5);

    public RoarPreferencePanel() {

        _components = new HashMap<String, Object>();
        _colormap = new HashMap<ColorTypes, Color>();
        for (ColorTypes e : ColorTypes.values()) {
            _colormap.put(e, Color.BLACK);
        }

        this.setLayout(new BorderLayout());

        ClassLoader cl = getClass().getClassLoader();
        _backgroundimage = new ImageIcon(cl.getResource("background2.png")).getImage();

        _duration = new JTextField();
        _amount = new JTextField();
        _enabledCheckbox = new JCheckBox(RoarResources.getString("roar.enabled"));

        _singleColorpicker = new ColorPick(false);
        _singleColorpicker.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                stateChangedSingleColorPicker(e);
            }
        });

        DefaultListModel<ColorTypes> listModel = new DefaultListModel<>();
        listModel.addElement(ColorTypes.BACKGROUNDCOLOR);
        listModel.addElement(ColorTypes.HEADERCOLOR);
        listModel.addElement(ColorTypes.TEXTCOLOR);
        _singleColorlist = new JList<>(listModel);

        _typelistdata = new String[3];
        _typelistdata[0] = TopRight.getLocalizedName();
        _typelistdata[1] = BottomRight.getLocalizedName();
        _typelistdata[2] = SparkToasterHandler.getLocalizedName();

        _typelist = new JComboBox<String>(_typelistdata);

        add(makeGeneralSettingsPanel());
        _singleColorlist.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                colorListMouseClicked(e);
            }
        });
    }

    private JComponent makeGeneralSettingsPanel() {
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new GridBagLayout());
        generalPanel.setBackground(new Color(0, 0, 0, 0));
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));

        int rowcount = 0;
        generalPanel.add(_enabledCheckbox,
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        rowcount++;
        generalPanel.add(new JLabel(RoarResources.getString("roar.amount")),
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        generalPanel.add(_amount,
                new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        rowcount++;
        generalPanel.add(new JLabel(RoarResources.getString("roar.location")),
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        generalPanel.add(_typelist,
                new GridBagConstraints(1, rowcount, 1, 1, 0.8, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        JPanel panel = new JPanel(new VerticalFlowLayout());
        panel.add(generalPanel);
        panel.add(makeSinglePanel());
        panel.add(makeGroupChatPanel());
        panel.add(makeKeyWordPanel());

        JScrollPane scroll = new JScrollPane(panel);

        return scroll;
    }

    private JPanel makeSinglePanel() {
        JPanel singlePanel = new JPanel();
        singlePanel.setLayout(new GridBagLayout());
        singlePanel.setBackground(new Color(0, 0, 0, 0));
        singlePanel.setBorder(BorderFactory.createTitledBorder("Single"));
        JCheckBox disableSingle = new JCheckBox("Disable Roar for single-user chats");

        // row
        int rowcount = 0;
        singlePanel.add(_singleColorlist,
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        singlePanel.add(_singleColorpicker,
                new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        rowcount++;
        singlePanel.add(new JLabel(RoarResources.getString("roar.duration")),
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        singlePanel.add(_duration,
                new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        rowcount++;
        singlePanel.add(disableSingle,
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        _components.put("roar.disable.single", disableSingle);
        return singlePanel;
    }

    private JPanel makeGroupChatPanel() {
        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new GridBagLayout());
        groupPanel.setBackground(new Color(0, 0, 0, 0));
        groupPanel.setBorder(BorderFactory.createTitledBorder("Group"));

        JCheckBox enableDifferentGroup = new JCheckBox("Use different settings for multi-user chats");
        JCheckBox disableGroup = new JCheckBox("Disable Roar for multi-user chats");
        JTextField durationGroup = new JTextField();

        enableDifferentGroup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDifferentSettingsForGroup(enableDifferentGroup.isSelected());
            }
        });

        int rowcount = 0;
        groupPanel.add(enableDifferentGroup,
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        rowcount++;
        groupPanel.add(new JLabel(RoarResources.getString("roar.duration")),
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        groupPanel.add(durationGroup,
                new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        rowcount++;
        groupPanel.add(disableGroup,
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        _components.put("group.different.enabled", enableDifferentGroup);
        _components.put("group.duration", durationGroup);
        _components.put("group.disable", disableGroup);

        return groupPanel;
    }

    private JPanel makeKeyWordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Keyword"));

        JCheckBox differentKeyword = new JCheckBox("Use different settings for keywords");
        differentKeyword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDifferentSettingsForKeyword(differentKeyword.isSelected());
            }
        });

        JTextField durationKeyword = new JTextField();
        JTextField keywords = new JTextField();

        int rowcount = 0;
        panel.add(differentKeyword,
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        rowcount++;
        panel.add(new JLabel(RoarResources.getString("roar.duration")),
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        panel.add(durationKeyword,
                new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        rowcount++;
        panel.add(new JLabel("Keywords (comma separated)"),
                new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        panel.add(keywords, new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        _components.put("keyword.different.enabled", differentKeyword);
        _components.put("keyword.duration", durationKeyword);
        _components.put("keywords", keywords);

        return panel;
    }

    public void initializeValues() {
        RoarProperties props = RoarProperties.getInstance();

        _enabledCheckbox.setSelected(props.getShowingPopups());
        _amount.setText("" + props.getMaximumPopups());
        setDisplayType(props.getDisplayType());

        setColor(ColorTypes.BACKGROUNDCOLOR, props.getBackgroundColor());
        setColor(ColorTypes.BACKGROUNDCOLOR_GROUP, props.getColor(RoarProperties.BACKGROUNDCOLOR_GROUP, props.getBackgroundColor()));
        setColor(ColorTypes.BACKGROUNDCOLOR_KEYWORD, props.getColor(RoarProperties.BACKGROUNDCOLOR_KEYWORD, props.getBackgroundColor()));
        setColor(ColorTypes.HEADERCOLOR, props.getHeaderColor());
        setColor(ColorTypes.HEADERCOLOR_GROUP, props.getColor(RoarProperties.HEADERCOLOR_GROUP, props.getHeaderColor()));
        setColor(ColorTypes.HEADERCOLOR_KEYWORD, props.getColor(RoarProperties.HEADERCOLOR_KEYWORD, props.getHeaderColor()));
        setColor(ColorTypes.TEXTCOLOR, props.getTextColor());
        setColor(ColorTypes.TEXTCOLOR_GROUP, props.getColor(RoarProperties.TEXTCOLOR_GROUP, props.getTextColor()));
        setColor(ColorTypes.TEXTCOLOR_GROUP, props.getColor(RoarProperties.TEXTCOLOR_KEYWORD, props.getTextColor()));

        retrieveComponent("roar.disable.single", JCheckBox.class).setSelected(props.getBoolean("roar.disable.single", false));
        _duration.setText("" + props.getDuration());
        retrieveComponent("keyword.duration", JTextField.class).setText("" + props.getInt("keyword.duration"));
        retrieveComponent("group.duration", JTextField.class).setText("" + props.getInt("group.duration"));
        retrieveComponent("keywords", JTextField.class).setText(props.getProperty("keywords"));

        retrieveComponent("group.disable", JCheckBox.class).setSelected(props.getBoolean("group.disable", false));

        boolean group_different_enabled = props.getBoolean("group.different.enabled", false);
        retrieveComponent("group.different.enabled", JCheckBox.class).setSelected(group_different_enabled);
        toggleDifferentSettingsForGroup(group_different_enabled);

        boolean keyword_different_enabled = props.getBoolean("keyword.different.enabled", false);
        retrieveComponent("keyword.different.enabled", JCheckBox.class).setSelected(keyword_different_enabled);
        toggleDifferentSettingsForKeyword(keyword_different_enabled);
    }

    public void storeValues() {
        RoarProperties props = RoarProperties.getInstance();
        props.setShowingPopups(_enabledCheckbox.isSelected());
        props.setDisplayType(this.getDisplayType());
        props.setMaximumPopups(this.getAmount());

        props.setDuration(this.getDuration());
        props.setInt("group.duration", getIntFromTextField("group.duration"));
        props.setInt("keyword.duration", getIntFromTextField("keyword.duration"));

        props.setProperty("keywords", retrieveComponent("keywords", JTextField.class).getText());

        props.setBackgroundColor(getColor(ColorTypes.BACKGROUNDCOLOR));
        props.setColor(RoarProperties.BACKGROUNDCOLOR_GROUP, getColor(ColorTypes.BACKGROUNDCOLOR_GROUP));
        props.setColor(RoarProperties.BACKGROUNDCOLOR_KEYWORD, getColor(ColorTypes.BACKGROUNDCOLOR_KEYWORD));
        props.setTextColor(getColor(ColorTypes.TEXTCOLOR));
        props.setColor(RoarProperties.TEXTCOLOR_GROUP, getColor(ColorTypes.TEXTCOLOR_GROUP));
        props.setColor(RoarProperties.TEXTCOLOR_KEYWORD, getColor(ColorTypes.TEXTCOLOR_KEYWORD));
        props.setHeaderColor(getColor(ColorTypes.HEADERCOLOR));
        props.setColor(RoarProperties.HEADERCOLOR_GROUP, getColor(ColorTypes.HEADERCOLOR_GROUP));
        props.setColor(RoarProperties.HEADERCOLOR_KEYWORD, getColor(ColorTypes.HEADERCOLOR_KEYWORD));

        props.setBoolean("roar.disable.single", retrieveComponent("roar.disable.single", JCheckBox.class).isSelected());
        props.setBoolean("group.different.enabled", retrieveComponent("group.different.enabled", JCheckBox.class).isSelected());
        props.setBoolean("keyword.different.enabled", retrieveComponent("keyword.different.enabled", JCheckBox.class).isSelected());
        props.setBoolean("group.disable", retrieveComponent("group.disable", JCheckBox.class).isSelected());

        props.save();
    }

    @SuppressWarnings("unchecked")
    private <K> K retrieveComponent(String key, Class<K> classs) {
        return (K) _components.get(key);
    }

    private int getIntFromTextField(String key) {
        JTextField field = retrieveComponent(key, JTextField.class);

        try {
            return Integer.parseInt(field.getText());
        } catch (Exception e) {
            return 3000;
        }
    }

    /**
     * returns the popup duration
     * 
     * @return int
     */
    public int getDuration() {
        try {
            return Integer.parseInt(_duration.getText());
        } catch (Exception e) {
            return 3000;
        }
    }

    /**
     * Amount of Windows on Screen
     * 
     * @return int
     */
    public int getAmount() {
        return Integer.parseInt(_amount.getText());
    }

    public Color getColor(ColorTypes type) {
        return _colormap.get(type);
    }

    public void setColor(ColorTypes type, Color color) {
        _colormap.put(type, color);
    }

    private void colorListMouseClicked(MouseEvent e) {
        if (e.getSource() == _singleColorlist) {
            ColorTypes key = (ColorTypes) _singleColorlist.getSelectedValue();
            _singleColorpicker.setColor(_colormap.get(key));
        }
    }

    public void setDisplayType(String t) {
        if (t.equals(TopRight.getName())) {
            _typelist.setSelectedItem(TopRight.getLocalizedName());
        } else if (t.equals(SparkToasterHandler.getName())) {
            _typelist.setSelectedItem(SparkToasterHandler.getLocalizedName());
        } else {
            _typelist.setSelectedItem(BottomRight.getLocalizedName());
        }
    }

    public String getDisplayType() {
        String o = (String) _typelist.getSelectedItem();
        if (o.equals(TopRight.getLocalizedName())) {
            return TopRight.getName();
        } else if (o.equals(SparkToasterHandler.getLocalizedName())) {
            return SparkToasterHandler.getName();
        } else {
            return BottomRight.getName();
        }
    }

    private void toggleDifferentSettingsForKeyword(boolean isSelected) {

        DefaultListModel<ColorTypes> model = (DefaultListModel<ColorTypes>) _singleColorlist.getModel();
        JTextField duration = retrieveComponent("keyword.duration", JTextField.class);

        if (isSelected) {
            if (!model.contains(ColorTypes.BACKGROUNDCOLOR_KEYWORD)) {
                model.addElement(ColorTypes.BACKGROUNDCOLOR_KEYWORD);
                model.addElement(ColorTypes.HEADERCOLOR_KEYWORD);
                model.addElement(ColorTypes.TEXTCOLOR_KEYWORD);
            }
            duration.setEnabled(true);
        } else {
            model.removeElement(ColorTypes.BACKGROUNDCOLOR_KEYWORD);
            model.removeElement(ColorTypes.HEADERCOLOR_KEYWORD);
            model.removeElement(ColorTypes.TEXTCOLOR_KEYWORD);
            duration.setEnabled(false);
            duration.setText(_duration.getText());
        }
    }

    private void toggleDifferentSettingsForGroup(boolean isSelected) {

        DefaultListModel<ColorTypes> model = (DefaultListModel<ColorTypes>) _singleColorlist.getModel();
        JTextField duration = retrieveComponent("group.duration", JTextField.class);

        if (isSelected) {
            if (!model.contains(ColorTypes.BACKGROUNDCOLOR_GROUP)) {
                model.addElement(ColorTypes.BACKGROUNDCOLOR_GROUP);
                model.addElement(ColorTypes.HEADERCOLOR_GROUP);
                model.addElement(ColorTypes.TEXTCOLOR_GROUP);
            }
            duration.setEnabled(true);
        } else {
            model.removeElement(ColorTypes.BACKGROUNDCOLOR_GROUP);
            model.removeElement(ColorTypes.HEADERCOLOR_GROUP);
            model.removeElement(ColorTypes.TEXTCOLOR_GROUP);
            duration.setEnabled(false);
            duration.setText(_duration.getText());
        }
    }

    private void stateChangedSingleColorPicker(ChangeEvent e) {
        if (e.getSource() instanceof JSlider) {
            _colormap.put((ColorTypes) _singleColorlist.getSelectedValue(), _singleColorpicker.getColor());
        }
    }

    // ============================================================================================================
    // ============================================================================================================
    // ============================================================================================================
    public void paintComponent(Graphics g) {
        // CENTER LOGO
        // int imgwi = _backgroundimage.getWidth(null);
        // int imghe = _backgroundimage.getHeight(null);
        // int x = this.getSize().width;
        // x = (x/2)-(imgwi/2) < 0 ? 0 : (x/2)-(imgwi/2) ;
        //
        // int y = this.getSize().height;
        // y = (y/2) -(imghe/2)< 0 ? 0 : y/2-(imghe/2) ;

        // LOGO in bottom right corner

        int x = this.getSize().width - _backgroundimage.getWidth(null);
        int y = this.getSize().height - _backgroundimage.getHeight(null);

        super.paintComponent(g);
        g.drawImage(_backgroundimage, x, y, this);
    }

    // ============================================================================================================
    // ============================================================================================================
    // ============================================================================================================
    public enum ColorTypes {
        BACKGROUNDCOLOR(RoarResources.getString("roar.background")), HEADERCOLOR(RoarResources.getString("roar.header")), TEXTCOLOR(
                RoarResources.getString("roar.text")), BACKGROUNDCOLOR_GROUP("roar.background.group"), HEADERCOLOR_GROUP(
                        "roar.header.group"), TEXTCOLOR_GROUP("roar.text.group"), BACKGROUNDCOLOR_KEYWORD(
                                "roar.background.keyword"), HEADERCOLOR_KEYWORD("roar.header.keyword"), TEXTCOLOR_KEYWORD("roar.text.keyword");

        private String string;

        private ColorTypes(String c) {
            string = c;
        }

        public String toString() {
            return string;
        }

    }

}
