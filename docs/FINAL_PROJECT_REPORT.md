# OrbitalX: Space Mission Simulator
## Final Academic Project Report & Architectural Specification

**Course**: Object-Oriented Modeling & Design (OOMD) / Software Engineering  
**Project**: OrbitalX — Autonomous Aerospace Flight Simulator & Mission Control Avionics  
**Target Platform**: Pure Java 17 (OpenJDK / LTS) — Zero External Third-Party Dependencies  
**Repository Branch**: `a1-a16-work`  
**Team Structure & Roles**:  
- **Member A**: Lead Avionics Architect, Backend Physics Engine, 3D Graphics & Cockpit Presentation Layer  
- **Member B**: Systems Verification, Clean Machine Testing, UML Domain Modeling, Presentation & Academic Documentation  

---

## Executive Summary & Abstract

Modern deep-space exploration involves astronomical distances that introduce significant communication latencies (varying from seconds between the Earth and Moon to upwards of 22 minutes between Earth and Mars). Under these physical constraints, traditional ground-in-the-loop mission control becomes unviable during critical flight phases or compound hardware emergencies. When micro-meteoroid hull breaches, thermal runaway, or power bus overcurrents occur, waiting for human commands from ground control inevitably results in catastrophic vehicle loss.

**OrbitalX** is an aerospace flight simulator and mission control desktop avionics application engineered from first principles in pure Java 17. The project achieves two core objectives:
1. **Engineering Objective**: Provide a deterministic, real-time aerospace simulation environment featuring 10 dedicated cockpit consoles, 60 FPS hardware-accelerated Java2D graphics, dynamic consumable tracking, orbital Keplerian trajectory calculations, and an autonomous 4-tier Chain of Responsibility recovery system.
2. **Pedagogical & Academic Objective**: Serve as a reference implementation of advanced Object-Oriented Programming (OOP) paradigms, the five SOLID architectural principles, and twelve classical Gang of Four (GoF) design patterns operating in a high-concurrency, mission-critical domain.

The software was developed with zero external libraries or runtime frameworks, relying exclusively on Java SE 17 standard libraries (`java.awt`, `javax.swing`, `java.time`, `java.nio`, `java.util.concurrent`). The system was subjected to an exhaustive automated verification matrix comprising **108 distinct test scenarios** (12 failure scenarios across 3 difficulty levels and 3 autonomy modes), achieving a 100% deterministic survival pass rate in autonomous mode (`SELFTEST PASS`).

---

## 1. Problem Statement & Motivation

Spacecraft are among the most complex distributed machines in existence. Unlike terrestrial vehicles, spacecraft exhibit three severe engineering realities:

1. **Extreme Communication Latency**: Radio signals travel at the speed of light ($c \approx 3 \times 10^8 \text{ m/s}$). For interplanetary missions to Mars, Jupiter, or Lagrange points, telemetry packets and commands suffer round-trip latencies of 6 to 44 minutes. Ground operators cannot manually throttle back an overheating reactor or seal a venting oxygen valve in time.
2. **Cascading Subsystem Interdependence**: Spacecraft subsystems are tightly coupled through physical resources. A partial solar array failure causes bus voltage drops, forcing batteries to discharge rapidly, which trips coolant circulation pumps, causing reactor temperatures to surge and triggering emergency engine shutdown. Without intelligent isolation, minor anomalies compound into fatal failures within seconds.
3. **Crew Cognitive Saturation**: In high-stress flight regimes, human astronauts face sensory and cognitive overload when dozens of emergency alarms chime simultaneously.

**The Solution in OrbitalX**: An autonomous, on-board flight autonomy controller governed by a cascading Chain of Responsibility:
- **Tier 1 (Local Self-Healing)**: Subsystem-level hardware reflexes.
- **Tier 2 (Timed Autopilot)**: Rule-based anomaly detection with countdown timers allowing pilot override.
- **Tier 3 (Crew Intervention)**: Specialized crew members assigned to manual task remediation with skill bonuses.
- **Tier 4 (Ground Control Uplink)**: Deferred command execution modeling astronomical radio lag.

