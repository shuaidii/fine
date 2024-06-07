package comp1110.ass2;

import comp1110.ass2.testdata.AllValidMovesDataLoader;
import comp1110.ass2.testdata.DataLoader;
import comp1110.ass2.testdata.GameDataLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
public class IsMoveValidTest implements TestMapNamePlayerCount {
    private final GameDataLoader gameDataLoader = new GameDataLoader();
    private final AllValidMovesDataLoader loader = new AllValidMovesDataLoader();

    private int boardSize = 13;

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    private static void testTrue(String state, String move){
        boolean result = BlueLagoon.isMoveValid(state, move);
        Assertions.assertTrue(result, "Move: " + move + " should be valid for state " + state);
    }

    private static void testFalse(String state, String move){
        boolean result = BlueLagoon.isMoveValid(state, move);
        Assertions.assertFalse(result, "Move: " + move + " should not be valid for state " + state);
    }

    private static final String[] PREFIXES = {"S ", "T "};
    private void testGameRowCol(String state, Set<String> sols, int row, int col){
        for(String prefix : PREFIXES){
            String move = prefix + row + "," + col;
            if(sols.contains(move)){
                testTrue(state, move);
            } else{
                testFalse(state, move);
            }
        }
    }

    private void testGame(List<String> game, List<Set<String>> solutions, boolean odd){
        int row = -1;
        for (int i = 0; i < solutions.size(); i++) {
            Set<String> sols = solutions.get(i);
            String state = game.get(2*i);
            for(int col=odd ? -1 : 0; col < boardSize + 1; col+=2){
                testGameRowCol(state, sols, row, col);
            }

            row = row == boardSize ? -1 : row + 1;
            odd = !odd;
        }
    }

    public void testMapNamePlayerCount(String mapName, int playerCount){
        List<List<String>> games = gameDataLoader.fetchGames(mapName, playerCount);
        List<List<Set<String>>> solutions = loader.fetchGames(mapName, playerCount);

        for(int game = 0; game < DataLoader.GAME_COUNT; game++){
            testGame(games.get(game), solutions.get(game), game % 2 == 0);
        }
    }

    @Test
    public void testDefaultTwoPlayerGames(){
        testMapNamePlayerCount("default", 2);
    }

    @Test
    public void testWheelsTwoPlayerGames(){
        testMapNamePlayerCount("wheels", 2);
    }

    @Test
    public void testFaceTwoPlayerGames(){
        testMapNamePlayerCount("face", 2);
    }

    @Test
    public void testEdgeCaseGames(){
        List<List<String>> games = gameDataLoader.fetchAllEdgeCaseGames();
        List<List<Set<String>>> solutions = loader.fetchAllEdgeCaseGames();

        for(int game = 0; game < games.size(); game++){
            testGame(games.get(game), solutions.get(game), game % 2 == 0);
        }
    }
}
