package racinggame;

import java.util.Date;

public class PlayerRecord{
    // player name
    String playerName;
    // game start time
    Date startTime;
    int score;
    int costTime;

    public PlayerRecord(String playerName) {
        this.playerName = playerName;
        this.startTime = new Date();
    }

    public PlayerRecord(String playerName, Date startTime, int score, int costTime) {
        this.playerName = playerName;
        this.startTime = startTime;
        this.score = score;
        this.costTime = costTime;
    }
}
