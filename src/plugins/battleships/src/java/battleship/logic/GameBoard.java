package battleship.logic;

import java.util.ArrayList;
import java.util.List;

import battleship.types.Direction;
import battleship.types.Ship;

public class GameBoard {

    private Ship[][] _fields;
    private Shipmodel[] _myships;
    
    private boolean myTurn;
    
    public GameBoard(boolean myTurn) {
	this.myTurn = myTurn;
	
	_fields = new Ship[12][12]; 
	// Construct a 12x12 Gameboard with a surrounding void
	// actual game is in [1][1] to [10][10]
	_myships = new Shipmodel[5];
	_myships[0] = new Shipmodel(2);
	_myships[1] = new Shipmodel(3);
	_myships[2] = new Shipmodel(3);
	_myships[3] = new Shipmodel(4);
	_myships[4] = new Shipmodel(5);
	
	for (int x = 0; x < _fields.length; x++) {
            for (int y = 0; y < _fields.length; y++) {
                _fields[x][y] = Ship.EMPTY;
            }
        }
	
    }
    
    /**
     * Places a Bomb of the Opponent on your field
     * @param x
     * @param y
     * @return True if the Opponent hit a Ship, else false
     */
    public boolean placeBomb(int x, int y)
    {
        // Add one to subtract the void fields
        x = x+1;
        y = y+1;
	
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

    /**
     * Places a Ship on the Board
     * Returns false if not placeable
     * @param x
     * @param y
     * @param s
     * @param dir
     * @return
     */
    public List<CoordinatePair> placeShip(int x, int y, Ship s, Direction dir) {
	boolean placementOK = true;

	x = x+1;
	y = y+1;
	List<CoordinatePair> liste = new ArrayList<CoordinatePair>();
	
	System.out.println("placing ship");
	if (dir == Direction.HORIZONTAL) // Left to Right
	{
	    for (int i = x; i < x+s.getFields(); i++) {
		if (!checkSurrounding(i, y))
		    System.out.println("checking "+i+","+y);
		    placementOK = false;
	    }

	    if (placementOK) {
		for (int i = x,j = 0; i < x+s.getFields(); i++,j++) {
		    _fields[i][y] = s;
		    System.out.println("adding coord at horiz"+j);
		    liste.add( new CoordinatePair(i, y));
		}
	    }
	} else // top to bottom
	{

	    for (int i = y; i < y+s.getFields(); i++) {
		if (!checkSurrounding(x, i))
		    System.out.println("checking "+i+","+y);
		    placementOK = false;
	    }

	    if (placementOK) {
		for (int i = y,j=0; i < y+s.getFields(); i++,j++) {
		    _fields[x][i] = s;
		    liste.add(new CoordinatePair(x, i));
		    System.out.println("adding coord at vert"+j);

		}
	    }
	}

	System.out.println("placement ok? "+placementOK);
	
        if (!placementOK)
            return null;
        else
            return liste;
    }

    /**
     * Checks the surrounding fields for collision
     * @param x
     * @param y
     * @return
     */
    private boolean checkSurrounding(int x, int y) {
        
        if(x < 1 || x > 10)
        {
            System.out.println("1");
            return false;
        }
        if(y < 1 || y > 10)
        {
            System.out.println("2");
            return false;
        }
        
	if (_fields[x - 1][y - 1] != Ship.EMPTY 
	        || _fields[x][y - 1] != Ship.EMPTY
		|| _fields[x + 1][y - 1] != Ship.EMPTY) 
	{
	    System.out.println("3");
	    return false;
	} 
	else if (_fields[x - 1][y] != Ship.EMPTY 
	        || _fields[x][y] != Ship.EMPTY
		|| _fields[x + 1][y] != Ship.EMPTY) 
	{
	    System.out.println("4");
	    return false;
	} 
	else if (_fields[x - 1][y + 1] != Ship.EMPTY 
	        || _fields[x][y + 1] != Ship.EMPTY
		|| _fields[x + 1][y + 1] != Ship.EMPTY) 
	{
	    System.out.println("5");
	    return false;
	} 
	else
	    return true;
    }
    
    public int getField(int x, int y)
    {
	Ship s = _fields[x][y];
	return s.getFields();
    }

}
