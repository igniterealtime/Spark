package freeseawind.lf.geom;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import freeseawind.lf.geom.LuckProperty.LuckPropertyType;

/**
 * 自定义矩形类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRectangle
{
    private LuckProperty<Integer> startxProp;
    private LuckProperty<Integer> startyProp;
    private LuckProperty<Integer> heightProp;
    private LuckProperty<Integer> widthProp;
    private Rectangle rect;
    private JComponent parent;

    public LuckRectangle(JComponent parent)
    {
        this(new LuckProperty<Integer>(0), new LuckProperty<Integer>(0),
                new LuckProperty<Integer>(0), new LuckProperty<Integer>(0),
                parent);
    }

    public LuckRectangle(LuckProperty<Integer> startxProp,
                         LuckProperty<Integer> startyProp,
                         LuckProperty<Integer> widthProp,
                         LuckProperty<Integer> heightProp,
                         JComponent parent)
    {
        super();
        this.startxProp = startxProp;
        this.startyProp = startyProp;
        this.heightProp = heightProp;
        this.widthProp = widthProp;
        this.parent = parent;

        int x = startxProp.getField();
        int y = startyProp.getField();
        int w = widthProp.getField();
        int h = heightProp.getField();

        this.rect = new Rectangle(x, y, w, h);
    }

    public void updateFrame(int startX, int startY, int height, int width)
    {
        updateProp(startxProp, startX);

        updateProp(startyProp, startY);

        updateProp(heightProp, height);

        updateProp(widthProp, width);

        updateLocation();

        updateDeimension(height, width);
    }

    public void updateDeimension(int height, int width)
    {
        updateProp(heightProp, height);

        updateProp(widthProp, width);

        updateDimension();
    }

    public void updateLocation(int startX, int startY)
    {
        updateProp(startxProp, startX);

        updateProp(startyProp, startY);

        updateLocation();
    }

    public boolean contains(Point point)
    {
        rect.setFrame(getStartX(), getStartY(), getWidth(), getHeight());

        return rect.contains(point);
    }

    private void updateProp(LuckProperty<Integer> prop, int val)
    {
        if(prop.getType() != LuckPropertyType.BINDPARENT)
        {
            prop.setField(val);
        }
    }

    private void updateLocation()
    {
        if(startxProp.getType() != LuckPropertyType.BINDPARENT)
        {
            rect.x = getStartX();
        }

        if(startyProp.getType() != LuckPropertyType.BINDPARENT)
        {
            rect.y = getStartX();
        }
    }

    private void updateDimension()
    {
        if(heightProp.getType() != LuckPropertyType.BINDPARENT)
        {
            rect.width = getWidth();
        }

        if(widthProp.getType() != LuckPropertyType.BINDPARENT)
        {
            rect.height = getHeight();
        }
    }

    public int getStartX()
    {
        if(startxProp.getType() == LuckPropertyType.BINDPARENT)
        {
            return parent.getInsets().left;
        }

        return startxProp.getField().intValue();
    }

    public int getStartY()
    {
        if(startyProp.getType() == LuckPropertyType.BINDPARENT)
        {
            return parent.getInsets().top;
        }

        return startyProp.getField().intValue();
    }

    public int getHeight()
    {
        if(heightProp.getType() == LuckPropertyType.BINDPARENT)
        {
            return parent.getHeight();
        }

        return heightProp.getField().intValue();
    }

    public int getWidth()
    {
        if(widthProp.getType() == LuckPropertyType.BINDPARENT)
        {
            return parent.getWidth();
        }

        return widthProp.getField().intValue();
    }
}
