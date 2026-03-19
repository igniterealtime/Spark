/**
 * Copyright (C) 2026 Ignite Realtime. All rights reserved.
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
package org.jivesoftware.sparkimpl.plugin.scratchpad;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.awt.GridBagConstraints.*;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;

public class NotepadUI {

    public static void showNotepad(final Notepad notepad) {
        final JFrame frame = new JFrame(Res.getString("button.view.notepad"));
        frame.setIconImage(SparkManager.getMainWindow().getIconImage());
        frame.setLayout(new BorderLayout());

        final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JPanel notesPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 5, 5, true, false));
        final JScrollPane scrollPane = new JScrollPane(notesPanel);

        final String[] currentFilter = {null};

        final Runnable refreshUI = new Runnable() {
            @Override
            public void run() {
                // Refresh Tags
                topPanel.removeAll();
                Set<String> allTags = new TreeSet<>();
                for (Notepad.NotepadNote note : notepad.getNotes()) {
                    allTags.addAll(note.getTags());
                }

                JLabel allLabel = new JLabel("All");
                allLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                allLabel.setForeground(currentFilter[0] == null ? Color.BLUE : Color.BLACK);
                allLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        currentFilter[0] = null;
                        run();
                    }
                });
                topPanel.add(allLabel);

                for (final String tag : allTags) {
                    JLabel tagLabel = new JLabel(tag);
                    tagLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    tagLabel.setForeground(tag.equals(currentFilter[0]) ? Color.BLUE : Color.BLACK);
                    tagLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            currentFilter[0] = tag;
                            run();
                        }
                    });
                    topPanel.add(tagLabel);
                }

                topPanel.add(Box.createHorizontalGlue());
                RolloverButton newNoteButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.Icon.ADD_IMAGE_24x24));
                ResourceUtils.resButton(newNoteButton, Res.getString("button.new"));
                newNoteButton.addActionListener(e -> {
                    Notepad.NotepadNote newNote = new Notepad.NotepadNote();
                    newNote.setTitle("");
                    newNote.setText("");
                    newNote.setTags(List.of());
                    notepad.getNotes().add(newNote);
                    showEditNote(newNote, notepad, frame, this);
                });
                topPanel.add(newNoteButton);

                topPanel.revalidate();
                topPanel.repaint();

                // Refresh Notes
                notesPanel.removeAll();
                for (final Notepad.NotepadNote note : notepad.getNotes()) {
                    if (currentFilter[0] == null || note.getTags().contains(currentFilter[0])) {
                        JPanel noteUI = createNoteUI(note, notepad, frame, currentFilter, this);
                        notesPanel.add(noteUI);
                    }
                }
                notesPanel.revalidate();
                notesPanel.repaint();
            }
        };

        refreshUI.run();

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(500, 600);
        GraphicUtils.centerWindowOnComponent(frame, SparkManager.getMainWindow());
        frame.setVisible(true);
    }

    private static JPanel createNoteUI(final Notepad.NotepadNote note, final Notepad notepad, final JFrame parentFrame, final String[] currentFilter, final Runnable refreshUI) {
        JPanel panel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 5, 5, true, false));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.setBackground(Color.WHITE);

        String title = note.getTitle() != null ? note.getTitle() : "No Title";
        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        panel.add(titleLabel);

        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tagsPanel.setOpaque(false);
        if (note.getTags() != null) {
            for (final String tag : note.getTags()) {
                JLabel tagLabel = new JLabel(tag);
                tagLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                tagLabel.setForeground(Color.BLUE);
                tagLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        currentFilter[0] = tag;
                        refreshUI.run();
                    }
                });
                tagsPanel.add(tagLabel);
            }
        }
        panel.add(tagsPanel);

        String text = note.getText() != null ? note.getText() : "";
        if (text.length() > 100) {
            text = text.substring(0, 100) + "... More...";
        }
        JLabel textLabel = new JLabel("<html>" + text.replace("\n", "<br>") + "</html>");
        panel.add(textLabel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditNote(note, notepad, parentFrame, refreshUI);
                }
            }
        });

        return panel;
    }


    private static void showEditNote(final Notepad.NotepadNote note, final Notepad notepad, JFrame parentFrame, final Runnable refreshUI) {
        final JFrame editFrame = new JFrame("Edit: " + (note.getTitle() != null ? note.getTitle() : ""));
        editFrame.setIconImage(SparkManager.getMainWindow().getIconImage());
        editFrame.setLayout(new GridBagLayout());

        final JTextField titleField = new JTextField(note.getTitle());
        final JTextField tagsField = new JTextField(note.getTags() != null ? String.join(" ", note.getTags()) : "");
        final JTextArea textArea = new JTextArea(note.getText());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    saveNoteFields(note, titleField, tagsField, textArea, notepad);
                    refreshUI.run();
                    editFrame.dispose();
                }
            }
        });

        RolloverButton deleteButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.Icon.SMALL_DELETE));
        ResourceUtils.resButton(deleteButton, Res.getString("delete"));
        RolloverButton closeButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.Icon.CLOSE_IMAGE));
        ResourceUtils.resButton(closeButton, Res.getString("close"));
        RolloverButton saveButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.Icon.SAVE_AS_16x16));
        ResourceUtils.resButton(saveButton, Res.getString("save"));

        Insets insets = new Insets(5, 5, 5, 5);
        editFrame.add(new JLabel("Title"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets, 0, 0));
        editFrame.add(titleField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));
        editFrame.add(new JLabel("Tags"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets, 0, 0));
        editFrame.add(tagsField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));
        editFrame.add(new JScrollPane(textArea), new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, WEST, BOTH, insets, 0, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        buttonPanel.add(saveButton);
        editFrame.add(buttonPanel, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, EAST, HORIZONTAL, insets, 0, 0));

        saveButton.addActionListener(e -> {
            saveNoteFields(note, titleField, tagsField, textArea, notepad);
            refreshUI.run();
            editFrame.dispose();
        });

        closeButton.addActionListener(e -> editFrame.dispose());

        deleteButton.addActionListener(e -> {
            notepad.getNotes().remove(note);
            updateNotepad(notepad);
            refreshUI.run();
            editFrame.dispose();
        });

        editFrame.pack();
        editFrame.setSize(480, 500);
        GraphicUtils.centerWindowOnComponent(editFrame, parentFrame);
        editFrame.setVisible(true);
    }

    private static void saveNoteFields(Notepad.NotepadNote note, JTextField titleField, JTextField tagsField, JTextArea textArea, Notepad notepad) {
        note.setTitle(titleField.getText());
        List<String> tagList = Arrays.stream(tagsField.getText().split("\\s+")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        note.setTags(tagList);
        note.setText(textArea.getText());
        updateNotepad(notepad);
    }

    private static void updateNotepad(Notepad notepad) {
        Log.warning("Update notepad");
        Notepad.saveNotepad(notepad);
    }

}
