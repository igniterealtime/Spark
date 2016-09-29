package freeseawind.lf.basic.rootpane;

import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;

import freeseawind.lf.border.LuckNinePatchBorder;
import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * 根窗格资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRootPaneUIBundle extends LuckResourceBundle
{
	/**
	 * [自定义属性]根窗格背景颜色key
	 */
    public static final String ROOTPANE_BACKGROUND_COLOR = "RootPane.alpha";

    /**
     * [自定义属性]标题字体前景色属性key
     */
    public static final String TITLE_FONT_COLOR = "Title.font.color";

    /**
     * frame边框属性key
     */
    public static final String FRAME_BORDER = "RootPane.frameBorder";

    /**
     * plainDialog边框属性key
     */
    public static final String PLAINDIALOG_BORDER = "RootPane.plainDialogBorder";

    /**
     * informationDialog边框属性key
     */
    public static final String INFORMATIONDIALOG_BORDER = "RootPane.informationDialogBorder";

    /**
     * colorChooserDialog边框属性key
     */
    public static final String COLORCHOOSERDIALOG_BORDER = "RootPane.colorChooserDialogBorder";

    /**
     * errorDialog边框属性key
     */
    public static final String ERRORDIALOG_BORDER = "RootPane.errorDialogBorder";

    /**
     * fileChooserDialog边框属性key
     */
    public static final String FILECHOOSERDIALOG_BORDER = "RootPane.fileChooserDialogBorder";

    /**
     * questionDialog边框属性key
     */
    public static final String QUESTIONDIALOG_BORDER = "RootPane.questionDialogBorder";

    /**
     * warningDialog边框属性key
     */
    public static final String WARNINGDIALOG_BORDER = "RootPane.warningDialogBorder";

    /**
     *  [自定义属性]标题面板高度属性key
     */
    public static final String TITLEPANEL_HEIGHT = "TitlePanel.height";

    /**
     *  [自定义属性]标题面板背景图片属性key
     */
    public static final String TITLEPANEL_BG_IMG = "TitlePanel.bg.img";

    /**
     *  [自定义属性]应用标题图片和字体的间距
     */
    public static final String APPLICATION_TITLE_TEXTGAP = "Application.title.textgap";

    /**
     * [自定义属性]应用图标间距
     */
    public static final String APPLICATION_TITLE_INSETS = "Application.icon.insets";

    // ################################ 窗体按钮图标 ###########################################

    /**
     * [自定义属性]关闭按钮无状态下图标属性key
     */
    public static final String CLOSE_NORMAL_ICON = "Frame.closeNormal";

    /**
     * [自定义属性]关闭按钮鼠标经过时图标属性key
     */
    public static final String CLOSE_ROVER_ICON = "Frame.closeRover";

    /**
     * [自定义属性]关闭按钮鼠标点击时图标属性key
     */
    public static final String CLOSE_PRESSED_ICON = "Frame.closePressed";

    /**
     * [自定义属性]最小化按钮无状态下图标属性key
     */
    public static final String MIN_NORMAL_ICON = "Frame.minNormal";

    /**
     * [自定义属性]最小化按钮鼠标经过时图标属性key
     */
    public static final String MIN_ROVER_ICON = "Frame.minRover";

    /**
     * [自定义属性]最小化按钮鼠标点击时图标属性key
     */
    public static final String MIN_PRESSED_ICON = "Frame.minPressed";

    /**
     * [自定义属性]最大化按钮无状态下图标属性key
     */
    public static final String MAX_NORMAL_ICON = "Frame.maxNormal";

    /**
     * [自定义属性]最大化按钮鼠标经过时图标属性key
     */
    public static final String MAX_ROVER_ICON = "Frame.maxRover";

    /**
     * [自定义属性]最大化按钮鼠标点击时图标属性key
     */
    public static final String MAX_PRESSED_ICON = "Frame.maxPressed";

    /**
     * [自定义属性]还原按钮无状态下图标属性key
     */
    public static final String MAXIMIZE_NORMAL_ICON = "Frame.maxmizeNormal";

    /**
     * [自定义属性]还原按钮鼠标经过时图标属性key
     */
    public static final String MAXIMIZE_ROVER_ICON = "Frame.maxmizeRover";

    /**
     * [自定义属性]还原按钮鼠标点击时图标属性key
     */
    public static final String MAXIMIZE_PRESSED_ICON = "Frame.maxmizePressed";


    @Override
    protected void installColor()
    {
        UIManager.put(ROOTPANE_BACKGROUND_COLOR, new ColorUIResource(new Color(0, 0, 0, 0)));
        UIManager.put(TITLE_FONT_COLOR, new ColorUIResource(Color.BLACK));
    }

    @Override
    protected void installBorder()
    {
        // 初始化阴影边框
        Insets insets = new Insets(5, 5, 5, 5);
        BufferedImage shadowImg = LuckRes.getImage("frame/shadow_border.9.png");
        Border shadowBorder = new LuckNinePatchBorder(insets, shadowImg);

        // 设置窗体、弹窗边框配置
        UIManager.put(FRAME_BORDER, shadowBorder);
        UIManager.put(PLAINDIALOG_BORDER, shadowBorder);
        UIManager.put(INFORMATIONDIALOG_BORDER, shadowBorder);
        UIManager.put(ERRORDIALOG_BORDER, shadowBorder);
        UIManager.put(COLORCHOOSERDIALOG_BORDER, shadowBorder);
        UIManager.put(FILECHOOSERDIALOG_BORDER, shadowBorder);
        UIManager.put(QUESTIONDIALOG_BORDER, shadowBorder);
        UIManager.put(WARNINGDIALOG_BORDER, shadowBorder);
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(CLOSE_NORMAL_ICON, getIconRes("frame/frame_close_normal.png"));
        UIManager.put(CLOSE_ROVER_ICON, getIconRes("frame/frame_close_rover.png"));
        UIManager.put(CLOSE_PRESSED_ICON, getIconRes("frame/frame_close_pressed.png"));

        UIManager.put(MIN_NORMAL_ICON, getIconRes("frame/frame_min_normal.png"));
        UIManager.put(MIN_ROVER_ICON, getIconRes("frame/frame_min_rover.png"));
        UIManager.put(MIN_PRESSED_ICON, getIconRes("frame/frame_min_pressed.png"));

        UIManager.put(MAX_NORMAL_ICON, getIconRes("frame/frame_max_normal.png"));
        UIManager.put(MAX_ROVER_ICON, getIconRes("frame/frame_max_rover.png"));
        UIManager.put(MAX_PRESSED_ICON, getIconRes("frame/frame_max_pressed.png"));

        UIManager.put(MAXIMIZE_NORMAL_ICON, getIconRes("frame/frame_maxwin_normal.png"));
        UIManager.put(MAXIMIZE_ROVER_ICON, getIconRes("frame/frame_maxwin_rover.png"));
        UIManager.put(MAXIMIZE_PRESSED_ICON, getIconRes("frame/frame_maxwin_pressed.png"));
        
        UIManager.put(TITLEPANEL_BG_IMG, LuckRes.getImage("frame/title_bg.9.png"));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(TITLEPANEL_HEIGHT, 26);
        UIManager.put(APPLICATION_TITLE_TEXTGAP, 5);
        UIManager.put(APPLICATION_TITLE_INSETS, new InsetsUIResource(4, 6, 0, 0));
    }
}
