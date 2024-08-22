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
package org.jivesoftware.sparkimpl.profile;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import net.coobird.thumbnailator.Thumbnails;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.endsWithAny;

/**
 * UI to view/edit avatar.
 */
public class AvatarPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = -5526978906063691519L;
    private final JLabel avatar;
    private byte[] bytes;
    private File avatarFile;
    final JButton browseButton = new JButton();
    final JButton clearButton = new JButton();
    private FileDialog fileChooser;

    private Dialog dlg;

    /**
     * Default Constructor
     */
    public AvatarPanel() {
        setLayout(new GridBagLayout());


        final JLabel photo = new JLabel(Res.getString("label.avatar"));

        avatar = new JLabel();

        add(photo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(avatar, new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(browseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(clearButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        browseButton.addActionListener(this);

        // Add ResourceUtils
        ResourceUtils.resButton(browseButton, Res.getString("button.browse"));
        ResourceUtils.resButton(clearButton, Res.getString("button.clear"));

        clearButton.addActionListener( actionEvent -> {
            avatar.setIcon(null);
            bytes = null;
            avatarFile = null;
            avatar.setBorder(null);
        } );

        avatar.setText(Res.getString("message.no.avatar.found"));

        GraphicUtils.makeSameSize(browseButton, clearButton);
    }

    /**
     * Sets if the Avatar can be edited.
     *
     * @param editable true if editable.
     */
    public void setEditable(boolean editable) {
        browseButton.setVisible(editable);
        clearButton.setVisible(editable);
    }

    /**
     * Sets the displayable icon with the user's avatar.
     *
     * @param icon the icon.
     */
    public void setAvatar(ImageIcon icon) {
        avatar.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
        if (icon.getIconHeight() > 128 || icon.getIconWidth() > 128) {
            avatar.setIcon(new ImageIcon(icon.getImage().getScaledInstance(-1, 128, Image.SCALE_SMOOTH)));
        }
        else {
            avatar.setIcon(icon);
        }
        avatar.setText("");
    }

    /**
     * Sets the avatar bytes.
     *
     * @param bytes the bytes.
     */
    public void setAvatarBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Returns the avatars bytes.
     *
     * @return the bytes.
     */
    public byte[] getAvatarBytes() {
        return bytes;
    }

    /**
     * Returns the Icon representation of the Avatar.
     *
     * @return Icon of avatar.
     */
    public Icon getAvatar() {
        return avatar.getIcon();
    }

    /**
     * Returns the image file to use as the avatar.
     *
     * @return File of avatar.
     */
    public File getAvatarFile() {
        return avatarFile;
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        // init file chooser (if not already done)
        initFileChooser();

        fileChooser.setVisible(true);

        final File[] files = fileChooser.getFiles();
        if (files.length == 0) {
            // no selection
            return;
        }
        File file = files[0]; // Single-file selection is used. Using the first array item is safe.
        changeAvatar(file, this);
    }

    private void changeAvatar(final File selectedFile, final Component parent) {
        SwingWorker worker = new SwingWorker() {
            @Override
			public Object construct() {
                return resizeImage(selectedFile);
            }

            @Override
			public void finished() {
                BufferedImage avatarImage = (BufferedImage)get();
                if (avatarImage == null) {
                    UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                    JOptionPane.showMessageDialog(parent, "Please choose a valid image file.", Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                }
                String message = "";
                int finalImageWidth = avatarImage.getWidth();
                int finalImageHeight = avatarImage.getHeight();
                boolean showWarning = false;
                if (finalImageWidth != finalImageHeight) {
                    message += "\u2022 " + Res.getString("message.image.not.square") + "\n";
                    showWarning = true;
                }
                if (finalImageWidth < 32 && finalImageHeight < 32) {
                    message += "\u2022 " + Res.getString("message.image.small.resolution") + "\n";
                    showWarning = true;
                }
                if (showWarning) {
                    message += Res.getString("message.image.suggestion");
                    JOptionPane.showMessageDialog(parent, message, Res.getString("title.warning"), JOptionPane.WARNING_MESSAGE);
                }
                /*
                // Check size.
                long length = GraphicUtils.getBytesFromImage(avatarImage).length * 8;

                long k = 8192;

                long actualSize = (length / k) + 1;

                if (actualSize > 16) {
                    // Do not allow
                    JOptionPane.showMessageDialog(parent, Res.getString("message.image.too.large"));
                    return;
                }
                */
                //convert BufferedImage to bytes
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(avatarImage, "png", baos);
                    setAvatar(new ImageIcon(avatarImage));
                    setAvatarBytes(baos.toByteArray());
                } catch (IOException ex) {
                    Log.error(ex);
                }
            }
        };

        worker.start();
    }

    public void allowEditing(boolean allowEditing) {
        Component[] comps = getComponents();
        if (comps != null) {
            for (Component comp : comps) {
                if (comp instanceof JTextField) {
                    ((JTextField) comp).setEditable(allowEditing);
                }
            }
        }
    }

    public void initFileChooser() {
        if (fileChooser == null) {
            fileChooser = new FileDialog(dlg, "Choose Avatar", FileDialog.LOAD);
            fileChooser.setFilenameFilter((dir, name) -> endsWithAny(name.toLowerCase(), ".jpeg", ".jpg", ".gif", ".png"));
        }
    }

    public void setParentDialog(Dialog dialog) {
        this.dlg = dialog;
    }
    /*
     * Resize images larger than 96 pixels without changing aspect ratio.
     * Returns modified image as BufferedImage.
     */
    private BufferedImage resizeImage(File selectedFile) {
        BufferedImage resizedImage = null;
        try {
            resizedImage = Thumbnails.of(selectedFile)
                .size(96, 96)
                .asBufferedImage();
        } catch (IOException ex) {
            Log.error(ex);
        }
        return resizedImage;
    }
}
