package org.jivesoftware.spark.phone.client.event;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * The <tt>phone-event</tt> extension sent by the Asterisk-IM Openfire plugin.
 * <p>
 * The plugin pushes these to a user whenever one of their mapped devices changes state:
 * <pre>
 * &lt;phone-event xmlns="http://jivesoftware.com/xmlns/phone" type="RING" callID="..." device="SIP/2001"&gt;
 *     &lt;callerID&gt;2002&lt;/callerID&gt;
 *     &lt;callerIDName&gt;Bob&lt;/callerIDName&gt;
 * &lt;/phone-event&gt;
 * </pre>
 */
public class PhoneEventExtension implements ExtensionElement {

    /**
     * Smack resolves an extension class to a QName through a static QNAME field, falling back
     * to ELEMENT + NAMESPACE. Without these, Stanza#getExtensions(Class) throws
     * NoSuchFieldException at runtime. ELEMENT_NAME is kept as the name this plugin has always
     * used internally.
     */
    public static final String ELEMENT = "phone-event";
    public static final String ELEMENT_NAME = ELEMENT;
    public static final String NAMESPACE = "http://jivesoftware.com/xmlns/phone";
    public static final javax.xml.namespace.QName QNAME =
            new javax.xml.namespace.QName(NAMESPACE, ELEMENT);

    /** Event types used by the Asterisk-IM plugin, as they appear in the 'type' attribute. */
    public static final String RING = "RING";
    public static final String ON_PHONE = "ON_PHONE";
    public static final String HANG_UP = "HANG_UP";
    public static final String DIALED = "DIALED";

    private final String type;
    private final String callID;
    private final String device;
    private final String callerID;
    private final String callerIDName;

    public PhoneEventExtension(String type, String callID, String device, String callerID,
                               String callerIDName) {
        this.type = type;
        this.callID = callID;
        this.device = device;
        this.callerID = callerID;
        this.callerIDName = callerIDName;
    }

    public String getType() {
        return type;
    }

    public String getCallID() {
        return callID;
    }

    public String getDevice() {
        return device;
    }

    public String getCallerID() {
        return callerID;
    }

    public String getCallerIDName() {
        return callerIDName;
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public XmlStringBuilder toXML(XmlEnvironment xmlEnvironment) {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.attribute("type", type);
        xml.optAttribute("callID", callID);
        xml.optAttribute("device", device);
        xml.rightAngleBracket();
        xml.optElement("callerID", callerID);
        xml.optElement("callerIDName", callerIDName);
        xml.closeElement(this);
        return xml;
    }
}
