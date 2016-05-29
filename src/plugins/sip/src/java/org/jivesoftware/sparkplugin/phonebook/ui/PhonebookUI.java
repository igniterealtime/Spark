package org.jivesoftware.sparkplugin.phonebook.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.java.sipmack.common.Log;
import net.java.sipmack.softphone.SoftPhoneManager;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.sparkplugin.phonebook.BookManager;
import org.jivesoftware.sparkplugin.phonebook.PhoneNumber;
import org.jivesoftware.sparkplugin.phonebook.PhonebookManager;

public class PhonebookUI extends JPanel
{
	private static final long	serialVersionUID	= -5477841619200563149L;
	private JFrame frame = new JFrame();
	private RolloverButton btnAdd;
   private RolloverButton btnDel;
   private RolloverButton btnEdit;
   private RolloverButton btnDial;
   private static final int iconwidth = 20;
   private static final int iconheight = 20;
   private DefaultTableModel model;
   private BookManager manager;
   private JTable table;
   private TableRowSorter<TableModel> sorter;
   private JTextField tfsearch;
   private static final String DEFAULT_FILTER = "^(?is)#.*";
   private static final PhonebookUI instance = new PhonebookUI();
   
   public static PhonebookUI getInstance() {
   	return instance;
   }
   
	public PhonebookUI() {
		manager = new PhonebookManager();
		init();
	}
	
