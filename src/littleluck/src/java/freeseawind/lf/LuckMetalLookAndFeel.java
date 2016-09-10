package freeseawind.lf;

import javax.swing.UIDefaults;
import javax.swing.plaf.metal.MetalLookAndFeel;

import freeseawind.lf.cfg.LuckResConfig;
import freeseawind.lf.cfg.LuckUIConfig;

/**
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckMetalLookAndFeel extends MetalLookAndFeel
{
    private static final long serialVersionUID = -7537863322955102478L;

    public LuckMetalLookAndFeel()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected void initComponentDefaults(UIDefaults table)
    {
        super.initComponentDefaults(table);

        LuckResConfig resConfig = LittleLuck.getSingleton().getResConfig();

        if (resConfig != null)
        {
            resConfig.loadResources();
        }
    }

    protected void initClassDefaults(UIDefaults table)
    {
        super.initClassDefaults(table);

        LuckUIConfig uiConfig = LittleLuck.getSingleton().getUiConfig();

        if(uiConfig != null)
        {
            uiConfig.initClassDefaults(table);
        }
    }

    public boolean getSupportsWindowDecorations()
    {
        return true;
    }
    
    public String getName()
    {
        return "LittleLuckMetalLookAndFeel";
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
