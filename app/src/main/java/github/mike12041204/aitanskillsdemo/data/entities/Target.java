package github.mike12041204.aitanskillsdemo.data.entities;

import github.mike12041204.aitanskillsdemo.data.field.PointCm;

import java.util.List;

// What drones attack, only one target can be selected at a time
// The command types are in TargetView so the UI can read them
public final class Target extends Entity implements TargetView {

    // Real-life size of a target in cm
    private static final float SIZE_CM = 0.5f;

    // Targets stay where they are for now
    private static final float SPEED_CM_PER_SECOND = 0f;

    // How long a target explodes for before it deletes itself
    private static final float EXPLODE_DURATION_SECONDS = 1f;

    private TargetCommand command;

    public Target(PointCm positionCm) {
        super(positionCm, SIZE_CM, SPEED_CM_PER_SECOND, EXPLODE_DURATION_SECONDS);
        this.command = TargetCommand.NONE;
    }

    @Override
    public TargetCommand getCommand() {
        return command;
    }

    public void setCommand(TargetCommand command) {
        this.command = command;
    }

    // Runs the actions for the target's command, targets do not move
    @Override
    protected void updateCommand(float secondsSinceLastFrame, List<Entity> entities) {
    }
}
