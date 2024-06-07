
public class SimpleSetOperations {

    public static SimpleSet union(SimpleSet one, SimpleSet two) {
        SimpleSet simpleSet = new SimpleSet();

        int[] array = one.toArray();
        for (int i = 0; i < array.length; i++) {
            simpleSet.add(array[i]);
        }

        array = two.toArray();
        for (int i = 0; i < array.length; i++) {
            simpleSet.add(array[i]);
        }

        return simpleSet;
    }

    public static SimpleSet intersection(SimpleSet one, SimpleSet two) {
        SimpleSet simpleSet = new SimpleSet();

        int[] array = one.toArray();
        for (int i = 0; i < array.length; i++) {
            if (two.contains(array[i])) {
                simpleSet.add(array[i]);
            }
        }

        return simpleSet;
    }

    public static SimpleSet difference(SimpleSet one, SimpleSet two) {
        SimpleSet simpleSetUnion = union(one, two);
        SimpleSet simpleSetInter = intersection(one, two);

        SimpleSet simpleSet = new SimpleSet();
        int[] array = simpleSetUnion.toArray();
        for (int i = 0; i < array.length; i++) {
            if (!simpleSetInter.contains(array[i])) {
                simpleSet.add(array[i]);
            }
        }

        return simpleSet;
    }

    public static SimpleSet setMinus(SimpleSet one, SimpleSet two) {
        SimpleSet simpleSet = intersection(one, two);
        return difference(one, simpleSet);
    }

    public static boolean subset(SimpleSet one, SimpleSet two) {
        SimpleSet simpleSet = intersection(one, two);
        return simpleSet.size() == one.size();
    }

    public static boolean properSubset(SimpleSet one, SimpleSet two) {
        return subset(one, two) && !equals(one, two);
    }

    public static boolean equals(SimpleSet one, SimpleSet two) {
        return difference(one, two).size() == 0;
    }
}
