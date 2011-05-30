/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.sparkimpl.search.users;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Column;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.Table;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.RosterDialog;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.profile.VCardManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * UserSearchResults displays the UI for all users found using the JEP-055 search service.
 */
public class UserSearchResults extends JPanel {

    private static final long serialVersionUID = 4196389090818949068L;
    private UsersInfoTable resultsTable;

    /**
     * Intiliaze the Search Service Results UI.
     */
    public UserSearchResults() {
        setLayout(new BorderLayout());
    }

    /**
     * Populate the SearchResults UI table with the ReportedData returned from the search service.
     *
     * @param data the <code>ReportedData</code> returned by the Search Service.
     */
    public void showUsersFound(ReportedData data) {
        List<String> columnList = new ArrayList<String>();
        Iterator<Column> columns = data.getColumns();
        while (columns.hasNext()) {
            Column column = columns.next();
            String label = column.getLabel();
            columnList.add(label);
        }

        if (resultsTable == null) {
            resultsTable = new UsersInfoTable(columnList.toArray(new String[columnList.size()]));

            final JScrollPane scrollPane = new JScrollPane(resultsTable);
            scrollPane.getViewport().setBackground(Color.white);

            add(scrollPane, BorderLayout.CENTER);

            resultsTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int row = resultsTable.getSelectedRow();
                        openChatRoom(row);
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    checkPopup(e);
                }

                public void mousePressed(MouseEvent e) {
                    checkPopup(e);
                }
            });
        }
        else {
            resultsTable.clearTable();
        }
        // Populate with answers
        Iterator<Row> rows = data.getRows();
        List<String> modelList;
        while (rows.hasNext()) {
            modelList = new ArrayList<String>();
            Row row = rows.next();
            for (int i = 0; i < resultsTable.getColumnCount(); i++) {
                String tableValue = (String)resultsTable.getTableHeader().getColumnModel().getColumn(i).getHeaderValue();
                Iterator<Column> columnIterator = data.getColumns();
                while (columnIterator.hasNext()) {
                    Column column = columnIterator.next();
                    if (column.getLabel().equals(tableValue)) {
                        tableValue = column.getVariable();
                        break;
                    }
                }

                String modelValue = getFirstValue(row, tableValue);
                modelList.add(modelValue);
            }

            resultsTable.getTableModel().addRow(modelList.toArray());

        }
    }

    private void checkPopup(MouseEvent e) {
        if (!e.isPopupTrigger()) {
            return;
        }
        // Get agent
        final int row = resultsTable.rowAtPoint(e.getPoint());

        final JPopupMenu menu = new JPopupMenu();

        Action addContactAction = new AbstractAction() {
	    private static final long serialVersionUID = -6377937878941477145L;

	    public void actionPerformed(ActionEvent e) {
                RosterDialog dialog = new RosterDialog();
                String jid = (String)resultsTable.getValueAt(row, 0);

                TableColumn column = null;
                try {
                    column = resultsTable.getColumn("Username");
                }
                catch (Exception ex) {
                    try {
                        column = resultsTable.getColumn("nick");
                    }
                    catch (Exception e1) {
                        // Nothing to do
                    }
                }
                if (column != null) {
                    int col = column.getModelIndex();
                    String nickname = (String)resultsTable.getValueAt(row, col);
                    if (!ModelUtil.hasLength(nickname)) {
                        nickname = StringUtils.parseName(jid);
                    }
                    dialog.setDefaultNickname(nickname);
                }

                dialog.setDefaultJID(jid);
                dialog.showRosterDialog();
            }
        };

        Action chatAction = new AbstractAction() {
	    private static final long serialVersionUID = 5651812282020177800L;

	    public void actionPerformed(ActionEvent e) {
                openChatRoom(row);
            }
        };

        Action profileAction = new AbstractAction() {
  	    private static final long serialVersionUID = -2014872840628217586L;

	    public void actionPerformed(ActionEvent e) {
                VCardManager vcardSupport = SparkManager.getVCardManager();
                String jid = (String)resultsTable.getValueAt(row, 0);
                vcardSupport.viewProfile(jid, resultsTable);
            }
        };

        if (!Default.getBoolean(Default.ADD_CONTACT_DISABLED)) {
	        final JMenuItem addAsContact = new JMenuItem(addContactAction);
	        addContactAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_ADD_IMAGE));
	        addContactAction.putValue(Action.NAME, Res.getString("menuitem.add.as.contact"));
	        menu.add(addAsContact);
        }

        final JMenuItem chatMenu = new JMenuItem(chatAction);
        chatAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
        chatAction.putValue(Action.NAME, Res.getString("menuitem.chat"));
        menu.add(chatMenu);

        final JMenuItem viewProfileMenu = new JMenuItem(profileAction);
        profileAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_PROFILE_IMAGE));
        profileAction.putValue(Action.NAME, Res.getString("menuitem.view.profile"));
        menu.add(viewProfileMenu);


        menu.show(resultsTable, e.getX(), e.getY());
    }

    private final class UsersInfoTable extends Table {
	private static final long serialVersionUID = -7097826349368800291L;

	UsersInfoTable(String[] headers) {
            super(headers);
            getColumnModel().setColumnMargin(0);
            setSelectionBackground(Table.SELECTION_COLOR);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setRowSelectionAllowed(true);
        }
    }

    /**
     * Returns the first value found in the ReportedData.Row.
     *
     * @param row the ReportedData.Row.
     * @param key the specified key in the ReportedData.Row.
     * @return the first value found in the ReportedData.Row
     */
    public String getFirstValue(ReportedData.Row row, String key) {
        try {
            final Iterator<String> rows = row.getValues(key);
            while (rows.hasNext()) {
                return rows.next();
            }
        }
        catch (Exception e) {
            Log.error("Error retrieving the first value.", e);
        }
        return null;
    }

    private void openChatRoom(int row) {
        String jid = (String)resultsTable.getValueAt(row, 0);
        String nickname = StringUtils.parseName(jid);

        TableColumn column;
        try {
            column = resultsTable.getColumn("nick");
            int col = column.getModelIndex();
            nickname = (String)resultsTable.getValueAt(row, col);
            if (!ModelUtil.hasLength(nickname)) {
                nickname = StringUtils.parseName(jid);
            }
        }
        catch (Exception e1) {
            // Ignore e1
        }

        ChatManager chatManager = SparkManager.getChatManager();
        ChatRoom chatRoom = chatManager.createChatRoom(jid, nickname, nickname);

        ChatContainer chatRooms = chatManager.getChatContainer();
        chatRooms.activateChatRoom(chatRoom);
    }

    /**
     * Clears the Search Results Table.
     */
    public void clearTable() {
        if (resultsTable != null) {
            resultsTable.clearTable();
        }
    }

}
