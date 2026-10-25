package github.mike12041204.aitanskillsdemo.data.entities;

import androidx.annotation.Nullable;

import github.mike12041204.aitanskillsdemo.data.path.DronePathView;

// Read only view of a drone for the UI
public interface DroneView extends EntityView {

    // A patrolling drone straight attacks the closest target that comes within this distance
    float PATROL_DISCOVER_DISTANCE_CM = 2.75f;

    // What the drone is currently ordered to do
    enum DroneCommand {
        NONE,
        STRAIGHT_ATTACK,
        EVASIVE_ATTACK,
        PATROL
    }

    DroneCommand getCommand();

    // Null when the command has no target
    @Nullable
    TargetView getCommandTarget();

    // Null when the drone has no command
    @Nullable
    DronePathView getPath();
}
