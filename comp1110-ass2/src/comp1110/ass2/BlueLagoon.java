package comp1110.ass2;

import GAME.Player;
import GAME.enums.PhaseType;
import GAME.Position;
import GAME.enums.PieceType;

import java.util.*;


public class BlueLagoon {
    // The Game Strings for five maps have been created for you.
    // They have only been encoded for two players. However, they are
    // easily extendable to more by adding additional player statements.
    public static final String DEFAULT_GAME = "a 13 2; c 0 E; i 6 0,0 0,1 0,2 0,3 1,0 1,1 1,2 1,3 1,4 2,0 2,1; i 6 0,5 0,6 0,7 1,6 1,7 1,8 2,6 2,7 2,8 3,7 3,8; i 6 7,12 8,11 9,11 9,12 10,10 10,11 11,10 11,11 11,12 12,10 12,11; i 8 0,9 0,10 0,11 1,10 1,11 1,12 2,10 2,11 3,10 3,11 3,12 4,10 4,11 5,11 5,12; i 8 4,0 5,0 5,1 6,0 6,1 7,0 7,1 7,2 8,0 8,1 8,2 9,0 9,1 9,2; i 8 10,3 10,4 11,0 11,1 11,2 11,3 11,4 11,5 12,0 12,1 12,2 12,3 12,4 12,5; i 10 3,3 3,4 3,5 4,2 4,3 4,4 4,5 5,3 5,4 5,5 5,6 6,3 6,4 6,5 6,6 7,4 7,5 7,6 8,4 8,5; i 10 5,8 5,9 6,8 6,9 7,8 7,9 7,10 8,7 8,8 8,9 9,7 9,8 9,9 10,6 10,7 10,8 11,7 11,8 12,7 12,8; s 0,0 0,5 0,9 1,4 1,8 1,12 2,1 3,5 3,7 3,10 3,12 4,0 4,2 5,9 5,11 6,3 6,6 7,0 7,8 7,12 8,2 8,5 9,0 9,9 10,3 10,6 10,10 11,0 11,5 12,2 12,8 12,11; r C 0,0 3,4 3,1 B 2,6 12,1 4,5 W 8,9 9,0 P 1,3 2,4 1,10 S 5,9 9,5 7,6 8,6; p 0 0 0 0 0 0 0 S 2,7 6,7 T 1,0; p 1 0 0 0 0 0 0 S 3,4 5,6 T 2,5 6,7;";
    public static final String WHEELS_GAME = "a 13 2; c 0 E; i 5 0,1 0,2 0,3 0,4 1,1 1,5 2,0 2,5 3,0 3,6 4,0 4,5 5,1 5,5 6,1 6,2 6,3 6,4; i 5 0,8 0,9 0,10 1,8 1,11 2,7 2,11 3,8 3,11 4,8 4,9 4,10; i 7 8,8 8,9 8,10 9,8 9,11 10,7 10,11 11,8 11,11 12,8 12,9 12,10; i 7 10,0 10,1 10,4 10,5 11,0 11,2 11,3 11,4 11,6 12,0 12,1 12,4 12,5; i 9 2,2 2,3 3,2 3,4 4,2 4,3; i 9 2,9; i 9 6,6 6,7 6,8 6,9 6,10 6,11 7,6 8,0 8,1 8,2 8,3 8,4 8,5; i 9 10,9; s 0,1 0,4 0,10 2,2 2,3 2,9 2,11 3,0 3,2 3,4 3,6 4,2 4,3 4,10 6,1 6,4 6,6 6,11 8,0 8,5 8,8 8,10 10,0 10,5 10,7 10,9 10,11 11,3 12,1 12,4 12,8 12,10; r C B W P S; p 0 0 0 0 0 0 0 S T; p 1 0 0 0 0 0 0 S T;";
    public static final String FACE_GAME = "a 13 2; c 0 E; i 6 0,0 0,1 0,2 0,3 0,4 0,5 0,6 0,7 0,8 0,9 0,10 0,11 1,0 1,12 2,0 2,11 3,0 3,12 4,0 4,11 5,0 5,12 6,0 6,11 7,0 7,12 8,0 8,11 9,0 9,12 10,0 10,11 11,0 11,12 12,0 12,1 12,2 12,3 12,4 12,5 12,6 12,7 12,8 12,9 12,10 12,11; i 6 2,4 2,5 2,6 2,7; i 9 4,4 4,5 4,6 4,7; i 9 6,5 6,6 7,5 7,7 8,5 8,6; i 12 2,2 3,2 3,3 4,2 5,2 5,3 6,2 7,2 7,3; i 12 2,9 3,9 3,10 4,9 5,9 5,10 6,9 7,9 7,10; i 12 9,2 9,10 10,2 10,3 10,4 10,5 10,6 10,7 10,8 10,9; s 0,3 0,8 1,0 1,12 2,2 2,4 2,7 2,9 4,2 4,5 4,6 4,9 5,0 5,12 6,2 6,5 6,6 6,9 8,0 8,5 8,6 8,11 9,2 9,10 10,3 10,5 10,6 10,8 11,0 11,12 12,4 12,7; r C B W P S; p 0 0 0 0 0 0 0 S T; p 1 0 0 0 0 0 0 S T;";
    public static final String SIDES_GAME = "a 7 2; c 0 E; i 4 0,0 0,1 0,2 0,3 1,0 1,1 1,2 1,3 2,0 2,1 2,2 2,3 3,0 3,1 3,2 3,3 4,0 4,1 4,2 4,3 5,0 5,1 5,2 5,3 6,0 6,1 6,2 6,3; i 20 0,5 1,5 1,6 2,5 3,5 3,6 4,5 5,5 5,6 6,5; s 0,0 0,1 0,2 0,3 1,1 1,2 1,3 1,5 1,6 2,0 2,1 2,2 2,3 3,0 3,1 3,2 3,3 3,5 3,6 4,0 4,1 4,2 4,3 5,1 5,2 5,3 5,5 5,6 6,0 6,1 6,2 6,3; r C B W P S; p 0 0 0 0 0 0 0 S T; p 1 0 0 0 0 0 0 S T;";
    public static final String SPACE_INVADERS_GAME = "a 23 2; c 0 E; i 6 0,2 0,7 1,3 1,7 2,2 2,3 2,4 2,5 2,6 2,7 3,2 3,4 3,5 3,6 3,8 4,0 4,1 4,2 4,3 4,4 4,5 4,6 4,7 4,8 4,9 5,0 5,1 5,3 5,4 5,5 5,6 5,7 5,9 5,10 6,0 6,2 6,7 6,9 7,3 7,4 7,6 7,7; i 6 0,14 0,19 1,15 1,19 2,14 2,15 2,16 2,17 2,18 2,19 3,14 3,16 3,17 3,18 3,20 4,12 4,13 4,14 4,15 4,16 4,17 4,18 4,19 4,20 4,21 5,12 5,13 5,15 5,16 5,17 5,18 5,19 5,21 5,22 6,12 6,14 6,19 6,21 7,15 7,16 7,18 7,19; i 6 17,9 18,8 18,9 19,6 19,7 19,8 19,9 19,10 19,11 19,12 20,5 20,6 20,7 20,8 20,9 20,10 20,11 20,12 21,5 21,6 21,7 21,8 21,9 21,10 21,11 21,12 21,13 22,5 22,6 22,7 22,8 22,9 22,10 22,11 22,12; i 8 12,3 12,5 13,3 13,4 13,5 13,6 14,1 14,2 14,3 14,4 14,5 15,1 15,2 15,3 16,1 16,2; i 8 12,17 12,18 12,19 13,17 13,18 13,19 13,20 14,17 14,18 14,19 14,20 15,19 15,20 15,21 16,19 16,20; i 8 13,14 14,13 14,14 15,13 15,14 15,15 16,13 16,14; i 8 14,7 15,7 15,8 16,7; i 10 8,9 9,9 10,9 11,9; i 10 8,12 9,13 10,12 11,13; i 10 9,1 10,1 11,1 12,1; i 10 9,22 10,21 11,22 12,21; i 10 13,10 14,10 15,10; i 10 17,0 18,0 19,0 20,0; i 10 17,16 18,16 19,16 20,16; s 0,2 0,7 0,14 0,19 3,5 3,17 6,0 6,9 6,12 6,21 7,4 7,6 7,16 7,18 11,9 11,13 12,1 12,19 12,21 13,10 15,2 15,8 15,14 15,20 17,9 18,8 18,9 20,0 20,16 21,6 21,9 21,12; r C B W P S; p 0 0 0 0 0 0 0 S T; p 1 0 0 0 0 0 0 S T;";

