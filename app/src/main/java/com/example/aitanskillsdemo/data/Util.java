package com.example.aitanskillsdemo.data;

import java.util.Random;

public class Util {
    private static final Random RANDOM = new Random();

    public static float randomFloat(float min, float max){
        return min + RANDOM.nextFloat() * (max - min);
    }
}
