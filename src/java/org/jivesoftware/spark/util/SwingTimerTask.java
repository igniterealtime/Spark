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
package org.jivesoftware.spark.util;

import java.awt.EventQueue;

/**
 * Allows for handling of TimerTask operations inside of the SwingEventThread.
 */
public abstract class SwingTimerTask extends java.util.TimerTask {
    public abstract void doRun();

    public void run() {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(this);
        }
        else {
            doRun();
        }
    }
}
