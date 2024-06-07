import java.util.Iterator;

public class SimpleSet implements Iterable<Integer>{

    private int[] data;
    private int size;

    public SimpleSet() {
        data = new int[10];
        size = 0;
    }

    public SimpleSet(int... numbers) {
        data = new int[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            data[i] = numbers[i];
            size++;
        }
    }

    public void add(int number) {
        if (contains(number)) {
            return;
        }
        if (data.length == size) {
            // if data is full, resize it
            int[] tmp = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                tmp[i] = data[i];
            }
            data = tmp;
        }
        data[size++] = number;
    }

    public void remove(int number) {
        if (!contains(number)) {
            return;
        }
        for (int i = 0; i < size; i++) {
            if (data[i] == number) {
                for (int j = i; j < size - 1; j++) {
                    data[j] = data[j + 1];
                }
                data[--size] = 0;
            }
        }
    }

    public boolean contains(int number) {
        for (int i = 0; i < size; i++) {
            if (data[i] == number) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int[] toArray() {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = data[i];
        }
        return array;
    }

    public String toString() {
        String message = "{";
        for (int i = 0; i < size; i++) {
            message += data[i];
            if (i != size - 1) {
                message += ", ";
            }
        }
        message += "}";
        return message;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new SimpleSetIterator(toArray());
    }

    class SimpleSetIterator implements Iterator<Integer> {
        private int[] array;
        private int index = 0;

        public SimpleSetIterator(int[] array) {
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return index < array.length;
        }

        @Override
        public Integer next() {
            return array[index++];
        }

    }

}
