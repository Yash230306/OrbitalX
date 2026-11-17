# Indian Institute of Information Technology Allahabad
## Object Oriented Modelling · Mini Project
# OrbitalX
### A Space Mission Simulator with Autonomous Fault Recovery, built in Java
## PROJECT REPORT

**Submitted by:**
| Name | Roll number |
| :--- | :--- |
| Bharat Upadhyay | IIT2025045 |
| Kaushal Gond | IIT2025042 |

**November 2026**

---

## Contents
- [Abstract](#abstract)
- [1. Introduction](#1-introduction)
  - [1.1 Background](#11-background)
  - [1.2 Problem statement](#12-problem-statement)
  - [1.3 Objectives](#13-objectives)
  - [1.4 Scope](#14-scope)
  - [1.5 Team and responsibilities](#15-team-and-responsibilities)
  - [1.6 Organisation of the report](#16-organisation-of-the-report)
- [2. Requirements](#2-requirements)
  - [2.1 Users and goals](#21-users-and-goals)
  - [2.2 Functional requirements](#22-functional-requirements)
  - [2.3 Non-functional requirements](#23-non-functional-requirements)
  - [2.4 Requirements traceability](#24-requirements-traceability)
  - [2.5 Constraints and assumptions](#25-constraints-and-assumptions)
- [3. Object-Oriented Analysis](#3-object-oriented-analysis)
  - [3.1 Finding classes: nouns and verbs](#31-finding-classes-nouns-and-verbs)
  - [3.2 Use case model](#32-use-case-model)
  - [3.3 Use case descriptions](#33-use-case-descriptions)
  - [3.4 CRC cards](#34-crc-cards)
- [4. Classes, Objects and Packages](#4-classes-objects-and-packages)
  - [4.1 Package structure](#41-package-structure)
  - [4.2 Kinds of types used](#42-kinds-of-types-used)
  - [4.3 Attributes, methods and constructors](#43-attributes-methods-and-constructors)
  - [4.4 Catalogue of domain objects](#44-catalogue-of-domain-objects)
  - [4.5 Object life cycle and object identity](#45-object-life-cycle-and-object-identity)
- [5. The Four Pillars of OOP](#5-the-four-pillars-of-oop)
  - [5.1 Encapsulation](#51-encapsulation)
  - [5.2 Abstraction](#52-abstraction)
  - [5.3 Inheritance](#53-inheritance)
  - [5.4 Polymorphism](#54-polymorphism)
  - [5.5 Abstract class or interface: how we chose](#55-abstract-class-or-interface-how-we-chose)
- [6. Relationships Between Classes](#6-relationships-between-classes)
  - [6.1 Association](#61-association)
  - [6.2 Aggregation](#62-aggregation)
  - [6.3 Composition](#63-composition)
  - [6.4 Dependency and realisation](#64-dependency-and-realisation)
  - [6.5 Summary table](#65-summary-table)
- [7. UML Models](#7-uml-models)
  - [7.1 Class diagrams](#71-class-diagrams)
  - [7.2 Sequence diagrams](#72-sequence-diagrams)
  - [7.3 Activity diagrams](#73-activity-diagrams)
  - [7.4 State machine diagrams](#74-state-machine-diagrams)
  - [7.5 Package diagram](#75-package-diagram)
- [8. SOLID Design Principles](#8-solid-design-principles)
- [9. Design Patterns](#9-design-patterns)
  - [9.1 Creational patterns](#91-creational-patterns)
  - [9.2 Structural patterns](#92-structural-patterns)
  - [9.3 Behavioural patterns](#93-behavioural-patterns)
  - [9.4 Pattern summary](#94-pattern-summary)
- [10. Exception Handling and Robustness](#10-exception-handling-and-robustness)
- [11. Implementation](#11-implementation)
  - [11.1 Technology](#111-technology)
  - [11.2 The simulation loop](#112-the-simulation-loop)
  - [11.3 Anomaly detection](#113-anomaly-detection)
  - [11.4 The response chain](#114-the-response-chain)
  - [11.5 Other rules in the model](#115-other-rules-in-the-model)
  - [11.6 Threads and synchronisation](#116-threads-and-synchronisation)
  - [11.7 User interface](#117-user-interface)
- [12. Testing](#12-testing)
  - [12.1 Approach](#121-approach)
  - [12.2 Scenario matrix results](#122-scenario-matrix-results)
  - [12.3 Functional checks](#123-functional-checks)
  - [12.4 Defects found](#124-defects-found)
  - [12.5 Documented test cases](#125-documented-test-cases)
- [13. Limitations and Future Work](#13-limitations-and-future-work)
- [14. Conclusion](#14-conclusion)
- [References](#references)
- [Appendix A: Building and Running](#appendix-a-building-and-running)
- [Appendix B: Demonstration Script](#appendix-b-demonstration-script)
- [Appendix C: Viva Questions and Answers](#appendix-c-viva-questions-and-answers)
- [Appendix D: Class Inventory](#appendix-d-class-inventory)
- [Appendix E: Development Timeline](#appendix-e-development-timeline)

---

## Abstract
Spacecraft far from Earth cannot wait for orders from the ground. A command to Mars takes minutes to arrive, so when a fuel line leaks or a radiator fails, the craft has to notice the problem and react by itself. OrbitalX is a desktop simulator of such a mission. The user plans a flight, watches live telemetry from eight subsystems, injects faults, and decides how much authority to hand to the onboard autonomy: none (manual), some (a timed autopilot that waits for the human first) or full.

Behind the screens, OrbitalX is an exercise in object-oriented modelling. Sensors are objects that can be wrapped by noise, drift, spike and stuck-value decorators. Eight subsystem classes inherit a common template. Three anomaly detectors and fifteen advisory rules are interchangeable strategies. Twenty corrective actions are command objects. A recommendation travels along a chain of four handlers (self-healing, autopilot, crew, escalation to a human) until one of them deals with it. The user interface never touches the engine directly; it talks to a single `SimulationController` interface and reads immutable snapshot records.

The project is written in plain Java 17 with Swing and Java2D and has no third-party libraries. It contains 296 Java source files (about 11,000 lines) in 24 packages, including 11 abstract classes, 24 interfaces, 13 enumerations and 33 records. This report follows the course syllabus: it identifies classes from the problem text, gives CRC cards and use cases, explains encapsulation, abstraction, inheritance and polymorphism with code from the project, shows association, aggregation and composition, presents fourteen UML diagrams, and discusses the SOLID principles, sixteen design patterns and the exception-handling design. A built-in self-test runs 108 seeded missions (12 scenarios × 3 difficulties × 3 autonomy modes) plus 15 groups of functional checks and passes. With either autonomous mode switched on, all 72 autonomous missions reached their destination; with no human input in manual mode, 19 of 36 did.

**Keywords:** object-oriented modelling, UML, design patterns, SOLID, Java, simulation, chain of responsibility, decorator.

---

## 1. Introduction

### 1.1 Background
A spacecraft is a set of tightly coupled machines: propulsion, power, thermal control, communication, attitude control, life support, navigation and an onboard computer. Each subsystem is watched by sensors. When one of them fails the effect spreads; a solar array failure drains the battery, a drained battery switches off pumps, and stopped pumps overheat the engine. Real missions handle this with layered fault management: the hardware reacts first, then onboard software, then the crew, and only then the ground.

This structure maps naturally onto objects. Subsystems, sensors, crew members, resources, faults, rules and actions are all things with their own data and behaviour, and the layered response is a textbook case for the Chain of Responsibility pattern. That is why we chose a mission simulator for the Object Oriented Modelling project.

### 1.2 Problem statement
Build an interactive program in which a user can fly a simulated mission to the Moon, Mars or the asteroid belt, see the health of every subsystem in real time, cause failures on purpose, and compare what happens when a human, a timed autopilot or full autonomy is in charge. The design must show the object-oriented concepts of the course in a program large enough for them to matter.

### 1.3 Objectives
1. Model the spacecraft, its subsystems, sensors, resources and crew as a clean class hierarchy.
2. Detect anomalies in sensor data and turn them into ranked recommendations with a corrective action attached.
3. Respond to faults through a four-level chain (self-healing, autopilot, crew, human) whose behaviour depends on the selected autonomy mode.
4. Keep the user interface separate from the simulation behind one interface, so the simulation can also run from a command line or a test without a window.
5. Make every mission reproducible from a seed, so that results can be replayed, previewed and tested automatically.
6. Demonstrate the OOM syllabus: the four pillars, class relationships, UML, SOLID, design patterns and exception handling.

### 1.4 Scope
* **In scope:** mission planning (destination, craft type, difficulty, scenario, seed); a simulation advanced in discrete ticks; 15 fault types and 12 scripted scenarios; anomaly detection; recommendations with preview; three autonomy modes; crew tasks; a delayed radio uplink; mission replay; HTML and CSV reports; ten GUI screens with four colour themes; a command-line console; a headless self-test.
* **Out of scope:** real orbital mechanics (the physics is a simplified, game-like model in arbitrary units), networking or multiplayer, saving and loading a mission mid-flight, and a JUnit test suite (tests are written as a self-test program instead; see Chapter 12).

### 1.5 Team and responsibilities
| Member | Roll number | Main responsibilities |
| :--- | :--- | :--- |
| **Bharat Upadhyay** | IIT2025045 | Simulation engine and tick loop; model, sensor, fault and autonomy packages; the ten Swing views and the 3D renderer; the self-test |
| **Kaushal Gond** | IIT2025042 | Requirements and use cases; UML models; user guide and OOP guide; this report, slides and viva preparation; testing on fresh machines |

### 1.6 Organisation of the report
Chapter 2 lists the requirements. Chapter 3 performs object-oriented analysis: finding classes, use cases and CRC cards. Chapters 4 to 6 cover classes and objects, the four pillars and the relationships between classes, each with code taken from the project. Chapter 7 collects the UML models. Chapters 8 to 10 cover the SOLID principles, design patterns and exception handling. Chapter 11 describes the implementation and the user interface, Chapter 12 the testing, and Chapters 13 and 14 the limitations and conclusions. The appendices explain how to build and run the program, give a demonstration script and list likely viva questions.

---

## 2. Requirements

### 2.1 Users and goals
| Actor | Goal |
| :--- | :--- |
| **Operator (student or player)** | Plan a mission, fly it, react to failures and see the result |
| **Evaluator (instructor)** | Run the self-test, replay a mission, read the report |
| **Onboard autonomy (system actor)** | Fix faults by itself within the authority it has been given, or hand them to a human |

### 2.2 Functional requirements
*Priority: H = must have, M = should have, L = could have.*

| ID | Requirement | Pri. |
| :--- | :--- | :--- |
| **FR-1** | The user shall configure a mission: destination (Moon, Mars transfer, asteroid belt), craft (Explorer, Freighter, Crew Capsule), difficulty (Easy, Normal, Hard), scenario (12) and random seed. | H |
| **FR-2** | The user shall start, pause and reset the mission and change its speed. | H |
| **FR-3** | Each tick the system shall update fuel, battery, temperature, oxygen, CO2, cabin pressure, radiation, course error, signal and progress. | H |
| **FR-4** | The system shall end the mission with SUCCESS when the destination is reached, or LOST when fuel or life support runs out. | H |
| **FR-5** | The user shall set throttle and four operating limits; a value above the design limit shall require an explicit override. | H |
| **FR-6** | The user and the scenarios shall be able to inject any of 15 fault types into a subsystem. | H |
| **FR-7** | Three detectors (threshold, rate of change, z-score) shall scan every sensor every tick and report anomalies with a severity. | H |
| **FR-8** | Fifteen rules shall turn the spacecraft state into recommendations, each with a priority, a risk (SAFE or COSTLY) and a corrective action. | H |
| **FR-9** | The user shall execute or dismiss any recommendation and preview its effect over a chosen number of ticks without changing the live mission. | H |
| **FR-10** | The user shall choose MANUAL, TIMEDAUTOPILOT or FULLAUTO autonomy; recommendations shall pass through self-healing, autopilot, crew and escalation handlers accordingly. | H |
| **FR-11** | The user shall assign crew tasks (EVA repair, medical care, manual reboot, rest); EVA is allowed only when radiation, pressure and oxygen are safe. | M |
| **FR-12** | When the delayed uplink is on, commands shall wait the destination's light-time (1, 3 or 5 ticks) before running. | M |
| **FR-13** | The system shall keep the last 600 snapshots so the user can scrub, step and play back the mission. | M |
| **FR-14** | The system shall export an HTML mission report and a CSV telemetry file and compute a score, a grade (S to F) and 10 achievements. | M |
| **FR-15** | A text console (in the GUI and with --cli) shall accept the same commands as the buttons. | M |
| **FR-16** | A self-test (--selftest) shall run the scenario matrix and the functional checks without opening a window. | H |
| **FR-17** | The user shall switch between four colour themes; the choice shall persist between runs. | L |

### 2.3 Non-functional requirements
| ID | Category | Requirement |
| :--- | :--- | :--- |
| **NFR-1** | Portability | Runs on Windows, macOS and Linux with Java 17 or newer and no other software. |
| **NFR-2** | Determinism | Two runs with the same configuration and seed give identical outcomes and fuel to 10⁻⁶. |
| **NFR-3** | Performance | The GUI animates at about 60 frames per second (16 ms timer) while the simulation ticks at a user-chosen rate. |
| **NFR-4** | Separation | No class in the `gui` package refers to the engine; the UI depends only on the `api` package. |
| **NFR-5** | Safety of data | Snapshots handed to the UI are immutable; changing them throws UnsupportedOperationException. |
| **NFR-6** | Robustness | Invalid input (bad seed, unknown command, unsafe limit) is refused with a message and leaves the mission unchanged. |
| **NFR-7** | Maintainability | New faults, rules, actions, detectors, views and report formats can be added by writing new classes, with little or no change to existing ones. |
| **NFR-8** | Testability | The full self-test finishes in under 10 seconds (about 6 s measured). |

### 2.4 Requirements traceability
| Req. | Implemented by | Verified by |
| :--- | :--- | :--- |
| **FR-1** | PlannerView, MissionConfig.Builder, Destination, CraftType, Difficulty | API checks: config commands, builder; “Planner rejects an invalid seed” |
| **FR-2** | SimulationController.start/pause/reset/setSpeed, MainWindow timers | Keyboard check (Space) |
| **FR-3** | SimulationEngine.advancePhysics, 8 Subsystem.applyPhysics | Scenario matrix; model checks |
| **FR-4** | SimulationEngine.checkMissionEnd, Mission | API check: completed mission reaches 100 % |
| **FR-5** | OperatingLimits, LimitOverrideException, DashboardView | Unsafe limit refused; dashboard limit control |
| **FR-6** | FaultFactory, Fault subclasses, ScenarioFactory, FaultInjector | Every fault type created; console inject hull_breach |
| **FR-7** | DetectionEngine, 3 AnomalyDetector classes, TrendPredictor | Threshold, trend and z-score checks |
| **FR-8** | AdvisorEngine, 15 ConditionRule subclasses, Recommendation | 15 rules for an unhealthy craft, ordered by priority |
| **FR-9** | PreviewService, Copyable, CorrectiveAction | Preview leaves state unchanged; demo preview of 30 ticks |
| **FR-10** | AutopilotController, 4 handlers, 3 policies | Self-heal, manual, timed, escalation and crew decisions |
| **FR-11** | CrewMember, CrewRoster, CrewHandler, CrewView | Crew task assignment feedback |
| **FR-12** | CommandChannel, DelayedChannel, DirectChannel | Delayed command countdown and execution |
| **FR-13** | History<T>, ReplayController, ReplayView | Six replay checks |
| **FR-14** | ReportVisitor and 3 visitors, ScoreKeeper, 10 achievements | HTML preview and exports; achievement catalogue |
| **FR-15** | CommandRegistry, CommandParser, ConsoleView, Main --cli | Console output and parser errors |
| **FR-16** | SelfTest | Itself: exit code 0 |
| **FR-17** | Theme, Themes, Settings, SettingsView | Settings theme persisted |

### 2.5 Constraints and assumptions
* Only the Java standard library may be used, so that the program builds with `javac` alone.
* Units are simplified: fuel, distance and oxygen are abstract quantities, and one tick is one step of the simulation, not a fixed number of seconds.
* One mission runs at a time in one window.

---

## 3. Object-Oriented Analysis

### 3.1 Finding classes: nouns and verbs
*“A spacecraft flies a mission to a destination. It has subsystems such as power and propulsion; each subsystem has sensors that take readings. Faults damage subsystems. Detectors find anomalies in the readings, and rules recommend corrective actions. The autopilot, the crew or the human operator executes an action. Every event is logged, and the mission can be replayed.”*

| Noun / verb in the text | Becomes | Why |
| :--- | :--- | :--- |
| spacecraft | class Spacecraft | Owns subsystems, crew, resources and limits |
| mission, destination | class Mission, enum Destination | Mission tracks progress; destinations are a fixed set of values |
| subsystem (power, propulsion …) | abstract class Subsystem + 8 subclasses | Same structure, different physics |
| sensor, reading | interface Sensor, record Reading | Many sensor kinds; a reading is a plain value |
| fault | abstract class Fault + 14 subclasses, enum FaultType (15 values) | Each fault has its own effect and duration |
| detector, anomaly | interface AnomalyDetector, record Anomaly | Three interchangeable algorithms |
| rule, recommendation | interface RecommendationRule, record Recommendation | Fifteen rules, one result type |
| corrective action | interface CorrectiveAction + 20 classes | An action is an object that can be stored and run later |
| autopilot, crew, operator | 4 ResponseHandler subclasses | Each tries to handle a recommendation in turn |
| crew member | abstract CrewMember + 4 roles | Roles differ in skill bonus |
| is logged | class EventBus, record MissionEvent | Publish once, many listeners |
| replayed | class History<T> | Stores past snapshots |
| flies, takes readings, executes | tick(), read(), execute() | Verbs become methods |

### 3.2 Use case model
- Figure 3.1: Use case diagram showing Operator, Evaluator, and Onboard Autonomy with «include» and «extend» relationships.

### 3.3 Use case descriptions
* **UC-1 Configure and launch a mission:** Operator selects destination, craft, difficulty, scenario, enters seed and launches.
* **UC-2 Respond to a recommendation:** Operator reviews recommendation card, previews impact, executes or dismisses.
* **UC-3 Autonomy handles a fault:** Self-healing resolves SAFE actions, autopilot acts per policy, crew handles repairs, otherwise escalates.
* **UC-4 Assign a crew task:** Operator tasks astronaut with EVA, medical, reboot or rest subject to environmental constraints.
* **UC-5 Replay a mission:** Operator scrubs historical timeline across past 600 ticks.

### 3.4 CRC cards
| Class | Responsibilities | Collaborators |
| :--- | :--- | :--- |
| **SimulationEngine** | Advance the mission one tick; keep faults, limits and history; publish snapshots and events | Spacecraft, DetectionEngine, AdvisorEngine, AutopilotController, EventBus, History |
| **Spacecraft** | Own subsystems, crew and resources; report overall health; hold operating limits and current state | Subsystem, CrewRoster, Resource, OperatingLimits, SpacecraftState |
| **Subsystem** | Apply its own physics each tick; hold its sensors and active faults; compute its health | Spacecraft, Sensor, SpaceEnvironment |
| **BaseSensor** | Read a value from its source; keep the last 120 readings; refuse non-finite values | Reading, RingBuffer |
| **DetectionEngine** | Run every detector over every sensor and collect anomalies | AnomalyDetector, Sensor, Anomaly |
| **AdvisorEngine** | Ask each rule for a recommendation and sort them by priority | RecommendationRule, Recommendation |
| **AutopilotController** | Pick the policy for the mode; pass the recommendation along the handler chain; count waiting ticks | ResponseHandler, AutonomyPolicy, EventBus |
| **CrewMember** | Keep health, stress and dose; accept one task at a time; finish tasks | CrewTask, CrewTaskType, Spacecraft |
| **EventBus** | Deliver each event to every subscribed listener | MissionEventListener, MissionEvent |
| **DashboardView** | Draw gauges and recommendation cards; turn clicks into controller calls | SimulationController, SimulationSnapshot |

---

## 4. Classes, Objects and Packages

### 4.1 Package structure
Code is organized into 24 modular packages under `com.orbitalix`:
`api` (32), `app` (1), `simulation` (6), `model` (17), `sensor` (17), `resource` (5), `crew` (7), `fault` (18), `state` (6), `detection` (7), `advisor` (19), `action` (24), `autonomy` (12), `event` (4), `comms` (4), `replay` (2), `whatif` (1), `scoring` (15), `report` (7), `config` (8), `console` (3), `exception` (11), `util` (7), `gui` (63).

### 4.2 Kinds of types used
* **Concrete classes (~210):** SimulationEngine, ZScoreDetector, etc.
* **Abstract classes (11):** Subsystem, CrewMember, ResponseHandler, etc.
* **Interfaces (24):** Sensor, CorrectiveAction, SimulationController, etc.
* **Enumerations (13):** Difficulty, FaultType, Grade, etc.
* **Records (33):** SimulationSnapshot, Reading, Anomaly, etc.
* **Generics:** History<T>, RingBuffer<T>, ReportVisitor<T>, Topic<T>.

---

## 5. The Four Pillars of OOP
* **Encapsulation:** Private fields, checked setters in `OperatingLimits`, defensive collection copies (`List.copyOf`), immutable records across boundary.
* **Abstraction:** Interfaces (`Sensor`, `AnomalyDetector`, `CorrectiveAction`, `SimulationController`), template method in `Subsystem`.
* **Inheritance:** Shallow hierarchies for subsystems, sensors, decorators, crew roles, resources, faults, and handlers.
* **Polymorphism:** Dynamic method dispatch in simulation loop (`subsystem.update()`, `detector.detect()`), constructor overloading, polymorphic enums (`Difficulty.guidance()`).

---

## 6. Relationships Between Classes
* **Composition (◆):** Spacecraft ◆— Subsystems, CrewRoster, Resources, OperatingLimits.
* **Aggregation (◇):** AdvisorEngine ◇— Rules, DetectionEngine ◇— Detectors.
* **Association (→):** View → SimulationController, ResponseHandler → next ResponseHandler.
* **Realisation (⊳):** DefaultSimulationController realizes SimulationController.
* **Dependency (⇢):** CorrectiveAction ⇢ Spacecraft.

---

## 7. UML Models & 8. SOLID Principles & 9. Design Patterns
Includes all 14 UML models (Use Case, Class, Object, Sequence, Activity, State, Package diagrams), SOLID audit, and 16 GoF Design Patterns (Builder, Abstract Factory, Factory Method, Prototype, Decorator, Facade, Proxy, Composite, Chain of Responsibility, Strategy, Command, Observer, State, Template Method, Visitor, Memento).

---

## 10. Exception Handling & 11. Implementation & 12. Testing
* Custom hierarchy rooted at `OrbitalIXException`.
* Swing 60 FPS rendering decoupled from simulation clock advance.
* Headless test suite (`SelfTest.java`) executes 108 scenario matrix missions deterministically in ~6 seconds (108/108 PASS).

---

## 14. Conclusion & Appendices
Fully functional aerospace mission simulator demonstrating pure object-oriented design and rigorous testing. Complete class inventory and development history included.
