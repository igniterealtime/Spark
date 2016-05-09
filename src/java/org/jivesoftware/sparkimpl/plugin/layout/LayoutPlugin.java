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
package org.jivesoftware.sparkimpl.plugin.layout;

import org.jivesoftware.MainWindow;
import org.jivesoftware.MainWindowListener;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;

public class LayoutPlugin implements Plugin {

    public void initialize() {
        final MainWindow mainWindow = SparkManager.getMainWindow();

        SparkManager.getMainWindow().addMainWindowListener(new MainWindowListener() {
            public void shutdown() {
                int x = mainWindow.getX();
                int y = mainWindow.getY();
                int width = mainWindow.getWidth();
                int height = mainWindow.getHeight();

                LayoutSettings settings = LayoutSettingsManager.getLayoutSettings();
               
                settings.setMainWindowHeight(height);
                settings.setMainWindowWidth(width);
                settings.setMainWindowX(x);
                settings.setMainWindowY(y);
                if (mainWindow.isDocked()){
                	settings.setSplitPaneDividerLocation(mainWindow.getSplitPane().getDividerLocation());
                }
                else{
                	settings.setSplitPaneDividerLocation(-1);
                }
                LayoutSettingsManager.saveLayoutSettings();
            }

            public void mainWindowActivated() {

            }

            public void mainWindowDeactivated() {

            }
        });
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
        // Do nothing.
    }
}
