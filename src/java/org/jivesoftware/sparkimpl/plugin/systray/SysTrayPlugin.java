package org.jivesoftware.sparkimpl.plugin.systray;

import java.awt.SystemTray;
import java.awt.TrayIcon;
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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.status.CustomStatusItem;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.ui.status.StatusItem;

public class SysTrayPlugin implements Plugin
{
	private JPopupMenu popupMenu = new JPopupMenu();
	
	private final JMenuItem openMenu 		= new JMenuItem(Res.getString("menuitem.open"));
	private final JMenuItem minimizeMenu 	= new JMenuItem(Res.getString("menuitem.hide"));
	private final JMenuItem exitMenu 		= new JMenuItem(Res.getString("menuitem.exit"));
	private final JMenu 	statusMenu		= new JMenu(Res.getString("menuitem.status"));
	private final JMenuItem logoutMenu 		= new JMenuItem(Res.getString("menuitem.logout.no.status"));
	
	private ImageIcon availableIcon;
	private TrayIcon trayIcon;

	public boolean canShutDown() 
	{
		return true;
	}

	public void initialize() 
	{		
		SystemTray tray = SystemTray.getSystemTray();
		
		if (SystemTray.isSupported()) {
						
			
			availableIcon = Default.getImageIcon(Default.TRAY_IMAGE);
	        if (availableIcon == null) {
	            availableIcon = SparkRes.getImageIcon(SparkRes.TRAY_IMAGE);
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
	        
	        if (Spark.isWindows())
	        {
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
	        
	        
	        // init Presence Listener
	        // Small feature preview
	        /*
			SparkManager.getSessionManager().addPresenceListener(new PresenceListener() {
				public void presenceChanged(Presence presence) {
					Icon icon = PresenceManager.getIconFromPresence(presence);
					if (icon instanceof ImageIcon)
					{
						// Add Change Icon
						trayIcon.setImage( ((ImageIcon) icon).getImage()  );
						
						// Add 
						trayIcon.displayMessage("Status", presence.getMode().toString(), TrayIcon.MessageType.INFO);
					}
					
				}	
			});
			*/
	        //
	                
		    try {        
		    	trayIcon = new TrayIcon(availableIcon.getImage(),"Spark",null);		
			    trayIcon.setImageAutoSize(true);
			    
				trayIcon.addMouseListener(new MouseListener() {
		
						public void mouseClicked(MouseEvent event) {
							if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 1)
							{

								if (SparkManager.getMainWindow().isVisible())
								{
									SparkManager.getMainWindow().setVisible(false);
								}
								else
								{
									SparkManager.getMainWindow().setVisible(true);
									SparkManager.getMainWindow().toFront();
								}
							}
							else if (event.getButton() == MouseEvent.BUTTON1)
							{
								SparkManager.getMainWindow().toFront();
								//SparkManager.getMainWindow().requestFocus();
							}
							else if (event.getButton() == MouseEvent.BUTTON3) {
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
				    	
				    }
			    );

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

					  StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
					
					  SparkManager.getSessionManager().changePresence(statusItem.getPresence());
					  statusBar.setStatus(statusItem.getText());
				  }
			  };
			  action.putValue(Action.NAME, statusItem.getText());
			  action.putValue(Action.SMALL_ICON, statusItem.getIcon());

			  boolean hasChildren = false;
			  for (Object aCustom : SparkManager.getWorkspace().getStatusBar().getCustomStatusList()) {
	                final CustomStatusItem cItem = (CustomStatusItem) aCustom;
	                String type = cItem.getType();
	                if (type.equals(statusItem.getText())) {
	                    hasChildren = true;
	                }
			  }
			  
			  if (!hasChildren) {
				  JMenuItem status = new JMenuItem(action);
				  statusMenu.add(status);
			  }
			  else {
				  final JMenu status = new JMenu(action);
				  statusMenu.add(status);
				  
				  status.addMouseListener(new MouseAdapter() {
	                    public void mouseClicked(MouseEvent mouseEvent) {	
	                    	action.actionPerformed(null);
	                    	popupMenu.setVisible(false);
	                    }
				  });
				  
				  for (Object aCustom : SparkManager.getWorkspace().getStatusBar().getCustomStatusList()) {
					  final CustomStatusItem customItem = (CustomStatusItem) aCustom;
					  String type = customItem.getType();
					  if (type.equals(statusItem.getText())) {
						  AbstractAction customAction = new AbstractAction() {
							  private static final long serialVersionUID = 1L;
	
							  public void actionPerformed(ActionEvent e) {
								  StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
								  
								  Presence oldPresence = statusItem.getPresence(); 
								  Presence presence = StatusBar.copyPresence(oldPresence);
                                  presence.setStatus(customItem.getStatus());
                                  presence.setPriority(customItem.getPriority());
                                  SparkManager.getSessionManager().changePresence(presence);

								  statusBar.setStatus(statusItem.getName()  + " - " +  customItem.getStatus());
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

	public void uninstall()	{

		
	}

}
