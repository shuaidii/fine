import java.util.List;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.stream.Collectors;

public class NumberSequence {

    /**
     * The following section of code does not need to
     * be modified. The section to complete is indicated
     * below.
     **/

    //The array the data will be stored in
    private final int[] sequence;

    //The constructor for the class
    public NumberSequence(String filename) {
        this.sequence = NumberSequence.readFile(filename);
    }

    //A helper method to read the data in from the file.
    //See the example file for the format if you want to
    //write your own tests.
    private static int[] readFile(String filename) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            List<String> rawData = reader.lines().collect(Collectors.toList());
            int[] sequence = new int[rawData.size()];

            for (int i = 0; i < rawData.size(); i++) {
                sequence[i] = Integer.parseInt(rawData.get(i).trim());
            }

            return sequence;
        } catch (FileNotFoundException e) {
            System.err.println("There is no file with the name: " + filename);
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Something went wrong reading data from the the file " + filename + ".");
            System.err.println(e.getMessage());
        }

        return null;
    }

    /**
     * Write your code below here.
     **/
    public void printResult() {
        System.out.printf("There are %d numbers.%n", sequence.length);

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int sum = 0;
        for (int number : sequence) {
            max = Math.max(max, number);
            min = Math.min(min, number);
            sum += number;
        }

        System.out.printf("The maximum is %d.%n", max);
        System.out.printf("The minimum is %d.%n", min);
        System.out.printf("The mean is %s.%n", getMean(sum, sequence.length));

        if (isIncr()) {
            System.out.println("The sequence is increasing.");
        } else if (isDecr()) {
            System.out.println("The sequence is decreasing.");
        } else {
            System.out.println("The sequence is wobbly or short.");
        }
    }

    private String getMean(int sum, int length) {
        double mean = sum * 1.0 / length;
        String str = String.valueOf(mean);
        while (str.endsWith("0")) {
            str = str.substring(0, str.length() - 1);
        }
        if (str.endsWith(".")) {
            str += "0";
        }
        return str;
    }

    private boolean isIncr() {
        if (sequence.length <= 1) {
            return false;
        }
        for (int i = 0; i < sequence.length - 1; i++) {
            if (sequence[i + 1] <= sequence[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isDecr() {
        if (sequence.length <= 1) {
            return false;
        }
        for (int i = 0; i < sequence.length - 1; i++) {
            if (sequence[i + 1] >= sequence[i]) {
                return false;
            }
        }
        return true;
    }


}
