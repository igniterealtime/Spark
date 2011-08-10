package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.smackx.bookmark.BookmarkedConference;

public class ConferenceItem {
   private BookmarkedConference bookmarkedConf;

   public ConferenceItem(BookmarkedConference bookmarkedConf) {
       this.bookmarkedConf = bookmarkedConf;
   }

   @Override
   public String toString() {
       return bookmarkedConf.getName();
   }

   public BookmarkedConference getBookmarkedConf() {
       return bookmarkedConf;
   }


}
