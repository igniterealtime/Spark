package org.jivesoftware.sparkplugin.phonebook;

import java.util.List;

public interface BookManager
{
	public void deleteEntry(String name, String number);
	
	public List<PhoneNumber> getPhoneNumbers();
	
	public boolean update(PhoneNumber original, String name, String number);
	
	public boolean add(String name, String number);
	
	public PhoneNumber getPhonebookEntry(String name, String number);
}
