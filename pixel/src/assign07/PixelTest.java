package assign07;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * This class contains tests for the Pixel class.
 *
 * @author Prof. Martin and ?
 * @version ?
 */
public class PixelTest {

    @Test
    public void testGetRedAmount() {
        Pixel tan = new Pixel(210, 180, 140);
        assertEquals(210, tan.getRedAmount());
    }

    @Test
    public void testGetGreenAmount() {
        Pixel tan = new Pixel(210, 180, 140);
        assertEquals(180, tan.getGreenAmount());
    }

    @Test
    public void testGetBlueAmount() {
        Pixel tan = new Pixel(210, 180, 140);
        assertEquals(140, tan.getBlueAmount());
    }

    @Test
    public void testPixelFail() {
        try {
            Pixel tan = new Pixel(-1, 180, 140);
            fail("new Pixel with out-of-range parameter");
        }
        catch(IllegalArgumentException e) {
            // do nothing -- exception expected
        }
    }

    @Test
    public void testGetPackedRGBZero() {
        Pixel black = new Pixel(0, 0, 0);
        assertEquals(0, black.getPackedRGB());
    }

    @Test
    public void testGetPackedRGB() {
        Pixel black = new Pixel(160, 82, 45);
        assertEquals(10506797, black.getPackedRGB());
    }
}