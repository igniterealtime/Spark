package org.jivesoftware.spark.phone.client.action;

import org.jivesoftware.smack.packet.IQ;

/**
 * The <tt>phone-action</tt> IQ understood by the Asterisk-IM Openfire plugin.
 * <p>
 * Dialling is a 'set' carrying either an extension or a JID:
 * <pre>
 * &lt;iq type="set"&gt;
 *     &lt;phone-action xmlns="http://jivesoftware.com/xmlns/phone" type="DIAL"&gt;
 *         &lt;extension&gt;601&lt;/extension&gt;
 *     &lt;/phone-action&gt;
 * &lt;/iq&gt;
 * </pre>
 * Note that the plugin compares the 'type' attribute against the uppercase enum name, so
 * "DIAL" is required — "dial" is answered with feature-not-implemented.
 */
public class PhoneActionIQ extends IQ {

    public static final String ELEMENT_NAME = "phone-action";
    public static final String NAMESPACE = "http://jivesoftware.com/xmlns/phone";

    public static final String DIAL = "DIAL";
    public static final String FORWARD = "FORWARD";

    private final String actionType;
    private String extension;
    private String jid;

    public PhoneActionIQ(String actionType) {
        super(ELEMENT_NAME, NAMESPACE);
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("type", actionType);
        xml.rightAngleBracket();
        xml.optElement("extension", extension);
        xml.optElement("jid", jid);
        return xml;
    }
}
