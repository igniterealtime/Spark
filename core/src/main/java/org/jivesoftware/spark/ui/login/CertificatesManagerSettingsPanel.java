package org.jivesoftware.spark.ui.login;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;

import javax.naming.InvalidNameException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.certificates.CertificateController;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * This class serve as visual in implementation of manageable list of
 * certificates. Together with CertificateController and CertificateModel
 * Classes this apply MVC pattern.
 *
 * @author Paweł Ścibiorski
 *
 */
public class CertificatesManagerSettingsPanel extends JPanel implements ActionListener, MouseListener, TableModelListener {

    private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private final LocalPreferences localPreferences;

    //table with certificates
    private final CertificateController certControll;
    private static JTable certTable;
    private final JButton showCert = new JButton();

    private JScrollPane scrollPane;

    //add certificate utilities
    private final JFileChooser fileChooser = new JFileChooser();
    private final JButton fileButton = new JButton();
    private final JPanel filePanel = new JPanel();
    private final FileNameExtensionFilter certFilter = new FileNameExtensionFilter(
            Res.getString("menuitem.certificate.files.filter"), "cer", "crt", "der", "pem");

    //checboxes with options
    private final JCheckBox acceptExpired = new JCheckBox();
    private final JCheckBox acceptRevoked = new JCheckBox();
    private final JCheckBox acceptSelfSigned = new JCheckBox();
    private final JCheckBox checkCRL = new JCheckBox();
    private final JCheckBox checkOCSP = new JCheckBox();
    private final JCheckBox allowSoftFail = new JCheckBox();
    private final JCheckBox acceptNotValidYet = new JCheckBox();

    public CertificatesManagerSettingsPanel(LocalPreferences localPreferences, JDialog optionsDialog) {

        this.localPreferences = localPreferences;

        certControll = new CertificateController(localPreferences);
        setLayout(new GridBagLayout());

        addCertTableToPanel();

        ResourceUtils.resButton(acceptExpired, Res.getString("checkbox.accept.expired"));
        ResourceUtils.resButton(acceptNotValidYet, Res.getString("checkbox.accept.not.valid.yet"));
        ResourceUtils.resButton(acceptRevoked, Res.getString("checkbox.accept.revoked"));
        ResourceUtils.resButton(acceptSelfSigned, Res.getString("checkbox.accept.self.signed"));
        ResourceUtils.resButton(checkCRL, Res.getString("checkbox.check.crl"));
        ResourceUtils.resButton(checkOCSP, Res.getString("checkbox.check.ocsp"));
        ResourceUtils.resButton(allowSoftFail, Res.getString("checkbox.allow.soft.fail"));
        ResourceUtils.resButton(showCert, Res.getString("button.show.certificate"));
        ResourceUtils.resButton(fileButton, Res.getString("label.choose.file"));

        acceptSelfSigned.setSelected(localPreferences.isAcceptSelfSigned());
        acceptExpired.setSelected(localPreferences.isAcceptExpired());
        acceptNotValidYet.setSelected(localPreferences.isAcceptNotValidYet());
        acceptRevoked.setSelected(localPreferences.isAcceptRevoked());
        checkCRL.setSelected(localPreferences.isCheckCRL());
        checkOCSP.setSelected(localPreferences.isCheckOCSP());
        allowSoftFail.setSelected(localPreferences.isAllowSoftFail());

        certTable.addMouseListener(this);
        certTable.getModel().addTableModelListener(this);
        showCert.setEnabled(false);
        showCert.addActionListener(this);
        fileButton.addActionListener(this);
        checkCRL.addActionListener(this);
        checkOCSP.addActionListener(this);
        acceptRevoked.addActionListener(this);

        checkCRL.setEnabled(!acceptRevoked.isSelected());
        checkOCSP.setEnabled(checkCRL.isSelected());
        allowSoftFail.setEnabled(checkOCSP.isSelected());
        filePanel.setLayout(new GridBagLayout());
        filePanel.add(fileButton, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 40, 0));
        filePanel.setBorder(
                BorderFactory.createTitledBorder(Res.getString("label.certificate.add.certificate.to.truststore")));

        add(scrollPane, new GridBagConstraints(0, 0, 6, 1, 1.0, 0.8, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));

