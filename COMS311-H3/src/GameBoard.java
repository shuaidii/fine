import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;


public class GameBoard {
    private int[][] board = new int[6][6];
    private List<Vehicle> vehicles = new ArrayList<>();

    public GameBoard() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                board[i][j] = -1;
            }
        }
    }

    public void readInput(String FileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(FileName));
        int numOfVehicles = Integer.parseInt(br.readLine().trim());

        for (int i = 0; i < numOfVehicles; i++) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                // Skip empty lines or handle them appropriately
                continue;
            }
            String[] positions = line.split("\\s+");
            Vehicle vehicle = new Vehicle(i, positions);
            vehicles.add(vehicle);
            for (String pos : positions) {
                if (pos != null && !pos.trim().isEmpty()) {
                    int position = Integer.parseInt(pos.trim());
                    int row = (position - 1) / 6;
                    int col = (position - 1) % 6;
                    board[row][col] = i;
                }
            }
        }
        br.close();
    }

    public ArrayList<Pair> getPlan() {
        Queue<State> queue = new LinkedList<>();
        Set<HashKey> visited = new HashSet<>(); // To keep track of visited states using HashKey

        queue.add(new State(board, vehicles, new ArrayList<>(), 0));

        while (!queue.isEmpty()) {
            State currentState = queue.poll();
            HashKey currentStateHashKey = stateToHashKey(currentState.boardState);

            //Test
            System.out.println("Exploring State:");
            // Print the current board state being processed
            printBoardState(currentState.boardState);
//            System.out.println("Moves leading to this state:");
//            for (Pair p : currentState.moves) {
//                System.out.println(p.getId() + " " + p.getDirection());
//            }
//            System.out.println("------------");

            // If this state has been visited before, skip it
            if (visited.contains(currentStateHashKey)) {
                continue;
            }
            // Mark the current state as visited
            visited.add(currentStateHashKey);

            // Check if the current state is a solution
            if (isSolution(currentState.boardState)) {
                return currentState.moves;
            }

            // Explore all possible moves for each vehicle
            for (int vehicleId = 0; vehicleId < currentState.vehicles.size(); vehicleId++) {
                for (char direction : new char[]{'n', 's', 'e', 'w'}) {
                    if (isValidMove(currentState, vehicleId, direction)) {
                        State nextState = applyMove(currentState, vehicleId, direction);
//                        System.out.println("Move After: " + vehicleId + " " + direction);
//                        printBoardState(nextState.boardState);
                        queue.add(nextState);
                    }
                }
            }
        }

        return new ArrayList<>(); // Return an empty list if no solution is found
    }


    public int getNumOfPaths() {
        Queue<State> queue = new LinkedList<>();
        Set<HashKey> visited = new HashSet<>();
        queue.add(new State(board, vehicles, new ArrayList<>(), 0));
        int shortestPathLength = Integer.MAX_VALUE;
        int count = 0;

        while (!queue.isEmpty()) {
            State currentState = queue.poll();
            if (currentState.numOfMoves > shortestPathLength) {
                continue;
            }

            HashKey currentStateHashKey = new HashKey(currentState.moves);
            if (visited.contains(currentStateHashKey)) continue;
            visited.add(currentStateHashKey);

            if (isSolution(currentState.boardState)) {
                if (currentState.moves.size() < shortestPathLength) {
                    shortestPathLength = currentState.moves.size();
                    count = 1;
                } else if (currentState.moves.size() == shortestPathLength) {
                    count++;
                }
                continue;
            }

            for (int vehicleId = 0; vehicleId < currentState.vehicles.size(); vehicleId++) {
                for (char direction : new char[]{'n', 's', 'e', 'w'}) {
                    if (isValidMove(currentState, vehicleId, direction)) {
                        State nextState = applyMove(currentState, vehicleId, direction);
                        queue.add(nextState);
                    }
                }
            }
        }

        return count;
    }

    private HashKey stateToHashKey(int[][] boardState) {
        int[] linearState = new int[6 * 6];
        int index = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                linearState[index++] = boardState[i][j];
            }
        }
        return new HashKey(linearState);
    }


    private boolean isValidMove(State currentState, int vehicleId, char direction) {
        Vehicle vehicle = currentState.vehicles.get(vehicleId);
        boolean isHorizontal = false;

        // Check if the vehicle is horizontal
        int firstRow = (vehicle.positions.get(0) - 1) / 6;
        int secondRow = (vehicle.positions.get(1) - 1) / 6;
        if (firstRow == secondRow) {
            isHorizontal = true;
        }

        // Restrict movement based on orientation
        if (isHorizontal && (direction == 'n' || direction == 's')) {
            return false;
        }
        if (!isHorizontal && (direction == 'e' || direction == 'w')) {
            return false;
        }

        for (int pos : vehicle.positions) {
            int row = (pos - 1) / 6;
            int col = (pos - 1) % 6;

            switch (direction) {
                case 'n':
                    if (!checkSquareCanMove(row - 1, col, vehicleId, currentState.boardState)) return false;
                    break;
                case 's':
                    if (!checkSquareCanMove(row + 1, col, vehicleId, currentState.boardState)) return false;
                    break;
                case 'e':
                    if (!checkSquareCanMove(row, col + 1, vehicleId, currentState.boardState)) return false;
                    break;
                case 'w':
                    if (!checkSquareCanMove(row, col - 1, vehicleId, currentState.boardState)) return false;
                    break;
            }
        }
        return true;
    }

    private boolean checkSquareCanMove(int row, int col, int vehicleId, int[][] boardState) {
        if (row < 0 || row > 5 || col < 0 || col > 5) {
            return false;
        }
        return boardState[row][col] == -1 || boardState[row][col] == vehicleId;
    }


    private State applyMove(State currentState, int vehicleId, char direction) {
        int[][] newBoard = new int[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                newBoard[i][j] = currentState.boardState[i][j];
            }
        }
        List<Vehicle> newVehicles = new ArrayList<>();
        for (Vehicle vehicle : currentState.vehicles) {
            newVehicles.add(vehicle.copy());
        }

        List<Integer> positions = newVehicles.get(vehicleId).positions;
        for (int pos : positions) {
            int row = (pos - 1) / 6;
            int col = (pos - 1) % 6;
            newBoard[row][col] = -1;// Clear the old position
        }

        for (int i = 0; i < positions.size(); i++) {
            int pos = positions.get(i);
            int row = (pos - 1) / 6;
            int col = (pos - 1) % 6;
            switch (direction) {
                case 'n':
                    row--;
                    break;
                case 's':
                    row++;
                    break;
                case 'e':
                    col++;
                    break;
                case 'w':
                    col--;
                    break;
            }
            newBoard[row][col] = vehicleId;  // Set the new position
            positions.set(i, row * 6 + col + 1);
        }

        ArrayList<Pair> newMoves = new ArrayList<>(currentState.moves);
        newMoves.add(new Pair(vehicleId, direction));
        return new State(newBoard, newVehicles, newMoves, currentState.numOfMoves + 1);
    }


    private boolean isSolution(int[][] boardState) {
        // Check if any part of the vehicle with ID 0 is in the rightmost column
        for (int row = 0; row < 6; row++) {
            if (boardState[row][5] == 0) {
                return true;
            }
        }
        return false;
    }


    private void printBoardState(int[][] boardState) {
        System.out.println("Current Board State:");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                String show = boardState[i][j] + "";
                if (boardState[i][j] >= 0) {
                    show = " " + show;
                }
                System.out.print(show + " ");
            }
            System.out.println();
        }
        System.out.println("------------"); // A separator for clarity
    }

    private class State {
        int[][] boardState;
        List<Vehicle> vehicles;
        ArrayList<Pair> moves;
        int numOfMoves;

        public State(int[][] boardState, List<Vehicle> vehicles, ArrayList<Pair> moves, int numOfMoves) {
            this.boardState = boardState;
            this.moves = moves;
            this.numOfMoves = numOfMoves;
            this.vehicles = vehicles;
        }
    }

    private class Vehicle {
        int id;
        List<Integer> positions;

        public Vehicle(int id, String[] positions) {
            this.id = id;
            this.positions = new ArrayList<>();
            for (String pos : positions) {
                this.positions.add(Integer.parseInt(pos));
            }
        }

        public Vehicle(int id, List<Integer> positions) {
            this.id = id;
            this.positions = positions;
        }

        public Vehicle copy() {
            return new Vehicle(id, new ArrayList<>(positions));
        }
    }

    public int getNumOfVehicles() {
        return vehicles.size();
    }

    public void printInitialPositions() {
        for (Vehicle vehicle : vehicles) {
            for (int pos : vehicle.positions) {
                System.out.print(pos + " ");
            }
            System.out.println();
        }
    }


    public class HashKey {
        private int[] c;

        public HashKey(int[] inputc) {
            c = inputc.clone();
        }

        public HashKey(List<Pair> pairs) {
            c = new int[pairs.size()];
            for (int i = 0; i < pairs.size(); i++) {
                Pair pair = pairs.get(i);
                c[i] = 100 * pair.getId() + (int) pair.getDirection();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof HashKey)) return false;
            HashKey h = (HashKey) o;
            return Arrays.equals(c, h.c);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(c);
        }
    }


}
