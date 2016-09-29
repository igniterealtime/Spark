package freeseawind.lf.canvas;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * draw callback interface.
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public interface LuckCanvas
{
    /**
     *
     * @param g Graphics
     *
     * @param c JComponent
     *
     */
	public void drawComponent(Graphics g, JComponent c);
}
