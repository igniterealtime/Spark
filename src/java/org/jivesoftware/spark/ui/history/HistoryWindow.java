package org.jivesoftware.spark.ui.history;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Vyacheslav Durin (nixspirit@gmail.com)
 * 
 *         Apr 10, 2013
 * @version 0.1
 */
public class HistoryWindow extends JFrame {

	private static final long serialVersionUID = 2704122897798171413L;

	private static final String CONTENT_TYPE = "text/html";
	private static final String EMPTY = "";
	private static final String LABEL_SIZE = "Size:";
	private static final String BTN_FIND = "Find";
	private static final String BTN_CLOSE = "Close";
	private static final Dimension SIZE = new Dimension(700, 400);
	private static final Point LOCATION = new Point(400, 150);
	private static final MessageFormat TITLE_FORMAT = new MessageFormat("{0}");
	private static final MessageFormat LABEL_FORMAT = new MessageFormat("{0}");
	private static final MessageFormat HISTORY_FILE_FORMAT = new MessageFormat(
			"transcripts/{0}.xml");
	private static final Font LABEL_FONT = new Font("Droid Sans", Font.BOLD, 16);
	private static final Font SIZE_TEXT_FONT = new Font("Droid Sans",
			Font.PLAIN, 14);
	private static final Font TEXT_FONT = new Font("Droid Sans", Font.PLAIN, 13);
	private static final Dimension SIZE_FIND_FIELD = new Dimension(100, 25);

	private String roomName;
	private InputStream historyStream;
	private JButton btnClose;
	private JTextPane historyContentText;
	private JScrollPane historyContentTextScrollPane;
	private JTextField findTextField;
	private JButton btnFind;
	private JTree historyTree;
	private JScrollPane historyTreeView;
	private String sizeText;
	private XMLHistoryFile historyFile;
	private TreeModel historyOriginalModel;

	public HistoryWindow(InputStream is, String roomName) {
		setPreferredSize(SIZE);
		setLocation(LOCATION);
		setResizable(true);
		setTitle(TITLE_FORMAT.format(new String[] { roomName }));
		this.roomName = roomName;
		this.historyStream = is;
		initComponents();
	}

	public HistoryWindow(File file, String roomName)
			throws FileNotFoundException {
		this(new FileInputStream(new File(file,
				HISTORY_FILE_FORMAT.format(new String[] { roomName }))),
				roomName);
	}

	public void showWindow() {
		pack();
		setVisible(true);
	}

	public void hideWindow() {
		setVisible(false);
		dispose();
	}

	private void initComponents() {
		historyFile = new XMLHistoryFile(historyStream);
		sizeText = historyFile.getFormatSize();

		btnClose = createJButton(BTN_CLOSE);
		btnClose.setFont(TEXT_FONT);

		btnFind = createJButton(BTN_FIND);
		btnFind.setFont(TEXT_FONT);

		HistoryTreeNode historyTreeTopNode = buildHistoryTree( historyFile, roomName );
		historyTree = createJTree( historyTreeTopNode );
		historyOriginalModel = historyTree.getModel();
		historyTree.setFont(TEXT_FONT);
		historyTreeView = new JScrollPane(historyTree);
		historyTreeView.setMinimumSize(new Dimension(200, 200));

		findTextField = createJTextField();
		findTextField.setPreferredSize(SIZE_FIND_FIELD);
		findTextField.setMinimumSize(SIZE_FIND_FIELD);

		historyContentText = createJTextPane();
		historyContentText.setContentType(CONTENT_TYPE);
		historyContentText.setBackground(Color.WHITE);
		historyContentText.setFont(TEXT_FONT);
		historyContentTextScrollPane = new JScrollPane(historyContentText);

		addListeners();
		initLayout();

		selectVeryFirstLeaf();
	}

	private void addListeners() {
		historyTree.addTreeSelectionListener(onTreeSelected());
		btnClose.addActionListener(onCloseBtnClick());
		btnFind.addActionListener(onFindBtnClick());
	}

	private void initLayout() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(13, 13, 0, 13);
		add(getLabel(), c);

		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(getHistoryContentPanel(), c);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 0;
		c.insets = new Insets(5, 13, 0, 13);

