package freeseawind.swing;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JdkVersion
{
    private String version;
    private int major;
    private int minor;
    private int revision;
    private int num;
    private static final JdkVersion singleton = new JdkVersion();
    
    private JdkVersion()
    {
        version = System.getProperty("java.version");
        
        Pattern p = Pattern.compile("\\d{1,}");
        
        Matcher m = p.matcher(version);
        
        List<Integer> list = new LinkedList<Integer>();
        
        while (m.find())
        {
            String key = version.substring(m.start(), m.end()).toLowerCase().trim();
            
            list.add(Integer.parseInt(key));
        }
        
        major = getVersionInfo(list, 0);
        minor = getVersionInfo(list, 1);
        revision = getVersionInfo(list, 2);
        num = getVersionInfo(list, 3);
    }
    
    public static JdkVersion getSingleton()
    {
        return singleton;
    }
    
    private int getVersionInfo(List<Integer> list, int idx)
    {
        if(idx < list.size())
        {
            return list.get(idx);
        }
        
        return 0;
    }
    
    public String getVersion()
    {
        return version;
    }

    public int getMajor()
    {
        return major;
    }

    public int getMinor()
    {
        return minor;
    }

    public int getRevision()
    {
        return revision;
    }

    public int getNum()
    {
        return num;
    }

    @Override
    public String toString()
    {
        return "JdkVersion [version=" + version + ", major=" + major
                + ", minor=" + minor + ", revision=" + revision + ", num=" + num
                + "]";
    }
}
