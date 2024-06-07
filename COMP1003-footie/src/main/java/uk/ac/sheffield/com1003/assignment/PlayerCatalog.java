package uk.ac.sheffield.com1003.assignment;

import uk.ac.sheffield.com1003.assignment.codeprovided.*;

import java.util.*;

/**
 * SKELETON IMPLEMENTATION
 */
public class PlayerCatalog extends AbstractPlayerCatalog {
    /**
     * Constructor
     */
    public PlayerCatalog(String eplFilename, String ligaFilename) {
        super(eplFilename, ligaFilename);
    }

    @Override
    public PlayerPropertyMap parsePlayerEntryLine(String line) throws IllegalArgumentException {
        String[] properties = line.split(",");
        // 假设属性数量为 29
        if (properties.length != 29) {
            throw new IllegalArgumentException("Invalid player entry line: " + line);
        }
        PlayerPropertyMap playerPropertyMap = new PlayerPropertyMap();
        playerPropertyMap.putDetail(PlayerDetail.PLAYER, properties[0]);
        playerPropertyMap.putDetail(PlayerDetail.NATION, properties[1]);
        playerPropertyMap.putDetail(PlayerDetail.POSITION, properties[2]);
        playerPropertyMap.putDetail(PlayerDetail.TEAM, properties[3]);
        playerPropertyMap.put(PlayerProperty.AGE, Double.parseDouble(properties[4]));
        playerPropertyMap.put(PlayerProperty.MATCHES, Double.parseDouble(properties[5]));
        playerPropertyMap.put(PlayerProperty.MINUTES, Double.parseDouble(properties[6]));
        playerPropertyMap.put(PlayerProperty.YELLOWCARDS, Double.parseDouble(properties[7]));
        playerPropertyMap.put(PlayerProperty.REDCARDS, Double.parseDouble(properties[8]));
        playerPropertyMap.put(PlayerProperty.GOALS, Double.parseDouble(properties[9]));
        playerPropertyMap.put(PlayerProperty.PKGOALS, Double.parseDouble(properties[10]));
        playerPropertyMap.put(PlayerProperty.PKATTEMPTS, Double.parseDouble(properties[11]));
        playerPropertyMap.put(PlayerProperty.ASSISTS, Double.parseDouble(properties[12]));
        playerPropertyMap.put(PlayerProperty.OWNGOALS, Double.parseDouble(properties[13]));
        playerPropertyMap.put(PlayerProperty.PASSATTEMPTED, Double.parseDouble(properties[14]));
        playerPropertyMap.put(PlayerProperty.PASSCOMPLETED, Double.parseDouble(properties[15]));
        playerPropertyMap.put(PlayerProperty.AERIALSWON, Double.parseDouble(properties[16]));
        playerPropertyMap.put(PlayerProperty.AERIALSLOST, Double.parseDouble(properties[17]));
        playerPropertyMap.put(PlayerProperty.AERIALSWONPERC, Double.parseDouble(properties[18]));
        playerPropertyMap.put(PlayerProperty.TACKLES, Double.parseDouble(properties[19]));
        playerPropertyMap.put(PlayerProperty.TACKLESWON, Double.parseDouble(properties[20]));
        playerPropertyMap.put(PlayerProperty.CLEARANCES, Double.parseDouble(properties[21]));
        playerPropertyMap.put(PlayerProperty.FOULSCOMMITTED, Double.parseDouble(properties[22]));
        playerPropertyMap.put(PlayerProperty.PKCONCEDED, Double.parseDouble(properties[23]));
        playerPropertyMap.put(PlayerProperty.SHOTS, Double.parseDouble(properties[24]));
        playerPropertyMap.put(PlayerProperty.SHOTSTARGET, Double.parseDouble(properties[25]));
        playerPropertyMap.put(PlayerProperty.FOULSDRAWN, Double.parseDouble(properties[26]));
        playerPropertyMap.put(PlayerProperty.CROSSES, Double.parseDouble(properties[27]));
        playerPropertyMap.put(PlayerProperty.PKWON, Double.parseDouble(properties[28]));
        return playerPropertyMap;

    }

    @Override
    public void updatePlayerCatalog() {
        List<PlayerEntry> allPlayer = new ArrayList<>();
        // 遍历所有联赛，将球员添加到allPlayer列表中
        for (League league : playerEntriesMap.keySet()) {
            if (league.equals(League.ALL)) {
                continue;
            }
            List<PlayerEntry> leaguePlayer = playerEntriesMap.get(league);
            if (leaguePlayer != null) {
                allPlayer.addAll(leaguePlayer);
            }
        }

        // 将allPlayer列表中的球员分别放入对应联赛的playerEntriesMap中
        playerEntriesMap.put(League.ALL, allPlayer);
//        playerEntriesMap.put(League.EPL, new ArrayList<>(allPlayer));
//        playerEntriesMap.put(League.LIGA, new ArrayList<>(allPlayer));
    }


    @Override
    public double getMinimumValue(PlayerProperty playerProperty, List<PlayerEntry> playerEntryList)
            throws NoSuchElementException {

        return playerEntryList.stream()
                .mapToDouble(playerEntry -> playerEntry.getProperty(playerProperty))
                .min().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public double getMaximumValue(PlayerProperty playerProperty, List<PlayerEntry> playerEntryList)
            throws NoSuchElementException {
        return playerEntryList.stream()
                .mapToDouble(playerEntry -> playerEntry.getProperty(playerProperty))
                .max().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public double getMeanAverageValue(PlayerProperty playerProperty, List<PlayerEntry> playerEntryList)
            throws NoSuchElementException {
        return playerEntryList.stream()
                .mapToDouble(playerEntry -> playerEntry.getProperty(playerProperty))
                .average().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<PlayerEntry> getFirstFivePlayerEntries(League type) {
        List<PlayerEntry> list = playerEntriesMap.get(type);
        if (list == null) {
            return new ArrayList<>();
        }
        if (list.size() <= 5) {
            return list;
        }
        return list.subList(0, 5);
    }


}

