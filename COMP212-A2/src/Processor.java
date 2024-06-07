import java.util.ArrayList;
import java.util.Random;

enum Status {UNKNOWN, LEADER}

public class Processor {
    //Processor data
    private int myID;
    private int sendID;
    private int phase = 0;
    private Processor previousProcessor;
    private Processor nextProcessor;
    private Message preMessage;
    private Message nextMessage;
    private Status myStatus;


    //HS part of data
    private Message sendClock;
    private Message sendCounterClock;

    public Processor() {
    }

    public Processor(int ID) {
        myID = ID;
        myStatus = Status.UNKNOWN;
        previousProcessor = null;
        nextProcessor = null;
        //LCR & HS incoming Message stored here
        preMessage = new Message();
        nextMessage = new Message();
        //LCR Message to be send stored here
        sendID = myID;
        //HS Message to be SEND stored here
        sendClock = new Message(myID, Direct.OUT, 1);
        sendCounterClock = new Message(myID, Direct.OUT, 1);
    }

    //Processor method
    //set data
    public void setMyID(int ID) {
        myID = ID;
    }

    public void setSendID(int ID) {
        sendID = ID;
    }

    public void setMyStatus(Status status) {
        myStatus = status;
    }

    public void setPreviousProcessor(Processor pre) {
        previousProcessor = pre;
    }

    public void setNextProcessor(Processor nxt) {
        nextProcessor = nxt;
    }

    public void setPreMessage(int ID) {
        preMessage.setID(ID);
    }

    public void setNxtMessage(int ID) {
        nextMessage.setID(ID);
    }

    //get data
    public int getMyID() {
        return myID;
    }

    public int getSendID() {
        return sendID;
    }

    public Status getMyStatus() {
        return myStatus;
    }

    public Message getPreMessage() {
        return preMessage;
    }

    public Message getNxtMessage() {
        return nextMessage;
    }

    public void incPhase() {
        phase++;
    }

    public int getPhase() {
        return phase;
    }

    public void sendLCRMessage() {
        if (previousProcessor.sendClock != null) {
            preMessage = previousProcessor.sendClock;
            previousProcessor.sendClock = null;
            Message.incNumOfMsg();
        }
        //nextProcessor.setPreMessage(getSndID());
        if (preMessage != null)
            System.out.format("%-10s%-1s%-10s%-1s%-10s\n", myID, "|", preMessage.getID(), "|", myStatus);
        else
            System.out.format("%-10s%-1s%-10s%-1s%-10s\n", myID, "|", null, "|", myStatus);
    }

    public void exeLCRProcess() {
        if (Message.getRound() > 1) {
            if (preMessage != null) {
                if (preMessage.getID() == myID) {
                    setMyStatus(Status.LEADER);
                    preMessage = null;
                } else if (preMessage.getID() > myID) {
                    sendClock = preMessage;
                    preMessage = null;
                } else
                    preMessage = null;
            }
        }
        if (sendClock != null)
            System.out.format("%-10s%-1s%-10s%-1s%-10s\n", myID, "|", sendClock.getID(), "|", myStatus);
        else
            System.out.format("%-10s%-1s%-10s%-1s%-10s\n", myID, "|", null, "|", myStatus);
    }

    public void sendHSMessage() {
        if (previousProcessor.sendClock != null) {
            this.preMessage = previousProcessor.sendClock;
            previousProcessor.sendClock = null;
            Message.incNumOfMsg();
        }
        if (nextProcessor.sendCounterClock != null) {
            this.nextMessage = nextProcessor.sendCounterClock;
            nextProcessor.sendCounterClock = null;
            Message.incNumOfMsg();
        }
        //send sendClock to clockwise neighbour
        System.out.format("%-10s%-1s", myID, "|");
        if (preMessage != null)
            System.out.format("%-10s%-10s%-10s%-1s", preMessage.getID(), preMessage.getDirection(), preMessage.getHighCount(), "|");
        else
            System.out.format("%-10s%25s", "null", "|");

        if (nextMessage == null)
            System.out.format("%-10s%25s", "null", "|");
        else
            System.out.format("%-10s%-10s%-10s%-1s", nextMessage.getID(), nextMessage.getDirection(), nextMessage.getHighCount(), "|");
        System.out.format("%-10s%-1s%-12s\n", phase, "|", myStatus);
    }

