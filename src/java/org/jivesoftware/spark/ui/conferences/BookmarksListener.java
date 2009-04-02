/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
