package freeseawind.lf.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

/**
 * <p>
 * 布局类，重写所有方法，需要时再覆盖，避免每次继承{@link LayoutManager2}时出来一大堆方法
 * </p>
 * 
 * <p>
 * {@link LayoutManager2} sub class, override all method. someone extend it,
 * only override method you need.
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class AbstractLayout implements LayoutManager2
{
    public void addLayoutComponent(String name, Component comp)
    {

    }

    public void removeLayoutComponent(Component comp)
    {

    }

    public Dimension preferredLayoutSize(Container parent)
    {
        return null;
    }

    public Dimension minimumLayoutSize(Container parent)
    {
        return null;
    }

    public void layoutContainer(Container parent)
    {

    }

    public void addLayoutComponent(Component comp, Object constraints)
    {

    }

    public Dimension maximumLayoutSize(Container target)
    {
        return null;
    }

    public float getLayoutAlignmentX(Container target)
    {
        return 0;
    }

    public float getLayoutAlignmentY(Container target)
    {
        return 0;
    }

    public void invalidateLayout(Container target)
    {

    }

    /**
     * 获取布局容器大小
     *
     * @param inset 容器间距
     * @param w 容器宽度
     * @param h 容器高度
     * @return <code>Dimension</code>
     */
    protected Dimension getDimension(Insets inset, int w, int h)
    {
        return new Dimension(w + inset.left + inset.right, h + inset.top + inset.bottom);
    }
}
