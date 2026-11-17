# OrbitalX — Space Mission Simulator
## Executive Viva Q&A & OOP Architectural Cheat Sheet

> **Document Purpose**: Quick-reference guide for external examiners, academic evaluators, and project viva questions. Contains precise, technically accurate, one-paragraph answers referencing exact classes and design patterns in pure Java 17.

---

### 1. Object-Oriented Programming (OOP) Core Pillars

#### ❓ Q1: Where and how is Polymorphism implemented in OrbitalX?
**Answer:** Polymorphism is primarily implemented via:
1. **Dynamic Method Dispatch in Physics:** The abstract class `Subsystem` declares `public abstract void applyPhysics(double deltaSeconds)`. Subclasses (`PropulsionSubsystem`, `PowerSubsystem`, `LifeSupportSubsystem`, `ThermalSubsystem`) override this method with specific mathematical models (e.g., fuel mass flow, solar irradiance, radiator heat dissipation). The `SimulationEngine` iterates over `List<Subsystem>` invoking `applyPhysics()` without knowing the concrete subsystem type.
2. **Command Pattern Polymorphism:** `CorrectiveAction.execute(Spacecraft craft)` dynamically executes remediation algorithms (`ReroutePowerAction`, `VentCoolantAction`, `SwitchToBackupGyroAction`).
3. **Sensor Polymorphism:** `BaseSensor.readValue()` dynamically injects drift, gaussian noise, or failure values depending on sensor subclass.

#### ❓ Q2: How does OrbitalX enforce Encapsulation and Data Invariance?
**Answer:** 
- All state variables in `Spacecraft`, `Subsystem`, and `Resource` are marked `private` or `protected`.
- State mutation is restricted to validated domain methods (e.g., `Resource.consume()`, `Spacecraft.injectFault()`), preventing illegal states (such as negative battery charge or non-physical pressures).
- **Defensive Copying:** Methods returning collections (e.g., sensor lists, crew members) return `Collections.unmodifiableList(...)`.
- **Java 17 Records:** Telemetry transferred across architectural boundaries uses immutable records (`SimulationSnapshot`), ensuring that UI rendering threads cannot alter physics state or read half-computed intermediate values.

#### ❓ Q3: How is Abstraction applied across the architecture?
**Answer:** High-level abstractions decouple the complex aerospace physics calculations from presentation and telemetry consumption.
- `SystemComponent` interface abstracts anything with a lifecycle, status, and power draw (`Subsystem`, `Resource`, `CrewMember`).
- `Sensor` interface hides physical measurement calculations behind a clean `double readValue()` contract.
- The cockpit GUI views never perform differential equations or manage timing ticks; they interact purely through high-level abstractions provided by the controller.

#### ❓ Q4: Where is Inheritance used, and how does the project avoid deep/brittle class hierarchies?
**Answer:** Inheritance is used sparingly and cleanly with abstract base classes capturing common behavior:
- `Subsystem` (abstract base) -> specialized physical subsystems.
- `BaseSensor` (abstract base) -> `GyroSensor`, `TemperatureSensor`, `VoltageSensor`.
- `CrewMember` (abstract base) -> `Commander`, `Pilot`, `FlightEngineer`, `MedicalOfficer`.
- `ResponseHandler` (abstract base for Chain of Responsibility) -> `SelfHealingHandler`, `AutopilotHandler`, `CrewHandler`, `EscalationHandler`.
Hierarchies are kept shallow (max depth 2–3) and favor composition (`Spacecraft` *has-a* `CrewRoster`, `Subsystem` *has-a* `Sensor`).

---

### 2. SOLID Design Principles

#### ❓ Q5: How does Dependency Inversion Principle (DIP) work in the GUI and Engine boundary?
**Answer:** 
- **High-level modules** (e.g., `MainWindow`, `DashboardView`, `OrbitView`) do **not** depend on low-level engine classes (`SimulationEngine`).
- **Low-level modules** do **not** depend on the GUI.
- **Both depend on abstractions:** The presentation layer communicates solely through the `SimulationController` interface (`setThrottle()`, `pause()`, `getSnapshot()`). `DefaultSimulationController` implements this interface and wraps `SimulationEngine`.
- **Key Advantage:** Enables running the engine completely headless in `--selftest` mode without loading any GUI or graphics libraries.

#### ❓ Q6: How does OrbitalX follow the Single Responsibility Principle (SRP)?
**Answer:** Each class has exactly one clear reason to change:
- `SimulationEngine`: Solely responsible for stepping simulation physics deterministically.
- `DetectionEngine`: Solely responsible for telemetry anomaly identification.
- `AdvisorEngine`: Solely responsible for evaluating remediation rules and generating recommendations.
- `EventBus`: Solely responsible for message publishing and event subscriber dispatch.
- `ViewRegistry`: Solely responsible for registering, caching, and switching GUI views.

#### ❓ Q7: How is the Open/Closed Principle (OCP) respected when adding new spacecraft faults?
**Answer:** The fault system is open for extension but closed for modification. New emergencies (e.g., Solar Storm, Micrometeoroid Breach) are created by subclassing `Fault` and registering them in `FaultFactory` without modifying `SimulationEngine` or core physics loops.

---

### 3. Classical GoF Design Patterns

