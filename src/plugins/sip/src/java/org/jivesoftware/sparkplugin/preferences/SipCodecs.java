package org.jivesoftware.sparkplugin.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.media.format.AudioFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import net.java.sipmack.softphone.SoftPhoneManager;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;

public class SipCodecs extends JPanel
{
	private static final long	serialVersionUID	= 321651651106469534L;
	private static final String SEPERATOR = "\\^";
	private static final String SEP = "^";
	private Vector<Vector<String>> dataSel;
	private Vector<Vector<String>> dataAvail;
	private DefaultTableModel modelSel; 
	private DefaultTableModel modelAvail;
	private JTable tSel;
	private JTable tAvail;
	
	public SipCodecs() {
		init();
	}
	
	private void init() {
		tSel = new JTable();
		tAvail = new JTable();
		JScrollPane scrollSel = new JScrollPane(tSel);
		JScrollPane scrollAvail = new JScrollPane(tAvail);
		JButton btnLeft = new JButton(PhoneRes.getIString("codecs.select"));
		JButton btnRight = new JButton(PhoneRes.getIString("codecs.unselect"));
		JButton btnUp = new JButton(PhoneRes.getIString("codecs.up"));
		JButton btnDown = new JButton(PhoneRes.getIString("codecs.down"));
		JPanel pBtnMid = new JPanel();
		JPanel pBtnDown = new JPanel();
		JPanel pSel = new JPanel();
		JPanel pAvail = new JPanel();
		modelSel = new DefaultTableModel(){
			private static final long	serialVersionUID	= 7489555730751416391L;

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		modelAvail = new DefaultTableModel(){
			private static final long	serialVersionUID	= -3119910584736396606L;

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		
		tSel.setModel(modelSel);
		tAvail.setModel(modelAvail);
		
		// set sizes
		btnLeft.setMinimumSize(new Dimension(100,24));
		btnLeft.setMaximumSize(new Dimension(100,24));
		btnLeft.setPreferredSize(new Dimension(100,24));
		
		btnRight.setMinimumSize(new Dimension(100,24));
		btnRight.setMaximumSize(new Dimension(100,24));
		btnRight.setPreferredSize(new Dimension(100,24));
		
		btnUp.setMinimumSize(new Dimension(90,24));
		btnUp.setMaximumSize(new Dimension(90,24));
		btnUp.setPreferredSize(new Dimension(90,24));
		
		btnDown.setMinimumSize(new Dimension(90,24));
		btnDown.setMaximumSize(new Dimension(90,24));
		btnDown.setPreferredSize(new Dimension(90,24));
		
		tSel.setMinimumSize(new Dimension(185, 300));
		tSel.setPreferredSize(new Dimension(185, 300));
		scrollSel.setMinimumSize(new Dimension(185, 300));
		scrollSel.setPreferredSize(new Dimension(185, 300));
		
		tAvail.setMinimumSize(new Dimension(190, 300));
		tAvail.setPreferredSize(new Dimension(190, 300));
		scrollAvail.setMinimumSize(new Dimension(190, 300));
		scrollAvail.setPreferredSize(new Dimension(190, 300));
		
		// add actionlisteners
		btnLeft.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				left();
			}
		});
		
