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
package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.sparkimpl.plugin.bookmarks.BookmarkUI;

/**
 *
 * @author kos
 */
public interface BookmarksListener {

    /**
     * Invoked by <code>BookmarksUI</code> when a new Bookmark has been added.
     *
     * @param roomJID - the <code>Bookmark</code> JID that has been added.
     * @see BookmarkUI
     */
    void bookmarkAdded(String roomJID);

    /**
     * Invoked by <code>BookmarksUI</code> when a new Bookmark has been removed.
     *
     * @param roomJID - the <code>Bookmark</code> JID that has been removed.
     * @see BookmarkUI
     */
    void bookmarkRemoved(String roomJID);
    

}
