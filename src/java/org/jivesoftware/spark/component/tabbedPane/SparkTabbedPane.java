package org.jivesoftware.spark.component.tabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

public class SparkTabbedPane extends JPanel {
	private static final long serialVersionUID = -9007068462231539973L;
	private List<SparkTabbedPaneListener> listeners = new ArrayList<SparkTabbedPaneListener>();
	private JTabbedPane pane = null;
	private Icon closeInactiveButtonIcon;
	private Icon closeActiveButtonIcon;
	private boolean closeEnabled = false;
	private int draggedTabIndex;
	private boolean drag = false;

	/**
	 * The default Hand cursor.
	 */
	public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	/**
	 * The default Text Cursor.
	 */
	public static final Cursor DEFAULT_CURSOR = new Cursor(
			Cursor.DEFAULT_CURSOR);

	public SparkTabbedPane() {
		this(JTabbedPane.TOP);
	}
	
	public SparkTabbedPane(int type) {
		pane = new JTabbedPane(type);
		setLayout(new BorderLayout());
		add(pane);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if (index > 0) {
					fireTabSelected(getTabAt(index), getTabAt(index).getComponent(), index);
				}
			}
		};
		pane.addChangeListener(changeListener);
//		pane.addMouseMotionListener(new MouseMotionListener()
//		{
//
//			@Override
//			public void mouseDragged(MouseEvent e) {
//				if (!drag)
//				{
//					int newIndex = pane.getUI().tabForCoordinate(pane, e.getX(), e.getY());
//	
//					if (newIndex != -1 && newIndex != draggedTabIndex)
//					{
//						setCursor(DragSource.DefaultMoveDrop);
//						draggedTabIndex = newIndex;
//						drag = true;
//						System.out.println("DRAG" + draggedTabIndex);
//					}
//				}
//			}
//
//			@Override
//			public void mouseMoved(MouseEvent arg0) {}
//			
//		});
//		
//		pane.addMouseListener(new MouseListener()
//		{
//
//			@Override
//			public void mouseClicked(MouseEvent e) {}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {}
//
//			@Override
//			public void mouseExited(MouseEvent e) {}
//
//			@Override
//			public void mousePressed(MouseEvent e) {}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				if (drag)
//				{
//					int newIndex = pane.getUI().tabForCoordinate(pane, e.getX(), e.getY());
//					if (newIndex > 0 && draggedTabIndex > 0)
//					{
//						moveTab(newIndex,draggedTabIndex);
//					}
//					drag = false;
//				}
//			}
//			
//		});
		
		// Initialize close button
		closeInactiveButtonIcon = SparkRes.getImageIcon(SparkRes.CLOSE_WHITE_X_IMAGE);
		closeActiveButtonIcon = SparkRes.getImageIcon(SparkRes.CLOSE_DARK_X_IMAGE);
	}

	public SparkTab getTabContainingComponent(Component component) {
		for (Component comp : pane.getComponents()) {
			if (comp instanceof SparkTab) {
				SparkTab tab = (SparkTab) comp;
				if (tab.getComponent() == component)
					return tab;
			}
		}
		return null;
	}
	
	public SparkTab addTab(String title, Icon icon, final Component component) {
		return addTab(title, icon, component, null);
	}

	public SparkTab addTab(String title, Icon icon, final Component component,
			String tip) {
		final SparkTab sparktab = new SparkTab(this, component);

		TabPanel tabpanel = new TabPanel(sparktab, title, icon);

		pane.addTab(null, null, sparktab, tip);
		pane.setTabComponentAt(pane.getTabCount() - 1, tabpanel);
		fireTabAdded(sparktab, component, getTabPosition(sparktab));

		return sparktab;
	}

	public SparkTab getTabAt(int index) {
		return ((SparkTab) pane.getComponentAt(index));
	}

	public int getTabPosition(SparkTab tab) {
		return pane.indexOfComponent(tab);
	}

	public Component getComponentInTab(SparkTab tab) {
		return tab.getComponent();
	}

	public void setIconAt(int index, Icon icon) {
		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel) {
			TabPanel panel = (TabPanel) com;
			panel.setIcon(icon);
		}
	}

	public void setTitleAt(int index, String title) {
		if (index > 0) {
			Component com = pane.getTabComponentAt(index);
			if (com instanceof TabPanel) {
				TabPanel panel = (TabPanel) com;
				panel.setTitle(title);
			}
		}
	}

	public void setTitleColorAt(int index, Color color) {

		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel) {
			TabPanel panel = (TabPanel) com;
			panel.setTitleColor(color);
		}
	}

	public void setTitleBoldAt(int index, boolean bold) {
		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel) {
			TabPanel panel = (TabPanel) com;
			panel.setTitleBold(bold);
		}
	}

	public void setTitleFontAt(int index, Font font) {
		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel) {
			TabPanel panel = (TabPanel) com;
			panel.setTitleFont(font);
		}
	}

	public Font getDefaultFontAt(int index) {
		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel) {
			TabPanel panel = (TabPanel) com;
			return panel.getDefaultFont();
		}
		return null;
	}

	public String getTitleAt(int index) {
		return pane.getTitleAt(index);
	}

	public int getTabCount() {
		return pane.getTabCount();
	}

	public void setSelectedIndex(int index) {
		pane.setSelectedIndex(index);
	}

	public int indexOfComponent(Component component) {
		for (Component comp : pane.getComponents()) {
			if (comp instanceof SparkTab) {
				SparkTab tab = (SparkTab) comp;
				if (tab.getComponent() == component)
					return pane.indexOfComponent(tab);
			}
		}
		return -1;
	}

	public Component getComponentAt(int index) {
		return ((SparkTab) pane.getComponentAt(index)).getComponent();
	}

	public Component getTabComponentAt(int index) {
		return pane.getTabComponentAt(index);
	}

	public Component getTabComponentAt(SparkTab tab) {
		return pane.getTabComponentAt(indexOfComponent(tab));
	}

	public Component getSelectedComponent() {
		if (pane.getSelectedComponent() instanceof SparkTab) {
			SparkTab tab = (SparkTab) pane.getSelectedComponent();
			tab.getComponent();
		}
		return null;
	}

	public void removeTabAt(int index) {
		pane.remove(index);
	}

	public int getSelectedIndex() {
		return pane.getSelectedIndex();
	}

	public void setCloseButtonEnabled(boolean enable) {
		closeEnabled = enable;
	}

	public void addSparkTabbedPaneListener(SparkTabbedPaneListener listener) {
		listeners.add(listener);
	}

	public void removeSparkTabbedPaneListener(SparkTabbedPaneListener listener) {
		listeners.remove(listener);
	}

	public void fireTabAdded(SparkTab tab, Component component, int index) {
		final Iterator list = ModelUtil.reverseListIterator(listeners
				.listIterator());
		while (list.hasNext()) {
			((SparkTabbedPaneListener) list.next()).tabAdded(tab, component,
					index);
		}
	}
	
	public JPanel getMainPanel() {
		return this;
	}
	  
    public void removeComponent(Component comp) {
        int index = indexOfComponent(comp);
        if (index != -1) {
            removeTabAt(index);
        }
    }

	public void fireTabRemoved(SparkTab tab, Component component, int index) {
		final Iterator list = ModelUtil.reverseListIterator(listeners
				.listIterator());
		while (list.hasNext()) {
			((SparkTabbedPaneListener) list.next()).tabRemoved(tab, component,
					index);
		}
	}

	public void fireTabSelected(SparkTab tab, Component component, int index) {
		final Iterator list = ModelUtil.reverseListIterator(listeners
				.listIterator());
		while (list.hasNext()) {
			((SparkTabbedPaneListener) list.next()).tabSelected(tab, component,
					index);
		}
	}

	public void allTabsClosed() {
		final Iterator list = ModelUtil.reverseListIterator(listeners
				.listIterator());
		while (list.hasNext()) {
			((SparkTabbedPaneListener) list.next()).allTabsRemoved();
		}
	}

	public void close(SparkTab sparktab) {
		int closeTabNumber = pane.indexOfComponent(sparktab);
		pane.removeTabAt(closeTabNumber);
		fireTabRemoved(sparktab, sparktab.getComponent(), closeTabNumber);

		if (pane.getTabCount() == 0) {
			allTabsClosed();
		}
	}

	private class TabPanel extends JPanel {
		private static final long serialVersionUID = -8249981130816404360L;
		private Font defaultFont = new Font("Dialog", Font.PLAIN, 11);
		private JLabel iconLabel;
		private JLabel titleLabel;

		public TabPanel(final SparkTab sparktab, String title, Icon icon) {

			setOpaque(false);
			titleLabel = new JLabel(title);
			titleLabel.setFont(defaultFont);
			iconLabel = new JLabel(icon);
			add(iconLabel, BorderLayout.WEST);
			add(titleLabel, BorderLayout.CENTER);
			if (closeEnabled) {
				final JLabel tabCloseButton = new JLabel(
						closeInactiveButtonIcon);
				tabCloseButton.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent mouseEvent) {
						if (Spark.isWindows()) {
							tabCloseButton.setIcon(closeActiveButtonIcon);
						}
						setCursor(HAND_CURSOR);
					}

					public void mouseExited(MouseEvent mouseEvent) {
						if (Spark.isWindows()) {
							tabCloseButton.setIcon(closeInactiveButtonIcon);
						}
						setCursor(DEFAULT_CURSOR);
					}

					public void mousePressed(MouseEvent mouseEvent) {
						final SwingWorker closeTimerThread = new SwingWorker() {
							public Object construct() {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									Log.error(e);
								}
								return true;
							}

							public void finished() {
								close(sparktab);
							}
						};
						closeTimerThread.start();
					}
				});
				add(tabCloseButton, BorderLayout.EAST);
			}
		}

		public Font getDefaultFont() {
			return defaultFont;
		}

		public void setIcon(Icon icon) {
			iconLabel.setIcon(icon);
		}

		public void setTitle(String title) {
			titleLabel.setText(title);
		}

		public void setTitleColor(Color color) {
			titleLabel.setForeground(color);
			titleLabel.validate();
			titleLabel.repaint();
		}

		public void setTitleBold(boolean bold) {
			Font oldFont = titleLabel.getFont();
			Font newFont;
			if (bold) {
				newFont = new Font(oldFont.getFontName(), Font.BOLD, oldFont
						.getSize());
			} else {
				newFont = new Font(oldFont.getFontName(), Font.PLAIN, oldFont
						.getSize());
			}

			titleLabel.setFont(newFont);
			titleLabel.validate();
			titleLabel.repaint();
		}

		public void setTitleFont(Font font) {
			titleLabel.setFont(font);
			titleLabel.validate();
			titleLabel.repaint();
		}

	}

	private void moveTab(int prev, int next) {
		if (next < 0 || prev == next) {
			return;
		}
		Component cmp = pane.getComponentAt(prev);
		Component tab = pane.getTabComponentAt(prev);
		String str = pane.getTitleAt(prev);
		Icon icon = pane.getIconAt(prev);
		String tip = pane.getToolTipTextAt(prev);
		boolean flg = pane.isEnabledAt(prev);
		int tgtindex = prev > next ? next : next - 1;
		pane.remove(prev);
		pane.insertTab(str, icon, cmp, tip, tgtindex);
		pane.setEnabledAt(tgtindex, flg);

		if (flg)
			pane.setSelectedIndex(tgtindex);


		pane.setTabComponentAt(tgtindex, tab);
	}

}
