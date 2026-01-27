package org.jivesoftware.sparkimpl.plugin.idle;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.*;


/**
 * Utility method to retrieve the idle time on Linux with X11
 *
 * @author Martin
 * @author MoonRockSeven
 * @author Ian Zwanink
 * @see "http://ochafik.com/blog/?p=98"
 */
class X11IdleTimeDetector implements IdleTimeDetector {

    /**
     * Structure providing info on the XScreensaver.
     */
    public static class XScreenSaverInfo extends Structure {
        /**
         * screen saver window
         */
        public Window window;
        /**
         * ScreenSaver{Off,On,Disabled}
         */
        public int state;
        /**
         * ScreenSaver{Blanked,Internal,External}
         */
        public int kind;
        /**
         * milliseconds
         */
        public NativeLong til_or_since;
        /**
         * milliseconds
         */
        public NativeLong idle;
        /**
         * events
         */
        public NativeLong event_mask;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("window", "state", "kind", "til_or_since", "idle", "event_mask");
        }
    }

    /**
     * Definition (incomplete) of the Xext library.
     */
    public interface Xss extends Library {
        /**
         * Instance of the Xss library bindings.
         */
        Xss INSTANCE = Native.load("Xss", Xss.class);

        /**
         * Allocate a XScreensaver information structure.
         * @return a {@link XScreenSaverInfo} instance.
         */
        XScreenSaverInfo XScreenSaverAllocInfo();

        /**
         * Query the XScreensaver.
         * @param display    the display.
         * @param drawable   a {@link Drawable} structure.
         * @param saver_info a previously allocated {@link XScreenSaverInfo} instance.
         * @return an int return code.
         */
        int XScreenSaverQueryInfo(Display display, Drawable drawable, XScreenSaverInfo saver_info);
    }

    @Override
	public long getIdleTimeMillis() {
        Window window;
        XScreenSaverInfo info = null;
        Display display = null;
        final X11 x11 = X11.INSTANCE;
        final Xss xss = Xss.INSTANCE;

        long idleMillis = 0;
        try {
            display = x11.XOpenDisplay(null);
            if (display != null) {
                window = x11.XDefaultRootWindow(display);
                info = xss.XScreenSaverAllocInfo();
                if (info != null) {
                    xss.XScreenSaverQueryInfo(display, window, info);
                    idleMillis = info.idle.longValue();
                }
            }
        } catch (Throwable e) {
            return 0;
        } finally {
            if (info != null) x11.XFree(info.getPointer());
            if (display != null) x11.XCloseDisplay(display);
        }
        return idleMillis;
    }
}