    public void exeHSProcess() {
        //direct == OUT process Message from counterClockwise neighbour
        if (preMessage != null) {
            if (preMessage.getDirection() == Direct.OUT) {

                if (preMessage.getID() > myID && preMessage.getHighCount() > 1) {
                    sendClock = preMessage;
                    sendClock.setHighCount(preMessage.getHighCount() - 1);
                    preMessage = null;

                } else if (preMessage.getID() > myID && preMessage.getHighCount() == 1) {

                    sendCounterClock = preMessage;
                    sendCounterClock.setDirection(Direct.IN);
                    preMessage = null;

                } else if (preMessage.getID() == myID)

                    setMyStatus(Status.LEADER);

            }
        }
        //dir == OUT, process Message from clockwise neighbour
        if (nextMessage != null) {
            if (nextMessage.getDirection() == Direct.OUT) {
                if (nextMessage.getID() > myID && nextMessage.getHighCount() > 1) {
                    sendCounterClock = nextMessage;
                    sendCounterClock.setHighCount(nextMessage.getHighCount() - 1);
                    nextMessage = null;
                } else if (nextMessage.getID() > myID && nextMessage.getHighCount() == 1) {
                    sendClock = nextMessage;
                    sendClock.setDirection(Direct.IN);
                    nextMessage = null;

                } else if (nextMessage.getID() == myID)
                    setMyStatus(Status.LEADER);
            }
        }
        //different id
        if (preMessage != null && preMessage.getDirection() == Direct.IN && preMessage.getHighCount() == 1 && preMessage.getID() != myID) {
            sendClock = preMessage;
            preMessage = null;
        }

        if (nextMessage != null && nextMessage.getDirection() == Direct.IN && nextMessage.getHighCount() == 1 && nextMessage.getID() != myID) {
            sendCounterClock = nextMessage;
            nextMessage = null;
        }

        //IN with both inID the same
        if (preMessage != null && nextMessage != null
                && preMessage.getDirection() == Direct.IN && nextMessage.getDirection() == Direct.IN
                && preMessage.getHighCount() == 1 && nextMessage.getHighCount() == 1
                && preMessage.getID() == myID && nextMessage.getID() == myID) {
            incPhase();
            sendClock = nextMessage;
            sendClock.setDirection(Direct.OUT);
            sendClock.setHighCount((int) Math.pow(2, getPhase()));
            sendCounterClock = preMessage;
            sendCounterClock.setDirection(Direct.OUT);
            sendCounterClock.setHighCount((int) Math.pow(2, getPhase()));
            preMessage = null;
            nextMessage = null;
        }

        preMessage = null;
        nextMessage = null;
        System.out.format("%-10s%-1s", myID, "|");
        if (sendCounterClock == null)
            System.out.format("%-10s%25s", "null", "|");
        else
            System.out.format("%-10s%-10s%-10s%-1s", sendCounterClock.getID(), sendCounterClock.getDirection(), sendCounterClock.getHighCount(), "|");
        if (sendClock != null)
            System.out.format("%-10s%-10s%-10s%-1s", sendClock.getID(), sendClock.getDirection(), sendClock.getHighCount(), "|");
        else
            System.out.format("%-10s%25s", null, "|");

        System.out.format("%-10s%-1s%-12s\n", phase, "|", myStatus);
    }

    public static ArrayList<Processor> ringConstructor(int numOfProcessors) {
        ArrayList<Processor> processorList = new ArrayList<Processor>();
        ArrayList<Integer> IDHolderList = new ArrayList<Integer>();
        Random r = new Random();
        //construct an arrayList of Processors
        for (int i = 0; i < numOfProcessors; i++) {
            boolean uniqueIDFlag = true;
            int n;
            do {
                uniqueIDFlag = true;
                n = r.nextInt(3 * numOfProcessors) + 1;
                for (int counter = 0; counter < processorList.size(); counter++) {
                    if (processorList.get(counter).getMyID() == n)
                        uniqueIDFlag = false;
                }
            } while (!uniqueIDFlag);
            Processor p = new Processor(n);
            processorList.add(p);
        }

        System.out.println("----------------------------");
        //construct a bidirectional ring of Processors
        for (int counter = 0; counter < processorList.size(); counter++) {
            if (counter < processorList.size() - 1)
                processorList.get(counter).setNextProcessor(processorList.get(counter + 1));
            else
                processorList.get(counter).setNextProcessor(processorList.get(0));
            if (counter > 0)
                processorList.get(counter).setPreviousProcessor(processorList.get(counter - 1));
            else
                processorList.get(counter).setPreviousProcessor(processorList.get(processorList.size() - 1));
        }
        return processorList;
    }

}

