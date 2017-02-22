package org.jivesoftware.spark.ui;

import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.View;

/**
 * A LabelView implementation to support line wrapping.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 * @see <a href="http://stackoverflow.com/questions/8666727/wrap-long-words-in-jtextpane-java-7">StackOverflow: Wrap long words in JTextPane (Java 7)</a>
 */
public class WrapLabelView extends LabelView
{
    public WrapLabelView( Element elem )
    {
        super( elem );
    }

    public float getMinimumSpan( int axis )
    {
        switch ( axis )
        {
            case View.X_AXIS:
                return 0;
            case View.Y_AXIS:
                return super.getMinimumSpan( axis );
            default:
                throw new IllegalArgumentException( "Invalid axis: " + axis );
        }
    }
}