	private void init() {
		this.removeAll();
		model = new DefaultTableModel(){
			private static final long	serialVersionUID	= -1231025049889503785L;
			public boolean isCellEditable(int i, int j)
			{
				return false;
			}
		};
		sorter = new TableRowSorter<TableModel>(model);
		table = new JTable(model);
		JPanel pbtn = new JPanel();
		JPanel psearch = new JPanel();
		tfsearch = new JTextField();
		JScrollPane scroll = new JScrollPane(table);
		
		table.setRowSorter(sorter);
		table.getTableHeader().setReorderingAllowed(false);
		
		btnAdd = new RolloverButton(PhoneRes.getIString("btn.add"));
		btnDel = new RolloverButton(PhoneRes.getIString("btn.del"));
		btnEdit = new RolloverButton(PhoneRes.getIString("btn.edit"));
		btnDial = new RolloverButton(PhoneRes.getIString("btn.dial"));
		
		// add icons
		btnAdd.setIcon(new ImageIcon(PhoneRes.getImage("ADDICON").getScaledInstance(iconwidth, iconheight, Image.SCALE_SMOOTH)));
		btnDel.setIcon(new ImageIcon(PhoneRes.getImage("DELICON").getScaledInstance(iconwidth, iconheight, Image.SCALE_SMOOTH)));
		btnEdit.setIcon(new ImageIcon(PhoneRes.getImage("EDITICON").getScaledInstance(iconwidth, iconheight, Image.SCALE_SMOOTH)));
		btnDial.setIcon(new ImageIcon(PhoneRes.getImage("DIALICON").getScaledInstance(iconwidth, iconheight, Image.SCALE_SMOOTH)));
		
		// add actionlisteners
		btnAdd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				btnAddPerformed();
			}
		});
		
		btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] selected = table.getSelectedRows();
				if(selected.length == 0) {
					JOptionPane.showMessageDialog(PhonebookUI.this, PhoneRes.getIString("book.noEntry"), 
						PhoneRes.getIString("book.warning"), JOptionPane.WARNING_MESSAGE);
				}
				for(int select : selected) {
					if(select > -1) {
						manager.deleteEntry(table.getValueAt(select, 0).toString(), table.getValueAt(select, 1).toString());
						model.removeRow(sorter.convertRowIndexToModel(select));
					}
				}
				loadEntries();
			}
		});
		
		btnEdit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int selected = table.getSelectedRow();
				if(selected > -1) {
					btnEditPerformed(table.getValueAt(selected, 0).toString(), table.getValueAt(selected, 1).toString());
				}
				else {
					JOptionPane.showMessageDialog(PhonebookUI.this, PhoneRes.getIString("book.noEntry"), 
						PhoneRes.getIString("book.warning"), JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		btnDial.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int selected = table.getSelectedRow();
				if(selected > -1) {
					final SoftPhoneManager phoneManager = SoftPhoneManager.getInstance();
					if (phoneManager.getInterlocutors().size() > 0) {
						phoneManager.getDefaultGuiManager().hangupAll();
				   }
				   else {
				      phoneManager.getDefaultGuiManager().dial(table.getValueAt(selected, 1).toString());
				   }
				}
				else {
					JOptionPane.showMessageDialog(PhonebookUI.this, PhoneRes.getIString("book.noEntry"), 
						PhoneRes.getIString("book.warning"), JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		sorter.addRowSorterListener(new RowSorterListener(){
			public void sorterChanged(RowSorterEvent arg0)
			{
					TableRowSorter<?> rs = (TableRowSorter<?>) arg0.getSource();
					String temp = tfsearch.getText();
	
					if (model.getRowCount()> 0 
						&& rs.getViewRowCount() < 1) {
						// nur wenn auch was drin steht, erkennen
						if (temp.length() > 0) {
							tfsearch.setText(temp.substring(0, temp.length() - 1));
						}
						filterTable(tfsearch.getText());
					}
					else if(table.getRowCount() > 0){
						table.setRowSelectionInterval(0, 0);
					}
			}
		});
		
		tfsearch.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent evt)
			{
				keyReleasedSuchFeld(evt);
				if(table.getRowCount() > 0) {
					table.setRowSelectionInterval(0, 0);
				}
			}
		});
		
		loadEntries();
		
		// add components to layout
		pbtn.setLayout(new BoxLayout(pbtn, BoxLayout.X_AXIS));
		pbtn.setBorder(new EmptyBorder(5,5,5,5));
		pbtn.add(Box.createHorizontalGlue());
		pbtn.add(btnDial);
		pbtn.add(Box.createHorizontalStrut(5));
		pbtn.add(btnAdd);
		pbtn.add(Box.createHorizontalStrut(5));
		pbtn.add(btnEdit);
		pbtn.add(Box.createHorizontalStrut(5));
		pbtn.add(btnDel);
		pbtn.add(Box.createHorizontalGlue());
		
		psearch.setLayout(new BoxLayout(psearch, BoxLayout.X_AXIS));
		psearch.setBorder(new EmptyBorder(5,5,5,5));
		psearch.add(new JLabel(PhoneRes.getIString("book.search") + ":"));
		psearch.add(tfsearch);
		
		this.setLayout(new BorderLayout());
		this.add(psearch, BorderLayout.NORTH);
		this.add(pbtn, BorderLayout.SOUTH);
		this.add(scroll, BorderLayout.CENTER);
		
		tfsearch.requestFocusInWindow();
		
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.setTitle(PhoneRes.getIString("frame.title"));
		frame.setSize(new Dimension(400,300));
		frame.setIconImage(SparkRes.getImageIcon(SparkRes.ADDRESS_BOOK_16x16).getImage());
	}
	
	/**
	 * gets the list of phonenumbers from themanager and put them
	 * into the table
	 * 
	 */
	public void loadEntries() {	
		int selRow = table.getSelectedRow();
		
		Vector<String> heading = new Vector<String>();
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> temp; 
		
		heading.add(PhoneRes.getIString("book.name"));
		heading.add(PhoneRes.getIString("book.number"));
		
		List<PhoneNumber> numbers = manager.getPhoneNumbers();
		
		for(PhoneNumber number : numbers) {
			temp = new Vector<String>();
			temp.add(number.getName());
			temp.add(number.getNumber());
			data.add(temp);
		}
		
		model.setDataVector(data, heading);

		// sort alphabetic
		sorter.toggleSortOrder(0);
		// selecte the first row
		if(selRow != -1 
			&& selRow < table.getRowCount())
			table.setRowSelectionInterval(selRow, selRow);
		else if(table.getRowCount()>0)
			table.setRowSelectionInterval(0, 0);
	}
	
	public void invoke() {
		GraphicUtils.centerWindowOnComponent(frame, SparkManager.getMainWindow());
		frame.setVisible(true);
	}
	
	public void setManager(BookManager manage) {
		manager = manage;
		init();
	}
	
	/**
	 * makes a JFrame where you can enter a new entry
	 */
	private void btnAddPerformed() {
		try {
			EventQueue.invokeLater(new Runnable(){
				public void run() {
					EntryFrame addFrame = new EntryFrame(PhonebookUI.this, manager, EntryFrame.TYP_ADD);
					addFrame.invoke();
				}
			});
		}
		catch(Exception ex) {
			Log.error(ex);
		}
	}
	
	/**
	 * makes a JFrame where you can edit an existing entry
	 */
	private void btnEditPerformed(final String name, final String number) {
		try {
			EventQueue.invokeLater(new Runnable(){
				public void run() {
					EntryFrame editFrame = new EntryFrame(PhonebookUI.this, manager, EntryFrame.TYP_EDIT);
					editFrame.setName(name);
					editFrame.setNumber(number);
					editFrame.invoke();
				}
			});
		}
		catch(Exception ex) {
			Log.error(ex);
		}
	}
	
	private void filterTable(String text)
	{
		String filterString = text;
		
		ArrayList<RowFilter<TableModel, Object>> andFilter = new ArrayList<RowFilter<TableModel, Object>>(1); //split.length);
		ArrayList<RowFilter<TableModel, Object>> subFilter;
		RowFilter<TableModel, Object> rf;
		RowFilter<TableModel, Object> rf0;

		try
		{
			subFilter = new ArrayList<RowFilter<TableModel, Object>>(1); //split.length);

			for (Integer i = 0; i < model.getColumnCount(); i++)
			{
				rf0 = RowFilter.regexFilter("^(?i)" + filterString, i);
				subFilter.add(rf0);
			}
			rf = RowFilter.orFilter(subFilter);

			andFilter.add(rf);
		}
		catch (PatternSyntaxException pse)
		{
			Log.error(pse);
		}

		RowFilter<TableModel, Object> rowf = RowFilter.andFilter(andFilter);
		sorter.setRowFilter(rowf);
	}
	
	private void keyReleasedSuchFeld(KeyEvent evt)
	{
		if(table.getRowCount() > 0) {
			filterTable(DEFAULT_FILTER.replace("#", tfsearch.getText()));
			table.updateUI();
		}
	}
}
