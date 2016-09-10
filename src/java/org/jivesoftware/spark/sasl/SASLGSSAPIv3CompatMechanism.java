package org.jivesoftware.spark.sasl;

import org.jivesoftware.smack.sasl.javax.SASLGSSAPIMechanism;

/**
 * This is an implementation of a SASL GSS-API mechanisms, that uses the XMPP domain name, instead of the fully
 * qualified domain name of the XMPP server that is being connected to.
 *
 * This implementation reverts the GSS-API SASL mechanism behavior back to what it was in Smack 3, which can facilitate
 * running Smack-3- and Smack-4-based clients on the same Openfire instance (Openfire will be configured to require
 * either the XMPP domain name, or the FQDN).
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class SASLGSSAPIv3CompatMechanism extends SASLGSSAPIMechanism
{
    @Override
    protected String getServerName()
    {
        return super.serviceName;
    }
}
