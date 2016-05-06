package org.jivesoftware.spark.ui.history;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Vyacheslav Durin (nixspirit@gmail.com)
 * 
 *         Apr 15, 2013
 * @version 0.1
 */
public class XMLHistoryFile extends AbstractHistoryFile {

	private static final String MESSAGE_TAG = "message";
	private InputStream roomFileStream;

	/**
	 * @param roomFileStream
	 */
	public XMLHistoryFile(InputStream fileStream) {

		if (fileStream == null)
			throw new IllegalArgumentException("History Stream cannot be null");

		roomFileStream = fileStream;
	}

	@Override
	protected long getSize() {
		try {
			return roomFileStream.available() / SIZE_MULTIPLICATOR;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	protected List<HistoryEntry> createEntries() {
		Document historyXML = read();
		if ( historyXML == null)
			return Collections.emptyList();

		Element document = historyXML.getDocumentElement();
		Map<Date, HistoryEntry> months = new HashMap<>();
		Map<Date, HistoryEntry> days = new HashMap<>();

		NodeList nl = document.getElementsByTagName(MESSAGE_TAG);
		for (int i = 0; i < nl.getLength(); i++) {
			Element messageElement = (Element) nl.item(i);
			HistoryMessage message = new HistoryMessage(messageElement);

			Date normalizedMonth = message.getNormalizedMonth();
			Date normalizedDate = message.getNormalizedDate();

			// create month
			if (!months.containsKey(normalizedMonth)) {
				HistoryEntry monthEntry = new HistoryEntry();
				monthEntry.setDate(normalizedMonth);
				monthEntry.setName(MONTH_NAME_FORMAT.format(normalizedMonth));
				months.put(normalizedMonth, monthEntry);
			}

			// create a day and put it in a month
			if (!days.containsKey(normalizedDate)) {
				HistoryEntry dayEntry = new HistoryEntry();
				dayEntry.setDate(normalizedDate);
				dayEntry.setName(DAY_NAME_FORMAT.format(normalizedDate));
				days.put(normalizedDate, dayEntry);
				months.get(normalizedMonth).getEntries().add(dayEntry);
			}

			// add messages to a day
			HistoryEntry entry = days.get(normalizedDate);
			entry.getMessages().add(message);
		}

		// printTree(months, days);

		// sort by months
		return toList(months);

	}

	private Document read() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(roomFileStream);
			roomFileStream.close();
			doc.getDocumentElement().normalize();
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}