package meta;

public class BuildingSquare extends Square {
    public static final int MAX_LEVEL = 5;

    private BuildingType buildingType;
    private int buildingNumber;
    private Player owner;
    private int starRating = 0;

    public BuildingSquare(int index, BuildingType buildingType, int buildingNumber) {
        super(index);
        this.buildingType = buildingType;
        this.buildingNumber = buildingNumber;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public String getName() {
        return "" + buildingType + buildingNumber;
    }

    @Override
    public String toString() {
        String message = index + "-" + buildingType + buildingNumber + "(" + starRating + " Star)";
        if (owner != null) {
            message += " Owned by player " + owner.getName() + " with overnight fee $" + getOvernightFee();
        } else {
            message += " with sell price $" + getSellPrice();
        }
        return message;
    }


    @Override
    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * overnight fee
     *
     * @return
     */
    public int getOvernightFee() {
        if (owner == null) {
            throw new IllegalArgumentException();
        }
        int base = (int) (getSellPrice() * 0.1 * starRating);
        if (owner.getBuildingCount(buildingType) == 3) {
            base *= 2;
        }
        return base;
    }

    public boolean canIncrStarRating() {
        if (starRating >= MAX_LEVEL || owner == null) {
            return false;
        }
        return owner.getRemainAmount() >= incrStarRatingCost();
    }

    public void incrStarRating() {
        if (!canIncrStarRating()) {
            throw new IllegalArgumentException();
        }
        starRating++;
        owner.decrAmount(incrStarRatingCost());
    }

    public int incrStarRatingCost() {
        return getSellPrice() / 2;
    }

    /**
     * get buy price
     */
    public int getSellPrice() {
        int base = buildingType.getIndex() * 50;
        if (buildingNumber == 3) {
            base += 20;
        }
        return base;
    }

    public boolean isMaxStarRating() {
        return starRating == MAX_LEVEL;
    }

    public int getStarRating() {
        return starRating;
    }
}
