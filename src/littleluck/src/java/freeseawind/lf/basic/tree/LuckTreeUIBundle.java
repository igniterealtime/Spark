package freeseawind.lf.basic.tree;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * TreeUI资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckTreeUIBundle extends LuckResourceBundle
{
    /**
     * TreeUI展开子菜单时父节点图标属性key
     */
    public static final String OPENICON = "Tree.openIcon";

    /**
     * TreeUI收起子菜单时父节点图标属性key
     */
    public static final String CLOSEDICON = "Tree.closedIcon";

    /**
     * TreeUI收起子菜单时箭头图标属性key
     */
    public static final String COLLAPSEDICON = "Tree.collapsedIcon";

    /**
     * TreeUI展开子菜单时箭头图标属性key
     */
    public static final String EXPANDEDICON = "Tree.expandedIcon";

    /**
     * TreeUI叶子节点头图标属性key
     */
    public static final String LEAFICON = "Tree.leafIcon";

    /**
     * TreeUI层次线绘制属性key(true:绘制层次线)
     */
    public static final String PAINTLINES = "Tree.paintLines";

    /**
     * TreeUI选中时背景颜色属性key
     */
    public static final String SELECTIONBACKGROUND = "Tree.selectionBackground";
    
    /**
     * TreeUI选中时前景颜色属性key
     */
    public static final String SELECTIONFOREGROUND = "Tree.selectionForeground";

    /**
     * TreeUI编辑时边框属性key
     */
    public static final String EDITORBORDER = "Tree.editorBorder";
    
    

    @Override
    protected void installColor()
    {
        UIManager.put(SELECTIONBACKGROUND, getColorRes(171, 225, 235));
        
        UIManager.put(SELECTIONFOREGROUND, Color.WHITE);
    }

    @Override
    protected void installBorder()
    {
        UIManager.put(EDITORBORDER, new LineBorder(new Color(3, 158, 211)));
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(OPENICON, new ImageIcon(LuckRes.getImage("tree/folder_open.png")));

        UIManager.put(CLOSEDICON, new ImageIcon(LuckRes.getImage("tree/folder_normal.png")));

        UIManager.put(EXPANDEDICON, new ImageIcon(LuckRes.getImage("tree/expanded.png")));

        UIManager.put(COLLAPSEDICON, new ImageIcon(LuckRes.getImage("tree/collapsed.png")));

        UIManager.put(LEAFICON, new ImageIcon());
    }

    @Override
    protected void installOther()
    {
        UIManager.put(PAINTLINES, false);
    }
}
