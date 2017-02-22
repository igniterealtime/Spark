package org.jivesoftware.spark.ui.history;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Vyacheslav Durin (nixspirit@gmail.com)
 * 
 *         Apr 15, 2013
 * @version 0.1
 */
public class HistoryEntry {

	private String name;
	private List<HistoryEntry> entries;
	private List<HistoryMessage> messages;
	private Date date;

	public HistoryEntry() {
	}

	public HistoryEntry(HistoryEntry orig) {
		if (orig == null)
			throw new IllegalArgumentException("Orig cannob be null");
		setDate(orig.getDate());
		setName(orig.getName());
		for (HistoryMessage msg : orig.getMessages()) {
			getMessages().add(new HistoryMessage(msg));
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the entries
	 */
	public List<HistoryEntry> getEntries() {
		if (entries == null)
			entries = new ArrayList<>();
		return entries;
	}

	/**
	 * @param entries
	 *            the entries to set
	 */
	public void setEntries(List<HistoryEntry> entries) {
		this.entries = entries;
	}

	public List<HistoryMessage> getMessages() {
		if (null == messages)
			messages = new ArrayList<>();
		return messages;
	}

	/**
	 * @return true if there are any sub entries in it
	 */
	public boolean hasRecords() {
		return entries != null && entries.size() > 0;
	}

	/**
	 * @return true if there are any messages in it
	 */
	public boolean isEmpty() {
		return messages == null || messages.size() < 1;
	}

	public String getHistory() {
		StringBuilder history = new StringBuilder();
		for (HistoryMessage msg : getMessages()) {
			history.append(msg.getContent());
		}
		return history.toString();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "HistoryEntry [ name=" + name + ", entries=" + entries;
	}

}
