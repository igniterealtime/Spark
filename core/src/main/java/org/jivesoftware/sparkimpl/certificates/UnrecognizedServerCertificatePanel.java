package org.jivesoftware.sparkimpl.certificates;

import org.jivesoftware.resource.Res;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.LINE_END;
import static java.awt.GridBagConstraints.LINE_START;

public class UnrecognizedServerCertificatePanel extends JPanel {
    private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);

    private final CertificateModel certModel;

    public UnrecognizedServerCertificatePanel( CertificateModel certModel) {
        if (certModel == null) {
            throw new IllegalArgumentException("Certificate Model cannot be null");
        }
        this.certModel = certModel;

        setLayout(new GridBagLayout());
        add( new JLabel(Res.getString("dialog.certificate.unrecognized.server.certificate")), new GridBagConstraints(0, 0, 2, 1, 1, 0, LINE_START, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        final JLabel subjectLabel = new JLabel( Res.getString("label.certificate.subject") );
        subjectLabel.setHorizontalAlignment(SwingConstants.RIGHT );
        add( subjectLabel, new GridBagConstraints(0, 1, 1, 1, 0.1, 0, LINE_END, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        final JTextField subjectNameField = new JTextField();
        subjectNameField.setEditable(false);
        subjectNameField.setText( certModel.getSubjectCommonName() != null ? certModel.getSubjectCommonName() : certModel.getSubject() );
        add( subjectNameField, new GridBagConstraints(1, 1, 1, 1, 1, 0, LINE_START, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        final JLabel issuerLabel = new JLabel( Res.getString("label.certificate.issuer") );
        issuerLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        add( issuerLabel, new GridBagConstraints(0, 2, 1, 1, 0.1, 0, LINE_END, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        final JTextField issuerNameField = new JTextField();
        issuerNameField.setEditable(false);
        issuerNameField.setText( certModel.getIssuerCommonName() != null ? certModel.getIssuerCommonName() : certModel.getIssuer() );
        add( issuerNameField, new GridBagConstraints(1, 2, 1, 1, 1, 0, LINE_START, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        final JLabel validityFromLabel = new JLabel( Res.getString("label.certificate.not.before") );
        validityFromLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        add( validityFromLabel, new GridBagConstraints(0, 3, 1, 1, 0.1, 0, LINE_END, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        final JTextField validityFromField = new JTextField();
        validityFromField.setEditable(false);
        validityFromField.setText( certModel.getNotBefore() );
        add( validityFromField, new GridBagConstraints(1, 3, 1, 1, 1, 0, LINE_START, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        final JLabel validityUntilLabel = new JLabel( Res.getString("label.certificate.not.after") );
        validityUntilLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        add( validityUntilLabel, new GridBagConstraints(0, 4, 1, 1, 0.1, 0, LINE_END, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        final JTextField validityUntilField = new JTextField();
        validityUntilField.setEditable(false);
        validityUntilField.setText( certModel.getNotAfter() );
        add( validityUntilField, new GridBagConstraints(1, 4, 1, 1, 1, 0, LINE_START, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        add( new JLabel(Res.getString("dialog.certificate.add.unrecognized.server.certificate")), new GridBagConstraints(0, 5, 2, 1, 1, 0, LINE_START, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
    }
}
