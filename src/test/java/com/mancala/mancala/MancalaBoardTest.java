package com.mancala.mancala;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MancalaBoardTest {

    @Test
    public void testInitialization() {
        // 4 stones in each pit, 0 in mancalas
        MancalaBoard board = new MancalaBoard();
        for (int i=0;i<6;i++) {
            Assertions.assertEquals(board.getStonesInPit(i), 4);
        }
        Assertions.assertEquals(board.getStonesInPit(6), 0);
        Assertions.assertEquals(board.getStonesInPit(13), 0);
        for (int i=7;i<12;i++) {
            Assertions.assertEquals(board.getStonesInPit(i), 4);
        }
    }

    @Test
    public void testIsGameOver() {
        MancalaBoard board = new MancalaBoard();
        Assertions.assertFalse(board.isGameOver());

        byte[] bytes = {0,0,0,0,0,0,20,5,2,3,8,0,0,10};
        board = new MancalaBoard(bytes);
        Assertions.assertTrue(board.isGameOver());
        Assertions.assertEquals(board.getPlayer1Score(), 20);
        Assertions.assertEquals(board.getPlayer2Score(), 28);
        Assertions.assertEquals(board.getWinner(), 2);
    }

    @Test
    public void testIsPlayerRow() {
        MancalaBoard board = new MancalaBoard();

        for (int i=0;i<6;i++) {
            Assertions.assertTrue(board.isPlayer1Row(i));
            Assertions.assertFalse(board.isPlayer2Row(i));
        }
        for (int i=7;i<13;i++) {
            Assertions.assertTrue(board.isPlayer2Row(i));
            Assertions.assertFalse(board.isPlayer1Row(i));
        }
        Assertions.assertFalse(board.isPlayer2Row(6));
        Assertions.assertFalse(board.isPlayer1Row(6));
        Assertions.assertFalse(board.isPlayer2Row(13));
        Assertions.assertFalse(board.isPlayer1Row(13));
    }

    @Test
    public void testIsValidMove() {
        byte[] bytes = {0,0,0,0,0,0,20,5,2,3,8,0,0,10};
        MancalaBoard board = new MancalaBoard(bytes);
        for (int i=0;i<8;i++) {
            Assertions.assertFalse(board.isValidMove(i));
        }
        board.changePlayerTurn();
        for (int i=1;i<5;i++) {
            Assertions.assertTrue(board.isValidMove(i));
        }
        Assertions.assertFalse(board.isValidMove(5));
        Assertions.assertFalse(board.isValidMove(6));
    }

    @Test
    public void testChangePlayerTurn() {
        MancalaBoard board = new MancalaBoard();
        Assertions.assertEquals(board.getPlayerTurn(), 1);
        board.changePlayerTurn();
        Assertions.assertEquals(board.getPlayerTurn(), 2);
        board.changePlayerTurn();
        Assertions.assertEquals(board.getPlayerTurn(), 1);
    }

    @Test
    public void testTakeTurn() {
        MancalaBoard board = new MancalaBoard();
        board.takeTurn(1);
        // state of board should be 0,5,5,5,5,4,0,4,4,4,4,4,4,0
        byte[] expected = {0,5,5,5,5,4,0,4,4,4,4,4,4,0};
        Assertions.assertArrayEquals(expected, board.getBoard());
        Assertions.assertEquals(board.getPlayerTurn(), 2);

        board.takeTurn(4);
        // state of board should be 1,5,5,5,5,4,0,4,4,4,0,5,5,1
        // tests going around the board
        expected = new byte[] {1,5,5,5,5,4,0,4,4,4,0,5,5,1};
        Assertions.assertArrayEquals(expected, board.getBoard());
        Assertions.assertEquals(board.getPlayerTurn(), 1);

        board.takeTurn(1);
        // state of board should be 0,6,5,5,5,4,0,4,4,4,0,5,5,1
        expected = new byte[] {0,6,5,5,5,4,0,4,4,4,0,5,5,1};
        Assertions.assertArrayEquals(expected, board.getBoard());
        Assertions.assertEquals(board.getPlayerTurn(), 2);

        board.takeTurn(3);
        // state of board should be 0,6,5,5,5,4,0,4,4,0,1,6,6,2
        // still player 2 turn because ends in own mancala
        expected = new byte[] {0,6,5,5,5,4,0,4,4,0,1,6,6,2};
        Assertions.assertArrayEquals(expected, board.getBoard());
        Assertions.assertEquals(board.getPlayerTurn(), 2);


        board = new MancalaBoard(new byte[] {0,6,5,5,1,0,4,4,4,4,1,6,6,2});
        board.takeTurn(5);
        // state of board should be 0,6,5,5,0,1,8,0,4,4,1,6,6,2
        // player 1 gets to capture player 2 opposite stones
        expected = new byte[] {0,6,5,5,0,1,8,0,4,4,1,6,6,2};
        Assertions.assertArrayEquals(expected, board.getBoard());
        Assertions.assertEquals(board.getPlayerTurn(), 2);

        // test player 1 ends in own mancala and gets to go again
        board = new MancalaBoard();
        board.takeTurn(3);
        // state of board should be 4,4,0,5,5,5,1,4,4,4,4,4,4,0
        expected = new byte[] {4,4,0,5,5,5,1,4,4,4,4,4,4,0};
        Assertions.assertArrayEquals(expected, board.getBoard());
        Assertions.assertEquals(board.getPlayerTurn(), 1);

        //  test player 2 capturing player 1 stones
        board = new MancalaBoard(new byte[] {0,6,5,5,1,6,4,4,4,4,1,0,6,2});
        board.changePlayerTurn();
        board.takeTurn(4);
        // state of board should be 0,0,5,5,1,6,4,4,4,4,0,1,6,8
        expected = new byte[] {0,0,5,5,1,6,4,4,4,4,0,1,6,8};
        //System.out.println(board.toString());
        Assertions.assertArrayEquals(expected, board.getBoard());
        Assertions.assertEquals(board.getPlayerTurn(), 1);
    }

    @Test
    public void testLastStone() {
        MancalaBoard board = new MancalaBoard(new byte[] {1,0,5,5,1,6,4,4,4,4,1,5,6,2});
        Assertions.assertEquals(board.lastStone(0),"Captured 6 opponent stones!");
        Assertions.assertNull(board.lastStone(2));
        Assertions.assertNull(board.lastStone(10));
        Assertions.assertEquals(board.lastStone(6),"Go again");

        board.changePlayerTurn();
        Assertions.assertEquals(board.lastStone(10),"Captured 5 opponent stones!");
        Assertions.assertEquals(board.lastStone(13),"Go again");
        Assertions.assertNull(board.lastStone(8));
        Assertions.assertNull(board.lastStone(1));
    }
}
