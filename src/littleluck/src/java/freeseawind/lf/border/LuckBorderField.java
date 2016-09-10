package freeseawind.lf.border;

import java.awt.geom.RectangularShape;

/**
 * 边框属性接口
 *
 * @author freeseawind@github
 * @version 1.0
 */
public interface LuckBorderField
{
    /**
     * 设置组件焦点状态
     *
     * @param isFoucusGaind 有焦点则true，否则false
     */
    public void setFocusGained(boolean isFoucusGaind);

    /**
     * 判断组件是否获取焦点
     *
     * @return 有焦点则返回true，否则返回false
     */
    public boolean isFoucusGaind();

    /**
     * 获取边框形状
     *
     * @return <code>RectangularShape</code>
     */
    public RectangularShape getBorderShape();
}
