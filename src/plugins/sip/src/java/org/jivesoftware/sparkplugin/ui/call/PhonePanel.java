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
package org.jivesoftware.sparkplugin.ui.call;

import net.java.sipmack.sip.InterlocutorUI;
import org.jivesoftware.spark.ui.ContainerComponent;

import javax.swing.JPanel;

import java.awt.Color;

/**
 *
 */
public abstract class PhonePanel extends JPanel implements ContainerComponent {

    private static final long serialVersionUID = -6467506473797199694L;
    protected final Color greenColor = new Color(91, 175, 41);
    protected final Color orangeColor = new Color(229, 139, 11);
    protected final Color blueColor = new Color(64, 103, 162);
    protected final Color redColor = new Color(211, 0, 0);


    abstract void callEnded();

    abstract void setInterlocutorUI(InterlocutorUI ic);

    abstract InterlocutorUI getActiveCall();

    abstract String getPhoneNumber();

}
