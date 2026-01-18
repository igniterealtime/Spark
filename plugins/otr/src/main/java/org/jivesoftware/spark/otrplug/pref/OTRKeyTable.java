package org.jivesoftware.spark.otrplug.pref;

import java.awt.BorderLayout;

import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jivesoftware.spark.otrplug.util.OTRResources;

/**
 * OTRKeyTable displays a key table.
 * You can add keys using the addEntry() method
 * 
 * @author Bergunde Holger
 */
public class OTRKeyTable extends JPanel {
    private static final long serialVersionUID = -2922785387942547350L;
    private JTable _table;
    private DefaultTableModel _tableModel;

    public OTRKeyTable() {
        final String[] header = { OTRResources.getString("otr.table.jid"), OTRResources.getString("otr.table.public.key"), OTRResources.getString("otr.key.verified") };

        _tableModel = new MyTableModel(header);
        _table = new JTable(_tableModel);
        _table.getTableHeader().setReorderingAllowed(false);

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(_table), BorderLayout.CENTER);
    }

    static class MyTableModel extends DefaultTableModel {
        private static final long serialVersionUID = -2930577433474767242L;
        public MyTableModel(String[] headers) {
            super(headers, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 2 ? Boolean.class : String.class;
        }
    }

    public void addTableChangeListener(TableModelListener listener) {
        _tableModel.addTableModelListener(listener);
    }

    /**
     * Adds a key to the table
     */
    public void addEntry(String jid, String hash, boolean verified) {
        Vector<Object> data = new Vector<Object>(3);
        data.add(jid);
        data.add(hash);
        data.add(verified);
        _tableModel.addRow(data);
    }

    /**
     * Returns a specified row
     */
    public Object getValueAt(int row, int col) {
        return _tableModel.getValueAt(row, col);
    }

}
