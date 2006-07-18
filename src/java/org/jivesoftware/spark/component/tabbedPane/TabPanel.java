/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component.tabbedPane;

import javax.swing.JPanel;

/**
 *
 */
public class TabPanel extends JPanel {

    private boolean selected;
    private TabPanelUI ui;

    /**
     * Creates a background panel using the default Spark background image.
     */
    public TabPanel() {
        ui = new TabPanelUI();
        setUI(ui);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        ui.setSelected(selected);
    }

    public void showBorder(boolean show) {
        ui.setHideBorder(!show);
    }

    protected void setTabPlacement(int placement){
        ui.setPlacement(placement);
    }
}

