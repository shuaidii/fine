import java.util.Scanner;

public class GameLogic {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String args[]) {

        GameState gameState = new GameState();
        boolean AGo = true;

        while (!gameState.aWinner() && !gameState.bWinner()) {

            boolean moved = false;
            System.out.println(gameState);
            do {
                System.out.print("It's Player " + (AGo? "A" : "B") + "'s turn: ");
                int pile = scanner.nextInt();
                int count = scanner.nextInt();
                if (gameState.isValidMove(pile, count)) {
                    if (AGo) gameState.moveA(pile, count);
                    else gameState.moveB(pile, count);
                    moved = true;
                }
                else {
                    System.out.println("Invalid move.");
                }
            }
            while (!moved);

            AGo = !AGo;
        }

        System.out.println(gameState);
        System.out.println("Game Over!");
        if (gameState.aWinner()) {
            System.out.println("Player A wins!");
        }
        else {
            System.out.println("Player B wins!");
        }

        System.out.println("The game took " + gameState.turns() + " turn(s).");

    }
}
