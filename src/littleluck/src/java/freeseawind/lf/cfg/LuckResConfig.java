package freeseawind.lf.cfg;

import javax.swing.UIDefaults;

/**
 * UI resource bundle interface.
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public interface LuckResConfig
{
    /**
     * load resource when install LookAndFeel
     * 
     * @param table
     */
    public void loadResources(UIDefaults table);
    
    /**
     * remove resource when Uninstall LookAndFeel
     */
    public void removeResource();
}
