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
package org.jivesoftware.game.reversi;

import java.util.Arrays;

/**
 * Model for a Reversi game. <a href="http://en.wikipedia.org/wiki/Reversi">Reversi</a>
 * is a game played by two players ({@link #WHITE White} and {@link #BLACK Black}) on
 * an 8x8 board. Board positions are 0-63; starting at 0 in the upper-left corner then
 * travelling horizontally row by row until position 63 in the bottom-right corner of
 * the board.<p>
 *
 * This class maintains game state as individual turns are taken until the game is
 * {@link #isGameFinished() finished}. {@link #BLACK Black} always starts the game.
 * Two stones for each player are always placed in the center of the game board at
 * the start of each game (following standard Reversi rules).
 *
 * @author Matt Tucker
 */
public class ReversiModel {

    /**
     * Blank.
     */
    public static int BLANK = 0;

    /**
     * White.
     */
    public static int WHITE = 1;

    /**
     * Black.
     */
    public static int BLACK = 2;

    private int[] board;
    private int currentPlayer;
    private boolean gameFinished = false;
    private int[] flipBuffer = new int[7];
    private int flipBufferSize = 0;

    /**
     * Constructs a new game instance and sets up initial board positions.
     */
    public ReversiModel() {
        board = new int[64];
        // Set all positions to blank.
        Arrays.fill(board, BLANK);
        // Setup initial positions.
        board[27] = WHITE;
        board[28] = BLACK;
        board[35] = BLACK;
        board[36] = WHITE;

        // Black always starts.
        currentPlayer = BLACK;
    }

    /**
     * Returns true if the game is finished (i.e., no more moves are possible.). The winner
     * is the player with the highest score when the game is finished.
     *
     * @return true if the game is finished.
     */
    public boolean isGameFinished() {
        return gameFinished;
    }

    /**
     * Returns true if the current player can place a stone at the specified position.
     *
     * @param position the position on the board (0-63).
     * @return true if the position is a valid move.
     */
    public boolean isValidMove(int position) {
        return !gameFinished && isValidMove(position, getCurrentPlayer());
    }

    /**
     * Returns the current value at a specific board position (between 0 and 63, inclusive). The value
     * is either {@link #BLANK blank}, {@link #WHITE white}, or {@link #BLACK black}.
     *
     * @param position the board position.
     * @return the value at the specified board position.
     */
    public int getBoardValue(int position) {
        if (position < 0 || position > 63) {
            throw new IllegalArgumentException("Invalid position: " + position + ". Valid board " +
                    "positions are 0 through 63");
        }
        return board[position];
    }

    /**
     * Returns the current player: {@link #WHITE white} or {@link #BLACK black}. Note that
     * it's possible for a player to have several turns in a row, depending on whether
     * there's a valid move available for the other player. The {@link #BLACK black} player
     * always starts the game.
     *
     * @return the current player.
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the black player's current score.
     *
     * @return the black player's current score.
     */
    public int getWhiteScore() {
        int score = 0;
        for (int aBoard : board) {
            if (aBoard == WHITE) {
                score++;
            }
        }
        return score;
    }

    /**
     * Returns the white player's current score.
     *
     * @return the white player's current score.
     */
    public int getBlackScore() {
        int score = 0;
        for (int aBoard : board) {
            if (aBoard == BLACK) {
                score++;
            }
        }
        return score;
    }

    /**
     * Causes the {@link #getCurrentPlayer() current player} to execute a move by placing a stone at
     * the specified position. Executing a move will flip one or more of the other player's stones.
     *
     * @param position the position to make the move at.
     * @return true if the move was successful; false otherwise.
     */
    public boolean makeMove(int position) {
        if (!isValidMove(position)) {
            return false;
        }
        board[position] = currentPlayer;
        // Execute the move
        getFlipCount(position, currentPlayer, true);

        // See if other user has a valid move. If so, switch to other player.
        int otherPlayer = currentPlayer==WHITE ? BLACK: WHITE;
        boolean hasMove = false;
        for (int i=0; i<board.length; i++) {
            if (isValidMove(i, otherPlayer)) {
                hasMove = true;
                break;
            }
        }

        // If the other player has at least one valid move, switch players.
        if (hasMove) {
            currentPlayer = otherPlayer;
        }
        // Otherwise, stick with the current player.
        else {
            // Make sure the current player has a valid move. Otherwise, the game must be over.
            hasMove = false;
            for (int i=0; i<board.length; i++) {
                if (isValidMove(i, currentPlayer)) {
                    hasMove = true;
                    break;
                }
            }
            if (!hasMove) {
                gameFinished = true;
            }
        }

        return true;
    }

    /**
     * Returns a String representation of the current game board (ASCII art). A fixed
     * width font is necessary to display the String properly.
     *
     * @return a String representation of the current game board.
     */
    public String printBoard() {
        StringBuffer buf = new StringBuffer();
        buf.append("+----------------+\n");
        for (int i=0; i<64; i++) {
            if (i%8 == 0) {
                buf.append("|");
            }
            if (board[i] == BLANK) {
                buf.append("  ");
            }
            else if (board[i] == WHITE) {
                buf.append(" w");
            }
            else {
                buf.append(" b");
            }
            if (i%8 == 7) {
                buf.append("|\n");
            }
        }
        buf.append("+----------------+");
        return buf.toString();
    }

