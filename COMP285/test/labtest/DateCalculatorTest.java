package labtest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DateCalculatorTest {

    private DateCalculator calculator;

    @Before
    public void setUp() throws Exception {
        calculator = new DateCalculator();
        int day = 19;
        int month = 4;
        int year = 2023;
        calculator.setCurrentDate(day, month, year);
    }

    @After
    public void tearDown() throws Exception {
        calculator = null;
    }

    @Test
    public void test() {
        boolean ok = true;
        try {
            // Bad date, year negative
            calculator.calculateDaysTill(20, 4, -1);
        } catch (BadDateException exc) {
            ok = false;
        }
        assertFalse("Check is year negative rejected", ok);
        assertEquals("Check one week from today", 7, calculator.calculateDaysTill(26, 4, 2023));
        assertEquals("Check one year from today", 366, calculator.calculateDaysTill(19, 4, 2024));

        // Add in more assertions to complete testing
    }

}
