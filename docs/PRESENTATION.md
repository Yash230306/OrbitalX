# OBJECT ORIENTED MODELLING · MINI PROJECT
# OrbitalX
### A space mission simulator where faults are found and fixed by a chain of objects: self-healing, autopilot, crew and the human operator
**Java 17 · Swing and Java2D · 296 source files · 108/108 self-test missions pass**

**Presented by:**
- **Bharat Upadhyay** (IIT2025045)
- **Kaushal Gond** (IIT2025042)

---

## Slide 1: What we built and why a spacecraft
1. **Fly a mission:** Choose Moon, Mars or the asteroid belt, a craft and a difficulty; fuel, power, heat, air and course change every tick.
2. **Break things on purpose:** 15 kinds of fault and 12 scripted scenarios, from a fuel leak to a six-fault cascade.
3. **Let objects fix them:** Detectors spot anomalies, rules recommend actions, and a chain of handlers decides who acts.
4. **Show good OO design:** UI separated from the model, 16 design patterns, and a self-test that runs 108 missions in 6 s.

### Why it suits an OOM project:
- The domain is full of real objects: craft, subsystems, sensors, crew, faults, actions.
- 8 subsystems and 9 sensors share data but behave differently: inheritance and polymorphism.
- Faults are handled in layers, which is exactly the Chain of Responsibility pattern.
- Separating screen from simulation lets us test 108 missions without a window.

---

## Slide 2: The program: ten mission-control screens
- **On the dashboard:**
  - Six gauges: fuel, battery, oxygen, temperature, cabin pressure, radiation.
  - Throttle lever and four operating limits.
  - Mission Advisor card: Preview, Execute, Dismiss.
  - Progress and trajectory.
- **Other screens:** 3D cutaway, orbit map, planner, systems, crew, replay, console, reports, settings.

---

## Slide 3: Use case diagram: who does what
- **Actors:** Operator, Evaluator, Onboard Autonomy (system actor).
- **«include»:** Always happens as part of the base use case (e.g., configuring validates input).
- **«extend»:** Happens conditionally (e.g., previewing recommendations, delayed uplink, limit overrides).
- **System actor:** Onboard autonomy initiates self-healing or escalation without operator intervention.

---

## Slide 4: Finding classes: nouns and verbs
- Nouns become classes (`Spacecraft`, `Subsystem`, `Sensor`, `Fault`, `Recommendation`, `EventBus`, `History`).
- Verbs become methods (`tick()`, `read()`, `execute()`).

---

## Slide 5: CRC cards: responsibilities and collaborators
- `SimulationEngine` collaborates with `Spacecraft`, `DetectionEngine`, `AdvisorEngine`, `AutopilotController`, `EventBus`.
- `Spacecraft` collaborates with `Subsystem`, `CrewRoster`, `Resource`, `OperatingLimits`, `SpacecraftState`.
- `AutopilotController` collaborates with `ResponseHandler`, `AutonomyPolicy`, `EventBus`.

---

## Slide 6: Three layers: screen never touches simulation
- **User Interface:** Swing Views depend only on `SimulationController`.
- **Boundary (api):** Clean contract with immutable snapshot records.
- **Simulation Core:** Physics engine, state, and autonomy.
- **Benefits:** Identical headless and GUI execution; no UI race conditions.

---

## Slide 7: Class diagram: main classes in five color groups
- Blue: UI
- Amber: Engine & Logic
- Grey: Domain Model
- Green: Autonomy & Actions
- Purple: API interfaces & records

---

## Slide 8: The codebase in numbers
- **296** Java source files
- **24** packages under `com.orbitalix`
- **~11,000** lines of code (~9,700 SLOC)
- **11** abstract classes, **24** interfaces, **13** enums, **33** records

---

## Slide 9: Encapsulation: state is private and checked
- `OperatingLimits`: Mutators enforce bounds and require explicit override flags.
- Defensive copying: `subsystem.sensors()` returns immutable `List.copyOf()`.
- Immutable snapshots: `SimulationSnapshot` records prevent UI state corruption.
- Template method guards: `Subsystem.update()` and `CrewMember.assign()` are `final`.

---

## Slide 10: Abstraction: show what an object does, hide how
- Interfaces define behavioral contracts (`Sensor`, `AnomalyDetector`, `CorrectiveAction`, `CommandChannel`, `SimulationController`).
- Base classes like `Subsystem` supply common orchestration and leave only `applyPhysics()` abstract.

---

## Slide 11: Inheritance: shallow hierarchies with real "is-a" links
- `Subsystem` ◁— 8 subsystems (Propulsion, Power, Thermal, LifeSupport, Navigation, etc.).
- `BaseSensor` ◁— 9 sensors.
- `CrewMember` ◁— 4 crew roles.
- `ResponseHandler` ◁— 4 response handlers.

---

## Slide 12: Polymorphism: one call, eight different behaviours
- Dynamic binding: `subsystem.update(this, env)` executes specialized physics per subsystem.
- Open/Closed: adding new modules requires zero changes to the simulation engine loop.

---

