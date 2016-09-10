package freeseawind.lf.utils;

import java.awt.Toolkit;
import java.awt.event.InputEvent;

public class LuckUtils
{
    public static boolean isMenuShortcutKeyDown(InputEvent event)
    {
        return (event.getModifiers()
                & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
    }
}
