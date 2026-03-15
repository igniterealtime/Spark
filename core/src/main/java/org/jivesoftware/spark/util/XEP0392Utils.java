package org.jivesoftware.spark.util;

import org.jivesoftware.smackx.colors.ConsistentColor;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.parts.Resourcepart;

import java.awt.*;

public class XEP0392Utils {

    public static Color colorOfMucParticipant(Resourcepart userNickname) {
        return toColor(ConsistentColor.RGBFrom(userNickname));
    }

    public static Color colorOfMuc(EntityBareJid mucJid) {
        return toColor(ConsistentColor.RGBFrom(mucJid));
    }

    private static Color toColor(float[] rgb) {
        // avoid negative values
        rgb[0] = Math.max(0, rgb[0]);
        rgb[1] = Math.max(0, rgb[1]);
        rgb[2] = Math.max(0, rgb[2]);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }
}
