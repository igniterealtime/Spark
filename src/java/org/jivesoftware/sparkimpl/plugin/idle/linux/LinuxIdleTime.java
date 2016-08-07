package org.jivesoftware.sparkimpl.plugin.idle.linux;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.X11;
import org.jivesoftware.sparkimpl.plugin.idle.IdleTime;


/**
 * Utility method to retrieve the idle time on Linux with X11
 *
 * @author Martin
 * @author MoonRockSeven
 * @author Ian Zwanink
 * @see http://ochafik.com/blog/?p=98
 */
public class LinuxIdleTime implements IdleTime {
    /** Definition (incomplete) of the Xext library. */
    interface Xss extends Library {
        Xss INSTANCE = (Xss) Native.loadLibrary("Xss", Xss.class);

        public class XScreenSaverInfo extends Structure {
            public X11.Window window; /* screen saver window */
            public int state; /* ScreenSaver{Off,On,Disabled} */
            public int kind; /* ScreenSaver{Blanked,Internal,External} */
            public NativeLong til_or_since; /* milliseconds */
            public NativeLong idle; /* milliseconds */
            public NativeLong event_mask; /* events */

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[] { "window", "state", "kind", "til_or_since",
                        "idle", "event_mask" });
            }
        }

        XScreenSaverInfo XScreenSaverAllocInfo();

        int XScreenSaverQueryInfo(X11.Display dpy, X11.Drawable drawable,
                                  XScreenSaverInfo saver_info);
    }

    public long getIdleTimeMillis() {
        X11.Window win = null;
        Xss.XScreenSaverInfo info = null;
        X11.Display dpy = null;
        final X11 x11 = X11.INSTANCE;
        final Xss xss = Xss.INSTANCE;

        long idlemillis = 0L;
        try {
            dpy = x11.XOpenDisplay(null);
            win = x11.XDefaultRootWindow(dpy);
            info = xss.XScreenSaverAllocInfo();
            xss.XScreenSaverQueryInfo(dpy, win, info);

            idlemillis = info.idle.longValue();
        } finally {
            if (info != null)
                x11.XFree(info.getPointer());
            info = null;

            if (dpy != null)
                x11.XCloseDisplay(dpy);
            dpy = null;
        }
        return idlemillis;
    }
}