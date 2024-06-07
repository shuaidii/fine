import meta.BuildingSquare;
import meta.Player;
import meta.Square;

import java.util.Scanner;

public class CLIGame extends Game {
    private static final Scanner SCANNER = new Scanner(System.in);

    public CLIGame() {
        this(false);
    }


    public CLIGame(boolean cheatingMode) {
        super(cheatingMode);
    }

    public void start() {
        randomStartPlayer();
        while (true) {
            int step = playerMove();
            Player winner = getWinner();
            if (winner != null) {
                System.out.println("Game end, " + winner.getName() + " wins");
                break;
            }
            this.currentPlayer = nextPlayer();

            System.out.println("=========");
            for (Player player : playerList) {
                System.out.println("Player" + player);
            }
            System.out.println("=========");

        }
    }

    public int playerMove() {
        System.out.println();
        System.out.println("Player " + currentPlayer.getName() + " start to play");
        int step = die(12);
        System.out.println("Die number: " + step);
        Square currentSquare = currentPlayer.getCurrentSquare();
        int nextSquareIndex = ((step + currentSquare.getIndex()) % board.getSquareCount());
        Square nextSquare = board.getSquare(nextSquareIndex);
        currentPlayer.setCurrentSquare(nextSquare);
        System.out.println("Move to new Square: " + nextSquare.toString());

        processOnSquare(nextSquare);
        return step;
    }

    /**
     * throwing a twelve-sided die
     *
     * @return 1-12
     */
    public int die(int max) {
        if (!cheatingMode) {
            return RANDOM.nextInt(max) + 1;
        }

        while (true) {
            System.out.print("Select a die number from 1 to 12: ");
            try {
                int number = Integer.parseInt(SCANNER.nextLine());
                if (number > 0 && number <= max) {
                    return number;
                }
            } catch (Exception ignore) {
            }
        }
    }

    public void processOnSquare(Square square) {
        if (!(square instanceof BuildingSquare)) {
            return;
        }
        BuildingSquare buildingSquare = (BuildingSquare) square;

        // pay for overnight fee
        if (buildingSquare.getOwner() != null && !buildingSquare.getOwner().equals(currentPlayer)) {
            int overnightFee = buildingSquare.getOvernightFee();
            //  the fee is doubled if the owner owns both of the other two hotels in the letter group eg A3 and A2
            //  as well as A1 and is halved if the guest owns either or both of the other two hotels in the letter group.
            if (buildingSquare.getOwner().getBuildingCount(buildingSquare.getBuildingType()) == 3) {
                overnightFee *= 2;
            } else if (currentPlayer.getBuildingCount(buildingSquare.getBuildingType()) == 2) {
                overnightFee /= 2;
            }
            System.out.println("Must pay $" + overnightFee + " for overnight fee.");
            currentPlayer.decrAmount(overnightFee);
            buildingSquare.getOwner().incrAmount(overnightFee);
            return;
        }

        // buy the building
        if (buildingSquare.getOwner() == null) {
            if (buildingSquare.getSellPrice() > currentPlayer.getRemainAmount()) {
                System.out.println("Sorry, you has not enough money to have chance to buy this building with $" + buildingSquare.getSellPrice());
            } else {
                boolean buy = getUserInputForYesOrNo("Do you want to buy this building with $" + buildingSquare.getSellPrice() + "?(y/n): ");
                if (buy) {
                    currentPlayer.buyBuilding(buildingSquare);
                }
            }
        }

        // increase the building star rating
        if (currentPlayer.equals(buildingSquare.getOwner())) {
            while (true) {
                if (buildingSquare.isMaxStarRating()) {
                    System.out.println("This building is at maximum star rating, can not increase again: " + buildingSquare);
                    break;
                }
                if (!buildingSquare.canIncrStarRating()) {
                    System.out.println("Sorry, you has not enough money to have chance to increase the star rating with $" + buildingSquare.incrStarRatingCost());
                    break;
                }
                boolean incr = getUserInputForYesOrNo("Do you want to increase the star rating with $" + buildingSquare.incrStarRatingCost() + "?(y/n): ");
                if (incr) {
                    buildingSquare.incrStarRating();
                } else {
                    break;
                }
            }
        }
    }

    /**
     * random select the first one to play
     */
    public void randomStartPlayer() {
        if (cheatingMode) {
            while (true) {
                System.out.print("Select a player number from 1 to " + playerList.size() + ": ");
                try {
                    int number = Integer.parseInt(SCANNER.nextLine());
                    currentPlayer = playerList.get(number - 1);
                    return;
                } catch (Exception ignore) {
                }
            }
        }
        System.out.print("Random select first player: ");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int player = RANDOM.nextInt(2);
        this.currentPlayer = playerList.get(player);
        System.out.println("Player " + currentPlayer.getName());
    }

    /**
     * get player input for yes or no
     *
     * @param message message
     * @return true for yes, false for no
     */
    private boolean getUserInputForYesOrNo(String message) {
        while (true) {
            System.out.print(message);
            String input = SCANNER.nextLine();
            if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                return true;
            } else if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                return false;
            }
        }
    }
}
