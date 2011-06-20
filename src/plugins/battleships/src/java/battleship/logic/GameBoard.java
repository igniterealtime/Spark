package battleship.logic;

import battleship.types.Direction;
import battleship.types.Ship;

public class GameBoard {

    private Ship[][] _fields;
    private Shipmodel[] _myships;
    
    private boolean myTurn;
    
    public GameBoard(boolean myTurn) {
	this.myTurn = myTurn;
	_fields = new Ship[11][11];
	_myships = new Shipmodel[5];

	_myships[0] = new Shipmodel(2);
	_myships[1] = new Shipmodel(3);
	_myships[2] = new Shipmodel(3);
	_myships[3] = new Shipmodel(4);
	_myships[4] = new Shipmodel(5);
    }
    
    /**
     * Places a Bomb of the Opponent on you field
     * @param x
     * @param y
     * @return True if the Opponent hit a Ship, else false
     */
    public boolean placeBomb(int x, int y)
    {
	
	if(_fields[x][y] != Ship.EMPTY)
	{
	    Shipmodel smo =_myships[_fields[x][y].inArrayPosition()];
	    smo.setBomb();
	    return true;
	}
	else
	{
	    myTurn=!myTurn; // Now its my turn
	    return false;
	}
    }

    public boolean placeShip(int x, int y, Ship s, Direction dir) {
	boolean placementOK = true;

	if (dir == Direction.HORIZONTAL) // Left to right
	{
	    for (int i = x; i < s.getFields(); i++) {
		if (!checkSurrounding(i, y))
		    placementOK = false;
	    }

	    if (placementOK) {
		for (int i = x; i < s.getFields(); i++) {
		    _fields[i][y] = s;
		}
	    }
	} else // top to bottom
	{

	    for (int i = y; i < s.getFields(); i++) {
		if (!checkSurrounding(x, i))
		    placementOK = false;
	    }

	    if (placementOK) {
		for (int i = y; i < s.getFields(); i++) {
		    _fields[x][i] = s;

		}
	    }

	}

	return placementOK;
    }

    private boolean checkSurrounding(int x, int y) {
	if (_fields[x - 1][y - 1] != Ship.EMPTY || _fields[x][y - 1] != Ship.EMPTY
		|| _fields[x + 1][y - 1] != Ship.EMPTY) 
	{
	    return false;
	} 
	else if (_fields[x - 1][y] != Ship.EMPTY || _fields[x][y] != Ship.EMPTY
		|| _fields[x + 1][y] != Ship.EMPTY) 
	{
	    return false;
	} 
	else if (_fields[x - 1][y + 1] != Ship.EMPTY || _fields[x][y + 1] != Ship.EMPTY
		|| _fields[x + 1][y + 1] != Ship.EMPTY) 
	{
	    return false;
	} 
	else
	    return true;
    }
    
    private int getField(int x, int y)
    {
	Ship s = _fields[x][y];
	return 0;
    }

}
