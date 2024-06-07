package comp1110.ass2.stringcomparator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Comparison class that lets you check certain parts of the String
 * Main method is compare, which will take two strings: A expected and B actual.
 * You can pick which parts of the Strings you want to make sure match. The order of Statements is not
 * important in each String
 * You can choose which checks you want to make using check() checkAll() and checkIgnore(). These take a
 * resultType enum value that points to the type of statement you want to check. You can also choose some
 * options within each resultType (e.g. you may want to just check the NUMBER of resource coordinates in the
 * Resource Statements match, rather than the actual coordinate values).
 * <p>
 * These are the options you have for each resultType
 * General:
 *      "numStatements": do the number of statements in each String match?
 *      "unrecognisedStatements": are there any statements that begin with an unrecognised Statement ID?
 * GameArrangement:
 *      "boardHeight": does the boardHeight value match?
 *      "numPlayers": does the numPlayers value match?
 * CurrentState:
 *      "currentPlayer": does the currentPlayer value match?
 *      "phase": does the phase value match?
 * Island:
 *      No options - just check if the Strings match
 * Stones:
 *      No options - just check if there is exactly 1 Stones Statement, and it matches
 * Resources:
 *      "checkNumCoords": does the number of coordinates match?
 *      "checkMatching": do the actual coordinate values match?
 * Player:
 *      "score": does the score value match?
 *      "resources": do the resource counts match?
 *      "pieces": do the piece coordinates match?
 * <p>
 * An example of checking all General, only the phase of the CurrentState, and all but the resources of Player would be
 *         StringComparator sc = new StringComparator();
 *         sc.checkAll(ResultType.General);
 *         sc.check(ResultType.CurrentState, List.of(new String[]{"phase"}));
 *         sc.checkIgnore(ResultType.Player, List.of(new String[]{"resources"}));
 *         List<String> errors = sc.compare(stateStrA, stateStrB);
 * <p>
 *   errors will be a list of Strings that point out issues found in B, or differences between A and B. It is assumed
 *   that A is well-formed.
 * <p>
 *   A helper method, compareFormatted, will return a formatted String given both A and B and the errors between them.
 */
public class StringComparator {
    Map<ResultType,List<String>> checks = new HashMap<>();

    public void checkIgnore(ResultType resultType, List<String> fields) {
         checkAll(resultType);
         checks.get(resultType).removeAll(fields);
    }

    public void checkAll(ResultType resultType) {
        switch (resultType) {
            case General -> checks.put(ResultType.General,
                    new ArrayList<>(List.of(new String[]{"numStatements", "unrecognisedStatements"}))
            );
            case GameArrangement -> checks.put(ResultType.GameArrangement,
                    new ArrayList<>(List.of(new String[]{"boardHeight", "numPlayers"}))
            );
            case CurrentState -> checks.put(ResultType.CurrentState,
                    new ArrayList<>(List.of(new String[]{"currentPlayer", "phase"}))
            );
            case Island -> checks.put(ResultType.Island,
                    new ArrayList<>());
            case Stones -> checks.put(ResultType.Stones,
                    new ArrayList<>());
            case Resources -> checks.put(ResultType.Resources,
                    new ArrayList<>(List.of(new String[]{"checkNumCoords", "checkMatching"}))
            );
            case Player -> checks.put(ResultType.Player,
                    new ArrayList<>(List.of(new String[]{"score", "resources", "pieces"}))
            );
        }
    }

