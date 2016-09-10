package freeseawind.lf.utils;

/**
 * 该类主要用来判断运行时的操作系统类型
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckPlatformUtils
{
    /**
     * 判断是否是在Linux系统下运行
     *
     * @return 是Linux系统返回true，否则false
     */
    public static boolean isLinux()
    {
        final String osName = System.getProperty("os.name").toLowerCase();

        return osName.startsWith("linux");
    }

    /**
     * 判断是否是在Mac Os系统下运行
     *
     * @return 是Mac系统返回true，否则false
     */
    public static boolean isMac()
    {
        final String osName = System.getProperty("os.name").toLowerCase();

        return osName.startsWith("mac os");
    }

    /**
     * 判断是否是在Windows系统下运行
     *
     * @return 是Windows系统返回true，否则false
     */
    public static boolean isWindows()
    {
        final String osName = System.getProperty("os.name").toLowerCase();

        return osName.startsWith("windows");
    }
}
