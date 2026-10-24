package github.mike12041204.aitanskillsdemo.data;

public class Target extends Entity {

    private static final EntityShape SHAPE = EntityShape.SQUARE;
    // Size of entity in cm as it will be drawn on screen
    private static final float SIZE = 0.5f;
    private static final long UNSELECTED_COLOR = 0xFFFFCC80L; // light orange
    private static final long SELECTED_COLOR = 0xFFFB8C00L;   // orange

    public Target(float xPosition, float yPosition) {
        super(xPosition, yPosition, UNSELECTED_COLOR, SELECTED_COLOR, SHAPE, SIZE);
    }
}