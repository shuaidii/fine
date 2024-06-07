import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.*;

public class LCR extends DistributedAlgorithm {

    public int intParser() {
        // Use try function to prevent invalid
        try {
            int number = sc.nextInt();
            if (number < 10000 && number > 0)
                return number;
            else {
                System.out.println("Sorry, invalid input.");
                return 0;
            }
        } catch (Exception e) {
            System.out.println("Sorry, invalid input.");
            System.exit(1);
        }
        return 0;
    }

    public void execute(Server.ServerSocketService serverSocketService) throws IOException, ClassNotFoundException {
        int leaderID = 0;
        int leaderRound = 0;
        int leaderMessageSent = 0;
        //request num of processors
//        int numOfProcessors = inputProcessorNumber();
        int numOfProcessors =  serverSocketService.numOfProcessors();

        //construct a bidirectional ring ADT
//        ArrayList<Processor> processorList = Processor.ringConstructor(numOfProcessors);
        //LCR leader election
        boolean leaderFlag = false;
        while (!leaderFlag) {
            for (int index = 0; index < numOfProcessors; index++) {
//                Processor processor = processorList.get(index);
                Map<String, Object> map = serverSocketService.exeLCRProcess(index);
                if (map.get("myStatus") == Status.LEADER) {
                    leaderFlag = true;
                    leaderID = (int) map.get("myID");
                    leaderRound = (int) map.get("round");
                    leaderMessageSent = (int) map.get("numOfMessageSent");
                }
            }
            serverSocketService.sendLCRMessage();
            serverSocketService.incRound();
        }
        String result = "The leader ID is: " + leaderID + " Elected in round: " + leaderRound + " With number of sent message: " + leaderMessageSent;
        serverSocketService.sendMessage(result);
        System.out.println(result);
    }
}
