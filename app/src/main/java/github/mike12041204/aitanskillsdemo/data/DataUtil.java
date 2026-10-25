package github.mike12041204.aitanskillsdemo.data;

import java.util.Random;

// Small helper functions for the data layer, never created as an object
/* package */ final class DataUtil {

    private static final Random RANDOM = new Random();

    // Private so nobody can create a DataUtil
    private DataUtil() {
    }

    // Random number between min and max
    /* package */ static float randomFloat(float min, float max) {
        return min + RANDOM.nextFloat() * (max - min);
    }

    // Keeps value between min and max
    /* package */ static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    // Straight line distance between two points in cm
    /* package */ static float distanceCm(PointCm from, PointCm to) {
        return (float) Math.hypot(to.getXCm() - from.getXCm(), to.getYCm() - from.getYCm());
    }

    // Point part of the way from one point to another, fraction 0 is from and 1 is to
    /* package */ static PointCm pointBetween(PointCm from, PointCm to, float fraction) {
        return new PointCm(
                from.getXCm() + (to.getXCm() - from.getXCm()) * fraction,
                from.getYCm() + (to.getYCm() - from.getYCm()) * fraction);
    }
}
