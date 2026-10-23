package com.example.aitanskillsdemo.data;

public class Target extends Entity {

    private static final long UNSELECTED_COLOR = 0xFFFFCC80L; // light orange
    private static final long SELECTED_COLOR = 0xFFFB8C00L;   // orange

    public Target(float xPosition, float yPosition) {
        super(xPosition, yPosition, UNSELECTED_COLOR, SELECTED_COLOR);
    }
}