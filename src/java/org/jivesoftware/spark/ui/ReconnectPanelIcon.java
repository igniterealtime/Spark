package org.jivesoftware.spark.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;

/**
 * Used for silent reconnecting, no slap in the face with connection loss
 * Displays a little Icon at first position
 * 
 * @author wolf.posdorfer
 * 
 */
public class ReconnectPanelIcon implements ConnectionListener {

    private static final long serialVersionUID = 437696141257704105L;
    private JButton _button;
    private JPanel _commandpanel;


    /**
     * creates a new Panel
     * 
     * @param groupName
     */
    public ReconnectPanelIcon() {

	_commandpanel = SparkManager.getWorkspace().getCommandPanel();

	_button = new JButton(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));
	_button.setBorder(BorderFactory.createBevelBorder(0, new Color(255, 0,
		0), new Color(255, 0, 0)));
	_button.setBorderPainted(true);

	_button.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		startReconnecting();
	    }
	});
    }

    public JPanel getPanel() {
	return _commandpanel;
    }

    public JButton getButton() {
	return _button;
    }

    /**
     * Starts the Timer for Icon-reconnection
     */
    public void startReconnecting() {
	if (!SparkManager.getConnection().isConnected()) {
	    TimerTask task = new SwingTimerTask() {
		public void doRun() {
		    reconnect();
		}
	    };
	    TaskEngine.getInstance().schedule(task, 100);
	}

	SparkManager.getPreferenceManager();

    }

    /**
     * Reconnect Thread
     */
    private void reconnect() {
	try {
	    SparkManager.getConnection().connect();
	} catch (Exception ex) {
	    // ex.printStackTrace();
	    // ...dont need to flood the errorlog with this
	}
    }

    public void setReconnectText(String text) {
	_button.setToolTipText(text);
    }

    public void remove() {
	SwingWorker worker = new SwingWorker() {
	    @Override
	    public Object construct() {

		_commandpanel.remove(_button);
		_commandpanel.revalidate();
		return 42; // this solves everything
	    }
	};
	worker.start();
	
    }

    @Override
    public void connectionClosed() {
    }

    @Override
    public void connectionClosedOnError(Exception e) {
    }

    @Override
    public void reconnectingIn(int seconds) {
    }

    @Override
    public void reconnectionSuccessful() {
    }

    @Override
    public void reconnectionFailed(Exception e) {
    }

}
