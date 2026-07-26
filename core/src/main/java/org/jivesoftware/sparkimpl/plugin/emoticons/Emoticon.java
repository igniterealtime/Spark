/**
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
package org.jivesoftware.sparkimpl.plugin.emoticons;

import java.util.List;
import java.io.File;

/**
 * Represents a single emoticon.
 *
 * @author Derek DeMoro
 */
public class Emoticon {
    private final String imageName;
    private final String emoticonName;
    private final File emoticonDirectory;
    private final List<String> equivalents;

    /**
     * Creates a single Emoticon entry.
     *
     * @param nameOfImage  the name of the image that represents this emoticon (ex. smile.gif)
     * @param emoticonName the name of this emoticon
     * @param equivalents  all string representations of this emoticon.
     * @param emoticonDirectory Directory that contains emoticons.
     */
    public Emoticon(String nameOfImage, String emoticonName, List<String> equivalents, File emoticonDirectory) {
        this.imageName = nameOfImage;
        this.emoticonName = emoticonName;
        this.equivalents = equivalents;
        this.emoticonDirectory = emoticonDirectory;
    }

    /**
     * Return the name of the image.
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Returns the name of this emoticon.
     */
    public String getEmoticonName() {
        return emoticonName;
    }

    /**
     * Returns all text equivalents of this emoticon.
     */
    public List<String> getEquivalents() {
        return equivalents;
    }

    public File getEmoticonDirectory(){
        return emoticonDirectory;
    }
}
