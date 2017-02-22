package org.jivesoftware.spark.ui;

import javax.swing.text.*;

/**
 * A ViewFactory implementation to support line wrapping.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 * @see <a href="http://stackoverflow.com/questions/8666727/wrap-long-words-in-jtextpane-java-7">StackOverflow: Wrap long words in JTextPane (Java 7)</a>
 */
public class WrapColumnFactory implements ViewFactory
{
    public View create( Element elem )
    {
        String kind = elem.getName();
        if ( kind != null )
        {
            switch ( kind )
            {
                case AbstractDocument.ContentElementName:
                    return new WrapLabelView( elem );
                case AbstractDocument.ParagraphElementName:
                    return new ParagraphView( elem );
                case AbstractDocument.SectionElementName:
                    return new BoxView( elem, View.Y_AXIS );
                case StyleConstants.ComponentElementName:
                    return new ComponentView( elem );
                case StyleConstants.IconElementName:
                    return new IconView( elem );
            }
        }

        // default to text display
        return new LabelView( elem );
    }
}