    public List<String> compare(String stateStringA, String stateStringB) {
        // Result
        List<String> errors = new LinkedList<>();

        // Gather, trim, filter statements
        List<String> statementsA = List.of(stateStringA.split(";"));
        statementsA = statementsA.stream().map(String::trim)
                .filter(statement -> statement.length() > 0).collect(Collectors.toList());
        List<String> statementsB = List.of(stateStringB.split(";"));
        statementsB = statementsB.stream().map(String::trim)
                .filter(statement -> statement.length() > 0).collect(Collectors.toList());

        // Check the general string formation
        if (checks.containsKey(ResultType.General)) {
            checkGeneralString(statementsA, statementsB, errors);
        }

        // Check the Game Arrangement Statements
        // “a {boardHeight} {numPlayers}”
        if (checks.containsKey(ResultType.GameArrangement)) {
            checkArrangementStatements(statementsA, statementsB, errors);
        }

        // Check the Current State Statement
        if (checks.containsKey(ResultType.CurrentState)) {
            checkCurrentStateStatements(statementsA, statementsB, errors);
        }

        // Check the Island Statements
        if (checks.containsKey(ResultType.Island)) {
            checkIslandStatements(statementsA, statementsB, errors);
        }

        // Check the Stones Statement
        if (checks.containsKey(ResultType.Stones)) {
            checkStonesStatements(statementsA, statementsB, errors);
        }

        // Check the Unclaimed Resources Statement
        if (checks.containsKey(ResultType.Resources)) {
            checkResourcesStatements(statementsA, statementsB, errors);
        }

        // Check the Player Statements
        if (checks.containsKey(ResultType.Player)) {
            checkPlayerStatements(statementsA, statementsB, errors);
        }

        // Return Result
        return errors;
    }

    private void checkGeneralString(List<String> statementsA, List<String> statementsB, List<String> errors) {
        // Check for the number of statements
        if (checks.get(ResultType.General).contains("num_statements") &&
                statementsA.size() != statementsB.size())
        {
            errors.add("Strings contain different numbers of statements");
        }

        // Check if there are any unrecognised statement IDs
        if (checks.get(ResultType.General).contains("unrecognised_statements"))
        {
            List<String> unrecognised = statementsB
                    .stream()
                    .filter(statement -> !statement.matches("[acisrp].*"))
                    .toList();
            if (unrecognised.size() != 0) {
                errors.add("Unrecognised Statements: "+unrecognised);
            }
        }
    }

    private void checkPlayerStatements(List<String> statementsA, List<String> statementsB, List<String> errors) {
        //“p {playerId} {score} {coconut} {bamboo} {water} {preciousStone} {statuette} S ({row,col})* V ({row,col})*”
        List<String> toCheck = checks.get(ResultType.Player);
        List<String> playerStatementsA = getStatement(statementsA, 'p');
        List<String> playerStatementsB = getStatement(statementsB, 'p');
        if (playerStatementsA.size() != playerStatementsB.size()) {
            errors.add("Different number of Player Statements. Expecting: "+playerStatementsA.size()+
                    " Found: "+playerStatementsB.size());
        }
        // Break into checkable pieces
        List<List<String>> playerAParts = playerStatementsA
                .stream()
                .map(this::getPlayerParts)
                .toList();
        // Note the IDs expected
        boolean unexpectedID = false;
        List<String> expectedIDs = new ArrayList<>();
        for (List<String> parts : playerAParts) {
            expectedIDs.add(parts.get(1));
        }
        // Check through all submitted Player Statements
        outer: for (String playerBStr : playerStatementsB) {
            List<String> playerBParts = getPlayerParts(playerBStr);
            if (playerBParts.size() != 10) {
                errors.add("Player Statement not well formed "+playerBStr);
                continue;
            }
            String playerBID = playerBParts.get(1);
            // Note if the ID was not expected
            if (expectedIDs.contains(playerBID)) {
                expectedIDs.remove(playerBID);
            } else {
                unexpectedID = true;
                continue;
            }
            // Find the matching Player Statement in A
            for (List<String> playerA : playerAParts) {
                if (playerBID.equals(playerA.get(1))) {
                    // We've found the matching playerID
                    // Check score
                    if (toCheck.contains("score") &&
                            !playerBParts.get(2).equals(playerA.get(2)))
                        errors.add("Player Statement with ID: "+playerBID+" has incorrect {score} information");
                    // Check resource numbers
                    if (toCheck.contains("resources")) {
                        for (int i = 3; i <= 7; i++) {
                            if (!playerBParts.get(i).equals(playerA.get(i))) {
                                errors.add("Player Statement with ID: "+playerBID+" has incorrect {resource} information");
                                break;
                            }
                        }
                    }
                    // Check piece coordinates
                    if (toCheck.contains("pieces") &&
                            (!playerBParts.get(8).equals(playerA.get(8)) ||
                             !playerBParts.get(9).equals(playerA.get(9))))
                        errors.add("Player Statement with ID: "+playerBID+" has incorrect {piece} information");
                    continue outer;
                }
            }
            errors.add("No matching Player ID for Player: "+playerBStr);
        }
        // Check that no unexpected IDs were found
        if (unexpectedID) {
            errors.add("An unexpected Player ID was found");
        }
        // Check that all expectedIDs were found
        if (expectedIDs.size() != 0) {
            errors.add("Missing Player Statements. Should have IDs: "+expectedIDs);
        }
    }

