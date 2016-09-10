package freeseawind.ninepatch.common;

/**
 * 内容显示区域间距控制类
 * @author freeseawind@github
 *
 */
public class Padding
{
    private int left;
    private int top;
    private int right;
    private int bottom;

    public Padding(int left, int top, int right, int bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft()
    {
        return left;
    }

    public void setLeft(int left)
    {
        this.left = left;
    }

    public int getTop()
    {
        return top;
    }

    public void setTop(int top)
    {
        this.top = top;
    }

    public int getRight()
    {
        return right;
    }

    public void setRight(int right)
    {
        this.right = right;
    }

    public int getBottom()
    {
        return bottom;
    }

    public void setBottom(int bottom)
    {
        this.bottom = bottom;
    }

    @Override
    public String toString()
    {
        return "ContentPadding [left=" + left + ", top=" + top + ", right="
                + right + ", bottom=" + bottom + "]";
    }
}
