package org.jivesoftware.spark.ui.themes;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Manages the Look and Feel instances that can be used by Spark.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class LookAndFeelManager
{
    /**
     * All non-system provided look and feel implementations that are available.
     */
    public static final Class<? extends LookAndFeel> lafs[] = new Class[]
    {
        // LittleLuck
        freeseawind.lf.LittleLuckLookAndFeel.class,

        // JTattoo
        com.jtattoo.plaf.acryl.AcrylLookAndFeel.class,
        com.jtattoo.plaf.aero.AeroLookAndFeel.class,
        com.jtattoo.plaf.aluminium.AluminiumLookAndFeel.class,
        com.jtattoo.plaf.bernstein.BernsteinLookAndFeel.class,
        com.jtattoo.plaf.fast.FastLookAndFeel.class,
        com.jtattoo.plaf.graphite.GraphiteLookAndFeel.class,
        com.jtattoo.plaf.hifi.HiFiLookAndFeel.class,
        com.jtattoo.plaf.luna.LunaLookAndFeel.class,
        com.jtattoo.plaf.mcwin.McWinLookAndFeel.class,
        com.jtattoo.plaf.mint.MintLookAndFeel.class,
        com.jtattoo.plaf.noire.NoireLookAndFeel.class,
        com.jtattoo.plaf.smart.SmartLookAndFeel.class,
        com.jtattoo.plaf.texture.TextureLookAndFeel.class,

        // Insubstance (Substance)
        org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceCeruleanLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceEmeraldDuskLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel.class,
        org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel.class
    };

    // Maybe Sometime well get a Synthetica License
    //"de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel", //commec
    //"de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel", //commerc
    //"de.javasoft.plaf.synthetica.SyntheticaBlackMoonLookAndFeel", //free
    //"de.javasoft.plaf.synthetica.SyntheticaBlackStarLookAndFeel", //free
    //"de.javasoft.plaf.synthetica.SyntheticaBlueIceLookAndFeel", //free
    //"de.javasoft.plaf.synthetica.SyntheticaBlueMoonLookAndFeel", //free
    //"de.javasoft.plaf.synthetica.SyntheticaBlueSteelLookAndFeel", //free
    //"de.javasoft.plaf.synthetica.SyntheticaClassyLookAndFeel", //commerc
    //"de.javasoft.plaf.synthetica.SyntheticaGreenDreamLookAndFeel", //free
    //"de.javasoft.plaf.synthetica.SyntheticaOrangeMetallicLookAndFeel", //commerc
    //"de.javasoft.plaf.synthetica.SyntheticaSilverMoonLookAndFeel",	//free
    //"de.javasoft.plaf.synthetica.SyntheticaSimple2DLookAndFeel", //commerc
    //"de.javasoft.plaf.synthetica.SyntheticaSkyMetallicLookAndFeel", //commerc
    //"de.javasoft.plaf.synthetica.SyntheticaWhiteVisionLookAndFeel", //commerc

    static
    {
        // Add all additional look and feels to the UIManager.
        for ( Class<? extends LookAndFeel> laf : lafs )
        {
            String name;
            try
            {
                name = laf.newInstance().getName();
            }
            catch ( InstantiationException | IllegalAccessException e )
            {
                name = laf.getTypeName();
            }

            UIManager.installLookAndFeel( name, laf.getName() );
        }
    }

    private static transient Map<String, String> lookAndFeelClassByName = null;

    /**
     * Gets the (human readable) name of a Look and Feel, based on its class name.
     *
     * @param className A class name (eg: com.jtattoo.plaf.luna.LunaLookAndFeel).
     * @return a human readable name, or null if the class name is not recognized.
     */
    public static String getName( String className )
    {
        final Map<String, String> lookAndFeelClassByName = getLookAndFeelClassByName();
        for ( final Map.Entry<String, String> entry : lookAndFeelClassByName.entrySet() )
        {
            if ( entry.getValue().equalsIgnoreCase( className ) )
            {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Gets the class name of a Look and Feel, based on its (human readable) name.
     *
     * @param name a human readable name (eg: "Luna")
     * @return A class name (eg: com.jtattoo.plaf.luna.LunaLookAndFeel) or null if the class name is not recognized
     */
    public static String getClassName( String name )
    {
        return getLookAndFeelClassByName().get( name );
    }

    /**
     * Determines if setting the look and feel as identified by it's human readable name requires a Spark restart.
     *
     * @param name The (human readable name (eg: "Luna").
     * @return true when Spark must be restarted to apply this Look And Feel.
     */
    public static boolean requiresRestart( String name )
    {
        // TODO: the original code required a restart for the java-provided LaFs and Synthetica, but perhaps that's no longer needed?
        return false;
    }

    private synchronized static Map<String, String> getLookAndFeelClassByName()
    {
        // Lazy loading of the ordered collection.
        if ( lookAndFeelClassByName == null )
        {
            final UIManager.LookAndFeelInfo[] lafis = UIManager.getInstalledLookAndFeels();

            lookAndFeelClassByName = new TreeMap<>( String::compareToIgnoreCase );
            for ( final UIManager.LookAndFeelInfo lafi : lafis )
            {
                lookAndFeelClassByName.put( lafi.getName(), lafi.getClassName() );
            }
        }
        return lookAndFeelClassByName;
    }

    private static String getLookandFeel(LocalPreferences preferences)
    {
        String result;

        String whereToLook = Spark.isMac() ? Default.DEFAULT_LOOK_AND_FEEL_MAC : Default.DEFAULT_LOOK_AND_FEEL;

        if (!Default.getBoolean(Default.LOOK_AND_FEEL_DISABLED)) {
            result = preferences.getLookAndFeel();
        } else if (Default.getString(whereToLook).length() > 0) {
            result = Default.getString(whereToLook);
        } else {
            result = UIManager.getSystemLookAndFeelClassName();
        }

        return result;
    }

    /**
     * Handles the Loading of the Look And Feel, as defined as the prefered Look And Feel in the local settings.
     */
    public static void loadPreferredLookAndFeel()
    {
        final LocalPreferences preferences = SettingsManager.getLocalPreferences();
        final String laf = getLookandFeel( preferences );

        if ( laf.toLowerCase().contains( "substance" ) )
        {
            EventQueue.invokeLater( () -> doSetLookAndFeel( laf ) );
        }
        else
        {
            doSetLookAndFeel( laf );
        }

//        if ( laf.contains( "jtattoo" ) )
//        {
//            Properties props = new Properties();
//            String menubar = Default.getString( Default.MENUBAR_TEXT ) == null ? ""
//                    : Default.getString( Default.MENUBAR_TEXT );
//            props.put( "logoString", menubar );
//            try
//            {
//                Class<?> c = Thread.currentThread().getContextClassLoader().loadClass( laf );
//                Method m = c.getMethod( "setCurrentTheme", Properties.class );
//                m.invoke( c.newInstance(), props );
//            }
//            catch ( Exception e )
//            {
//                Log.error( "Error Setting JTattoo ", e );
//            }
//        }
    }

    private static void doSetLookAndFeel( String laf )
    {
        try
        {
            if ( Spark.isWindows() )
            {
                JFrame.setDefaultLookAndFeelDecorated( true );
                JDialog.setDefaultLookAndFeelDecorated( true );
            }
            UIManager.setLookAndFeel( laf );
        }
        catch ( Exception e )
        {
            Log.error( "An exception occurred while trying to load the look and feel.", e );
        }
    }

    /**
     * Returns the human readable name of all available Look And Feels.
     *
     * @return An array of names, never null.
     */
    public static String[] getLookAndFeelNames()
    {
        return getLookAndFeelClassByName().keySet().toArray( new String[0] );
    }
}
