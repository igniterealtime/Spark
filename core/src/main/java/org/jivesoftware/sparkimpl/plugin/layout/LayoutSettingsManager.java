/*
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
package org.jivesoftware.sparkimpl.plugin.layout;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Responsible for the loading and persisting of layout-settings.
 */
public class LayoutSettingsManager
{
    private static LayoutSettings layoutSettings;

    private LayoutSettingsManager()
    {
    }

    /**
     * Returns the LayoutSettings for this agent.
     *
     * @return the LayoutSettings for this agent.
     */
    public static LayoutSettings getLayoutSettings()
    {
        if ( !exists() && layoutSettings == null )
        {
            layoutSettings = new LayoutSettings();
        }

        if ( layoutSettings == null )
        {
            // Do Initial Load from FileSystem.
            File settingsFile = getSettingsFile();
            layoutSettings = load( settingsFile );
        }
        return layoutSettings;
    }

    /**
     * Persists the settings to the local file system.
     */
    public static void saveLayoutSettings()
    {
        final Properties props = new Properties();

        final Rectangle mainWindow = layoutSettings.getMainWindowBounds();
        if ( mainWindow != null )
        {
            props.setProperty( "mainWindowX", Integer.toString( mainWindow.x ) );
            props.setProperty( "mainWindowY", Integer.toString( mainWindow.y ) );
            props.setProperty( "mainWindowHeight", Integer.toString( mainWindow.height ) );
            props.setProperty( "mainWindowWidth", Integer.toString( mainWindow.width ) );
        }

        final Rectangle chatFrame = layoutSettings.getChatFrameBounds();
        if ( chatFrame != null )
        {
            props.setProperty( "chatFrameX", Integer.toString( chatFrame.x ) );
            props.setProperty( "chatFrameY", Integer.toString( chatFrame.y ) );
            props.setProperty( "chatFrameWidth", Integer.toString( chatFrame.width ) );
            props.setProperty( "chatFrameHeight", Integer.toString( chatFrame.height ) );
        }

        final Rectangle preferences = layoutSettings.getPreferencesBounds();
        if ( preferences != null )
        {
            props.setProperty( "preferencesFrameX", Integer.toString( preferences.x ) );
            props.setProperty( "preferencesFrameY", Integer.toString( preferences.y ) );
            props.setProperty( "preferencesFrameWidth", Integer.toString( preferences.width ) );
            props.setProperty( "preferencesFrameHeight", Integer.toString( preferences.height ) );
        }
        props.setProperty( "splitDividerLocation", Integer.toString( layoutSettings.getSplitPaneDividerLocation() ) );

        try
        {
            props.store( new FileOutputStream( getSettingsFile() ), "Storing Spark Layout Settings" );
        }
        catch ( Exception e )
        {
            Log.error( "Error saving settings.", e );
        }
    }

    /**
     * Return true if the settings file exists.
     *
     * @return true if the settings file exists.('settings.xml')
     */
    public static boolean exists()
    {
        return getSettingsFile().exists();
    }

    /**
     * Returns the settings file.
     *
     * @return the settings file.
     */
    public static File getSettingsFile()
    {
        File file = new File( Spark.getSparkUserHome() );
        if ( !file.exists() )
        {
            file.mkdirs();
        }
        return new File( file, "layout.settings" );
    }

    public static int asInt( Properties props, String propertyName )
    {
        return Integer.parseInt( props.getProperty( propertyName, "-1" ) );
    }

    private static LayoutSettings load( File file )
    {
        final Properties props = new Properties();
        try
        {
            props.load( new FileInputStream( file ) );

            final LayoutSettings settings = new LayoutSettings();

            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            // Main Window
            final Dimension mainWindowDimension = new Dimension(
                asInt( props, "mainWindowWidth" ),
                asInt( props, "mainWindowHeight" )
            );

            if ( mainWindowDimension.width > screenSize.width )
            {
                mainWindowDimension.width = screenSize.width - 50;
            }

            if ( mainWindowDimension.height > screenSize.height )
            {
                mainWindowDimension.height = screenSize.height - 50;
            }

            final Point mainWindowLocation = ensureValidWindowPosition(
                new Point(
                    asInt( props, "mainWindowX" ),
                    asInt( props, "mainWindowY" )
                ),
                mainWindowDimension );

            settings.setMainWindowBounds( new Rectangle( mainWindowLocation, mainWindowDimension ) );

            // Chat Frame
            final Dimension chatFrameDimension = new Dimension(
                asInt( props, "chatFrameWidth" ),
                asInt( props, "chatFrameHeight" )
            );
            final Point chatFrameLocation = ensureValidWindowPosition(
                new Point(
                    asInt( props, "chatFrameX" ),
                    asInt( props, "chatFrameY" )
                ),
                chatFrameDimension );

            settings.setChatFrameBounds( new Rectangle( chatFrameLocation, chatFrameDimension ) );

            // Preferences
            final Dimension preferencesDimension = new Dimension(
                asInt( props, "preferencesFrameWidth" ),
                asInt( props, "preferencesFrameHeight" )
            );
            final Point preferencesLocation = ensureValidWindowPosition(
                new Point(
                    asInt( props, "preferencesFrameX" ),
                    asInt( props, "preferencesFrameY" )
                ),
                chatFrameDimension );

            settings.setPreferencesBounds( new Rectangle( preferencesLocation, preferencesDimension ) );

            // Split Divider
            settings.setSplitPaneDividerLocation( asInt( props, "splitDividerLocation" ) );

            return settings;
        }
        catch ( Exception e )
        {
            Log.error( e );
            return new LayoutSettings();
        }
    }

    protected static boolean isValidWindowPosition( Point location, Dimension dimension )
    {
        Rectangle windowTitleBounds = new Rectangle( location.x, location.y, dimension.width, 20 );
        double windowTitleArea = windowTitleBounds.getWidth() * windowTitleBounds.getHeight();

        Rectangle[] screenBounds = GraphicUtils.getScreenBounds();
        for ( Rectangle screenBound : screenBounds )
        {
            Rectangle screen = screenBound.getBounds();
            Rectangle intersection = screen.intersection( windowTitleBounds );
            double visibleArea = intersection.getWidth() * intersection.getHeight();

            // if 25% of it is visible in the device, then it is good
            if ( ( visibleArea / windowTitleArea ) > 0.25 )
            {
                return true;
            }
        }

        return false;
    }

    protected static Point ensureValidWindowPosition( Point location, Dimension dimension )
    {
        if ( isValidWindowPosition( location, dimension ) )
        {
            return location;
        }

        // Center
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point( (screenSize.width - dimension.width) / 2, (screenSize.height - dimension.height) / 2 );
    }
}
