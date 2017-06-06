package org.jivesoftware.spark.ui.login;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.jivesoftware.resource.Res;
import org.jivesoftware.sparkimpl.certificates.CertificateModel;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

public class CertificateDialog extends JDialog {

	private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
	private final LocalPreferences localPreferences;
	private CertificateModel cert;

	public CertificateDialog(LocalPreferences localPreferences, CertificateModel cert) {
		this.localPreferences = localPreferences;
		this.cert = cert;
		this.setTitle(Res.getString("title.certificate"));
		this.setSize(400, 500);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dimension.width / 2 - this.getWidth(), dimension.height / 2 - this.getHeight() / 2);
		this.setModal(true);
		this.isAlwaysOnTop();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
