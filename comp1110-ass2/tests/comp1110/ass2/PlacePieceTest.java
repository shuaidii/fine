package comp1110.ass2;

import comp1110.ass2.stringcomparator.StringComparator;
import comp1110.ass2.testdata.DataLoader;
import comp1110.ass2.testdata.GameDataLoader;
import comp1110.ass2.testdata.PlacePieceDataLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
public class PlacePieceTest implements TestMapNamePlayerCount {
    private final GameDataLoader gameDataLoader = new GameDataLoader();
    private final PlacePieceDataLoader placePieceDataLoader = new PlacePieceDataLoader();
    private static void testGame(List<String> game, List<String> solutions){
        for (int i = 2; i < game.size(); i+=2) {
            StringComparator sc = new StringComparator();
            sc.checkAll(StringComparator.ResultType.General);
            sc.checkAll(StringComparator.ResultType.GameArrangement);
            sc.checkAll(StringComparator.ResultType.CurrentState);
            sc.checkAll(StringComparator.ResultType.Island);
            sc.checkAll(StringComparator.ResultType.Stones);
            sc.checkAll(StringComparator.ResultType.Resources);
            sc.checkAll(StringComparator.ResultType.Player);

            String result = BlueLagoon.placePiece(game.get(i-2), game.get(i-1));
            List<String> errors = sc.compare(solutions.get(i / 2 - 1), result);
            if(errors.size() > 0){
                Assertions.fail("\n"+"expected: " + solutions.get(i / 2 - 1) + "\nactual:   " + result + "\nerrors:\n" + String.join("\n", errors));
            }
        }
    }

    public void testMapNamePlayerCount(String mapName, int playerCount){
        List<List<String>> games = gameDataLoader.fetchGames(mapName, playerCount);
        List<List<String>> solutions = placePieceDataLoader.fetchGames(mapName, playerCount);
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
        List<List<String>> solutions = placePieceDataLoader.fetchAllEdgeCaseGames();
        for(int game = 0; game < games.size(); game++){
            testGame(games.get(game), solutions.get(game));
        }
    }
}
