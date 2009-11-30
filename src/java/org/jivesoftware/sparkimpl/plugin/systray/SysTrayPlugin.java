package org.jivesoftware.sparkimpl.plugin.systray;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.MessageEventNotificationListener;
import org.jivesoftware.spark.NativeHandler;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.ui.status.CustomStatusItem;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.ui.status.StatusItem;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

public class SysTrayPlugin implements Plugin, NativeHandler, MessageEventNotificationListener {
	private JPopupMenu popupMenu = new JPopupMenu();

	private final JMenuItem openMenu = new JMenuItem(Res.getString("menuitem.open"));
	private final JMenuItem minimizeMenu = new JMenuItem(Res.getString("menuitem.hide"));
	private final JMenuItem exitMenu = new JMenuItem(Res.getString("menuitem.exit"));
	private final JMenu statusMenu = new JMenu(Res.getString("menuitem.status"));
	private final JMenuItem logoutMenu = new JMenuItem(Res.getString("menuitem.logout.no.status"));

	private LocalPreferences pref = SettingsManager.getLocalPreferences();
	private ImageIcon availableIcon;
	private ImageIcon dndIcon;
	private ImageIcon awayIcon;
    private ImageIcon offlineIcon;
    private ImageIcon connectingIcon;
	private ImageIcon newMessageIcon;
	private ImageIcon typingIcon;
	private TrayIcon trayIcon;

	public boolean canShutDown() {
		return true;
	}

