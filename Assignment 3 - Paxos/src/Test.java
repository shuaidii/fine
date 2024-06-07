import vote.Proposer;

import java.util.Arrays;
import java.util.List;

public class Test {
    private static int testCount = 1;

    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
    }

    public static void test1() {
        List<String> membersList = Arrays.asList("M1", "M2");
        List<String> proposalList = Arrays.asList("M1", "M2");
        testInner(membersList, proposalList, Arrays.asList(0.0, 0.0), Arrays.asList(2, 2));
    }

    public static void test2()  {
        List<String> membersList = Arrays.asList("M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9");
        testInner(membersList, membersList, Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    public static void test3() {
        List<String> membersList = Arrays.asList("M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9");
        testInner(membersList, membersList, Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0), Arrays.asList(2, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    public static void test4()  {
        List<String> membersList = Arrays.asList("M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9");
        testInner(membersList, membersList, Arrays.asList(0.0, 0.7, 0.9, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    private static void testInner(List<String> members, List<String> prop, List<Double> chance, List<Integer> responseTime) {
        System.out.println("================= Test " + testCount + " ================");
        System.out.printf("%-12s", "Member");
        for (String member : members) {
            System.out.printf("%-6s", member);
        }

        System.out.println();
        System.out.printf("%-12s", "MissChance");
        for (Double aDouble : chance) {
            System.out.printf("%-6s", aDouble);
        }
        System.out.println();

        CouncilElection election = new CouncilElection(members, prop, chance, responseTime);
        election.vote(new Proposer(0, members.get(0)));

        System.out.println("================= Test " + testCount + " completed! ================");
        System.out.println();

    }
}
