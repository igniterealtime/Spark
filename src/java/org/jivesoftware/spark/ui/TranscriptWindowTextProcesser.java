package org.jivesoftware.spark.ui;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by bona on 2015/8/14.
 */
public interface TranscriptWindowTextProcesser {
    /**
     * Is called before chatroom text by this user is inserted into the TranscriptWindow.
     *
     * @param window  the TranscriptWindow.
     * @param text  the text
     * @return true if it should be handled by processed.
     */
    boolean isProcessed(TranscriptWindow window, String text);
}
