package github.mike12041204.aitanskillsdemo.data;

// What drones attack, only one target can be selected at a time
public class Target extends Entity {

    // Real life size of a target in cm
    private static final float SIZE_CM = 0.5f;

    // Targets stay where they are for now
    private static final float SPEED_CM_PER_SECOND = 0f;

    public Target(PointCm positionCm) {
        super(positionCm, SIZE_CM, SPEED_CM_PER_SECOND);
    }

    // Targets do not move or change over time
    @Override
    void update(float secondsSinceLastFrame) {
    }
}
