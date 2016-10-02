package freeseawind.lf.basic.internalframe;

import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;

import freeseawind.lf.border.LuckNinePatchBorder;
import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * <pre>
 * LuckInternalFrameUI资源绑定类。
 *
 * LuckInternalFrameUI resource bundle class.
 * </pre>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckInternalFrameUIBundle extends LuckResourceBundle
{
    /**
     * <p>InternalFrame标题面板活动状态下背景颜色属性key</p>
     *
     * <p>InternalFrame title background color properties when active.</p>
     */
    public static final String ACTIVETITLEBACKGROUND = "InternalFrame.activeTitleBackground";

    /**
     * <p>InternalFrame标题面板非活动状态下背景颜色属性key</p>
     *
     * <p>InternalFrame title background color properties when inactive.</p>
     */
    public static final String INACTIVETITLEBACKGROUND = "InternalFrame.inactiveTitleBackground";

    /**
     * <p>InternalFrame桌面背景颜色属性key</p>
     *
     * <p>Desktop background color properties.</p>
     */
    public static final String DESKTOP_BACKGROUND = "Desktop.background";

    /**
     * <p>InternalFrame边框属性key</p>
     *
     * <p>InternalFrame border properties.</p>
     */
    public static final String BORDER = "InternalFrame.border";

    /**
     * <p>PaletteBorder属性key</p>
     *
     * <p>PaletteBorder properties.</p>
     */
    public static final String PALETTEBORDER = "InternalFrame.paletteBorder";

    /**
     * <p>OptionDialog边框属性key</p>
     *
     * <p>OptionDialog border properties.</p>
     */
    public static final String OPTIONDIALOGBORDER = "InternalFrame.optionDialogBorder";

    /**
     * <p><strong>[LittleLuck属性]</strong> 关闭按钮(无状态)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong> Close button icon properties.</p>
     */
    public static final String CLOSEICON_NORMAL = "InternalFrame.closeIcon";

    /**
     * <p><strong>[LittleLuck属性]</strong> 关闭按钮(鼠标经过)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong> Close button icon properties when mouse over.</p>
     */
    public static final String CLOSEICON_ROLLVER = "InternalFrame.closeIcon.rollver";

    /**
     * <p><strong>[LittleLuck属性]</strong> 关闭按钮(鼠标经过)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong> Close button icon properties when mouse click.</p>
     */
    public static final String CLOSEICON_PRESSED = "InternalFrame.closeIcon.pressed";

    /**
     * <p><strong>[LittleLuck属性]</strong> 最小化按钮(无状态)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>Minimize button icon properties.</p>
     */
    public static final String ICONIFYICON_NORMAL = "InternalFrame.iconifyIcon";

    /**
     * <p><strong>[LittleLuck属性]</strong> 最小化按钮(鼠标经过)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>Minimize button icon properties when mouse over.</p>
     */
    public static final String ICONIFYICON_ROLLVER = "InternalFrame.iconifyIcon.rollver";

    /**
     * <p><strong>[LittleLuck属性]</strong> 最小化按钮(鼠标点击)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>Minimize button icon properties when mouse click.</p>
     */
    public static final String ICONIFYICON_PRESSED = "InternalFrame.iconifyIcon.pressed";

    /**
     * <p><strong>[LittleLuck属性]</strong> 最大或最小化按钮(无状态)图标属性key </p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>MaxMinimize button icon properties.</p>
     */
    public static final String MAXMINIMIZEICON_NORMAL = "InternalFrame.maxMinimizeIcon";

    /**
     * <p><strong>[LittleLuck属性]</strong> 最大或最小化按钮(鼠标经过)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>MaxMinimize button icon properties when mouse over.</p>
     */
    public static final String MAXMINIMIZEICON_ROLLVER = "InternalFrame.maxMinimizeIcon.rollver";

    /**
     * <p><strong>[LittleLuck属性]</strong> 最大或最小化按钮(鼠标点击)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>MaxMinimize button icon properties when mouse click.</p>
     */
    public static final String MAXMINIMIZEICON_PRESSED = "InternalFrame.maxMinimizeIcon.pressed";

    /**
     * <p><strong>[LittleLuck属性]</strong> 最大或还原按钮(无状态)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>Maximize button icon properties.</p>
     */
    public static final String MAXICON_NORMAL = "InternalFrame.maximizeIcon";

    /**
     * <p><strong>[LittleLuck属性]</strong> 最大或还原按钮(鼠标经过)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>Maximize button icon properties when mouse over.</p>
     */
    public static final String MAXICON_ROLLVER = "InternalFrame.maximizeIcon.rollver";

    /**
     * <p><strong>[LittleLuck属性]</strong> 最大或还原按钮(鼠标点击)图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>Maximize button icon properties when mouse click.</p>
     */
    public static final String MAXICON_PRESSED = "InternalFrame.maximizeIcon.pressed";

    /**
     * <p><strong>[LittleLuck属性]</strong>InternalFrame应用图标属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong>InternalFrame icon properties.</p>
     */
    public static final String INTERNALFRAME_ICON = "InternalFrame.icon";

    /**
     * <p><strong>[LittleLuck属性]</strong>标题面板背景图片属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong> title panel background image properties.</p>
     */
    public static final String TITLEPANEL_BG_IMG = "InternalFrame.titlePanel.bgImg";

    /**
     * <p><strong>[LittleLuck属性]</strong> InternalFrame边标题面板高度属性key</p>
     *
     * <p><strong>[LittleLuck Attributes]</strong> title panel height properties.</p>
     */
    public static final String TITLEPANE_HEIGHT = "InternalFrame.titlePanel.height";

    /**
     * <p><strong>[重要]</strong>标题面板布局属性key</p>
     *
     * <p>Layout title pane at origin properties.</p>
     */
    public static final String LAYOUTTITLEPANEATORIGIN = "InternalFrame.layoutTitlePaneAtOrigin";

    public void uninitialize()
    {
        UIManager.put(INTERNALFRAME_ICON, null);

        // ----------------------分割线--------------------------------- //
        UIManager.put(CLOSEICON_NORMAL, null);
        UIManager.put(CLOSEICON_ROLLVER, null);
        UIManager.put(CLOSEICON_PRESSED, null);

        // ----------------------分割线--------------------------------- //
        UIManager.put(ICONIFYICON_NORMAL, null);
        UIManager.put(ICONIFYICON_ROLLVER, null);
        UIManager.put(ICONIFYICON_PRESSED, null);

        // ----------------------分割线--------------------------------- //
        UIManager.put(MAXMINIMIZEICON_NORMAL, null);
        UIManager.put(MAXMINIMIZEICON_ROLLVER, null);
        UIManager.put(MAXMINIMIZEICON_PRESSED, null);

        // ----------------------分割线--------------------------------- //
        UIManager.put(MAXICON_NORMAL, null);
        UIManager.put(MAXICON_ROLLVER, null);
        UIManager.put(MAXICON_PRESSED, null);

        // ----------------------分割线--------------------------------- //
        UIManager.put(TITLEPANEL_BG_IMG, null);

        UIManager.put(TITLEPANE_HEIGHT, null);
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        // 初始化阴影边框
        Insets insets = new Insets(5, 5, 5, 5);
        BufferedImage shadowImg = LuckRes.getImage("internalframe/shadow_border.9.png");
        Border shadowBorder = new LuckNinePatchBorder(insets, shadowImg);

        //
        table.put(BORDER, getBorderRes(shadowBorder));
        table.put(PALETTEBORDER, getBorderRes(shadowBorder));
        table.put(OPTIONDIALOGBORDER, getBorderRes(shadowBorder));
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        UIManager.put(ACTIVETITLEBACKGROUND, getColorRes(Color.WHITE));

        UIManager.put(INACTIVETITLEBACKGROUND, getColorRes(Color.WHITE));

        UIManager.put(DESKTOP_BACKGROUND, getColorRes(Color.WHITE));
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        UIManager.put(INTERNALFRAME_ICON, getIconRes("frame/default_frame_icon.png"));

        // ----------------------分割线--------------------------------- //
        UIManager.put(CLOSEICON_NORMAL, getIconRes("frame/frame_close_normal.png"));
        UIManager.put(CLOSEICON_ROLLVER, getIconRes("frame/frame_close_rover.png"));
        UIManager.put(CLOSEICON_PRESSED, getIconRes("frame/frame_close_pressed.png"));

        // ----------------------分割线--------------------------------- //
        UIManager.put(ICONIFYICON_NORMAL, getIconRes("frame/frame_min_normal.png"));
        UIManager.put(ICONIFYICON_ROLLVER, getIconRes("frame/frame_min_rover.png"));
        UIManager.put(ICONIFYICON_PRESSED, getIconRes("frame/frame_min_pressed.png"));

        // ----------------------分割线--------------------------------- //
        UIManager.put(MAXMINIMIZEICON_NORMAL, getIconRes("frame/frame_max_normal.png"));
        UIManager.put(MAXMINIMIZEICON_ROLLVER, getIconRes("frame/frame_max_rover.png"));
        UIManager.put(MAXMINIMIZEICON_PRESSED, getIconRes("frame/frame_max_pressed.png"));

        // ----------------------分割线--------------------------------- //
        UIManager.put(MAXICON_NORMAL, getIconRes("frame/frame_maxwin_normal.png"));
        UIManager.put(MAXICON_ROLLVER, getIconRes("frame/frame_maxwin_rover.png"));
        UIManager.put(MAXICON_PRESSED, getIconRes("frame/frame_maxwin_pressed.png"));

        // ----------------------分割线--------------------------------- //
        UIManager.put(TITLEPANEL_BG_IMG, LuckRes.getImage("frame/title_bg.9.png"));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        UIManager.put(TITLEPANE_HEIGHT, 26);

        table.put(LAYOUTTITLEPANEATORIGIN, Boolean.FALSE);
    }
}