## Slide 13: Overloading, enums, generics and records
- Constructor overloading (`ZScoreDetector`).
- Enums with polymorphic method bodies (`Difficulty.guidance()`).
- Generic type-safe containers (`History<T>`, `RingBuffer<T>`, `Topic<T>`).
- Records for lightweight immutable data carriers (`Reading`, `Anomaly`, `SimulationSnapshot`).

---

## Slide 14: Relationships: who owns whom
- Composition (◆): Spacecraft owns and instantiates its 8 subsystems.
- Aggregation (◇): Engines hold lists of rules and detectors provided from outside.
- Association (→): Views hold references to `SimulationController`.
- Dependency (⇢): Actions accept `Spacecraft` as method parameters.

---

## Slide 15: Decorator: faulty sensors without subclass explosion
- Decorators implement `Sensor` and wrap another `Sensor`.
- `NoiseDecorator`, `DriftDecorator`, `SpikeDecorator`, `StuckDecorator` stack seamlessly.

---

## Slide 16: Chain of Responsibility: four levels of fault response
1. `SelfHealingHandler`: Resolves SAFE actions immediately if enabled.
2. `AutopilotHandler`: Executes based on autonomy policy (Full Auto or Timed window).
3. `CrewHandler`: Dispatches qualified astronauts for repairs.
4. `EscalationHandler`: Alerts ground operator with "NEEDS HUMAN".

---

## Slide 17: Strategy: the autonomy mode is an object
- `AutonomyPolicy`: `ManualPolicy` (never acts), `TimedAutopilotPolicy` (waits for window), `FullAutoPolicy` (acts immediately).
- Dynamic swapping at runtime without conditional branching.

---

## Slide 18: Command: every corrective action is an object
- `CorrectiveAction` encapsulates commands with `id()`, `risk()` (SAFE vs COSTLY), and `execute(Spacecraft)`.
- 8 SAFE actions and 12 COSTLY actions.

---

## Slide 19: State: spacecraft changes its own behaviour
- `SpacecraftState`: `NominalState` (100% throttle), `WarningState` (80%), `CriticalState` (50%), `SafeModeState` (0%, no override).
- State transitions evaluate health status dynamically.

---

## Slide 20: Observer, Visitor, Memento, and Proxy
- Observer: `EventBus` pub/sub with typed topics and AutoCloseable unsubscribe tokens.
- Visitor: `ReportVisitor` exports HTML, CSV, and plain text.
- Memento: `History<T>` stores 600-snapshot sliding window for time travel.
- Proxy: `DelayedChannel` models radio propagation light delay.

---

## Slide 21: Creational patterns: Builder, Factory, and Prototype
- Builder: Fluent `MissionConfigBuilder`.
- Abstract Factory: `CraftFactory` presets for Explorer, Freighter, Crew Capsule.
- Factory Method: `ScenarioFactory` and `FaultFactory`.
- Prototype: `Copyable<T>` allows isolated 30-tick what-if previews.

---

## Slide 22: Sixteen patterns summary
Overview of 16 Gang-of-Four design patterns implemented cleanly across the project.

---

## Slide 23: SOLID principles audit
- Single Responsibility: Separated detectors, rules, handlers, and views.
- Open/Closed: Plug-in architectures for actions, rules, detectors, and reports.
- Liskov Substitution: Decorators replace sensors; handlers follow base contract.
- Interface Segregation: Compact, single-purpose interfaces.
- Dependency Inversion: UI and engine decouple via abstractions.

---

## Slide 24: Sequence: one simulation tick
Timer tick &rarr; Controller &rarr; Engine physics &rarr; Detection scan &rarr; Advisor rules &rarr; Autonomy response chain &rarr; History snapshot &rarr; View repainting.

---

## Slide 25: Sequence: recommendation down the chain
Detailed interaction flow through SelfHealing, Autopilot, Crew, and Escalation handlers.

---

## Slide 26: Activity diagram: fault to recovery walkthrough
Traces the life of a fuel leak fault from injection to detector alert, advisor rule, autonomy dispatch, and cooldown suppression.

---

## Slide 27: Exception handling: fail fast, fail clearly
- Custom checked exception hierarchy (`OrbitalIXException`).
- Unchecked exceptions guard preconditions in constructors and setters.
- Layer boundary translation and safe fallbacks for audio/theme configurations.

---

## Slide 28: User interface showcase
High-resolution captures of the 3D cutaway, orbit radar, crew vitals HUD, telemetry bus, and mission replay timeline.

---

## Slide 29: Testing: 108 seeded missions
- 12 scenarios × 3 difficulties × 3 autonomy modes.
- 108/108 deterministic repeatability.
- Headless execution completes in ~6 seconds.

---

## Slide 30: Real defects identified and resolved
Analysis of race conditions, cooldown suppression, OCP handler refactoring, and UI event absorption fixes.

---

## Slide 31: Limitations and future work
Unifying engine and model state, fully wiring decorators to live faults, and porting to JUnit 5.

---

## Slide 32: Summary & Conclusion
Demonstrates complete compliance with the Object Oriented Modelling curriculum through a practical, high-performance aerospace simulator.
