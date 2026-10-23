package com.example.aitanskillsdemo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class FieldRepository {

    // all positions and sizes in real life centimeters
    public static final float FIELD_WIDTH = 10;
    public static final float FIELD_HEIGHT = 18;
    // for example, 6 means that entities won't generate in the outer 1/6 edges of the field
    public static final float EXCLUDED_BAND_FRACTION = 6;
    public static final float MIN_X_ENTITY_START = FIELD_WIDTH / EXCLUDED_BAND_FRACTION;
    public static final float MAX_X_ENTITY_START = FIELD_WIDTH - MIN_X_ENTITY_START;
    public static final float MIN_Y_ENTITY_START = FIELD_HEIGHT / EXCLUDED_BAND_FRACTION;
    public static final float MAX_Y_ENTITY_START = FIELD_HEIGHT - MIN_Y_ENTITY_START;
    private static final int NUMBER_OF_TARGETS = 2;
    private static final int NUMBER_OF_DRONES = 5;

    private long version = 0L;
    private final BehaviorSubject<Long> versionStream = BehaviorSubject.createDefault(0L);

    private final List<Entity> entities = new ArrayList<>();

    public FieldRepository() {
        Stream.generate(() -> new Target(DataUtil.randomFloat(MIN_X_ENTITY_START, MAX_X_ENTITY_START), DataUtil.randomFloat(MIN_Y_ENTITY_START, MAX_Y_ENTITY_START))).limit(NUMBER_OF_TARGETS).forEach(entities::add);
        Stream.generate(() -> new Drone(DataUtil.randomFloat(MIN_X_ENTITY_START, MAX_X_ENTITY_START), DataUtil.randomFloat(MIN_Y_ENTITY_START, MAX_Y_ENTITY_START))).limit(NUMBER_OF_DRONES).forEach(entities::add);

        publish();
    }

    public Observable<Long> getVersion() {
        return versionStream;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void sendSelection(Entity entity) {
        entity.setSelected(!entity.getSelected());

        // only one target can be selected at a time
        if (entity instanceof Target) {
            for (Entity other : entities) {
                if (other instanceof Target && other != entity && other.getSelected()) {
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