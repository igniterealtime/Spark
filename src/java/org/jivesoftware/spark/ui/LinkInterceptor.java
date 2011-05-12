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
package org.jivesoftware.spark.ui;

import java.awt.event.MouseEvent;

/**
 * Implementors of this interface wish to interecept link clicked events within
 * an active chat.
 */
public interface LinkInterceptor {

    /**
     * Returns true if you wish to handle this link, otherwise, will default to Spark.
     *
     * @param mouseEvent the MouseEvent.
     * @param link the link that was clicked.
     * @return true if the user wishes to handle the link.
     */
    public boolean handleLink(MouseEvent mouseEvent, String link);
}
