package github.mike12041204.aitanskillsdemo.data;

// Moves around the field following commands, many drones can be selected at once
public class Drone extends Entity {

    // What the drone is currently ordered to do
    public enum DroneCommand {
        NONE,
        STRAIGHT_ATTACK,
        EVASIVE_ATTACK,
        PATROL
    }

    // Real-life size of a drone in cm
    private static final float SIZE_CM = 0.25f;
    // How far a drone moves along its path every second
    private static final float SPEED_CM_PER_SECOND = 2f;

    private DroneCommand command;
    // Target the command is aimed at, stays the same even if a different target gets selected later
    // Null when the command has no target
    private Target commandTarget;
    // Route the drone is following for its command, null when it has no command
    private DronePath path;

    public Drone(PointCm positionCm) {
        super(positionCm, SIZE_CM, SPEED_CM_PER_SECOND);
        this.command = DroneCommand.NONE;
        this.commandTarget = null;
        this.path = null;
    }

    public DroneCommand getCommand() {
        return command;
    }

    public void setCommand(DroneCommand command) {
        this.command = command;
    }

    public Target getCommandTarget() {
        return commandTarget;
    }

    public void setCommandTarget(Target commandTarget) {
        this.commandTarget = commandTarget;
    }

    public DronePath getPath() {
        return path;
    }

    public void setPath(DronePath path) {
        this.path = path;
    }

    // Moves the drone along its command path
    @Override
    void update(float secondsSinceLastFrame) {
        if (path == null) {
            return;
        }

        setPositionCm(path.advance(getPositionCm(), getSpeedCmPerSecond() * secondsSinceLastFrame));

        // TODO decide what happens when a drone reaches the end of its path, for now it just stops
        if (path.isFinished()) {
            command = DroneCommand.NONE;
            commandTarget = null;
            path = null;
        }
    }
}
