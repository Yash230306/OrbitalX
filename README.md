# OrbitalX — Space Mission Simulator

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Dependencies](https://img.shields.io/badge/Dependencies-Zero%20(Pure%20JavaSE)-blue.svg)](https://docs.oracle.com/en/java/javase/17/)
[![GUI](https://img.shields.io/badge/GUI-Swing%20%2F%20Java2D-green.svg)](https://en.wikipedia.org/wiki/Swing_(Java))
[![Documentation](https://img.shields.io/badge/Documentation-User%20%26%20OOP%20Guides-purple.svg)](#-official-documentation)
[![Architecture](https://img.shields.io/badge/Design-Clean%20OOP%20%2B%20SOLID-cyan.svg)](docs/OOP_GUIDE.md)

**OrbitalX** is an aerospace mission-control simulator, telemetry monitor, and autonomous recovery testbed built from scratch with pure Java 17. It models real-time spacecraft physics, environmental hazards, sensor streams, telemetry anomalies, autonomous recovery chains, crew interventions, and time-travel replay without external runtime dependencies.

---

## 📚 Official Documentation

The project maintains comprehensive documentation across technical architecture, operations, and academic deliverables:

| Document | Description | Formats |
| :--- | :--- | :--- |
| 📘 **User Operations Guide** | Complete flight operations manual: installation, first mission walkthrough, in-depth guide to all 10 cockpit screens, flight maneuvers, emergency recovery, keyboard shortcuts, and CLI commands. | [**Markdown**](docs/USER_GUIDE.md) · [**Printable PDF**](docs/USER_GUIDE.pdf) · [**HTML**](docs/USER_GUIDE.html) |
| 🏛️ **OOP Principles Guide** | Exhaustive 28-page Object-Oriented design guide: Classes & Objects, Encapsulation, Abstraction, Inheritance, Polymorphism, Relationships, Interfaces, SOLID principles, layered architecture, and 12+ GoF patterns. | [**Markdown**](docs/OOP_GUIDE.md) · [**Printable PDF**](docs/OOP_GUIDE.pdf) · [**HTML**](docs/OOP_GUIDE.html) |
| 📄 **Final Project Report<br>(Group 39)** | Complete academic and technical report covering requirements, OOA/D, CRC cards, 4 pillars of OOP, class relationships, 14 UML models, SOLID principles, 16 GoF design patterns, exception handling, simulation loop, testing matrix (108/108 PASS), and appendices. | [**Printable PDF**](docs/Group_39-ProjectReport.pdf) · [**Markdown**](docs/PROJECT_REPORT.md) |
| 📊 **Presentation Slides<br>(Group 39)** | 32-slide presentation detailing system architecture, three-layer separation, Gang-of-Four design patterns, Chain of Responsibility, sensor decorators, state machines, headless testing, and UI showcase. | [**Printable PDF**](docs/Group_39-PPT.pdf) · [**Markdown**](docs/PRESENTATION.md) |

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
* **Autonomous Resolution Chain**: Implements a four-tier Chain of Responsibility (`Self-Healing Circuits` &rarr; `Timed Autopilot` &rarr; `Crew Tasking` &rarr; `Ground Escalation`).
* **Object-Oriented Excellence**: Clean Layered Architecture with strict Dependency Inversion (GUI and physics communicate exclusively through the `SimulationController` abstraction).

---

## Directory Structure

```text
OrbitalX/
├── pom.xml                      # Standard Maven build descriptor (Java 17 target)
├── compile.bat                  # Windows zero-dependency compilation script
├── run.bat                      # Windows unified runtime launcher
├── compile.sh                   # Linux/macOS compilation script
├── run.sh                       # Linux/macOS launcher script
├── LICENSE                      # Project license (MIT)
├── README.md                    # Project overview and quick start
├── docs/                        # Complete Documentation, Guides & Visual Assets
│   ├── USER_GUIDE.md            # 📘 Complete User Operations Manual
│   ├── USER_GUIDE.pdf           # 📄 Printable User Guide PDF (with cockpit screenshots)
│   ├── USER_GUIDE.html          # 🌐 Interactive standalone User Guide
│   ├── OOP_GUIDE.md             # 🏛️ Comprehensive OOP Principles & Architecture
│   ├── OOP_GUIDE.pdf            # 📄 Printable 28-Page OOP Principles PDF
│   ├── OOP_GUIDE.html           # 🌐 Interactive standalone OOP Guide
│   ├── Group_39-ProjectReport.pdf # 📄 Final Printable Project Report PDF
│   ├── Group_39-PPT.pdf         # 📊 Final Printable Presentation Slides PDF
│   ├── PROJECT_REPORT.md        # 📝 Markdown Project Report
│   ├── PRESENTATION.md          # 📽️ Markdown Presentation Deck
│   ├── puml/                    # PlantUML domain diagrams
│   └── screenshots/             # 80-image capture matrix of all views & themes
└── src/main/java/com/orbitalix/ # Modular Java 17 source tree
    ├── action/                  # Command pattern: corrective actions
    ├── advisor/                 # Diagnostic rules and recommendation engine
    ├── alert/                   # Event notification & dispatch
    ├── api/                     # Public contracts, records, and enums (MVC Boundary)
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
# Compile and package into a JAR
mvn clean package

# Run via Maven
mvn exec:java
```

---

## Cockpit Shortcuts & Hotkeys

| Shortcut | Function | Description |
| :--- | :--- | :--- |
| `1` .. `0` | **Console Select** | Directly switches between the 10 navigation views |
| `Space` | **Launch / Pause** | Toggles simulation clock execution |
| `Up` / `Down` | **Throttle Control**| Adjusts main engine throttle up/down by 5% |
| `Ctrl + K` | **Quick Command** | Focuses the command input field |
| `Ctrl + S` | **Screenshot** | Captures the active viewport to `out/screenshots` |
| `F` or `F11` | **Fullscreen** | Toggles borderless mission-control fullscreen mode |
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

## Verification & Self-Test

OrbitalX includes a deterministic headless test runner validating mathematical determinism, failure recovery survival, and view rendering across 108 test permutations:

```powershell
java -cp out com.orbitalix.app.Main --selftest
```

---

## Team Roles & Division of Responsibilities

| Team Member | Project Role | Core Responsibilities & Deliverables |
| :--- | :--- | :--- |
| **Member A (Bharat)** | Lead Software Architect & Core Engine Developer | • Full Java 17 deterministic simulation engine & clock tick loop (Tasks A1–A7)<br>• 10 Desktop Mission Control cockpit consoles in Swing / Java2D (Tasks A8–A14)<br>• Real-time 3D spacecraft rasterizer with dual-source space lighting<br>• Four-tier autonomous recovery engine (Self-Healing, Autopilot, Crew, Ground)<br>• Automated 108-permutation regression & self-test suite (`--selftest`) (Tasks A15–A16) |
| **Member B (Kaushal Gond)** | Systems Analyst, QA & Technical Documentation Lead | • System requirements specification & UML modeling (`docs/puml/`) (Tasks B1–B3)<br>• Flight Operations Manual & User Guide ([`docs/USER_GUIDE.md`](docs/USER_GUIDE.md))<br>• Final Project Report ([`docs/Group_39-ProjectReport.pdf`](docs/Group_39-ProjectReport.pdf))<br>• Project Architecture Presentation Slides ([`docs/Group_39-PPT.pdf`](docs/Group_39-PPT.pdf)) (Tasks B5–B6)<br>• Viva examination Q&A preparation (Appendix C) (Task B7)<br>• Multi-environment verification testing on fresh machine clones (Task B8) |

---

## License

This project is licensed under the [MIT License](LICENSE).
