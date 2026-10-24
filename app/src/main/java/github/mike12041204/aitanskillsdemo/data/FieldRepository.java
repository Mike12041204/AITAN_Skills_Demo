package github.mike12041204.aitanskillsdemo.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class FieldRepository {

    // All positions and sizes in real life centimeters
    public static final float FIELD_WIDTH = 10f;
    public static final float FIELD_HEIGHT = 18f;
    // For example, 6 means that entities won't generate in the outer 1/6 edges of the field
    public static final float EXCLUDED_BAND_DIVISOR = 6f;
    public static final float MIN_X_ENTITY_START = FIELD_WIDTH / EXCLUDED_BAND_DIVISOR;
    public static final float MAX_X_ENTITY_START = FIELD_WIDTH - MIN_X_ENTITY_START;
    public static final float MIN_Y_ENTITY_START = FIELD_HEIGHT / EXCLUDED_BAND_DIVISOR;
    public static final float MAX_Y_ENTITY_START = FIELD_HEIGHT - MIN_Y_ENTITY_START;

    private static final int NUMBER_OF_TARGETS = 2;
    private static final int NUMBER_OF_DRONES = 5;

    private long version = 0L;
    private final BehaviorSubject<Long> versionStream = BehaviorSubject.createDefault(0L);

    private final List<Entity> entities = new ArrayList<>();
    private final List<Entity> readOnlyEntities = Collections.unmodifiableList(entities);

    public FieldRepository() {
        Stream.generate(() -> new Target(DataUtil.randomFloat(MIN_X_ENTITY_START, MAX_X_ENTITY_START), DataUtil.randomFloat(MIN_Y_ENTITY_START, MAX_Y_ENTITY_START))).limit(NUMBER_OF_TARGETS).forEach(entities::add);
        Stream.generate(() -> new Drone(DataUtil.randomFloat(MIN_X_ENTITY_START, MAX_X_ENTITY_START), DataUtil.randomFloat(MIN_Y_ENTITY_START, MAX_Y_ENTITY_START))).limit(NUMBER_OF_DRONES).forEach(entities::add);

        publish();
    }

    public Observable<Long> getVersion() {
        return versionStream;
    }

    public List<Entity> getEntities() {
        return readOnlyEntities;
    }

    public void toggleSelection(Entity entity) {
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

    private void publish() {
        versionStream.onNext(++version);
    }
}
