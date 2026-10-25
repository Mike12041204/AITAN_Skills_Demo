package github.mike12041204.aitanskillsdemo.data.entities;

import github.mike12041204.aitanskillsdemo.data.field.PointCm;

// Read only view of an entity for the UI, changes only happen through the data layer
public interface EntityView {

    PointCm getPositionCm();

    boolean isSelected();

    float getSizeCm();

    float getSpeedCmPerSecond();

    boolean isExploding();

    float getExplodingSeconds();
}
