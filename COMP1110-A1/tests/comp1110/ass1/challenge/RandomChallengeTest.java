package comp1110.ass1.challenge;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.HashSet;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(value = 2000, unit = MILLISECONDS)
public class RandomChallengeTest {
    private int getDifficulty(Challenge challenge) {
        return (challenge.getChallengeNumber() - 1) / 16;
    }

    private int countChallenges(Challenge[] challenges) {
        HashSet<Challenge> challengeSet = new HashSet<>(Arrays.asList(challenges));
        return challengeSet.size();
    }

    private void doTest(int difficulty) {
        Challenge[] out = new Challenge[20];
        for (int i = 0; i < out.length; i++) {
            Challenge challenge = Challenge.randomChallenge(difficulty);
            Assertions.assertNotNull(challenge, "Expected newChallenge to not return null");
            int actualDifficulty = getDifficulty(challenge);
            assertEquals(
                    difficulty,
                    actualDifficulty,
                    "Expected challenge difficulty "
                            + difficulty
                            + ", on calling randomChallenge("
                            + difficulty
                            + ") but got one of difficulty "
                            + actualDifficulty
                            + ": challenge number " + challenge.getChallengeNumber() + ".");
            out[i] = challenge;
        }
        int unique = countChallenges(out);
        assertTrue(unique >= 3,
                "Expected at least 3 different challenges after calling randomChallenge() 20 times, but only got " + unique + ".");
    }

    @Test
    public void testStarter() {
        doTest(0);
    }

    @Test
    public void testJunior() {
        doTest(1);
    }

    @Test
    public void testExpert() {
        doTest(2);
    }

    @Test
    public void testMaster() {
        doTest(3);
    }
}
