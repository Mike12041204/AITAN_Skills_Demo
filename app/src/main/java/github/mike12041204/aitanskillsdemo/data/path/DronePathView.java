package github.mike12041204.aitanskillsdemo.data.path;

import github.mike12041204.aitanskillsdemo.data.field.PointCm;

import java.util.List;

// Read only view of a drone's path for the UI
public interface DronePathView {

    // How far the drone has moved along this path in total, the UI uses it to keep dotted lines still
    float getDistanceTraveledCm();

    // Points the drone has not reached yet, in order, for drawing the rest of the path
    List<PointCm> getRemainingPoints();
}
