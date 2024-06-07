package comp1110.ass1.caterpillar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public class RotateTest {

    Caterpillar caterpillar = Caterpillar.newGreen();


    @Test
    void caseRight() {
        caterpillar.headDirection = Direction.RIGHT;
        caterpillar.rotate();
        Assertions.assertEquals(Direction.UP,caterpillar.headDirection);
    }

    @Test
    void caseLeft() {
        caterpillar.headDirection = Direction.LEFT;
        caterpillar.rotate();
        Assertions.assertEquals(Direction.DOWN,caterpillar.headDirection);
    }

    @Test
    void caseUp() {
        caterpillar.headDirection = Direction.UP;
        caterpillar.rotate();
        Assertions.assertEquals(Direction.LEFT,caterpillar.headDirection);
    }

    @Test
    void caseDown() {
        caterpillar.headDirection = Direction.DOWN;
        caterpillar.rotate();
        Assertions.assertEquals(Direction.RIGHT,caterpillar.headDirection);
    }



}
