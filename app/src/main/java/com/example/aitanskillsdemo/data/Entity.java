package com.example.aitanskillsdemo.data;

public abstract class Entity {

    public enum EntityShape {
        CIRCLE,
        SQUARE
    }

    private final EntityShape shape;
    private final float size;
    private final long unselectedColor;
    private final long selectedColor;

    private float xPosition;
    private float yPosition;
    private boolean selected;

    protected Entity(float xPosition, float yPosition, long unselectedColor, long selectedColor,
                     EntityShape shape, float size) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.selected = false;
        this.unselectedColor = unselectedColor;
        this.selectedColor = selectedColor;
        this.shape = shape;
        this.size = size;
    }

    public float getXPosition() {
        return xPosition;
    }

    public float getYPosition() {
        return yPosition;
    }

    public boolean isSelected() {
        return selected;
    }

    public long getColor() {
        return selected ? selectedColor : unselectedColor;
    }

    public EntityShape getShape() {
        return shape;
    }

    public float getSize() {
        return size;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
