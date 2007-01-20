/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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