    List<String> getPlayerParts(String playerStatement) {
        String[] splitStatement = playerStatement.split("[SV]");
        List<String> parts = new ArrayList<>(Arrays.stream(splitStatement[0].split(" ")).toList());
        parts.add(splitStatement[1].trim());
        if (splitStatement.length == 3) {
            parts.add(splitStatement[2].trim());
        } else {
            parts.add("");
        }
        return parts;
    }

    private void checkResourcesStatements(List<String> statementsA, List<String> statementsB, List<String> errors) {
        List<String> toCheck = checks.get(ResultType.Resources);
        List<String> resourcesStatementsA = getStatement(statementsA, 'r');
        List<String> resourcesStatementsB = getStatement(statementsB, 'r');
        if (resourcesStatementsB.size() != 1) {
            errors.add("String should have 1 Current State Statement. There are: "+resourcesStatementsB.size());
            return;
        }
        String resourceA = resourcesStatementsA.get(0);
        String resourceB = resourcesStatementsB.get(0);
        String[] resourceAParts = resourceA.split("[CBWPS]");
        resourceAParts = Arrays.copyOfRange(resourceAParts, 1, resourceAParts.length);
        String[] resourceBParts = resourceB.split("[CBWPS]");
        resourceBParts = Arrays.copyOfRange(resourceBParts, 1, resourceBParts.length);
        // Check the coords in B are well-formed
        boolean wellFormed = resourceB.charAt(0) == 'r' && Arrays.stream(resourceBParts)
                .allMatch(StringComparator::coordsStrWellFormed);
        if (!wellFormed) {
            errors.add("Resource Statement coordinates are not well formed");
            return;
        }
        // Check that there is a matching NUMBER of resource coordinates
        if (toCheck.contains("checkNumCoords")) {
            int numACoords = Arrays.stream(resourceAParts)
                    .map(coordsStr -> coordsStr.split(" ").length)
                    .reduce(0, Integer::sum);
            int numBCoords = Arrays.stream(resourceBParts)
                    .map(coordsStr -> coordsStr.split(" ").length)
                    .reduce(0, Integer::sum);
            if (numACoords != numBCoords) {
                errors.add("Number of placed resources does not match");
            }
        }
        // Check that the resource coordinates ACTUALLY match
        if (toCheck.contains("checkMatching")) {
            if (!resourceA.equals(resourceB)) {
                errors.add("Resource Statements do not match");
            }
        }
    }

    static boolean coordsStrWellFormed(String coordsStr) {
        if(coordsStr.trim().length() == 0){
            return true;
        }
        return Arrays.stream(coordsStr.trim().split(" "))
                .allMatch(coordinate -> coordinate.matches("\\d+,\\d+"));
    }

