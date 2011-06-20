package battleship;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;

import battleship.listener.ChatRoomOpeningListener;
import battleship.packets.GameOfferPacket;
import battleship.packets.MoveAnswerPacket;
import battleship.packets.MovePacket;


public class BattleshipPlugin implements Plugin{

    
    private PacketListener _gameofferListener;
    private ChatRoomOpeningListener _chatRoomListener;
    
    @Override
    public void initialize() {
	
	ProviderManager.getInstance().addIQProvider(GameOfferPacket.ELEMENT_NAME, GameOfferPacket.NAMESPACE, GameOfferPacket.class);
	ProviderManager.getInstance().addExtensionProvider(MovePacket.ELEMENT_NAME, MovePacket.NAMESPACE, MovePacket.class);
	ProviderManager.getInstance().addExtensionProvider(MoveAnswerPacket.ELEMENT_NAME, MoveAnswerPacket.NAMESPACE, MoveAnswerPacket.class);

	
	_gameofferListener = new PacketListener() {
	    
	    @Override
	    public void processPacket(Packet packet) {
		GameOfferPacket invitation = (GameOfferPacket) packet;
		if (invitation.getType() == IQ.Type.GET) {
		    showInvitationInChat(invitation);
		}
	    }
	};
	
	SparkManager.getConnection().addPacketListener(_gameofferListener,
		new PacketTypeFilter(GameOfferPacket.class));
	
	_chatRoomListener = new ChatRoomOpeningListener();
	
	SparkManager.getChatManager().addChatRoomListener(_chatRoomListener);
	
	
    
    }

    private void showInvitationInChat(final GameOfferPacket invitation) {
	invitation.setType(IQ.Type.RESULT);
	invitation.setTo(invitation.getFrom());
	
	
	final ChatRoom room = SparkManager.getChatManager().getChatRoom(StringUtils.parseBareAddress(invitation.getFrom()));
	
	String name = StringUtils.parseName(invitation.getFrom());
	final JPanel panel = new JPanel();
	JLabel text = new JLabel("Game request from" + name); 
	JLabel game = new JLabel("Battleships");
	game.setFont(new Font("Dialog", Font.BOLD, 24));
	game.setForeground(Color.RED);
	JButton accept = new JButton(Res.getString("button.accept").replace("&", ""));
	JButton decline = new JButton(Res.getString("button.decline").replace("&", ""));
	panel.add(text);
	panel.add(game);
	panel.add(accept);
	panel.add(decline);
	room.getTranscriptWindow().addComponent(panel);
	
	accept.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		SparkManager.getConnection().sendPacket(invitation);
		invitation.setStartingPlayer(!invitation.isStartingPlayer());
		ChatRoomOpeningListener.createWindow(invitation, invitation.getFrom());
		panel.remove(3);
		panel.remove(2);
		panel.repaint();
		panel.revalidate();
	    }
	});
	
	decline.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		invitation.setType(IQ.Type.ERROR);
		SparkManager.getConnection().sendPacket(invitation);
		panel.remove(3);
		panel.remove(2);
		panel.repaint();
		panel.revalidate();
	    }
	});
	
	
    }

    @Override
    public void shutdown() {
	
    }

    @Override
    public boolean canShutDown() {
	return false;
    }

    @Override
    public void uninstall() {
	
    }

}
