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
package org.jivesoftware.sparkimpl.settings.local;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Class that helps getting the My Documents Folder on Windows via the registry
 * 
 * @author wolf.posdorfer
 * 
 */
public class WinRegistryReader {

    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";
  
    private static final String PERSONAL_FOLDER_CMD = REGQUERY_UTIL
            + "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\"
            + "Explorer\\Shell Folders\" /v Personal";

    /**
     * returns the Path to the "My Documents" folder or <code>null</code>
     * 
     * @return {@link String}
     */
    public static String getMyDocumentsFromWinRegistry() {
        try {
            Process process = Runtime.getRuntime().exec(PERSONAL_FOLDER_CMD);
            StreamReader streamreader = new StreamReader(process.getInputStream());

            streamreader.start();
            process.waitFor();
            streamreader.join();

            String result = streamreader.getResult();
            int p = result.indexOf(REGSTR_TOKEN);

            if (p == -1)
                return null;

            return result.substring(p + REGSTR_TOKEN.length()).trim();
        } catch (Exception e) {
            return null;
        }
    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        @Override
        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        String getResult() {
            return sw.toString();
        }
    }
}
