package freeseawind.swing;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckGlobalBundle;

public class TranslucentPopup extends Popup
{
    private JWindow popupWindow;
    
    TranslucentPopup(Component owner, Component contents, int ownerX, int ownerY) 
    { 
        this.popupWindow = new JWindow();
        popupWindow.setBackground(UIManager.getColor(LuckGlobalBundle.TRANSLUCENT_COLOR));
        popupWindow.setLocation(ownerX, ownerY); 
        popupWindow.getContentPane().add(contents, BorderLayout.CENTER); 
        contents.invalidate(); 
    }

    @Override
    public void show()
    {
        this.popupWindow.setVisible(true);
        this.popupWindow.pack();
    }

    @Override
    public void hide()
    {
        this.popupWindow.setVisible(false);
        this.popupWindow.removeAll();
        this.popupWindow.dispose();
    }
}
