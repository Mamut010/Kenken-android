package au.edu.jcu.cp5307.proj2.application;

import static org.junit.Assert.*;

import org.junit.Test;

public class StopwatchTest {

    @Test
    public void testStopwatch_should_success() {
        Stopwatch stopwatch = new Stopwatch();

        long initialMillis = stopwatch.getTimeInMillis();
        String initialTimeString = stopwatch.getTimeString();

        assertEquals(0, initialMillis);
        assertEquals("00:00:00", initialTimeString);

        long sec = 2 * 3600 + 25 * 60 + 30;
        for (int i = 0; i < sec; i++) {
            stopwatch.tick(1000);
        }

        long currentMillis = stopwatch.getTimeInMillis();
        String currentTimeString = stopwatch.getTimeString();

        assertEquals(sec * 1000, currentMillis);
        assertEquals("02:25:30", currentTimeString);

        stopwatch.reset();

        currentMillis = stopwatch.getTimeInMillis();
        currentTimeString = stopwatch.getTimeString();

        assertEquals(initialMillis, currentMillis);
        assertEquals(initialTimeString, currentTimeString);
    }
}