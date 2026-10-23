package com.example.aitanskillsdemo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class FieldRepository {

    private final static int NUMBER_OF_ENTITIES = 5;
    public static final float MIN_X_ENTITY_START = 0;
    public static final float MAX_X_ENTITY_START = 100;
    public static final float MIN_Y_ENTITY_START = 0;
    public static final float MAX_Y_ENTITY_START = 100;

    private long version = 0L;
    private final BehaviorSubject<Long> versionStream = BehaviorSubject.createDefault(0L);

    private final List<Entity> entities = new ArrayList<>();

    // default field has above defined number of random entities
    public FieldRepository(){
        Stream.generate(() -> new Entity(Util.randomFloat(MIN_X_ENTITY_START, MAX_X_ENTITY_START), Util.randomFloat(MIN_Y_ENTITY_START, MAX_Y_ENTITY_START))).limit(NUMBER_OF_ENTITIES).forEach(entities::add);
        publish();
    }

    public Observable<Long> getVersion(){
        return versionStream;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void sendSelection(Entity entity){
        entity.setSelected(!entity.getSelected());
        publish();
    }

    private void publish() {
        versionStream.onNext(++version);
    }
}
