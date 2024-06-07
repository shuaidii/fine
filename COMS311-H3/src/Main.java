import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please provide the input file path as an argument.");
            return;
        }

        GameBoard gb = new GameBoard();
        gb.readInput(args[0]);

        // Print the initial setup
        System.out.println("Consider the following initial set up:");
        // Assuming you have a method in GameBoard to get the number of vehicles
        System.out.println(gb.getNumOfVehicles());
        // Assuming you have a method in GameBoard to print the initial positions of vehicles
        gb.printInitialPositions();

        // Print the output plan
        System.out.println("\nThe output plan can be:");
        ArrayList<Pair> path = gb.getPlan();
        for (int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).getId() + " " + path.get(i).getDirection());
        }

        // Print the number of paths
        System.out.println("\n" + gb.getNumOfPaths());
    }
}
