package org.jivesoftware.spark.phone.client.action;

import org.jivesoftware.smack.packet.IqData;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.parsing.SmackParsingException;
import org.jivesoftware.smack.provider.IqProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jxmpp.JxmppContext;

import java.io.IOException;

/**
 * Parses the <tt>phone-action</tt> replies sent back by the Asterisk-IM Openfire plugin.
 */
public class PhoneActionIQProvider extends IqProvider<PhoneActionIQ> {

    @Override
    public PhoneActionIQ parse(XmlPullParser parser, int initialDepth, IqData iqData,
                               XmlEnvironment xmlEnvironment, JxmppContext context)
            throws XmlPullParserException, IOException, SmackParsingException {

        final String type = parser.getAttributeValue(null, "type");
        final PhoneActionIQ iq = new PhoneActionIQ(type != null ? type : PhoneActionIQ.DIAL);

        while (true) {
            XmlPullParser.Event event = parser.next();
            if (event == XmlPullParser.Event.START_ELEMENT) {
                switch (parser.getName()) {
                    case "extension":
                        iq.setExtension(parser.nextText());
                        break;
                    case "jid":
                        iq.setJid(parser.nextText());
                        break;
                    default:
                        break;
                }
            } else if (event == XmlPullParser.Event.END_ELEMENT && parser.getDepth() == initialDepth) {
                break;
            }
        }

        return iq;
    }
}
