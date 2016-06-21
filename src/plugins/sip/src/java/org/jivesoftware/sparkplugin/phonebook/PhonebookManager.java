package org.jivesoftware.sparkplugin.phonebook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import net.java.sipmack.common.Log;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

public class PhonebookManager implements BookManager
{
	private static final PhonebookManager instance = new PhonebookManager();
	private List<PhoneNumber> phonenumbers = new ArrayList<PhoneNumber>();
	
	public PhonebookManager() {
		
	}
	
	public static PhonebookManager getInstance() {
		return instance;
	}
	
	/**
	 * makes a new Phonenumber-Object and write it into the file
	 */
	public boolean add(String name, String number) {
		if(getPhonebookEntry(name, number) != null)
			return false;
		
		PhoneNumber entry = new PhoneNumber();
		entry.setName(name);
		entry.setNumber(number);
		
		phonenumbers.add(entry);
		commit();
		
		return true;
	}
	
	/**
	 * checks whether the entry already exists or not.
	 * if it exists it returns the entry.
	 * 
	 * @param entry: Phonenumber
	 * @return
	 */
	public PhoneNumber getPhonebookEntry(String name, String number) {
		PhoneNumber entry = new PhoneNumber();
		entry.setName(name);
		entry.setNumber(number);
		
		for(PhoneNumber numbers : phonenumbers) {
			if(entry.getName().equals(numbers.getName())
				&& entry.getNumber().equals(numbers.getNumber())) {
				return numbers;
			}
		}
		return null;
	}
	
	public void deleteEntry(String name, String number) {
		PhoneNumber existing = getPhonebookEntry(name, number);
		if(existing != null) {
			phonenumbers.remove(existing);
			commit();
		}
	}
	
	public List<PhoneNumber> getPhoneNumbers() {
		// if there are no entries, read the phonebook
		if(phonenumbers == null
			|| phonenumbers.size() == 0)
			loadPhonebook();
		
		return phonenumbers;
	}
	
	
   private File getPhonebookFile() {
      File file = new File(Spark.getSparkUserHome());
      if (!file.exists()) {
          file.mkdirs();
      }
      return new File(file, "spark-phonebook.xml");
  }
   
   /**
    * Reads in the transcript file using the Xml Pull Parser.
    */
   private void loadPhonebook() {
       File bookFile = getPhonebookFile();
       if (!bookFile.exists()) {
           return;
       }

       // Otherwise load
       try {
           final MXParser parser = new MXParser();
           parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
           BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile), "UTF-8"));
           parser.setInput(in);
           boolean done = false;
           while (!done) {
               int eventType = parser.next();
               if (eventType == XmlPullParser.START_TAG && "entry".equals(parser.getName())) {
               	phonenumbers.add(getBookEntry(parser));
               }
               else if (eventType == XmlPullParser.END_TAG && "book".equals(parser.getName())) {
                   done = true;
               }
           }
           in.close();
       }
       catch (Exception e) {
           Log.error(e);
       }
   }
   
   /**
    * xml-structure:
    * 
    * <book><entry><name>ABC</name><number>123</number></entry><entry>...</entry></book>
    * 
    * @param parser
    * @return
    * @throws Exception
    */
   private static PhoneNumber getBookEntry(XmlPullParser parser) throws Exception {
   	PhoneNumber entry = new PhoneNumber();

      // Check for Names
      boolean done = false;
      while (!done) {
          int eventType = parser.next();
          if (eventType == XmlPullParser.START_TAG && "name".equals(parser.getName())) {
         	 entry.setName(parser.nextText());
          }
          else if (eventType == XmlPullParser.START_TAG && "number".equals(parser.getName())) {
         	 entry.setNumber(parser.nextText());
          }
          else if (eventType == XmlPullParser.END_TAG && "entry".equals(parser.getName())) {
              done = true;
          }
      }

      return entry;
  }
	
   
   public void commit() {
      final StringBuilder builder = new StringBuilder();

      builder.append("<book>");

      for (PhoneNumber m : phonenumbers) {
          builder.append("<entry>");
          builder.append("<name>").append(m.getName()).append("</name>");
          builder.append("<number>").append(m.getNumber()).append("</number>");
          builder.append("</entry>");
      }

      builder.append("</book>");

      // Write out new File
      try {
          getPhonebookFile().getParentFile().mkdirs();
          BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getPhonebookFile()), "UTF-8"));
          out.write(builder.toString());
          out.close();
      }
      catch (IOException e) {
          org.jivesoftware.spark.util.log.Log.error(e);
      }
  }
   
   public boolean update(PhoneNumber original, String name, String number) {
   	// if the entry was added succesfully
   	if(add(name, number)) {
   		// delete the old one
   		deleteEntry(original.getName(), original.getNumber());
   		return true;
   	}
   	else {
   		JOptionPane.showMessageDialog(null, PhoneRes.getIString("book.exists"), 
				PhoneRes.getIString("book.warning"), JOptionPane.WARNING_MESSAGE);
   		return false;
   	}
   }
}
