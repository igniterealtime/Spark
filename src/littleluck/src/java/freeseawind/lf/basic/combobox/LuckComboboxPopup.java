package freeseawind.lf.basic.combobox;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboPopup;

import freeseawind.lf.utils.LuckUtils;
import freeseawind.swing.LuckList;
import freeseawind.swing.LuckScrollPane;

/**
 * <p>
 * Combobox弹出框，使用{@link LuckScrollPane}和{@link LuckList}替换原有实现，使用不透明点九图作为默认边框。
 * </p>
 * <p>
 * 另请参见 {@link LuckList}, {@link LuckScrollPane}, {@link LuckComboBoxUIBundle}
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckComboboxPopup extends BasicComboPopup
{
    private static final long serialVersionUID = -5046103803275794934L;

    public LuckComboboxPopup(JComboBox<?> combo)
    {
        super(combo);
    }

    public void show()
    {
        comboBox.firePopupMenuWillBecomeVisible();

        setListSelection(comboBox.getSelectedIndex());

        Point location = getPopupLocation();

        Point offset = (Point) UIManager.get(LuckComboBoxUIBundle.POPUPLOCATION);

        show(comboBox, location.x + offset.x, location.y + offset.y);
    }

    @Override
    protected JScrollPane createScroller()
    {
        // 滚动条悬浮在内容面板上的滚动面板
        JScrollPane sp = new LuckScrollPane(list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        sp.setHorizontalScrollBar(null);

        return sp;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected JList createList()
    {
        // 使用完全不透明List覆盖原有实现， 只改变容器类，不改变其它实现
        // 解决字体渲染问题
        return new LuckList(comboBox.getModel())
        {
            private static final long serialVersionUID = 2418863980554774059L;

            public void processMouseEvent(MouseEvent e)
            {
                if (LuckUtils.isMenuShortcutKeyDown(e))
                {
                    // Fix for 4234053. Filter out the Control Key from the
                    // list.
                    // ie., don't allow CTRL key deselection.
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    e = new MouseEvent((Component) e.getSource(), e.getID(),
                            e.getWhen(),
                            e.getModifiers() ^ toolkit.getMenuShortcutKeyMask(),
                            e.getX(), e.getY(), e.getXOnScreen(),
                            e.getYOnScreen(), e.getClickCount(),
                            e.isPopupTrigger(), MouseEvent.NOBUTTON);
                }

                super.processMouseEvent(e);
            }
        };
    }

    protected void configurePopup()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorderPainted(true);

        // 使用自定义边框
        setBorder(UIManager.getBorder(LuckComboBoxUIBundle.POPUPBORDER));
        setOpaque(false);
        add(scroller);
        setDoubleBuffered(true);
        setFocusable(false);
    }

    private void setListSelection(int selectedIndex)
    {
        if (selectedIndex == -1)
        {
            list.clearSelection();
        }
        else
        {
            list.setSelectedIndex(selectedIndex);
            list.ensureIndexIsVisible(selectedIndex);
        }
    }

    private Point getPopupLocation()
    {
        Dimension popupSize = comboBox.getSize();

        Insets insets = getInsets();

        // reduce the width of the scrollpane by the insets so that the popup
        // is the same width as the combo box.
        popupSize.setSize(popupSize.width - (insets.right + insets.left),
                getPopupHeightForRowCount(comboBox.getMaximumRowCount()));

        Rectangle popupBounds = computePopupBounds(0,
                comboBox.getBounds().height, popupSize.width, popupSize.height);

        Dimension scrollSize = popupBounds.getSize();

        Point popupLocation = popupBounds.getLocation();

        scroller.setMaximumSize(scrollSize);

        scroller.setPreferredSize(scrollSize);

        scroller.setMinimumSize(scrollSize);

        list.revalidate();

        return popupLocation;
    }
}
