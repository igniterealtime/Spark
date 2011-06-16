package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui;

/**
 * Provides static access to often used Time and Byte functions
 * 
 * @author wolf.posdorfer
 * 
 */
public class TransferUtils {

    /**
     * Calculates the speed when given a timedifference and bytedifference
     * 
     * @param bytediff
     *            in bytes
     * @param timediff
     *            in milliseconds
     * @return <b>xx,x kB/s</b> or <b>xx,x MB/s</b>
     */
    public static String calculateSpeed(long bytediff, long timediff) {
	double kB = calculateSpeedLong(bytediff, timediff);

	if (bytediff == 0 && timediff == 0) {
	    return "";
	}
	if (kB < 1024) {
	    String KB = Double.toString(kB);
	    // Convert 3.1415926535897932384626433832795 to 3.1
	    KB = splitAtDot(KB, 1);

	    return KB + "kB/s";
	} else {
	    String MB = Double.toString((kB / 1024.0));
	    // Convert 3.1415926535897932384626433832795 to 3.1
	    MB = splitAtDot(MB, 1);

	    return MB + "MB/s";
	}

    }

    /**
     * Calculates the speed and returns a long in kB/s
     * 
     * @param bytediff
     * @param timediff
     * @return kB/s
     */
    public static double calculateSpeedLong(long bytediff, long timediff) {
	timediff = timediff == 0 ? 1 : timediff;
	double kB = ((bytediff / timediff) * 1000.0) / 1024.0;
	return kB;
    }

    /**
     * Calculate the estimated time of arrival
     * 
     * @param currentsize
     *            in byte
     * @param totalsize
     *            in byte
     * @param timestart
     *            in milliseconds
     * @param timenow
     *            in milliseconds
     * @return time in (HH:MM:SS)
     */
    public static String calculateEstimate(long currentsize, long totalsize,
	    long timestart, long timenow) {
	long timediff = timenow - timestart;
	long sizeleft = totalsize - currentsize;

	// currentsize = timediff
	// sizeleft = x
	currentsize = currentsize == 0 ? 1L : currentsize;
	long x = sizeleft * timediff / currentsize;

	// Make it seconds
	x = x / 1000;

	return convertSecondstoHHMMSS(Math.round(x));
    }

    /**
     * Converts given Seconds to HH:MM:SS
     * 
     * @param second
     *            in seconds
     * @return (HH:MM:SS)
     */
    public static String convertSecondstoHHMMSS(int second) {

	int hours = Math.round(second / 3600);
	int minutes = Math.round((second / 60) % 60);
	int seconds = Math.round(second % 60);
	String hh = hours < 10 ? "0" + hours : "" + hours;
	String mm = minutes < 10 ? "0" + minutes : "" + minutes;
	String ss = seconds < 10 ? "0" + seconds : "" + seconds;

	return "(" + hh + ":" + mm + ":" + ss + ")";

    }
    
    /**
     * Converts a given Byte into KB or MB or GB if applicable
     * @param bytes
     * @return "12 KB" or "27 MB" etc
     */
    public static String getAppropriateByteWithSuffix(long bytes)
    {
	if(bytes >= 1099511627776L)
	{
	    String x = splitAtDot(""+(bytes/1099511627776L),2);
	    return x +" TB"; 
	}
	else if(bytes >= 1073741824)
	{
	    String x = splitAtDot(""+(bytes/1073741824L),2);
	    return x +" GB"; 
	}
	else if(bytes >= 1048576)
	{
	    String x = splitAtDot(""+(bytes/1048576L),2);
	    return x +" MB"; 
	}
	else if(bytes >= 1024)
	{
	    String x = splitAtDot(""+(bytes/1024L),2);
	    return x +" KB";
	}
	else return bytes + " B";
    }
    /**
     * shorten a double or long with sig.digits<br>
     * splitAtDot("3.123",2) -> "3.12"<br>
     * does not round!
     * @param string
     * @param significantdigits
     * @return
     */
    private static String splitAtDot(String string, int significantdigits) {
	if (string.contains(".")) {
	    // no idea why but string.split doesnt like "."
	    String s = string.replace(".", "T").split("T")[1];

	    if (s.length() >= significantdigits) {
		return string.substring(0, string.indexOf(".") + 1
			+ significantdigits);
	    } else {
		return string
			.substring(0, string.indexOf(".") + 1 + s.length());
	    }
	}
	else return string;

    }

}
