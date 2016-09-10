package freeseawind.ninepatch.common;

import java.awt.Rectangle;

/**
 * 九宫格的每一列
 * @author freeseawind@github
 *
 */
public class Row implements Comparable<Row>
{
    private Rectangle rect;
    private Type type;

    public Row(Rectangle rect, Type type)
    {
        super();
        this.rect = rect;
        this.type = type;
    }

    public Rectangle getRectangle()
    {
        return rect;
    }

    public Type getType()
    {
        return type;
    }

    public int compareTo(Row o)
    {
        return Integer.compare(getRectangle().x, o.getRectangle().x);
    }

    @Override
    public String toString()
    {
        return "Row [row=" + rect + ", type=" + type + "]";
    }

    static enum Type
    {
        FIX, // 固定类型
        HORIZONTALPATCH, // 水平拉伸类型
        VERTICALPATCH, // 垂直拉伸类型
        TILEPATCH // 平铺类型
    }


}
