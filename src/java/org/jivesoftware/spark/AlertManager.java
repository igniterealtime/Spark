/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark;

import org.jivesoftware.spark.util.ModelUtil;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The AlertManager handles the delegation of Alerting based.
 *
 * @author Derek DeMoro
 */
public class AlertManager {

    private List<Alerter> alerts = new ArrayList<Alerter>();

    public AlertManager() {

    }

    /**
     * Adds an alert.
     *
     * @param alerter the Alerter to add.
     */
    public void addAlert(Alerter alerter) {
        alerts.add(alerter);
    }

    /**
     * Removes an alerter.
     *
     * @param alerter the alerter to remove.
     */
    public void removeAlert(Alerter alerter) {
        alerts.remove(alerter);
    }


    /**
     * Flash the given window.
     *
     * @param window the window to flash.
     */
    public void flashWindow(Window window) {
        final Iterator alertNotifier = ModelUtil.reverseListIterator(alerts.listIterator());
        while (alertNotifier.hasNext()) {
            final Alerter alert = (Alerter)alertNotifier.next();
            boolean handle = alert.handleNotification();
            if (handle) {
                alert.flashWindow(window);
                break;
            }
        }
    }

    /**
     * Flash the given window, but stop flashing when the window takes focus.
     *
     * @param window the window to start flashing.
     */
    public void flashWindowStopOnFocus(Window window) {
        final Iterator alertNotifiers = ModelUtil.reverseListIterator(alerts.listIterator());
        while (alertNotifiers.hasNext()) {
            final Alerter alert = (Alerter)alertNotifiers.next();
            boolean handle = alert.handleNotification();
            if (handle) {
                alert.flashWindowStopWhenFocused(window);
                break;
            }
        }
    }

    /**
     * Stop the flashing of the window.
     *
     * @param window the window to stop flashing.
     */
    public void stopFlashing(Window window) {
        final Iterator alertNotifiers = ModelUtil.reverseListIterator(alerts.listIterator());
        while (alertNotifiers.hasNext()) {
            final Alerter alert = (Alerter)alertNotifiers.next();
            boolean handle = alert.handleNotification();
            if (handle) {
                alert.stopFlashing(window);
                break;
            }
        }
    }

}
