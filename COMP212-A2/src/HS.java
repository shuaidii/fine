import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class HS extends DistributedAlgorithm {
    public int intParser() {
        try {
            int number = sc.nextInt();
            if (number < 10000 && number > 0)
                return number;
            else {
                System.out.println("Sorry, invalid input. ");
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
        int leaderPhase = 0;
        int leaderRound = 0;
        int leaderMessageSent = 0;

        //request num of processors
        int numOfProcessors =  serverSocketService.numOfProcessors();
        //LCR leader election
        boolean leaderFlag = false;
        do {
            int round = serverSocketService.getRound();
            serverSocketService.sendMessage("----------------------------After HSProcess in round " + round);
            System.out.println("----------------------------After HSProcess in round " + round);
//            System.out.format("%-10s%-1s%-10s%-10s%-10s%-1s%-10s%-10s%-10s%-1s%-10s%-1s%-10s\n", "Processor", "|", "toSendCLK ", "Direction", "HopCount ", "|", "toSendCCW", "Direction", "HopCount", "|", "ProcPhase", "|", "MyStatus");
            for (int index = 0; index < numOfProcessors; index++) {
//                processorList.get(index).exeHSProcess();
//                if (processorList.get(index).getMyStatus() == Status.LEADER) {
//                    leaderMessageSent = Message.getNumOfMsgSent();
//                    leaderPhase = processorList.get(index).getPhase();
//                    leaderID = processorList.get(index).getMyID();
//                    leaderRound = Message.getRound();
//                    leaderFlag = true;
//                }
                Map<String, Object> map = serverSocketService.exeHSProcess(index);
                if (map.get("myStatus") == Status.LEADER) {
                    leaderMessageSent = (int) map.get("numOfMessageSent");
                    leaderPhase = (int) map.get("phase");
                    leaderID = (int) map.get("myID");
                    leaderRound = (int) map.get("round");
                    leaderFlag = true;
                }
            }

            round = serverSocketService.getRound();
            System.out.println("----------------------------After message sent in round " + round);
//            System.out.format("%-10s%-1s%-10s%-10s%-10s%-1s%-10s%-10s%-10s%-1s%-10s%-1s%-12s\n", "Processor", "|", "receivedCLK", "Direction", "HopCount", "|", "ReceivedCCW", "Direction", "HopCount", "|", "ProcPhase", "|", "MyStatus");
//            for (int index = 0; index < processorList.size(); index++) {
//                processorList.get(index).sendHSMessage();
//            }
//            Message.incRound();
            serverSocketService.sendHSMessage();
            serverSocketService.incRound();
        } while (leaderFlag == false);
        String result = "The leader ID is: " + leaderID + " Elected in Phase: " + leaderPhase + " Round:" + leaderRound + " With number of sent message: " + leaderMessageSent;
        System.out.println(result);
        serverSocketService.sendMessage(result);
    }
}