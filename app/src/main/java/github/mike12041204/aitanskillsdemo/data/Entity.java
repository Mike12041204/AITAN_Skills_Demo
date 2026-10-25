package github.mike12041204.aitanskillsdemo.data;

// Anything that sits on the field, subclasses decide the size and speed, how it looks is up to the UI
public abstract class Entity {

    // Set once by the subclass and never change
    private final float sizeCm;
    private final float speedCmPerSecond;

    // Can change while the app runs, position is in field cm
    private PointCm positionCm;
    private boolean selected;

    protected Entity(PointCm positionCm, float sizeCm, float speedCmPerSecond) {
        this.positionCm = positionCm;
        this.selected = false;
        this.sizeCm = sizeCm;
        this.speedCmPerSecond = speedCmPerSecond;
    }

    public PointCm getPositionCm() {
        return positionCm;
    }

    // Only entities can move themselves
    protected void setPositionCm(PointCm positionCm) {
        this.positionCm = positionCm;
    }

    public boolean getSelected() {
        return selected;
    }

    public float getSizeCm() {
        return sizeCm;
    }

    public float getSpeedCmPerSecond() {
        return speedCmPerSecond;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Called once per frame, each kind of entity decides what it does over time
    abstract void update(float secondsSinceLastFrame);
}
