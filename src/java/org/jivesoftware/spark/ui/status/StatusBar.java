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
package org.jivesoftware.spark.ui.status;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.CommandPanel;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ImageCombiner;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;
import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyList;
import org.jivesoftware.sparkimpl.profile.VCardEditor;
import org.jivesoftware.sparkimpl.profile.VCardListener;
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jivesoftware.spark.util.UIComponentRegistry;

//TODO: I need to remove the presence logic from this class.
public class StatusBar extends JPanel implements VCardListener {
	private static final long serialVersionUID = -4322806442034868526L;

	private List<StatusItem> statusList = new ArrayList<StatusItem>();

    private JLabel imageLabel = new JLabel();
    private JLabel descriptiveLabel = new JLabel();
    private JLabel nicknameLabel = new JLabel();
    private StatusPanel statusPanel = new StatusPanel();

    private CommandPanel commandPanel ;
    private Image backgroundImage;
    private Runnable changePresenceRunnable;

    private Presence currentPresence;

	public StatusBar() {
		this(true);
	}

    public StatusBar(boolean doLayout) {
		commandPanel = UIComponentRegistry.createCommandPanel();
	if (doLayout) {
		setLayout(new GridBagLayout());

		backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage();



		ImageIcon brandedImage = Default.getImageIcon(Default.BRANDED_IMAGE);
		if (brandedImage != null && brandedImage.getIconWidth() > 1) {
			final JLabel brandedLabel = new JLabel(brandedImage);
			add(brandedLabel, new GridBagConstraints(3, 0, 1, 3, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		}


        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 8, 2, 2), 0, 0));

        add(nicknameLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 12, 0, 0), 0, 0));
        add(statusPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 7, 0, 0), 0, 0));
        add(commandPanel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        nicknameLabel.setToolTipText(SparkManager.getConnection().getUser());
        nicknameLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        setStatus(Res.getString("status.online"));
	}

        buildStatusItemList();


        currentPresence = new Presence(Presence.Type.available, Res.getString("status.online"), 1, Presence.Mode.available);


        //setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230), 1));

        SparkManager.getSessionManager().addPresenceListener(new PresenceListener() {
            public void presenceChanged(Presence presence) {
        	presence.setStatus(StringUtils.modifyWildcards(presence.getStatus()));
                changeAvailability(presence);
            }
        });

        // Show profile on double click of image label
        imageLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {
                    VCardManager vcardManager = SparkManager.getVCardManager();
                    VCardEditor editor = new VCardEditor();
                    editor.editProfile(vcardManager.getVCard(), SparkManager.getWorkspace());
                }
            }

            public void mouseEntered(MouseEvent e) {
                imageLabel.setCursor(GraphicUtils.HAND_CURSOR);
            }

            public void mouseExited(MouseEvent e) {
                imageLabel.setCursor(GraphicUtils.DEFAULT_CURSOR);
            }
        });

        final TimerTask task = new SwingTimerTask() {
            public void doRun() {
                SparkManager.getVCardManager().addVCardListener(SparkManager.getWorkspace().getStatusBar());
            }
        };

        TaskEngine.getInstance().schedule(task, 3000);
		changePresenceRunnable = new Runnable() {
			@Override
			public void run() {
				updatePresence();
			}
		};


    }

    public void setAvatar(Icon icon) {
        Image image = ImageCombiner.iconToImage(icon);
        if (icon.getIconHeight() > 64 || icon.getIconWidth() > 64) {
            imageLabel.setIcon(new ImageIcon(image.getScaledInstance(-1, 64, Image.SCALE_SMOOTH)));
        } else {
            imageLabel.setIcon(icon);
        }
        imageLabel.setBorder(null);
        revalidate();
    }

    public CommandPanel getCommandPanel()
    {
	return commandPanel;
    }

    public void setNickname(String nickname) {
        nicknameLabel.setText(nickname);
    }

    /**
     * Sets the current status text in the Status Manager.
     *
     * @param status the status to set.
     */
    public void setStatus(String status) {
        statusPanel.setStatus(status);
    }

	protected void updatePresence() {
		setStatus(currentPresence.getStatus());
		final Icon icon = PresenceManager.getIconFromPresence(currentPresence);
		if (icon != null) {
			statusPanel.setIcon(icon);
		}
	}

    public void showPopup(MouseEvent e) {
        final JPopupMenu popup = new JPopupMenu();

        List<CustomStatusItem> custom = CustomMessages.load();
        if (custom == null) {
            custom = new ArrayList<CustomStatusItem>();
        }

        // Sort Custom Messages
        Collections.sort( custom, new Comparator<CustomStatusItem>()
        {
        	public int compare( final CustomStatusItem a, final CustomStatusItem b )
        	{
        		return( a.getStatus().compareToIgnoreCase( b.getStatus() ) );
        	}
        } );

        // Build menu from StatusList
        for (final StatusItem statusItem : statusList) {
            final Action statusAction = new AbstractAction() {
				private static final long serialVersionUID = -192865863435381702L;

				public void actionPerformed(ActionEvent actionEvent) {
                    final String text = statusItem.getText();
                    final StatusItem si = getStatusItem(text);
                    if (si == null) {
                        // Custom status
                        Log.error("Unable to find status item for status - " + text);
                        return;
                    }

                    SwingWorker worker = new SwingWorker() {
                        public Object construct() {
                            SparkManager.getSessionManager().changePresence(si.getPresence());
                            return "ok";
                        }

                        public void finished() {
                            setStatus(text);
                        }
                    };
                    worker.start();
                }
            };

            statusAction.putValue(Action.NAME, statusItem.getText());
            statusAction.putValue(Action.SMALL_ICON, statusItem.getIcon());

            // Has Children
            boolean hasChildren = false;
            for (Object aCustom : custom) {
                final CustomStatusItem cItem = (CustomStatusItem) aCustom;
                String type = cItem.getType();
                if (type.equals(statusItem.getText())) {
                    hasChildren = true;
                }
            }

            if (!hasChildren) {
                // Add as Menu Item
                popup.add(statusAction);
            }
            else {

                final JMenu mainStatusItem = new JMenu(statusAction);


                popup.add(mainStatusItem);

                // Add Custom Messages
                for (Object aCustom : custom) {
                    final CustomStatusItem customItem = (CustomStatusItem) aCustom;
                    String type = customItem.getType();
                    if (type.equals(statusItem.getText())) {
                        // Add Child Menu
                        Action action = new AbstractAction() {
							private static final long serialVersionUID = -1264239704492879742L;

							public void actionPerformed(ActionEvent actionEvent) {
                                final String text = mainStatusItem.getText();
                                final StatusItem si = getStatusItem(text);
                                if (si == null) {
                                    // Custom status
                                    Log.error("Unable to find status item for status - " + text);
                                    return;
                                }

                                SwingWorker worker = new SwingWorker() {
                                    public Object construct() {
                                        Presence oldPresence = si.getPresence();
                                        Presence presence = copyPresence(oldPresence);
                                        presence.setStatus(customItem.getStatus());
                                        presence.setPriority(customItem.getPriority());
                                        SparkManager.getSessionManager().changePresence(presence);
                                        return "ok";
                                    }

                                    public void finished() {
                                        String status = customItem.getType() + " - " + customItem.getStatus();
                                        setStatus(status);
                                    }
                                };
                                worker.start();
                            }
                        };
                        action.putValue(Action.NAME, customItem.getStatus());
                        action.putValue(Action.SMALL_ICON, statusItem.getIcon());
                        mainStatusItem.add(action);
                    }
                }

                // If menu has children, allow it to still be clickable.
                mainStatusItem.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent mouseEvent) {
                        statusAction.actionPerformed(null);
                        popup.setVisible(false);
                    }
                });
            }
        }


        //Add privacy menu
        if (PrivacyManager.getInstance().isPrivacyActive()) {
            JMenu privMenu = new JMenu(Res.getString("privacy.status.menu.entry"));
            privMenu.setIcon(SparkRes.getImageIcon("PRIVACY_ICON_SMALL"));
            final PrivacyManager pmanager = PrivacyManager.getInstance();
            for (SparkPrivacyList plist : pmanager.getPrivacyLists()) {
                JMenuItem it = new JMenuItem(plist.getListName());
                privMenu.add(it);
                if (plist.isActive()) {
                    it.setIcon(SparkRes.getImageIcon("PRIVACY_LIGHTNING"));
                } else {
                    it.setIcon(null);
                }
                final SparkPrivacyList finalList = plist;
                it.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        PrivacyManager.getInstance().setListAsActive(finalList.getListName());
                    }
                });
            }

            if (pmanager.hasActiveList()) {
                JMenuItem remMenu = new JMenuItem(Res.getString("privacy.menuitem.deactivate.current.list", pmanager.getActiveList().getListName()),
                        SparkRes.getImageIcon("PRIVACY_DEACTIVATE_LIST"));
                remMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pmanager.declineActiveList();
                    }
                });
                privMenu.addSeparator();
                privMenu.add(remMenu);
            }

            popup.add(privMenu);
        }

        // Add change message
        final JMenuItem changeStatusMenu = new JMenuItem(Res.getString("menuitem.set.status.message"), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE));
        popup.addSeparator();


        popup.add(changeStatusMenu);
        changeStatusMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CustomMessages.addCustomMessage();
            }
        });


        Action editMessagesAction = new AbstractAction() {
			private static final long serialVersionUID = 7148051050075679995L;

			public void actionPerformed(ActionEvent actionEvent) {
                CustomMessages.editCustomMessages();
            }
        };

        editMessagesAction.putValue(Action.NAME, Res.getString("menuitem.edit.status.message"));
        popup.add(editMessagesAction);

        final JPanel panel = getStatusPanel();
        popup.show(panel, 0, panel.getHeight());
    }

	protected JPanel getStatusPanel() {
		return statusPanel;
	}

    public void changeAvailability(final Presence presence) {
        if (!presence.isAvailable()) {
            return;
        }

        if ((presence.getMode() == currentPresence.getMode()) && (presence.getType() == currentPresence.getType()) && (presence.getStatus().equals(currentPresence.getStatus()))) {
            PacketExtension pe = presence.getExtension("x", "vcard-temp:x:update");
            if (pe != null) {
                // Update VCard
                loadVCard();
            }
            return;
        }

        currentPresence = presence;

        SwingUtilities.invokeLater(changePresenceRunnable);
    }

    /**
     * Populates the current Dnd List.
     */
    private void buildStatusItemList() {
        for (Presence presence : PresenceManager.getPresences()) {
            Icon icon = PresenceManager.getIconFromPresence(presence);
            StatusItem item = new StatusItem(presence, icon);
            statusList.add(item);
        }

        final Icon availableIcon = PresenceManager.getIconFromPresence(new Presence(Presence.Type.available));

        // Set default presence icon (Avaialble)
        statusPanel.setIcon(availableIcon);
    }


    public Collection<StatusItem> getStatusList() {
        return statusList;
    }

    public Collection<CustomStatusItem> getCustomStatusList()
    {
    	List<CustomStatusItem> custom = CustomMessages.load();
    	if (custom == null)
    		custom = new ArrayList<CustomStatusItem>();

    	// Sort Custom Messages
        Collections.sort( custom, new Comparator<CustomStatusItem>()
        {
        	public int compare( final CustomStatusItem a, final CustomStatusItem b )
        	{
        		return( a.getStatus().compareToIgnoreCase( b.getStatus() ) );
        	}
        } );

    	return custom;
    }

    public Presence getPresence() {
        return currentPresence;
    }

    public StatusItem getStatusItem(String label) {
        for (StatusItem aStatusList : statusList) {
            if (aStatusList.getText().equals(label)) {
                return aStatusList;
            }
        }
        return null;
    }

    public void paintComponent(Graphics g) {
    	  super.paintComponent(g);
    	  if (backgroundImage != null) {
    		  double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
    		  double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
    		  AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
    		  ((Graphics2D)g).drawImage(backgroundImage, xform, this);
    	  }
    }


    public void loadVCard() {
        final Runnable loadVCard = new Runnable() {
            public void run() {
                VCard vcard = SparkManager.getVCardManager().getVCard();
                updateVCardInformation(vcard);
            }
        };

        TaskEngine.getInstance().submit(loadVCard);
    }

    protected void updateVCardInformation(final VCard vCard) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (vCard.getError() == null) {
                    String firstName = vCard.getFirstName();
                    String lastName = vCard.getLastName();
                    String nickname = vCard.getNickName();
                    if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
                        setNickname(firstName + " " + lastName);
                    }
                    else if (ModelUtil.hasLength(firstName)) {
                        setNickname(firstName);
                    }
                    else if (ModelUtil.hasLength(nickname)) {
                        setNickname(nickname);
                    }
                    else {
                        nickname = SparkManager.getSessionManager().getUsername();
                        setNickname(nickname);
                    }
                }
                else {
                    String nickname = SparkManager.getSessionManager().getUsername();
                    setNickname(nickname);
                    return;
                }


                byte[] avatarBytes = null;
                try {
                    avatarBytes = vCard.getAvatar();
                }
                catch (Exception e) {
                    Log.error("Cannot retrieve avatar bytes.", e);
                }


                if (avatarBytes != null && avatarBytes.length > 0) {
                    try {
                        ImageIcon avatarIcon = new ImageIcon(avatarBytes);
                        avatarIcon = VCardManager.scale(avatarIcon);
                        setAvatar(avatarIcon);
                        imageLabel.invalidate();
                        imageLabel.validate();
                        imageLabel.repaint();
                    }
                    catch (Exception e) {
                        // no issue
                    }
                }
                else {
                    imageLabel.setIcon(null);
                    imageLabel.setBorder(null);
                    imageLabel.invalidate();
                    imageLabel.validate();
                    imageLabel.repaint();
                }
            }
        });

    }

    public static Presence copyPresence(Presence presence) {
        return new Presence(presence.getType(), presence.getStatus(), presence.getPriority(), presence.getMode());
    }

    /**
     * Return the nickname Component used to display the users profile name.
     *
     * @return the label.
     */
    public JLabel getNicknameLabel() {
        return nicknameLabel;
    }

    private class StatusPanel extends JPanel {
		private static final long serialVersionUID = -5086334443225239032L;
		private JLabel iconLabel;
        private JLabel statusLabel;

        public StatusPanel() {
            super();

            setOpaque(false);

            iconLabel = new JLabel();
            statusLabel = new JLabel();

            setLayout(new GridBagLayout());

            // Remove padding from icon label
            iconLabel.setIconTextGap(0);

            add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            add(statusLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));

            statusLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
            statusLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOWN_ARROW_IMAGE));
            statusLabel.setHorizontalTextPosition(JLabel.LEFT);

            setOpaque(false);

            final Border border = BorderFactory.createEmptyBorder(2, 2, 2, 2);
            setBorder(border);


            statusLabel.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }

                public void mouseEntered(MouseEvent e) {
                    setCursor(GraphicUtils.HAND_CURSOR);

                    setBorder(BorderFactory.createBevelBorder(0));
                }

                public void mouseExited(MouseEvent e) {
                    setCursor(GraphicUtils.DEFAULT_CURSOR);
                    setBorder(border);
                }

                public void mousePressed(MouseEvent e) {
                    setBorder(BorderFactory.createBevelBorder(1));
                }
            });

        }

        public void setStatus(String status) {
            int length = status.length();
            String visualStatus = status;
            if (length > 30) {
                visualStatus = status.substring(0, 27) + "...";
            }

            statusLabel.setText(visualStatus);
            statusLabel.setToolTipText(status);
        }

        public void setIcon(Icon icon) {
            iconLabel.setIcon(icon);
        }
    }

    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
    }

    public void setDescriptiveText(String text) {
        descriptiveLabel.setText(text);
    }

    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        dim.width = 0;
        return dim;
    }


    public void vcardChanged(VCard vcard) {
        updateVCardInformation(vcard);
    }

    protected Runnable getChangePresenceRunnable() {
        return changePresenceRunnable;
    }

    protected Presence getCurrentPresence() {
        return currentPresence;
    }


}
