package freeseawind.lf.basic.internalframe;

import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;

import freeseawind.lf.border.LuckNinePatchBorder;
import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * LuckInternalFrameUI资源绑定类
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckInternalFrameUIBundle extends LuckResourceBundle
{
    /**
     * InternalFrame标题面板活动状态下背景颜色属性key
     */
    public static final String ACTIVETITLEBACKGROUND = "InternalFrame.activeTitleBackground";

    /**
     * InternalFrame标题面板非活动状态下背景颜色属性key
     */
    public static final String INACTIVETITLEBACKGROUND = "InternalFrame.inactiveTitleBackground";

    /**
     * InternalFrame桌面背景颜色属性key
     */
    public static final String DESKTOP_BACKGROUND = "Desktop.background";

    /**
     * InternalFrame边框属性key
     */
    public static final String BORDER = "InternalFrame.border";

    /**
     *
     */
    public static final String PALETTEBORDER = "InternalFrame.paletteBorder";

    /**
     * InternalFrame弹出框边框属性key
     */
    public static final String OPTIONDIALOGBORDER = "InternalFrame.optionDialogBorder";

    /**
     * [自定义属性] 关闭按钮(无状态)图标属性key
     */
    public static final String CLOSEICON_NORMAL = "InternalFrame.closeIcon";

    /**
     * [自定义属性] 关闭按钮(鼠标经过)图标属性key
     */
    public static final String CLOSEICON_ROLLVER = "InternalFrame.closeIcon.rollver";

    /**
     * [自定义属性] 关闭按钮(鼠标点击)图标属性key
     */
    public static final String CLOSEICON_PRESSED = "InternalFrame.closeIcon.pressed";

    /**
     * [自定义属性] 最小化按钮(无状态)图标属性key
     */
    public static final String ICONIFYICON_NORMAL = "InternalFrame.iconifyIcon";

    /**
     * [自定义属性] 最小化按钮(鼠标经过)图标属性key
     */
    public static final String ICONIFYICON_ROLLVER = "InternalFrame.iconifyIcon.rollver";

    /**
     * [自定义属性] 最小化按钮(鼠标点击)图标属性key
     */
    public static final String ICONIFYICON_PRESSED = "InternalFrame.iconifyIcon.pressed";

    /**
     * [自定义属性] 最大或最小化按钮(无状态)图标属性key
     */
    public static final String MINICON_NORMAL = "InternalFrame.minimizeIcon";

    /**
     * [自定义属性] 最大或最小化按钮(鼠标经过)图标属性key
     */
    public static final String MINICON_ROLLVER = "InternalFrame.minimizeIcon.rollver";

    /**
     * [自定义属性] 最大或最小化按钮(鼠标点击)图标属性key
     */
    public static final String MINICON_PRESSED = "InternalFrame.minimizeIcon.pressed";

    /**
     * [自定义属性] 最大或还原按钮(无状态)图标属性key
     */
    public static final String MAXICON_NORMAL = "InternalFrame.maximizeIcon";

    /**
     * [自定义属性] 最大或还原按钮(鼠标经过)图标属性key
     */
    public static final String MAXICON_ROLLVER = "InternalFrame.maximizeIcon.rollver";

    /**
     * [自定义属性] 最大或还原按钮(鼠标点击)图标属性key
     */
    public static final String MAXICON_PRESSED = "InternalFrame.maximizeIcon.pressed";
    
    /**
     * [自定义属性] InternalFrame应用图标属性key
     */
    public static final String INTERNALFRAME_ICON = "InternalFrame.icon";
    
    /**
     *  [自定义属性]标题面板背景图片属性key
     */
    public static final String TITLEPANEL_BG_IMG = "InternalFrame.titlePanel.bgImg";

    /**
     * [自定义属性] InternalFrame边标题面板高度属性key
     */
    public static final String TITLEPANE_HEIGHT = "InternalFrame.titlePanel.height";

    /**
     * [重要]标题面板布局属性key
     */
    public static final String LAYOUTTITLEPANEATORIGIN = "InternalFrame.layoutTitlePaneAtOrigin";

    @Override
    protected void installBorder()
    {
        // 初始化阴影边框
        Insets insets = new Insets(5, 5, 5, 5);
        BufferedImage shadowImg = LuckRes.getImage("internalframe/shadow_border.9.png");
        Border shadowBorder = new LuckNinePatchBorder(insets, shadowImg);

        //
        UIManager.put(BORDER, new BorderUIResource(shadowBorder));
        UIManager.put(PALETTEBORDER, new BorderUIResource(shadowBorder));
        UIManager.put(OPTIONDIALOGBORDER, new BorderUIResource(shadowBorder));
    }

    @Override
    protected void installColor()
    {
        UIManager.put(ACTIVETITLEBACKGROUND, new ColorUIResource(Color.WHITE));

        UIManager.put(INACTIVETITLEBACKGROUND, new ColorUIResource(Color.WHITE));

        UIManager.put(DESKTOP_BACKGROUND, new ColorUIResource(Color.white));
    }

    @Override
    protected void loadImages()
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
        UIManager.put(MINICON_NORMAL, getIconRes("frame/frame_max_normal.png"));
        UIManager.put(MINICON_ROLLVER, getIconRes("frame/frame_max_rover.png"));
        UIManager.put(MINICON_PRESSED, getIconRes("frame/frame_max_pressed.png"));

        // ----------------------分割线--------------------------------- //
        UIManager.put(MAXICON_NORMAL, getIconRes("frame/frame_maxwin_normal.png"));
        UIManager.put(MAXICON_ROLLVER, getIconRes("frame/frame_maxwin_rover.png"));
        UIManager.put(MAXICON_PRESSED, getIconRes("frame/frame_maxwin_pressed.png"));
        
        // ----------------------分割线--------------------------------- //
        UIManager.put(TITLEPANEL_BG_IMG, LuckRes.getImage("frame/title_bg.9.png"));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(TITLEPANE_HEIGHT, 26);

        UIManager.put(LAYOUTTITLEPANEATORIGIN, Boolean.FALSE);
    }
}
