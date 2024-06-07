package racinggame;

import java.util.ArrayList;
import java.util.List;

public class GameRecord {
    List<PlayerRecord> playerRecordList;

    public GameRecord() {
        playerRecordList = new ArrayList<>();
    }

    /**
     * Add one player record
     *
     * @param playerRecord
     */
    public void addRecord(PlayerRecord playerRecord) {
        playerRecordList.add(playerRecord);
    }

    /**
     * 获取topN个最高得分
     *
     * @param top topN
     * @return List<Record>
     */
    public List<PlayerRecord> getHighestRecords(int top) {
        sort(); // 按得分排序
        if (top <= 0) {
            return new ArrayList<>();
        } else if (playerRecordList.size() < top) {
            return new ArrayList<>(playerRecordList);
        } else {
            return new ArrayList<>(playerRecordList.subList(0, top));
        }
    }

    /**
     * 冒泡排序，按分数从大到小排序
     */
    private void sort() {
        for (int i = 0; i < playerRecordList.size() - 1; i++) {
            for (int j = 0; j < playerRecordList.size() - 1 - i; j++) {
                if (playerRecordList.get(j).score < playerRecordList.get(j + 1).score) {
                    PlayerRecord record = playerRecordList.get(j);
                    playerRecordList.set(j, playerRecordList.get(j + 1));
                    playerRecordList.set(j + 1, record);
                }
            }
        }
    }
}
