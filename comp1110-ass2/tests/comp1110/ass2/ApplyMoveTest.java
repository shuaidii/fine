package comp1110.ass2;

import comp1110.ass2.stringcomparator.StringComparator;
import comp1110.ass2.testdata.GameDataLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Timeout(value = 2500, unit = TimeUnit.MILLISECONDS)
public class ApplyMoveTest implements TestMapNamePlayerCount {
    private final GameDataLoader gameDataLoader = new GameDataLoader();
    private static void testGame(List<String> game){
        for (int i = 2; i < game.size(); i+=2) {
            StringComparator sc = new StringComparator();
            sc.checkAll(StringComparator.ResultType.General);
            sc.checkAll(StringComparator.ResultType.GameArrangement);
            sc.checkAll(StringComparator.ResultType.CurrentState);
            sc.checkAll(StringComparator.ResultType.Island);
            sc.checkAll(StringComparator.ResultType.Stones);
            if(game.get(i).split(";")[1].charAt(5) == 'S' && game.get(i-2).split(";")[1].charAt(5) == 'E'){
                sc.checkIgnore(StringComparator.ResultType.Resources, List.of(new String[]{"checkMatching"}));
            } else {
                sc.checkAll(StringComparator.ResultType.Resources);
            }
            sc.checkAll(StringComparator.ResultType.Player);

            String result = BlueLagoon.applyMove(game.get(i-2), game.get(i-1));
            List<String> errors = sc.compare(game.get(i), result);
            if(errors.size() > 0){
                Assertions.fail("\n"+"expected: " + game.get(i) + "\nactual:   " + result + "\nerrors:\n" + String.join("\n", errors));
            }
        }
    }

    public void testMapNamePlayerCount(String mapName, int playerCount){
        List<List<String>> games = gameDataLoader.fetchGames(mapName, playerCount);
        for (List<String> game : games) {
            testGame(game);
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
        for (List<String> game : games) {
            testGame(game);
        }
    }
}
