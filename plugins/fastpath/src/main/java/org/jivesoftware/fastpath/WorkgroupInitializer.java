package org.jivesoftware.fastpath;

/**
 * Fastpath / Workgroup related code is available in smack-legacy.jar. Sadly, only part of the IQ providers and packet
 * extensions is loaded by default by Smack (see SMACK-729 in the issue tracker).
 * <p>
 * To work around this issue, this class initializes the remaining classes. This class should no longer be needed when
 * the original problem in Smack is fixed.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 * @see <a href="https://issues.igniterealtime.org/browse/SMACK-729">Issue SMACK-729</a>
 */

import org.jivesoftware.smack.initializer.UrlInitializer;

public class WorkgroupInitializer extends UrlInitializer
{
    public WorkgroupInitializer()
    {
    }

    protected String getProvidersUrl()
    {
        return "classpath:org.jivesoftware.smack.legacy/workgroup.providers";
    }
}

