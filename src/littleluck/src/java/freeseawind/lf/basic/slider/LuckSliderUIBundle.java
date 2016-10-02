package freeseawind.lf.basic.slider;

import java.awt.Color;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * <p>SliderUI资源绑定类。</p>
 * 
 * <p>SliderUI resource bundle class.</p>
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckSliderUIBundle extends LuckResourceBundle
{
    /**
     * <p>标尺颜色属性key。</p>
     * 
     * <p>Slider tick color properties.</p>
     */
    public static final String TICKCOLOR = "Slider.tickColor";

    /**
     * <p>背景颜色属性key。</p>
     * 
     * <p>Slider background color properties.</p>
     */
    public static final String BACKGROUND = "Slider.background";
    
    /**
     * <p>[LittleLuck属性] 水平滑块图片属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Slider horizontal thumb image properties.</p>
     */
    public static final String THUMB_HORIZONTAL = "Slider.thumbHorizontalImg";

    /**
     * <p>[LittleLuck属性] 垂直滑块图片属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Slider vertical thumb image properties.</p>
     */
    public static final String THUMB_VERTICAL = "Slider.thumbVerticalImg";

    /**
     * <p>[LittleLuck属性] 水平滑道背景图片属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Slider horizontal track image properties.</p>
     */
    public static final String TRACK_HORIZONTAL = "Slider.trackHorizontal";

    /**
     * <p>[LittleLuck属性] 水平滑道高亮背景图片属性key。</p>
     * 
     * <p>[LittLeLuck Attributes] Slider horizontal track progress image properties.</p>
     */
    public static final String TRACK_HORIZONTAL_H = "Slider.trackHorizontalHighlight";

    /**
     * <p>[LittleLuck属性] 垂直滑道背景图片属性key。 </p>
     * 
     * <p>[LittLeLuck Attributes] Slider vertical track image properties.</p>
     */
    public static final String TRACK_VERTICAL = "Slider.trackVertical";

    /**
     * <p>[LittleLuck属性] 垂直滑道高亮背景图片属性key </p>
     * 
     * <p>[LittLeLuck Attributes] Slider vertical track progress image properties.</p>
     */
    public static final String TRACK_VERTICAL_H = "Slider.trackVerticalHighlight";

    /**
     * <p>[LittleLuck属性]滑道大小, 默认值为8。</p>
     * 
     * <p>[LittLeLuck Attributes] Slider track size properties, default size 8.</p>
     */
    public static final String TRACK_SIZE = "Slider.trackSize";
    
    public void uninitialize()
    {
        UIManager.put(THUMB_HORIZONTAL, null);
        
        UIManager.put(THUMB_VERTICAL, null);
        
        UIManager.put(TRACK_HORIZONTAL, null);
        
        UIManager.put(TRACK_HORIZONTAL, null);
        
        UIManager.put(TRACK_HORIZONTAL_H, null);
        
        UIManager.put(TRACK_VERTICAL, null);
        
        UIManager.put(TRACK_VERTICAL_H, null);
        
        UIManager.put(TRACK_SIZE, null);
    }
    
    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(TICKCOLOR, getColorRes(131, 131, 131));
        
        table.put(BACKGROUND, getColorRes(Color.WHITE));
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        table.put(THUMB_HORIZONTAL, LuckRes.getImage("slider/thumb_h.png"));

        table.put(THUMB_VERTICAL, LuckRes.getImage("slider/thumb_v.png"));

        table.put(TRACK_HORIZONTAL, LuckRes.getImage("slider/track_horizontal.9.png"));

        table.put(TRACK_HORIZONTAL_H, LuckRes.getImage("slider/track_horizontal_h.9.png"));

        table.put(TRACK_VERTICAL, LuckRes.getImage("slider/track_vertical.9.png"));

        table.put(TRACK_VERTICAL_H, LuckRes.getImage("slider/track_vertical_h.9.png"));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        table.put(TRACK_SIZE, 8);
    }
}
