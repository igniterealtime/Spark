/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tic.tac.toe;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to represent the TicTacToe gameboard
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 */
public class GameBoard {

    private int[][] _board;

    private int _currentPlayer;

    private int _winner;

    public GameBoard() {

	_board = new int[3][3];

	for (int[] x : _board) {
	    Arrays.fill(x, Mark.BLANK.ordinal());
	}

	_currentPlayer = Mark.X.ordinal();
	_winner = 0;

    }

    public boolean isGameFinished() {
	return _winner != 0;
    }
    
    public int getWinner()
    {
	return _winner;
    }

    public Mark getMarkAtPos(int x, int y) {

	return Mark.valueOf(_board[x][y]);
    }

    public Mark getCurrentPlayer() {
	return Mark.valueOf(_currentPlayer);
    }

    public void placeMark(int x, int y) {
	_board[x][y] = _currentPlayer;

	if (didCurrentPlayerWin()) {
	    _winner = _currentPlayer;
	}

	if (isBoardFull() && _winner < 1) {
	    // Setting to Tie
	    _winner = -1;
	}

	_currentPlayer = _currentPlayer == 1 ? 2 : 1;

    }

    /**
     * Checks if the Currentplayer has won the game
     * 
     * @return current player won the game?
     */
    private boolean didCurrentPlayerWin() {

	for (int x = 0; x < 3; x++) {

	    if (_board[x][0] == _currentPlayer
		    && _board[x][1] == _currentPlayer
		    && _board[x][2] == _currentPlayer) {
		return true;
	    }

	}

	for (int y = 0; y < 3; y++) {
	    if (_board[0][y] == _currentPlayer
		    && _board[1][y] == _currentPlayer
		    && _board[2][y] == _currentPlayer) {
		return true;
	    }
	}

	if (_board[0][0] == _currentPlayer && _board[1][1] == _currentPlayer
		&& _board[2][2] == _currentPlayer) {
	    return true;
	}
	if (_board[2][0] == _currentPlayer && _board[1][1] == _currentPlayer
		&& _board[0][2] == _currentPlayer) {
	    return true;
	}

	return false;

    }

    /**
     * Checks if the Board is Full
     * 
     * @return board is full?
     */
    private boolean isBoardFull() {

	for (int x = 0; x < 3; x++) {
	    for (int y = 0; y < 3; y++) {

		if (_board[x][y] == 0) {
		    return false;
		}
	    }
	}

	return true;

    }
    /**
     * Returns the winning positions in an arraylist of Pairs
     * @return ArrayList<Pair> with 3 entries
     */
    public Pair[] getWinningPositions()
    {
	ArrayList<Pair> liste = new ArrayList<Pair>();
	
	if(_winner == -1)
	{
	    // TIE
	    return null;
	}
	
	Mark m = Mark.valueOf(_winner);
	
	for (int x = 0; x < 3; x++) {

	    if (_board[x][0] == _winner
		    && _board[x][1] == _winner
		    && _board[x][2] == _winner) {
		new Pair(x, 0,m);
		
		liste.add(new Pair(x, 0, m));
		liste.add(new Pair(x, 1, m));
		liste.add(new Pair(x, 2, m));
		return liste.toArray(new Pair[3]);
		
	    }

	}

	for (int y = 0; y < 3; y++) {
	    if (_board[0][y] == _winner
		    && _board[1][y] == _winner
		    && _board[2][y] == _winner) {
		
		liste.add(new Pair(0, y, m));
		liste.add(new Pair(1, y, m));
		liste.add(new Pair(2, y, m));
		return liste.toArray(new Pair[3]);
	    }
	}

	if (_board[0][0] == _winner && _board[1][1] == _winner
		&& _board[2][2] == _winner) {
	    
	    liste.add(new Pair(0, 0, m));
	    liste.add(new Pair(1, 1, m));
	    liste.add(new Pair(2, 2, m));
	    	
		return liste.toArray(new Pair[3]);
	}
	if (_board[2][0] == _winner && _board[1][1] == _winner
		&& _board[0][2] == _winner) {
	    liste.add(new Pair(2, 0, m));
	    liste.add(new Pair(1, 1, m));
	    liste.add(new Pair(0, 2, m));
		return liste.toArray(new Pair[3]);
	}

	return null;

    }
    
    /**
     * Checks if the palced move is Valid
     * @param x
     * @param y
     * @return
     */
    public boolean isValidMove(Mark markplaced, int x , int y)
    {
	return _board[x][y] == 0 && _currentPlayer == markplaced.getValue();
    }
}
