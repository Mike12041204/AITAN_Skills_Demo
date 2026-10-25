package github.mike12041204.aitanskillsdemo.data.entities;

import github.mike12041204.aitanskillsdemo.data.field.PointCm;

import java.util.List;

// Anything that sits on the field, subclasses decide the size, speed and explode time, how it looks is up to the UI
public abstract class Entity {

    // Set once by the subclass and never change
    private final float sizeCm;
    private final float speedCmPerSecond;
    private final float explodeDurationSeconds;

    // Can change while the app runs, position is in field cm
    private PointCm positionCm;
    private boolean selected;
    // True once the entity has started exploding, it stops following its command and is deleted when done
    private boolean exploding;
    // How long the entity has been exploding so far
    private float explodingSeconds;
    // True once the entity should be taken off the field
    private boolean deleted;

    protected Entity(PointCm positionCm, float sizeCm, float speedCmPerSecond, float explodeDurationSeconds) {
        this.positionCm = positionCm;
        this.selected = false;
        this.exploding = false;
        this.explodingSeconds = 0f;
        this.deleted = false;
        this.sizeCm = sizeCm;
        this.speedCmPerSecond = speedCmPerSecond;
        this.explodeDurationSeconds = explodeDurationSeconds;
    }

    public PointCm getPositionCm() {
        return positionCm;
    }

    public boolean isSelected() {
        return selected;
    }

    public float getSizeCm() {
        return sizeCm;
    }

    public float getSpeedCmPerSecond() {
        return speedCmPerSecond;
    }

    public boolean isExploding() {
        return exploding;
    }

    public float getExplodingSeconds() {
        return explodingSeconds;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // For subclasses to move themselves
    protected void setPositionCm(PointCm positionCm) {
        this.positionCm = positionCm;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Starts exploding, called by the entity itself or by a drone that hit it
    // Unselected so an exploding entity can never be given a command
    public void explode() {
        this.exploding = true;
        this.selected = false;
    }

    // Called once per frame, exploding entities count down until they are deleted, the rest follow their command
    // entities is every entity on the field, read only, so entities can react to each other
    public final void update(float secondsSinceLastFrame, List<Entity> entities) {
        if (exploding) {
            explodingSeconds += secondsSinceLastFrame;

            // Done exploding, take the entity off the field
            if (explodingSeconds >= explodeDurationSeconds) {
                deleted = true;
            }
        } else {
            updateCommand(secondsSinceLastFrame, entities);
        }
    }

    // Called once per frame while not exploding, each kind of entity decides what its command does
    protected abstract void updateCommand(float secondsSinceLastFrame, List<Entity> entities);
}
