package pkg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Use this Class to compare your locale with the standard english to find missing entries
 * @author wolf.posdorfer
 *
 */
public class CompareLocales {

    private static ArrayList<String> _mainlist = new ArrayList<String>();
    static ArrayList<String> _standardlist = new ArrayList<String>();
    static ArrayList<String> _mylocalelist = new ArrayList<String>();

    static String _endofFile = "#!#";

    public static void main(String[] args)
    {
	// Change Path
	String path = "C:\\spark\\spark_i18n";
	File standard = new File(path + ".properties");
	
	/**
	 * Change this to your locale
	 */
	File mylocale = new File(path + "_de.properties");

	
	
	readFile(standard, _standardlist);
	readFile(mylocale, _mylocalelist);

	
	// check if all standard items are in the locale file
	for (String s : _standardlist) {
	    if (!_mylocalelist.contains(s)) {
		System.out.println(_mainlist.get(_standardlist.indexOf(s)));
	    }
	}
	// Check if all locale-items are in the standard file
	for (String s : _mylocalelist) {
	    if (!_standardlist.contains(s)) {
		System.out.println("Delete this->"+s+ "  @"+_mylocalelist.indexOf(s));
	    }
	}

	System.out.println("standardlist has: "+_standardlist.size() + " , my local has " + _mylocalelist.size());

	
	// Check if we have doubles
	checkdoubles(mylocale);

    }

    /**
     * Check if our File has double entries
     * @param file
     */
    private static void checkdoubles(File file) {
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(file));

	    ArrayList<String> liste = new ArrayList<String>();
	    HashSet<String> hashliste = new HashSet<String>();
	    String zeile = "";

	    boolean t = true;
	    while (t) {

		// Skip all files that are not items
		// like comments and empty lines
		if (zeile != null && zeile.contains("=")) {

		    String s = zeile;
		    s = zeile.replace(" ", "");
		    s = s.substring(0, zeile.indexOf("="));
		   
		    liste.add(s);
		    if (!hashliste.add(s))
			System.out.println("Double-> "+zeile);

		}

		zeile = reader.readLine();
		t = !zeile.contains(_endofFile);

	    }

	    System.out.println("\n     "+file.getName()+"\n            >>>containing " + liste.size() + " entries, with " + (liste.size()-hashliste.size())+ " doubles");
	    reader.close();
	} catch (FileNotFoundException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	}

    }

    /**
     * Reads a file into the Destination
     * @param file
     * @param destination
     */
    public static void readFile(File file, ArrayList<String> destination) {
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(file));

	    String zeile = "";

	    boolean t = true;
	    while (t) {

		if (zeile != null && zeile.contains("=")) {
		    if (destination.equals(_standardlist))
			_mainlist.add(zeile);
		    String s = zeile;
		    s = zeile.replace(" ", "");
		    s = s.substring(0, zeile.indexOf("="));
		   
		    destination.add(s);

		}

		zeile = reader.readLine();
		t = !zeile.contains(_endofFile);

	    }

	    reader.close();
	} catch (FileNotFoundException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	}

    }
}