    /**
     * Check if the string encoding of the game state is well-formed.
     * Note that this does not mean checking that the state is valid
     * (represents a state that players could reach in game play),
     * only that the string representation is syntactically well-formed.
     * <p>
     * A description of the state string will be included in README.md
     * in an update of the project after D2B is complete.
     *
     * @param stateString a string representing a game state
     * @return true if stateString is well-formed and false otherwise
     */
    public static boolean isStateStringWellFormed(String stateString) {
        // FIXME Task 3
        // If the State string is empty, it is not well-formed.
        if (stateString.isEmpty()) {
            return false;
        }

        stateString = stateString.trim() + " ";
        String[] sections = stateString.split(";\\s+");
        int boardHeight = 0;
        int playerNumber = 0;
        boolean foundA = false;

        for (String section : sections) {
            String[] tokens = section.split("\\s+");
            char identifier = tokens[0].charAt(0);

            switch (identifier) {
                case 'a':
                    foundA = true;
                    if (tokens.length != 3 || !tokens[1].matches("\\d+") || !tokens[2].matches("\\d+")) {
                        return false;
                    }
                    boardHeight = Integer.parseInt(tokens[1]);
                    playerNumber = Integer.parseInt(tokens[2]);
                    if (boardHeight < 0 || playerNumber < 2 || playerNumber > 4) {
                        return false;
                    }
                    break;

                case 'c':
                    if (tokens.length != 3 || !tokens[1].matches("\\d+") || !tokens[2].matches("[ES]")) {
                        return false;
                    }
                    break;

                case 'i':
                    if (!section.matches("^i\\s\\d+(?:\\s\\d+,\\d+)+$") || section.contains("  ")) {
                        return false;
                    }
                    break;

                case 's':
                    if (!section.matches("^s(?:\\s\\d+,\\d+)+$") || section.contains("  ")) {
                        return false;
                    }
                    break;

                case 'r':
                    if (!section.matches("^r\\sC(\\s\\d+,\\d+)*\\sB(\\s\\d+,\\d+)*\\sW(\\s\\d+,\\d+)*\\sP(\\s\\d+,\\d+)*\\sS(\\s\\d+,\\d+)*$") || section.contains("  ")) {
                        return false;
                    }
                    break;

                case 'p':
                    if (!section.matches("^p\\s\\d\\s\\d+(?:\\s\\d+){5}\\sS(?:\\s\\d+,\\d+)*\\sT(?:\\s\\d+,\\d+)*$") || section.contains("  ")) {
                        return false;
                    }
                    break;

                default:
                    return false;
            }
        }

        return foundA;
    }


//    public static void main(String[] args) {
//        // You can use this main method to test your implementation.
//        System.out.println(isStateStringWellFormed(DEFAULT_GAME)); // should return true
//        System.out.println(isStateStringWellFormed(WHEELS_GAME)); // should return true
//        System.out.println(isStateStringWellFormed(FACE_GAME)); // should return true
//        System.out.println(isStateStringWellFormed(SIDES_GAME)); // should return true
//        System.out.println(isStateStringWellFormed(SPACE_INVADERS_GAME)); // should return true
//        System.out.println(isStateStringWellFormed("")); // should return false
//        System.out.println(isStateStringWellFormed("a 13 2; c 0 E;")); // should return false
//    }


