package freeseawind.lf.basic.slider;

import java.awt.Color;

import javax.swing.UIManager;


import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

public class LuckSliderUIBundle extends LuckResourceBundle
{
    /**
     * 标尺颜色属性key
     */
    public static final String TICKCOLOR = "Slider.tickColor";

    /**
     * 背景颜色属性key
     */
    public static final String BACKGROUND = "Slider.background";
    
    /**
     * [自定义属性] 水平滑块图片属性key
     */
    public static final String THUMB_HORIZONTAL = "Slider.thumbHorizontalImg";

    /**
     * [自定义属性] 垂直滑块图片属性key
     */
    public static final String THUMB_VERTICAL = "Slider.thumbVerticalImg";

    /**
     * [自定义属性] 水平滑道背景图片属性key
     */
    public static final String TRACK_HORIZONTAL = "Slider.trackHorizontal";

    /**
     * [自定义属性] 水平滑道高亮背景图片属性key
     */
    public static final String TRACK_HORIZONTAL_H = "Slider.trackHorizontalHighlight";

    /**
     * [自定义属性] 垂直滑道高亮背景图片属性key
     */
    public static final String TRACK_VERTICAL = "Slider.trackVertical";

    /**
     * [自定义属性] 垂直滑道高亮背景图片属性key
     */
    public static final String TRACK_VERTICAL_H = "Slider.trackVerticalHighlight";

    /**
     * [自定义属性]滑道大小
     */
    public static final String TRACK_SIZE = "Slider.trackSize";
    
    @Override
    protected void installColor()
    {
        UIManager.put(TICKCOLOR, getColorRes(131, 131, 131));
        
        UIManager.put(BACKGROUND, Color.WHITE);
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(THUMB_HORIZONTAL, LuckRes.getImage("slider/thumb_h.png"));

        UIManager.put(THUMB_VERTICAL, LuckRes.getImage("slider/thumb_v.png"));

        UIManager.put(TRACK_HORIZONTAL, LuckRes.getImage("slider/track_horizontal.9.png"));

        UIManager.put(TRACK_HORIZONTAL_H, LuckRes.getImage("slider/track_horizontal_h.9.png"));

        UIManager.put(TRACK_VERTICAL, LuckRes.getImage("slider/track_vertical.9.png"));

        UIManager.put(TRACK_VERTICAL_H, LuckRes.getImage("slider/track_vertical_h.9.png"));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(TRACK_SIZE, 8);
    }
    
    
}
