package freeseawind.lf.utils;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import javax.swing.UIManager;

import freeseawind.ninepatch.common.RepeatType;
import freeseawind.ninepatch.swing.SwingNinePatch;

public class LuckUtils
{
    public static boolean isMenuShortcutKeyDown(InputEvent event)
    {
        return (event.getModifiers()
                & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
    }
    
    public static BufferedImage getUiImage(String imageKey)
    {
        return (BufferedImage) UIManager.get(imageKey);
    }
    
    public static SwingNinePatch createNinePatch(String imageKey)
    {
        return new SwingNinePatch(getUiImage(imageKey));
    }
    
    public static SwingNinePatch createNinePatch(String imageKey, RepeatType type)
    {
        return new SwingNinePatch(getUiImage(imageKey), type);
    }
}
