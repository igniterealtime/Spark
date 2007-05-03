package org.jivesoftware;

import java.util.*;
import java.security.*;
import javax.security.auth.*;
import javax.security.auth.login.*;


public class GSAPPIConfiguration extends Configuration {

	Map<String,Vector<AppConfigurationEntry>> configs;

	GSAPPIConfiguration() {
		super();

		configs = new HashMap<String,Vector<AppConfigurationEntry>>();

		//The structure of the options is not well documented in terms of
		//data types.  Since the file version of the Configuration object
		//puts things in quotes, String is assumed. But boolean options
		//do not have quotes, and my represent different types internally.
		HashMap<String,String> c_options = new HashMap<String,String>();
		c_options.put("doNotPrompt","true");
		c_options.put("useTicketCache","true");
		c_options.put("debug","true");


		putAppConfigurationEntry("GetPrincipal","com.sun.security.auth.module.Krb5LoginModule",AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,c_options);
		putAppConfigurationEntry("com.sun.security.jgss.initiate","com.sun.security.auth.module.Krb5LoginModule",AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,c_options);
		putAppConfigurationEntry("com.sun.security.jgss.krb5.initiate","com.sun.security.auth.module.Krb5LoginModule",AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,c_options);

	}

	public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
		AppConfigurationEntry[] a = new AppConfigurationEntry[1];
		if(configs.containsKey(name)) {
			Vector<AppConfigurationEntry> v = configs.get(name);
			a = v.toArray(a);
			return a;
		} else {
			return null;
		}
	}

	public boolean putAppConfigurationEntry(String name, String module, AppConfigurationEntry.LoginModuleControlFlag controlFlag, Map options) {
		Vector<AppConfigurationEntry> v;
		if(configs.containsKey(name)) {
			v = configs.get(name);
		} else {
			v = new Vector<AppConfigurationEntry>();
			configs.put(name,v);
		}

		return v.add(new AppConfigurationEntry(module,controlFlag,options));
	}


    public void refresh() {
    }
}