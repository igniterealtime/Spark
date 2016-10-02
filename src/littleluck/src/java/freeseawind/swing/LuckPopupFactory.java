package freeseawind.swing;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

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
    public LuckPopupFactory()
    {
        
    }
    
    @Override
    public Popup getPopup(Component owner, Component contents, int x, int y)
        throws IllegalArgumentException
    {
        Popup popup = super.getPopup(owner, contents, x, y);
        
        // 比较安全的hack方式
        Object obj = SwingUtilities.getWindowAncestor(contents);

        if (obj instanceof JWindow)
        {
            JWindow window = (JWindow) obj;

            // 承载内容的窗体透明
            window.setBackground(UIManager.getColor(LuckGlobalBundle.TRANSLUCENT_COLOR));

            ((JComponent) window.getContentPane()).setOpaque(false);
            
            JdkVersion version = JdkVersion.getSingleton();
            
            boolean isCompatible = (version.getMajor() <= 1 && version.getMinor() < 8);
            
            if (contents instanceof JPopupMenu && isCompatible)
            {
                boolean isFound = false;
                
                for (ComponentListener listener : window.getComponentListeners())
                {
                    if(listener instanceof LuckPopupComponentListener)
                    {
                        isFound = true;
                        
                        break;
                    }
                }
                
                if(!isFound)
                {
                    window.addComponentListener(new LuckPopupComponentListener());
                }
            }
        }

        return popup;
    }
    
    class LuckPopupComponentListener extends ComponentAdapter
    {
        @Override
        public void componentShown(ComponentEvent e)
        {
            Object obj = e.getSource();
            
            if(obj instanceof JWindow)
            {
                JWindow window = (JWindow) obj;
                
                window.repaint();
            }
        }
    }
}
