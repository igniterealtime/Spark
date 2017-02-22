import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * To Run in Terminal:
 * javac CompareLocales.java
 * java CompareLocales
 * 
 * 
 * 
 * 
 * Use this Class to compare your locale with the standard english to find
 * missing entries
 * 
 * @author wolf.posdorfer
 * 
 */
public class CompareLocales {

    static HashMap<String, String>_englishlist = new HashMap<>();
    static HashMap<String, String> _mylocalelist = new HashMap<>();

    static String _path = "C:\\Dokumente und Einstellungen\\wolf.posdorfer\\Desktop\\eclipse\\spark\\src\\resources\\i18n\\spark_i18n";
    static String english = _path + ".properties";
    static String totest = _path + "_pl.properties";

    public static void main(String[] args) {

	readFile(new File(english), _englishlist);
	readFile(new File(totest), _mylocalelist);
	
	
	for(String key : _englishlist.keySet())
	{
	    if(!_mylocalelist.containsKey(key))
	    {
		System.out.println(key +" = "+ _englishlist.get(key));
	    }
	}
	
	for(String key : _mylocalelist.keySet())
	{
	    
	    if(!_englishlist.containsKey(key))
	    {
		System.out.println("Not Found in English:   "+key +" = "+ _mylocalelist.get(key));
	    }
	}


	System.out.println("standardlist has: " + _englishlist.size() + " , my local has " + _mylocalelist.size());


    }


    /**
     * Reads a file into the Destination
     * 
     * @param file
     * @param destination
     */
    public static void readFile(File file, HashMap<String,String> destination) {

	Properties props = new Properties();

	try {

	 props.load(new FileInputStream(file));
	} catch (Exception e) {
	    System.err.println("error with file");
	}
	
	Enumeration<Object> enume = props.keys();
	while(enume.hasMoreElements())
	{
	    String s = (String) enume.nextElement();
	    destination.put(s, props.getProperty(s));
	}

    }
}
