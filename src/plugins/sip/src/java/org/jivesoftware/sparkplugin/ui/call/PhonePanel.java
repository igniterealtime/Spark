/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.ui.call;

import net.java.sipmack.sip.InterlocutorUI;
import org.jivesoftware.spark.ui.ContainerComponent;

import javax.swing.JPanel;

import java.awt.Color;

/**
 *
 */
public abstract class PhonePanel extends JPanel implements ContainerComponent {

    protected final Color greenColor = new Color(91, 175, 41);
    protected final Color orangeColor = new Color(229, 139, 11);
    protected final Color blueColor = new Color(64, 103, 162);
    protected final Color redColor = new Color(211, 0, 0);


    abstract void callEnded();

    abstract void setInterlocutorUI(InterlocutorUI ic);

    abstract InterlocutorUI getActiveCall();

    abstract String getPhoneNumber();

}
