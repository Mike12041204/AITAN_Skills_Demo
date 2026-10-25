package github.mike12041204.aitanskillsdemo.data;

// Straight line from the drone to its target
/* package */ class StraightPath extends DronePath {

    /* package */ StraightPath(Drone drone, Target target) {
        super(false);

        PointCm start = drone.getPositionCm();
        PointCm end = target.getPositionCm();

        addPoint(start.getXCm(), start.getYCm());
        addPoint(end.getXCm(), end.getYCm());
    }
}
