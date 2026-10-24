package com.example.aitanskillsdemo.data;

public class Drone extends Entity {

    private static final EntityShape SHAPE = EntityShape.CIRCLE;
    // Size of entity in cm as it will be drawn on screen
    private static final float SIZE = 0.25f;
    private static final long UNSELECTED_COLOR = 0xFF90CAF9L; // light blue
    private static final long SELECTED_COLOR = 0xFF1E88E5L;   // blue

    public Drone(float xPosition, float yPosition) {
        super(xPosition, yPosition, UNSELECTED_COLOR, SELECTED_COLOR, SHAPE, SIZE);
    }
}