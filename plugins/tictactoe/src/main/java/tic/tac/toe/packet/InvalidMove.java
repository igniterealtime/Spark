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
    public String toXML(String enclosingNamespace) {

        return "<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">"
            + "<gameID>" + _gameID + "</gameID>"
            + "<positionX>" + _posx + "</positionX>"
            + "<positionY>" + _posy + "</positionY>"
            + "</" + ELEMENT_NAME + ">";
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
                        final int gameID = Integer.parseInt( parser.nextText() );
                        gameMove.setGameID( gameID );
                    }
                    if ( "positionX".equals( parser.getName() ) )
                    {
                        final int position = Integer.parseInt( parser.nextText() );
                        gameMove.setPositionX( position );
                    }
                    if ( "positionY".equals( parser.getName() ) )
                    {
                        final int position = Integer.parseInt( parser.nextText() );
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
