package comp1110.ass1.challenge;

import java.util.Random;

public class Challenge {
    // The challenge number from game booklet
    private final int challengeNumber;

    // The static configuration of the board (apple) including
    // caterpillar head requirements in the challenge
    private final String layout;

    // Each challenge in the game booklet
    public static final Challenge[] CHALLENGES = new Challenge[] {
            // Starter
            new Challenge(1, "12C4EG12T20Y11"),
            new Challenge(2, "12C4EG10T12Y22"),
            new Challenge(3, "12C4EG43T42Y12"),
            new Challenge(4, "12C4EG12T20Y10"),
            new Challenge(5, "12C4EG43T42Y11"),
            new Challenge(6, "12C4EG23T01Y22"),
            new Challenge(7, "12C4EG12T10Y42"),
            new Challenge(8, "12C4EG43T10Y44"),
            new Challenge(9, "12C4EG23T01Y42"),
            new Challenge(10, "12C4EG43T12Y20"),
            new Challenge(11, "12C4EG12T42Y11"),
            new Challenge(12, "12C4EG03T34Y01"),
            new Challenge(13, "12C4EG23T24Y01"),
            new Challenge(14, "12C4EG12T10Y30"),
            new Challenge(15, "12C4EG23T24Y11"),
            new Challenge(16, "12C4EG41T01Y04"),
            // Junior
            new Challenge(17, "1BCD5T22Y31X14"),
            new Challenge(18, "1BCD5Y43X01X44"),
            new Challenge(19, "1BCD5T42X01X21"),
            new Challenge(20, "1BCD5G23X02X22"),
            new Challenge(21, "1BCD5X23X33X24"),
            new Challenge(22, "1BCD5X01X34X44"),
            new Challenge(23, "1BCD5X20X30X33"),
            new Challenge(24, "1BCD5X40X01X14"),
            new Challenge(25, "1BCD5X01X31X23"),
            new Challenge(26, "1BCD5X22X33X14"),
            new Challenge(27, "1BCD5X31X02X14"),
            new Challenge(28, "1BCD5X11X12X42"),
            new Challenge(29, "1BCD5X01X31X13"),
            new Challenge(30, "1BCD5X10X40X42"),
            new Challenge(31, "1BCD5X40X23X24"),
            new Challenge(32, "1BCD5X20X22X14"),
            // Expert
            new Challenge(33, "A2C4EX11X12"),
            new Challenge(34, "1234EX11X43"),
            new Challenge(35, "A2345X32"),
            new Challenge(36, "A23D5X24"),
            new Challenge(37, "ABC4EX12"),
            new Challenge(38, "A234EX11"),
            new Challenge(39, "AB3D5X44"),
            new Challenge(40, "12C45X42"),
            new Challenge(41, "1B34EX21X44"),
            new Challenge(42, "1B3D5X10X01"),
            new Challenge(43, "12CD5X41X44"),
            new Challenge(44, "1BC45X10X23"),
            new Challenge(45, "1B345X03"),
            new Challenge(46, "A23DE"),
            new Challenge(47, "A2CDEX33"),
            new Challenge(48, "ABC45X23"),
            // Master
            new Challenge(49, "A2C45"),
            new Challenge(50, "12345"),
            new Challenge(51, "ABCDE"),
            new Challenge(52, "123DE"),
            new Challenge(53, "AB34EX22"),
            new Challenge(54, "AB345X43"),
            new Challenge(55, "A2CD5X13"),
            new Challenge(56, "ABCD5X00"),
            new Challenge(57, "1B3DEX22"),
            new Challenge(58, "12CDEX13"),
            new Challenge(59, "AB3DE"),
            new Challenge(60, "1BCDEX31")
    };

    /**
     * Constructor for a challenge. It takes a challenge number (used to later
     * identify the challenge) and puzzle layout, denoting the board (apple) configuration.
     *
     * @param challengeNumber the challenge number as-per the game booklet
     * @param layout of the board (apple)
     */
    public Challenge(int challengeNumber, String layout) {
        assert challengeNumber >= 1 && challengeNumber <= 60;
        this.challengeNumber = challengeNumber;
        this.layout = layout;
    }

    /**
     * @return the problem number of this challenge
     */
    public int getChallengeNumber() {
        return this.challengeNumber;
    }

    /**
     * The static configuration of the board (apple)
     *
     * This is split into two parts.
     *
     * The first 5 characters represent the 5 apple rows selected
     * for use in the challenge, top to bottom:
     *
     * First row: '1' or 'A'
     * Second row: '2' or 'B'
     * Third row: '3' or 'C'
     * Fourth row: '4' or D'
     * Fifth row: '5' or 'E'
     *
     * The remaining characters encode the caterpillar head requirements
     * Each consecutive group of 3 characters encodes a spot requirement:
     *
     * The first character in each block is a character 'T', 'G', 'Y', or 'X'
     * that corresponds to a caterpillar colour ('X' stands for a wildcard
     * that any caterpillar can satisfy).
     *
     * The next two characters are digits from the set '0', '1', ... '4',
     * corresponding to the x (column) and y (row) position respectively of
     * the apple spot that the requirement applies to.
     *
     * @return the static board layout of the challenge
     */
    public String getLayout() {
        return this.layout;
    }

    /**
     * A random challenge of the provided difficulty
     *
     * Difficulty is defined as:
     *     0 "Starter" includes challenge numbers 1 to 16 inclusive
     *     1 "Junior" includes challenge numbers 17 to 32 inclusive
     *     2 "Expert" includes challenge numbers 33 to 48 inclusive
     *     3 "Master" includes challenge numbers 49 to 60 inclusive
     *
     * @param difficulty a number within the range 0 to 3 inclusive, with higher numbers
     *                   denoting a harder difficulty
     * @return a random challenge of the given difficulty
     */
    public static Challenge randomChallenge(int difficulty) {
        assert difficulty >= 0 && difficulty <= 3;
        Random random = new Random();
        if (difficulty == 0) {
            return CHALLENGES[random.nextInt(16)];
        } else if (difficulty == 1) {
            return CHALLENGES[random.nextInt(16) + 16];
        } else if (difficulty == 2) {
            return CHALLENGES[random.nextInt(16) + 32];
        } else {
            return CHALLENGES[random.nextInt(12) + 48]; // FIXME Task 3
        }
    }

    /**
     * @param challengeNumber the challenge number as-per the game booklet
     * @return challenge associated with challenge number
     */
    public static Challenge challengeFromNumber(int challengeNumber) {
        for (Challenge chal : CHALLENGES) {
            if (chal.getChallengeNumber() == challengeNumber) {
                return chal;
            }
        }
        return null;
    }

    /**
     * Method returns the challenge layout string given a challenge number.
     * Method is only used for unit testing purposes.
     * @return challenge layout if challenge number found, null otherwise
     */
    public static String getChallengeLayout(int challengeNumber) {
        for (Challenge challenge : CHALLENGES) {
            if (challenge.challengeNumber == challengeNumber) {
                return challenge.layout;
            }
        }
        return null;
    }
}
