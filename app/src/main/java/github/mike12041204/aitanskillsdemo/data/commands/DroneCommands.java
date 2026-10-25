package github.mike12041204.aitanskillsdemo.data.commands;

import androidx.annotation.Nullable;
import github.mike12041204.aitanskillsdemo.data.entities.Drone;
import github.mike12041204.aitanskillsdemo.data.entities.Entity;
import github.mike12041204.aitanskillsdemo.data.entities.Target;
import github.mike12041204.aitanskillsdemo.data.field.FieldRepository;
import github.mike12041204.aitanskillsdemo.data.path.EvasivePath;
import github.mike12041204.aitanskillsdemo.data.path.PatrolPath;
import github.mike12041204.aitanskillsdemo.data.path.StraightPath;

import java.util.ArrayList;
import java.util.List;

// Orders that can be given to the selected drones, each command has a condition that must be true first
public final class DroneCommands {

    // Coordinated attack pattern, one straight drone then this many evasive drones, repeating
    private static final int EVASIVE_DRONES_PER_STRAIGHT_DRONE = 2;

    private final FieldRepository fieldRepo;

    public DroneCommands(FieldRepository fieldRepo) {
        this.fieldRepo = fieldRepo;
    }

    // Conditions, a command only runs if its condition is true
    public boolean canStraightAttack() {
        return countSelected(Drone.class) >= 1 && countSelected(Target.class) == 1;
    }

    public boolean canEvasiveAttack() {
        return countSelected(Drone.class) >= 1 && countSelected(Target.class) == 1;
    }

    public boolean canCoordinatedAttack() {
        return countSelected(Drone.class) >= 1 && countSelected(Target.class) == 1;
    }

    // Patrol and cancel ignore any selected target
    public boolean canPatrol() {
        return countSelected(Drone.class) >= 1;
    }

    public boolean canCancel() {
        return countSelected(Drone.class) >= 1;
    }

    // Commands, each one unselects all drones when it is done
    // All selected drones head straight for the selected target
    public void straightAttack() {
        requireCondition(canStraightAttack(), "straightAttack");

        Target target = firstSelected(Target.class);

        for (Drone drone : allSelected(Drone.class)) {
            drone.setCommand(Drone.DroneCommand.STRAIGHT_ATTACK);
            drone.setCommandTarget(target);
            drone.setPath(new StraightPath(drone, target));
        }

        unselectAllDrones();

        fieldRepo.publish();
    }

    // All selected drones weave toward the selected target
    public void evasiveAttack() {
        requireCondition(canEvasiveAttack(), "evasiveAttack");

        Target target = firstSelected(Target.class);

        for (Drone drone : allSelected(Drone.class)) {
            drone.setCommand(Drone.DroneCommand.EVASIVE_ATTACK);
            drone.setCommandTarget(target);
            drone.setPath(new EvasivePath(drone, target));
        }

        unselectAllDrones();

        fieldRepo.publish();
    }

    // All selected drones attack the selected target, mixing straight and evasive attacks
    public void coordinatedAttack() {
        requireCondition(canCoordinatedAttack(), "coordinatedAttack");

        List<Drone> drones = allSelected(Drone.class);
        Target target = firstSelected(Target.class);

        for (int i = 0; i < drones.size(); i++) {
            Drone drone = drones.get(i);

            // Each group starts with one straight drone followed by the evasive ones
            if (i % (EVASIVE_DRONES_PER_STRAIGHT_DRONE + 1) == 0) {
                drone.setCommand(Drone.DroneCommand.STRAIGHT_ATTACK);
                drone.setPath(new StraightPath(drone, target));
            } else {
                drone.setCommand(Drone.DroneCommand.EVASIVE_ATTACK);
                drone.setPath(new EvasivePath(drone, target));
            }

            drone.setCommandTarget(target);
        }

        unselectAllDrones();

        fieldRepo.publish();
    }

    // All selected drones patrol where they are, patrol has no target
    public void patrol() {
        requireCondition(canPatrol(), "patrol");

        for (Drone drone : allSelected(Drone.class)) {
            drone.setCommand(Drone.DroneCommand.PATROL);
            drone.setCommandTarget(null);
            drone.setPath(new PatrolPath(drone));
        }

        unselectAllDrones();

        fieldRepo.publish();
    }

    // All selected drones stop what they are doing
    public void cancel() {
        requireCondition(canCancel(), "cancel");

        for (Drone drone : allSelected(Drone.class)) {
            drone.setCommand(Drone.DroneCommand.NONE);
            drone.setCommandTarget(null);
            drone.setPath(null);
        }

        unselectAllDrones();

        fieldRepo.publish();
    }

    // Commands are only called after their condition is checked, so a false one is a bug
    private void requireCondition(boolean condition, String commandName) {
        if (!condition) {
            throw new IllegalStateException(commandName + " called when its condition is false");
        }
    }

    // How many selected entities are of the given type, for example Drone.class
    private int countSelected(Class<? extends Entity> type) {
        int count = 0;

        for (Entity entity : fieldRepo.getEntities()) {
            if (entity.isSelected() && type.isInstance(entity)) {
                count++;
            }
        }

        return count;
    }

    // Every selected entity of the given type, in field order
    private <T extends Entity> List<T> allSelected(Class<T> type) {
        List<T> selected = new ArrayList<>();

        for (Entity entity : fieldRepo.getEntities()) {
            if (entity.isSelected() && type.isInstance(entity)) {
                selected.add(type.cast(entity));
            }
        }

        return selected;
    }

    // First selected entity of the given type, or null if none is selected
    @Nullable
    private <T extends Entity> T firstSelected(Class<T> type) {
        List<T> selected = allSelected(type);

        if (selected.isEmpty()) {
            return null;
        } else {
            return selected.get(0);
        }
    }

    // Unselects every drone so the next command starts with a fresh selection
    private void unselectAllDrones() {
        for (Drone drone : allSelected(Drone.class)) {
            drone.setSelected(false);
        }
    }
}