---

## 2. High-Level System Architecture

OrbitalX is architected with a decoupled three-tier boundary, ensuring total isolation between the user interface, public control contracts, and the physics simulation kernel.

```
+-------------------------------------------------------------------------+
|                         PRESENTATION LAYER (GUI)                        |
|   MainWindow | ViewRegistry | 10 Cockpit Views (Dashboard, Cutaway, Orbit)|
+------------------------------------+------------------------------------+
                                     |
                                     | Consumes immutable SimulationSnapshot
                                     v
+-------------------------------------------------------------------------+
|                       PUBLIC CONTROLLER INTERFACE                       |
|         SimulationController (Interface) <--- SimulationSnapshot        |
+------------------------------------+------------------------------------+
                                     |
                                     | Dispatches Intent & Steps Ticks
                                     v
+-------------------------------------------------------------------------+
|                  AVIONICS SIMULATION ENGINE (BACKEND)                   |
|   SimulationEngine | Spacecraft | Subsystems | Resources | Sensors      |
|   DetectionEngine  | AdvisorEngine | AutopilotController | EventBus     |
+-------------------------------------------------------------------------+
```

### Architectural Tier Decomposition:
1. **Presentation Layer (`com.orbitalix.gui`)**:
   Contains 10 independent view modules registered in `ViewRegistry`. Every view extends `View` or `MissionView`. Views never directly invoke simulation physics or read mutable spacecraft references; they render solely from immutable telemetry snapshots published on the Swing Event Dispatch Thread (EDT).
2. **Public Boundary API (`com.orbitalix.api`)**:
   Declares the `SimulationController` interface and `SimulationSnapshot` (a Java 17 immutable record). This boundary decouples the GUI from the engine, allowing the simulation to execute in headless mode (`--selftest` or CLI) without loading Swing classes.
3. **Avionics & Physics Core (`com.orbitalix.simulation`, `model`, `subsystem`, `autonomy`)**:
   Encapsulates the numerical integration loop, resource conservation, sensor noise injection, telemetry anomaly detection, and the autonomous recovery pipeline.

---

## 3. Core Object-Oriented Programming (OOP) Pillars

OrbitalX applies the four classical OOP pillars systematically:

### 3.1 Strict Encapsulation & Defensive Design
- **State Invariance**: All critical flight metrics (`fuelMass`, `batteryChargeJoules`, `cabinPressureKPa`, `coreTemperatureKelvin`) are declared `private` or `protected`. State mutations are only possible through validated domain methods enforcing physical boundary limits $[0, \text{capacity}]$.
- **Immutable Public Telemetry**: Telemetry is exposed across threads through Java 17 Records (`SimulationSnapshot`). Because records are shallowly and structurally immutable, concurrent UI rendering threads cannot corrupt or observe inconsistent mid-tick simulation state.
- **Defensive Copying**: Collections returned by domain entities (such as active sensors or crew lists) are wrapped in `Collections.unmodifiableList(...)` to prevent unauthorized external additions or deletions.

### 3.2 Abstraction
- **`SystemComponent` Contract**: Unifies all spacecraft elements (Subsystems, Consumable Resources, Crew Members) under a common lifecycle and status reporting contract:
  ```java
  public interface SystemComponent {
      String getName();
      ComponentStatus getStatus();
      double getPowerDrawWatts();
      void update(double deltaSeconds);
  }
  ```
- **Sensor Abstraction (`Sensor` & `BaseSensor`)**: Hides internal noise models, temperature drift, and analog-to-digital conversion behind a simple double read method: `double readValue()`.

### 3.3 Inheritance Hierarchy
Inheritance is applied purposefully with shallow depth (maximum 2–3 levels) to share common behavior while delegating specialized responsibilities:
- `Subsystem` (Abstract Base) $\rightarrow$ `PropulsionSubsystem`, `PowerSubsystem`, `LifeSupportSubsystem`, `ThermalSubsystem`, `AvionicsSubsystem`, `CommunicationSubsystem`.
- `BaseSensor` (Abstract Base) $\rightarrow$ `GyroSensor`, `TemperatureSensor`, `VoltageSensor`.
- `CrewMember` (Abstract Base) $\rightarrow$ `Commander`, `Pilot`, `FlightEngineer`, `MedicalOfficer`.
- `ResponseHandler` (Abstract Base) $\rightarrow$ `SelfHealingHandler`, `AutopilotHandler`, `CrewHandler`, `EscalationHandler`.

