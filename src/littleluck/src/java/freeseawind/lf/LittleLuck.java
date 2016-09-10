package freeseawind.lf;

import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResConfig;
import freeseawind.lf.cfg.LuckResConfigImpl;
import freeseawind.lf.cfg.LuckUIConfig;
import freeseawind.lf.cfg.LuckUIConfigImpl;
import freeseawind.lf.constant.LuckSystemConstant;
import freeseawind.lf.utils.LuckPlatformUtils;
import freeseawind.lf.utils.LuckRes;

/**
 * littleluck主题帮助类，建议使用该类来加载littleluck
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LittleLuck
{
    private static final LittleLuck singleton = new LittleLuck();
    private LuckUIConfig uiConfig;
    private LuckResConfig resConfig;

    private LittleLuck()
    {
        initLookAndFeelParam();
        
        initConfig();
    }

    public static LittleLuck getSingleton()
    {
        return singleton;
    }

    public void luanchLookAndFeel() throws Exception
    {
        luanchMetalLookAndFeel();
    }

    /**
     * 加载跨平台观感
     * 
     * @throws Exception 加载观感失败
     */
    public void luanchMetalLookAndFeel() throws Exception
    {
        UIManager.setLookAndFeel(LuckMetalLookAndFeel.class.getName());

        setPlatformFont();
    }
    
    /**
     * 设置全局字体
     * @param f 字体信息对象
     */
    public void setApplicationFont(Font f)
    {
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();

        synchronized (defaults)
        {
            for (Object ui_property : defaults.keySet())
            {
                if (ui_property.toString().endsWith(".font"))
                {
                    UIManager.put(ui_property, f);
                }
            }
        }
    }

    protected void initLookAndFeelParam()
    {
        // 该参数用于解决中文输入法白屏问题
        System.setProperty("sun.java2d.noddraw", "true");

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        JFrame.setDefaultLookAndFeelDecorated(true);

        JDialog.setDefaultLookAndFeelDecorated(true);
    }
    
    protected void initConfig()
    {
        if(uiConfig == null)
        {
            uiConfig = new LuckUIConfigImpl();
        }
        
        if(resConfig == null)
        {
            resConfig = new LuckResConfigImpl();
        }
    }
    
    /**
     * 设置平台字体，如果是Windows平台则设置为微软雅黑
     */
    private void setPlatformFont()
    {
        if(LuckPlatformUtils.isWindows())
        {
            String fontName = LuckRes.getString(LuckSystemConstant.DEF_WINDOWS_FONT);

            setApplicationFont(new Font(fontName, Font.PLAIN, 12));
        }
    }

    public LuckUIConfig getUiConfig()
    {
        return uiConfig;
    }

    public void setUiConfig(LuckUIConfig uiConfig)
    {
        this.uiConfig = uiConfig;
    }

    public LuckResConfig getResConfig()
    {
        return resConfig;
    }

    public void setResConfig(LuckResConfig resConfig)
    {
        this.resConfig = resConfig;
    }
}
