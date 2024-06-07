package comp1110.ass2;

import comp1110.ass2.testdata.GameDataLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
public class IsStateStringWellFormedTest {
    private final GameDataLoader gameDataLoader = new GameDataLoader();
    private final List<List<String>> allGames = gameDataLoader.fetchAllGames();

    private static void testTrue(String input){
        boolean result = BlueLagoon.isStateStringWellFormed(input);
        Assertions.assertTrue(result, "The following input is a well-formed state but returns false: " + input);
    }

    private static void testFalse(String input){
        boolean result = BlueLagoon.isStateStringWellFormed(input);
        Assertions.assertFalse(result, "The following input is not a well-formed state but returns true: " + input);
    }
    @Test
    public void testAllGameStates(){
        // Guard against case where true returned by default for everything
        testFalse("");
        for (List<String> game : allGames) {
            for (int line = 0; line < game.size(); line += 2) {
                testTrue(game.get(line));
            }
        }
    }

    @Test
    public void testAllMoves(){
        // Guard against case where false returned by default for everything
        testTrue(GameDataLoader.DEFAULT_GAME);
        for (List<String> game : allGames) {
            for (int line = 1; line < game.size(); line += 2) {
                testFalse(game.get(line));
            }
        }
    }

    @Test
    public void testMissingCharacters(){
        // Guard against case where false returned by default for everything
        testTrue(GameDataLoader.DEFAULT_GAME);
        for (int i = 0; i < GameDataLoader.DEFAULT_GAME.length(); i++) {
            char character = GameDataLoader.DEFAULT_GAME.charAt(i);
            if(!Character.isDigit(character)){
                testFalse(GameDataLoader.DEFAULT_GAME.substring(0,i) + GameDataLoader.DEFAULT_GAME.substring(i+1));
            }
        }
    }

    @Test
    public void testExtraSpaces(){
        // Guard against case where false returned by default for everything
        testTrue(GameDataLoader.DEFAULT_GAME);
        for (int i = 0; i < GameDataLoader.DEFAULT_GAME.length(); i++) {
            char character = GameDataLoader.DEFAULT_GAME.charAt(i);
            if(character == ' '){
                testFalse(GameDataLoader.DEFAULT_GAME.substring(0,i) + "  " + GameDataLoader.DEFAULT_GAME.substring(i+1));
            }
        }
    }

    @Test
    public void testWrongCharacters(){
        // Guard against case where false returned by default for everything
        testTrue(GameDataLoader.DEFAULT_GAME);
        for (int i = 0; i < GameDataLoader.DEFAULT_GAME.length(); i++) {
            char character = GameDataLoader.DEFAULT_GAME.charAt(i);
            if(!Character.isDigit(character)){
                testFalse(GameDataLoader.DEFAULT_GAME.substring(0,i) + (char) (character + 1) + GameDataLoader.DEFAULT_GAME.substring(i+1));
                testFalse(GameDataLoader.DEFAULT_GAME.substring(0,i) + (char) (character - 1) + GameDataLoader.DEFAULT_GAME.substring(i+1));
            }
        }
    }

    @Test
    public void testReplacedDigit(){
        // Guard against case where false returned by default for everything
        testTrue(GameDataLoader.DEFAULT_GAME);
        for (int i = 0; i < GameDataLoader.DEFAULT_GAME.length(); i++) {
            char character = GameDataLoader.DEFAULT_GAME.charAt(i);
            if(Character.isDigit(character)){
                testFalse(GameDataLoader.DEFAULT_GAME.substring(0,i) + (char) ('A' + (i % 26)) + GameDataLoader.DEFAULT_GAME.substring(i+1));
                testFalse(GameDataLoader.DEFAULT_GAME.substring(0,i) + (char) ('a' + (i % 26)) + GameDataLoader.DEFAULT_GAME.substring(i+1));
            }
        }
    }
}