	public void initialize() {
		SystemTray tray = SystemTray.getSystemTray();

		SparkManager.getNativeManager().addNativeHandler(this);
		SparkManager.getMessageEventManager().addMessageEventNotificationListener(this);
		
        if ( Spark.isLinux() ) {
            newMessageIcon = SparkRes.getImageIcon(SparkRes.MESSAGE_NEW_TRAY_LINUX);
            typingIcon = SparkRes.getImageIcon(SparkRes.TYPING_TRAY_LINUX);
        } else {
		newMessageIcon = SparkRes.getImageIcon(SparkRes.MESSAGE_NEW_TRAY);
		typingIcon = SparkRes.getImageIcon(SparkRes.TYPING_TRAY);
        }
		
		if (SystemTray.isSupported()) {		
			availableIcon = Default.getImageIcon(Default.TRAY_IMAGE);
            if ( Spark.isLinux() ) {
			if (availableIcon == null) {
                    availableIcon = SparkRes.getImageIcon(SparkRes.TRAY_IMAGE_LINUX);
                }
                awayIcon = SparkRes.getImageIcon(SparkRes.TRAY_AWAY_LINUX);
                dndIcon = SparkRes.getImageIcon(SparkRes.TRAY_DND_LINUX);
                offlineIcon = SparkRes.getImageIcon(SparkRes.TRAY_OFFLINE_LINUX);
                connectingIcon = SparkRes.getImageIcon(SparkRes.TRAY_CONNECTING_LINUX);
            } else {
                if (availableIcon == null) {
				availableIcon = SparkRes.getImageIcon(SparkRes.TRAY_IMAGE);
			}
			awayIcon = SparkRes.getImageIcon(SparkRes.TRAY_AWAY);
			dndIcon = SparkRes.getImageIcon(SparkRes.TRAY_DND);
                offlineIcon = SparkRes.getImageIcon(SparkRes.TRAY_OFFLINE);
                connectingIcon = SparkRes.getImageIcon(SparkRes.TRAY_CONNECTING);
            }
			
			popupMenu.add(openMenu);
			openMenu.addActionListener(new AbstractAction() {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent event) {
					SparkManager.getMainWindow().setVisible(true);
					SparkManager.getMainWindow().toFront();
				}

			});
			popupMenu.add(minimizeMenu);
			minimizeMenu.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent event) {
					SparkManager.getMainWindow().setVisible(false);
				}
			});
			popupMenu.addSeparator();
			addStatusMessages();
			popupMenu.add(statusMenu);
			statusMenu.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent event) {

				}
			});

			if (Spark.isWindows()) {
				popupMenu.add(logoutMenu);
				logoutMenu.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						SparkManager.getMainWindow().logout(false);
					}
				});
			}
			// Exit Menu
			exitMenu.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					SparkManager.getMainWindow().shutdown();
				}
			});
			popupMenu.add(exitMenu);

            /**
             * If connection closed set offline tray image
             */
            SparkManager.getConnection().addConnectionListener(new ConnectionListener() {

                public void connectionClosed() {
                    trayIcon.setImage(offlineIcon.getImage());
                }

                public void connectionClosedOnError(Exception arg0) {
                    trayIcon.setImage(offlineIcon.getImage());
                }

                public void reconnectingIn(int arg0) {
                    trayIcon.setImage(connectingIcon.getImage());
                }

                public void reconnectionSuccessful() {
                    trayIcon.setImage(availableIcon.getImage());
                }

                public void reconnectionFailed(Exception arg0) {
                    trayIcon.setImage(offlineIcon.getImage());
                }
            });

			SparkManager.getSessionManager().addPresenceListener(new
			PresenceListener() {
				public void presenceChanged(Presence presence) { 
					if (presence.getMode() == Presence.Mode.available) {
						trayIcon.setImage(availableIcon.getImage());
					} else if (presence.getMode() == Presence.Mode.away || presence.getMode() == Presence.Mode.xa) {
						trayIcon.setImage(awayIcon.getImage());
					} else if (presence.getMode() == Presence.Mode.dnd) {
						trayIcon.setImage(dndIcon.getImage());
					} else {
						trayIcon.setImage(availableIcon.getImage());
					}
				} 
			});
			 
		
			try {
				trayIcon = new TrayIcon(availableIcon.getImage(), Default.getString(Default.APPLICATION_NAME), null);
				trayIcon.setImageAutoSize(true);

				trayIcon.addMouseListener(new MouseListener() {

					public void mouseClicked(MouseEvent event) {
						if (event.getButton() == MouseEvent.BUTTON1
								&& event.getClickCount() == 1) {

							if (SparkManager.getMainWindow().isVisible()) {
								SparkManager.getMainWindow().setVisible(false);
							} else {
								SparkManager.getMainWindow().setVisible(true);
								SparkManager.getMainWindow().toFront();
							}
						} else if (event.getButton() == MouseEvent.BUTTON1) {
							SparkManager.getMainWindow().toFront();
							// SparkManager.getMainWindow().requestFocus();
						} else if (event.getButton() == MouseEvent.BUTTON3) {
							popupMenu.setLocation(event.getX(), event.getY());
							popupMenu.setInvoker(popupMenu);
							popupMenu.setVisible(true);
						}
					}

					public void mouseEntered(MouseEvent event) {

					}

					public void mouseExited(MouseEvent event) {

					}

					public void mousePressed(MouseEvent event) {

					}

					public void mouseReleased(MouseEvent event) {

					}

				});

				tray.add(trayIcon);
			} catch (Exception e) {
				// Not Supported
			}
		}
	}

	public void addStatusMessages() {
		StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
		for (Object o : statusBar.getStatusList()) {
			final StatusItem statusItem = (StatusItem) o;

			final AbstractAction action = new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {

					StatusBar statusBar = SparkManager.getWorkspace()
							.getStatusBar();

					SparkManager.getSessionManager().changePresence(
							statusItem.getPresence());
					statusBar.setStatus(statusItem.getText());
				}
			};
			action.putValue(Action.NAME, statusItem.getText());
			action.putValue(Action.SMALL_ICON, statusItem.getIcon());

			boolean hasChildren = false;
			for (Object aCustom : SparkManager.getWorkspace().getStatusBar()
					.getCustomStatusList()) {
				final CustomStatusItem cItem = (CustomStatusItem) aCustom;
				String type = cItem.getType();
				if (type.equals(statusItem.getText())) {
					hasChildren = true;
				}
			}

			if (!hasChildren) {
				JMenuItem status = new JMenuItem(action);
				statusMenu.add(status);
			} else {
				final JMenu status = new JMenu(action);
				statusMenu.add(status);

				status.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent mouseEvent) {
						action.actionPerformed(null);
						popupMenu.setVisible(false);
					}
				});

				for (Object aCustom : SparkManager.getWorkspace()
						.getStatusBar().getCustomStatusList()) {
					final CustomStatusItem customItem = (CustomStatusItem) aCustom;
					String type = customItem.getType();
					if (type.equals(statusItem.getText())) {
						AbstractAction customAction = new AbstractAction() {
							private static final long serialVersionUID = 1L;

							public void actionPerformed(ActionEvent e) {
								StatusBar statusBar = SparkManager
										.getWorkspace().getStatusBar();

								Presence oldPresence = statusItem.getPresence();
								Presence presence = StatusBar
										.copyPresence(oldPresence);
								presence.setStatus(customItem.getStatus());
								presence.setPriority(customItem.getPriority());
								SparkManager.getSessionManager()
										.changePresence(presence);

								statusBar.setStatus(statusItem.getName()
										+ " - " + customItem.getStatus());
							}
						};
						customAction.putValue(Action.NAME, customItem.getStatus());
						customAction.putValue(Action.SMALL_ICON, statusItem.getIcon());
						JMenuItem menuItem = new JMenuItem(customAction);
						status.add(menuItem);
					}
				}

			}
		}
	}

	public void shutdown() {
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			tray.remove(trayIcon);
		}
	}

	public void uninstall() {

	}

	// Info on new Messages
	@Override
	public void flashWindow(Window window) {		
		if (pref.isSystemTrayNotificationEnabled())
		{
			trayIcon.setImage(newMessageIcon.getImage());
		}
	}

	@Override
	public void flashWindowStopWhenFocused(Window window) {
		trayIcon.setImage(availableIcon.getImage());
	}

	@Override
	public boolean handleNotification() {
		return true;
	}

	@Override
	public void stopFlashing(Window window) {
		trayIcon.setImage(availableIcon.getImage());
	}

	
	// For Typing
	@Override
	public void cancelledNotification(String from, String packetID) {
		trayIcon.setImage(availableIcon.getImage());
	}

	@Override
	public void composingNotification(String from, String packetID) {
		if (pref.isTypingNotificationShown()) {
			trayIcon.setImage(typingIcon.getImage());
		}
	}

	@Override
	public void deliveredNotification(String from, String packetID) {
		// Nothing
	}

	@Override
	public void displayedNotification(String from, String packetID) {
		// Nothing
	}

	@Override
	public void offlineNotification(String from, String packetID) {
		// Nothing
	}

}
