/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.callhistory;

import java.util.ArrayList;
import java.util.List;

public class CallList {
    private List<HistoryCall> list;

    public CallList() {
        list = new ArrayList<HistoryCall>();
    }

    public List<HistoryCall> getList() {
        return list;
    }

    public void addCall(HistoryCall call) {
        this.list.add(0, call);
    }
}
