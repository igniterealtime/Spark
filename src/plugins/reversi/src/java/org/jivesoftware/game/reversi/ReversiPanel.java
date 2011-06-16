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

import javax.swing.*;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.DefaultPacketExtension;

/**
 * The game UI, which is created after both players have accepted a new game.
 *
 * @author Bill Lynch
 */
public class ReversiPanel extends JPanel {

	private static final long serialVersionUID = 3591458286918924065L;
	private static final int BOARD_SIZE = 320;
    private static final int INFO_PANEL_HEIGHT = 50;
    private static final int BORDER_SIZE = 5;
    public static final int TOTAL_WIDTH = BOARD_SIZE + (BORDER_SIZE*2);
    public static final int TOTAL_HEIGHT = TOTAL_WIDTH + INFO_PANEL_HEIGHT; // frame width + 50 for the info panel
    private static final int NUM_BLOCKS = 8;
    private static final int BLOCK_SIZE = BOARD_SIZE/NUM_BLOCKS;

    private static final int DISC_SIZE = (int)(BLOCK_SIZE*0.8); // 80% of block size

    private XMPPConnection connection;
    private int otherPlayer;
    private int gameID;
    private String opponentJID;
    private PacketListener gameMoveListener;

    private List<ReversiBlock> blocks = new ArrayList<ReversiBlock>();
    // Main game object
    private ReversiModel reversi;

    // All images used by the game.
    
     ImageIcon imageIcon = ReversiRes.getImageIcon(ReversiRes.REVERSI_ICON);
    
    private Image imageBackground = ReversiRes.getImageIcon(ReversiRes.REVERSI_BOARD).getImage();
    private Image imageScoreWhite = ReversiRes.getImageIcon(ReversiRes.REVERSI_SCORE_WHITE).getImage();
    private Image imageScoreBlack = ReversiRes.getImageIcon(ReversiRes.REVERSI_SCORE_BLACK).getImage();
    private Image imageTurnBlack = ReversiRes.getImageIcon(ReversiRes.REVERSI_LABEL_BLACK).getImage();
    private Image imageTurnWhite = ReversiRes.getImageIcon(ReversiRes.REVERSI_LABEL_WHITE).getImage();
    private Image imageButtonResign = ReversiRes.getImageIcon(ReversiRes.REVERSI_RESIGN).getImage();
    private Image imageYou = ReversiRes.getImageIcon(ReversiRes.REVERSI_YOU).getImage();
    private Image imageThem = ReversiRes.getImageIcon(ReversiRes.REVERSI_THEM).getImage();