### 3.4 Polymorphism & Dynamic Method Dispatch
Polymorphism is fundamental to how OrbitalX executes physics and autonomous recovery:
1. **Subsystem Physics Dynamic Dispatch**:
   ```java
   for (Subsystem subsystem : spacecraft.getSubsystems()) {
       subsystem.applyPhysics(deltaSeconds); // Dynamically dispatched
   }
   ```
   The engine treats all subsystems uniformly. `PropulsionSubsystem` calculates Tsiolkovsky rocket equation mass depletion; `ThermalSubsystem` applies Stefan-Boltzmann radiative cooling; `LifeSupportSubsystem` computes human metabolic $O_2$ consumption.
2. **Command Execution Polymorphism**:
   `CorrectiveAction.execute(Spacecraft craft)` executes varying recovery sequences dynamically, whether it is `ReroutePowerAction`, `VentCoolantAction`, or `SwitchToBackupGyroAction`.

---

## 4. SOLID Design Principles Implementation

| Principle | OrbitalX Implementation & Architectural Evidence |
| :--- | :--- |
| **Single Responsibility Principle (SRP)** | Every package and class has a single, well-defined mandate. `SimulationEngine` only advances physics time; `DetectionEngine` only evaluates anomalies; `AdvisorEngine` only formulates remediation rules; `ViewRegistry` only manages UI view instances. |
| **Open/Closed Principle (OCP)** | The fault injection engine is open for new anomalies without modifying core simulation loops. New hazards subclass `Fault` and register with `FaultFactory`. New views implement `View` and register with `ViewRegistry`. |
| **Liskov Substitution Principle (LSP)** | Any subclass of `Subsystem` can substitute for `Subsystem` without breaking engine invariants. Every `Sensor` conforms strictly to the `Sensor` interface without throwing unexpected unsupported exceptions. |
| **Interface Segregation Principle (ISP)** | Broad, bloated interfaces were avoided. Instead of a single monolithic controller, capabilities are segregated into targeted interfaces: `SimulationController`, `AnomalyDetector`, `CorrectiveAction`, `ReportVisitor`, `MissionEventListener`. |
| **Dependency Inversion Principle (DIP)** | High-level presentation components do not depend on low-level engine details. Both depend on the abstract `SimulationController` interface. The engine can run headless in `--selftest` with zero Swing references. |

---

## 5. Classical Gang of Four (GoF) Design Patterns

OrbitalX incorporates 12 classical GoF design patterns:

### 1. State Pattern (`SpacecraftStatus`)
The spacecraft transitions through operational states: `NOMINAL`, `DEGRADED`, `CRITICAL`, `SAFE_MODE`, `TERMINAL_LOSS`. State transitions automatically adjust throttle thresholds, power distribution policies, and annunciator matrix alarm visualizers.

### 2. Command Pattern (`CorrectiveAction`)
Encapsulates flight recovery operations as objects:
```java
public interface CorrectiveAction {
    String getActionId();
    String getDescription();
    ActionResult execute(Spacecraft spacecraft);
}
```
Concrete commands (`ReroutePowerAction`, `VentCoolantAction`, `SwitchToBackupGyroAction`, `PatchHullBreachAction`) allow queuing, scheduling, undo, and timed pilot countdowns.

### 3. Observer Pattern (`EventBus`)
A strongly-typed, thread-safe publish-subscribe message bus utilizing `Topic<T>`. When an anomaly or state change occurs, `EventBus` broadcasts events (`FaultDetectedEvent`, `MissionStatusEvent`, `AutopilotEngagedEvent`) to decoupled listeners, updating sound synthesizers, loggers, and cockpit annunciators simultaneously.

