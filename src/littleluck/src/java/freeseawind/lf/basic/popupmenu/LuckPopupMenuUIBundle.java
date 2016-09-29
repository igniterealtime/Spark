package freeseawind.lf.basic.popupmenu;

import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.PopupFactory;
import javax.swing.UIManager;

import freeseawind.lf.border.LuckNinePatchBorder;
import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;
import freeseawind.swing.LuckPopupFactory;

/**
 * PopupMenuUI资源绑定类。
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckPopupMenuUIBundle extends LuckResourceBundle
{
    /**
     * PopupMenu边框属性key
     */
    public static final String BORDER = "PopupMenu.border";

    /**
     * PopupMenu分割线颜色属性key
     */
    public static final String SEPEREATOR_COLOR = "PopupSeparator.bgcolor";

    @Override
    protected void installColor()
    {
        UIManager.put(SEPEREATOR_COLOR, new Color(215, 220, 222));
    }

    @Override
    protected void installBorder()
    {
        BufferedImage img = LuckRes.getImage("popupmenu/shadow_border.9.png");

        UIManager.put(BORDER, new LuckNinePatchBorder(new Insets(5, 3, 6, 3), img));
    }

    @Override
    protected void installOther()
    {
        //使用自定义工厂, 设置Popup为透明, 否则无法使用阴影边框
        PopupFactory.setSharedInstance(new LuckPopupFactory());
    }
}
