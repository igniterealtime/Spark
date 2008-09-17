/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

public class Downloads {
    final JPanel mainPanel = new JPanel();
    private File downloadedDir;
    private JPanel list = new JPanel();
    private Runtime rt = Runtime.getRuntime();
    private static Downloads singleton;
    private static final Object LOCK = new Object();
    private JFileChooser chooser;
    private JDialog dlg;
    
    private LocalPreferences pref;
    
    private Model model;
    private File[] file;
    private JTable table; 
    private JScrollPane scroller = new JScrollPane();
    private File dir = new File(SettingsManager.getLocalPreferences().getDownloadDir());
    private ImageIcon icon = SparkRes.getImageIcon("LEFT_ARROW_IMAGE"); 
    private JButton backButton = new JButton(icon);
    private JTextField path = new JTextField();
    private JButton renewExplorer;
    
    private JPopupMenu popup 		= new JPopupMenu();
    private JMenuItem mi_delete 	= new JMenuItem(Res.getString("menuitem.delete"));
    private JMenuItem mi_rename 	= new JMenuItem(Res.getString("menuitem.rename"));
    private JMenuItem mi_open		= new JMenuItem(Res.getString("menuitem.open.with"));
    /**
     * Returns the singleton instance of <CODE>Downloads</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>Downloads</CODE>
     */
    public static Downloads getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                Downloads controller = new Downloads();
                singleton = controller;
                return controller;
            }
        }
        singleton.model.objects = new Object[singleton.dir.listFiles().length][3];
        singleton.fillDownloadPanel(singleton.dir);
        singleton.updateTable();
        if(Spark.isMac())
      	  singleton.openFile(singleton.downloadedDir);
        else
      	  singleton.dlg.setVisible(true);
        return singleton;
    }


    private Downloads() {
        ChatFrame frame = SparkManager.getChatManager().getChatContainer().getChatFrame();
        dlg = new JDialog(SparkManager.getMainWindow(), Res.getString("title.downloads"), false);
        dlg.setContentPane(mainPanel);
        dlg.pack();
        dlg.setSize(600, 550);
        dlg.setResizable(true);
        
        dlg.setLocationRelativeTo(frame);
        
        pref = SettingsManager.getLocalPreferences();
        downloadedDir = new File(pref.getDownloadDir());
        downloadedDir.mkdirs();
        path.setText(dir.getAbsolutePath());
        path.setEditable(false);
        path.setBackground(null);
        
        pref.setDownloadDir(downloadedDir.getAbsolutePath());
        SettingsManager.saveSettings();

        if(Spark.isLinux())
      	  popup.add(mi_open);
        
        popup.add(mi_rename);
        popup.add(mi_delete);
        
        mi_open.addActionListener(new ActionListener(){
      	  public void actionPerformed(ActionEvent e)
	  			{
	  				mi_open();
	  			}
  		  });
        
        mi_rename.addActionListener(new ActionListener(){
	  			public void actionPerformed(ActionEvent e)
	  			{
	  				String name = JOptionPane.showInputDialog(Res.getString("title.input.newname"), model.objects[table.getSelectedRow()][1]);
	  				if(name != null)
	  				{
		  				try
						{	
		  					if(Spark.isLinux())
		  						rt.exec("mv " + model.objects[table.getSelectedRow()][1] + " " + name, null, dir);
		  					else
		  					{
		  						File file = new File(dir.getAbsolutePath() + "\\" + model.objects[table.getSelectedRow()][1]);
		  						boolean isFileRenamed = file.renameTo(new File(dir.getAbsolutePath() + "\\" + name));
		  						if(!isFileRenamed)
	  							{
	  								JOptionPane.showMessageDialog(dlg, Res.getString("title.error.rename.file"));
	  							}
		  					}
		  					updateTable();
						}
						catch (IOException e1)
						{	
							JOptionPane.showMessageDialog(dlg, Res.getString("title.error.rename.file"));
						}
	  				}
	  			}
  		  });
        
        mi_delete.addActionListener(new ActionListener(){
	  			public void actionPerformed(ActionEvent e)
	  			{
	  				int loeschen = JOptionPane.showOptionDialog(dlg, Res.getString("title.delete.file"), "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
	  				try
					{
	  					if(loeschen == 0)
	  					{
							if(Spark.isLinux())
	  							rt.exec("rm -R " + model.objects[table.getSelectedRow()][1], null, dir);
	  						else
	  						{
	  							File file = new File(dir.getAbsolutePath() + "\\" + model.objects[table.getSelectedRow()][1]);
	  							boolean isFileDeleted = file.delete();
	  							if(!isFileDeleted)
	  							{
	  								JOptionPane.showMessageDialog(dlg, Res.getString("title.error.delete.file"));
	  							}
	  						}
	  					}
	  					updateTable();
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(dlg, Res.getString("title.error.delete.file"));
					}
	  			}
  		  });
        
        list.setLayout(new BorderLayout());
        list.setBackground(Color.white);

        JButton refreshTable = new JButton(SparkRes.getImageIcon("REFRESH_IMAGE"));
        
        JPanel UpperPanel = new JPanel();
        UpperPanel.add(backButton);
        UpperPanel.add(refreshTable);
        
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(UpperPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(path, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(list, new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        
        if(dir.getAbsolutePath().equals(pref.getDownloadDir()))
      	  backButton.setEnabled(false);
        backButton.addActionListener(new ActionListener(){
      	public void actionPerformed(ActionEvent e)
  			{
  				String str = dir.getAbsolutePath().replace("\\", "/");
  				String[] array_str = str.split("/");
  				if(Spark.isLinux())
  					str = "/";
  				else if(!Spark.isLinux())
  					str = array_str[0] + "/";
  				for(int i = 1; i < array_str.length-1; i++)
  				{
  					str = str + array_str[i] + "/";
  				}
  				if(str.equals((pref.getDownloadDir() + "/").replace("\\", "/")))
  					backButton.setEnabled(false);
  				else
  					backButton.setEnabled(true);
  				
  				path.setText(str);
				dir = new File(str);
				updateTable();
  			}
        });
        
        renewExplorer = new JButton(Res.getString("button.unset.file.explorer"), SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        if(pref.getFileExplorer() == null || pref.getFileExplorer().equals(""))
      	  renewExplorer.setEnabled(false);
        
        JLabel locationLabel = new JLabel(Res.getString("label.downloads"));

        JButton userHomeButton = new JButton(Res.getString("title.downloads"), null);

        Action openFolderAction = new AbstractAction() {
          /**
			 * 
			 */
			private static final long	serialVersionUID	= 1L;

				public void actionPerformed(ActionEvent e) {
                if (!downloadedDir.exists()) {
                    downloadedDir.mkdirs();
                }
                showDownloadsDirectory();
                
            }
        };
        userHomeButton.addActionListener(openFolderAction);
        
        // ---------- Tabelle initialisieren ---------------
        initalizeTable();
        model.objects = new Object[dir.listFiles().length][3];
        fillDownloadPanel(dir);
        // -------------------------------------------------------
        
        mainPanel.add(locationLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(userHomeButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(renewExplorer, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

      
        refreshTable.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               updateTable();
           }
        });  
        
        renewExplorer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pref.setFileExplorer("");
                renewExplorer.setEnabled(false);
            }
        });  
        if(Spark.isLinux())
      	  renewExplorer.setVisible(true);
        else
        {
      	  renewExplorer.setVisible(false);
        }
        
        dlg.setVisible(true);
    }

    public JFileChooser getFileChooser() {
        if (chooser == null) {
            downloadedDir = new File(SparkManager.getUserDirectory(), "downloads");
            if (!downloadedDir.exists()) {
                downloadedDir.mkdirs();
            }
            chooser = new JFileChooser(downloadedDir);
            if (Spark.isWindows()) {
                chooser.setFileSystemView(new WindowsFileSystemView());
            }
        }
        return chooser;
    }

    private void openFile(File downloadedFile) {
        try {
            if (Spark.isMac()) {
                Runtime.getRuntime().exec("open " + downloadedFile.getCanonicalPath());
            }
            else if (!Spark.isMac()) {
            	boolean couldOpenFile = SparkManager.getNativeManager().openFile(downloadedFile);
            	if(!couldOpenFile)
            		JOptionPane.showMessageDialog(dlg, Res.getString("title.error.couldnt.open.file"));
            }
        }
        catch (IOException e1) {
            Log.error(e1);
        }
    }


    public File getDownloadDirectory() {
        return downloadedDir;
    }

    public void addDownloadPanel(JScrollPane scroller) {
   	 list.add(scroller);
    }

    public void removeDownloadPanel(JPanel panel) {
        list.remove(panel);
        list.validate();
        list.repaint();
    }

    public void showDownloadsDirectory() {
        downloadedDir = new File(pref.getDownloadDir());
        if (!downloadedDir.exists()) {
            downloadedDir.mkdirs();
        }
        
        if(!Spark.isLinux())
      	  openFile(downloadedDir);
        else if(Spark.isLinux())
        {
           try
					{
          	 	String str;
          	 	if(pref.getFileExplorer() == null || pref.getFileExplorer().equals(""))
          	 	{
               	 	str = JOptionPane.showInputDialog(Res.getString("title.input.fileexplorer"));
               	 	if(str != null)
               	 	{
	               	 	str = str.toLowerCase();
	               	 	pref.setFileExplorer(str);
               	 	}
          	 	}

          	 	if(pref.getFileExplorer() == null || pref.getFileExplorer().equals(""));
          	 	else
          	 	{
               	 	rt.exec(pref.getFileExplorer() + " " + downloadedDir);
               	 	renewExplorer.setEnabled(true);
          	 	}
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(dlg, Res.getString("title.error.find.app") );
						pref.setFileExplorer("");
						renewExplorer.setEnabled(false);
					}
		            /*   	try
								{
									Desktop.getDesktop().open(new File(pref.getDownloadDir()));
								}
								catch (IOException e1)
								{
									JOptionPane.showMessageDialog(dlg, "Der Dateimanager konnte nicht geöffnet werden!");
								}
						*/
        	}
    }
    
    public String filesize(File file)
    {
   	 float filesize = file.length();
   	 filesize = filesize/1000/1000;  	  	 
  	  	 DecimalFormat df = new DecimalFormat( "0.00" );
  	  	 String s = df.format( filesize );
  	  	 return s + " MB";
    }
    
    public void fillDownloadPanel(File dir)
    {
   	 file = dir.listFiles();
   	 int x = 0;
   	 
   	 // ----- insert Folders  into tablemodel -----
       for(int i = 0; i < file.length; i++)
       {
	     	  if(file[i].isDirectory())
	     	  {
	     		  model.setValue(	SparkRes.getImageIcon("FOLDER_CLOSED"), 
	     			  					file[i],
	     			  					"", x);
	     		  x++;
	     	  }
       }
       // -------------------------------------------
       int folder_count = x;
       // ----------- Sorting Folders ---------------
       String[] str = new String[x];;
       for(int i = 0; i < x; i++)
       {
     			str[i] = model.objects[i][1].toString();
     	 }
       java.util.Arrays.sort(str, 0, str.length);
       for(int i = 0; i < x; i++)
       {
     			model.objects[i][1] = str[i];
     	 }
       // ------------------------------------------
       
       int i;
       // ----- insert files into tablemodel -------
       for(i = 0; i < file.length; i++)
       {
     	  if(file[i].isDirectory() == false)
     	  {  
     		  FileSystemView view = FileSystemView.getFileSystemView();      
     		  ImageIcon icon = (ImageIcon)view.getSystemIcon(file[i]); 
     		  
 	    	  model.setValue( icon, 
 									file[i],
 									filesize(file[i]), x);
 	    	  x++;
     	  }
       }
       // ------------------------------------------
       
       // ------------ sorting files ---------------
       str = new String[i];
       for(int y = folder_count; y < x; y++)
       {
     			str[y] = model.objects[y][1].toString();
     	 }
       java.util.Arrays.sort(str, folder_count, str.length);
       for(int y = folder_count; y < x; y++)
       {
     			model.objects[y][1] = str[y];
     			model.objects[y][2] = filesize(new File(dir + "/" + model.objects[y][1].toString()));
     			model.objects[y][0] = FileSystemView.getFileSystemView().getSystemIcon(new File(dir + "/" + model.objects[y][1].toString()));
     	 }
       // ------------------------------------------
    }
    
    public void initalizeTable()
    {
   	   model = new Model();
   	   
         table = new JTable( model );

         table.getColumn("0").setHeaderValue("");
         table.getColumn("1").setHeaderValue(Res.getString("title.file"));
         table.getColumn("2").setHeaderValue(Res.getString("title.filesize"));
         
         table.getColumn("").setMaxWidth(20);
     
         table.addMouseListener(new MouseAdapter(){
   			public void mousePressed(MouseEvent e)
   			{
   				String name = (String)table.getValueAt(table.getSelectedRow(), 1);
				int index_files = 0;
				for(int i = 0; i < file.length; i++)
				{
					if(name.equals(file[i].getName()))
					{
						index_files = i;
						break;
					}
				}
   				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
   				{   					
   					if(file[index_files].isDirectory())
   					{
   						backButton.setEnabled(true);
   						dir = new File(dir + "/" +file[index_files].getName());
   						path.setText(dir.getAbsolutePath());
   						updateTable();
   					}
   					else if(!file[index_files].isDirectory())
   					{
   						if(Spark.isLinux())
   							mi_open();
   						else
   							openFile(file[index_files]);
   					}
   					
   				}
   				if ((e.getButton() == MouseEvent.BUTTON3)
   					&& table.getSelectedRow() != -1 && !file[index_files].isDirectory())
   				{
	   					popup.setLocation(e.getX(), e.getY());
		   				popup.show(table, e.getX(), e.getY());
   				}
	   			else
	   				popup.setVisible(false);
   				}
   			});
         
         table.setDefaultRenderer( Object.class, new Renderer() );
             
        
         table.add(popup);
     		scroller.getViewport().add(table);
         addDownloadPanel(scroller);
    }
    
    public void updateTable()
    {
   	 	try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
   	 	model.clear();
			fillDownloadPanel(dir);		
			model.fireTableDataChanged();
    }
    
    public void mi_open()
    {	 
   	 String str;
   	 str = JOptionPane.showInputDialog(Res.getString("title.input.openwith"));
		 if(str != null)
		 {
	   	 str = str.toLowerCase();
	   	 try
	   	 {
	   		 rt.exec(str + " " + model.objects[table.getSelectedRow()][1], null, dir);
	   	 }
	   	 catch (IOException e1)
	   	 {
	   		 JOptionPane.showMessageDialog(dlg, Res.getString("title.error.find.app"));
	   	 }
		 }
    }
    
   
// ----------------------------------------------------------------------------------------

   class Model extends AbstractTableModel{
      /**
       * 
	 	*/
   	private static final long	serialVersionUID	= 1L;
		
   	private Object[][] objects;
      
         public Model()
         {
            objects = new Object[downloadedDir.listFiles().length][3];
         }
         
         public void clear()
         {
         	objects = new Object[dir.listFiles().length][3];
         }
      
         // Die Anzahl Columns
         public int getColumnCount() {
            return 3;
         }
      
         // Die Anzahl Rows
         public int getRowCount() {
            return objects.length;
         }
      
         // Die Titel der einzelnen Columns
         public String getColumnName(int column) {
            return String.valueOf( column );
         }
               
         // Der Wert der Zelle (rowIndex, columnIndex)
         public Object getValueAt(int rowIndex, int columnIndex) {
            return objects[ rowIndex ][ columnIndex ];
         }

         // Eine Angabe, welchen Typ von Objekten in den Columns angezeigt werden soll
         public Class<Object> getColumnClass(int columnIndex) {
            return Object.class;
         }
         
         public void setValue(ImageIcon icon, File file, String filesize, int rowIndex)
         {
         	objects[rowIndex][0] = icon;
         	objects[rowIndex][1] = file.getName();
         	objects[rowIndex][2] = filesize;
         }
   }
 
// -------------------------------------------------------------------------------------------   
   
   class Renderer extends JLabel implements TableCellRenderer{
      /**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;
		
		private Color colorSelected = new Color( 200, 200, 255 );
      private Color colorNormal = Color.white;

      public Renderer(){
         setOpaque( true );
      }
      
      public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
         
         // die normalen Farben
         setForeground( Color.BLACK );
         if( hasFocus )
            setBackground( colorSelected);
         else if( isSelected )
            setBackground( colorSelected );
         else
            setBackground( colorNormal );
         
         setText( null );
         setIcon( null );
         
         if( value instanceof Icon )
            setIcon( (Icon)value );
         else if( value instanceof Boolean ){
            if( ((Boolean)value).booleanValue() )
               setText( "yes" );
            else
               setText( "no" );
         }
         else
         	try
         {
            setText( value.toString() );
         }
         catch (Exception e1)
			{
				System.out.println(e1);
			} 
        
         return this;
      }
   }

   
}

 
