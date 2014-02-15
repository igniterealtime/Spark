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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tic.tac.toe;

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.spark.util.log.Log;

/**
 * Resource Bundle for TicTacToe
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 */
public class TTTRes {

    private static PropertyResourceBundle prb;

    static ClassLoader cl = TTTRes.class.getClassLoader();

    static {
	prb = (PropertyResourceBundle) ResourceBundle
		.getBundle("i18n/tictactoe_i18n");
    }

    public static final String getString(String propertyName) {
	try {
	    return prb.getString(propertyName);
	} catch (Exception e) {
	    Log.error(e);
	    return propertyName;
	}
    }

    public static String getString(String propertyName, Object... obj) {
	String str = prb.getString(propertyName);
	if (str == null) {
	    return propertyName;
	}

	return MessageFormat.format(str, obj);
    }

}
