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

package org.jivesoftware.sparkimpl.preference;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PreferenceDialog implements PropertyChangeListener
{
    private JDialog preferenceDialog;
    private JOptionPane pane = null;
    private PreferencesPanel prefPanel;

    public void invoke( JFrame parentFrame, PreferencesPanel contentPane )
    {
        this.prefPanel = contentPane;

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        // Construct Dialog
        preferenceDialog = new JDialog( parentFrame,
                                        Res.getString( "title.preferences" ),
                                        false );

        preferenceDialog.setMinimumSize( new Dimension( 600, 600 ) );


        JButton btn_apply = new JButton( Res.getString( "apply" ) );
        JButton btn_save = new JButton( Res.getString( "save" ) );
        JButton btn_close = new JButton( Res.getString( "close" ) );

        btn_close.addActionListener( e ->
                                     {
                                         preferenceDialog.setVisible( false );
                                         preferenceDialog.dispose();
                                     } );

        btn_save.addActionListener( e ->
                                    {
                                        boolean okToClose = prefPanel.closing();
                                        if ( okToClose )
                                        {
                                            preferenceDialog.setVisible( false );
                                            preferenceDialog.dispose();
                                        }
                                        else
                                        {
                                            pane.setValue( JOptionPane.UNINITIALIZED_VALUE );
                                        }
                                    } );

        btn_apply.addActionListener( e ->
                                     {
                                         boolean okToClose = prefPanel.closing();
                                         if ( !okToClose )
                                         {
                                             pane.setValue( JOptionPane.UNINITIALIZED_VALUE );
                                         }
                                     } );

        Object[] options = { btn_apply, btn_save, btn_close };
        pane = new JOptionPane( contentPane, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[ 0 ] );
        mainPanel.add( pane, BorderLayout.CENTER );
        preferenceDialog.setContentPane( mainPanel );

        preferenceDialog.pack();
        final Rectangle preferencesBounds = LayoutSettingsManager.getLayoutSettings().getPreferencesBounds();
        if ( preferencesBounds == null || preferencesBounds.width <= 0 || preferencesBounds.height <= 0 )
        {
            // Use default settings.
            preferenceDialog.setSize( 750, 550 );
            preferenceDialog.setLocationRelativeTo( SparkManager.getMainWindow() );
        }
        else
        {
            preferenceDialog.setBounds( preferencesBounds );
        }

        pane.addPropertyChangeListener( this );

        preferenceDialog.setVisible( true );
        preferenceDialog.toFront();

        preferenceDialog.addComponentListener( new ComponentAdapter()
        {
            @Override
            public void componentResized( ComponentEvent e )
            {
                LayoutSettingsManager.getLayoutSettings().setPreferencesBounds( preferenceDialog.getBounds() );
            }

            @Override
            public void componentMoved( ComponentEvent e )
            {
                LayoutSettingsManager.getLayoutSettings().setPreferencesBounds( preferenceDialog.getBounds() );
            }
        } );
    }

    @Override
	public void propertyChange( PropertyChangeEvent e )
    {
        if ( pane.getValue() instanceof Integer )
        {
            pane.removePropertyChangeListener( this );
            preferenceDialog.dispose();
        }
    }

    public JDialog getDialog()
    {
        return preferenceDialog;
    }
}
