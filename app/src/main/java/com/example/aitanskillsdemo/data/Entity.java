package com.example.aitanskillsdemo.data;

import java.util.Random;

public class Entity {

    private float xPosition;
    private float yPosition;
    private boolean selected;

    public Entity(float xPosition, float yPosition){
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        selected = false;
    }

    public float getXPosition(){
        return xPosition;
    }

    public float getYPosition(){
        return yPosition;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
