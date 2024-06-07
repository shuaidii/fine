import java.util.Arrays;

public class Runner {
//    public static void main(String[] args) {
//        //For your personal use.
//        SimpleSet simpleSet = new SimpleSet();
//        System.out.println(simpleSet.isEmpty());
//
//        for (int i = 0; i < 15; i++) {
//            simpleSet.add(i);
//        }
//        System.out.println(simpleSet.isEmpty());
//        System.out.println(simpleSet.size());
//
//        for (int i = 0; i < 15; i++) {
//            simpleSet.remove(i * 2);
//        }
//        System.out.println(simpleSet.size());
//
//        System.out.println(Arrays.toString(simpleSet.toArray()));
//
//        System.out.println(simpleSet.toString());
//    }

    public static void main(String[] args) {
        //For your personal use.
        SimpleSet simpleSet1 = new SimpleSet();
        SimpleSet simpleSet2 = new SimpleSet();
        for (int i = 0; i < 5; i++) {
            simpleSet1.add(i);
            simpleSet2.add(i);
        }

        for (int i = 5; i < 10; i++) {
            simpleSet1.add(i * 2);
            simpleSet2.add(i * 2 + 1);
        }
        System.out.println(SimpleSetOperations.union(simpleSet1, simpleSet2));
        System.out.println(SimpleSetOperations.intersection(simpleSet1, simpleSet2));
        System.out.println(SimpleSetOperations.difference(simpleSet1, simpleSet2));
        System.out.println(SimpleSetOperations.subset(simpleSet1, simpleSet2));
        System.out.println(SimpleSetOperations.equals(simpleSet1, simpleSet2));

    }


}
