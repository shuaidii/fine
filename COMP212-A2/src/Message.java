enum Direct { OUT, IN}
public class Message {
    //ounds & highCount
    static private int round = 1;
    static private int numOfMessageSent = 0;

    static public void incRound() {
        round++;
    }

    static public int getRound() {
        return round;
    }

    static public void incNumOfMsg() {
        numOfMessageSent++;
    }

    static public int getNumOfMsgSent() {
        return numOfMessageSent;
    }


    //Message method
    public void setID(int ID) {
        sendID = ID;
    }

    public int getID() {
        return sendID;
    }

    public void setDirection(Direct dir) {
        direction = dir;
    }

    public Direct getDirection() {
        return direction;
    }

    public void setHighCount(int hC) {
        highCount = hC;
    }

    public int getHighCount() {
        return highCount;
    }

    //Message data
    private int sendID;
    private Direct direction = Direct.OUT; //data initialized for sendClock/sendCounterclock
    private int highCount = 1;



    //constructor
    public Message() {}

    public Message(int ID, Direct dir, int hCount) {
        highCount = hCount;
        direction = dir;
        sendID = ID;
    }
}
