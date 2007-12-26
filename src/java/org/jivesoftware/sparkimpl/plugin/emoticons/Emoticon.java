/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.emoticons;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * Represents a single emoticon.
 *
 * @author Derek DeMoro
 */
public class Emoticon {

    private String imageName;
    private String emoticonName;
    private File emoticonDirectory;
    private List<String> equivalants = new ArrayList<String>();


    /**
     * Creates a single Emoticon entry.
     *
     * @param nameOfImage  the name of the image that represents this emoticon (ex. smile.gif)
     * @param emoticonName the name of this emoticon
     * @param equivalants  all string representations of this emoticon.
     * @param emoticonDirectory Directory that contains emoticons.
     */
    public Emoticon(String nameOfImage, String emoticonName, List<String> equivalants, File emoticonDirectory) {
        this.imageName = nameOfImage;
        this.emoticonName = emoticonName;

        this.equivalants = equivalants;
        this.emoticonDirectory = emoticonDirectory;
    }

    /**
     * Return the name of the image.
     *
     * @return image name.
     */
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * Returns the name of this emoticon.
     *
     * @return name of emoticon.
     */
    public String getEmoticonName() {
        return emoticonName;
    }

    public void setEmoticonName(String emoticonName) {
        this.emoticonName = emoticonName;
    }

    /**
     * Returns all text equivilants of this emoticon.
     *
     * @return list of all text equivilants.
     */
    public List<String> getEquivalants() {
        return equivalants;
    }

    public File getEmoticonDirectory(){
        return emoticonDirectory;
    }
}
