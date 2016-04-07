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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
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
public class RoarPreferencePanel extends JPanel implements ChangeListener {

    private static final long serialVersionUID = -5334936099931215962L;

    private Image _backgroundimage;

    private JTextField _duration;
    private JTextField _amount;
    private JCheckBox _checkbox;

    private JList<ColorTypes> _colorlist;
    private JComboBox<String> _typelist;

    private ColorPick _colorpicker;

    private HashMap<ColorTypes, Color> _colormap;
    
    private HashMap<String, Object> _components;

    private String[] _typelistdata;

    private Insets INSETS;

    public RoarPreferencePanel() {

        _components = new HashMap<String, Object>();
        _colormap = new HashMap<ColorTypes, Color>();
        for (ColorTypes e : ColorTypes.values()) {
            _colormap.put(e, Color.BLACK);
        }

        JTabbedPane tabbedPane = new JTabbedPane();

        this.setLayout(new BorderLayout());

        ClassLoader cl = getClass().getClassLoader();
        _backgroundimage = new ImageIcon(cl.getResource("background2.png")).getImage();

        _colorpicker = new ColorPick(false);

        _colorpicker.addChangeListener(this);

        _duration = new JTextField();
        _amount = new JTextField();

        _checkbox = new JCheckBox(RoarResources.getString("roar.enabled"));

        ColorTypes[] colortypesdata = { ColorTypes.BACKGROUNDCOLOR, ColorTypes.HEADERCOLOR, ColorTypes.TEXTCOLOR };
        _colorlist = new JList<ColorTypes>(colortypesdata);

        _typelistdata = new String[3];
        _typelistdata[0] = TopRight.getLocalizedName();
        _typelistdata[1] = BottomRight.getLocalizedName();
        _typelistdata[2] = SparkToasterHandler.getLocalizedName();

        _typelist = new JComboBox<String>(_typelistdata);

        tabbedPane.add(RoarResources.getString("roar.settings"), makeGeneralSettingsPanel());
        tabbedPane.add("Group Chat", makeGroupChatPanel());
        tabbedPane.add("Keywords", makeKeyWordPanel());

        add(tabbedPane);

        _colorlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                colorListMouseClicked(e);
            }
        });
    }

    private JPanel makeGroupChatPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(0, 0, 0, 0));

        JCheckBox box = new JCheckBox("Use different Settings for Groupchat");
        panel.add(box, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH, INSETS, 0, 0));
        
        _components.put("group.enabled",box);

        // wrap in a vertical panel, to make settings look nicer
        JPanel verti = new JPanel(new VerticalFlowLayout());
        verti.add(panel);
        return verti;
    }

    private JPanel makeKeyWordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(0, 0, 0, 0));

        // wrap in a vertical panel, to make settings look nicer
        JPanel verti = new JPanel(new VerticalFlowLayout());
        verti.add(panel);
        return verti;
    }
    
    private JPanel makeGeneralSettingsPanel() {
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new GridBagLayout());
        generalPanel.setBackground(new Color(0, 0, 0, 0));
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
        INSETS = new Insets(5, 5, 5, 5);
        
        
        int rowcount = 0;
        // row
        generalPanel.add(_checkbox, new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH, INSETS, 0, 0));
        rowcount++;
        // row
        generalPanel.add(new JLabel(RoarResources.getString("roar.amount")), new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        generalPanel.add(_amount, new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH, INSETS, 0, 0));
        rowcount++;
        // row
        generalPanel.add(new JLabel(RoarResources.getString("roar.location")), new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        generalPanel.add(_typelist, new GridBagConstraints(1, rowcount, 1, 1, 0.8, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH, INSETS, 0, 0));

        rowcount = 0;
        JPanel singlePanel = new JPanel();
        singlePanel.setLayout(new GridBagLayout());
        singlePanel.setBackground(new Color(0, 0, 0, 0));
        singlePanel.setBorder(BorderFactory.createTitledBorder("Single"));
        // row
        singlePanel.add(_colorlist, new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH, INSETS, 0, 0));
        singlePanel.add(_colorpicker, new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH, INSETS, 0, 0));
        rowcount++;
        // row
        singlePanel.add(new JLabel(RoarResources.getString("roar.duration")), new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        singlePanel.add(_duration, new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH, INSETS, 0, 0));

        JPanel panel = new JPanel(new VerticalFlowLayout());
        panel.add(generalPanel);
        panel.add(singlePanel);
        return panel;
    }

    public void initializeValues() {
        RoarProperties props = RoarProperties.getInstance();
        
        setColor(RoarPreferencePanel.ColorTypes.BACKGROUNDCOLOR, props.getBackgroundColor());
        setColor(RoarPreferencePanel.ColorTypes.HEADERCOLOR, props.getHeaderColor());
        setColor(RoarPreferencePanel.ColorTypes.TEXTCOLOR, props.getTextColor());
        
        setDisplayType(props.getDisplayType());
        
        _checkbox.setSelected(props.getShowingPopups());
        _duration.setText("" + props.getDuration());
        _amount.setText("" + props.getMaximumPopups());
        
        retrieveComponent("group.enabled", JCheckBox.class).setSelected(props.getBoolean("group.enabled", false));
        
    }
    
    public void storeValues()
    {
        RoarProperties props = RoarProperties.getInstance();
        props.setDuration(this.getDuration());
        props.setShowingPopups(_checkbox.isSelected());
        props.setBackgroundColor(this.getColor(RoarPreferencePanel.ColorTypes.BACKGROUNDCOLOR));
        props.setHeaderColor(this.getColor(RoarPreferencePanel.ColorTypes.HEADERCOLOR));
        props.setTextColor(this.getColor(RoarPreferencePanel.ColorTypes.TEXTCOLOR));
        props.setDisplayType(this.getDisplayType());
        props.setMaximumPopups(this.getAmount());
        
        
        props.setBoolean("group.enabled", retrieveComponent("group.enabled", JCheckBox.class).isSelected());
        
        
        props.save();
    }
    
    @SuppressWarnings("unchecked")
    private <K> K retrieveComponent(String key, Class<K> classs) {
        return (K) _components.get(key);
    }

    /**
     * returns the Current Backgroundcolor of Popups
     * 
     * @return
     */
    public Color getColor() {
        return _colorpicker.getColor();
    }

    /**
     * Sets the Background Color for Popups
     * 
     * @param c
     */
    public void setBackgroundColor(Color c) {
        _colorpicker.setColor(c);
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
     * Set the Amount of Windows on Screen
     * 
     * @param am
     */
    public void setAmount(int am) {
        _amount.setText("" + am);
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
        ColorTypes key = (ColorTypes) _colorlist.getSelectedValue();
        _colorpicker.setColor(_colormap.get(key));
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

    // ====================================================================================
    // ====================================================================================
    // ====================================================================================
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

    public enum ColorTypes {
        BACKGROUNDCOLOR(RoarResources.getString("roar.background")), 
        HEADERCOLOR(RoarResources.getString("roar.header")), 
        TEXTCOLOR(RoarResources.getString("roar.text"));

        private String string;

        private ColorTypes(String c) {
            string = c;
        }

        public String toString() {
            return string;
        }

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JSlider) {
            _colormap.put((ColorTypes) _colorlist.getSelectedValue(), _colorpicker.getColor());
        }
    }

}
