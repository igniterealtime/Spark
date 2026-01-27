package org.jivesoftware.sparkimpl.plugin.idle;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

import java.util.Arrays;
import java.util.List;

/**
 * Utility method to retrieve the idle time on Windows and sample code to test it.
 * JNA shall be present in your classpath for this to work (and compile).
 * @author ochafik
 */
class WindowsIdleTimeDetector implements IdleTimeDetector {

    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        /**
         * Retrieves the number of milliseconds that have elapsed since the system was started.
         * @see "http://msdn2.microsoft.com/en-us/library/ms724408.aspx"
         * @return number of milliseconds that have elapsed since the system was started.
         */
        int GetTickCount();
    }

    /**
     * Contains the time of the last input.
     * @see "http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/userinput/keyboardinput/keyboardinputreference/keyboardinputstructures/lastinputinfo.asp"
     */
    class LASTINPUTINFO extends Structure {
        public int cbSize = 8;

        /** Tick count of when the last input event was received. */
        public int dwTime;

        @SuppressWarnings("rawtypes")
        @Override
        protected List getFieldOrder() {
            return Arrays.asList("cbSize", "dwTime");
        }
    }

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);
        /**
         * Retrieves the time of the last input event.
         * @see "http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/userinput/keyboardinput/keyboardinputreference/keyboardinputfunctions/getlastinputinfo.asp"
         * @return time of the last input event, in milliseconds
         */
        boolean GetLastInputInfo(LASTINPUTINFO result);
    }

    /**
     * Get the amount of milliseconds that have elapsed since the last input event
     * (mouse or keyboard)
     * @return idle time in milliseconds
     */
    @Override
	public long getIdleTimeMillis() {
        LASTINPUTINFO lastInputInfo = new LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
    }
}

