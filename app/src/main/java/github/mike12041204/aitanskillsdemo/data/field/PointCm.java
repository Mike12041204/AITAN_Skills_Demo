package github.mike12041204.aitanskillsdemo.data.field;

// One position on the field in cm, never changes once made
public final class PointCm {

    private final float xCm;
    private final float yCm;

    public PointCm(float xCm, float yCm) {
        this.xCm = xCm;
        this.yCm = yCm;
    }

    public float getXCm() {
        return xCm;
    }

    public float getYCm() {
        return yCm;
    }
}
