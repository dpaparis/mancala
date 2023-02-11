package com.mancala.mancala;

public class MancalaBoard {

    static private final int player1Mancala = 6;
    static private final int player2Mancala = 13;
    static private final int boardLength = 14;
    static private boolean switchTurn = true;

    // default to player 1 always starting
    private int playerTurn = 1;
    private byte [] board = new byte[14];


    public MancalaBoard() {
        // initialize board
        int startStones = 4;
        for (int i = 0; i<player1Mancala; i++) {
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

    private void setPitValueToZero(int pit) {
      board[pit] = (byte) 0;
    }

    private void incrementPitValue(int pit) {
        board[pit] = (byte) ((int) board[pit] + 1);
    }

    private void incrementPitValue(int pit, int value) {
        board[pit] = (byte) ((int) board[pit] + value);
    }

    /**
     * the game is over if either player has an empty row
     * @return boolean indicating that the game has ended
     */
    public boolean isGameOver() {
        boolean player1Done = true;
        boolean player2Done = true;
        for (int i=0;i<player1Mancala;i++) {
            if ((int) board[i] > 0) {
                player1Done = false;
                break;
            }
        }
        if (player1Done) {
            // player 2 gets to capture all remaining pieces on his/her row
            for (int i=player1Mancala+1;i<player2Mancala;i++) {
                incrementPitValue(player2Mancala, board[i]);
                setPitValueToZero(i);
            }
            return true;
        }
        for (int i=player1Mancala+1;i<player2Mancala;i++) {
            if ((int) board[i] > 0) {
                player2Done = false;
                break;
            }
        }
        if (player2Done) {
            // player 1 gets to capture all remaining pieces on his/her row
            for (int i=0;i<player1Mancala;i++) {
                incrementPitValue(player1Mancala, board[i]);
                setPitValueToZero(i);
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
     * @return boolean a move is valid if it's in the range 1-6 and that pit is not empty
     */
    public boolean isValidMove(int pit) {
        if (pit < 1 || pit > 6) return false;
        int index = playerPitPickToBoardIndex(pit);
        return (int) board[index] != 0;
    }

    protected void changePlayerTurn() {
        if (playerTurn ==1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }
    }

    /**
     *
     * @param pit
     * @return translates the picked pit (1-6) to an index for the byte array where we store the board state
     */
    protected int playerPitPickToBoardIndex(int pit) {
        if (getPlayerTurn() == 1) {
            // player can pick 1 through 6, this translates to index 0-5 in the array
            return pit -1;
        } else {
            // player can pick 1 through 6, this translates to index 7-12 in the array
            return pit + 6;
        }
    }

    /**
     * logic for taking a turn: take stones from the picked pit and traverse board counterclockwise
     * placing a stone in each pit you traverse (including your own mancala, but skipping your opponent's mancala)
     * @param pit
     * @return String message to print out
     */
    public String takeTurn(int pit) {
        switchTurn = true;
        String message = null;
        int index = playerPitPickToBoardIndex(pit);
        int numStones = board[index];
        // first step take stones
        setPitValueToZero(index);
        int current = index + 1;
        for (int i=0;i<numStones;i++) {
            // check if is other player's mancala, then skip it
            if (playerTurn == 1 && current == player2Mancala) {
                current = 0;
            }
            if (playerTurn == 2 && current == player1Mancala) {
                current = player1Mancala + 1;
            }
            incrementPitValue(current);
            // last stone logic
            if (i==numStones-1) {
                message = lastStone(current);
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
        return message;
    }

    /**
     * Special logic for placing the last stone: if last stone is placed in on of your own empty pits, you capture
     * the pieces in your opponent's opposite pit, if the last stone is placed in your mancala then you can go again
     * @param current
     * @return String with message to be printed for special cases
     */
    protected String lastStone(int current) {
        String message = null;
        // if we placed the last stone in an empty pit in own row capture opposite pieces
        if ((int) board[current] == 1) {
            // capture opposite pieces
            // opposite pit is 12 - current
            if (playerTurn == 1 && isPlayer1Row(current)) {
                // place stone and capture other pieces
                int oppositeStones = board[12-current];
                setPitValueToZero(12-current);
                incrementPitValue(player1Mancala, oppositeStones);
                message = "Captured " + oppositeStones + " opponent stones!";
            } else if (playerTurn == 2 && isPlayer2Row(current)) {
                int oppositeStones = board[12-current];
                setPitValueToZero(12-current);
                incrementPitValue(player2Mancala, oppositeStones);
                message = "Captured " + oppositeStones + " opponent stones!";
            }
        }
        // if last stone is placed in own mancala go again
        if ((playerTurn == 1 && current == player1Mancala) || (playerTurn == 2 && current == player2Mancala)) {
            switchTurn = false;
            message = "Go again";
        }
        return message;
    }

    @Override
    /**
     * builds a more user friendly string representation of the board state
     * player 2 mancala is all the way to the left and player 2's row is on top ordered from 1 to 6, right to left,
     * simulating player 2 sitting across from player 1
     * player 1 mancala is on the far right and player 1's row is ordered from left to right
     */
    public String toString() {
        //TODO: rotate board for player 2 turn
        StringBuilder player2Row = new StringBuilder("| ");
        for (int i=player2Mancala-1;i>player1Mancala;i--) {
            player2Row.append(getStonesInPit(i)).append(" | ");
        }
        StringBuilder player1Row = new StringBuilder("| ");
        for (int i=0;i<player1Mancala;i++) {
            player1Row.append(getStonesInPit(i)).append(" | ");
        }
        StringBuilder spaces = new StringBuilder();
        int numSpaces = getPlayer2Score() > 9 ? 15 : 14;
        spaces.append(" ".repeat(numSpaces + 1));
        return "Mancala 2: " + getPlayer2Score() + "   " + player2Row + "\n" +
                spaces + player1Row + " Mancala 1: " + getPlayer1Score();
    }
}