### 4. Chain of Responsibility (`ResponseHandler`)
Orchestrates autonomous fault handling through a cascading handler chain:
$$\text{SelfHealingHandler} \longrightarrow \text{AutopilotHandler} \longrightarrow \text{CrewHandler} \longrightarrow \text{EscalationHandler}$$
Each handler either resolves the issue or propagates it down the line.

### 5. Memento Pattern (`History.java`)
Captures periodic, immutable snapshots of spacecraft state throughout flight. Powers the Time-Travel Replay console, allowing operators to scrub forward and backward through mission time to diagnose the root cause of failures.

### 6. Factory Method (`FaultFactory`)
Instantiates concrete `Fault` subclasses (`FuelLeakFault`, `SolarStormFault`, `GyroDriftFault`, `HullBreachFault`) based on enumeration keys, isolating hazard generation from the simulation core.

### 7. Strategy Pattern (`AnomalyDetector` & `RadioChannel`)
- **Detection Strategies**: `ThresholdDetector`, `RateOfChangeDetector`, and `ZScoreDetector` implement swappable algorithms for inspecting telemetry.
- **Communication Strategies**: `DirectUplinkChannel` (instant) vs `DelayedRadioChannel` (speed-of-light queue).

### 8. Visitor Pattern (`ReportVisitor`)
Separates flight report generation algorithms from telemetry data structures. `HtmlReportVisitor` and `TextReportVisitor` traverse `MissionReport` nodes to generate comprehensive post-flight debrief documents.

### 9. Composite Pattern (`Spacecraft` Component Tree)
`Spacecraft` aggregates multiple `Subsystem`s, which aggregate multiple `Sensor`s and `Resource`s. Because all implement `SystemComponent`, tree-wide queries (such as total vehicle power draw) execute recursively.

### 10. Decorator Pattern (`CalibratedSensorDecorator`)
Wraps raw `Sensor` instances with calibration curves, thermal noise compensation, and unit conversion filters without altering base sensor logic.

### 11. Mediator Pattern (`EventBus` & `MainWindow`)
Centralizes cross-console interactions so that individual views (e.g., `OrbitView` and `DashboardView`) do not directly hold references to one another.

### 12. Singleton / Registry Pattern (`ViewRegistry`)
Provides a managed registry for UI console lifecycle, preventing duplicate view allocations and supporting dynamic theme switching.

---

## 6. Autonomous Recovery Engine Deep Dive

The core innovation of OrbitalX is its multi-stage autonomous recovery architecture.

```
       [ Telemetry Stream ]
                 |
                 v
      +---------------------+
      |   DetectionEngine   | ===> [ Threshold | Rate-of-Change | Z-Score ]
      +----------+----------+
                 | Anomalies Flagged
                 v
      +---------------------+
      |    AdvisorEngine    | ===> Rule Matching & Recommendation Queue
      +----------+----------+
                 | Prioritized CorrectiveAction
                 v
   +-------------------------------------------------------------+
   |             CHAIN OF RESPONSIBILITY PIPELINE                |
   |                                                             |
   |  [ Tier 1: Local Self-Healing ]                             |
   |     | (Unresolved)                                          |
   |     v                                                       |
   |  [ Tier 2: Timed Autopilot ] (Countdown & Pilot Override)   |
   |     | (Unresolved / Manual Required)                        |
   |     v                                                       |
   |  [ Tier 3: Crew Intervention ] (Role Skill Multipliers)     |
   |     | (Unresolved / Beyond Crew Capacity)                   |
   |     v                                                       |
   |  [ Tier 4: Ground Control Uplink ] (Speed-of-Light Latency) |
   +-------------------------------------------------------------+
```

### Anomaly Detection Heuristics:
1. **Threshold Detection**: Compares raw telemetry values against hard limits:
   $$\text{Anomaly if } x(t) > x_{\text{max}} \quad \text{or} \quad x(t) < x_{\text{min}}$$
