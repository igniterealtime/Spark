package org.jivesoftware.sparkimpl.profile;

import org.jivesoftware.resource.Res;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCropDialog {
    public static BufferedImage selectSquareFromPhoto(File file, Dialog parent) {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(file);
        } catch (IOException e) {
            return null;
        }
        if (originalImage == null) {
            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(parent, "Please choose a valid image file.", Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
            return null;
        }
        BufferedImage[] croppedImage = {null};
        JDialog cropDialog = new JDialog(parent, Res.getString("title.avatar.crop"), true);
        cropDialog.setLayout(new BorderLayout());

        // Calculate a scaling factor to fit the image in the dialog
        int maxDisplayWidth = 800;
        int maxDisplayHeight = 600;
        double scaleX = (double) maxDisplayWidth / originalImage.getWidth();
        double scaleY = (double) maxDisplayHeight / originalImage.getHeight();
        double scale = Math.min(Math.min(scaleX, scaleY), 1.0); // Don't scale up
        int scaledWidth = (int) (originalImage.getWidth() * scale);
        int scaledHeight = (int) (originalImage.getHeight() * scale);
        // Create a scaled image for display
        BufferedImage displayImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = displayImage.createGraphics();
        g2.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g2.dispose();
        // Calculate initial square size (smaller dimension) in scaled coordinates
        int initialSize = Math.min(scaledWidth, scaledHeight);
        Rectangle cropRect = new Rectangle(
            (scaledWidth - initialSize) / 2,
            (scaledHeight - initialSize) / 2,
            initialSize,
            initialSize
        );
        // Panel to display image with crop rectangle
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(displayImage, 0, 0, this);
                // Draw crop rectangle
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillRect(0, 0, scaledWidth, cropRect.y);
                g2d.fillRect(0, cropRect.y, cropRect.x, scaledHeight - cropRect.y);
                g2d.fillRect(cropRect.x + cropRect.width, cropRect.y,
                    scaledWidth - (cropRect.x + cropRect.width), scaledHeight - cropRect.y);
                g2d.fillRect(cropRect.x, cropRect.y + cropRect.height, cropRect.width,
                    scaledHeight - (cropRect.y + cropRect.height));
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(cropRect.x, cropRect.y, cropRect.width, cropRect.height);
                // Draw resize handles at all four corners (gray)
                g2d.setColor(Color.GRAY);
                g2d.fillRect(cropRect.x - 5, cropRect.y - 5, 10, 10); // top-left
                g2d.fillRect(cropRect.x + cropRect.width - 5, cropRect.y - 5, 10, 10); // top-right
                g2d.fillRect(cropRect.x - 5, cropRect.y + cropRect.height - 5, 10, 10); // bottom-left
                g2d.fillRect(cropRect.x + cropRect.width - 5, cropRect.y + cropRect.height - 5, 10, 10); // bottom-right
            }
        };

        int[] dragStart = {0, 0};
        boolean[] isDragging = {false};
        boolean[] isResizing = {false};
        int[] resizeCorner = {-1}; // -1: none, 0: top-left, 1: top-right, 2: bottom-left, 3: bottom-right
        // Dragging and resizing of crop rectangle
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart[0] = e.getX();
                dragStart[1] = e.getY();
                // Check if clicking on any of the four resize handles
                Rectangle topLeft = new Rectangle(cropRect.x - 5, cropRect.y - 5, 10, 10);
                Rectangle topRight = new Rectangle(cropRect.x + cropRect.width - 5, cropRect.y - 5, 10, 10);
                Rectangle bottomLeft = new Rectangle(cropRect.x - 5, cropRect.y + cropRect.height - 5, 10, 10);
                Rectangle bottomRight = new Rectangle(cropRect.x + cropRect.width - 5, cropRect.y + cropRect.height - 5, 10, 10);
                // Check for a resize handle or dragging within the crop rectangle
                if (topLeft.contains(e.getPoint())) {
                    isResizing[0] = true;
                    resizeCorner[0] = 0;
                } else if (topRight.contains(e.getPoint())) {
                    isResizing[0] = true;
                    resizeCorner[0] = 1;
                } else if (bottomLeft.contains(e.getPoint())) {
                    isResizing[0] = true;
                    resizeCorner[0] = 2;
                } else if (bottomRight.contains(e.getPoint())) {
                    isResizing[0] = true;
                    resizeCorner[0] = 3;
                } else if (cropRect.contains(e.getPoint())) {
                    isDragging[0] = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging[0] = false;
                isResizing[0] = false;
                resizeCorner[0] = -1;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - dragStart[0];
                int dy = e.getY() - dragStart[1];
                if (isResizing[0]) {
                    // Resize (maintain square) from different corners
                    if (resizeCorner[0] == 0) { // top-left
                        int change = -Math.min(dx, dy);
                        int newSize = Math.max(50, cropRect.width + change);
                        int newX = cropRect.x + cropRect.width - newSize;
                        int newY = cropRect.y + cropRect.height - newSize;
                        if (newX >= 0 && newY >= 0) {
                            cropRect.x = newX;
                            cropRect.y = newY;
                            cropRect.width = newSize;
                            cropRect.height = newSize;
                            dragStart[0] = e.getX();
                            dragStart[1] = e.getY();
                        }
                    } else if (resizeCorner[0] == 1) { // top-right
                        int change = Math.max(dx, -dy);
                        int newSize = Math.max(50, cropRect.width + change);
                        int newY = cropRect.y + cropRect.height - newSize;
                        if (cropRect.x + newSize <= scaledWidth && newY >= 0) {
                            cropRect.y = newY;
                            cropRect.width = newSize;
                            cropRect.height = newSize;
                            dragStart[0] = e.getX();
                            dragStart[1] = e.getY();
                        }
                    } else if (resizeCorner[0] == 2) { // bottom-left
                        int change = Math.max(-dx, dy);
                        int newSize = Math.max(50, cropRect.width + change);
                        int newX = cropRect.x + cropRect.width - newSize;
                        if (newX >= 0 && cropRect.y + newSize <= scaledHeight) {
                            cropRect.x = newX;
                            cropRect.width = newSize;
                            cropRect.height = newSize;
                            dragStart[0] = e.getX();
                            dragStart[1] = e.getY();
                        }
                    } else if (resizeCorner[0] == 3) { // bottom-right
                        int delta2 = Math.max(dx, dy);
                        int newSize = Math.max(50, cropRect.width + delta2);
                        if (cropRect.x + newSize <= scaledWidth &&
                            cropRect.y + newSize <= scaledHeight) {
                            cropRect.width = newSize;
                            cropRect.height = newSize;
                            dragStart[0] = e.getX();
                            dragStart[1] = e.getY();
                        }
                    }
                } else if (isDragging[0]) {
                    // Move rectangle
                    int newX = cropRect.x + dx;
                    int newY = cropRect.y + dy;
                    // Ensure within bounds
                    if (newX >= 0 && newX + cropRect.width <= scaledWidth) {
                        cropRect.x = newX;
                        dragStart[0] = e.getX();
                    }
                    if (newY >= 0 && newY + cropRect.height <= scaledHeight) {
                        cropRect.y = newY;
                        dragStart[1] = e.getY();
                    }
                }
                imagePanel.repaint();
            }
        };

        imagePanel.addMouseListener(mouseAdapter);
        imagePanel.addMouseMotionListener(mouseAdapter);
        imagePanel.setPreferredSize(new Dimension(scaledWidth, scaledHeight));
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cropButton = new JButton(Res.getString("button.crop"));
        JButton cancelButton = new JButton(Res.getString("button.cancel"));

        cropButton.addActionListener(e -> {
            // Convert scaled coordinates back to original image coordinates
            int originalX = (int) (cropRect.x / scale);
            int originalY = (int) (cropRect.y / scale);
            int originalWidth = (int) (cropRect.width / scale);
            int originalHeight = (int) (cropRect.height / scale);
            croppedImage[0] = originalImage.getSubimage(originalX, originalY, originalWidth, originalHeight);
            cropDialog.dispose();
        });

        cancelButton.addActionListener(e -> cropDialog.dispose());

        buttonPanel.add(cropButton);
        buttonPanel.add(cancelButton);

        cropDialog.add(new JScrollPane(imagePanel), BorderLayout.CENTER);
        cropDialog.add(buttonPanel, BorderLayout.SOUTH);
        cropDialog.pack();
        cropDialog.setLocationRelativeTo(parent);
        cropDialog.setVisible(true);

        return croppedImage[0];
    }
}
