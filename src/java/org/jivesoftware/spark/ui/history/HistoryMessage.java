package org.jivesoftware.spark.ui.history;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Vyacheslav Durin (nixspirit@gmail.com)
 * 
 *         Apr 17, 2013
 * @version 0.1
 */
public class HistoryMessage {

	private static final String EMPTY = "";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.S z");
	private static final SimpleDateFormat fromDateFormat = new SimpleDateFormat(
			"hh:mm:ss a");
	private static final MessageFormat contentFormat = new MessageFormat(
			"<font color=\"#0066CC\" style=\"font-weight: bold;\">({0}) {1}:</font> {2}<br />");
	private Element messageElement;
	private String to;
	private String from;
	private String body;
	private Date date;

	public HistoryMessage(Element messageEl) {
		this.messageElement = messageEl;

		String to = getElementValue(this.messageElement, "to");
		String from = getElementValue(this.messageElement, "from");
		String body = getElementValue(this.messageElement, "body");
		String date = getElementValue(this.messageElement, "date");

		setTo(to);
		setFrom(from);
		setBody(body);
		setDate(date);
	}

	public HistoryMessage() {
	}

	public HistoryMessage(HistoryMessage orig) {
		if (orig == null)
			throw new IllegalArgumentException(
					"Original message cannot be null");
		setTo(orig.getTo());
		setFrom(orig.getFrom());
		setBody(orig.getBody());
		setDate(orig.getDate());
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = parseHistoryDate(date);
	}

	/**
	 * @return the messageElement
	 */
	public Element getXML() {
		return messageElement;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	public Date getNormalizedDate() {
		Calendar cal = normalizeDate(getDate());
		return cal.getTime();
	}

	public Date getNormalizedMonth() {
		Calendar cal = normalizeDate(getDate());
		cal.set(Calendar.DATE, 1);
		return cal.getTime();
	}

	public String getContent() {
		return contentFormat.format(new String[] { fromDateFormat.format(date),
				from, body });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "HistoryMessage to=" + to + ", from=" + from + ", body=" + body
				+ ", date=" + date + "]";
	}

	private static Date parseHistoryDate(String dateToParse) {
		try {
			return dateFormat.parse(dateToParse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Calendar.getInstance().getTime();
	}

	private static Calendar normalizeDate(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	private static String getElementValue(Element element, String tag) {
		if (element == null || tag == null || EMPTY.equals(tag))
			return EMPTY;

		NodeList to = element.getElementsByTagName(tag);
		if (to.getLength() < 1)
			return EMPTY;
		Element el = (Element) to.item(0);
		if (el == null)
			return EMPTY;
		return el.getFirstChild().getNodeValue();
	}

}
