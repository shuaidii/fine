package meta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
    public static final int INIT_AMOUNT = 2000;

    private int remainAmount;
    private List<BuildingSquare> buildingSquareList;
    private Square currentSquare;
    private String name;
    private Color color;

    public Player(String name) {
        this.name = name;
        this.remainAmount = INIT_AMOUNT;
        this.buildingSquareList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name + "($" + remainAmount + ")";
    }

    public int getBuildingCount(BuildingType buildingType) {
        int count = 0;
        for (BuildingSquare buildingSquare : buildingSquareList) {
            if (buildingSquare.getBuildingType().equals(buildingType)) {
                count++;
            }
        }
        return count;
    }

    /**
     * increase money
     * @param amount amount
     */
    public void incrAmount(int amount) {
        remainAmount += amount;
    }

    /**
     * decrease money
     * @param amount amount
     */
    public void decrAmount(int amount) {
        this.remainAmount -= amount;
    }

    /**
     * get remain money
     *
     * @return remainAmount
     */
    public int getRemainAmount() {
        return remainAmount;
    }

    /**
     * set current stand on square
     *
     * @param currentSquare currentSquare
     */
    public void setCurrentSquare(Square currentSquare) {
        this.currentSquare = currentSquare;
    }

    /**
     * get current stand on square
     * @return currentSquare
     */
    public Square getCurrentSquare() {
        return currentSquare;
    }

    /**
     * get player name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * buy one building
     *
     * @param buildingSquare
     */
    public void buyBuilding(BuildingSquare buildingSquare) {
        if (buildingSquare.getOwner() != null || buildingSquare.getSellPrice() > remainAmount) {
            throw new IllegalArgumentException();
        }
        this.remainAmount -= buildingSquare.getSellPrice();
        buildingSquare.setOwner(this);
        buildingSquareList.add(buildingSquare);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
