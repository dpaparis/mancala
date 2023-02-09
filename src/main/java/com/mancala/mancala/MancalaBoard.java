package com.mancala.mancala;

import java.util.Arrays;

public class MancalaBoard {

    static private int player1Mancala = 6;
    static private int player2Mancala = 13;
    static private int startStones = 4;
    static private int boardLength = 14;

    // default to player 1 always starting
    private int playerTurn = 1;
    private byte [] board = new byte[14];


    public MancalaBoard() {
        // initialize board
        for (int i=0;i<player1Mancala;i++) {
            this.board[i] = (byte) startStones;
        }
        for (int i=player1Mancala+1;i<player2Mancala;i++) {
            this.board[i] = (byte) startStones;
        }
    }

    // for testing
    public MancalaBoard(byte[] board) {
        // initialize board
        this.board = board;
    }

    protected byte[] getBoard() {
        return board;
    }

    public int getStonesInPit(int pit) {
        assert pit >= 0;
        assert pit < boardLength;
        return board[pit];
    }

    public int getPlayer1Score() {
        return board[player1Mancala];
    }

    public int getPlayer2Score() {
        return board[player2Mancala];
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    private void setPitValue(int pit, int value) {
      board[pit] = (byte) value;
    }

    private void incrementPitValue(int pit) {
        board[pit] = (byte) ((int) board[pit] + 1);
    }

    private void incrementPitValue(int pit, int value) {
        board[pit] = (byte) ((int) board[pit] + value);
    }

    public boolean isGameOver() {
        boolean player1Done = true;
        boolean player2Done = true;
        for (int i=0;i<player1Mancala;i++) {
            if ((int) board[i] > 0) {
                player1Done = false;
            }
        }
        if (player1Done) {
            // player 2 gets to capture all remaining pieces on his/her row
            for (int i=player1Mancala+1;i<player2Mancala;i++) {
                incrementPitValue(player2Mancala, (int) board[i]);
                setPitValue(i, 0);
            }
            return true;
        }
        for (int i=player1Mancala+1;i<player2Mancala;i++) {
            if ((int) board[i] > 0) {
                player2Done = false;
            }
        }
        if (player2Done) {
            // player 1 gets to capture all remaining pieces on his/her row
            for (int i=0;i<player1Mancala;i++) {
                incrementPitValue(player1Mancala, (int) board[i]);
                setPitValue(i, 0);
            }
            return true;
        }
        return false;
    }

    public int getWinner() {
        return (int) board[player1Mancala] > (int) board[player2Mancala]? 1 : 2;
    }

    protected boolean isPlayer1Row(int pit) {
        return pit < player1Mancala;
    }

    protected boolean isPlayer2Row(int pit) {
        return pit > player1Mancala && pit < player2Mancala;
    }

    /**
     * @param pit
     * @return a move is valid if it's in the player's own row and the pit is not empty
     */
    public boolean isValidMove(int pit) {
        if ((int) board[pit] == 0) return false;
        if (playerTurn == 1) {
            return pit >= 0 && pit < player1Mancala;
        } else {
            return pit > player1Mancala && pit < player2Mancala;
        }
    }

    protected void changePlayerTurn() {
        if (playerTurn ==1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }
    }
    public void takeTurn(int pit) {
        boolean switchTurn = true;
        int numStones = board[pit];
        // first step take stones
        setPitValue(pit, 0);
        int current = pit + 1;
        for (int i=0;i<numStones;i++) {
            // check if is other mancala, then skip it
            if (playerTurn == 1 && current == player2Mancala) {
                current = 0;
            }
            if (playerTurn == 2 && current == player1Mancala) {
                current = player1Mancala + 1;
            }
            incrementPitValue(current);
            // last stone logic
            if (i==numStones-1) {
                switchTurn = lastStone(current);
                break;
            }
            current++;
            // keep going around the board
            if (current > boardLength - 1) {
                current = current % boardLength;
            }
        }

        // set correct player turn
        if (switchTurn) changePlayerTurn();
    }

    private boolean lastStone(int current) {
        // if we place last stone in an empty pit in own row capture opposite pieces
        if ((int) board[current] == 1) {
            // capture opposite pieces
            // opposite pit is 12 - current
            if (playerTurn == 1 && isPlayer1Row(current)) {
                // place stone and capture other pieces
                int oppositeStones = (int) board[12-current];
                setPitValue(12-current, 0);
                incrementPitValue(player1Mancala, oppositeStones);
            } else if (playerTurn == 2 && isPlayer2Row(current)) {
                int oppositeStones = (int) board[12-current];
                setPitValue(12-current, 0);
                incrementPitValue(player2Mancala, oppositeStones);
            }
        }
        // if place last stone in own mancala go again
        if (playerTurn == 1 && current == player1Mancala) {
            return false;
        } else if (playerTurn == 2 && current == player2Mancala) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MancalaBoard{" +
                "playerTurn=" + playerTurn + "\n" +
                ", board=" + Arrays.toString(board) +
                '}';
    }
}
