package org.jivesoftware.sparkplugin.phonebook.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.sparkplugin.phonebook.BookManager;

public class EntryFrame extends JFrame
{
	private static final long	serialVersionUID	= -8956851041216444903L;
	private JTextField tfName, tfNumber;
	private PhonebookUI parent;
	private BookManager manager;
	public static final int TYP_ADD  = 0;
	public static final int TYP_EDIT = 1;
	private int typ = 0;;
	private String name, number;
	
	public EntryFrame(PhonebookUI parent, BookManager manager, int typ) {
		this.parent = parent;
		this.manager = manager;
		this.typ = typ;
		init();
	}
	
	private void init() {
		JPanel pName = new JPanel();
		JPanel pNumber = new JPanel();
		JPanel pBtn = new JPanel();
		tfName = new JTextField();
		tfNumber = new JTextField();
		JButton btnOK = new JButton(PhoneRes.getIString("btn.ok"));
		JButton btnCancel = new JButton(PhoneRes.getIString("btn.cancel"));
		
		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				EntryFrame.this.dispose();
			}
		});
		
		btnOK.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(tfName.getText() != null 
					&& !tfName.getText().trim().equals("")
					&& tfNumber.getText() != null 
					&& !tfNumber.getText().trim().equals("")) {
					// choose between edit or add
					if(typ == TYP_EDIT) {
						// if there where problems, don't close the dialog
						if(!manager.update(manager.getPhonebookEntry(name, number), tfName.getText(), tfNumber.getText()))
							return;
					}
					else if(!manager.add(tfName.getText(), tfNumber.getText())) {
							JOptionPane.showMessageDialog(EntryFrame.this, PhoneRes.getIString("book.exists"), 
								PhoneRes.getIString("book.warning"), JOptionPane.WARNING_MESSAGE);
							return;
					}
					EntryFrame.this.dispose();
					parent.loadEntries();
				}
				else {
					JOptionPane.showMessageDialog(EntryFrame.this, PhoneRes.getIString("book.fillFields"),
						PhoneRes.getIString("book.warning"), JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		pName.setLayout(new BoxLayout(pName, BoxLayout.X_AXIS));
		pName.setBorder(new EmptyBorder(5,5,5,5));
		pName.add(new JLabel(PhoneRes.getIString("book.name") + ":"));
		pName.add(Box.createHorizontalStrut(17));
		pName.add(tfName);
		
		pNumber.setLayout(new BoxLayout(pNumber, BoxLayout.X_AXIS));
		pNumber.setBorder(new EmptyBorder(5,5,5,5));
		pNumber.add(new JLabel(PhoneRes.getIString("book.number") + ":"));
		pNumber.add(Box.createHorizontalStrut(5));
		pNumber.add(tfNumber);
		
		pBtn.setLayout(new BoxLayout(pBtn, BoxLayout.X_AXIS));
		pBtn.setBorder(new EmptyBorder(5,5,5,5));
		pBtn.add(btnOK);
		pBtn.add(Box.createHorizontalStrut(5));
		pBtn.add(btnCancel);
		
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.add(pName);
		this.add(pNumber);
		this.add(pBtn);
		
		this.setResizable(false);
		this.setIconImage(SparkRes.getImageIcon(SparkRes.ADDRESS_BOOK_16x16).getImage());
		this.setSize(new Dimension(250,130));
		if(typ == TYP_ADD) {
			this.setTitle(PhoneRes.getIString("frame.add"));
		}
		else if(typ == TYP_EDIT) {
			this.setTitle(PhoneRes.getIString("frame.edit"));
		}
	}
	
	public void setName(String name) {
		tfName.setText(name);
		this.name = name;
	}
	
	public void setNumber(String number) {
		tfNumber.setText(number);
		this.number = number;
	}
	
	public void invoke() {
		GraphicUtils.centerWindowOnComponent(this, parent);
		this.setVisible(true);
	}
}
