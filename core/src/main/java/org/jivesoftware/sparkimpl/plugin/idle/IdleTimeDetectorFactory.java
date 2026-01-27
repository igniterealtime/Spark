package org.jivesoftware.sparkimpl.plugin.idle;

import static org.apache.commons.lang3.SystemUtils.*;

/**
 * A factory implementation that returns an idle system detector based on the OS detected for the current host.
 *
 * @author Laurent Cohen
 */
public class IdleTimeDetectorFactory {
    public IdleTimeDetector newIdleTimeDetector() {
        if (IS_OS_WINDOWS) {
            return new WindowsIdleTimeDetector();
        } else if (IS_OS_MAC) {
            return new MacIdleTimeDetector();
        } else if (IS_OS_LINUX || IS_OS_FREE_BSD || IS_OS_OPEN_BSD) {
            return new X11IdleTimeDetector();
        }
        return null;
    }
}
