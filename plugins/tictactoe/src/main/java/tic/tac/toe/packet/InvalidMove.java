package tic.tac.toe.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;

import javax.xml.namespace.QName;
import java.io.IOException;

public class InvalidMove implements ExtensionElement {

    public static final String ELEMENT_NAME = "ttt-invalid";
    public static final String NAMESPACE = "tictactoe";
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT_NAME);

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
    public String toXML(XmlEnvironment xmlEnvironment) {

        return "<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">"
            + "<gameID>" + _gameID + "</gameID>"
            + "<positionX>" + _posx + "</positionX>"
            + "<positionY>" + _posy + "</positionY>"
            + "</" + ELEMENT_NAME + ">";
    }

    public static class Provider extends ExtensionElementProvider<InvalidMove>
    {
        public InvalidMove parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException
        {
            final InvalidMove gameMove = new InvalidMove();
            boolean done = false;
            while ( !done )
            {
                final XmlPullParser.Event eventType = parser.next();

                if ( eventType == XmlPullParser.Event.START_ELEMENT )
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
                else if ( eventType == XmlPullParser.Event.END_ELEMENT )
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
