package org.jivesoftware.spark.ui;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

/**
 * An EditorKit implementation to support line wrapping.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 * @see <a href="http://stackoverflow.com/questions/8666727/wrap-long-words-in-jtextpane-java-7">StackOverflow: Wrap long words in JTextPane (Java 7)</a>
 */
public class WrapEditorKit extends StyledEditorKit
{
    private final ViewFactory defaultFactory = new WrapColumnFactory();

    public ViewFactory getViewFactory()
    {
        return defaultFactory;
    }
}