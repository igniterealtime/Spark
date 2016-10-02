package freeseawind.lf.basic.popupmenu;

import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.PopupFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;

import freeseawind.lf.border.LuckNinePatchBorder;
import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;
import freeseawind.swing.LuckPopupFactory;

/**
 * <p>PopupMenuUI资源绑定类。</p>
 *
 * <p>A PopupMenuUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckPopupMenuUIBundle extends LuckResourceBundle
{
    /**
     * <p>PopupMenu边框属性key</p>
     *
     * <p>PopupMenu border properties.</p>
     */
    public static final String BORDER = "PopupMenu.border";

    /**
     * <p>PopupMenu分割线颜色属性key</p>
     *
     * <p>PopupMenu Separator color properties.</p>
     */
    public static final String SEPEREATOR_COLOR = "PopupSeparator.bgcolor";

    public void uninitialize()
    {
        PopupFactory.setSharedInstance(new PopupFactory());
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        UIManager.put(SEPEREATOR_COLOR, getColorRes(215, 220, 222));
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        BufferedImage img = LuckRes.getImage("popupmenu/shadow_border.9.png");
        
        Border border = new LuckNinePatchBorder(new Insets(5, 3, 6, 3), img);

        table.put(BORDER, getBorderRes(border));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        // 使用自定义工厂, 设置Popup为透明, 否则无法使用阴影边框
        // Use a custom factory, set the Popup to be transparent.
        // otherwise you can not use the shadow border.
        PopupFactory.setSharedInstance(new LuckPopupFactory());
    }
}
