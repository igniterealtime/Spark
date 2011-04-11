package org.jivesoftware.sparkplugin.preferences;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.media.format.AudioFormat;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.java.sipmack.softphone.SoftPhoneManager;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;

public class SipCodecs extends JPanel {
    private static final long serialVersionUID = 321651651106469534L;
    private static final String SEPERATOR = "\\^";
    private static final String SEP = "^";

    private JList _listSelected;
    private DefaultListModel _listSelectedModel;
    private JList _listAvailable;
    private DefaultListModel _listAvailableModel;

    public SipCodecs() {
	init();
    }

    private void init() {

	_listSelectedModel = new DefaultListModel();
	_listSelected = new JList(_listSelectedModel);

	_listAvailableModel = new DefaultListModel();
	_listAvailable = new JList(_listAvailableModel);

	JScrollPane scrollSel = new JScrollPane(_listSelected);
	JScrollPane scrollAvail = new JScrollPane(_listAvailable);

	JButton btnLeft = new JButton(PhoneRes.getIString("codecs.select"));
	JButton btnRight = new JButton(PhoneRes.getIString("codecs.unselect"));
	JButton btnUp = new JButton(PhoneRes.getIString("codecs.up"));
	JButton btnDown = new JButton(PhoneRes.getIString("codecs.down"));

	btnUp.setMinimumSize(new Dimension(80, 25));
	btnUp.setPreferredSize(new Dimension(80, 25));
	btnUp.setMaximumSize(new Dimension(80, 25));
	btnDown.setMaximumSize(new Dimension(80, 25));
	btnDown.setPreferredSize(new Dimension(80, 25));
	btnDown.setMaximumSize(new Dimension(80, 25));

	JPanel panelButtonUpDown = new JPanel();
	JPanel panelSelect = new JPanel();
	JPanel panelAvailable = new JPanel();

	// add actionlisteners
	btnLeft.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		left();
	    }
	});

	btnRight.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		right();
	    }
	});

	btnUp.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		up();
	    }
	});

	btnDown.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		down();
	    }
	});

	// add Components
	panelButtonUpDown.setLayout(new VerticalFlowLayout(
		VerticalFlowLayout.MIDDLE));
	panelButtonUpDown.add(btnUp);
	panelButtonUpDown.add(btnDown);

	panelSelect.setLayout(new BoxLayout(panelSelect, BoxLayout.Y_AXIS));
	panelSelect.add(btnRight);
	panelSelect.add(new JLabel(PhoneRes.getIString("codecs.audio.selected")
		+ ":"));
	panelSelect.add(scrollSel);

	panelAvailable
		.setLayout(new BoxLayout(panelAvailable, BoxLayout.Y_AXIS));
	panelAvailable.add(btnLeft);
	panelAvailable.add(new JLabel(PhoneRes.getIString("codecs.audio.avail")
		+ ":"));
	panelAvailable.add(scrollAvail);

	this.setLayout(new GridBagLayout());
	this.add(panelButtonUpDown, new GridBagConstraints(0, 0, 1, 1, 0.0,
		0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
		new Insets(5, 5, 5, 5), 0, 0));
	this.add(panelSelect, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
		GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
			5, 5, 5), 0, 0));
	this.add(panelAvailable, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
		GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
			5, 5, 5), 0, 0));

    }

    /**
     * set the codecs which where selected in the correct order
     * 
     * @param sel
     */
    public void setSelected(String sel) {
	// if there are no codecs selected, select all
	_listSelectedModel.removeAllElements();
	if (sel == null || sel.trim().length() == 0) {
	    sel = allCodecs();
	    // clear the available-Vector
	    setAvailable(null);
	}

	String[] split = sel.split(SEPERATOR);

	for (String selsplit : split) {

	    _listSelectedModel.addElement(selsplit);
	}

    }

    /**
     * set the codecs which where available
     * 
     * @param avail
     */
    public void setAvailable(String avail) {
	_listAvailableModel.removeAllElements();
	if (avail == null || avail.equals("")) {
	    return;
	}

	String[] split = avail.split(SEPERATOR);
	if (avail.trim() != "")
	    for (String selavail : split) {
		_listAvailableModel.addElement(selavail);
	    }

    }

    public String getSelected() {
	Vector<String> selected = new Vector<String>();

	for (Object s : _listSelectedModel.toArray()) {
	    String ss = (String) s;
	    selected.add(ss);
	}

	return formatToString(selected);
    }

    public String getAvailable() {
	Vector<String> selected = new Vector<String>();
	if (_listAvailableModel.toArray().length > 0) {
	    for (Object s : _listAvailableModel.toArray()) {
		String ss = (String) s;
		selected.add(ss);
	    }

	    return formatToString(selected);
	} else
	    return "";
    }

    private String formatToString(Vector<String> vec) {
	String str = "";
	for (String vecString : vec) {
	    str = str + vecString + SEP;
	}
	return str;
    }

    /**
     * gets all the codecs from the fmjMediaManager and put them into a string
     * (seperated by SEPERATOR)
     * 
     * @return
     */
    private String allCodecs() {
	String all = "";
	List<AudioFormat> codecs = SoftPhoneManager.getInstance()
		.getJmfMediaManager().getAudioFormats();
	for (AudioFormat audio : codecs) {
	    all = all + audio.getEncoding() + SEP;
	}
	return all;
    }

    /**
     * the selected Rows from the selected-codec-table will be moved to the
     * available-table
     */
    private void right() {

	int[] selRows = _listSelected.getSelectedIndices();// tableSelected.getSelectedRows();
	// check if there are rows selected
	if (selRows == null || selRows.length == 0) {
	    JOptionPane.showMessageDialog(this,
		    PhoneRes.getIString("book.noEntry"),
		    PhoneRes.getIString("book.warning"),
		    JOptionPane.WARNING_MESSAGE);
	    return;
	}
	Vector<String> removelater = new Vector<String>();
	for (int i = 0; i < selRows.length; i++) {
	    String item = (String) _listSelectedModel.elementAt(selRows[i]);

	    _listAvailableModel.addElement(item);
	    removelater.add(item);
	}

	for (String item : removelater) {
	    _listSelectedModel.removeElement(item);
	}

	_listSelected.updateUI();
	_listAvailable.updateUI();

    }

    /**
     * the selected Rows from the available-codec-table will be moved to the
     * selected-table
     */
    private void left() {
	int[] selRows = _listAvailable.getSelectedIndices();// tableAvailable.getSelectedRows();
	// check if there are rows selected
	if (selRows == null || selRows.length == 0) {
	    JOptionPane.showMessageDialog(this,
		    PhoneRes.getIString("book.noEntry"),
		    PhoneRes.getIString("book.warning"),
		    JOptionPane.WARNING_MESSAGE);
	    return;
	}

	Vector<String> removelater = new Vector<String>();
	for (int i = 0; i < selRows.length; i++) {
	    String item = (String) _listAvailableModel.elementAt(selRows[i]);
	    _listSelectedModel.addElement(item);
	    removelater.add(item);

	}
	for (String item : removelater) {
	    _listAvailableModel.removeElement(item);
	}

	_listAvailable.updateUI();
	_listSelected.updateUI();

    }

    /**
     * changes the order of the selected Codecs (move selected Row up)
     */
    private void up() {
	int oldindex = _listSelected.getSelectedIndex();// tableSelected.getSelectedRow();
	if (oldindex > 0) {

	    Object obj = _listSelected.getSelectedValue();

	    _listSelectedModel.removeElementAt(oldindex);
	    _listSelectedModel.add(oldindex - 1, obj);
	}
	_listSelected.updateUI();
    }

    /**
     * changes the order of the selected Codecs (move selected Row down)
     */
    private void down() {

	int oldindex = _listSelected.getSelectedIndex();// tableSelected.getSelectedRow();

	Object obj = _listSelected.getSelectedValue();

	_listSelectedModel.removeElementAt(oldindex);
	_listSelectedModel.add(oldindex + 1, obj);
	_listSelected.updateUI();

    }
}
