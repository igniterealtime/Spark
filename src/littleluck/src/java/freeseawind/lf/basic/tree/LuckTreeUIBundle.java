package freeseawind.lf.basic.tree;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * <p>TreeUI 资源绑定类。</p>
 *
 * <p>A TreeUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckTreeUIBundle extends LuckResourceBundle
{
    /**
     * <p>TreeUI展开子菜单时父节点图标属性key。</p>
     *
     * <p>expand the sub-menu icon properties.</p>
     */
    public static final String OPENICON = "Tree.openIcon";

    /**
     * <p>TreeUI收起子菜单时父节点图标属性key。</p>
     *
     * <p>Collapse the sub-menu icon properties.</p>
     */
    public static final String CLOSEDICON = "Tree.closedIcon";

    /**
     * <p>TreeUI收起子菜单时箭头图标属性key。</p>
     *
     * <p>Collapse the sub-menu arrow icon properties.</p>
     */
    public static final String COLLAPSEDICON = "Tree.collapsedIcon";

    /**
     * <p>TreeUI展开子菜单时箭头图标属性key。</p>
     *
     * <p>expand the sub-menu arrow icon properties.</p>
     */
    public static final String EXPANDEDICON = "Tree.expandedIcon";

    /**
     * <p>TreeUI叶子节点头图标属性key。</p>
     *
     * <p>leaf node icon properties.</p>
     */
    public static final String LEAFICON = "Tree.leafIcon";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong>TreeUI层次线绘制属性key(true:绘制层次线)， 默认false。
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> Level line drawing
     * attributes(true is drawn), Defaults false.
     * </p>
     */
    public static final String PAINTLINES = "Tree.paintLines";

    /**
     * <p>TreeUI选中时背景颜色属性key。</p>
     *
     * <p>Select the Background Color property.</p>
     */
    public static final String SELECTIONBACKGROUND = "Tree.selectionBackground";

    /**
     * <p>TreeUI选中时前景颜色属性key。</p>
     *
     * <p>Select the Foreground Color property.</p>
     */
    public static final String SELECTIONFOREGROUND = "Tree.selectionForeground";

    /**
     * <p>TreeUI编辑时边框属性key。</p>
     *
     * <p>Edit border properties.</p>
     */
    public static final String EDITORBORDER = "Tree.editorBorder";

    public void uninitialize()
    {
        // remove color.
        UIManager.put(SELECTIONBACKGROUND, null);
        UIManager.put(SELECTIONFOREGROUND, null);

        // remove border.
        UIManager.put(EDITORBORDER, null);

        // remove image and icon.
        UIManager.put(OPENICON, null);
        UIManager.put(COLLAPSEDICON, null);
        UIManager.put(CLOSEDICON, null);
        UIManager.put(EXPANDEDICON, null);
        UIManager.put(COLLAPSEDICON, null);
        UIManager.put(LEAFICON, null);

        // remove other.
        UIManager.put(PAINTLINES, null);
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(SELECTIONBACKGROUND, getColorRes(171, 225, 235));

        table.put(SELECTIONFOREGROUND, getColorRes(Color.WHITE));
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        table.put(EDITORBORDER, getBorderRes(new LineBorder(new Color(3, 158, 211))));
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        UIManager.put(OPENICON, getIconRes("tree/folder_open.png"));

        UIManager.put(CLOSEDICON, getIconRes("tree/folder_normal.png"));

        UIManager.put(EXPANDEDICON, getIconRes("tree/expanded.png"));

        UIManager.put(COLLAPSEDICON, getIconRes("tree/collapsed.png"));

        UIManager.put(LEAFICON, getIconRes(new ImageIcon()));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        table.put(PAINTLINES, Boolean.FALSE);
    }
}
