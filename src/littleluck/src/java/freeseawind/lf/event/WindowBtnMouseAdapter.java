package freeseawind.lf.event;

import java.awt.event.MouseAdapter;

import javax.swing.JButton;
import javax.swing.UIManager;

/**
 * 窗体按钮鼠标事件适配器
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class WindowBtnMouseAdapter extends MouseAdapter
{
    protected String normalIconKey;
    protected String hoverIconKey;
    protected String pressIconKey;
    protected JButton btn;

    public WindowBtnMouseAdapter(JButton btn,
                                 String normalIconKey,
                                 String hoverIconKey,
                                 String pressIconKey)
    {
        super();
        this.btn = btn;
        this.normalIconKey = normalIconKey;
        this.hoverIconKey = hoverIconKey;
        this.pressIconKey = pressIconKey;
        
        resetIcon();
    }
    
    protected void resetIcon()
    {
        btn.setIcon(UIManager.getIcon(normalIconKey));
        btn.setRolloverIcon(UIManager.getIcon(hoverIconKey));
        btn.setPressedIcon(UIManager.getIcon(pressIconKey));
    }
}