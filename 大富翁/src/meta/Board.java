package meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Board {
    private Map<Integer, Square> squareIndexMap;

    public Board() {
        initSquares();
    }

    public StartSquare getStartSquare() {
        return (StartSquare) squareIndexMap.get(0);
    }

    public int getSquareCount() {
        return squareIndexMap.size();
    }

    public Square getSquare(int index) {
        if (index < 0 || index >= getSquareCount()) {
            throw new IllegalArgumentException();
        }
        return squareIndexMap.get(index);
    }

    /**
     * init Square
     */
    private void initSquares() {
        List<Square> squareList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            BuildingType buildingType = BuildingType.values()[2 * i];
            squareList.add(new EmptySquare(10 * i));
            squareList.add(new BuildingSquare(10 * i + 1, buildingType, 1));
            squareList.add(new EmptySquare(10 * i + 2));
            squareList.add(new BuildingSquare(10 * i + 3, buildingType, 2));
            squareList.add(new BuildingSquare(10 * i + 4, buildingType, 3));

            buildingType = BuildingType.values()[2 * i + 1];
            squareList.add(new EmptySquare(10 * i + 5));
            squareList.add(new BuildingSquare(10 * i + 6, buildingType, 1));
            squareList.add(new EmptySquare(10 * i + 7));
            squareList.add(new BuildingSquare(10 * i + 8, buildingType, 2));
            squareList.add(new BuildingSquare(10 * i + 9, buildingType, 3));
        }
        squareList.set(0, new StartSquare(0));

        squareIndexMap = new HashMap<>();
        for (Square square : squareList) {
            squareIndexMap.put(square.getIndex(), square);
        }
    }

    public void display(List<Player> playerList) {
        for (int i = 0; i < 40; i++) {
            Square square = squareIndexMap.get(i);
            String standPlayers = "";
            for (Player player : playerList) {
                if (player.getCurrentSquare().equals(square)) {
                    standPlayers += player + ",";
                }
            }
            if (standPlayers.length() > 0) {
                standPlayers = " stand Player " + standPlayers.substring(0, standPlayers.length() - 1);
            }
            System.out.println(i + ": " + square.toString() + standPlayers);
        }
    }
}
