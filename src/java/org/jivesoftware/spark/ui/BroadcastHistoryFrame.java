package org.jivesoftware.spark.ui;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.spark.SparkManager;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ps
 */
public class BroadcastHistoryFrame extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public BroadcastHistoryFrame() {
        jTextArea1 = new javax.swing.JTextArea();
        jTextArea1.setEditable(false);
        initComponents();
    }
    
    public void readFromFile(String date) throws FileNotFoundException, IOException
    {
        String fileName = Spark.getSparkUserHome()+File.separator+"broadcast_history."+date+".txt";
        String fileLocation=Spark.getSparkUserHome()+File.separator+"user"+File.separator+SparkManager.getSessionManager().getUsername()+"@"+SparkManager.getSessionManager().getServerAddress()+File.separator+"transcripts"+File.separator+fileName;
        File myfile = new File(fileLocation);
       FileInputStream fis = new FileInputStream(myfile);
 
	
	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        
	String line = null;
       
	while ((line = br.readLine()) != null) {
            jTextArea1.append(line+"\n");
            }
 
	br.close();
       
        
 
}
    private void initComponents()
    {
        
        
        jToggleButton1 = new javax.swing.JToggleButton();
        JFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        Date date=new Date();
        Format formatter = new SimpleDateFormat("yyy-MM");
        String myDate = formatter.format(date);
        JFormattedTextField1.setValue(myDate);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        JScrollPane panelPane = new JScrollPane(jTextArea1);
        jLabel1.setText("Input Date to find broadcast history in format yyyy-MM");
        setTitle(Res.getString("title.broadcast_history"));
        
        
       
            
        try {
            readFromFile(myDate);
        } catch (IOException ex) {
            Log.error("Couldn't read from file"+ex.getMessage()+ex.getStackTrace());
        }
       
        jToggleButton1.setText("Search");
         jToggleButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseClicked(evt);
            }
        });
       

       
        

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPane, javax.swing.GroupLayout.DEFAULT_SIZE, 533, javax.swing.GroupLayout.DEFAULT_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton1)
                    .addComponent(JFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                   .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(panelPane, javax.swing.GroupLayout.DEFAULT_SIZE, 301, javax.swing.GroupLayout.DEFAULT_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(JFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jToggleButton1)))
                .addContainerGap(79, Short.MAX_VALUE))
        );
  
        pack();  
        setLocationRelativeTo(null);
     }


    private void jToggleButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseClicked
        // TODO add your handling code here:
        jTextArea1.setText("");
       
        
        try {
            readFromFile(JFormattedTextField1.getText());
        } catch (IOException ex) {
            Log.error("Couldn't read from file"+ex.getCause()+ex.getStackTrace());
        }
       
        
    }//GEN-LAST:event_jToggleButton1MouseClicked
public void run()
{
    java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               BroadcastHistoryFrame frame= new BroadcastHistoryFrame();
               frame.setVisible(true);
               frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            }
            
        });
}
    /**
     * @param args the command line arguments
     */

   private javax.swing.JFormattedTextField JFormattedTextField1;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea  jTextArea1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