    /**
     * Creates a new Reversi panel.
     *
     * @param connection Connection associated.
     * @param gameID Game ID number
     * @param startingPlayer Whether we are the starting player or not
     * @param opponentJID JID of opponent
     */
    public ReversiPanel(XMPPConnection connection, final int gameID, boolean startingPlayer, String opponentJID) {
        this.connection = connection;
        this.gameID = gameID;
        this.opponentJID = opponentJID;
        otherPlayer = startingPlayer? ReversiModel.WHITE : ReversiModel.BLACK;

        // Load all images.

        // Start the game
        reversi = new ReversiModel();

        if (connection != null) {
            gameMoveListener = new PacketListener() {
                public void processPacket(Packet packet) {
                    GameMove move = (GameMove)packet.getExtension(GameMove.ELEMENT_NAME, GameMove.NAMESPACE);
                    // If this is a move for the current game.
                    if (move.getGameID() == gameID) {
                        int position = move.getPosition();
                        // Make sure that the other player is allowed to make the move right now.
                        if (reversi.getCurrentPlayer() == otherPlayer && reversi.isValidMove(position)) {
                            reversi.makeMove(position);
                            // Redraw board.
                            ReversiPanel.this.repaint();
                        }
                        else {
                            // TODO: other user automatically forfeits!
                        }
                        // Execute move.
                    }
                }
            };

            connection.addPacketListener(gameMoveListener, new PacketExtensionFilter(GameMove.ELEMENT_NAME,
                    GameMove.NAMESPACE));
            // TODO: at end of game, remove listener.
        }

        setOpaque(false);

        // Use absolute layout
        setLayout(null);
        // Set its size:
        setPreferredSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT));

        // Make a new panel which is the game board grid:
        JPanel reversiBoard = new JPanel(new GridLayout(NUM_BLOCKS,NUM_BLOCKS,0,0));
        reversiBoard.setOpaque(false);
        for (int i=0; i<NUM_BLOCKS*NUM_BLOCKS; i++) {
            ReversiBlock block = new ReversiBlock(this, i);
            blocks.add(block);
            reversiBoard.add(block);
        }
        // Add the reversi board to the main panel:
        add(reversiBoard);
        // Position it:
        reversiBoard.setBounds(BORDER_SIZE, BORDER_SIZE, BOARD_SIZE, BOARD_SIZE);


        // TODO: listen for click on resign button!!
    }

    /**
     * Sends a forfeit message to the other player.
     */
    public void sendForfeit() {
        DefaultPacketExtension forfeit = new DefaultPacketExtension(GameForfeit.ELEMENT_NAME, GameForfeit.NAMESPACE);
        forfeit.setValue("gameID", Integer.toString(gameID));
        Message message = new Message();
        message.setTo(opponentJID);
        message.addExtension(forfeit);
        connection.sendPacket(message);
        connection.removePacketListener(gameMoveListener);
    }

    public void paintComponent(Graphics g) {
        // Turn on anti-aliasing.
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Background
        g.drawImage(imageBackground, 0, 0, null);

        // Draw info panel components.

        // Draw the score.
        g.drawImage(imageScoreWhite, 3, BOARD_SIZE + BORDER_SIZE*2 + 7, null);
        g.drawImage(imageScoreBlack, 3, BOARD_SIZE + BORDER_SIZE*2 + 27, null);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        String whiteScore = String.valueOf(reversi.getWhiteScore());
        String blackScore = String.valueOf(reversi.getBlackScore());
        FontMetrics fm = g.getFontMetrics();
        int width = Math.max(fm.stringWidth(whiteScore), fm.stringWidth(blackScore));
        g.drawString(whiteScore, imageScoreBlack.getWidth(null) + 7 + width - fm.stringWidth(whiteScore),
                BOARD_SIZE + BORDER_SIZE*2 + 22);
        g.drawString(blackScore, imageScoreWhite.getWidth(null) + 7 + width - fm.stringWidth(blackScore),
                BOARD_SIZE + BORDER_SIZE*2 + 42);

        // Draw who's turn it is.
        if (!reversi.isGameFinished())
        {      
            if (reversi.getCurrentPlayer() == ReversiModel.BLACK) {
            g.drawImage(imageTurnBlack, 116, BOARD_SIZE + BORDER_SIZE*2 + 11, null);
            }
            else {
            g.drawImage(imageTurnWhite, 116, BOARD_SIZE + BORDER_SIZE*2 + 11, null);
            }
        } else
        {
            int me  = otherPlayer==ReversiModel.BLACK?ReversiModel.WHITE:ReversiModel.BLACK;
            String whoWins = "Draw";
            if (reversi.getBlackScore() > reversi.getWhiteScore())
            {   
                if (me == ReversiModel.BLACK)
                    whoWins = "YOU WIN!";
                else
                    whoWins = "YOU LOST!";
            } else if(reversi.getBlackScore() < reversi.getWhiteScore())
            {
                if (me == ReversiModel.WHITE)
                    whoWins = "YOU WIN!";
                else
                    whoWins = "YOU LOST!";
            }
            g.drawString(whoWins, 130, BOARD_SIZE + BORDER_SIZE*2 + 20);
        }
        if (reversi.getCurrentPlayer() == otherPlayer) {
            g.drawImage(imageThem, 163, BOARD_SIZE + BORDER_SIZE*2 + 31, null);
        }
        else {
            g.drawImage(imageYou, 163, BOARD_SIZE + BORDER_SIZE*2 + 31, null);
        }
        
        // The resign button.
        g.drawImage(imageButtonResign, 281, BOARD_SIZE + BORDER_SIZE*2 + 17, null);
    }

    /**
     * A Reversi block (one of the squares of the grid).
     */
    public class ReversiBlock extends JPanel {

		private static final long serialVersionUID = -8504469339731900770L;
		private ReversiPanel ui;
        private int index;

        public ReversiBlock(ReversiPanel ui, int index) {
            super();
            this.ui = ui;
            this.index = index;
            setPreferredSize(new Dimension(BLOCK_SIZE,BLOCK_SIZE));
            addMouseListener(new ReversiBlockMouseListener(this));
            setOpaque(false);
        }

        /**
         * Returns a handle on the game UI.
         *
         * @return ReversiPanel of block.
         */
        public ReversiPanel getReversiUI() {
            return ui;
        }

        /**
         * This block's index (0->63).
         *
         * @return Index of block
         */
        public int getIndex() {
            return index;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Turn on anti-aliasing:
            ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Draw a disc in the block if the game says we should.
            int boardValue = reversi.getBoardValue(index);
            if (boardValue == org.jivesoftware.game.reversi.ReversiModel.BLACK) {
                drawDisc(g, Color.BLACK);
            }
            else if (reversi.getBoardValue(index) == org.jivesoftware.game.reversi.ReversiModel.WHITE) {
                drawDisc(g, Color.WHITE);
            }
        }

        /**
         * Draws the disc.
         *
         * @param g Graphics to draw
         * @param color Color
         */
        private void drawDisc(Graphics g, Color color) {
            int position = BLOCK_SIZE - ((BLOCK_SIZE+DISC_SIZE)/2);
            g.setColor(color);
            g.fillOval(position, position, DISC_SIZE, DISC_SIZE);
        }
    }

    /**
     * A mouse listener for a Reversi block.
     */
    public class ReversiBlockMouseListener extends MouseAdapter {

        private ReversiBlock block;

        public ReversiBlockMouseListener(ReversiBlock block) {
            this.block = block;
        }

        /**
         * Highlight the block if this block is a valid move.
         */
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            if (reversi.getCurrentPlayer() != otherPlayer && reversi.isValidMove(block.getIndex()))
            {
                block.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                block.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            }
        }

        /**
         * Set the block color back to the default.
         */
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            block.setCursor(Cursor.getDefaultCursor());
            block.setBorder(null);
        }

        /**
         * If the click box is a valid move, register a move in this box.
         */
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            // Make sure that it's our turn and that it's a valid move.
            if (reversi.getCurrentPlayer() != otherPlayer && reversi.isValidMove(block.getIndex()))
            {
                // Update the game model.
                reversi.makeMove(block.getIndex());

                // Send the move to the other player.
                Message message = new Message(opponentJID);
                GameMove move = new GameMove();
                move.setGameID(gameID);
                move.setPosition(block.getIndex());
                message.addExtension(move);
                connection.sendPacket(message);

                // Repaint board.
                ReversiPanel.this.repaint();

                // Repaint all blocks.
//                for (Iterator it = block.getReversiUI().getBlocks().iterator(); it.hasNext();) {
//                    ReversiBlock component = (ReversiBlock)it.next();
//                    component.repaint();
//                }

            }
        }
    }

    public List<ReversiBlock> getBlocks() {
        return blocks;
    }
}
