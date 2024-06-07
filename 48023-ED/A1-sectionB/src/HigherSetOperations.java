
public class HigherSetOperations {

    public static SimpleSet map(SimpleSet simpleSet, UnaryFunction function) {
        SimpleSet result = new SimpleSet();

        int[] array = simpleSet.toArray();
        for (int i = 0; i < array.length; i++) {
            result.add(function.apply(array[i]));
        }

        return result;
    }

    public static int fold(SimpleSet simpleSet, BinaryFunction function, int value) {
        int[] array = simpleSet.toArray();
        for (int i = 0; i < array.length; i++) {
            value = function.apply(value, array[i]);
        }
        return value;
    }

    public static SimpleSet filter(SimpleSet simpleSet, BooleanFunction function) {
        SimpleSet result = new SimpleSet();

        int[] array = simpleSet.toArray();
        for (int i = 0; i < array.length; i++) {
            if (function.apply(array[i])) {
                result.add(array[i]);
            }
        }

        return result;
    }

    public static boolean all(SimpleSet simpleSet, BooleanFunction function) {
        int[] array = simpleSet.toArray();
        for (int i = 0; i < array.length; i++) {
            if (!function.apply(array[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean none(SimpleSet simpleSet, BooleanFunction function) {
        int[] array = simpleSet.toArray();
        for (int i = 0; i < array.length; i++) {
            if (function.apply(array[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean any(SimpleSet simpleSet, BooleanFunction function) {
        return !none(simpleSet, function);
    }
}
