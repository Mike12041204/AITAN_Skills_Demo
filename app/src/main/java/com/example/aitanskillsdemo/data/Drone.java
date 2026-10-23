package com.example.aitanskillsdemo.data;

public class Drone extends Entity {

    private static final long UNSELECTED_COLOR = 0xFF90CAF9L; // light blue
    private static final long SELECTED_COLOR = 0xFF1E88E5L;   // blue

    public Drone(float xPosition, float yPosition) {
        super(xPosition, yPosition, UNSELECTED_COLOR, SELECTED_COLOR);
    }
}