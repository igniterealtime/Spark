package org.jivesoftware.spark.ui;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;

/**
 * @author ps
 */
public class BroadcastHistoryFrame extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public BroadcastHistoryFrame() {
        BroadcastHistoryArea = new javax.swing.JTextArea();
        BroadcastHistoryArea.setEditable(false);
        BroadcastHistoryArea.setLineWrap(true);
        BroadcastHistoryArea.setWrapStyleWord(true);
        initComponents();
    }

    public void readFromFile(String date) throws IOException {
        String fileLocation = Spark.getSparkUserHome() + File.separator + "user" + File.separator + SparkManager.getSessionManager().getUsername() + "@" + SparkManager.getSessionManager().getServerAddress() + File.separator + "transcripts" + File.separator + "broadcast_history." + date + ".txt";
        File myfile = new File(fileLocation);
        if (!myfile.exists()) {
            return;
        }
        FileInputStream fis = new FileInputStream(myfile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;
        while ((line = br.readLine()) != null) {
            BroadcastHistoryArea.append(line + "\n");
        }
        br.close();
    }
    
    private void initComponents() {        
        SearchButton = new javax.swing.JToggleButton();
        DateField = new javax.swing.JFormattedTextField();
        SearchDate = new javax.swing.JLabel();
        Date date=new Date();
        Format formatter = new SimpleDateFormat("yyy-MM");
        String myDate = formatter.format(date);
        DateField.setValue(myDate);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        JScrollPane panelPane = new JScrollPane(BroadcastHistoryArea);
        SearchDate.setText(Res.getString("label.broadcast.history.search.date"));
        setTitle(Res.getString("title.broadcast.history"));
           
        try {
            readFromFile(myDate);
        } catch (IOException ex) {
            Log.error("Couldn't read from file", ex);
        }
       
        SearchButton.setText((Res.getString("button.search")));
        SearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
                SearchButtonMouseClicked(evt);
            }
        });
        

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPane, javax.swing.GroupLayout.DEFAULT_SIZE, 533, javax.swing.GroupLayout.DEFAULT_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                	.addComponent(SearchDate)
                    .addComponent(SearchButton)
                    .addComponent(DateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                   .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(panelPane, javax.swing.GroupLayout.DEFAULT_SIZE, 350, javax.swing.GroupLayout.DEFAULT_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(SearchDate)
                        .addGap(10, 10, 10)
                        .addComponent(DateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(SearchButton)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
  
        pack();

        final Rectangle bounds = LayoutSettingsManager.getLayoutSettings().getBroadcastHistoryBounds();
        if ( bounds == null || bounds.width <= 0 || bounds.height <= 0 )
        {
            // Use default settings.
            setLocationRelativeTo( null );
        }
        else
        {
            setBounds( bounds );
        }

        addComponentListener( new ComponentAdapter()
        {
            @Override
            public void componentResized( ComponentEvent e )
            {
                LayoutSettingsManager.getLayoutSettings().setBroadcastHistoryBounds( getBounds() );
            }

            @Override
            public void componentMoved( ComponentEvent e )
            {
                LayoutSettingsManager.getLayoutSettings().setBroadcastHistoryBounds( getBounds() );
            }
        } );
    }


    private void SearchButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SearchButtonMouseClicked
        BroadcastHistoryArea.setText("");
        try {
            readFromFile(DateField.getText());
        } catch (IOException ex) {
            Log.error("Couldn't read from file", ex);
        }
    }//GEN-LAST:event_SearchButtonMouseClicked

    public void run() {
    java.awt.EventQueue.invokeLater(() -> {
       BroadcastHistoryFrame frame= new BroadcastHistoryFrame();
       frame.setVisible(true);
       frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    });
}

   private javax.swing.JFormattedTextField DateField;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JTextArea  BroadcastHistoryArea;
    private javax.swing.JLabel SearchDate;
    private javax.swing.JToggleButton SearchButton;
    // End of variables declaration//GEN-END:variables
}
