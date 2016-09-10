package freeseawind.swing;

import java.awt.Component;
import java.awt.Window;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckGlobalBundle;

/**
 * 透明背景popup工厂方法
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckPopupFactory extends PopupFactory
{
    private Method method;
    private PopupFactory oldFactory;
    
    public LuckPopupFactory()
    {
        try
        {
            Class<?> cls =  PopupFactory.class;
            
            Class<?>[] types = new Class[]{Component.class, Component.class, int.class, int.class, int.class};
            
            method = cls.getDeclaredMethod("getPopup", types);
            
            method.setAccessible(true);
            
            oldFactory = new PopupFactory();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public Popup getPopup(Component owner, Component contents, int x, int y)
        throws IllegalArgumentException
    {
        Popup popup = null;
        
        if(contents instanceof JPopupMenu && isHeavyWeight(owner))
        {
            try
            {
                Object obj = method.invoke(oldFactory, new Object[]{owner, contents, x, y, 0});
                
                if(obj instanceof Popup)
                {
                    popup = (Popup)obj;
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        if(popup == null)
        {
            popup = super.getPopup(owner, contents, x, y);
        }
        
        // 比较安全的hack方式
        Object obj = SwingUtilities.getWindowAncestor(contents);

        if (obj instanceof JWindow)
        {
            JWindow window = (JWindow) obj;

            // 承载内容的窗体透明
            window.setBackground(UIManager.getColor(LuckGlobalBundle.TRANSLUCENT_COLOR));

            ((JComponent) window.getContentPane()).setOpaque(false);
        }

        return popup;
    }
    
    public boolean isHeavyWeight(Component owner)
    {
        Component c = owner;

        while (c != null)
        {
            if (c instanceof Window)
            {
                Window w = (Window) c;

                if (!w.isOpaque() || w.getOpacity() < 1 || w.getShape() != null)
                {
                    return true;
                }
            }
            
            c = c.getParent();
        }

        return false;
    }
}
