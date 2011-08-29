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
package org.jivesoftware.spark.ui.themes;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.Emoticon;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;


/**
 * ThemePanel is used for the setting of TranscriptWindows and Emoticon packs.
 */
public class ThemePanel extends JPanel {
    private static final long serialVersionUID = 2943854311454590459L;

    private TranscriptWindow emoticonpreviewtranscript;

    private JComboBox messageStyleBox;

    private JComboBox emoticonBox;

    private JButton addEmoticonButton;

    private JTextField contactListFontField;
    private JLabel contactListFontLabel;

    private JTextField chatRoomFontField;
    private JLabel chatRoomFontLabel;

    private JCheckBox emoticonCheckBox;
    private JFileChooser fc;

    private JCheckBox showAvatarsBox;
    private JCheckBox showVCards;
    private JLabel avatarSizeLabel;
    private JComboBox avatarSizeField;

    private JLabel _lookandfeelLabel;
    private JComboBox _lookandfeel;
    private JButton _lookandfeelpreview;
    private Vector<String> _lookandfeelname = new Vector<String>();
    private JCheckBox _useTabsForTransports;
    private JCheckBox _useTabsForConference;

    private ThemePanel _themepanel;

    private JComboBox _showReconnectBox;

    private LocalPreferences pref = SettingsManager.getLocalPreferences();

    private JScrollPane emoticonscrollpane;

    private JPanel emoticonspanel;