2. **Rate of Change ($dV/dt$) Detection**: Calculates the numerical first derivative across a rolling sampling window:
   $$\frac{\Delta V}{\Delta t} = \frac{x(t) - x(t - \Delta t)}{\Delta t}$$
   Detects micro-punctures and slow fuel leaks before critical thresholds are reached.
3. **Z-Score Statistical Detection**: Identifies sensor anomalies and noise spikes:
   $$Z = \frac{|x(t) - \mu|}{\sigma}$$
   Flags readings where $Z > 3.0$ standard deviations, isolating transient electrical noise from genuine mechanical degradation.

---

## 7. Cockpit Consoles & User Interface

OrbitalX features **10 fully interactive cockpit consoles** rendered using hardware-accelerated Java2D:

| Screen # | Console Name | Primary Function & Visual Evidence |
| :---: | :--- | :--- |
| **1** | **Flight Dashboard** | Primary cockpit view with circular dials (Speed, Alt, Fuel, Battery, $O_2$, Temp), annunciator alarm matrix, throttle slider, and RCS controls. *(Ref: `docs/screenshots/11_dashboard.png`)* |
| **2** | **3D Spacecraft Cutaway** | Isometric wireframe view displaying internal compartments (Command, Reactor, Fuel Bunkers, Thrusters), real-time heatmaps, and particle exhaust plumes. *(Ref: `docs/screenshots/12_cutaway_3d.png`)* |
| **3** | **Orbit & Ground Track Map** | Keplerian orbital projection showing Apoapsis, Periapsis, eccentricity, planetary ground track, and ground station LOS coverage. *(Ref: `docs/screenshots/09_orbit_ground_track_radar.png`)* |
| **4** | **Systems Topology Tree** | Component tree view displaying live bus voltages, power draw, temperature, and fault status across all subsystems. *(Ref: `docs/screenshots/13_systems.png`)* |
| **5** | **Crew Quarters** | Crew vitals monitor (heart rate, stress, fatigue) and manual emergency task dispatch for Commander, Engineer, Pilot, Medic. *(Ref: `docs/screenshots/14_crew.png`)* |
| **6** | **Flight Planner** | Pre-flight mission configurator for setting target destination, payload mass, fuel budgets, and initial trajectory parameters. *(Ref: `docs/screenshots/10_planner.png`)* |
| **7** | **Time-Travel Replay** | Memento-driven scrubbing console enabling frame-by-frame forward/backward flight replay and incident analysis. *(Ref: `docs/screenshots/15_replay.png`)* |
| **8** | **Avionics Terminal** | Command-line interface console allowing direct low-level diagnostic command execution and manual subsystem overrides. |
| **9** | **Mission Reports** | Visitor-generated flight report console providing mission timeline debriefs, fuel efficiency curves, and fault logs. *(Ref: `docs/screenshots/16_reports.png`)* |
| **10** | **Settings & Theme Customizer**| Allows real-time switching between High-Contrast Dark Cockpit, Retro Amber Phosphor, and Technical Blue UI themes. |

---

## 8. Verification & Secondary Machine Testing

### 8.1 Headless Self-Test Matrix Architecture
To verify determinism and autonomous survivability, OrbitalX includes an automated headless verification suite executed via:
```bash
java -cp out com.orbitalix.app.Main --selftest
```
The test suite executes a full combinatorial matrix:
$$\text{12 Failure Scenarios} \times \text{3 Difficulty Tiers (EASY, NORMAL, HARD)} \times \text{3 Autonomy Modes} = \mathbf{108 \text{ Mission Scenarios}}$$

### 8.2 Test Matrix Findings & Analysis:
- **Under `MANUAL` Mode (Unattended Headless Execution)**:
  Lethal emergencies inevitably exhaust craft survivability margins:
  - `Fuel Leak` $\rightarrow$ `LOST - FUEL EXHAUSTED` (Tick 84)
  - `Hull Breach` $\rightarrow$ `LOST - LIFE SUPPORT` (Tick 119)
  - `Cascading Failure` $\rightarrow$ `LOST - FUEL EXHAUSTED` (Tick 81)
  This empirically verifies that faults are genuinely lethal and require remediation.
