package freeseawind.lf.canvas;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * 绘图回调接口申明
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public interface LuckCanvas
{
    /**
     * 绘图回调方法
     *
     * @param g Graphics图形操作对象
     *
     * @param c JComponent需要绘制的容器类
     *
     */
	public void drawComponent(Graphics g, JComponent c);
}
