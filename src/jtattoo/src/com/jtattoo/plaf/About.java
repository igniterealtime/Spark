/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Michael Hagen
 */

// ToDo:
// - Auf dem Mac scheint es ein Problem mit dem zeichnen des Aluminium Hintergrunds zu geben
// - setMaximizedBounds unter Linux bei multiscreen Umgebungen funktioniert nicht. Aus diesem Grund
//   wird in Linux die Toolbar beim maximieren verdeckt (siehe BaseTitlePane maximize)
public class About extends JDialog {

    public static String JTATTOO_VERSION = "Version: 1.5.0";
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final Dimension dlgSize = new Dimension(320, 240);
    private static int dlgPosX = (screenSize.width / 2) - (dlgSize.width / 2);
    private static int dlgPosY = (screenSize.height / 2) - (dlgSize.height / 2);

    public About() {
        super((JFrame) null, "About JTattoo");
        JPanel contentPanel = new JPanel(null);
        JLabel titleLabel = new JLabel("JTattoo " + JTATTOO_VERSION);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 20, 312, 36);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(titleLabel);

        JLabel copyrightLabel = new JLabel("(C) 2002-2011 by MH Software-Entwicklung");
        copyrightLabel.setBounds(0, 120, 312, 20);
        copyrightLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(copyrightLabel);

        JButton okButton = new JButton("OK");
        okButton.setBounds(120, 170, 80, 24);
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
