package org.jivesoftware.sparkimpl.plugin.idle.windows;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.*;
import org.jivesoftware.spark.util.log.Log;


public class WinLockListener implements WinUser.WindowProc {

    public WinLockListener() {
        // define new window class
        final WString windowClass = new WString("MyWindowClass");
        final WinDef.HMODULE hInst = Kernel32.INSTANCE.GetModuleHandle("");

        WinUser.WNDCLASSEX wClass = new WinUser.WNDCLASSEX();
        wClass.hInstance = hInst;
        wClass.lpfnWndProc = WinLockListener.this;
        wClass.lpszClassName = windowClass;

        // register window class
        User32.INSTANCE.RegisterClassEx(wClass);
        getLastError();

        // create new window
        final WinDef.HWND hWnd = User32.INSTANCE.CreateWindowEx(User32.WS_EX_TOPMOST, windowClass, "'hidden helper window to catch Windows events", 0, 0, 0, 0, 0, null, // WM_DEVICECHANGE contradicts parent=WinUser.HWND_MESSAGE
                null, hInst, null);

        getLastError();
        Log.debug("window sucessfully created! window hwnd: " + hWnd.getPointer().toString());

        Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hWnd, Wtsapi32.NOTIFY_FOR_THIS_SESSION);

        WinUser.MSG msg = new WinUser.MSG();
        while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) != 0) {
            User32.INSTANCE.TranslateMessage(msg);
            User32.INSTANCE.DispatchMessage(msg);
        }

        /// This code is to clean at the end. You can attach it to your custom application shutdown listener
        Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd);
        User32.INSTANCE.UnregisterClass(windowClass, hInst);
        User32.INSTANCE.DestroyWindow(hWnd);
        Log.debug("program exit!");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sun.jna.platform.win32.User32.WindowProc#callback(com.sun.jna.platform .win32.WinDef.HWND, int, com.sun.jna.platform.win32.WinDef.WPARAM, com.sun.jna.platform.win32.WinDef.LPARAM)
     */
    public WinDef.LRESULT callback(WinDef.HWND hwnd, int uMsg, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
        switch (uMsg) {
            case WinUser.WM_DESTROY: {
                User32.INSTANCE.PostQuitMessage(0);
                return new WinDef.LRESULT(0);
            }
            case WinUser.WM_SESSION_CHANGE: {
                this.onSessionChange(wParam, lParam);
                return new WinDef.LRESULT(0);
            }
            default:
                return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
        }
    }

    /**
     * Gets the last error.
     *
     * @return the last error
     */
    public int getLastError() {
        int rc = Kernel32.INSTANCE.GetLastError();

        if (rc != 0)
            Log.debug("error: " + rc);

        return rc;
    }

    /**
     * On session change.
     *
     * @param wParam the w param
     * @param lParam the l param
     */
    protected void onSessionChange(WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
        switch (wParam.intValue()) {
            case Wtsapi32.WTS_SESSION_LOCK: {
                this.onMachineLocked(lParam.intValue());
                break;
            }
            case Wtsapi32.WTS_SESSION_UNLOCK: {
                this.onMachineUnlocked(lParam.intValue());
                break;
            }
        }
    }

    /**
     * On machine locked.
     *
     * @param sessionId the session id
     */
    protected void onMachineLocked(int sessionId) {
    }

    /**
     * On machine unlocked.
     *
     * @param sessionId the session id
     */
    protected void onMachineUnlocked(int sessionId) {
    }
    /**
     * Utility method to retrieve the idle time on Windows and sample code to test it.
     * JNA shall be present in your classpath for this to work (and compile).
     * @author ochafik
     */

}
