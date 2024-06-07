package comp1110.ass2;

import comp1110.ass2.testdata.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public class IsPhaseOverTest implements TestMapNamePlayerCount {
    private final GameDataLoader gameDataLoader = new GameDataLoader();
    private final PreEndPhaseDataLoader preEndLoader = new PreEndPhaseDataLoader();
    private final IsPhaseOverDataLoader isOverLoader = new IsPhaseOverDataLoader();

    private static void test(boolean expected, String input){
        if(expected){
            testTrue(input);
        } else{
            testFalse(input);
        }
    }
    private static void testTrue(String input){
        boolean result = BlueLagoon.isPhaseOver(input);
        Assertions.assertTrue(result, "Phase should be over for state: " + input);
    }

    private static void testFalse(String input){
        boolean result = BlueLagoon.isPhaseOver(input);
        Assertions.assertFalse(result, "Phase should not be over for state: " + input);
    }
    private static void testGame(List<String> game, List<String> preEnd, List<Boolean> solutions){
        boolean checkedFirstPreEnd = false;
        for (int i = 0; i < solutions.size(); i++) {
            if(solutions.get(i) && !checkedFirstPreEnd){
                checkedFirstPreEnd = true;
                test(solutions.get(i), preEnd.get(0));
            } else {
                test(solutions.get(i), game.get(2 * (checkedFirstPreEnd ? i - 1 : i)));
            }
        }
        if(preEnd.size() > 1) {
            test(true, preEnd.get(1));
        }
    }

    public void testMapNamePlayerCount(String mapName, int playerCount){
        List<List<String>> games = gameDataLoader.fetchGames(mapName, playerCount);
        List<List<String>> preEndPhase = preEndLoader.fetchGames(mapName, playerCount);
        List<List<Boolean>> solutions = isOverLoader.fetchGames(mapName, playerCount);
        for(int game = 0; game < DataLoader.GAME_COUNT; game++){
            testGame(games.get(game), preEndPhase.get(game), solutions.get(game));
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
        List<List<String>> preEndPhase = preEndLoader.fetchAllEdgeCaseGames();
        List<List<Boolean>> solutions = isOverLoader.fetchAllEdgeCaseGames();
        for(int game = 0; game < games.size(); game++){
            testGame(games.get(game), preEndPhase.get(game), solutions.get(game));
        }
    }
}