    /**
     * Returns true if the specified player has a valid move at the specified position (ignoring
     * whose turn it is currently).
     *
     * @param position the position.
     * @param player the player.
     * @return return true if the player has a move at the position.
     */
    private boolean isValidMove(int position, int player) {
        if (position < 0 || position > 63) {
            return false;
        }
        if (board[position] != BLANK) {
            return false;
        }
        else {
            return getFlipCount(position, player, false) > 0;
        }
    }

    /**
     * Computes the number of stones that would be flipped by executing a move at the specified position.
     * If <tt>doFlip</tt> is true, the game board will be updated.
     *
     * @param position the position for the move.
     * @param player the player.
     * @param doFlips true if stones should be actually flipped; false if flips should only
     *      be calculated hypothetically.
     * @return the number of stones that were or that would be flipped by the move.
     */
    private synchronized int getFlipCount(int position, int player, boolean doFlips) {
        int flipCount = 0;

        // Traverse vertically, horizontally, and diagonally to flip pieces.

        // Going left horizontally.

  			boolean edge;
  			
        if (position%8 > 1) {
    				edge = false;
            for (int i=position-1; !edge; i--) {
            		if (i % 8 == 0) edge = true;
                if (board[i] == BLANK) {
                    break;
                }
                else if (board[i] == player) {
                    for (int j=0; j<flipBufferSize; j++) {
                        flipCount++;
                        if (doFlips) {
                            board[flipBuffer[j]] = player;
                        }
                    }
                    break;
                }
                flipBuffer[flipBufferSize++] = i;
            }
            // Reset flip buffer.
            flipBufferSize = 0;
        }
        // Going right horizontally.
        if (position%8 < 6) {
        		edge = false;
            for (int i=position+1; !edge; i++) {
            		if (i % 8 == 7) edge = true;
                if (board[i] == BLANK) {
                    break;
                }
                else if (board[i] == player) {
                    for (int j=0; j<flipBufferSize; j++) {
                        flipCount++;
                        if (doFlips) {
                            board[flipBuffer[j]] = player;
                        }
                    }
                    break;
                }
                flipBuffer[flipBufferSize++] = i;
            }
            // Reset flip buffer.
            flipBufferSize = 0;
        }
        // Going up vertically.
        if (position > 15) {
            for (int i=position-8; i >= 0; i-=8) {
                if (board[i] == BLANK) {
                    break;
                }
                else if (board[i] == player) {
                    for (int j=0; j<flipBufferSize; j++) {
                        flipCount++;
                        if (doFlips) {
                            board[flipBuffer[j]] = player;
                        }
                    }
                    break;
                }
                flipBuffer[flipBufferSize++] = i;
            }
            // Reset flip buffer.
            flipBufferSize = 0;
        }
        // Going down vertically.
        if (position < 48) {
            for (int i=position+8; i <= 65; i+=8) {
                if (board[i] == BLANK) {
                    break;
                }
                else if (board[i] == player) {
                    for (int j=0; j<flipBufferSize; j++) {
                        flipCount++;
                        if (doFlips) {
                            board[flipBuffer[j]] = player;
                        }
                    }
                    break;
                }
                flipBuffer[flipBufferSize++] = i;
            }
            // Reset flip buffer.
            flipBufferSize = 0;
        }
        // Diagonal up-left
        if (position > 15 && position%8 > 1)  {
        		edge = false;
            for (int i=position-9; !edge; i-=9) {
            		if (i < 8 || i%8==0) edge = true;
                if (board[i] == BLANK) {
                    break;
                }
                else if (board[i] == player) {
                    for (int j=0; j<flipBufferSize; j++) {
                        flipCount++;
                        if (doFlips) {
                            board[flipBuffer[j]] = player;
                        }
                    }
                    break;
                }
                flipBuffer[flipBufferSize++] = i;
            }
            // Reset flip buffer.
            flipBufferSize = 0;
        }
        // Diagonal up-right
        if (position > 15 && position%8 < 6)  {
        		edge = false;
            for (int i=position-7; !edge; i-=7) {
            		if (i < 8 || i%8==7) edge = true;
                if (board[i] == BLANK) {
                    break;
                }
                else if (board[i] == player) {
                    for (int j=0; j<flipBufferSize; j++) {
                        flipCount++;
                        if (doFlips) {
                            board[flipBuffer[j]] = player;
                        }
                    }
                    break;
                }
                flipBuffer[flipBufferSize++] = i;
            }
            // Reset flip buffer.
            flipBufferSize = 0;
        }
        // Diagonal down-left
        if (position < 48 && position%8 > 1)  {
          edge = false;
          for (int i=position+7; !edge; i+=7) {
            		if (i > 55 || i%8==0) edge = true;
                if (board[i] == BLANK) {
                    break;
                }
                else if (board[i] == player) {
                    for (int j=0; j<flipBufferSize; j++) {
                        flipCount++;
                        if (doFlips) {
                            board[flipBuffer[j]] = player;
                        }
                    }
                    break;
                }
                flipBuffer[flipBufferSize++] = i;
            }
            // Reset flip buffer.
            flipBufferSize = 0;
        }
        // Diagonal down-right
        if (position < 48 && position%8 < 6)  {
        		edge = false;
            for (int i=position+9; !edge; i+=9) {
            		if (i > 55 || i%8==7) edge=true;
                if (board[i] == BLANK) {
                    break;
                }
                else if (board[i] == player) {
                    for (int j=0; j<flipBufferSize; j++) {
                        flipCount++;
                        if (doFlips) {
                            board[flipBuffer[j]] = player;
                        }
                    }
                    break;
                }
                flipBuffer[flipBufferSize++] = i;
            }
            // Reset flip buffer.
            flipBufferSize = 0;
        }

        return flipCount;
    }
}