    /**
     * Check if the string encoding of the move is syntactically well-formed.
     * <p>
     * A description of the move string will be included in README.md
     * in an update of the project after D2B is complete.
     *
     * @param moveString a string representing a player's move
     * @return true if moveString is well-formed and false otherwise
     */
    public static boolean isMoveStringWellFormed(String moveString) {
        if (moveString == null || moveString.isEmpty()) {
            return false;
        }

        String[] moveParts = moveString.split(" ");
        if (moveParts.length != 2) {
            return false;
        }

        String pieceType = moveParts[0];
        String coordinate = moveParts[1];

        if (!pieceType.equals("S") && !pieceType.equals("T")) {
            return false;
        }

        String[] coordinateParts = coordinate.split(",");
        if (coordinateParts.length != 2) {
            return false;
        }

        try {
            int x = Integer.parseInt(coordinateParts[0].trim());
            int y = Integer.parseInt(coordinateParts[1].trim());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    } // FIXME Task 4

    /**
     * Given a state string which is yet to have resources distributed amongst the stone circles,
     * randomly distribute the resources and statuettes between all the stone circles.
     * <p>
     * There will always be exactly 32 stone circles.
     * <p>
     * The resources and statuettes to be distributed are:
     * - 6 coconuts
     * - 6 bamboo
     * - 6 water
     * - 6 precious stones
     * - 8 statuettes
     * <p>
     * The distribution must be random.
     *
     * @param stateString a string representing a game state without resources distributed
     * @return a string of the game state with resources randomly distributed
     */
    public static String distributeResources(String stateString) {
        return ""; // FIXME Task 6
    }

    /**
     * Given a state string and a move string, determine if the move is
     * valid for the current player.
     * <p>
     * For a move to be valid, the player must have enough pieces left to
     * play the move. The following conditions for each phase must also
     * be held.
     * <p>
     * In the Exploration Phase, the move must either be:
     * - A settler placed on any unoccupied sea space
     * - A settler or a village placed on any unoccupied land space
     * adjacent to one of the player's pieces.
     * <p>
     * In the Settlement Phase, the move must be:
     * - Only a settler placed on an unoccupied space adjacent to
     * one of the player's pieces.
     * Importantly, players can now only play on the sea if it is
     * adjacent to a piece they already own.
     *
     * @param stateString a string representing a game state
     * @param moveString  a string representing the current player's move
     * @return true if the current player can make the move and false otherwise
     */
    public static void main(String[] args) {
        String states = "a 13 2; c 1 E; i 6 0,0 0,1 0,2 0,3 1,0 1,1 1,2 1,3 1,4 2,0 2,1; i 6 0,5 0,6 0,7 1,6 1,7 1,8 2,6 2,7 2,8 3,7 3,8; i 6 7,12 8,11 9,11 9,12 10,10 10,11 11,10 11,11 11,12 12,10 12,11; i 8 0,9 0,10 0,11 1,10 1,11 1,12 2,10 2,11 3,10 3,11 3,12 4,10 4,11 5,11 5,12; i 8 4,0 5,0 5,1 6,0 6,1 7,0 7,1 7,2 8,0 8,1 8,2 9,0 9,1 9,2; i 8 10,3 10,4 11,0 11,1 11,2 11,3 11,4 11,5 12,0 12,1 12,2 12,3 12,4 12,5; i 10 3,3 3,4 3,5 4,2 4,3 4,4 4,5 5,3 5,4 5,5 5,6 6,3 6,4 6,5 6,6 7,4 7,5 7,6 8,4 8,5; i 10 5,8 5,9 6,8 6,9 7,8 7,9 7,10 8,7 8,8 8,9 9,7 9,8 9,9 10,6 10,7 10,8 11,7 11,8 12,7 12,8; s 0,0 0,5 0,9 1,4 1,8 1,12 2,1 3,5 3,7 3,10 3,12 4,0 4,2 5,9 5,11 6,3 6,6 7,0 7,8 7,12 8,2 8,5 9,0 9,9 10,3 10,6 10,10 11,0 11,5 12,2 12,8 12,11; r C 1,8 3,5 3,12 7,12 12,8 12,11 B 0,9 1,4 3,7 3,10 5,11 9,0 W 1,12 4,0 4,2 8,2 10,6 12,2 P 0,0 2,1 5,9 6,6 9,9 11,5 S 0,5 6,3 7,0 7,8 8,5 10,3 10,10 11,0; p 0 0 0 0 0 0 0 S 9,3 T; p 1 0 0 0 0 0 0 S T;";
        String moveString = "S 0,0";
        System.out.println(isMoveValid(states,moveString));
    }
    public static boolean isMoveValid(String stateString, String moveString) {
//        System.out.println(stateString + " ==> " + moveString);
        PieceType pieceType = PieceType.parse(moveString.split(" ")[0]);
        String[] locations = moveString.split(" ")[1].split(",");
        int row = Integer.parseInt(locations[0]);
        int col = Integer.parseInt(locations[1]);
        if (row < 0 || col < 0) {
            return false;
        }

        String[] states = stateString.split(";");
        int size = Integer.parseInt(states[0].split(" ")[1]);
        int playerCount = Integer.parseInt(states[0].split(" ")[2]);
        Position.positions = new Position[size][size];

        // island

        int playerId = Integer.parseInt(states[1].trim().split(" ")[1]);
        PhaseType phaseType = PhaseType.parse(states[1].trim().split(" ")[2]);
        for (int i = 2; i < size - 1 - 2; i++) {
            String[] positions = states[i].trim().split(" ");
            for (int j = 2; j < positions.length; j++) {
                String positionStr = positions[j];
                int xRow = Integer.parseInt(positionStr.split(",")[0]);
                int xCol = Integer.parseInt(positionStr.split(",")[1]);
                if (Position.positions[xRow][xCol] != null) {
                    return false;
                }
                Position.positions[xRow][xCol] = new Position(xRow, xCol, true);
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (Position.positions[i][j] == null) {
                    Position.positions[i][j] = new Position(i, j, false);
                }
            }
        }

        List<Position> stonePositionList = new ArrayList<>();
        String[] moves = states[states.length - 2 - playerCount].trim().split(" ");
        for (int i = 1; i < moves.length; i++) {
            String positionStr = moves[i];
            int xRow = Integer.parseInt(positionStr.split(",")[0]);
            int xCol = Integer.parseInt(positionStr.split(",")[1]);
            stonePositionList.add(Position.positions[xRow][xCol]);
        }

        String resources = states[states.length - 1 - playerCount].trim().substring(1).trim();
        String[] resourceInfos = resources.split(" ");
        String resourceType = null;
        for (int i = 0; i < resourceInfos.length; i++) {
            String positionStr = resourceInfos[i].trim();
            if (!positionStr.contains(",")) {
                resourceType = positionStr;
                continue;
            }
            int xRow = Integer.parseInt(positionStr.split(",")[0]);
            int xCol = Integer.parseInt(positionStr.split(",")[1]);
            Position.positions[xRow][xCol].resources.add(resourceType);
        }

        List<Player> playerList = new ArrayList<>();
        for (int i = states.length - playerCount; i < states.length; i++) {
            String[] infos = states[i].split(" ");
            int[][] settlerCoords = new int[size][size];
            int[][] villageCoords = new int[size][size];
            assert infos[9].equals("S");
            int j = 0;
            for (j = 10; j < infos.length - 1; j++) {
                String info = infos[j];
                if (info.equalsIgnoreCase("T")) {
                    break;
                }
                settlerCoords[Integer.parseInt(info.split(",")[0])][Integer.parseInt(info.split(",")[1])] = 1;
            }
            for (j = j + 1; j < infos.length - 1; j++) {
                String info = infos[j];
                villageCoords[Integer.parseInt(info.split(",")[0])][Integer.parseInt(info.split(",")[1])] = 1;
            }
            Player player = new Player(Integer.parseInt(infos[2]),
                    Integer.parseInt(infos[3]), Integer.parseInt(infos[4]), Integer.parseInt(infos[5]),
                    Integer.parseInt(infos[6]), Integer.parseInt(infos[7]), Integer.parseInt(infos[8]),
                    settlerCoords, villageCoords);
            playerList.add(player);
            for (Position position : player.getOccupiedPosList()) {
                position.owner = player;
            }
        }

        Player player = playerList.stream().filter(playerTmp -> playerTmp.playerID == playerId).findFirst().orElse(null);
        if (player == null) {
            return false;
        }
        //
        Position movePos = Position.getPosition(row, col);
        if (movePos.owner != null) {
            return false;
        }
        List<Position> occupiedPosList = player.getOccupiedPosList();
        boolean isNeighbor = false;
        if (occupiedPosList.size() == 0) {
            isNeighbor = true;
        } else {
            for (Position position : occupiedPosList) {
                List<Position> neighbors = Position.getNeighbors(position);
                if (neighbors.contains(movePos)) {
                    isNeighbor = true;
                    break;
                }
            }
        }
        if (!isNeighbor) {
            return false;
        }

        if (PhaseType.Exploration.equals(phaseType)) {
            if (PieceType.VILLAGE.equals(pieceType) && !movePos.island) {
                return false;
            }
        } else if (PhaseType.Settlement.equals(phaseType)) {
            if (!(PieceType.SETTLER.equals(pieceType))) {
                return false;
            }
        }

        // FIXME Task 7
        return true;
    }

    /**
     * Given a state string, generate a set containing all move strings playable
     * by the current player.
     * <p>
     * A move is playable if it is valid.
     *
     * @param stateString a string representing a game state
     * @return a set of strings representing all moves the current player can play
     */
    public static Set<String> generateAllValidMoves(String stateString) {
        Set<String> validMoves = new HashSet<>();

        int width = 1200;
        int height = 700;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                String[] pieceTypes = {"S", "T"};

                for (String pieceType : pieceTypes) {
                    String moveString = pieceType + " " + x + "," + y;

                    if (isMoveValid(stateString, moveString)) {
                        validMoves.add(moveString);
                    }
                }
            }
        }

