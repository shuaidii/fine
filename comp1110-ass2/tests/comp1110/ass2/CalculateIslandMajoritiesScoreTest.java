package comp1110.ass2;

import comp1110.ass2.testdata.DataLoader;
import comp1110.ass2.testdata.GameDataLoader;
import comp1110.ass2.testdata.IslandMajoritiesScoreDataLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
public class CalculateIslandMajoritiesScoreTest implements TestMapNamePlayerCount {
    private final IslandMajoritiesScoreDataLoader islandMajoritiesScoreDataLoader = new IslandMajoritiesScoreDataLoader();
    private final GameDataLoader gameDataLoader = new GameDataLoader();

    private static void test(int[] expected, String input){
        int[] solution = BlueLagoon.calculateIslandMajoritiesScore(input);
        Assertions.assertArrayEquals(expected, solution, "failed on input: " + input + "\nexpected: "+ Arrays.toString(expected) + "\nactual: " + Arrays.toString(solution));
    }

    private static void testGame(List<String> game, List<List<Integer>> solutions){
        for (int i = 0; i < solutions.size(); i++) {
            List<Integer> solution = solutions.get(i);

            int[] expected = new int[solution.size()];
            for (int j = 0; j < solution.size(); j++) {
                expected[j] = solution.get(j);
            }

            test(expected, game.get(2*i));
        }
    }

    public void testMapNamePlayerCount(String mapName, int playerCount){
        List<List<String>> games = gameDataLoader.fetchGames(mapName, playerCount);
        List<List<List<Integer>>> solutions = islandMajoritiesScoreDataLoader.fetchGames(mapName, playerCount);
        for(int game = 0; game < DataLoader.GAME_COUNT; game++){
            testGame(games.get(game), solutions.get(game));
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
        List<List<List<Integer>>> solutions = islandMajoritiesScoreDataLoader.fetchAllEdgeCaseGames();
        for(int game = 0; game < games.size(); game++){
            testGame(games.get(game), solutions.get(game));
        }
    }
}
