package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.Table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

final class RoomList extends Table {
    public RoomList() {
        super(new String[]{
            " ",
            Res.getString("title.name"),
            Res.getString("title.address"),
            Res.getString("title.occupants"),
            Res.getString("menuitem.languages"),
            Res.getString("description"),
        });
        getColumnModel().setColumnMargin(0);
        getColumnModel().getColumn(0).setMaxWidth(30);
        getColumnModel().getColumn(3).setMaxWidth(80);
        getColumnModel().getColumn(4).setMaxWidth(120);

        setSelectionBackground(Table.SELECTION_COLOR);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionAllowed(true);
        // setSortable(true);
    }

    // Handle image rendering correctly
    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        Object o = getValueAt(row, column);
        if (o != null) {
            if (o instanceof JLabel) {
                return new JLabelRenderer(false);
            }
        }
        if (column == 3) {
            return new ConferenceRoomBrowser.CenterRenderer();
        }
        return super.getCellRenderer(row, column);
    }
}
