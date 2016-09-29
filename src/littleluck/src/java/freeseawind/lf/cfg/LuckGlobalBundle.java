package freeseawind.lf.cfg;

import java.awt.Color;

import javax.swing.UIManager;

/**
 * Global resource bundle.
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckGlobalBundle extends LuckResourceBundle
{
    /**
     * [custom field] application icon key.
     */
    public static final String APPLICATION_ICON = "Application.icon";

    /**
     * [custom field] application title key.
     */
    public static final String APPLICATION_TITLE = "Application.title";

    /**
     * [custom field] translucent color key.
     */
    public static final String TRANSLUCENT_COLOR = "translucent.color";

    /**
     * panel background key.
     */
    public static final String PANEL_BACKGROUND = "Panel.background";

    /**
     * ColorChooserUI background key.
     */
    public static final String COLORCHOOSERUI_BACKGROUND = "ColorChooserUI.background";

    @Override
    protected void installColor()
    {
        UIManager.put(TRANSLUCENT_COLOR, new Color(0, 0, 0, 0));
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(APPLICATION_ICON, getIconRes("frame/default_frame_icon.png"));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(APPLICATION_TITLE, "");

        UIManager.put(PANEL_BACKGROUND, Color.WHITE);

        UIManager.put(COLORCHOOSERUI_BACKGROUND, Color.WHITE);
    }
}
