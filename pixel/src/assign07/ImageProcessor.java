package assign07;


import java.util.Scanner;

public class ImageProcessor {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java assign07.ImageProcessor fox.png fox_redBlueSwap.png");
            return;
        }
        Image image = new Image(args[0]);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            showMean();
            String input = scanner.nextLine();
            if (input.equals("1")) {
                image.redBlueSwapFilter();
                System.out.println("... applying red and blue swap image filter");
            } else if (input.equals("2")) {
                image.blackAndWhiteFilter();
                System.out.println("... applying black and white image filter");
            } else if (input.equals("3")) {
                image.rotateClockwiseFilter();
                System.out.println("... applying rotate clockwise image filter");
            } else if (input.equals("4")) {
                image.customFilter();
                System.out.println("... applying half width and high image filter");
            } else if (input.equals("5")) {
                image.writeImage(args[1]);
                System.out.println("Filtered image written to " + args[1]);
                return;
            } else {
                System.out.println("You must enter an integer 1 to 5. Try again.");
            }
            System.out.println();
        }
    }

    /**
     * show mean
     */
    public static void showMean() {
        System.out.println("Select an option:");
        System.out.println("  1 -- Swap red and blue");
        System.out.println("  2 -- Convert to black and white");
        System.out.println("  3 -- Rotate clockwise");
        System.out.println("  4 -- Resize to half of width and high)");
        System.out.println("  5 -- Write image to file and quit");
    }
}
