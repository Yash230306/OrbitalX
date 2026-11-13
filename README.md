# OrbitalX — Spacecraft Mission Control & Autonomous Recovery Suite

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Dependencies](https://img.shields.io/badge/Dependencies-Zero%20(Pure%20JavaSE)-blue.svg)](https://docs.oracle.com/en/java/javase/17/)
[![GUI](https://img.shields.io/badge/GUI-Swing%20%2F%20Java2D-green.svg)](https://en.wikipedia.org/wiki/Swing_(Java))
[![Architecture](https://img.shields.io/badge/Design-Object--Oriented-purple.svg)](docs/OOP_GUIDE.md)

**OrbitalX** is a desktop aerospace mission-control simulator and autonomous recovery testbed built with pure Java 17. It models real-time spacecraft physics, environmental hazards, sensor streams, telemetry anomalies, autonomous recovery chains, crew interventions, and time-travel replay without external runtime dependencies.

---

## Highlights

* **Pure Java 17 Architecture**: Built entirely on standard Java libraries (`java.awt`, `javax.swing`, `java.nio`, `java.time`) — zero third-party dependencies, zero complex frameworks.
* **10 Desktop Mission Consoles**: 
  1. **Dashboard**: Primary avionics panel with 6 instrument arc gauges, vertical throttle lever, operating limit sliders, and dynamic Mission Advisor.
  2. **3D Cutaway**: Real-time 3D spacecraft rasterizer with dual-source space sunlight, Blinn-Phong specular reflections, 8 authentic aerospace modules, and tactical HUD telemetry.
  3. **Orbit Map**: Ground-track trajectory radar, orbital velocity vectors, communication delay cones, and debris threat corridors.
  4. **Flight Planner**: Maneuver sequencing, delta-V burn budgeting, and 30-tick what-if lookahead forecasts.
  5. **Systems Tree**: Component topology graph displaying failure cascades, sensor sparklines, and active fault badges.
  6. **Crew Quarters**: Astronaut life signs (Health, Stress, Radiation) and direct tasking (`EVA_REPAIR`, `MEDICAL_CARE`, `MANUAL_REBOOT`, `REST`).
  7. **Time-Travel Replay**: Memento-pattern timeline scrubber allowing rewinding and replaying mission events tick-by-tick.
  8. **Avionics Console**: Aviation terminal with command history and Tab completion.
  9. **Reports**: Single-click export of HTML5 mission dossiers and CSV telemetry spreadsheets.
  10. **Settings**: On-the-fly theme switching (Dark Mission, Lunar Light, Retro Amber, Cyberpunk Neon) and autonomy tuning.
* **Autonomous Resolution Chain**: Implements a four-tier Chain of Responsibility (`Self-Healing Circuits` → `Timed Autopilot` → `Crew Tasking` → `Ground Escalation`).
* **Object-Oriented Excellence**: Implements 12+ GoF design patterns (State, Command, Observer, Strategy, Chain of Responsibility, Visitor, Memento, Decorator, Composite, Factory, Prototype, Facade). See [**`docs/OOP_GUIDE.md`**](docs/OOP_GUIDE.md).

---

## Directory Structure

```text
OrbitalX/
├── compile.bat                  # Windows compilation script
├── run.bat                      # Windows launcher script
├── compile.sh                   # Linux/macOS compilation script
├── run.sh                       # Linux/macOS launcher script
├── pom.xml                      # Standard Maven build descriptor
├── docs/                        # Architecture guides, UML, screenshots, manuals
│   ├── OOP_GUIDE.md             # Comprehensive OOP Architecture & Design Patterns Guide
│   ├── OOP_CONCEPT_MAP.md       # Visual syllabus & pattern breakdown
│   ├── ARCHITECTURE.md          # Subsystem design & component overview
│   ├── BEGINNER_GUIDE.pdf       # Walkthrough manual with annotated cockpit diagrams
│   ├── puml/                    # PlantUML sequence and class diagrams
│   └── screenshots/             # 80-image capture matrix of all views & themes
└── src/main/java/com/orbitalix/ # Clean modular source tree
    ├── action/                  # Command pattern: corrective actions
    ├── advisor/                 # Diagnostic rules and recommendation engine
    ├── alert/                   # Event notification & dispatch
    ├── api/                     # Public contracts, records, and enums
    ├── app/                     # Main entry points (GUI and CLI)
    ├── autonomy/                # Chain of Responsibility: autopilot & self-healing
    ├── comms/                   # Communication channels & speed-of-light uplink delay
    ├── config/                  # Craft factories, destinations, and difficulties
    ├── crew/                    # Astronaut roles, skills, and command tasks
    ├── detection/               # Telemetry detectors (Threshold, RateOfChange, Z-Score)
    ├── event/                   # Central publish-subscribe EventBus
    ├── exception/               # Strongly-typed domain exceptions
    ├── gui/                     # Swing & Java2D cockpit views and rendering engine
    │   ├── render3d/            # Software 3D rasterizer, camera, matrix, and meshes
    │   └── theme/               # Cybernetic color palettes and styling
    ├── model/                   # Spacecraft avionics, physics, and subsystems
    ├── replay/                  # Memento history snapshots and replay controller
    ├── report/                  # Visitor pattern: HTML, CSV, and text reports
    ├── resource/                # Physical consumables (Fuel, Battery, Oxygen, Sealant)
    ├── scoring/                 # Grade calculations and mission achievements
    ├── sensor/                  # Decorator pattern: physical sensor telemetry & noise
    ├── simulation/              # Deterministic simulation engine and self-test harness
    ├── state/                   # State pattern: Nominal, Warning, Critical, Safe Mode
    └── util/                    # Circular ring buffers, deterministic RNG, clock
```

---

## Quick Start

### Prerequisites
- **Java 17 LTS or newer** (`java` and `javac` accessible on your `PATH`).

### Build & Run on Windows

```powershell
# Compile the project
.\compile.bat

# Launch the Mission Control GUI
.\run.bat

# Launch interactive CLI Console mode
.\run.bat --cli

# Execute the automated Self-Test suite (108 scenario matrix)
.\run.bat --selftest

# Generate headless screenshots of all views in all themes
.\run.bat --screenshots out\screenshots
```

### Build & Run on Linux / macOS

```bash
chmod +x *.sh

# Compile
./compile.sh

# Run Desktop GUI
./run.sh

# Run CLI mode
./run.sh --cli

# Run Self-Test
./run.sh --selftest
```

### Build with Apache Maven

```bash
# Compile and package
mvn clean package

# Run via Maven
mvn exec:java
```

---

## Cockpit Shortcuts & Hotkeys

| Shortcut | Function | Description |
| :--- | :--- | :--- |
| `0` .. `9` | **Console Select** | Directly switches between the 10 navigation views |
| `Space` | **Launch / Pause** | Toggles simulation clock execution |
| `Ctrl + K` | **Quick Command** | Focuses the command input field |
| `Ctrl + S` | **Screenshot** | Captures the active viewport to `out/screenshots` |
| `F11` | **Fullscreen** | Toggles borderless mission-control fullscreen mode |
| `Escape` | **Cancel** | Clears active prompt or closes modal overlays |

---

## Command Console (CLI)

The internal CLI terminal and standalone `--cli` mode support a comprehensive command set:

```text
help                                 List available commands
status                               Display current spacecraft telemetry
throttle <percent>                   Set main engine thrust (0..100)
inject <fault-type>                  Simulate a hardware failure (e.g. fuel-leak, overheat)
exec <recommendation-id>             Execute a diagnostic recommendation
dismiss <recommendation-id>          Dismiss an active recommendation
autonomy manual|timed|full           Adjust supervisory autonomy level
heal on|off                          Toggle automatic self-healing circuits
uplink on|off                        Toggle speed-of-light radio delay
limits [type value [override]]       View or override safety limits
crew                                 List astronaut status, health, and current task
task <name> <task>                   Assign astronaut task (EVA_REPAIR, MEDICAL_CARE, etc.)
preview <id> [ticks]                 Run what-if forward prediction
scenario <1-12>                      Load a pre-configured flight scenario
report                               Generate mission summary
quit                                 Exit console mode
```

---

## Documentation

- [**OOP Architecture Guide**](docs/OOP_GUIDE.md): In-depth 10-section breakdown of object-oriented principles, design patterns, and package topologies.
- [**OOP Concept Map**](docs/OOP_CONCEPT_MAP.md): Detailed syllabus alignment and design pattern inventory.
- [**Beginner's Flight Manual (PDF)**](docs/BEGINNER_GUIDE.pdf): Visual pilot's manual covering all displays, instruments, and procedures.
- [**Architectural Overview**](docs/ARCHITECTURE.md): Component diagrams, state machines, and data flow pipelines.

---

## License

This project is licensed under the [MIT License](LICENSE).