    /**
     * Construct UI
     */
    public ThemePanel() {

	_themepanel = this;
        _themepanel.setLayout(new GridBagLayout());

        LookAndFeelInfo[]  ui = UIManager.getInstalledLookAndFeels();


        Vector<String> lafname = new Vector<String>();

        for(int i=0;i<ui.length;i++)
        {
            _lookandfeelname.add(ui[i].getClassName());
          lafname.add(ui[i].getName());
        }

	String[] nonSystemLookAndFeels = {
		//JTattoo
		//"com.jtattoo.plaf.acryl.AcrylLookAndFeel",
		"com.jtattoo.plaf.aero.AeroLookAndFeel",
		"com.jtattoo.plaf.aluminium.AluminiumLookAndFeel",
		//"com.jtattoo.plaf.bernstein.BernsteinLookAndFeel",
		"com.jtattoo.plaf.fast.FastLookAndFeel",
		//"com.jtattoo.plaf.graphite.GraphiteLookAndFeel",
		//"com.jtattoo.plaf.hifi.HiFiLookAndFeel",
		"com.jtattoo.plaf.luna.LunaLookAndFeel",
		"com.jtattoo.plaf.mcwin.McWinLookAndFeel",
		"com.jtattoo.plaf.mint.MintLookAndFeel",
		//"com.jtattoo.plaf.noire.NoireLookAndFeel",
		"com.jtattoo.plaf.smart.SmartLookAndFeel",
		//Substance
		//"org.jvnet.substance.skin.SubstanceAutumnLookAndFeel",
		"org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel",
		"org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel",
		"org.jvnet.substance.skin.SubstanceBusinessLookAndFeel",
		//"org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel",
		"org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel",
		"org.jvnet.substance.skin.SubstanceCremeLookAndFeel",
		"org.jvnet.substance.skin.SubstanceDustCoffeeLookAndFeel",
		"org.jvnet.substance.skin.SubstanceDustLookAndFeel",
		//"org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel",
		"org.jvnet.substance.api.skin.SubstanceGeminiLookAndFeel",
		"org.jvnet.substance.api.skin.SubstanceGraphiteAquaLookAndFeel",
		//"org.jvnet.substance.skin.SubstanceMagmaLookAndFeel",
		//"org.jvnet.substance.api.skin.SubstanceMagellanLookAndFeel",
		"org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel",
		"org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel",
		"org.jvnet.substance.skin.SubstanceModerateLookAndFeel",
		"org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel",
		"org.jvnet.substance.skin.SubstanceNebulaLookAndFeel",
		"org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel",
		"org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel",
		"org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel",
		"org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel",
		//"org.jvnet.substance.skin.SubstanceRavenLookAndFeel",
		"org.jvnet.substance.skin.SubstanceSaharaLookAndFeel",
		//"org.jvnet.substance.skin.SubstanceTwilightLookAndFeel"
		};

	for (String s : nonSystemLookAndFeels) {
	    _lookandfeelname.add(s);
	    s = s.replace("LookAndFeel", "");

	    if (s.contains("jtattoo")) {
		s = "JTattoo" + s.substring(s.lastIndexOf(".") + 1);
	    } else if (s.contains("jgoodies")) {
		s = "JGoodies" + s.substring(s.lastIndexOf(".") + 1);
	    } else {
		s = s.substring(s.lastIndexOf(".") + 1);
	    }
	    lafname.add(s);
	}


        _lookandfeel = new JComboBox(lafname);

        if (Default.getBoolean(Default.LOOK_AND_FEEL_DISABLED)){
            _lookandfeel.setEnabled(false);
        }
        _lookandfeelLabel = new JLabel(Res.getString("lookandfeel.select"));
        _lookandfeelpreview = new JButton(Res.getString("lookandfeel.change.now"));

        _lookandfeel.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// Disable button for java.LaF's and for Synthetica

		if (_lookandfeel.getSelectedIndex() < UIManager.getInstalledLookAndFeels().length) {
		    _lookandfeelpreview.setEnabled(false);
		    _lookandfeelpreview
			    .setToolTipText(Res.getString("lookandfeel.tooltip.restart.yes"));
		    _lookandfeelpreview.revalidate();
		} else {
		    _lookandfeelpreview.setEnabled(true);
		    _lookandfeelpreview.setToolTipText(Res.getString("lookandfeel.tooltip.restart.no"));
		    _lookandfeelpreview.revalidate();
		}

	    }
	});


	_lookandfeelpreview.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		SwingWorker worker = new SwingWorker() {
		    @Override
		    public Object construct() {

			return 42;
		    }
		    public void finished() {
			  try {
			    UIManager.setLookAndFeel(_lookandfeelname.get(_lookandfeel
			    	    .getSelectedIndex()));
			    setJTattooBar(_lookandfeelname.get(_lookandfeel.getSelectedIndex()));
			} catch (Exception e) {
			//WTF, i dont care
			}

			SwingUtilities.updateComponentTreeUI(_themepanel);
			SwingUtilities.updateComponentTreeUI(_themepanel.getParent());
			SwingUtilities.updateComponentTreeUI(SparkManager.getMainWindow());
			SwingUtilities.updateComponentTreeUI(SparkManager.getChatManager().getChatContainer());
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			_themepanel.invalidate();
			_themepanel.repaint();
			_themepanel.validate();

		    }
		};
		worker.start();
	    }
	});


	    _useTabsForTransports = new JCheckBox("");
	    _useTabsForConference = new JCheckBox("");


        JLabel messageStyleLabel = new JLabel();
        messageStyleBox = new JComboBox();


        emoticonspanel = new EmoticonPanel(10);
        emoticonscrollpane = new JScrollPane(emoticonspanel);
        emoticonscrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        emoticonscrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        emoticonBox = new JComboBox();

        emoticonCheckBox = new JCheckBox();

        JButton addThemeButton = new JButton();
        addEmoticonButton = new JButton();

        emoticonpreviewtranscript = new TranscriptWindow();
        emoticonpreviewtranscript.setForceEmoticons(true);

        showAvatarsBox = new JCheckBox();
        avatarSizeLabel = new JLabel();
        String[] sizeChoices = {"16x16", "24x24", "32x32"};
        avatarSizeField = new JComboBox(sizeChoices);

        contactListFontField = new JTextField();
        contactListFontLabel = new JLabel();

        chatRoomFontField = new JTextField();
        chatRoomFontLabel = new JLabel();



	String[] r = { Res.getString("checkbox.reconnect.panel.big"),
		Res.getString("checkbox.reconnect.panel.small"),
		Res.getString("checkbox.reconnect.panel.icon") };
	_showReconnectBox = new JComboBox(r);

	_showReconnectBox.setSelectedIndex(pref.getReconnectPanelType());

	_showReconnectBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if(_showReconnectBox.getSelectedIndex()!=-1)
		setShowReconnectPanel(_showReconnectBox.getSelectedIndex());
	    }
	});



        showVCards = new JCheckBox();

        // Set ResourceUtils
        ResourceUtils.resLabel(messageStyleLabel, messageStyleBox, Res.getString("label.message.style") + ":");
