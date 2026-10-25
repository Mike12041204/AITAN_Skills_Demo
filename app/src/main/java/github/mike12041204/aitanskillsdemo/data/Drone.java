package github.mike12041204.aitanskillsdemo.data;

import java.util.List;

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
    // How close an attacking drone has to get to its target for both of them to explode
    private static final float EXPLODE_DISTANCE_CM = 0.5f;
    // When another drone destroys this drone's target, it only cancels its attack if it is farther away than this
    private static final float EXPLODE_CANCEL_DISTANCE_CM = 2f;
    // How long a drone explodes for before it deletes itself
    private static final float EXPLODE_DURATION_SECONDS = 1f;
    // A patrolling drone straight attacks the closest target that comes within this distance
    public static final float PATROL_DISCOVER_DISTANCE_CM = 4f;

    private DroneCommand command;
    // Target the command is aimed at, stays the same even if a different target gets selected later
    // Null when the command has no target
    private Target commandTarget;
    // Route the drone is following for its command, null when it has no command
    private DronePath path;

    public Drone(PointCm positionCm) {
        super(positionCm, SIZE_CM, SPEED_CM_PER_SECOND, EXPLODE_DURATION_SECONDS);
        this.command = DroneCommand.NONE;
        this.commandTarget = null;
        this.path = null;
    }

    public DroneCommand getCommand() {
        return command;
    }

    /* package */ void setCommand(DroneCommand command) {
        this.command = command;
    }

    public Target getCommandTarget() {
        return commandTarget;
    }

    /* package */ void setCommandTarget(Target commandTarget) {
        this.commandTarget = commandTarget;
    }

    public DronePath getPath() {
        return path;
    }

    /* package */ void setPath(DronePath path) {
        this.path = path;
    }

    // Moves the drone along its command path, then runs the actions for its command
    @Override
    /* package */ void updateCommand(float secondsSinceLastFrame, List<Entity> entities) {
        // Another drone already destroyed this target, so stop instead of flying to where it was
        if (commandTarget != null && commandTarget.getExploding()) {
            float distanceToTargetCm = DataUtil.distanceCm(getPositionCm(), commandTarget.getPositionCm());

            // If the drones already close keep going and explode with it
            if (distanceToTargetCm > EXPLODE_CANCEL_DISTANCE_CM) {
                command = DroneCommand.NONE;
                commandTarget = null;
                path = null;
            }
        }

        // Only drones with a path move
        if (path != null) {
            setPositionCm(path.advance(getPositionCm(), getSpeedCmPerSecond() * secondsSinceLastFrame));
        }

        // Actions for each command, run every frame
        if (command == DroneCommand.STRAIGHT_ATTACK || command == DroneCommand.EVASIVE_ATTACK) {
            float distanceToTargetCm = DataUtil.distanceCm(getPositionCm(), commandTarget.getPositionCm());

            // Close enough to the target, both explode and the drone drops its command and stops moving
            if (distanceToTargetCm <= EXPLODE_DISTANCE_CM) {
                explode();
                commandTarget.explode();

                command = DroneCommand.NONE;
                commandTarget = null;
                path = null;
            }
        } else if (command == DroneCommand.PATROL) {
            Target discoveredTarget = findClosestTarget(entities, PATROL_DISCOVER_DISTANCE_CM);

            // Found a target while patrolling, attack it the same way the straight attack button does
            if (discoveredTarget != null) {
                command = DroneCommand.STRAIGHT_ATTACK;
                commandTarget = discoveredTarget;
                path = new StraightPath(this, discoveredTarget);
            }
        }

        // Drone reached end of path, stop movement and command
        if (path != null && path.isFinished()) {
            command = DroneCommand.NONE;
            commandTarget = null;
            path = null;
        }
    }

    // Closest target that is not exploding and is within maxDistanceCm of the drone, null if there is none
    private Target findClosestTarget(List<Entity> entities, float maxDistanceCm) {
        Target closestTarget = null;
        float closestDistanceCm = maxDistanceCm;

        for (Entity entity : entities) {
            if (entity instanceof Target && !entity.getExploding()) {
                float distanceCm = DataUtil.distanceCm(getPositionCm(), entity.getPositionCm());

                if (distanceCm <= closestDistanceCm) {
                    closestTarget = (Target) entity;
                    closestDistanceCm = distanceCm;
                }
            }
        }

        return closestTarget;
    }
}
