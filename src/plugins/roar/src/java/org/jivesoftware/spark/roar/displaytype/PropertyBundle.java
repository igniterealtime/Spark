package org.jivesoftware.spark.roar.displaytype;

import java.awt.Color;

/**
 * PropertyBundle passed to implementing classes of RoarDisplayType
 * 
 * @author wolf.posdorfer
 */
public class PropertyBundle {

    public Color backgroundColor;
    public Color headerColor;
    public Color textColor;
    public int duration;

    public PropertyBundle(Color backgroundColor, Color headerColor, Color textColor, int duration) {
        this.backgroundColor = backgroundColor;
        this.headerColor = headerColor;
        this.textColor = textColor;
        this.duration = duration;
    }

}
