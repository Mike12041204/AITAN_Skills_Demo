package github.mike12041204.aitanskillsdemo.data.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import github.mike12041204.aitanskillsdemo.data.DataUtil;
import github.mike12041204.aitanskillsdemo.data.entities.Drone;
import github.mike12041204.aitanskillsdemo.data.entities.Entity;
import github.mike12041204.aitanskillsdemo.data.entities.EntityView;
import github.mike12041204.aitanskillsdemo.data.entities.Target;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

// Owns every entity on the field and all the rules for changing them
public final class FieldRepository {

    // All positions and sizes in real life centimeters
    // Width and height are suggested to be at least 15
    public static final float FIELD_WIDTH_CM = 15f;
    public static final float FIELD_HEIGHT_CM = 23f;

    // For example, 10 means that entities won't generate in the outer 1/10 edges of the field
    private static final float EXCLUDED_BAND_DIVISOR = 10f;
    private static final float MIN_X_ENTITY_START_CM = FIELD_WIDTH_CM / EXCLUDED_BAND_DIVISOR;
    private static final float MAX_X_ENTITY_START_CM = FIELD_WIDTH_CM - MIN_X_ENTITY_START_CM;
    private static final float MIN_Y_ENTITY_START_CM = FIELD_HEIGHT_CM / EXCLUDED_BAND_DIVISOR;
    private static final float MAX_Y_ENTITY_START_CM = FIELD_HEIGHT_CM - MIN_Y_ENTITY_START_CM;

    private static final int NUMBER_OF_TARGETS = 6;
    private static final int NUMBER_OF_DRONES = 12;
    // Entities are changed in place, the data layer gets a read only list and the UI only gets read only views
    private final List<Entity> entities;
    private final List<Entity> readOnlyEntities;
    private final List<EntityView> readOnlyEntityViews;
    // Goes up on every change so the UI knows to redraw
    private long version;
    private final BehaviorSubject<Long> versionStream;

    // Starts the field with random targets and drones inside the allowed start area
    public FieldRepository() {
        this.version = 0L;
        this.versionStream = BehaviorSubject.createDefault(0L);
        this.entities = new ArrayList<>();
        this.readOnlyEntities = Collections.unmodifiableList(entities);
        this.readOnlyEntityViews = Collections.unmodifiableList(entities);

        for (int i = 0; i < NUMBER_OF_TARGETS; i++) {
            entities.add(new Target(randomStartPositionCm()));
        }

        for (int i = 0; i < NUMBER_OF_DRONES; i++) {
            entities.add(new Drone(randomStartPositionCm()));
        }

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

    // Every entity on the field as read only views, for the UI
    public List<EntityView> getEntityViews() {
        return readOnlyEntityViews;
    }

    // Every entity on the field, for the data layer only, the UI uses getEntityViews
    public List<Entity> getEntities() {
        return readOnlyEntities;
    }

    // Selects an unselected entity or unselects a selected one
    // Takes a view because the UI only has views, every view on the field is one of its entities
    public void toggleSelection(EntityView entityView) {
        Entity entity = (Entity) entityView;
        entity.setSelected(!entity.isSelected());

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
    public void update(float secondsSinceLastFrame) {
        for (Entity entity : entities) {
            entity.update(secondsSinceLastFrame, readOnlyEntities);
        }

        // Removed after the loop because changing a list while looping over it crashes
        entities.removeIf(Entity::isDeleted);

        publish();
    }

    // Tells the UI the field changed, call this at the end of anything that changes entities
    // DroneCommands calls this too
    public void publish() {
        versionStream.onNext(++version);
    }
}
