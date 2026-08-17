package org.jivesoftware.spark.phone.client.event;

/**
 * Dispatched when the user's call has ended.
 */
public class HangUpEvent {

    private final String callID;
    private final String device;

    public HangUpEvent(String callID, String device) {
        this.callID = callID;
        this.device = device;
    }

    public String getCallID() {
        return callID;
    }

    public String getDevice() {
        return device;
    }
}
