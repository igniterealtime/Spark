package freeseawind.ninepatch.common;


/**
 * 区间类
 * @author freeseawind@github
 *
 */
public class Region
{
    private int start;
    private int end;

    public Region(int start, int end)
    {
        super();
        this.start = start;
        this.end = end;
    }

    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
    }

    @Override
    public String toString()
    {
        return "Region [start=" + start + ", end=" + end + "]";
    }
}
