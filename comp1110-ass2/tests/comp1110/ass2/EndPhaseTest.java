package comp1110.ass2;

import comp1110.ass2.stringcomparator.StringComparator;
import comp1110.ass2.testdata.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public class EndPhaseTest implements TestMapNamePlayerCount {
    private final PreEndPhaseDataLoader preEndLoader = new PreEndPhaseDataLoader();
    private final EndPhaseDataLoader endLoader = new EndPhaseDataLoader();
    private static void testGame(List<String> pre, List<String> post){
        assert pre.size() == post.size();
        StringComparator sc = new StringComparator();
        sc.checkAll(StringComparator.ResultType.General);
        sc.checkAll(StringComparator.ResultType.GameArrangement);
        sc.checkAll(StringComparator.ResultType.CurrentState);
        sc.checkAll(StringComparator.ResultType.Island);
        sc.checkAll(StringComparator.ResultType.Stones);
        sc.checkIgnore(StringComparator.ResultType.Resources, List.of(new String[]{"checkMatching"}));
        sc.checkAll(StringComparator.ResultType.Player);

        for (int i = 0; i < pre.size(); i++) {
            String result = BlueLagoon.endPhase(pre.get(i));
            List<String> errors = sc.compare(post.get(i), result);
            if(errors.size() > 0){
                Assertions.fail("\n"+"expected: " + post.get(i) + "\nactual:   " + result + "\nerrors:\n" + String.join("\n", errors));
            }
        }
    }

    public void testMapNamePlayerCount(String mapName, int playerCount){
        List<List<String>> input = preEndLoader.fetchGames(mapName, playerCount);
        List<List<String>> solutions = endLoader.fetchGames(mapName, playerCount);
        for(int game = 0; game < DataLoader.GAME_COUNT; game++){
            testGame(input.get(game), solutions.get(game));
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
        List<List<String>> input = preEndLoader.fetchAllEdgeCaseGames();
        List<List<String>> solutions = endLoader.fetchAllEdgeCaseGames();
        for(int game = 0; game < input.size(); game++){
            testGame(input.get(game), solutions.get(game));
        }
    }
}
