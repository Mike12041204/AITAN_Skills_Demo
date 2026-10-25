package github.mike12041204.aitanskillsdemo.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

// Owns every entity on the field and all the rules for changing them
public class FieldRepository {

    // All positions and sizes in real life centimeters
    public static final float FIELD_WIDTH_CM = 10f;
    public static final float FIELD_HEIGHT_CM = 18f;

    // For example, 6 means that entities won't generate in the outer 1/6 edges of the field
    private static final float EXCLUDED_BAND_DIVISOR = 6f;
    private static final float MIN_X_ENTITY_START_CM = FIELD_WIDTH_CM / EXCLUDED_BAND_DIVISOR;
    private static final float MAX_X_ENTITY_START_CM = FIELD_WIDTH_CM - MIN_X_ENTITY_START_CM;
    private static final float MIN_Y_ENTITY_START_CM = FIELD_HEIGHT_CM / EXCLUDED_BAND_DIVISOR;
    private static final float MAX_Y_ENTITY_START_CM = FIELD_HEIGHT_CM - MIN_Y_ENTITY_START_CM;

    private static final int NUMBER_OF_TARGETS = 2;
    private static final int NUMBER_OF_DRONES = 5;

    // Goes up on every change so the UI knows to redraw
    private long version = 0L;
    private final BehaviorSubject<Long> versionStream = BehaviorSubject.createDefault(0L);

    // Entities are changed in place, the UI only ever gets the read only view
    private final List<Entity> entities = new ArrayList<>();
    private final List<Entity> readOnlyEntities = Collections.unmodifiableList(entities);

    // Starts the field with random targets and drones inside the allowed start area
    public FieldRepository() {
        Stream.generate(() -> new Target(randomStartPositionCm()))
                .limit(NUMBER_OF_TARGETS)
                .forEach(entities::add);

        Stream.generate(() -> new Drone(randomStartPositionCm()))
                .limit(NUMBER_OF_DRONES)
                .forEach(entities::add);

        publish();
    }

    // Random point inside the allowed start area
    private static PointCm randomStartPositionCm() {
        return new PointCm(
                DataUtil.randomFloat(MIN_X_ENTITY_START_CM, MAX_X_ENTITY_START_CM),
                DataUtil.randomFloat(MIN_Y_ENTITY_START_CM, MAX_Y_ENTITY_START_CM));
    }

    public Observable<Long> getVersion() {
        return versionStream;
    }

    public List<Entity> getEntities() {
        return readOnlyEntities;
    }

    // Selects an unselected entity or unselects a selected one
    public void toggleSelection(Entity entity) {
        entity.setSelected(!entity.getSelected());

        // Only one target can be selected at a time
        if (entity instanceof Target) {
            for (Entity other : entities) {
                if (other instanceof Target && other != entity) {
                    other.setSelected(false);
                }
            }
        }

        publish();
    }

    // Called by SimulationClock once per frame, updates every entity then redraws the field once
    /* package */ void update(float secondsSinceLastFrame) {
        for (Entity entity : entities) {
            entity.update(secondsSinceLastFrame, readOnlyEntities);
        }

        // Removed after the loop because changing a list while looping over it crashes
        entities.removeIf(Entity::getDeleted);

        publish();
    }

    // Tells the UI the field changed, call this at the end of anything that changes entities
    // DroneCommands in the same package calls this too
    /* package */ void publish() {
        versionStream.onNext(++version);
    }
}
