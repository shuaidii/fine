package comp1110.ass2;

import comp1110.ass2.testdata.DataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

@Timeout(value = 30000, unit = TimeUnit.MILLISECONDS)
public class ThreeFourPlayerTest {
    private static void runTest(TestMapNamePlayerCount testClass){
        for(String mapName : DataLoader.NON_VARIABLE_SIZE){
            for(int players = 3; players <= 4; players++){
                testClass.testMapNamePlayerCount(mapName, players);
            }
        }
    }

    @Test
    public void applyMoveTest(){
        runTest(new ApplyMoveTest());
    }

    @Test
    public void calculateIslandLinksScoreTest(){
        runTest(new CalculateIslandLinksScoreTest());
    }

    @Test
    public void calculateIslandMajoritiesScoreTest(){
        runTest(new CalculateIslandMajoritiesScoreTest());
    }

    @Test
    public void calculateResourcesAndStatuettesScoreTest(){
        runTest(new CalculateResourcesAndStatuettesScoreTest());
    }

    @Test
    public void calculateScoresTest(){
        runTest(new CalculateScoresTest());
    }

    @Test
    public void calculateTotalIslandsScoreTest(){
        runTest(new CalculateTotalIslandsScoreTest());
    }

    @Test
    public void endPhaseTest(){
        runTest(new EndPhaseTest());
    }

    @Test
    public void generateAIMoveTest(){
        runTest(new GenerateAIMoveTest());
    }

    @Test
    public void generateAllValidMovesTest(){
        runTest(new GenerateAllValidMovesTest());
    }

    @Test
    public void isMoveValidTest(){
        runTest(new IsMoveValidTest());
    }

    @Test
    public void isPhaseOverTest(){
        runTest(new IsPhaseOverTest());
    }

    @Test
    public void placePieceTest(){
        runTest(new PlacePieceTest());
    }
}
