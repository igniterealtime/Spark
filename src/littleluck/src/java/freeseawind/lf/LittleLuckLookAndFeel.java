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
 * <p>Java跨平台观感实现类。</p>
 * <p>The Java Look and Feel, otherwise known as LittleLuck.</p>
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LittleLuckLookAndFeel extends MetalLookAndFeel
{
    private static final long serialVersionUID = -7537863322955102478L;
    private static final String BUNDLENAME = "com.github.freeseawind.littleluck";
    private LuckUIConfig uiConfig;
    private LuckResConfig resConfig;
    
    static
    {
        // setting before launch LookandFeel
        // set java2d parameter and application font
        initLookAndFeelParam();
    }

    public LittleLuckLookAndFeel()
    {
        super();
        
        initConfig();
        
        setPlatformFont();
    }
    
    public void uninitialize()
    {
        super.uninitialize();
        
        UIManager.getDefaults().removeResourceBundle(BUNDLENAME);
        
        resConfig.removeResource();
    }

    /**
     * {@inheritDoc}
     */
    protected void initComponentDefaults(UIDefaults table)
    {
        super.initComponentDefaults(table);
        
        initResourceBundle(table);

        if (resConfig != null)
        {
            // load UI resource
            resConfig.loadResources(table);
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
     * Initialize the defaults table with the name of the other ResourceBundle
     * used for getting localized defaults.
     */
    protected void initResourceBundle(UIDefaults table)
    {
        table.addResourceBundle(BUNDLENAME);
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
     * set platform font, if windows platform use "Microsoft YaHei".
     */
    public void setPlatformFont()
    {
        if(LuckPlatformUtils.isWindows())
        {
            String fontName = "Microsoft YaHei";

            setApplicationFont(new Font(fontName, Font.PLAIN, 12));
        }
    }
    
    /**
     * initialization LookAndFeel UI and UI resource class bundle.
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
     * setting global font.
     * 
     * @param f Font object
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

    public boolean getSupportsWindowDecorations()
    {
        return true;
    }
    
    public String getName()
    {
        return "LittleLuckLookAndFeel";
    }

    public String getDescription()
    {
        return "The LittleLuck cross platform Look and Feel";
    }

    public String getID()
    {
        return "LittleLuck";
    }
}
