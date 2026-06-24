package org.jivesoftware.sparkimpl.profile;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AvatarPanelTest {

    @Test
    public void acceptsSupportedImageExtensions() {
        assertTrue(AvatarPanel.isSupportedImageFile("avatar.jpg"));
        assertTrue(AvatarPanel.isSupportedImageFile("avatar.jpeg"));
        assertTrue(AvatarPanel.isSupportedImageFile("avatar.gif"));
        assertTrue(AvatarPanel.isSupportedImageFile("avatar.png"));
    }

    @Test
    public void isCaseInsensitive() {
        assertTrue(AvatarPanel.isSupportedImageFile("PHOTO.PNG"));
        assertTrue(AvatarPanel.isSupportedImageFile("Photo.JpG"));
    }

    @Test
    public void rejectsOtherFiles() {
        assertFalse(AvatarPanel.isSupportedImageFile("avatar.bmp"));
        assertFalse(AvatarPanel.isSupportedImageFile("document.pdf"));
        assertFalse(AvatarPanel.isSupportedImageFile("noextension"));
    }
}
