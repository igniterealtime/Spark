package org.jivesoftware.spark.phone.client.event;

public interface BasePhoneEventListener {
    void handleOnPhone(OnPhoneEvent event);

    void handleHangUp(HangUpEvent event);

    void handleRing(RingEvent event);
}
