package github.mike12041204.aitanskillsdemo.data;

// Loops around a circle that the drone is already on when the patrol starts
public class PatrolPath extends DronePath {

    private static final float PATROL_RADIUS_CM = 0.4f;
    // More points make a smoother circle
    private static final int PATROL_POINT_COUNT = 50;

    public PatrolPath(Drone drone) {
        super(true);

        PointCm start = drone.getPositionCm();

        // Center is to the left of the drone so the drone starts on the right edge of the circle
        float centerXCm = start.getXCm() - PATROL_RADIUS_CM;
        float centerYCm = start.getYCm();

        // Point 0 is at angle 0, which is exactly where the drone already is
        for (int i = 0; i < PATROL_POINT_COUNT; i++) {
            double angle = 2 * Math.PI * i / PATROL_POINT_COUNT;

            addPoint(
                    centerXCm + PATROL_RADIUS_CM * (float) Math.cos(angle),
                    centerYCm + PATROL_RADIUS_CM * (float) Math.sin(angle));
        }
    }
}
