package org.jivesoftware.spark.ui;

import java.awt.BorderLayout;

import java.util.Vector;

import javax.swing.JComponent;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jivesoftware.spark.otrplug.OTRResources;

public class OTRKeyTable extends JComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -2922785387942547350L;
    private JTable _table;
    private DefaultTableModel _tableModel;

    public OTRKeyTable() {

        final String[] header = { OTRResources.getString("otr.table.jid"), OTRResources.getString("otr.table.public.key"), OTRResources.getString("otr.key.verified") };

        _tableModel = new MyTableModel(header);
        _table = new JTable(_tableModel);

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(_table), BorderLayout.CENTER);

    }

    class MyTableModel extends DefaultTableModel {
        /**
         * 
         */
        private static final long serialVersionUID = -2930577433474767242L;

        public MyTableModel(String[] headers) {
            super(headers, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 2)
                return true;
            else
                return false;

        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 2) {
                return Boolean.class;
            } else
                return String.class;

        }
    }

    public void addTableChangeListener(TableModelListener listener) {
        _tableModel.addTableModelListener(listener);
    }

    public void addEntry(String jid, String hash, boolean verified) {
        Vector<Object> data = new Vector<Object>(3);

        data.add(jid);
        data.add(hash);
        data.add(verified);

        _tableModel.addRow(data);
    }

    public Object getValueAt(int row, int col) {
        return _tableModel.getValueAt(row, col);
    }

}