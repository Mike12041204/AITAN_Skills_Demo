# AITAN Skills Demo

An Android app that simulates drones and targets on a field shown at real life size in centimeters.
Tap drones and targets to select them, then use the hotbar to give the selected drones a command:
straight attack, evasive attack, coordinated attack, patrol, or cancel. Drag to move around the field.

## Building

Open the project in Android Studio and run the `app` configuration.

To build from a terminal, Gradle needs a JDK. If `JAVA_HOME` is not set, point it at the JDK that
comes with Android Studio first:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug
```

## Project layout

```
github.mike12041204.aitanskillsdemo/
├── AppContainer        Shared instances of the repository, commands and clock
├── MainActivity        Entry point, stacks the field, hotbar and debug overlay
├── data/               Java, the simulation and its rules
│   ├── DataUtil        Math helpers
│   ├── commands/       DroneCommands, what the hotbar buttons do
│   ├── entities/       Entity, Drone, Target and their read only views
│   ├── field/          FieldRepository, SimulationClock, PointCm
│   └── path/           Routes drones follow and their read only view
└── ui/                 Kotlin and Jetpack Compose, drawing and input
    ├── UiUtil          Centimeter, dp and pixel conversions
    ├── debug/          Frame counter overlay
    ├── field/          The field and everything on it
    ├── hotbar/         Command buttons and fail messages
    └── theme/          Colors, text styles and the app theme
```

## How it fits together

- The data layer is written in Java and the UI in Kotlin. New files should follow the same split
- `FieldRepository` owns every entity. `SimulationClock` ticks 30 times a second on the main thread
  and updates the field each tick
- The UI only sees read only views (`EntityView`, `DroneView`, `TargetView`, `DronePathView`)
  and changes the field through `FieldRepository` and `DroneCommands`
- The repository publishes a version number with RxJava every time the field changes, and the
  field screen redraws when it does
- The app always uses light mode, all colors are in `ui/theme/Color.kt`

## Style

- Android Studio default formatting, run Reformat Code and Optimize Imports before committing
- Units in names, for example `sizeCm`, `pxPerCm` and `FRAME_DURATION_SECONDS`
- Line comments without periods, explaining why rather than what
- User visible text in `res/values/strings.xml`
- Commit messages in the imperative with no period, for example `Add patrol command`
