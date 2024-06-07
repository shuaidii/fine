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

@Timeout(value = 30000, unit = TimeUnit.MILLISECONDS)
public class GenerateAIMoveTest implements TestMapNamePlayerCount {
    private final GameDataLoader gameDataLoader = new GameDataLoader();
    private final AllValidMovesDataLoader loader = new AllValidMovesDataLoader();
    private static void testGame(List<String> game, List<Set<String>> solutions){
        for (int i = 0; i < solutions.size() - 1; i++) {
            String move = BlueLagoon.generateAIMove(game.get(2*i));
            Assertions.assertTrue(solutions.get(i).contains(move), "invalid move for state: "
            + game.get(2*i) + "\ngot move: " + move + "\nallowed moves:" + solutions.get(i));
        }
    }

    public void testMapNamePlayerCount(String mapName, int playerCount){
        List<List<String>> games = gameDataLoader.fetchGames(mapName, playerCount);
        List<List<Set<String>>> solutions = loader.fetchGames(mapName, playerCount);
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
        List<List<Set<String>>> solutions = loader.fetchAllEdgeCaseGames();
        for(int game = 0; game < games.size(); game++){
            testGame(games.get(game), solutions.get(game));
        }
    }
}
