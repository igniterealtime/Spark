/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class DocumentShare extends JPanel {
    private String jid;

    private JPanel viewer = new JPanel();
    private JPanel documentList = new JPanel();
    private JPanel toolbar = new JPanel();

    public DocumentShare(String jid) {
        this.jid = jid;

        setLayout(new GridBagLayout());

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(viewer, BorderLayout.CENTER);
        mainPanel.add(documentList, BorderLayout.SOUTH);
        add(mainPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        add(toolbar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        final JButton shareDesktop = new JButton("Show Desktop");
    }
}