| Pattern | Role / Implementation Class | Purpose in OrbitalX |
| :--- | :--- | :--- |
| **State** | `SpacecraftStatus` (`NOMINAL`, `DEGRADED`, `CRITICAL`, `SAFE_MODE`, `TERMINAL_LOSS`) | Dynamically alters thruster availability, power distribution, and alarm systems based on craft health. |
| **Command** | `CorrectiveAction` interface (`ReroutePowerAction`, `VentCoolantAction`, `RebootComputerAction`) | Encapsulates recovery actions as first-class objects allowing queuing, undo/retry, and countdown timers. |
| **Observer** | `EventBus`, `Topic<T>`, `MissionEventListener` | Strongly-typed pub/sub mechanism broadcasting flight events (`FaultDetectedEvent`, `MissionStatusEvent`) without tight coupling. |
| **Chain of Responsibility** | `ResponseHandler` (`SelfHealingHandler` -> `AutopilotHandler` -> `CrewHandler` -> `EscalationHandler`) | Cascading autonomous recovery hierarchy that passes unhandled anomalies down the line. |
| **Memento** | `History.java`, `SimulationSnapshot` | Captures historical state records to support interactive time-travel scrubbing in the Replay Console. |
| **Factory Method** | `FaultFactory.createFault(FaultType)` | Decouples hazard creation from simulation runtime logic. |
| **Visitor** | `ReportVisitor` (`HtmlReportVisitor`, `TextReportVisitor`) | Compiles telemetry statistics into HTML and Markdown debrief reports without modifying data structures. |
| **Strategy** | `AnomalyDetector` (`ThresholdDetector`, `RateOfChangeDetector`, `ZScoreDetector`) | Swappable anomaly detection algorithms dynamically evaluating telemetry. |
| **Composite** | `Spacecraft` -> `Subsystem` -> `Sensor` / `Resource` | Treats individual components and aggregated subsystem trees uniformly through `SystemComponent`. |

---

### 4. Autonomous Recovery & Avionics Architecture

#### ❓ Q8: What are the 4 tiers of the Autonomous Recovery Chain, and how do they interact?
**Answer:** When an anomaly occurs, the Chain of Responsibility executes in sequence:
1. **Tier 1 (Self-Healing):** Instant hardware-level reflex (e.g., tripped breaker resets, backup gyro engage). If resolved, chain terminates.
2. **Tier 2 (Timed Autopilot):** Software advisor diagnoses systemic failure, schedules a `CorrectiveAction`, and displays a countdown on the HUD. If the pilot does not override, action executes automatically.
3. **Tier 3 (Crew Intervention):** If autopilot cannot resolve the issue or manual repairs are needed, specialist crew members (Commander, Flight Engineer, Medic) are assigned to physical tasks with role-based skill modifiers.
4. **Tier 4 (Ground Control Escalation):** Uplink request sent to Earth Ground Control, subject to speed-of-light radio delay (seconds to minutes). Ground sends back master patch commands.

#### ❓ Q9: What are the 3 Anomaly Detection strategies in the advisor module?
**Answer:**
1. **ThresholdDetector:** Checks whether telemetry breaches hard upper/lower safety bounds (e.g., Temp > 400°C, O₂ < 18%).
2. **RateOfChangeDetector:** Evaluates numerical derivative ($dV/dt$) over sliding time intervals to detect subtle rapid leaks (e.g., fuel tank puncture) before critical thresholds are reached.
3. **ZScoreDetector:** Computes rolling mean and standard deviation to detect statistical outliers (> 3.0$\sigma$), isolating noisy sensors and electromagnetic interference.

#### ❓ Q10: How does OrbitalX model realistic space communication delay?
**Answer:** The `RadioChannel` interface models transmission latency. In `DelayedRadioChannel`, telemetry packets and ground commands are stored in a timestamped FIFO queue with simulated propagation delays based on astronomical distances (e.g., Earth-Moon ~1.3s, Earth-Mars 3–22 minutes). This models why autonomous on-board recovery is non-negotiable for deep-space flight.

---

### 5. Testing, Verification & Technical Execution

#### ❓ Q11: What is the `--selftest` suite, and what does it prove?
**Answer:** Running `java -cp out com.orbitalix.app.Main --selftest` executes a comprehensive automated verification matrix of **108 distinct test runs**:
- 12 Mission Scenarios (Nominal Flight, Fuel Leak, Engine Trouble, Power Crisis, Hull Breach, Solar Storm, Cascading Failure, etc.)
- 3 Difficulty Tiers (`EASY`, `NORMAL`, `HARD`)
- 3 Autonomy Modes (`MANUAL`, `TIMED_AUTOPILOT`, `FULL_AUTO`)
- **Key Finding:** In headless `MANUAL` mode, lethal faults (Fuel Leak, Hull Breach) cause mission loss due to lack of pilot action. In `TIMED_AUTOPILOT` and `FULL_AUTO` modes, the autonomy chain autonomously mitigates the faults and reaches destination with positive fuel margins, proving 100% self-healing capability (`SELFTEST PASS`).

#### ❓ Q12: How is GUI thread safety ensured in Swing without deadlocks?
**Answer:**
- The background simulation loop runs on a dedicated simulation thread and never touches Swing UI widgets directly.
- The engine publishes immutable `SimulationSnapshot` records.
- UI updates are dispatched to the Swing Event Dispatch Thread (EDT) using `SwingUtilities.invokeLater(...)`.
- Because snapshots are immutable records, rendering views can read telemetry without locking or race conditions.

#### ❓ Q13: What external third-party dependencies are used in the backend?
**Answer:** **Zero external dependencies.** The entire application is built from first principles using standard Java 17 libraries (`java.awt`, `javax.swing`, `java.time`, `java.nio`, `java.util.concurrent`), guaranteeing zero external security vulnerabilities, instant compilation, and zero-setup deployment.
