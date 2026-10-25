package github.mike12041204.aitanskillsdemo.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Route a drone follows, built once when a command is given
// The drone moves along it each frame and the UI draws the same route
public abstract class DronePath {

    // Points in field cm the drone moves through, in order, the first one is where the drone started
    private final List<PointCm> points = new ArrayList<>();

    // True if the drone goes back to the first point after the last one and never finishes
    private final boolean loops;

    // Index in points of where the drone is heading next
    private int nextPointIndex;

    protected DronePath(boolean loops) {
        this.loops = loops;
        this.nextPointIndex = 1;
    }

    // Adds a point to the end of the path, kept inside the field so drones never leave it
    protected void addPoint(float xCm, float yCm) {
        points.add(new PointCm(
                DataUtil.clamp(xCm, 0f, FieldRepository.FIELD_WIDTH_CM),
                DataUtil.clamp(yCm, 0f, FieldRepository.FIELD_HEIGHT_CM)));
    }

    // True once the drone has reached the last point, looping paths never finish
    public boolean isFinished() {
        return !loops && nextPointIndex >= points.size();
    }

    // Moves from position along the path by distanceCm, returns where it ends up
    public PointCm advance(PointCm position, float distanceCm) {
        float remainingCm = distanceCm;

        // MBG - This seems like a bad way to do this as a low frame rate thing with a short path would stop when it shouldn't
        // Stops after one full lap in a single frame so a path with no length can never loop forever
        int pointsReached = 0;

        while (remainingCm > 0 && !isFinished() && pointsReached <= points.size()) {
            PointCm next = points.get(nextPointIndex);
            float toNextCm = DataUtil.distanceCm(position, next);

            if (toNextCm <= remainingCm) {
                // Reach the next point and keep going with the distance left over
                position = next;
                remainingCm -= toNextCm;
                pointsReached++;
                nextPointIndex++;

                if (loops && nextPointIndex >= points.size()) {
                    nextPointIndex = 0;
                }
            } else {
                // Move part of the way toward the next point
                position = DataUtil.pointBetween(position, next, remainingCm / toNextCm);
                remainingCm = 0;
            }
        }

        return position;
    }

    // Points the drone has not reached yet, in order, for drawing the rest of the path
    // A looping path wraps around so the whole loop is always shown
    public List<PointCm> getRemainingPoints() {
        List<PointCm> remaining = new ArrayList<>();

        for (int i = nextPointIndex; i < points.size(); i++) {
            remaining.add(points.get(i));
        }

        if (loops) {
            for (int i = 0; i < nextPointIndex; i++) {
                remaining.add(points.get(i));
            }
        }

        return Collections.unmodifiableList(remaining);
    }
}
