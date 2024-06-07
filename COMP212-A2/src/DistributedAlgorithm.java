import java.io.IOException;
import java.util.Scanner;

public abstract class DistributedAlgorithm {
    static public Scanner sc = new Scanner(System.in);

    abstract void execute(Server.ServerSocketService serverSocketService) throws IOException, ClassNotFoundException;

    public int inputProcessorNumber() {
        while (true) {
            System.out.println("Please enter the number of processors");
            try {
                int number = sc.nextInt();
                if (number < 10000 && number > 0) {
                    return number;
                }
            } catch (Exception e) {
            }
            System.out.println("Sorry, invalid input.");
        }
    }

}