    private void checkStonesStatements(List<String> statementsA, List<String> statementsB, List<String> errors) {
        List<String> stonesStatementsA = getStatement(statementsA, 's');
        List<String> stonesStatementsB = getStatement(statementsB, 's');
        if (stonesStatementsB.size() != 1) {
            errors.add("String should have 1 Stones Statement. Found "+stonesStatementsB.size());
            return;
        }
        if (!stonesStatementsB.equals(stonesStatementsA)) {
            errors.add("Stones Statements do not match");
        }
    }

    private void checkIslandStatements(List<String> statementsA, List<String> statementsB, List<String> errors) {
        //“i {bonus} ({row,col})*”
        List<String> islandStatementsA = new ArrayList<>(getStatement(statementsA, 'i'));
        List<String> islandStatementsB = new ArrayList<>(getStatement(statementsB, 'i'));
        if (islandStatementsA.size() != islandStatementsB.size()) {
            errors.add("Different number of Island Statements. Expecting: "+islandStatementsA.size()+
                    " Found: "+islandStatementsB.size());
        }
        outer: for (String bStatement : islandStatementsB) {
            for (String aStatement : islandStatementsA) {
                if (aStatement.equals(bStatement)) {
                    islandStatementsA.remove(aStatement);
                    continue outer;
                }
            }
            errors.add("Island Statement matches none in expected: "+bStatement);
        }
        if (islandStatementsA.size() != 0) {
            errors.add("Island Statements expected but not found: "+islandStatementsA);
        }
    }

    private void checkCurrentStateStatements(List<String> statementsA, List<String> statementsB, List<String> errors) {
        //"c {currentPlayer} {phase}"
        List<String> toCheck = checks.get(ResultType.CurrentState);
        List<String> currentStateStatementsA = getStatement(statementsA, 'c');
        List<String> currentStateStatementsB = getStatement(statementsB, 'c');
        if (currentStateStatementsB.size() != 1) {
            errors.add("String should have 1 Current State Statement. There are: " + currentStateStatementsB.size());
            return;
        }
        String[] AParts = currentStateStatementsA.get(0).split(" ");
        String[] BParts = currentStateStatementsB.get(0).split(" ");
        if (AParts.length != BParts.length) {
            errors.add("Current State Statement not well formed");
            return;
        }
        if (toCheck.contains("currentPlayer") && !AParts[1].equals(BParts[1])) {
            errors.add("Current State {currentPlayer} does not match");
        }
        if (toCheck.contains("phase") && !AParts[2].equals(BParts[2])) {
            errors.add("Current State {phase} does not match");
        }
    }

    private void checkArrangementStatements(List<String> statementsA, List<String> statementsB, List<String> errors) {
        List<String> toCheck = checks.get(ResultType.GameArrangement);
        List<String> arrangementStatementsA = getStatement(statementsA, 'a');
        List<String> arrangementStatementsB = getStatement(statementsB, 'a');
        if (arrangementStatementsB.size() != 1) {
            errors.add("String should have 1 Arrangement Statement. There are: " + arrangementStatementsB.size());
            return;
        }
        String[] AParts = arrangementStatementsA.get(0).split(" ");
        String[] BParts = arrangementStatementsB.get(0).split(" ");
        if (AParts.length != BParts.length) {
            errors.add("Arrangement Statement not well formed");
            return;
        }
        if (toCheck.contains("boardHeight") && !AParts[1].equals(BParts[1])) {
            errors.add("Arrangement Statement {boardHeight} does not match");
        }
        if (toCheck.contains("numPlayers") && !AParts[2].equals(BParts[2])) {
            errors.add("Arrangement Statement {numPlayers} does not match");
        }
    }

    // Return the statements that match the identifier
    static List<String> getStatement(List<String> statements, char statementIdentifier) {
        return statements
                .stream()
                .filter(statement -> statement.length() > 0 && statement.startsWith(String.valueOf(statementIdentifier)))
                .toList();
    }

    public enum ResultType {
        General, GameArrangement, CurrentState, Island, Stones, Resources, Player
    }
}
