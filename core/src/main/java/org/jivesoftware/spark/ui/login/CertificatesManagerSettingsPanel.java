package org.jivesoftware.spark.ui.login;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.naming.InvalidNameException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.certificates.CertificateController;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * This class serve as visual in implementation of manageable list of certificates. Together with CertificateController
 * and CertificateModel Classes this apply MVC pattern.
 * 
 * @author Paweł Ścibiorski
 *
 */
public class CertificatesManagerSettingsPanel extends JPanel implements ActionListener, MouseListener, TableModelListener {

	private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
	private final LocalPreferences localPreferences;
	private CertificateController certControll;
	private static JTable certTable;
	private JCheckBox acceptAll = new JCheckBox();
	private JCheckBox acceptExpired = new JCheckBox();
	private JCheckBox acceptRevoked = new JCheckBox();
	private JCheckBox acceptSelfSigned = new JCheckBox();
	private JCheckBox checkCRL = new JCheckBox();
	private JCheckBox checkOCSP = new JCheckBox();
	private JButton showCert = new JButton();
	private JFileChooser fileChooser = new JFileChooser();
	private JButton fileButton = new JButton();
	private JScrollPane scrollPane;
	private JPanel filePanel = new JPanel();
	private FileNameExtensionFilter certFilter = new FileNameExtensionFilter(Res.getString("menuitem.certificate.files.filter"),"cer", "crt", "der");

	public CertificatesManagerSettingsPanel(LocalPreferences localPreferences, JDialog optionsDialog) {

		this.localPreferences = localPreferences;
		certControll = new CertificateController(localPreferences);
		setLayout(new GridBagLayout());
		certControll.createCertTableModel();
		certTable = new JTable(certControll.getTableModel()){
			
			@Override
	        public Component prepareRenderer(TableCellRenderer renderer, int rowIndex,
	                int columnIndex) {
	            JComponent component = (JComponent) super.prepareRenderer(renderer, rowIndex, columnIndex);  
	            Object value = getModel().getValueAt(rowIndex, columnIndex);
	            
	    		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
	    		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
	    		this.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
	    		
	            if (value.equals(Res.getString("cert.self.signed"))) {
	                component.setBackground(Color.lightGray);
	            } else if (value.equals(Res.getString("cert.valid"))) {
	                component.setBackground(Color.green);
	            } else if(value.equals(Res.getString("cert.expired")) || value.equals(Res.getString("cert.not.valid.yet"))){
	            	component.setBackground(Color.red);
	            } else {
	               component.setBackground(Color.white);
	            }
	            return component;
	        }
			
		};

		
		scrollPane = new JScrollPane(certTable);
		certTable.setFillsViewportHeight(true);
		certTable.setAutoCreateRowSorter(true);
		
		resizeColumnWidth(certTable);
		certTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

		ResourceUtils.resButton(acceptAll, Res.getString("checkbox.accept.all"));
		ResourceUtils.resButton(acceptExpired, Res.getString("checkbox.accept.expired"));
		ResourceUtils.resButton(acceptRevoked, Res.getString("checkbox.accept.revoked"));
		ResourceUtils.resButton(acceptSelfSigned, Res.getString("checkbox.accept.self.signed"));
		ResourceUtils.resButton(checkCRL, Res.getString("checkbox.check.crl"));
		ResourceUtils.resButton(checkOCSP, Res.getString("checkbox.check.ocsp"));
		ResourceUtils.resButton(showCert, Res.getString("button.show.certificate"));
		ResourceUtils.resButton(fileButton, Res.getString("label.choose.file"));
		
		acceptAll.setSelected(localPreferences.isAcceptAllCertificates());
		acceptSelfSigned.setSelected(localPreferences.isAcceptSelfSigned());
		acceptExpired.setSelected(localPreferences.isAcceptExpired());
		acceptRevoked.setSelected(localPreferences.isAcceptRevoked());
		checkCRL.setSelected(localPreferences.isCheckCRL());
		checkOCSP.setSelected(localPreferences.isCheckOCSP());
		
		acceptAll.addActionListener(this);
		certTable.addMouseListener(this);
		certTable.getModel().addTableModelListener(this);
		showCert.setEnabled(false);
		showCert.addActionListener(this);
		fileButton.addActionListener(this);
		checkCRL.addActionListener(this);
		acceptRevoked.addActionListener(this);
		checkCRL.setEnabled(!acceptRevoked.isSelected());
		checkOCSP.setEnabled(checkCRL.isSelected());

		filePanel.setLayout(new GridBagLayout());
		filePanel.add(fileButton, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 40, 0));
		filePanel.setBorder(
				BorderFactory.createTitledBorder(Res.getString("label.certificate.add.certificate.to.truststore")));

