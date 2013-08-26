/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/
 
package com.jtattoo.plaf;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Michael Hagen
 */

// TODO:
// - Auf dem Mac scheint es ein Problem mit dem zeichnen des Aluminium Hintergrunds zu geben
// - setMaximizedBounds unter Linux bei multiscreen Umgebungen funktioniert nicht. Aus diesem Grund
//   wird in Linux die Toolbar beim maximieren verdeckt (siehe BaseTitlePane maximize)
public class About extends JDialog {

    public static String JTATTOO_VERSION = "Version: 1.6.9";
    
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final Dimension dlgSize = new Dimension(440, 240);
    private static int dlgPosX = (screenSize.width / 2) - (dlgSize.width / 2);
    private static int dlgPosY = (screenSize.height / 2) - (dlgSize.height / 2);

    public About() {
        super((JFrame) null, "About JTattoo");
        JPanel contentPanel = new JPanel(null);
        JLabel titleLabel = new JLabel("JTattoo " + JTATTOO_VERSION);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 20, dlgSize.width - 8, 36);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(titleLabel);

        JLabel copyrightLabel = new JLabel("(c) 2002 and later by MH Software-Entwicklung");
        copyrightLabel.setBounds(0, 80, dlgSize.width - 8, 20);
        copyrightLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(copyrightLabel);

        JButton okButton = new JButton("OK");
        okButton.setBounds((dlgSize.width - 80) / 2, 170, 80, 24);
        contentPanel.add(okButton);

        setContentPane(contentPanel);

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });

        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                System.exit(0);
            }
        });
    }

    /** Starten der Anwendung
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
            About dlg = new About();
            dlg.setSize(dlgSize);
            dlg.setLocation(dlgPosX, dlgPosY);
            dlg.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
