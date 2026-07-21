package org.jivesoftware.spark.ui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;

import static java.awt.EventQueue.invokeLater;
import static javax.swing.LayoutStyle.ComponentPlacement;

/**
 * @author ps
 */
public class BroadcastHistoryFrame extends JFrame {
    private JFormattedTextField dateField;
    private JTextArea broadcastHistoryArea;
    private JLabel searchDate;
    private JToggleButton searchButton;


    public BroadcastHistoryFrame() {
        initComponents();
    }

    public void readFromFile(String date) throws IOException {
        File transcriptsFolder = SparkManager.getTranscriptDir();
        File myfile = new File(transcriptsFolder, "broadcast_history." + date + ".txt");
        if (!myfile.exists()) {
            return;
        }
        String content = Files.readString(myfile.toPath());
        broadcastHistoryArea.setText(content);
    }

    private void initComponents() {
        broadcastHistoryArea = new JTextArea();
        broadcastHistoryArea.setEditable(false);
        broadcastHistoryArea.setLineWrap(true);
        broadcastHistoryArea.setWrapStyleWord(true);

        searchButton = new JToggleButton();
        dateField = new JFormattedTextField();
        searchDate = new JLabel();
        Date date = new Date();
        Format formatter = new SimpleDateFormat("yyy-MM");
        String myDate = formatter.format(date);
        dateField.setValue(myDate);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JScrollPane panelPane = new JScrollPane(broadcastHistoryArea);
        searchDate.setText(Res.getString("label.broadcast.history.search.date"));
        setTitle(Res.getString("title.broadcast.history"));

        try {
            readFromFile(myDate);
        } catch (IOException ex) {
            Log.error("Couldn't read from file", ex);
        }

        searchButton.setText((Res.getString("button.search")));
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchButtonMouseClicked(evt);
            }
        });


        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelPane, GroupLayout.DEFAULT_SIZE, 533, GroupLayout.DEFAULT_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(searchDate)
                        .addComponent(searchButton)
                        .addComponent(dateField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(15, 15, 15)
                            .addComponent(panelPane, GroupLayout.DEFAULT_SIZE, 350, GroupLayout.DEFAULT_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(15, 15, 15)
                            .addComponent(searchDate)
                            .addGap(10, 10, 10)
                            .addComponent(dateField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(searchButton)))
                    .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();

        Rectangle bounds = LayoutSettingsManager.getLayoutSettings().getBroadcastHistoryBounds();
        if (bounds == null || bounds.width <= 0 || bounds.height <= 0) {
            // Use default settings.
            setLocationRelativeTo(null);
        } else {
            setBounds(bounds);
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LayoutSettingsManager.getLayoutSettings().setBroadcastHistoryBounds(getBounds());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                LayoutSettingsManager.getLayoutSettings().setBroadcastHistoryBounds(getBounds());
            }
        });
    }


    private void searchButtonMouseClicked(java.awt.event.MouseEvent evt) {
        broadcastHistoryArea.setText("");
        try {
            readFromFile(dateField.getText());
        } catch (IOException ex) {
            Log.error("Couldn't read from file", ex);
        }
    }

    public void run() {
        invokeLater(() -> {
            BroadcastHistoryFrame frame = new BroadcastHistoryFrame();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        });
    }

}
