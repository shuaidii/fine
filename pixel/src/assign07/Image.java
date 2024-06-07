package assign07;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class represents an image as a two-dimensional array of pixels and provides a number
 * of image filters (via instance methods) for changing the appearance of the image.
 * Application of multiple filters is cumulative; e.g., obj.redBlueSwapFilter() followed by
 * obj.rotateClockwiseFilter() results in an image altered both in color and orientation.
 * <p>
 * Note:
 * - The pixel in the northwest corner of the image is stored in the first row, first column.
 * - The pixel in the northeast corner of the image is stored in the first row, last column.
 * - The pixel in the southeast corner of the image is stored in the last row, last column.
 * - The pixel in the southwest corner of the image is stored in the last row, first column.
 *
 * @author Prof. Martin and ?
 * @version ?
 */
public class Image {

    private Pixel[][] imageArray;

    /**
     * Creates a new Image object by reading the image file with the given filename.
     * <p>
     * DO NOT MODIFY THIS METHOD
     *
     * @param filename - name of the given image file to read
     * @throws IOException if file does not exist or cannot be read
     */
    public Image(String filename) {
        BufferedImage imageInput = null;
        try {
            imageInput = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.out.println("Image file " + filename + " does not exist or cannot be read.");
        }

        imageArray = new Pixel[imageInput.getHeight()][imageInput.getWidth()];
        for (int i = 0; i < imageArray.length; i++)
            for (int j = 0; j < imageArray[0].length; j++) {
                int rgb = imageInput.getRGB(j, i);
                imageArray[i][j] = new Pixel((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
            }
    }

    /**
     * Create an Image object directly from a pre-made Pixel array.
     * This is primarily to be used in testing.
     * <p>
     * DO NOT MODIFY THIS METHOD
     */
    public Image(Pixel[][] imageArray) {
        this.imageArray = imageArray;
    }

    /**
     * Create a new "default" Image object, whose purpose is to be used in testing.
     * <p>
     * The orientation of this image:
     * cyan 	 red
     * green	 magenta
     * yellow	 blue
     * <p>
     * DO NOT MODIFY THIS METHOD
     */
    public Image() {
        imageArray = new Pixel[3][2];
        imageArray[0][0] = new Pixel(0, 255, 255);  // cyan
        imageArray[0][1] = new Pixel(255, 0, 0);  // red
        imageArray[1][0] = new Pixel(0, 255, 0);  // green
        imageArray[1][1] = new Pixel(255, 0, 255);  // magenta
        imageArray[2][0] = new Pixel(255, 255, 0);  // yellow
        imageArray[2][1] = new Pixel(0, 0, 255);  // blue
    }

    /**
     * Gets the pixel at the specified row and column indexes.
     * <p>
     * DO NOT MODIFY THIS METHOD
     *
     * @param rowIndex    - given row index
     * @param columnIndex - given column index
     * @return the pixel at the given row index and column index
     * @throws IndexOutOfBoundsException if row or column index is out of bounds
     */
    public Pixel getPixel(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= imageArray.length)
            throw new IndexOutOfBoundsException("rowIndex must be in range 0-" + (imageArray.length - 1));

        if (columnIndex < 0 || columnIndex >= imageArray[0].length)
            throw new IndexOutOfBoundsException("columnIndex must be in range 0-" + (imageArray[0].length - 1));

        return imageArray[rowIndex][columnIndex];
    }

    /**
     * Writes the image represented by this object to file.
     * Does nothing if the image length is 0.
     * <p>
     * DO NOT MODIFY THIS METHOD
     *
     * @param filename - name of image file to write
     * @throws IOException if file does cannot be written
     */
    public void writeImage(String filename) {
        if (imageArray.length > 0) {
            BufferedImage imageOutput = new BufferedImage(imageArray[0].length,
                    imageArray.length, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < imageArray.length; i++)
                for (int j = 0; j < imageArray[0].length; j++)
                    imageOutput.setRGB(j, i, imageArray[i][j].getPackedRGB());

            try {
                ImageIO.write(imageOutput, "png", new File(filename));
            } catch (IOException e) {
                System.out.println("The image cannot be written to file " + filename);
            }
        }
    }

    /**
     * Applies a filter to the image represented by this object such that for each
     * pixel the red amount and blue amount are swapped.
     * <p>
     * HINT: Since the Pixel class does not include setter methods for its private
     * instance variables, create new Pixel objects with the altered colors.
     */
    public void redBlueSwapFilter() {
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                Pixel pixel = imageArray[i][j];
                Pixel tmp = new Pixel(pixel.getBlueAmount(), pixel.getGreenAmount(), pixel.getRedAmount());
                imageArray[i][j] = tmp;
            }
        }
    }


    /**
     * Applies a filter to the image represented by this object such that the color
     * of each pixel is converted to its corresponding grayscale shade, producing the
     * effect of a black and white photo. The filter sets the amount of red, green,
     * and blue all to the value of this average:
     * (originalRed + originalGreen + originalBlue) / 3
     * <p>
     * HINT: Since the Pixel class does not include setter methods for its private
     * instance variables, create new Pixel objects with the altered colors.
     */
    public void blackAndWhiteFilter() {
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                Pixel pixel = imageArray[i][j];
                int red = pixel.getRedAmount();
                int green = pixel.getGreenAmount();
                int blue = pixel.getBlueAmount();
                int average = (red + green + blue) / 3;
                Pixel tmp = new Pixel(average, average, average);
                imageArray[i][j] = tmp;
            }
        }

    }

    /**
     * Applies a filter to the image represented by this object such that it is rotated
     * clockwise (by 90 degrees). This filter rotates directly clockwise, it should
     * not do this by rotating counterclockwise 3 times.
     * <p>
     * HINT: If the image is not square, this filter requires creating a new array with
     * different lengths. Use the technique of creating and reassigning a new backing array
     * from BetterDynamicArray (assign06) as a guide for how to make a second array and
     * eventually reset the imageArray reference to this new array.
     * Note that we learned how to rotate a square 2D array *left* in Class Meeting 11.
     */
    public void rotateClockwiseFilter() {
        Pixel[][] pixels = new Pixel[imageArray[0].length][imageArray.length];
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                pixels[j][imageArray.length - 1 - i] = imageArray[i][j];
            }
        }
        imageArray = pixels;
    }

    /**
     * Applying a filter that resize the image width and high at half of its before.
     */
    public void customFilter() {
        Pixel[][] pixels = new Pixel[imageArray.length / 2][imageArray[0].length / 2];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                pixels[i][j] = imageArray[i * 2][j * 2];
            }
        }
        imageArray = pixels;
    }
}