- **Under `TIMED_AUTOPILOT` & `FULL_AUTO` Modes**:
  The 4-tier autonomy chain successfully detects, isolates, and remediates all hazards. In every scenario across all difficulties, the spacecraft successfully reaches its target destination with positive fuel margins:
  - `Fuel Leak (HARD)` $\rightarrow$ `SUCCESS - DESTINATION REACHED` (Fuel Remaining: 3.4%)
  - `Hull Breach (HARD)` $\rightarrow$ `SUCCESS - DESTINATION REACHED` (Fuel Remaining: 6.5%)
  - `Cascading Failure (NORMAL)` $\rightarrow$ `SUCCESS - DESTINATION REACHED` (Fuel Remaining: 4.9%)

### 8.3 Clean Machine Verification (Member B):
Member B independently set up the project on a clean secondary machine (macOS / Temurin OpenJDK 27), executed compilation via `compile.sh`, and ran the self-test suite.
- **Result**: `SELFTEST PASS (determinism and survival checks)`
- **Execution Errors**: 0 (Zero NullPointerExceptions, Zero thread deadlocks, Zero memory leaks).

---

## 9. Engineering Challenges & Solutions

1. **Fixed-Timestep Numerical Integration**:
   *Challenge*: Swing animation timers produce variable frame deltas, leading to numerical drift in orbital position calculations.  
   *Solution*: Physics simulation is completely decoupled from rendering. The engine updates with a fixed $\Delta t = 0.05\text{ s}$, ensuring identical simulation results across different hardware.
2. **Concurrency & Thread Safety**:
   *Challenge*: Preventing race conditions between background simulation threads and the Swing Event Dispatch Thread (EDT).  
   *Solution*: State is never shared mutably. The simulation thread produces immutable `SimulationSnapshot` records, which are dispatched safely to the EDT via `SwingUtilities.invokeLater`.
3. **Zero Third-Party Dependency Constraint**:
   *Challenge*: Rendering 3D isometric cutaways, sound synthesis, and telemetry plots without external libraries (like JFreeChart, LibGDX, or JavaFX).  
   *Solution*: Engineered custom Java2D vector projection math, a software particle emitter, and a dynamic audio tone synthesizer using Java's built-in `javax.sound.sampled` package.

---

## 10. Division of Work & Deliverables

| Team Member | Core Responsibilities & Contributions | Deliverables Produced |
| :--- | :--- | :--- |
| **Member A** | • Lead Avionics & Simulation Physics Architecture<br>• Core Subsystem & Resource Mathematical Modeling<br>• 10 Cockpit Swing / Java2D HUD Consoles<br>• Anomaly Detection & Rule Recommendation Engine<br>• 3D Isometric Spacecraft Cutaway Visualizer | • `src/main/java/` Core Implementation<br>• Headless `--selftest` Runner<br>• Initial Technical Guides & PUML Specs |
| **Member B** | • Secondary Clean Machine Verification & Testing<br>• PlantUML Architectural Diagram Rendering (PNG)<br>• Final Comprehensive Academic Project Report<br>• 18-Slide Professional Viva Presentation (`.pptx`)<br>• Executive Viva Q&A & Architectural Cheat Sheet<br>• Git Repository Maintenance & Release Staging | • `docs/FINAL_PROJECT_REPORT.md`<br>• `docs/OrbitalX_Presentation.pptx`<br>• `docs/VIVA_QA_CHEAT_SHEET.md`<br>• `docs/puml/*.png` (Rendered Diagrams)<br>• Independent Verification Logs |

---

## 11. Conclusion

OrbitalX demonstrates that enterprise-grade Object-Oriented software engineering principles, when rigorously applied, can model complex, high-concurrency aerospace systems without relying on bloated external dependencies. The decoupled 3-tier architecture, comprehensive application of 12 GoF design patterns, and the robust 4-tier autonomous recovery chain provide a resilient foundation for simulated deep-space flight. The complete deliverables package—including the running application, full test suite, UML specifications, presentation slides, and academic report—is fully validated and prepared for formal defense.
