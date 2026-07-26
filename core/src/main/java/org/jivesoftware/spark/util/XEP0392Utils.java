package org.jivesoftware.spark.util;

import org.jivesoftware.smackx.colors.ConsistentColor;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

import java.awt.Color;
import java.util.Map;
import java.util.WeakHashMap;

public class XEP0392Utils {
    private static final Map<Jid, Color> jidColors = new WeakHashMap<>(64);
    private static final Map<Resourcepart, Color> participantColors = new WeakHashMap<>(64);

    public static Color colorOfMucParticipant(Resourcepart userNickname) {
        return participantColors.computeIfAbsent(userNickname, it -> toColor(ConsistentColor.RGBFrom(it)));
    }

    public static Color colorOfMuc(EntityBareJid mucJid) {
        return getColorOfJid(mucJid);
    }

    public static Color colorOfContact(BareJid contactJid) {
        return getColorOfJid(contactJid);
    }

    private static Color getColorOfJid(Jid jid) {
        return jidColors.computeIfAbsent(jid, it -> toColor(ConsistentColor.RGBFrom(it)));
    }

    private static Color toColor(float[] rgb) {
        // avoid negative values
        rgb[0] = Math.max(0, rgb[0]);
        rgb[1] = Math.max(0, rgb[1]);
        rgb[2] = Math.max(0, rgb[2]);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }
}
