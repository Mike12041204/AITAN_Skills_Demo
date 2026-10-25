package github.mike12041204.aitanskillsdemo.data.path;

import github.mike12041204.aitanskillsdemo.data.entities.Drone;
import github.mike12041204.aitanskillsdemo.data.entities.Target;
import github.mike12041204.aitanskillsdemo.data.field.PointCm;

// Straight line from the drone to its target
public class StraightPath extends DronePath {

    public StraightPath(Drone drone, Target target) {
        super(false);

        PointCm start = drone.getPositionCm();
        PointCm end = target.getPositionCm();

        addPoint(start.getXCm(), start.getYCm());
        addPoint(end.getXCm(), end.getYCm());
    }
}
