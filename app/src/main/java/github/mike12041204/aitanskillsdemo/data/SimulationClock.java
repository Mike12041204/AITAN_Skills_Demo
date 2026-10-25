package github.mike12041204.aitanskillsdemo.data;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

// Ticks a set number of times per second and updates the field on every tick
public class SimulationClock {

    private static final long MICROSECONDS_PER_SECOND = 1_000_000L;

    // How many times per second drones update their positions
    private static final int FRAMES_PER_SECOND = 30;
    private static final long FRAME_DURATION_US = MICROSECONDS_PER_SECOND / FRAMES_PER_SECOND;
    private static final float FRAME_DURATION_SECONDS = (float) FRAME_DURATION_US / MICROSECONDS_PER_SECOND;

    private final FieldRepository fieldRepo;

    // The running ticks, null while the clock is stopped
    private Disposable ticks;

    // Frames run since the app started, keeps counting after a stop and start
    private long frameCount;
    private final BehaviorSubject<Long> frameCountStream = BehaviorSubject.createDefault(0L);

    public SimulationClock(FieldRepository fieldRepo) {
        this.fieldRepo = fieldRepo;
        this.ticks = null;
        this.frameCount = 0L;
    }

    public Observable<Long> getFrameCount() {
        return frameCountStream;
    }

    // Starts ticking, does nothing if already running
    public void start() {
        if (ticks != null) {
            return;
        }

        // Ticks on the main thread so entities are never changed while the UI is drawing them
        ticks = Observable.interval(FRAME_DURATION_US, TimeUnit.MICROSECONDS, AndroidSchedulers.mainThread())
                .subscribe(ignoredFrame -> onFrame());
    }

    // Runs once per frame
    private void onFrame() {
        fieldRepo.update(FRAME_DURATION_SECONDS);

        frameCount++;
        frameCountStream.onNext(frameCount);
    }

    // Stops ticking, the simulation pauses until start is called again
    public void stop() {
        if (ticks != null) {
            ticks.dispose();
            ticks = null;
        }
    }
}
