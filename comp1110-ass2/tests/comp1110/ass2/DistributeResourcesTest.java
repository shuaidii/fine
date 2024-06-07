package comp1110.ass2;

import comp1110.ass2.testdata.GameDataLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
public class DistributeResourcesTest {

    private static final Set<Character> resourceCharacters = new HashSet<>(List.of('C','B','W','P','S'));
    private static final int SIMULATIONS = 5000;
    private static final int BOUNDARY = 150;
    private static final double RESOURCE_EXPECTED = ((double) SIMULATIONS) * 6 / 32;
    private static final double STATUETTE_EXPECTED = ((double) SIMULATIONS) * 8 / 32;

    private static String fetchResourceComponent(String stateString){
        String[] parts = stateString.split("; ");
        for (String part : parts) {
            Assertions.assertFalse(part.isEmpty(),
                    "Empty statement in state string: " + stateString);
            if (part.charAt(0) == 'r'){
                Assertions.assertTrue(part.length() >= 3,
                        "Unclaimed resource statement too short in state string: " + stateString);
                return part.substring(2);
            }
        }
        throw new IllegalArgumentException("No resource statement found in: " + stateString);
    }

    private static String fetchStoneCircleComponent(String stateString){
        String[] parts = stateString.split("; ");
        for (String part : parts) {
            if (part.charAt(0) == 's'){
                return part.substring(2);
            }
        }
        throw new IllegalArgumentException("No resource component found for: " + stateString);
    }


    private static HashMap<String, HashMap<Character, Integer>> simulateDistribution(String toDistribute){
        HashMap<String, HashMap<Character, Integer>> overallDistribution = new HashMap<>();

        for(String stoneCircle : fetchStoneCircleComponent(toDistribute).split(" ")){
            HashMap<Character, Integer> resourceHashmap = new HashMap<>();
            for(Character r : resourceCharacters){
                resourceHashmap.put(r, 0);
            }
            overallDistribution.put(stoneCircle, resourceHashmap);
        }

        for (int i = 0; i < SIMULATIONS; i++) {
            String resourceComponent = fetchResourceComponent(BlueLagoon.distributeResources(toDistribute));
            Character lastCharacter = null;

            HashMap<Character, Integer> resourceCounts = new HashMap<>();
            for(Character r : resourceCharacters){
                resourceCounts.put(r, 0);
            }
            for(String value : resourceComponent.split(" ")){
                if(value.length() == 1 && resourceCharacters.contains(value.charAt(0))){
                    lastCharacter = value.charAt(0);
                    continue;
                }
                resourceCounts.put(lastCharacter, resourceCounts.get(lastCharacter) + 1);
                overallDistribution.get(value).put(lastCharacter, overallDistribution.get(value).get(lastCharacter) + 1);
            }

            for(Character r : resourceCharacters){
                Assertions.assertEquals(r == 'S' ? 8 : 6, resourceCounts.get(r), "Expected " + (r == 'S' ? 8 : 6)
                + " resources of type " + r + " to be distributed amongst the below board but got "
                + resourceCounts.get(r) + " instead\n" + toDistribute);
            }
        }
        return overallDistribution;
    }


    private static void testSufficientlyRandom(HashMap<String, HashMap<Character, Integer>> distribution){
        for (String coordinate : distribution.keySet()){
            int total = 0;
            for(Character resource : resourceCharacters){
                int amount = distribution.get(coordinate).get(resource);
                if(resource.equals('S')){
                    Assertions.assertTrue(STATUETTE_EXPECTED - BOUNDARY <= amount && amount <= STATUETTE_EXPECTED + BOUNDARY,
                            "After " + SIMULATIONS + " simulations expect statuettes at coordinate " + coordinate
                                    + " to be between " + (STATUETTE_EXPECTED - BOUNDARY) + " and " + (STATUETTE_EXPECTED + BOUNDARY)
                                    + " but got " + amount + " instead");
                } else{
                    Assertions.assertTrue(RESOURCE_EXPECTED - BOUNDARY <= amount && amount <= RESOURCE_EXPECTED + BOUNDARY,
                            "After " + SIMULATIONS + " simulations expect resource " + resource + " at coordinate " + coordinate
                                    + " to be between " + (RESOURCE_EXPECTED - BOUNDARY) + " and " + (RESOURCE_EXPECTED + BOUNDARY)
                                    + " but got " + amount + " instead");
                }
                total += amount;
            }
            Assertions.assertEquals(SIMULATIONS, total, " expected " + SIMULATIONS + " total resources counted at coordinate "
                    + coordinate + " after " + SIMULATIONS + " simulations but counted " + total);
        }
    }

    public static void testGame(String game){
            testSufficientlyRandom(simulateDistribution(game));
    }

    @Test
    public void testDefaultGame(){
        testGame(GameDataLoader.DEFAULT_GAME);
    }

    @Test
    public void testWheelsGame(){
        testGame(GameDataLoader.WHEELS_GAME);
    }

    @Test
    public void testFaceGame(){
        testGame(GameDataLoader.FACE_GAME);
    }
}