		add(scrollPane, new GridBagConstraints(0, 0, 6, 1, 1.0, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptAll, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptSelfSigned, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptExpired, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptRevoked, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(checkCRL, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(checkOCSP, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(showCert, new GridBagConstraints(2, 1, 2, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 40, 0));
		add(filePanel, new GridBagConstraints(2, 2, 2, 4, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 40, 0));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == acceptAll && acceptAll.isSelected()) {
			acceptSelfSigned.setSelected(true);
			acceptExpired.setSelected(true);
			acceptRevoked.setSelected(true);

			acceptSelfSigned.setEnabled(false);
			acceptExpired.setEnabled(false);
			acceptRevoked.setEnabled(false);
		} else if (e.getSource() == acceptAll && !acceptAll.isSelected()) {
			acceptSelfSigned.setEnabled(true);
			acceptExpired.setEnabled(true);
			acceptRevoked.setEnabled(true);
		} else if (e.getSource() == showCert) {
			certControll.showCertificate();
		} else if (e.getSource() == fileButton) {
			addCertificate();
        } else if (e.getSource() == checkCRL && checkCRL.isSelected()) {
            checkOCSP.setEnabled(true);
        } else if (e.getSource() == checkCRL && !checkCRL.isSelected()) {
            checkOCSP.setSelected(false);
            checkOCSP.setEnabled(false);
        } else if (e.getSource() == acceptRevoked && acceptRevoked.isSelected()) {
            checkCRL.setSelected(false);
            checkOCSP.setSelected(false);

            checkCRL.setEnabled(false);
            checkOCSP.setEnabled(false);
        } else if (e.getSource() == acceptRevoked && !acceptRevoked.isSelected()) {
            checkCRL.setEnabled(true);
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() == 2) {
			JTable source = (JTable) e.getSource();
			if (e.getSource() == certTable && source.getSelectedColumn() != 2) {
				certControll.showCertificate();
			}
		}
		if(e.getSource() == certTable){
			showCert.setEnabled(true);
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
	    int column = e.getColumn();
	    if (column == 2) {
	        TableModel model = (TableModel) e.getSource();
	        Boolean checked = (Boolean) model.getValueAt(row, column);
			certControll.addOrRemoveFromExceptionList(checked);
		}

	}
	
	private void addCertificate() {

		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(certFilter);
		fileChooser.setFileFilter(certFilter);

		int retVal = fileChooser.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {

			File file = fileChooser.getSelectedFile();
			try {
				certControll.addCertificateToKeystore(file);
			} catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException ex) {
				Log.error("Cannot upload certificate file", ex);
			} catch (IllegalArgumentException ex) {
				Log.warning("Certificate or it's alias cannot be null", ex);
			} catch (HeadlessException | InvalidNameException ex) {
				Log.error("Error at setting certificate alias", ex);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

	public static JTable getCertTable() {
		return certTable;
	}

	private void resizeColumnWidth(JTable table) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				final TableColumnModel columnModel = table.getColumnModel();
				final int maxWidth = certTable.getParent().getWidth();
				columnModel.getColumn(1).setPreferredWidth(80);
				columnModel.getColumn(2).setPreferredWidth(60);
				columnModel.getColumn(0).setPreferredWidth(maxWidth - 140);
			}
		});
	}
	
    public void saveSettings() {
        localPreferences.setAcceptExpired(acceptExpired.isSelected());
        localPreferences.setAcceptSelfSigned(acceptSelfSigned.isSelected());
        localPreferences.setAcceptRevoked(acceptRevoked.isSelected());
        localPreferences.setAcceptAllCertificates(acceptAll.isSelected());
        localPreferences.setCheckCRL(checkCRL.isSelected());
        localPreferences.setCheckOCSP(checkOCSP.isSelected());

        SettingsManager.saveSettings();
    }
}
