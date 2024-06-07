package coordinateComparisonModule;

import autopilotModule.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Strategy Pattern
 */
public class TwoThreeVoting implements ICompareCoordsStrategy {
    private static final Random RANDOM = new Random();

    @Override
    public Coordinates compareCoords(Coordinates gps1, Coordinates gps2, Coordinates gps3) {
        // NOTE: if there is no agreement between at least 2/3 receivers, then return null
        List<Coordinates> list = null;
        if (gps1.equals(gps2) && gps1.equals(gps3)) {
            System.out.println("------- AGREEMENT WITH GPS1 GPS2 AND GPS3");
            list = Arrays.asList(gps1, gps2, gps3);
        } else if (gps1.equals(gps2)) {
            System.out.println("------- AGREEMENT WITH GPS1 AND GPS2");
            list = Arrays.asList(gps1, gps2);
        } else if (gps1.equals(gps3)) {
            System.out.println("------- AGREEMENT WITH GPS1 AND GPS3");
            list = Arrays.asList(gps1, gps3);
        } else if (gps2.equals(gps3)) {
            System.out.println("------- AGREEMENT WITH GPS2 AND GPS3");
            list = Arrays.asList(gps2, gps3);
        }
        if (list == null) {
            return null;
        }
        return list.get(RANDOM.nextInt(list.size()));
    }

}
