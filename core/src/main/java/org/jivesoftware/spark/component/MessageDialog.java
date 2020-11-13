/*
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
package org.jivesoftware.spark.component;

import org.jivesoftware.MainWindow;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <code>MessageDialog</code> class is used to easily display the most commonly used dialogs.
 */
public final class MessageDialog
{
    /**
     * Display a dialog with an exception.
     *
     * The dialog's owner (to which the dialog is positioned relative to) is the Spark "Main Window".
     *
     * @param throwable the throwable object to display.
     */
    public static void showErrorDialog( final Throwable throwable )
    {
        showErrorDialog( SparkManager.getMainWindow(), null, throwable );
    }

    /**
     * Display a dialog with an exception.
     *
     * @param owner The owner of the dialog, to which the dialog is positioned relative to.
     * @param throwable the throwable object to display.
     */
    public static void showErrorDialog( final JFrame owner, final Throwable throwable )
    {
        showErrorDialog( owner, null, throwable );
    }

    /**
     * Display a dialog with an exception.
     *
     * The dialog's owner (to which the dialog is positioned relative to) is the Spark "Main Window".
     *
     * @param description Human readable text (can be null or empty).
     * @param throwable the throwable object to display.
     */
    public static void showErrorDialog( final String description, final Throwable throwable )
    {
        showErrorDialog( SparkManager.getMainWindow(), description, throwable );
    }
    /**
     * Display a dialog with an exception.
     *
     * @param owner The owner of the dialog, to which the dialog is positioned relative to.
     * @param description Human readable text (can be null or empty).
     * @param throwable the throwable object to display.
     */
    public static void showErrorDialog( final JFrame owner, final String description, final Throwable throwable )
    {
        EventQueue.invokeLater( () ->
        {
            // Create the title panel for this dialog
            final String desc = description == null || description.trim().isEmpty() ? null : description.trim();
            final TitlePanel titlePanel = new TitlePanel( Res.getString( "message.default.error" ), desc, SparkRes.getImageIcon( SparkRes.SMALL_DELETE ), true );

            final JLabel titleLabel = new JLabel( Res.getString( "message.default.error" ) );
            titleLabel.setFont(new Font("dialog", Font.BOLD, 11 ) );

            final JLabel descriptionLabel = new JLabel(desc);
            descriptionLabel.setFont(new Font("dialog", Font.PLAIN, 10 ) );

            // The stacktrace content.
            final JTextArea textPane = new JTextArea();
            textPane.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
            textPane.setEditable( false );
            textPane.setText( getStackTrace( throwable ) );
            textPane.setCaretPosition( 0 ); // scroll to top
            final JScrollPane scrollPane = new JScrollPane( textPane );
            scrollPane.setPreferredSize( new Dimension( 600, 400 ) );
            scrollPane.setVisible( false );

            // Construct main panel w/ layout.
            final JPanel mainPanel = new JPanel();
            mainPanel.setLayout( new BorderLayout() );

            mainPanel.add( titleLabel, BorderLayout.NORTH );
            if ( description != null && !description.trim().isEmpty() )
            {
                mainPanel.add( descriptionLabel, BorderLayout.CENTER );
            }
            mainPanel.add( scrollPane, BorderLayout.SOUTH );

            // The user should only be able to close this dialog.
            final Object[] options = { Res.getString( "details" ), Res.getString( "close" ) };
            final JOptionPane pane = new JOptionPane( mainPanel, JOptionPane.ERROR_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[ 1 ] );

            final JDialog dlg = new JDialog( owner, Res.getString( "title.error" ), false );
            dlg.setContentPane( pane );

            pane.addPropertyChangeListener( "value", evt ->
            {
                if ( dlg.isVisible() && evt.getSource() == pane )
                {
                    String value = (String) pane.getValue();
                    if ( Res.getString( "close" ).equals( value ) )
                    {
                        dlg.setVisible( false );
                    }
                    if ( Res.getString( "details" ).equals( value ) )
                    {
                        scrollPane.setVisible( !scrollPane.isVisible() );
                        dlg.pack();
                        dlg.setLocationRelativeTo( owner );
                        pane.setValue( null ); // reset the value, otherwise the value change listener won't fire again!
                    }
                }
            } );

            dlg.pack();
            dlg.setLocationRelativeTo( owner );

            // By setting the preferred size to whatever is the size after packaging, the size of these components is
            // unlikely to be modified by the expanding/collapsing of the stack trace pane.
            titleLabel.setPreferredSize( titleLabel.getSize() );
            descriptionLabel.setPreferredSize( descriptionLabel.getSize() );

            dlg.setVisible( true );
            dlg.toFront();
            dlg.requestFocus();
        } );
    }

