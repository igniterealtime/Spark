package org.jivesoftware.spark.util;

import org.jivesoftware.spark.SparkManager;
import org.jxmpp.jid.EntityJid;

import java.io.File;

public class JidUtils {
    public static File jidAsFileName(EntityJid jid, String suffix) {
        // contact: tybalt@capulet.lit
        //  chat: room@chat.capulet.lit
        // 1-1 PM in chat: room@chat.capulet.lit%2fTybalt
        String fileJid = jid.asUrlEncodedString().replace("%40", "@");
        return new File(SparkManager.getTranscriptDir(), fileJid + suffix);
    }
}
