/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.spark.ui.login;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class GSSAPIConfiguration extends Configuration
{
    Map<String, Vector<AppConfigurationEntry>> configs;

    public GSSAPIConfiguration()
    {
        super();
        init( true );
    }

    public GSSAPIConfiguration( boolean config_from_file )
    {
        super();
        init( config_from_file );
    }

    private void init( boolean config_from_file )
    {
        configs = new HashMap<>();

        //The structure of the options is not well documented in terms of
        //data types.  Since the file version of the Configuration object
        //puts things in quotes, String is assumed. But boolean options
        //do not have quotes, and my represent different types internally.
        HashMap<String, String> c_options = new HashMap<>();

        //If Kerberos config is not from a file, it's not possible to (re-)read the config file.
        //So don't set refreshKrb5Config
        if ( config_from_file )
        {
            c_options.put( "refreshKrb5Config", "true" );
        }
        c_options.put( "doNotPrompt", "true" );
        c_options.put( "useTicketCache", "true" );
        c_options.put( "debug", "true" );

        putAppConfigurationEntry( "com.sun.security.jgss.initiate", "com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, c_options );
        putAppConfigurationEntry( "com.sun.security.jgss.krb5.initiate", "com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, c_options );
    }

    public AppConfigurationEntry[] getAppConfigurationEntry( String name )
    {
        AppConfigurationEntry[] a = new AppConfigurationEntry[ 1 ];
        if ( configs.containsKey( name ) )
        {
            Vector<AppConfigurationEntry> v = configs.get( name );
            a = v.toArray( a );
            return a;
        }
        else
        {
            return null;
        }
    }

    public boolean putAppConfigurationEntry( String name, String module, AppConfigurationEntry.LoginModuleControlFlag controlFlag, Map<String, String> options )
    {
        Vector<AppConfigurationEntry> v;
        if ( configs.containsKey( name ) )
        {
            v = configs.get( name );
        }
        else
        {
            v = new Vector<>();
            configs.put( name, v );
        }

        return v.add( new AppConfigurationEntry( module, controlFlag, options ) );
    }

    public void refresh()
    {
    }
}
