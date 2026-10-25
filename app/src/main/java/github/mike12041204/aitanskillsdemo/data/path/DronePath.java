package github.mike12041204.aitanskillsdemo.data.path;

import github.mike12041204.aitanskillsdemo.data.DataUtil;
import github.mike12041204.aitanskillsdemo.data.field.FieldRepository;
import github.mike12041204.aitanskillsdemo.data.field.PointCm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Route a drone follows, built once when a command is given
// The drone moves along it each frame and the UI draws the same route
public abstract class DronePath implements DronePathView {

    // Points in field cm the drone moves through, in order, the first one is where the drone started
    private final List<PointCm> points;

    // True if the drone goes back to the first point after the last one and never finishes
    private final boolean loops;

    // Index in points of where the drone is heading next
    private int nextPointIndex;

    // How far the drone has moved along this path in total, the UI uses it to keep dotted lines still
    private float distanceTraveledCm;

    protected DronePath(boolean loops) {
        this.points = new ArrayList<>();
        this.loops = loops;
        // Point 0 is where the drone starts, so it heads for point 1 first
        this.nextPointIndex = 1;
        this.distanceTraveledCm = 0f;
    }

    @Override
    public float getDistanceTraveledCm() {
        return distanceTraveledCm;
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

        if (loops) {
            float loopLengthCm = getLoopLengthCm();

            // A loop with no length has nowhere to go, this also keeps the loop below from running forever
            if (loopLengthCm == 0) {
                return position;
            }

            // Whole laps end exactly where they started, so only the distance left after them matters
            remainingCm = remainingCm % loopLengthCm;
        }

        while (remainingCm > 0 && !isFinished()) {
            PointCm next = points.get(nextPointIndex);
            float toNextCm = DataUtil.distanceCm(position, next);

            if (toNextCm <= remainingCm) {
                // Reach the next point and keep going with the distance left over
                position = next;
                remainingCm -= toNextCm;
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

        // Whatever was not left over was traveled, including any whole laps skipped above
        distanceTraveledCm += distanceCm - remainingCm;

        return position;
    }

    // Points the drone has not reached yet, in order, for drawing the rest of the path
    // A looping path wraps around so the whole loop is always shown
    @Override
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

    // Length of one full lap of a looping path, including the way from the last point back to the first
    private float getLoopLengthCm() {
        float lengthCm = 0;

        for (int i = 0; i < points.size(); i++) {
            PointCm next = points.get((i + 1) % points.size());
            lengthCm += DataUtil.distanceCm(points.get(i), next);
        }

        return lengthCm;
    }
}
