package github.mike12041204.aitanskillsdemo

import github.mike12041204.aitanskillsdemo.data.SimulationClock
import github.mike12041204.aitanskillsdemo.data.commands.DroneCommands
import github.mike12041204.aitanskillsdemo.data.field.FieldRepository

// Single shared instances so every ViewModel works with the same data
object AppContainer {

    // The one field every screen reads from and changes
    val fieldRepository = FieldRepository()

    // Orders for the drones on that same field
    val droneCommands = DroneCommands(fieldRepository)

    // Moves time forward on that same field
    val simulationClock = SimulationClock(fieldRepository)
}