		btnRight.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				right();
			}
		});
		
		btnUp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				up();
			}
		});
		
		btnDown.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				down();
			}
		});
		
		// add Components
		pBtnDown.setLayout(new BoxLayout(pBtnDown, BoxLayout.X_AXIS));
		pBtnDown.add(Box.createHorizontalGlue());
		pBtnDown.add(btnUp);
		pBtnDown.add(Box.createHorizontalStrut(5));
		pBtnDown.add(btnDown);
		pBtnDown.add(Box.createHorizontalGlue());
		pBtnDown.setBorder(new EmptyBorder(5,5,5,5));
		
		pSel.setLayout(new BorderLayout());
		pSel.add(scrollSel, BorderLayout.CENTER);
		pSel.add(pBtnDown, BorderLayout.SOUTH);
		
		pBtnMid.setLayout(new BoxLayout(pBtnMid, BoxLayout.Y_AXIS));
		pBtnMid.add(btnLeft);
		pBtnMid.add(Box.createVerticalStrut(5));
		pBtnMid.add(btnRight);
		pBtnMid.setBorder(new EmptyBorder(5,5,5,5));
		
		pAvail.setLayout(new BorderLayout());
		pAvail.add(scrollAvail);
		pAvail.setBorder(new EmptyBorder(0,0,34,0));
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(pSel);
		this.add(pBtnMid);
		this.add(pAvail);
		
	}
	
	/**
	 * set the codecs which where selected in the correct order
	 * @param sel
	 */
	public void setSelected(String sel) {
		// if there are no codecs selected, select all
		if(sel == null 
			|| sel.trim().length() == 0) {
			sel = allCodecs();
			// clear the available-Vector
			setAvailable("");
		}

		String[] split = sel.split(SEPERATOR);
		Vector<String> temp;
		dataSel = new Vector<Vector<String>>();
		
		for(String selsplit: split) {
			temp = new Vector<String>();
			temp.add(selsplit);
			dataSel.add(temp);
		}
		Vector<String> heading = new Vector<String>();
		heading.add(PhoneRes.getIString("codecs.audio.selected"));
		
		modelSel.setDataVector(dataSel, heading);
	}
	
	/**
	 * set the codecs which where available
	 * @param avail
	 */
	public void setAvailable(String avail) {
		if(avail == null) {
			avail = "";
		}
			
		String[] split = avail.split(SEPERATOR);
		Vector<String> temp;
		dataAvail = new Vector<Vector<String>>();
		
		if(avail.trim()!= "")
			for(String selavail: split) {
				temp = new Vector<String>();
				temp.add(selavail);
				dataAvail.add(temp);
			}
		
		Vector<String> heading = new Vector<String>();
		heading.add(PhoneRes.getIString("codecs.audio.avail"));
		
		modelAvail.setDataVector(dataAvail, heading);
	}
	
	public String getSelected() {
		return formatToString(dataSel);
	}
	
	public String getAvailable() {
		return formatToString(dataAvail);
	}
	
	private String formatToString(Vector<Vector<String>> vec) {
		String str = "";
		for(Vector<String> vecString : vec) {
			str = str + vecString.get(0) + SEP;
		}
		return str;
	}
	
	/**
	 * gets all the codecs from the fmjMediaManager 
	 * and put them into a string (seperated by SEPERATOR)
	 * 
	 * @return
	 */
	private String allCodecs() {
		String all = "";
		List<AudioFormat> codecs = SoftPhoneManager.getInstance().getJmfMediaManager().getAudioFormats();
		for(AudioFormat audio : codecs) {
			all = all + audio.getEncoding() + SEP;
		}
		return all;
	}
	
	/**
	 * the selected Rows from the selected-codec-table will be 
	 * moved to the available-table 
	 */
	private void right() {
		int[] selRows = tSel.getSelectedRows();
		// check if there are rows selected
		if(selRows == null 
			|| selRows.length == 0) {
			JOptionPane.showMessageDialog(this, PhoneRes.getIString("book.noEntry"), 
				PhoneRes.getIString("book.warning"), JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		Vector<String> temp;
		for (int i = 0; i < selRows.length; i++)
		{
			// -i because the error after deleting a row
			temp = dataSel.elementAt(selRows[i]-i);
			modelSel.removeRow(selRows[i]-i);
			modelAvail.addRow(temp);
		}
		
		tSel.updateUI();
		tAvail.updateUI();
	}
	
	/**
	 * the selected Rows from the available-codec-table will be 
	 * moved to the selected-table 
	 */
	private void left() {
		int[] selRows = tAvail.getSelectedRows();
		// check if there are rows selected
		if(selRows == null 
			|| selRows.length == 0) {
			JOptionPane.showMessageDialog(this, PhoneRes.getIString("book.noEntry"), 
				PhoneRes.getIString("book.warning"), JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		Vector<String> temp;
		for (int i = 0; i < selRows.length; i++)
		{
			// -i because the error after deleting a row
			temp = dataAvail.elementAt(selRows[i]-i);
			modelAvail.removeRow(selRows[i]-i);
			modelSel.addRow(temp);
		}
		
		tAvail.updateUI();
		tSel.updateUI();
	}
	
	/**
	 * changes the order of the selected Codecs (move selected Row up)
	 */
	private void up() {
		int selrow = tSel.getSelectedRow();
		if (selrow != 0 // only move up if it isn't the first row
			&& selrow != -1) { 
			Vector<String> oldRow = dataSel.elementAt(selrow);
			Vector<String> newRow = dataSel.elementAt(selrow - 1);

			modelSel.moveRow(tSel.getSelectedRow(), tSel.getSelectedRow(), tSel.getSelectedRow() - 1);

			dataSel.setElementAt(oldRow, selrow - 1);
			dataSel.setElementAt(newRow, selrow);
			modelSel.fireTableDataChanged();

			tSel.setRowSelectionInterval(selrow - 1, selrow - 1);
			tSel.updateUI();
		}
	}
	
	/**
	 * changes the order of the selected Codecs (move selected Row down)
	 */
	private void down() {
		int selrow = tSel.getSelectedRow();
		if (selrow < tSel.getRowCount()-1 // only move up if it isn't the last row
			&& selrow != -1) { 
			Vector<String> oldRow = dataSel.elementAt(selrow);
			Vector<String> newRow = dataSel.elementAt(selrow + 1);

			modelSel.moveRow(tSel.getSelectedRow(), tSel.getSelectedRow(), tSel.getSelectedRow() + 1);

			dataSel.setElementAt(oldRow, selrow + 1);
			dataSel.setElementAt(newRow, selrow);
			modelSel.fireTableDataChanged();

			tSel.setRowSelectionInterval(selrow + 1, selrow + 1);
			tSel.updateUI();
		}
	}
}
