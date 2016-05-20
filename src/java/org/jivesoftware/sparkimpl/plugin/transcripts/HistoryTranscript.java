package org.jivesoftware.sparkimpl.plugin.transcripts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLEditorKit;


import org.jdesktop.swingx.calendar.DateUtils;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.BackgroundPanel;
import org.jivesoftware.spark.ui.VCardPanel;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

/**
 * This class represents the history transcript
 * @author tim.jentz
 *
 */
public class HistoryTranscript extends SwingWorker {

	private Semaphore token = new Semaphore(1);
	private int pageIndex = 0;
	private int maxPages = 0;
	private final String period_oneMonth = "message.search.period.month.one";
	private final String period_oneYear = "message.search.period.year.one";
	private final String period_noPeriod = "message.search.period.none";
	private String searchPeriod = "";
	private List<String> periods = new ArrayList<>();
	private final timerTranscript transcriptTask = new timerTranscript();
	private JLabel pageCounter = new JLabel("0 / 0");
	private JButton pageLeft = new JButton("<");
	private JButton pageRight = new JButton(">");
	private String jid = null;
	private SimpleDateFormat notificationDateFormatter = null;
	private SimpleDateFormat messageDateFormatter = null;
	private final AtomicBoolean isInitialized = new AtomicBoolean(false);

	private LocalPreferences pref = SettingsManager.getLocalPreferences();
	private final JComboBox periodChooser= new JComboBox() ;
	private final JPanel filterPanel = new JPanel();
	private final JPanel mainPanel = new BackgroundPanel();
	private final JPanel searchPanel = new BackgroundPanel();
	private final JPanel navigatorPanel = new JPanel();
	private final JPanel overTheTopPanel = new BackgroundPanel();
	private final JPanel controlPanel = new BackgroundPanel();
	private VCardPanel vacardPanel = null;
	private final JTextField searchField = new JTextField(25);
	private final JEditorPane window = new JEditorPane();
	private final JScrollPane pane = new JScrollPane(window);
	private final JFrame frame = new JFrame(Res.getString("title.history.for", jid));
	private final StringBuilder builder = new StringBuilder();
	private List<ChatTranscript> searchFilteredList = new ArrayList<>();
	private List<ChatTranscript> dateFilteredUnfilteredList = new ArrayList<>();
    private AtomicBoolean isHistoryLoaded = new AtomicBoolean(false);


	/**
	 * Open the Transcript with the given formatter.
	 * @param notificationDateFormatter the formatter for the notifications
	 * @param messageDateFormatter the formatter for dates
	 */
	public HistoryTranscript(SimpleDateFormat notificationDateFormatter, SimpleDateFormat messageDateFormatter) {
		this.notificationDateFormatter = notificationDateFormatter;
		this.messageDateFormatter = messageDateFormatter;
	}

	/**
	 * Show the History for the given Contact.
	 * @param jid the JID of the current transcript
	 */
	public void showHistory(String jid) {
		vacardPanel = new VCardPanel(jid);
		frame.setTitle(Res.getString("title.history.for", jid));
		this.jid = jid;
		this.start();
	}

