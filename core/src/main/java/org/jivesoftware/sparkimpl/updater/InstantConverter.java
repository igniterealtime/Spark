package org.jivesoftware.sparkimpl.updater;

import com.thoughtworks.xstream.converters.SingleValueConverter;

import java.time.Instant;

public class InstantConverter implements SingleValueConverter {

    @Override
    public boolean canConvert(Class type) {
        return type.equals(Instant.class);
    }

    @Override
    public String toString(Object obj) {
        Instant instant = (Instant) obj;
        return String.valueOf(instant.toEpochMilli());
    }

    @Override
    public Object fromString(String str) {
        long epochMillis = Long.parseLong(str);
        return Instant.ofEpochMilli(epochMillis);
    }
}