//        ResourceUtils.resLabel(emoticonsLabel, emoticonBox, Res.getString("label.emoticons") + ":");
        ResourceUtils.resButton(emoticonCheckBox, Res.getString("checkbox.enable.emoticons"));

        ResourceUtils.resButton(addThemeButton, Res.getString("button.add"));
        ResourceUtils.resButton(addEmoticonButton, Res.getString("button.add"));

        ResourceUtils.resLabel(contactListFontLabel, contactListFontField, Res.getString("label.contactlist.fontsize"));
        ResourceUtils.resLabel(chatRoomFontLabel, chatRoomFontField, Res.getString("label.chatroom.fontsize"));
        ResourceUtils.resButton(showAvatarsBox, Res.getString("checkbox.show.avatars.in.contactlist"));
        ResourceUtils.resLabel(avatarSizeLabel, avatarSizeField, Res.getString("label.contactlist.avatarsize"));
        ResourceUtils.resButton(showVCards, Res.getString("title.appearance.showVCards"));
        _useTabsForTransports.setText(Res.getString("checkbox.transport.tab.setting"));
        _useTabsForConference.setText(Res.getString("checkbox.conference.tab.setting"));

        // Build UI
        buildUI();
    }

    /**
     * Builds the UI.
     */
    private void buildUI() {
        // Add Viewer
//        add(new JScrollPane(transcript), new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        add(emoticonscrollpane, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        add(emoticonBox, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(addEmoticonButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(emoticonCheckBox, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(_lookandfeelLabel, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        add(_lookandfeel, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
        add(_lookandfeelpreview, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));


        add(chatRoomFontLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(chatRoomFontField, new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        add(contactListFontLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(contactListFontField, new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        add(showAvatarsBox, new GridBagConstraints(0, 7, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(avatarSizeLabel, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(avatarSizeField, new GridBagConstraints(1, 8, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        add(showVCards, new GridBagConstraints(0, 9, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        add(_useTabsForTransports, new GridBagConstraints(0, 10, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        add(_useTabsForConference, new GridBagConstraints(0, 11, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));


        JLabel reconnectionlabel = new JLabel(Res.getString("checkbox.reconnet.info"));
        add(reconnectionlabel, new GridBagConstraints(0, 12, 1, 1,0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
	add(_showReconnectBox, new GridBagConstraints(1, 12, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));


        // Activate live one.



        _useTabsForTransports.setSelected(pref.getShowTransportTab());
        _useTabsForTransports.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		SettingsManager.getLocalPreferences().setShowTransportTab(_useTabsForTransports.isSelected());

	    }
	});
		_useTabsForConference.setSelected(pref.isShowConferenceTab());
		_useTabsForConference.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsManager.getLocalPreferences().setShowConferenceTab(
						_useTabsForConference.isSelected());

			}
		});

        _useTabsForConference.setSelected(pref.isShowConferenceTab());
        _useTabsForConference.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		SettingsManager.getLocalPreferences().setShowConferenceTab(_useTabsForConference.isSelected());

	    }
	});

        final EmoticonManager emoticonManager = EmoticonManager.getInstance();
        if (emoticonManager.getEmoticonPacks() != null)
        {
	        for (String pack : emoticonManager.getEmoticonPacks()) {
	            emoticonBox.addItem(pack);
	        }
        }

        final String activePack = pref.getEmoticonPack();
        emoticonBox.setSelectedItem(activePack);

        emoticonBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                emoticonManager.addEmoticonPack((String)emoticonBox.getSelectedItem());
                emoticonManager.setActivePack((String)emoticonBox.getSelectedItem());
                showSelectedEmoticon();
            }
        });

        addEmoticonButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmoticonPack();
            }
        });

        showSelectedEmoticon();

        emoticonCheckBox.setSelected(pref.areEmoticonsEnabled());

        _lookandfeel.setSelectedIndex(_lookandfeelname.indexOf(pref.getLookAndFeel()));

        showVCards.setSelected(pref.areVCardsVisible());

        showAvatarsBox.setSelected(pref.areAvatarsVisible());


        if (pref.getContactListIconSize() == 16) {
            avatarSizeField.setSelectedIndex(0);
        }
        else if (pref.getContactListIconSize() == 24) {
            avatarSizeField.setSelectedIndex(1);
        }
        else if (pref.getContactListIconSize() == 32) {
            avatarSizeField.setSelectedIndex(2);
        }
        else {
            avatarSizeField.setSelectedIndex(1);
        }

        try {
            int chatRoomFontSize = pref.getChatRoomFontSize();
            int contactListFontSize = pref.getContactListFontSize();

            chatRoomFontField.setText(Integer.toString(chatRoomFontSize));
            contactListFontField.setText(Integer.toString(contactListFontSize));
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    /**
     * Displays the active emoticon pack.
     */
    protected void showSelectedEmoticon() {
	EmoticonManager emoticonManager = EmoticonManager.getInstance();

	int i = emoticonManager.getActiveEmoticonSet().size();
	if (i==0)
	{
	    emoticonspanel = new EmoticonPanel(1);
	    JLabel label = new JLabel(SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
	    emoticonspanel.add(label);
	}
	else if (i < 25) {
	    emoticonspanel = new EmoticonPanel(i);
	} else {
	    emoticonspanel = new EmoticonPanel(10);
	}
	for(Emoticon emoticon : emoticonManager.getActiveEmoticonSet())
	{
	   ImageIcon ico = new ImageIcon(emoticonManager.getEmoticonURL(emoticon));
	   JLabel label = new JLabel(ico);
	   emoticonspanel.add(label);
	}

	int rows= Math.min(((EmoticonPanel)emoticonspanel).getNumRows()*45, 300);
	emoticonscrollpane.setPreferredSize(new Dimension(300,rows));
	emoticonscrollpane.setViewportView(emoticonspanel);
	this.revalidate();
    }

    /**
     * Returns the name of the theme selected.
     *
     * @return the name of the selected theme.
     */
    public String getSelectedTheme() {
        return (String)messageStyleBox.getSelectedItem();
    }

    /**
     * Returns the name of the selected emoticon pack.
     *
     * @return the name of the emoticon pack.
     */
    public String getSelectedEmoticonPack() {
        return (String)emoticonBox.getSelectedItem();
    }

    public void setEmoticonsEnabled(boolean enabled) {
        emoticonCheckBox.setSelected(enabled);
    }

    public boolean areEmoticonsEnabled() {
        return emoticonCheckBox.isSelected();
    }


    /**
     * Adds a new Emoticon pack to Spark.
     */
    private void addEmoticonPack() {
        if (fc == null) {
            fc = new JFileChooser();
            if (Spark.isWindows()) {
                fc.setFileSystemView(new WindowsFileSystemView());
            }
        }
        fc.setDialogTitle("Add Emoticon Pack");

        fc.addChoosableFileFilter(new ZipFilter());

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File pack = fc.getSelectedFile();
            try {
                EmoticonManager emoticonManager = EmoticonManager.getInstance();
                String name = emoticonManager.installPack(pack);

                if (name == null) {
                    JOptionPane.showMessageDialog(this, "Not a valid emoticon pack.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // If the name does not exists, add it to the message box.
                for (int i = 0; i < emoticonBox.getItemCount(); i++) {
                    String n = (String)emoticonBox.getItemAt(i);
                    if (name.equals(n)) {
                        return;
                    }
                }

                emoticonBox.addItem(name);

                // Set Selected
                emoticonBox.setSelectedItem(name);
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
    }

    /**
     * The ZipFilter class is used by the emoticon file picker to filter out all
     * other files besides *.zip files.
     */
    private class ZipFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            String filename = file.getName();
            if (file.isDirectory()) {
                return true;
            }
            return filename.endsWith(".zip");
        }

        public String getDescription() {
            return "*.zip";
        }
    }

    public String getChatRoomFontSize(){
        return chatRoomFontField.getText();
    }

    public String getContactListFontSize(){
        return contactListFontField.getText();
    }

    public int getContactListIconSize(){
        if (avatarSizeField.getSelectedIndex() == 0) {
            return 16;
        }
        else if (avatarSizeField.getSelectedIndex() == 1) {
            return 24;
        }
        else if (avatarSizeField.getSelectedIndex() == 2) {
            return 32;
        }
        else {
            return 24;
        }
    }

    public boolean areAvatarsVisible(){
        return showAvatarsBox.isSelected();
    }

    public boolean areVCardsVisible(){
       return showVCards.isSelected();
   }

    /**
     * Returns the LookAndFeel with package origin <br>
     * for example:
     * <code>com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel</code>
     *
     * @return {@link String}
     */
    public String getLookAndFeel() {
	return _lookandfeelname.get(_lookandfeel.getSelectedIndex());
    }

    /**
     * Return 0,1,2
     *
     * @return
     */
    public int getReconnectPanelType() {
	return _showReconnectBox.getSelectedIndex();
    }

    /**
     * set 0,1,2
     *
     * @param reconnect
     */
    public void setShowReconnectPanel(int reconnect) {
	_showReconnectBox.setSelectedIndex(reconnect);
    }

    /**
     * Tries to set the Menubar String for JTatto LaFs, doesnt work on Substance
     * @param s, the class of the LookandFeel
     */
    private void setJTattooBar(String classname) {

	if (classname.contains("jtattoo")) {
	    try {
		Properties props = new Properties();

		String menubar = Default.getString(Default.MENUBAR_TEXT) == null ? ""
			: Default.getString(Default.MENUBAR_TEXT);

		props.put("logoString", menubar);

		Class<?> c = ClassLoader.getSystemClassLoader().loadClass(classname);
		Method m = c.getMethod("setCurrentTheme", Properties.class);

		m.invoke(c.newInstance(), props);
	    } catch (Exception e) {
		Log.error("Error Setting JTattoo ", e);
	    }
	}
    }

    protected JLabel getLookandfeelLabel() {
        return _lookandfeelLabel;
    }

    protected JComboBox getLookandfeel() {
        return _lookandfeel;
    }

    protected JButton getLookandfeelpreview() {
        return _lookandfeelpreview;
    }

    protected JCheckBox getUseTabsForConference() {
        return _useTabsForConference;
    }

    protected JCheckBox getShowAvatarsBox() {
        return showAvatarsBox;
    }

    protected JLabel getAvatarSizeLabel() {
        return avatarSizeLabel;
    }

    protected JComboBox getAvatarSizeField() {
        return avatarSizeField;
    }





}

// Maybe Sometime well get a Synthetica License
//"de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel", //commec
//"de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel", //commerc
//"de.javasoft.plaf.synthetica.SyntheticaBlackMoonLookAndFeel", //free
//"de.javasoft.plaf.synthetica.SyntheticaBlackStarLookAndFeel", //free
//"de.javasoft.plaf.synthetica.SyntheticaBlueIceLookAndFeel", //free
//"de.javasoft.plaf.synthetica.SyntheticaBlueMoonLookAndFeel", //free
//"de.javasoft.plaf.synthetica.SyntheticaBlueSteelLookAndFeel", //free
//"de.javasoft.plaf.synthetica.SyntheticaClassyLookAndFeel", //commerc
//"de.javasoft.plaf.synthetica.SyntheticaGreenDreamLookAndFeel", //free
//"de.javasoft.plaf.synthetica.SyntheticaOrangeMetallicLookAndFeel", //commerc
//"de.javasoft.plaf.synthetica.SyntheticaSilverMoonLookAndFeel",	//free
//"de.javasoft.plaf.synthetica.SyntheticaSimple2DLookAndFeel", //commerc
//"de.javasoft.plaf.synthetica.SyntheticaSkyMetallicLookAndFeel", //commerc
//"de.javasoft.plaf.synthetica.SyntheticaWhiteVisionLookAndFeel", //commerc
