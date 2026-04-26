package org.jivesoftware.sparkimpl.plugin.gateways.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;

import javax.xml.namespace.QName;

public class GatewayRegisterExtension implements ExtensionElement {
    public static final String ELEMENT_NAME = "x";
    public static final String NAMESPACE = "jabber:iq:gateway:register";
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT_NAME);

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String toXML(XmlEnvironment xmlEnvironment) {
        return "<" + getElementName() + " xmlns=\"" + getNamespace() + "\"/>";
    }
}
