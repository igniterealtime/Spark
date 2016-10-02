package freeseawind.lf.border;

import java.awt.geom.RectangularShape;

/**
 * A Border field interface.
 *
 * @author freeseawind@github
 * @version 1.0
 */
public interface LuckBorderField
{
    /**
     * set component is FoucusGaind. 
     *
     * @param isFoucusGaind 
     */
    public void setFocusGained(boolean isFoucusGaind);

    /**
     * get component focus state.
     *
     * @return
     */
    public boolean isFocusGaind();

    /**
     * get component border shape.
     *
     * @return <code>RectangularShape</code>
     */
    public RectangularShape getBorderShape();
}
