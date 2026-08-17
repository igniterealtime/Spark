package org.jivesoftware.spark.phone.client.event;

import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.parsing.SmackParsingException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jxmpp.JxmppContext;

import java.io.IOException;

/**
 * Parses the <tt>phone-event</tt> extension sent by the Asterisk-IM Openfire plugin.
 */
public class PhoneEventPacketExtensionProvider extends ExtensionElementProvider<PhoneEventExtension> {

    @Override
    public PhoneEventExtension parse(XmlPullParser parser, int initialDepth,
                                     XmlEnvironment xmlEnvironment, JxmppContext context)
            throws XmlPullParserException, IOException, SmackParsingException {

        final String type = parser.getAttributeValue(null, "type");
        final String callID = parser.getAttributeValue(null, "callID");
        final String device = parser.getAttributeValue(null, "device");

        String callerID = null;
        String callerIDName = null;

        while (true) {
            XmlPullParser.Event event = parser.next();
            if (event == XmlPullParser.Event.START_ELEMENT) {
                switch (parser.getName()) {
                    case "callerID":
                        callerID = parser.nextText();
                        break;
                    case "callerIDName":
                        callerIDName = parser.nextText();
                        break;
                    default:
                        // 'extension' and anything else the server may add: ignore.
                        break;
                }
            } else if (event == XmlPullParser.Event.END_ELEMENT && parser.getDepth() == initialDepth) {
                break;
            }
        }

        return new PhoneEventExtension(type, callID, device, callerID, callerIDName);
    }
}
