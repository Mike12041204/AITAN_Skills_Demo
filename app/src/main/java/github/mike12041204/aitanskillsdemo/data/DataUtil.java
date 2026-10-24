package github.mike12041204.aitanskillsdemo.data;

import java.util.Random;

public final class DataUtil {

    private static final Random RANDOM = new Random();

    private DataUtil() {
    }

    public static float randomFloat(float min, float max) {
        return min + RANDOM.nextFloat() * (max - min);
    }
}
