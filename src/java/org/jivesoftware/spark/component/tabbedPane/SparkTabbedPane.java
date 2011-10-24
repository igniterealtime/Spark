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
package org.jivesoftware.spark.component.tabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

public class SparkTabbedPane extends JPanel {
	private static final long serialVersionUID = -9007068462231539973L;
	private static final String NAME = "SparkTabbedPane";
	private List<SparkTabbedPaneListener> listeners = new ArrayList<SparkTabbedPaneListener>();
	private JTabbedPane pane = null;
	private Icon closeInactiveButtonIcon;
	private Icon closeActiveButtonIcon;
	private boolean closeEnabled = false;
	private int dragTabIndex = -1;

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

	public SparkTabbedPane(final Integer type) {
		this(type.intValue());
	}
	public SparkTabbedPane(final int type) {

		pane = buildTabbedPane(type);
		pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

		setLayout(new BorderLayout());
		add(pane);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if (index >= 0) {
					fireTabSelected(getTabAt(index), getTabAt(index).getComponent(), index);
				}
			}
		};
		pane.addChangeListener(changeListener);

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

	/*
	 * Updates the tab colors for unread,active and inactive tabs
	 */
	public void updateActiveTab()
	{
	for (int i = 0; i < pane.getTabCount(); ++i) {
	    Component com = pane.getTabComponentAt(i);
	    TabPanel panel = (TabPanel) com;
	    Font oldFont = panel.getFont();
	    try {
		if (SparkManager.getChatManager().getChatContainer().getChatRoom(i).getUnreadMessageCount() == 0)
		{
		    if (i == getSelectedIndex()) {
			panel.setTitleFont(new Font(oldFont.getFontName(), Font.BOLD,
				oldFont.getSize()));
			panel.setTitleColor((Color) UIManager.get("Chat.activeTabColor"));
		    } else {
			panel.setTitleFont(new Font(oldFont.getFontName(), Font.PLAIN,
				oldFont.getSize()));
			panel.setTitleColor((Color) UIManager.get("Chat.inactiveTabColor"));
		    }
		}

	    } catch (ChatRoomNotFoundException e) {
		//Do nothing
	    }

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
			return tab.getComponent();
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

	protected void fireTabAdded(SparkTab tab, Component component, int index) {
		final Iterator<SparkTabbedPaneListener> list = ModelUtil.reverseListIterator(listeners
				.listIterator());
		while (list.hasNext()) {
			(list.next()).tabAdded(tab, component,
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

	protected void fireTabRemoved(SparkTab tab, Component component, int index) {
		final Iterator<SparkTabbedPaneListener> list = ModelUtil.reverseListIterator(listeners
				.listIterator());
		while (list.hasNext()) {
			(list.next()).tabRemoved(tab, component,
					index);
		}
	}

	protected void fireTabSelected(SparkTab tab, Component component, int index) {
		final Iterator<SparkTabbedPaneListener> list = ModelUtil.reverseListIterator(listeners
				.listIterator());
		while (list.hasNext()) {
			(list.next()).tabSelected(tab, component,
					index);
		}
	}

	protected void allTabsClosed() {
		final Iterator<SparkTabbedPaneListener> list = ModelUtil.reverseListIterator(listeners
				.listIterator());
		while (list.hasNext()) {
			list.next().allTabsRemoved();
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
	private final BorderLayout layout = new BorderLayout(5, 5);
	private final Font defaultFontPlain = new Font("Dialog", Font.PLAIN, 11);
	private final Font defaultFontBold = new Font("Dialog", Font.BOLD, 11);
	private JLabel iconLabel;
	private JLabel titleLabel;
	private JLabel tabCloseButton = new JLabel(closeInactiveButtonIcon);

	public TabPanel(final SparkTab sparktab, String title, Icon icon) {
	    setOpaque(false);
	    this.setLayout(layout);
	    titleLabel = new JLabel(title);

	    titleLabel.setFont(closeEnabled ? defaultFontBold
		    : defaultFontPlain);
	    if (icon != null) {
		iconLabel = new JLabel(icon);
		add(iconLabel, BorderLayout.WEST);
	    }

	    add(titleLabel, BorderLayout.CENTER);
	    if (closeEnabled) {
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

	@Override
	public Dimension getPreferredSize() {
	    Dimension dim = super.getPreferredSize();

	    if (closeEnabled && titleLabel.getText() != null && titleLabel.getText().length() < 6
		    && dim.getWidth() < 80) {
		return new Dimension(80, dim.height);

	    } else {
		return dim;
	    }

	}

	public Font getDefaultFont() {
	    return defaultFontPlain;
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
		newFont = new Font(oldFont.getFontName(), Font.BOLD,
			oldFont.getSize());
	    } else {
		newFont = new Font(oldFont.getFontName(), Font.PLAIN,
			oldFont.getSize());
	    }

	    titleLabel.setFont(newFont);
	    titleLabel.validate();
	    titleLabel.repaint();
	    titleLabel.revalidate();
	}

	public void setTitleFont(Font font) {
	    titleLabel.setFont(font);
	    titleLabel.validate();
	    titleLabel.repaint();
	    titleLabel.revalidate();
	}

    }

	/**
	 * Drag and Drop
	 */
	public void enableDragAndDrop() {
		final DragSourceListener dsl = new DragSourceListener() {

			@Override
			public void dragDropEnd(DragSourceDropEvent event) {
				dragTabIndex = -1;
			}

			@Override
			public void dragEnter(DragSourceDragEvent event) {
				event.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
			}

			@Override
			public void dragExit(DragSourceEvent event) {}

			@Override
			public void dragOver(DragSourceDragEvent event) {}

			@Override
			public void dropActionChanged(DragSourceDragEvent event) {}

		};

	    final Transferable t = new Transferable() {
	    	private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
			@Override
			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException {
				return pane;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				DataFlavor[] f = new DataFlavor[1];
				f[0] = this.FLAVOR;
				return f;
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor.getHumanPresentableName().equals(NAME);
			}

	    };

	    final DragGestureListener dgl = new DragGestureListener() {

			@Override
			public void dragGestureRecognized(DragGestureEvent event) {
                dragTabIndex = pane.indexAtLocation(event.getDragOrigin().x, event.getDragOrigin().y);
                try {
                    event.startDrag(DragSource.DefaultMoveDrop, t, dsl);
                } catch(Exception idoe) {
                	Log.error(idoe);
                }
			}

	    };

	    final DropTargetListener dtl = new DropTargetListener() {

			@Override
			public void dragEnter(DropTargetDragEvent event) {}

			@Override
			public void dragExit(DropTargetEvent event) {}

			@Override
			public void dragOver(DropTargetDragEvent event) {}

			@Override
			public void drop(DropTargetDropEvent event) {
				int dropTabIndex = getTargetTabIndex(event.getLocation());
	            moveTab(dragTabIndex,dropTabIndex);
			}

			@Override
			public void dropActionChanged(DropTargetDragEvent event) {}

	    };

	    new DropTarget(pane, DnDConstants.ACTION_COPY_OR_MOVE, dtl, true);
	    new DragSource().createDefaultDragGestureRecognizer(pane, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
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

	private int getTargetTabIndex(Point point) {
		Point tabPt = SwingUtilities.convertPoint(pane, point, pane);
		boolean isTB = pane.getTabPlacement()==JTabbedPane.TOP || pane.getTabPlacement()==JTabbedPane.BOTTOM;
		for(int i=0;i < getTabCount();i++) {
			Rectangle r = pane.getBoundsAt(i);
			if(isTB) r.setRect(r.x-r.width/2, r.y,  r.width, r.height);
			else   r.setRect(r.x, r.y-r.height/2, r.width, r.height);
				if(r.contains(tabPt)) return i;
		}
		Rectangle r = pane.getBoundsAt(getTabCount()-1);
		if(isTB) r.setRect(r.x+r.width/2, r.y,  r.width, r.height);
		else   r.setRect(r.x, r.y+r.height/2, r.width, r.height);
		return   r.contains(tabPt)?getTabCount():-1;
	}

	protected JTabbedPane buildTabbedPane(final int type) {
		return new JTabbedPane(type);
	}

	protected JTabbedPane getTabbedPane() {
	    return pane;
	}

}
