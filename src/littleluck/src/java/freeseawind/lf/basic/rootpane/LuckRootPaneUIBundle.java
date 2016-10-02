package freeseawind.lf.basic.rootpane;

import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.InsetsUIResource;

import freeseawind.lf.border.LuckNinePatchBorder;
import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * <p>RootPaneUI资源绑定类。</p>
 * 
 * <p>RootPaneUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRootPaneUIBundle extends LuckResourceBundle
{
    /**
     * <p>[LittleLuck属性] 标题字体前景色属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] title font color properties.</p>
     */
    public static final String TITLE_FONT_COLOR = "Title.font.color";

    /**
     * <p>Frame边框属性key。</p>
     * 
     * <p>Frame border properties.</p>
     */
    public static final String FRAME_BORDER = "RootPane.frameBorder";

    /**
     * <p>PlainDialog边框属性key。</p>
     * 
     * <p>PlainDialog border properties.</p>
     */
    public static final String PLAINDIALOG_BORDER = "RootPane.plainDialogBorder";

    /**
     * <p>InformationDialog边框属性key。</p>
     * 
     * <p>InformationDialog border properties.</p>
     */
    public static final String INFORMATIONDIALOG_BORDER = "RootPane.informationDialogBorder";

    /**
     * <p>colorChooserDialog边框属性key。</p>
     * 
     * <p>ColorChooserDialog border properties.</p>
     */
    public static final String COLORCHOOSERDIALOG_BORDER = "RootPane.colorChooserDialogBorder";

    /**
     * <p>ErrorDialog边框属性key。</p>
     * 
     * <p>ErrorDialog border properties.</p>
     */
    public static final String ERRORDIALOG_BORDER = "RootPane.errorDialogBorder";

    /**
     * <p>FileChooserDialog边框属性key。</p>
     * 
     * <p>FileChooserDialog border properties.</p>
     */
    public static final String FILECHOOSERDIALOG_BORDER = "RootPane.fileChooserDialogBorder";

    /**
     * <p>questionDialog边框属性key。</p>
     * 
     * <p>QuestionDialog border properties.</p>
     */
    public static final String QUESTIONDIALOG_BORDER = "RootPane.questionDialogBorder";

    /**
     * <p>WarningDialog边框属性key。</p>
     * 
     * <p>WarningDialog border properties.</p>
     */
    public static final String WARNINGDIALOG_BORDER = "RootPane.warningDialogBorder";

    /**
     *  <p>[LittleLuck属性] 标题面板高度属性key。</p>
     *  
     *  <p>[LittLeLuck Attributes] Title panel height properties.</p>
     */
    public static final String TITLEPANEL_HEIGHT = "TitlePanel.height";

    /**
     *  <p>[LittleLuck属性] 标题面板背景图片属性key。</p>
     *  
     *  <p>[LittLeLuck Attributes] Title panel background image properties.</p>
     */
    public static final String TITLEPANEL_BG_IMG = "TitlePanel.bg.img";

    /**
     *  <p>[LittleLuck属性] 应用标题图片和字体的间距。</p>
     *  
     *  <p>[LittLeLuck Attributes] application icon text gap properties.</p>
     */
    public static final String APPLICATION_TITLE_TEXTGAP = "Application.title.textgap";

    /**
     * <p>[LittleLuck属性] 应用图标间距。</p>
     * 
     * <p>[LittLeLuck Attributes] Window icon Spacing</p>
     */
    public static final String APPLICATION_TITLE_INSETS = "Application.icon.insets";

    /**
     * <p>[LittleLuck属性] 关闭按钮无状态下图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Close button default icon property.</p>
     */
    public static final String CLOSE_NORMAL_ICON = "Frame.closeNormal";

    /**
     * <p>[LittleLuck属性] 关闭按钮鼠标经过时图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Close button Icon Properties when mouse enter.</p>
     */
    public static final String CLOSE_ROVER_ICON = "Frame.closeRover";

    /**
     * <p>[LittleLuck属性] 关闭按钮鼠标点击时图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Close button Icon Properties when mouse pressed.</p>
     */
    public static final String CLOSE_PRESSED_ICON = "Frame.closePressed";

    /**
     * <p>[LittleLuck属性] 最小化按钮无状态下图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Minimize button default Icon properties. </p>
     */
    public static final String MIN_NORMAL_ICON = "Frame.minNormal";

    /**
     * <p>[LittleLuck属性] 最小化按钮鼠标经过时图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Minimize button Icon properties when mouse enter.</p>
     */
    public static final String MIN_ROVER_ICON = "Frame.minRover";

    /**
     * <p>[LittleLuck属性] 最小化按钮鼠标点击时图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Minimize button Icon properties when mouse pressed.</p>
     */
    public static final String MIN_PRESSED_ICON = "Frame.minPressed";

    /**
     * <p>[LittleLuck属性] 最大化按钮无状态下图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Maximize button default Icon properties</p>
     */
    public static final String MAX_NORMAL_ICON = "Frame.maxNormal";

    /**
     * <p>[LittleLuck属性] 最大化按钮鼠标经过时图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Maximize button Icon properties when mouse enter.</p>
     */
    public static final String MAX_ROVER_ICON = "Frame.maxRover";

    /**
     * <p>[LittleLuck属性] 最大化按钮鼠标点击时图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Maximize button Icon properties when mouse pressed.</p>
     */
    public static final String MAX_PRESSED_ICON = "Frame.maxPressed";

    /**
     * <p>[LittleLuck属性]还原按钮无状态下图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] MaxMinimize button default Icon properties.</p>
     */
    public static final String MAXIMIZE_NORMAL_ICON = "Frame.maxmizeNormal";

    /**
     * <p>[LittleLuck属性] 还原按钮鼠标经过时图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] MaxMinimize button Icon properties when mouse enter.</p>
     */
    public static final String MAXIMIZE_ROVER_ICON = "Frame.maxmizeRover";

    /**
     * <p>[LittleLuck属性] 还原按钮鼠标点击时图标属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] MaxMinimize button Icon properties when mouse pressed.</p>
     */
    public static final String MAXIMIZE_PRESSED_ICON = "Frame.maxmizePressed";
    
    public void uninitialize()
    {
        UIManager.put(TITLE_FONT_COLOR, null);
        UIManager.put(TITLEPANEL_HEIGHT, null);
        UIManager.put(TITLEPANEL_BG_IMG, null);
        UIManager.put(APPLICATION_TITLE_TEXTGAP, null);
        UIManager.put(APPLICATION_TITLE_INSETS, null);
        
        UIManager.put(CLOSE_NORMAL_ICON, null);
        UIManager.put(CLOSE_ROVER_ICON, null);
        UIManager.put(CLOSE_PRESSED_ICON, null);

        UIManager.put(MIN_NORMAL_ICON, null);
        UIManager.put(MIN_ROVER_ICON, null);
        UIManager.put(MIN_PRESSED_ICON, null);

        UIManager.put(MAX_NORMAL_ICON, null);
        UIManager.put(MAX_ROVER_ICON, null);
        UIManager.put(MAX_PRESSED_ICON, null);

        UIManager.put(MAXIMIZE_NORMAL_ICON, null);
        UIManager.put(MAXIMIZE_ROVER_ICON, null);
        UIManager.put(MAXIMIZE_PRESSED_ICON, null);
        
        UIManager.put(TITLEPANEL_BG_IMG, null);
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(TITLE_FONT_COLOR, getColorRes(Color.BLACK));
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        // 初始化阴影边框
        Insets insets = new Insets(5, 5, 5, 5);
        BufferedImage shadowImg = LuckRes.getImage("frame/shadow_border.9.png");
        Border shadowBorder = getBorderRes(new LuckNinePatchBorder(insets, shadowImg));

        // 设置窗体、弹窗边框配置
        table.put(FRAME_BORDER, shadowBorder);
        table.put(PLAINDIALOG_BORDER, shadowBorder);
        table.put(INFORMATIONDIALOG_BORDER, shadowBorder);
        table.put(ERRORDIALOG_BORDER, shadowBorder);
        table.put(COLORCHOOSERDIALOG_BORDER, shadowBorder);
        table.put(FILECHOOSERDIALOG_BORDER, shadowBorder);
        table.put(QUESTIONDIALOG_BORDER, shadowBorder);
        table.put(WARNINGDIALOG_BORDER, shadowBorder);
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        table.put(CLOSE_NORMAL_ICON, getIconRes("frame/frame_close_normal.png"));
        table.put(CLOSE_ROVER_ICON, getIconRes("frame/frame_close_rover.png"));
        table.put(CLOSE_PRESSED_ICON, getIconRes("frame/frame_close_pressed.png"));

        table.put(MIN_NORMAL_ICON, getIconRes("frame/frame_min_normal.png"));
        table.put(MIN_ROVER_ICON, getIconRes("frame/frame_min_rover.png"));
        table.put(MIN_PRESSED_ICON, getIconRes("frame/frame_min_pressed.png"));

        table.put(MAX_NORMAL_ICON, getIconRes("frame/frame_max_normal.png"));
        table.put(MAX_ROVER_ICON, getIconRes("frame/frame_max_rover.png"));
        table.put(MAX_PRESSED_ICON, getIconRes("frame/frame_max_pressed.png"));

        table.put(MAXIMIZE_NORMAL_ICON, getIconRes("frame/frame_maxwin_normal.png"));
        table.put(MAXIMIZE_ROVER_ICON, getIconRes("frame/frame_maxwin_rover.png"));
        table.put(MAXIMIZE_PRESSED_ICON, getIconRes("frame/frame_maxwin_pressed.png"));
        
        table.put(TITLEPANEL_BG_IMG, LuckRes.getImage("frame/title_bg.9.png"));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        table.put(TITLEPANEL_HEIGHT, 26);
        table.put(APPLICATION_TITLE_TEXTGAP, 5);
        table.put(APPLICATION_TITLE_INSETS, new InsetsUIResource(4, 6, 0, 0));
    }
}
