package freeseawind.lf.geom;

/**
 *
 * @author freeseawind@github
 * @version 1.0
 *
 * @param <T> 对象类型
 */
public class LuckProperty<T>
{
    private LuckPropertyType type;
    private T field;

    public LuckProperty(T field)
    {
        super();
        this.type = LuckPropertyType.BINDPARENT;
        this.field = field;
    }

    public LuckProperty(LuckPropertyType type, T field)
    {
        super();
        this.type = type;
        this.field = field;
    }

    public static <T> LuckProperty<T> getBindParentProp(T field)
    {
        return new LuckProperty<T>(LuckPropertyType.BINDPARENT, field);
    }

    public static <T> LuckProperty<T> getFxiProp(T field)
    {
        return new LuckProperty<T>(LuckPropertyType.FIX, field);
    }

    public void setType(LuckPropertyType type)
    {
        this.type = type;
    }

    public LuckPropertyType getType()
    {
        return type;
    }

    public T getField()
    {
        return field;
    }

    public void setField(T field)
    {
        this.field = field;
    }
    
    static public enum LuckPropertyType
    {
        BINDPARENT, FIX
    }
}