	/**
	 * Check if the period was changed an apply the filter
	 * to the message history
	 * @param change the choosen value for the period
	 */
	private synchronized void handlePeriodChange(String change){
		try {
			token.acquire();
			if ( !Objects.equals( change, searchPeriod ) && isInitialized.get()){
				searchPeriod = change;
				pref.setSearchPeriod(searchPeriod);
				isHistoryLoaded.set(false);
				TaskEngine.getInstance().schedule(transcriptTask, 10);
			}
			token.release();
		} catch (InterruptedException e) {
			Log.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Navigate one page left through the history.
	 * Do nothing if at index 0.
	 */
	private synchronized void pageLeft() {
		try {
			AtomicBoolean changed = new AtomicBoolean();
			changed.set(false);
			token.acquire();
			if (pageIndex > 1){
				pageIndex--;
				changed.set(true);	
			}
			token.release();
			if (changed.get()) display();
		}catch(InterruptedException e){
			Log.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Navigate one page right through the history.
	 * Do nothing if at maximum index.
	 */
	private synchronized void pageRight() {
		try {
			AtomicBoolean changed = new AtomicBoolean();
			changed.set(false);
			token.acquire();
			if (pageIndex < maxPages){
				pageIndex++;
				changed.set(true);
			}
			token.release();
			if (changed.get()) display();
		} catch (InterruptedException e) {
			Log.error(e);
			e.printStackTrace();
		}
	}

	/**
     * Builds html string with the stored messages
     * @param notificationDateFormatter SimpleDateFormat for formating notifications
     * @param messageDateFormatter notificationDateFormatter SimpleDateFormat for formating messages
     * @return String containing the messages as html 
     */
    public final String buildString(List<HistoryMessage> messages){
    	StringBuilder builder = new StringBuilder();
    	final String personalNickname = SparkManager.getUserManager().getNickname();
		Date lastPost = null;
		String broadcastnick = null;
		boolean initialized = false;

		for (HistoryMessage message : messages) {
			String color = "blue";

			String from = message.getFrom();
			String nickname = SparkManager.getUserManager()
					.getUserNicknameFromJID(message.getFrom());
			String body = org.jivesoftware.spark.util.StringUtils
					.escapeHTMLTags(message.getBody());
			if (nickname.equals(message.getFrom())) {
				String otherJID = XmppStringUtils.parseBareJid(message
						.getFrom());
				String myJID = SparkManager.getSessionManager()
						.getBareAddress();

				if (otherJID.equals(myJID)) {
					nickname = personalNickname;
				} else {
					nickname = XmppStringUtils.parseLocalpart(nickname);
					broadcastnick = message.getFrom();
				}
			}

			if (!XmppStringUtils.parseBareJid(from).equals(
					SparkManager.getSessionManager().getBareAddress())) {
				color = "red";
			}

			long lastPostTime = lastPost != null ? lastPost.getTime() : 0;

			int diff;
			if (DateUtils.getDaysDiff(lastPostTime, message.getDate()
					.getTime()) != 0) {
				diff = DateUtils.getDaysDiff(lastPostTime, message
						.getDate().getTime());
			} else {
				diff = DateUtils.getDayOfWeek(lastPostTime)
						- DateUtils.getDayOfWeek(message.getDate()
								.getTime());
			}

			if (diff != 0) {
				if (initialized) {
					builder.append("<tr><td><br></td></tr>");
				}
				builder.append(
						"<tr><td colspan=2><font face=dialog size=3 color=black><b><u>")
						.append(notificationDateFormatter.format(message
								.getDate()))
						.append("</u></b></font></td></tr>");
				initialized = true;
			}

			String value = "(" + messageDateFormatter.format(message.getDate()) + ") ";

			builder.append("<tr valign=top><td colspan=2 nowrap>");
			builder.append("<font face=dialog size=3 color='").append(color).append("'>");
			builder.append(value);
			if (broadcastnick == null){
				builder.append(nickname + ": ");
			} else {
				builder.append(broadcastnick + ": ");
			}
			builder.append("</font>");
			builder.append("<font face=dialog size=3>");
			builder.append(body);
			builder.append("</font>");
			builder.append("</td></tr><br>");

			lastPost = message.getDate();
			broadcastnick = null;
		}
		builder.append("</table></body></html>");

		return builder.toString();
	}

    /**
     * If a new page is loaded or the search is 
     * changed, displays the current page again.
     */
	private synchronized void display() {
		try {
			token.acquire();

			if ((searchFilteredList.size() > 0) && (pageIndex <= searchFilteredList.size())) {
				builder.append(buildString(searchFilteredList.get(pageIndex-1).getMessages()));

			}else{
				// Handle no history
				builder.replace(0, builder.length(), "");
				builder.append("<b>")
						.append(Res.getString("message.no.history.found"))
						.append("</b>");
			}
			window.setText(builder.toString());
			builder.replace(0, builder.length(), "");
			if (window.getText().length() > 0) window.setCaretPosition(0);
			pageCounter.setText(pageIndex + " / " + maxPages);
			token.release();
		} catch (InterruptedException e) {
			Log.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * This class will run every time a search has startet on the messages, saved in the transcript
	 * or the time period has been changed
	 * @author tim.jentz
	 *
	 */
	private class timerTranscript extends TimerTask {

		/**
		 * This function check if the date of the new message is in the same period as the date
		 * of the old message.
		 * 
		 * At the moment, there are three different time periods:
		 * - one week
		 * - three weeks
		 * - one month
		 * @param newDate the date gained from new message
		 * @param oldDate the date gained from new message
		 * @return true if both dates in the same period, false if not
		 */
		private boolean dateInPeriod(Date newDate, Date oldDate){
			boolean result = false;
			Calendar cal = Calendar.getInstance();
			cal.setTime(newDate);
			long yearNew = Math.round((double)cal.get(Calendar.YEAR));
			long monthNew = Math.round((double)cal.get(Calendar.MONTH));	

			cal.setTime(oldDate);
			long yearOld = Math.round((double)cal.get(Calendar.YEAR));
			long monthOld = Math.round((double)cal.get(Calendar.MONTH));

			if (searchPeriod.equals(period_oneMonth)) { 
				// for one month, we only check if the month and the year is equal
				if ((monthOld == monthNew) && (yearOld == yearNew)) result = true;
			}else if (searchPeriod.equals(period_oneYear)) {
				// for one year, we only check if the year is the same
				if ((yearOld == yearNew)) result = true;
			}else if (searchPeriod.equals(period_noPeriod)) {
				// for unfiltered list, we return true all the time
				result = true;
			}
			return result;
		}

		/**
		 * Sort the messages by the choosen period of time
		 * @param transcript the transcript to sort
		 * @return List of transcript sorted by period. each transcript contains the messages of the giving period 
		 */
		private List<ChatTranscript> getDateSortedTranscript(ChatTranscript transcript){
			List<ChatTranscript> tmpList = new ArrayList<>();

			if (transcript.size() > 0){
				ChatTranscript sortedTranscript = new ChatTranscript();
				HistoryMessage msg;

				// retrieve the first message

				Date oldDate;
                Integer iteratorValue;
                Integer startValue;
                Integer endValue;

				boolean sortDateAsc = pref.getChatHistoryAscending();

				if ( sortDateAsc ){
				    oldDate = transcript.getMessage(0).getDate();
				    iteratorValue = 1;
				    startValue = 0;
				    endValue = transcript.size();
				}else{
				    oldDate = transcript.getMessage(transcript.size()-1).getDate();
                    iteratorValue = -1;
                    startValue = transcript.size()-1;
                    endValue = -1;
				}

				Calendar cal = Calendar.getInstance();
				cal.setTime(oldDate);
				Date newDate;

				ChatTranscript history = new ChatTranscript();
				boolean handled = true;

				for(int i = startValue; i != endValue; i += iteratorValue){
				    msg = transcript.getMessage(i);
                    // get the date of the current message
                    newDate = msg.getDate();
 
                    if (! dateInPeriod(newDate, oldDate)){
                        history = new ChatTranscript();
                        history.setList(sortedTranscript.getMessages());			    

                        // add the messages to the list
                        if (history.getMessages().size() > 0) tmpList.add(history);

                        // we have handled this entry

						oldDate = msg.getDate();
                        sortedTranscript = new ChatTranscript();
                    }
                    sortedTranscript.addHistoryMessage(msg);
                    // the latest entries not yet saved to the list
                    handled = false;     
				}

				// if the latest entries where not yet handled, do it now
				if (!handled) {
					history = new ChatTranscript();
					history.setList(sortedTranscript.getMessages());
					if (history.getMessages().size() > 0) tmpList.add(history);
				}
			}

			return tmpList;
		}

		@Override
		public void run() {
			if (! isHistoryLoaded.get()){
				// if we have to load the history
				dateFilteredUnfilteredList = getDateSortedTranscript((ChatTranscript) get());

				try {
					token.acquire();
					// confirm that the history is loaded
					isHistoryLoaded.set(true);
					token.release();
				} catch (InterruptedException e) {
					Log.error(e);
					e.printStackTrace();
				}
			}


			String searchString = searchField.getText();
			// if we searching for a string or not
			if (Res.getString("message.search.for.history").equals(
					searchField.getText())
					|| searchField.getText().equals(""))
				searchString = null;

			List<ChatTranscript> tmpList = new ArrayList<>();
			ChatTranscript tmpTranscript;

			for (int i = 0; i < dateFilteredUnfilteredList.size(); i++){
				tmpTranscript = new ChatTranscript();
				tmpTranscript.setList(dateFilteredUnfilteredList.get(i).getMessage(searchString));
				if (tmpTranscript.size() > 0) tmpList.add(tmpTranscript);
			}

			try {
				token.acquire();
				searchFilteredList = tmpList;
				pageIndex = (searchFilteredList.size() > 0) ? 1 : 0;
				maxPages = searchFilteredList.size();
				token.release();
			} catch (InterruptedException e) {
				Log.error(e);
				e.printStackTrace();
			}
			display();	
		}	
	}

	/**
	 * Check if the given String represents a valid period
	 * @param p the period, that have to be checked
	 * @return true if valid, false if invalid
	 */
	private int getPeriodIndex(String p){
		int result = 0;
		for (int i = 0; i < periods.size(); i++){
			if (p.equals(periods.get(i))) {
				result = i;
				break;
			}
		}
		return result;
	}

	/**
	 * Set the layout settings
	 */
	public void finished() {
		pageLeft.addActionListener( arg0 -> pageLeft() );
		pageRight.addActionListener( arg0 -> pageRight() );
	    periodChooser.addActionListener( e -> handlePeriodChange (periods.get(periodChooser.getSelectedIndex())) );

		// add search text input
		searchPanel.setLayout(new GridBagLayout());
		navigatorPanel.setLayout(new GridBagLayout());
		controlPanel.setLayout(new BorderLayout());
		filterPanel.setLayout(new GridBagLayout());
		mainPanel.setLayout(new BorderLayout());

		// the list of periods
//		periods.add(period_oneWeek);
//		periods.add(period_threeWeeks);
		periods.add(period_oneMonth);
		periods.add(period_oneYear);
		periods.add(period_noPeriod);

		// get the default preferences for the search period 
		int index = getPeriodIndex(pref.getSearchPeriod(periods.get(0)));

		for (String period : periods){
			periodChooser.addItem(Res.getString(period));

		}

		periodChooser.setToolTipText(Res.getString("message.search.page.timeperiod"));
		pageCounter.setToolTipText(Res.getString("message.search.page.counter"));
		pageRight.setToolTipText(Res.getString("message.search.page.right"));
		pageLeft.setToolTipText(Res.getString("message.search.page.left"));
		searchField.setText(Res.getString("message.search.for.history"));
		searchField.setToolTipText(Res.getString("message.search.for.history"));
		searchField.setForeground((Color) UIManager
				.get("TextField.lightforeground"));

		searchPanel.add(vacardPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(1, 5, 1, 1), 0, 0));

		filterPanel.add(periodChooser,new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 5, 1, 1), 0, 0));

		filterPanel.add(searchField, new GridBagConstraints(2, 0,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(1, 1, 6, 1), 0, 0));

		navigatorPanel.add(pageLeft, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 5, 1, 1), 0, 0));
		navigatorPanel.add(pageCounter, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(1, 5, 1, 1), 0, 0));
		navigatorPanel.add(pageRight, new GridBagConstraints(2, 0,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(1, 1, 6, 1), 0, 0));

		controlPanel.add(filterPanel, BorderLayout.NORTH);
		controlPanel.add(navigatorPanel, BorderLayout.SOUTH);

		overTheTopPanel.setLayout(new BorderLayout());
		overTheTopPanel.add(searchPanel,BorderLayout.NORTH);
		overTheTopPanel.add(controlPanel,BorderLayout.SOUTH);

		mainPanel.add(overTheTopPanel, BorderLayout.NORTH);

		window.setEditorKit(new HTMLEditorKit());
		window.setBackground(Color.white);
		pane.getVerticalScrollBar().setBlockIncrement(200);
		pane.getVerticalScrollBar().setUnitIncrement(20);

		mainPanel.add(pane, BorderLayout.CENTER);

		frame.setIconImage(SparkRes.getImageIcon(SparkRes.HISTORY_16x16)
				.getImage());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		frame.pack();
		frame.setSize(600, 400);
		window.setCaretPosition(0);
		window.requestFocus();
		GraphicUtils.centerWindowOnScreen(frame);
		frame.setVisible(true);
		window.setEditable(false);

		builder.append("<html><body><table cellpadding=0 cellspacing=0>");

		searchField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					TaskEngine.getInstance().schedule(transcriptTask, 10);
					searchField.requestFocus();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		searchField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				searchField.setText("");
				searchField.setForeground((Color) UIManager
						.get("TextField.foreground"));
			}

			public void focusLost(FocusEvent e) {
				searchField.setForeground((Color) UIManager
						.get("TextField.lightforeground"));
				searchField.setText(Res.getString("message.search.for.history"));
			}
		});

		// after initializing the period, we can load the history
		isInitialized.set(true);
		periodChooser.setSelectedIndex(index);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				window.setText("");
			}

			@Override
			public void windowClosed(WindowEvent e) {
				frame.removeWindowListener(this);
				frame.dispose();
				transcriptTask.cancel();
				searchPanel.remove(vacardPanel);
			}
		});
	}

	@Override
	public Object construct() {
		String bareJID = XmppStringUtils.parseBareJid(jid);
		return ChatTranscripts.getChatTranscript(bareJID);
	}

}
