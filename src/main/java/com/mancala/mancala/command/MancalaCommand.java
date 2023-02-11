package com.mancala.mancala.command;

import com.mancala.mancala.MancalaBoard;
import com.mancala.mancala.shell.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Scanner;

@ShellComponent
@Configuration
@ComponentScan(basePackages = {"com.mancala.mancala"})
public class MancalaCommand {

    @Autowired
    ShellHelper shellHelper;

    @ShellMethod("Start mancala game")
    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        MancalaBoard game = new MancalaBoard();
        shellHelper.printSuccess("Starting new Mancala game: \n" + game);
        Integer pit = null;
        boolean gameOver = false;
        while (!gameOver) {
            boolean validTurn = false;
            while (!validTurn) {
                shellHelper.print("Player " + game.getPlayerTurn() + " choose a pit");
                if (scanner.hasNextInt()) {
                    pit = scanner.nextInt();
                    validTurn = game.isValidMove(pit);
                    if (!validTurn) {
                        shellHelper.printWarning("Invalid move!");
                    }
                } else if (scanner.hasNext()) {
                    String next = scanner.next();
                    // quit
                    if (next.equals("q")) {
                        return;
                    } else {
                        shellHelper.printWarning("Pit must be an integer");
                    }
                }
            }
            String message = game.takeTurn(pit);
            if (message != null) shellHelper.printInfo(message);
            shellHelper.printSuccess("New board state: \n" + game);
            if (game.isGameOver()) {
                shellHelper.printSuccess("Game Over! Winner is Player " + game.getWinner() + "\n" +
                        "Score: Player 1 " + game.getPlayer1Score() + " - Player 2 " + game.getPlayer2Score());
                gameOver = true;
            }
        }
    }
}