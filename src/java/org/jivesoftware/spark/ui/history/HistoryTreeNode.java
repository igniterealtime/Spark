package org.jivesoftware.spark.ui.history;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Vyacheslav Durin (nixspirit@gmail.com)
 * 
 *         Apr 15, 2013
 * @version 0.1
 */
public class HistoryTreeNode extends DefaultMutableTreeNode {

	/**  */
	private static final long serialVersionUID = 9201268406146063915L;

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
