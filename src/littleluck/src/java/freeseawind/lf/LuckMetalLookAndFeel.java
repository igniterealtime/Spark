package freeseawind.lf;

import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import freeseawind.lf.cfg.LuckResConfig;
import freeseawind.lf.cfg.LuckResConfigImpl;
import freeseawind.lf.cfg.LuckUIConfig;
import freeseawind.lf.cfg.LuckUIConfigImpl;
import freeseawind.lf.utils.LuckPlatformUtils;

/**
 * littlelcuk LookAndFeel implement.
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckMetalLookAndFeel extends MetalLookAndFeel
{
    private static final long serialVersionUID = -7537863322955102478L;
    private LuckUIConfig uiConfig;
    private LuckResConfig resConfig;
    
    static
    {
        // setting before load LookandFeel
        // set java2d parameter and application font
        
        initLookAndFeelParam();
        
        setPlatformFont();
    }

    public LuckMetalLookAndFeel()
    {
        super();
        
        initConfig();
    }

    /**
     * {@inheritDoc}
     */
    protected void initComponentDefaults(UIDefaults table)
    {
        super.initComponentDefaults(table);

        if (resConfig != null)
        {
            // load UI resource
            resConfig.loadResources();
        }
    }

    protected void initClassDefaults(UIDefaults table)
    {
        super.initClassDefaults(table);

        if(uiConfig != null)
        {
            // set LookAndFeel UI
            uiConfig.initClassDefaults(table);
        }
    }
    
    /**
     * set variable before LookAndFeel load
     */
    public static void initLookAndFeelParam()
    {
        //  solve the problem of the white screen 
        //  by switching the Chinese input method of the translucent window
        System.setProperty("sun.java2d.noddraw", "true");

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        JFrame.setDefaultLookAndFeelDecorated(true);

        JDialog.setDefaultLookAndFeelDecorated(true);
    }
    
    /**
     * set platform font, if windows use "Microsoft YaHei"
     */
    public static void setPlatformFont()
    {
        if(LuckPlatformUtils.isWindows())
        {
            String fontName = "Microsoft YaHei";

            setApplicationFont(new Font(fontName, Font.PLAIN, 12));
        }
    }
    
    /**
     * set littleluck LookAndFeel UI class and UI resource class.
     */
    public void initConfig()
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
     * setting global font
     * 
     * @param f Font object
     */
    public static void setApplicationFont(Font f)
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

    public boolean getSupportsWindowDecorations()
    {
        return true;
    }
    
    public String getName()
    {
        return "LuckMetalLookAndFeel";
    }

    public String getDescription()
    {
        return "The littleluck cross platform Look and Feel";
    }

    public String getID()
    {
        return "littleluck";
    }
}