        return new HashSet<>(); // FIXME Task 8
    }

    /**
     * Given a state string, determine whether it represents an end of phase state.
     * <p>
     * A phase is over when either of the following conditions hold:
     * - All resources (not including statuettes) have been collected.
     * - No player has any remaining valid moves.
     *
     * @param stateString a string representing a game state
     * @return true if the state is at the end of either phase and false otherwise
     */
    public static boolean isPhaseOver(String stateString) {
        return false; // FIXME Task 9
    }

    /**
     * Given a state string and a move string, place the piece associated with the
     * move on the board. Ensure the player collects any corresponding resource or
     * statuettes.
     * <p>
     * Do not handle switching to the next player here.
     *
     * @param stateString a string representing a game state
     * @param moveString  a string representing the current player's move
     * @return a new state string achieved by placing the move on the board
     */
    public static String placePiece(String stateString, String moveString) {
        return ""; // FIXME Task 10
    }

    /**
     * Given a state string, calculate the "Islands" portion of the score for
     * each player as if it were the end of a phase. The return value is an
     * integer array sorted by player number containing the calculated score
     * for the respective player.
     * <p>
     * The "Islands" portion is calculated for each player as follows:
     * - If the player has pieces on 8 or more islands, they score 20 points.
     * - If the player has pieces on 7 islands, they score 10 points.
     * - No points are scored otherwise.
     *
     * @param stateString a string representing a game state
     * @return an integer array containing the calculated "Islands" portion of
     * the score for each player
     */
    public static int[] calculateTotalIslandsScore(String stateString) {
        return new int[]{0, 0}; // FIXME Task 11
    }

    /**
     * Given a state string, calculate the "Links" portion of the score for
     * each player as if it were the end of a phase. The return value is an
     * integer array sorted by player number containing the calculated score
     * for the respective player.
     * <p>
     * Players earn points for their chain of pieces that links the most
     * islands. For each island linked by this chain, they score 5 points.
     * <p>
     * Note the chain needn't be a single path. For instance, if the chain
     * splits into three or more sections, all of those sections are counted
     * towards the total.
     *
     * @param stateString a string representing a game state
     * @return an integer array containing the calculated "Links" portion of
     * the score for each player
     */
    public static int[] calculateIslandLinksScore(String stateString) {
        return new int[]{0, 0}; // FIXME Task 11
    }

    /**
     * Given a state string, calculate the "Majorities" portion of the score for
     * each player as if it were the end of a phase. The return value is an
     * integer array sorted by player number containing the calculated score
     * for the respective player.
     * <p>
     * The "Majorities" portion is calculated for each island as follows:
     * - The player with the most pieces on the island scores the number
     * of points that island is worth.
     * - In the event of a tie for pieces on an island, those points are
     * divided evenly between those players rounding down. For example,
     * if two players tied for an island worth 7 points, they would
     * receive 3 points each.
     * - No points are awarded for islands without any pieces.
     *
     * @param stateString a string representing a game state
     * @return an integer array containing the calculated "Majorities" portion
     * of the score for each player
     */
    public static int[] calculateIslandMajoritiesScore(String stateString) {
        return new int[]{0, 0}; // FIXME Task 11
    }

    /**
     * Given a state string, calculate the "Resources" and "Statuettes" portions
     * of the score for each player as if it were the end of a phase. The return
     * value is an integer array sorted by player number containing the calculated
     * score for the respective player.
     * <p>
     * Note that statuettes are not resources.
     * <p>
     * In the below "matching" means a set of the same resources.
     * <p>
     * The "Resources" portion is calculated for each player as follows:
     * - For each set of 4+ matching resources, 20 points are scored.
     * - For each set of exactly 3 matching resources, 10 points are scored.
     * - For each set of exactly 2 matching resources, 5 points are scored.
     * - If they have all four resource types, 10 points are scored.
     * <p>
     * The "Statuettes" portion is calculated for each player as follows:
     * - A player is awarded 4 points per statuette in their possession.
     *
     * @param stateString a string representing a game state
     * @return an integer array containing the calculated "Resources" and "Statuettes"
     * portions of the score for each player
     */
    public static int[] calculateResourcesAndStatuettesScore(String stateString) {
        return new int[]{0, 0}; // FIXME Task 11
    }

    /**
     * Given a state string, calculate the scores for each player as if it were
     * the end of a phase. The return value is an integer array sorted by player
     * number containing the calculated score for the respective player.
     * <p>
     * It is recommended to use the other scoring functions to assist with this
     * task.
     *
     * @param stateString a string representing a game state
     * @return an integer array containing the calculated scores for each player
     */
    public static int[] calculateScores(String stateString) {
        return new int[]{0, 0}; // FIXME Task 11
    }

    /**
     * Given a state string representing an end of phase state, return a new state
     * achieved by following the end of phase rules. Do not move to the next player
     * here.
     * <p>
     * In the Exploration Phase, this means:
     * - The score is tallied for each player.
     * - All pieces are removed from the board excluding villages not on stone circles.
     * - All resources and statuettes remaining on the board are removed. All resources are then
     * randomly redistributed between the stone circles.
     * <p>
     * In the Settlement Phase, this means:
     * - Only the score is tallied and added on for each player.
     *
     * @param stateString a string representing a game state at the end of a phase
     * @return a string representing the new state achieved by following the end of phase rules
     */
    public static String endPhase(String stateString) {
        return ""; // FIXME Task 12
    }

    /**
     * Given a state string and a move string, apply the move to the board.
     * <p>
     * If the move ends the phase, apply the end of phase rules.
     * <p>
     * Advance current player to the next player in turn order that has a valid
     * move they can make.
     *
     * @param stateString a string representing a game state
     * @param moveString  a string representing the current player's move
     * @return a string representing the new state after the move is applied to the board
     */
    public static String applyMove(String stateString, String moveString) {
        return ""; // FIXME Task 13
    }

    /**
     * Given a state string, returns a valid move generated by your AI.
     * <p>
     * As a hint, generateAllValidMoves() may prove a useful starting point,
     * maybe if you could use some form of heuristic to see which of these
     * moves is best?
     * <p>
     * Your AI should perform better than randomly generating moves,
     * see how good you can make it!
     *
     * @param stateString a string representing a game state
     * @return a move string generated by an AI
     */
    public static String generateAIMove(String stateString) {
        return ""; // FIXME Task 16
    }
}