    /**
     * Display an alert dialog.
     *
     * @param message the message to display.
     * @param header  the header/title of the dialog.
     * @param title   the title to display.
     * @param icon    the icon for the alert dialog.
     */
    public static void showAlert( final String message, final String header, final String title, final Icon icon )
    {
        EventQueue.invokeLater( () -> {
            JTextPane textPane;
            final JOptionPane pane;
            final JDialog dlg;

            TitlePanel titlePanel;

            textPane = new JTextPane();
            textPane.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
            textPane.setEditable( false );
            textPane.setText( message );
            textPane.setBackground( Color.white );

            // Create the title panel for this dialog
            titlePanel = new TitlePanel( header, null, icon, true );

            // Construct main panel w/ layout.
            final JPanel mainPanel = new JPanel();
            mainPanel.setLayout( new BorderLayout() );
            mainPanel.add( titlePanel, BorderLayout.NORTH );

            // The user should only be able to close this dialog.
            Object[] options = { Res.getString( "close" ) };
            pane = new JOptionPane( new JScrollPane( textPane ), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[ 0 ] );

            mainPanel.add( pane, BorderLayout.CENTER );

            MainWindow mainWindow = SparkManager.getMainWindow();
            dlg = new JDialog( mainWindow, title, false );
            dlg.pack();
            dlg.setSize( 300, 300 );
            dlg.setContentPane( mainPanel );
            dlg.setLocationRelativeTo( SparkManager.getMainWindow() );

            PropertyChangeListener changeListener = e ->
            {
                String value = (String) pane.getValue();
                if ( Res.getString( "close" ).equals( value ) )
                {
                    dlg.setVisible( false );
                }
            };

            pane.addPropertyChangeListener( changeListener );

            dlg.setVisible( true );
            dlg.toFront();
            dlg.requestFocus();
        } );
    }

    /**
     * Creates, but does not display, a dialog with a specified component.
     *
     * @param title       the title of the dialog.
     * @param description the description to display.
     * @param icon        the icon.
     * @param comp        the component to display.
     * @param parent      the parent of this dialog.
     * @param modal       true if it is modal.
     * @return the <code>JDialog</code> created.
     */
    public static JDialog createComponent( String title, String description, Icon icon, JComponent comp, Component parent, boolean modal )
    {
        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel( title, description, icon, true );

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.add( titlePanel, BorderLayout.NORTH );

        // The user should only be able to close this dialog.
        Object[] options = { Res.getString( "close" ) };
        pane = new JOptionPane( comp, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[ 0 ] );

        mainPanel.add( pane, BorderLayout.CENTER );

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog( parent, title );
        dlg.setModal( modal );

        dlg.pack();
        dlg.setResizable( true );
        dlg.setContentPane( mainPanel );

        PropertyChangeListener changeListener = e -> {
            String value;
            try
            {
                value = (String) pane.getValue();
                if ( Res.getString( "close" ).equals( value ) )
                {
                    dlg.setVisible( false );
                }
            }
            catch ( Exception ex )
            {
                // probably <ESC> pressed ;-)
            }

        };

        pane.addPropertyChangeListener( changeListener );

        return dlg;
    }

    /**
     * Display a dialog with a specified component.
     *
     * @param title       the title of the dialog.
     * @param description the description to display.
     * @param icon        the icon.
     * @param comp        the component to display.
     * @param parent      the parent of this dialog.
     * @param width       the width of this dialog.
     * @param height      the height of this dialog.
     * @param modal       true if it is modal.
     * @return the <code>JDialog</code> created.
     */
    public static JDialog showComponent( String title, String description, Icon icon, JComponent comp, Component parent, int width, int height, boolean modal )
    {
        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel( title, description, icon, true );

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.add( titlePanel, BorderLayout.NORTH );

        // The user should only be able to close this dialog.
        Object[] options = { Res.getString( "close" ) };
        pane = new JOptionPane( comp, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[ 0 ] );

        mainPanel.add( pane, BorderLayout.CENTER );

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog( parent, title );
        dlg.setModal( modal );

        dlg.pack();
        dlg.setSize( width, height );
        dlg.setResizable( true );
        dlg.setContentPane( mainPanel );
        dlg.setLocationRelativeTo( parent );

        PropertyChangeListener changeListener = e -> {
            String value;
            try
            {
                value = (String) pane.getValue();
                if ( Res.getString( "close" ).equals( value ) )
                {
                    dlg.setVisible( false );
                }
            }
            catch ( Exception ex )
            {
                // probably <ESC> pressed ;-)
            }
        };

        pane.addPropertyChangeListener( changeListener );

        dlg.setVisible( true );
        dlg.toFront();
        dlg.requestFocus();
        return dlg;
    }

    /**
     * Returns the String representation of a StackTrace.
     *
     * @param aThrowable the throwable object.
     * @return the string.
     */
    public static String getStackTrace( Throwable aThrowable )
    {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter( result );
        aThrowable.printStackTrace( printWriter );
        return result.toString();
    }

    /**
     * Defines a custom format for the stack trace as String.
     *
     * @param heading    the title of the stack trace.
     * @param aThrowable the throwable object.
     * @return the string.
     */
    public static String getCustomStackTrace( String heading, Throwable aThrowable )
    {
        //add the class name and any message passed to constructor
        final StringBuilder result = new StringBuilder( heading );
        result.append( aThrowable.toString() );
        final String lineSeperator = System.getProperty( "line.separator" );
        result.append( lineSeperator );

        //add each element of the stack trace
        StackTraceElement[] stackTrace = aThrowable.getStackTrace();
        for ( StackTraceElement traceElement : stackTrace)
        {
            result.append( traceElement );
            result.append( lineSeperator );
        }
        return result.toString();
    }
}
