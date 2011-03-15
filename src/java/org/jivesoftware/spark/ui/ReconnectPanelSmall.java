package org.jivesoftware.spark.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;

/**
 * Used for silent reconnecting, no slap in the face with connection loss
 * 
 * @author wolf.posdorfer
 * 
 */
public class ReconnectPanelSmall extends ContactGroup implements
	ConnectionListener {

    private static final long serialVersionUID = 437696141257704105L;
    private JLabel _reconnectionlabel = new JLabel(Res.getString("message.reconnect.attempting"),
	    SparkRes.getImageIcon(SparkRes.BUSY_IMAGE), 0);
    private Component thiscomp;

    /**
     * creates a new Panel
     * 
     * @param groupName
     */
    public ReconnectPanelSmall(String groupName) {
	super(groupName);
	this.add(_reconnectionlabel);
	this.setIcon(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));
	thiscomp = this;

	_reconnectionlabel.addMouseListener(new MouseListener() {

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON3) {

		    int x = e.getX();
		    int y = e.getY();

		    final JPopupMenu popup = new JPopupMenu();
		    final JMenuItem reconnect = new JMenuItem("Reconnect");
		    popup.add(reconnect);

		    reconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    reconnect();
			}
		    });

		    popup.show(thiscomp, x, y);
		}
	    }
	});
    }

    /**
     * Starts the Timer for no-panel-reconnection
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
    
    public void setReconnectText(String text)
    {
	_reconnectionlabel.setText(text);
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
