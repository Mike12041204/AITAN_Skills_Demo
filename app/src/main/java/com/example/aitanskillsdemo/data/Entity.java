package com.example.aitanskillsdemo.data;

public abstract class Entity {

    private float xPosition;
    private float yPosition;
    private boolean selected;
    private final long unselected_color;
    private final long selected_color;

    protected Entity(float xPosition, float yPosition, long unselected_color, long selected_color) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        selected = false;
        this.unselected_color = unselected_color;
        this.selected_color = selected_color;
    }

    public float getXPosition() {
        return xPosition;
    }

    public float getYPosition() {
        return yPosition;
    }

    public boolean getSelected() {
        return selected;
    }

    public long getColor() {
        return selected ? selected_color : unselected_color;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}