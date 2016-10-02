package freeseawind.lf.canvas;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * <pre>
 * 绘图回调接口申明
 * 
 * draw callback interface.
 * </pre>
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public interface LuckCanvas
{
    /**
     *
     * @param g <code>Graphics</code>
     *
     * @param c <code>JComponent</code>
     *
     */
	public void drawComponent(Graphics g, JComponent c);
}
