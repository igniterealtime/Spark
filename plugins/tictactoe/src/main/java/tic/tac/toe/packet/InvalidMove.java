package tic.tac.toe.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class InvalidMove implements ExtensionElement {

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

    public static class Provider extends ExtensionElementProvider<InvalidMove>
    {
        public InvalidMove parse( XmlPullParser parser, int initialDepth ) throws XmlPullParserException, IOException
        {
            final InvalidMove gameMove = new InvalidMove();
            boolean done = false;
            while ( !done )
            {
                final int eventType = parser.next();

                if ( eventType == XmlPullParser.START_TAG )
                {
                    if ( "gameID".equals( parser.getName() ) )
                    {
                        final int gameID = Integer.valueOf( parser.nextText() );
                        gameMove.setGameID( gameID );
                    }
                    if ( "positionX".equals( parser.getName() ) )
                    {
                        final int position = Integer.valueOf( parser.nextText() );
                        gameMove.setPositionX( position );
                    }
                    if ( "positionY".equals( parser.getName() ) )
                    {
                        final int position = Integer.valueOf( parser.nextText() );
                        gameMove.setPositionY( position );
                    }
                }
                else if ( eventType == XmlPullParser.END_TAG )
                {
                    if ( ELEMENT_NAME.equals( parser.getName() ) )
                    {
                        done = true;
                    }
                }
            }

            return gameMove;
        }
    }
}
