package freeseawind.lf.utils;

/**
 * Platform tool class.
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckPlatformUtils
{
    /**
     * check is Linux platform.
     *
     * @return 
     */
    public static boolean isLinux()
    {
        final String osName = System.getProperty("os.name").toLowerCase();

        return osName.startsWith("linux");
    }

    /**
     * check is Mac OS platform.
     *
     * @return 
     */
    public static boolean isMac()
    {
        final String osName = System.getProperty("os.name").toLowerCase();

        return osName.startsWith("mac os");
    }

    /**
     * check is Windows platform.
     *
     * @return
     */
    public static boolean isWindows()
    {
        final String osName = System.getProperty("os.name").toLowerCase();

        return osName.startsWith("windows");
    }
}
