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
package org.jivesoftware.spark.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.jivesoftware.MainWindow;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;


/**
 * <code>MessageDialog</code> class is used to easily display the most commonly used dialogs.
 */
public final class MessageDialog {

    private MessageDialog() {
   	 
    }

    /**
     * Display a dialog with an exception.
     *
     * @param throwable the throwable object to display.
     */
    public static void showErrorDialog(final Throwable throwable) {
     	 EventQueue.invokeLater(new Runnable() {
   		 public void run()
   		 {
		        JTextPane textPane;
		        final JOptionPane pane;
		        final JDialog dlg;
		
		        TitlePanel titlePanel;
		
		        textPane = new JTextPane();
		        textPane.setFont(new Font("Dialog", Font.PLAIN, 12));
		        textPane.setEditable(false);
		
		        String message = getStackTrace(throwable);
		        textPane.setText(message);
		        // Create the title panel for this dialog
		        titlePanel = new TitlePanel(Res.getString("message.default.error"), null, SparkRes.getImageIcon(SparkRes.SMALL_DELETE), true);
		
		        // Construct main panel w/ layout.
		        final JPanel mainPanel = new JPanel();
		        mainPanel.setLayout(new BorderLayout());
		        mainPanel.add(titlePanel, BorderLayout.NORTH);
		
		        // The user should only be able to close this dialog.
		        Object[] options = {Res.getString("close")};
		        pane = new JOptionPane(new JScrollPane(textPane), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
		
		        mainPanel.add(pane, BorderLayout.CENTER);
		
		        MainWindow mainWindow = SparkManager.getMainWindow();
		        dlg = new JDialog(mainWindow, Res.getString("title.error"), false);
		        dlg.pack();
		        dlg.setSize(600, 400);
		        dlg.setContentPane(mainPanel);
		        dlg.setLocationRelativeTo(mainWindow);
		
		        PropertyChangeListener changeListener = new PropertyChangeListener() {
		            public void propertyChange(PropertyChangeEvent e) {
		                String value = (String)pane.getValue();
		                if (Res.getString("close").equals(value)) {
		                    dlg.setVisible(false);
		                }
		            }
		        };
		
		        pane.addPropertyChangeListener(changeListener);
		
		        dlg.setVisible(true);
		        dlg.toFront();
		        dlg.requestFocus();
   		 }
     	 });
    }

    /**
     * Display an alert dialog.
     *
     * @param message the message to display.
     * @param header  the header/title of the dialog.
     * @param title   the title to display.
     * @param icon    the icon for the alert dialog.
     */
    public static void showAlert(final String message,final String header, final String title, final Icon icon) {
   	 EventQueue.invokeLater(new Runnable() {
   		 public void run()
   		 {
   	        JTextPane textPane;
   	        final JOptionPane pane;
   	        final JDialog dlg;

   	        TitlePanel titlePanel;

   	        textPane = new JTextPane();
   	        textPane.setFont(new Font("Dialog", Font.PLAIN, 12));
   	        textPane.setEditable(false);
   	        textPane.setText(message);
   	        textPane.setBackground(Color.white);

   	        // Create the title panel for this dialog
   	        titlePanel = new TitlePanel(header, null, icon, true);

   	        // Construct main panel w/ layout.
   	        final JPanel mainPanel = new JPanel();
   	        mainPanel.setLayout(new BorderLayout());
   	        mainPanel.add(titlePanel, BorderLayout.NORTH);

   	        // The user should only be able to close this dialog.
   	        Object[] options = {Res.getString("close")};
   	        pane = new JOptionPane(new JScrollPane(textPane), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

   	        mainPanel.add(pane, BorderLayout.CENTER);

   	        MainWindow mainWindow = SparkManager.getMainWindow();
   	        dlg = new JDialog(mainWindow, title, false);
   	        dlg.pack();
   	        dlg.setSize(300, 300);
   	        dlg.setContentPane(mainPanel);
   	        dlg.setLocationRelativeTo(SparkManager.getMainWindow());

   	        PropertyChangeListener changeListener = new PropertyChangeListener() {
   	            public void propertyChange(PropertyChangeEvent e) {
   	                String value = (String)pane.getValue();
   	                if (Res.getString("close").equals(value)) {
   	                    dlg.setVisible(false);
   	                }
   	            }
   	        };

   	        pane.addPropertyChangeListener(changeListener);

   	        dlg.setVisible(true);
   	        dlg.toFront();
   	        dlg.requestFocus();
   			 
   		 }	
   	 });

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
    public static JDialog showComponent(String title, String description, Icon icon, JComponent comp, Component parent, int width, int height, boolean modal) {
        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(title, description, icon, true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("close")};
        pane = new JOptionPane(comp, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog(parent, title);
        dlg.setModal(modal);

        dlg.pack();
        dlg.setSize(width, height);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String value;
                try {
                    value= (String)pane.getValue();
                    if (Res.getString("close").equals(value)) {
                        dlg.setVisible(false);
                    }    
                } catch (Exception ex) {
                    // probably <ESC> pressed ;-)
                }
                
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
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
    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    /**
     * Defines a custom format for the stack trace as String.
     *
     * @param heading    the title of the stack trace.
     * @param aThrowable the throwable object.
     * @return the string.
     */
    public static String getCustomStackTrace(String heading, Throwable aThrowable) {
        //add the class name and any message passed to constructor
        final StringBuffer result = new StringBuffer(heading);
        result.append(aThrowable.toString());
        final String lineSeperator = System.getProperty("line.separator");
        result.append(lineSeperator);

        //add each element of the stack trace
        StackTraceElement[] stackTrace = aThrowable.getStackTrace();
        final List<StackTraceElement> traceElements = Arrays.asList(stackTrace);
        for (StackTraceElement traceElement : traceElements) {
            result.append(traceElement);
            result.append(lineSeperator);
        }
        return result.toString();
    }


}