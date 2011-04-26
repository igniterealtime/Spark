package org.jivesoftware.spark.ui;

/**
 * Interface for all chatroom that get triggered if another chatroom or part of spark changes if the chat frame should always stay on top
 * (Observer Pattern)
 * @author Holger Bergunde
 *
 */
public interface ChatFrameToFrontListener {
    
    /**
     * Update. From now on the chat frame will stay on top, or not
     * @param active
     */
    void updateStatus(boolean active);
    
    /**
     * the observer should register on this chatframe component
     * @param chatframe
     */
    void registeredToFrame(ChatFrame chatframe);
}
