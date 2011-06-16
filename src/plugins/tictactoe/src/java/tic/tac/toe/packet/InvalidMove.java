package tic.tac.toe.packet;

import org.jivesoftware.smack.packet.PacketExtension;

public class InvalidMove implements PacketExtension {

    public static final String ELEMENT_NAME = "ttt-invalid";
    public static final String NAMESPACE = "tictactoe";

    private int _gameID;
    private int _posx;
    private int _posy;

    @Override
    public String getElementName() {
	return ELEMENT_NAME;
    }

    @Override
    public String getNamespace() {
	return NAMESPACE;
    }

    public int getGameID() {
	return _gameID;
    }

    public void setGameID(int gameID) {
	_gameID = gameID;
    }

    public int getPositionX() {
	return _posx;
    }

    public int getPositionY() {
	return _posy;
    }

    public void setPositionX(int x) {
	_posx = x;
    }

    public void setPositionY(int y) {
	_posy = y;
    }

    @Override
    public String toXML() {
	StringBuffer buf = new StringBuffer();
	buf.append("<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">");

	buf.append("<gameID>").append(_gameID).append("</gameID>");

	buf.append("<positionX>").append(_posx).append("</positionX>");

	buf.append("<positionY>").append(_posy).append("</positionY>");

	buf.append("</" + ELEMENT_NAME + ">");
	return buf.toString();
    }

}