		add(getSizePanel(), c);

		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 13, 5, 10);
		add(getButtonsPanel(), c);
	}

	private Component getSizePanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = createJLabel(LABEL_SIZE);
		label.setFont(LABEL_FONT);
		panel.add(label);

		JLabel sizeTextLabel = createJLabel(EMPTY);
		sizeTextLabel.setFont(SIZE_TEXT_FONT);
		sizeTextLabel.setText(sizeText);
		panel.add(sizeTextLabel);
		return panel;
	}

	private Component getButtonsPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(btnClose);
		return panel;
	}

	private Component getHistoryContentPanel() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(historyTreeView);
		splitPane.setRightComponent(getMessagesPanel());
		return splitPane;
	}

	private Component getMessagesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		panel.add(historyContentTextScrollPane, c);

		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0.02;
		c.insets = new Insets(0, 0, 0, 3);
		panel.add(findTextField, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0.1;
		c.insets = new Insets(0, 0, 0, 0);
		panel.add(btnFind, c);
		return panel;
	}

	private JLabel getLabel() {
		String formatRoomName = LABEL_FORMAT.format(new String[] { roomName });
		JLabel label = createJLabel(formatRoomName);
		label.setFont(LABEL_FONT);
		return label;
	}

	private static HistoryTreeNode buildHistoryTree(XMLHistoryFile file,
			String roomName) {
		HistoryTreeNode top = new HistoryTreeNode(roomName);

		HistoryTreeNode day;
		HistoryTreeNode item;

		for (HistoryEntry entry : file.getHistoryEntries()) {
			day = new HistoryTreeNode(entry.getName());

			if (entry.hasRecords()) {

				for (HistoryEntry child : entry.getEntries()) {
					item = new HistoryTreeNode(child.getName());
					item.setHistoryEntry(child);
					day.add(item);
				}
			}
			top.add(day);
		}
		return top;
	}

	private ActionListener onCloseBtnClick() {
		return e -> hideWindow();
	}

	private ActionListener onFindBtnClick() {
		return e -> {

            String searchText = findTextField.getText().toString().trim();
            if (EMPTY.equals(searchText)) {
                historyTree.setModel(historyOriginalModel);
                selectVeryFirstLeaf();
                return;
            }

            List<HistoryEntry> results = historyFile.search(findTextField
                    .getText().toString());

            HistoryTreeNode top = new HistoryTreeNode(searchText);

            for (HistoryEntry entry : results) {
                top.add(new HistoryTreeNode(entry, entry.getName()));
            }

            historyTree.setModel(new DefaultTreeModel(top));
            selectVeryFirstLeaf();
        };
	}

	private TreeSelectionListener onTreeSelected() {
		return e -> {

            TreePath tp = e.getNewLeadSelectionPath();
            if (tp == null)
                return;

            Object node = tp.getLastPathComponent();
            if (!(node instanceof HistoryTreeNode))
                return;

            HistoryTreeNode entry = (HistoryTreeNode) node;
            HistoryEntry historyEntry = entry.getHistoryEntry();

            String historyText = (historyEntry == null || historyEntry
                    .isEmpty()) ? EMPTY : historyEntry.getHistory();

            historyContentText.setText(historyText);
        };
	}

	private void selectVeryFirstLeaf() {
		if (historyTree == null || historyTree.getRowCount() < 1)
			return;

		historyTree.expandRow(1);
		DefaultMutableTreeNode firstLeaf = ((DefaultMutableTreeNode) historyTree
				.getModel().getRoot()).getFirstLeaf();
		TreePath path = new TreePath(firstLeaf.getPath());
		historyTree.setSelectionPath(path);
	}

	// ############ HELPERS ##########

	private static JButton createJButton(String title) {
		JButton btn = new JButton();
		btn.setText(title);
		return btn;
	}

	private static JTextField createJTextField() {
		JTextField field = new JTextField();
		return field;
	}

	private static JTree createJTree(DefaultMutableTreeNode top) {
		JTree tree = new JTree(top);
		return tree;
	}

	private static JTextPane createJTextPane() {
		JTextPane pane = new JTextPane();
		return pane;
	}

	private static JLabel createJLabel(String title) {
		JLabel label = new JLabel(title);
		return label;
	}
}
