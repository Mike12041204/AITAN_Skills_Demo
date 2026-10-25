package github.mike12041204.aitanskillsdemo.data;

// Zig zags side to side on the way from the drone to its target
/* package */ class EvasivePath extends DronePath {

    // Distance along the straight line between each corner of the zig zag
    private static final float ZIGZAG_SPACING_CM = 0.3f;
    // How far each corner is pushed out to the side of the straight line
    private static final float ZIGZAG_WIDTH_CM = 0.3f;

    /* package */ EvasivePath(Drone drone, Target target) {
        super(false);

        PointCm start = drone.getPositionCm();
        PointCm end = target.getPositionCm();
        float lengthCm = DataUtil.distanceCm(start, end);

        addPoint(start.getXCm(), start.getYCm());

        // Only zigzag if there is room, otherwise go straight to the target
        if (lengthCm > 0) {
            // Direction along the straight line, and sideways at a right angle to it
            float alongX = (end.getXCm() - start.getXCm()) / lengthCm;
            float alongY = (end.getYCm() - start.getYCm()) / lengthCm;
            float sideX = -alongY;
            float sideY = alongX;

            int steps = Math.max(1, Math.round(lengthCm / ZIGZAG_SPACING_CM));

            // Corners alternate left and right of the straight line
            for (int i = 1; i < steps; i++) {
                float distanceAlongCm = lengthCm * i / steps;
                float sideCm;

                if (i % 2 == 1) {
                    sideCm = ZIGZAG_WIDTH_CM;
                } else {
                    sideCm = -ZIGZAG_WIDTH_CM;
                }

                addPoint(
                        start.getXCm() + alongX * distanceAlongCm + sideX * sideCm,
                        start.getYCm() + alongY * distanceAlongCm + sideY * sideCm);
            }
        }

        addPoint(end.getXCm(), end.getYCm());
    }
}