        add(acceptSelfSigned, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.1, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));

        add(acceptExpired, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.1, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        add(acceptNotValidYet, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.1, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));

        add(acceptRevoked, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.1, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        add(checkCRL, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.1, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));

        add(checkOCSP, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.1, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        add(allowSoftFail, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.1, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));

        add(showCert, new GridBagConstraints(4, 1, 2, 1, 0.0, 0.1, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        add(filePanel, new GridBagConstraints(4, 2, 2, 4, 0.0, 0.1, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
    }

    public void addCertTableToPanel() {
        certControll.loadKeyStores();
        certControll.createTableModel();
        certTable = new JTable(certControll.getTableModel()) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int rowIndex,
                    int columnIndex) {
                JComponent component = (JComponent) super.prepareRenderer(renderer, rowIndex, columnIndex);
                if (isRowSelected(rowIndex)) {
                    component.setBackground(getBackground());
                    component.setForeground(Color.black);
                }
                Object value = getModel().getValueAt(convertRowIndexToModel(rowIndex), columnIndex);

                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();

                centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                this.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

                if (value.equals(Res.getString("cert.valid"))) {
                    component.setBackground(Color.green);
                } else if (value.equals(Res.getString("cert.expired")) || value
                        .equals(Res.getString("cert.not.valid.yet")) || value.equals(Res.getString("cert.revoked"))) {
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

        certControll.resizeColumnWidth(certTable);
        certTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == showCert) {
            certControll.showCertificate();

        } else if (e.getSource() == fileButton) {
            addCertificate();

        } else if (e.getSource() == checkCRL) {

            if (checkCRL.isSelected()) {

                checkOCSP.setEnabled(true);
                allowSoftFail.setEnabled(true);
            } else if (!checkCRL.isSelected()) {

                checkOCSP.setSelected(false);
                checkOCSP.setEnabled(false);
                allowSoftFail.setEnabled(false);
                allowSoftFail.setSelected(false);
            }

        } else if (e.getSource() == acceptRevoked) {
            if (acceptRevoked.isSelected()) {

                checkCRL.setSelected(false);
                checkOCSP.setSelected(false);
                allowSoftFail.setSelected(false);

                checkCRL.setEnabled(false);
                checkOCSP.setEnabled(false);
                allowSoftFail.setEnabled(false);
            } else if (!acceptRevoked.isSelected()) {
                checkCRL.setEnabled(true);
            }
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
        if (e.getSource() == certTable) {
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
                certControll.addEntryFileToKeyStore(file);
            } catch (CertificateException e) {
                JOptionPane.showMessageDialog(null, Res.getString("dialog.cannot.upload.certificate.might.be.ill.formatted"));
                Log.error("Cannot upload certificate file", e);
            } catch (KeyStoreException | InvalidNameException | IOException e) {
                JOptionPane.showMessageDialog(null, "dialog.cannot.upload.certificate");
                Log.error("Cannot upload certificate file", e);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

    }

    public static JTable getCertTable() {
        return certTable;
    }

    public void useDefault() {
        acceptExpired.setSelected(Default.getBoolean(Default.ACCEPT_EXPIRED));
        acceptNotValidYet.setSelected(Default.getBoolean(Default.ACCEPT_NOT_VALID_YET));
        acceptSelfSigned.setSelected(Default.getBoolean(Default.ACCEPT_SELF_SIGNED));
        acceptRevoked.setSelected(Default.getBoolean(Default.ACCEPT_REVOKED));
        checkCRL.setSelected(Default.getBoolean(Default.CHECK_CRL));
        checkOCSP.setSelected(Default.getBoolean(Default.CHECK_OCSP));
        allowSoftFail.setSelected(Default.getBoolean(Default.ALLOW_SOFT_FAIL));

        acceptExpired.setEnabled(true);
        acceptNotValidYet.setEnabled(true);
        acceptSelfSigned.setEnabled(true);
        acceptRevoked.setEnabled(true);
        checkCRL.setEnabled(true);
        checkOCSP.setEnabled(true);
        allowSoftFail.setEnabled(true);
    }

    public void saveSettings() {
        localPreferences.setAcceptExpired(acceptExpired.isSelected());
        localPreferences.setAcceptNotValidYet(acceptNotValidYet.isSelected());
        localPreferences.setAcceptSelfSigned(acceptSelfSigned.isSelected());
        localPreferences.setAcceptRevoked(acceptRevoked.isSelected());
        localPreferences.setCheckCRL(checkCRL.isSelected());
        localPreferences.setCheckOCSP(checkOCSP.isSelected());
        localPreferences.setAllowSoftFail(allowSoftFail.isSelected());
        certControll.overWriteKeyStores();
    }
}
