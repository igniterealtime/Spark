package org.jivesoftware.spark.ui.history;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Vyacheslav Durin (nixspirit@gmail.com)
 */
public class HistoryTreeNode extends DefaultMutableTreeNode {
	private HistoryEntry historyEntry;

	public HistoryTreeNode(String roomName) {
		super(roomName);
	}

	public HistoryTreeNode(HistoryEntry historyEntry, String roomName) {
		super(roomName);
		this.historyEntry = historyEntry;
	}

	public void setHistoryEntry(HistoryEntry historyEntry) {
		this.historyEntry = historyEntry;
	}

	public HistoryEntry getHistoryEntry() {
		return historyEntry;
	}
}
