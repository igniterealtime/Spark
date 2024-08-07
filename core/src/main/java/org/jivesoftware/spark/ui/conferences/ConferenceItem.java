/**
 * Copyright (C) 2011 eZuce Inc. All rights reserved.
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

import org.jivesoftware.smackx.bookmarks.BookmarkedConference;

public class ConferenceItem {
   private final BookmarkedConference bookmarkedConf;

   public ConferenceItem(BookmarkedConference bookmarkedConf) {
       this.bookmarkedConf = bookmarkedConf;
   }

   @Override
   public String toString() {
       return bookmarkedConf.getName() != null && !bookmarkedConf.getName().isEmpty() ? bookmarkedConf.getName() : bookmarkedConf.getJid().getLocalpart().asUnescapedString();
   }

   public BookmarkedConference getBookmarkedConf() {
       return bookmarkedConf;
   }


}
