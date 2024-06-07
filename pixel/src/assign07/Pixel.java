package assign07;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-10-26
 * Time: 22:44
 */
public class Pixel {
    private int redAmount;
    private int greenAmount;
    private int blueAmount;

    /**
     * construct
     *
     * @param redAmount   redAmount
     * @param greenAmount greenAmount
     * @param blueAmount  blueAmount
     */
    public Pixel(int redAmount, int greenAmount, int blueAmount) {
        if (redAmount < 0 || greenAmount < 0 || blueAmount < 0
                || redAmount > 255 || greenAmount > 255 || blueAmount > 255) {
            throw new IllegalArgumentException();
        }
        this.redAmount = redAmount;
        this.greenAmount = greenAmount;
        this.blueAmount = blueAmount;
    }

    /**
     * get redAmount
     *
     * @return redAmount
     */
    public int getRedAmount() {
        return redAmount;
    }

    /**
     * get greenAmount
     *
     * @return greenAmount
     */
    public int getGreenAmount() {
        return greenAmount;
    }

    /**
     * get blueAmount
     *
     * @return blueAmount
     */
    public int getBlueAmount() {
        return blueAmount;
    }

    /**
     * This method return the red, green, and blue amount of a Pixel object (each a one-byte integer value) packed into a four-byte int value
     *
     * @return four-byte int value
     */
    public int getPackedRGB() {
        String red = Integer.toBinaryString(redAmount);
        String green = Integer.toBinaryString(greenAmount);
        String blue = Integer.toBinaryString(blueAmount);
        while (green.length() < 8) {
            green = "0" + green;
        }
        while (blue.length() < 8) {
            blue = "0" + blue;
        }
        String bin = red + green + blue;
        return Integer.valueOf(bin, 2);
    }
}
