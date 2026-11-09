# OOP concept map

This map names the implementation in the active Java 17 source tree (`src/main/java`). It describes existing behavior rather than inferring concepts from package names.

## Inheritance, interfaces, and polymorphism

| Checklist item | Code evidence |
|---|---|
| Abstract base classes with multiple concrete implementations | `sensor/BaseSensor` → `GyroSensor`, `TemperatureSensor`, `VoltageSensor`, and other sensors; `model/Subsystem` → eight subsystem types; `resource/Resource` → `Battery`, `FuelTank`, `OxygenSupply`, `SealantStock`; `crew/CrewMember` → `Commander`, `FlightEngineer`, `MedicalOfficer`, `Pilot`; `fault/Fault` → the concrete fault classes; `gui/render3d/Mesh` → `BoxMesh`, `ConeMesh`, `CylinderMesh`, `SphereMesh`; `gui/View` → mission views; `autonomy/ResponseHandler` → `AutopilotHandler`, `CrewHandler`, `EscalationHandler`, `SelfHealingHandler`. |
| Interface-oriented contracts (20+ project interfaces) | Examples include `api/SimulationController`, `api/TickListener`, `api/MissionEventListener`, `api/Copyable`, `util/Identifiable`, `sensor/Sensor`, `detection/AnomalyDetector`, `advisor/RecommendationRule`, `action/CorrectiveAction`, `autonomy/AutonomyPolicy`, `comms/CommandChannel`, `report/ReportVisitor`, and `gui/theme/Theme`. `Sensor.latest()` is a default method; `Identifiable.requireId<T extends Identifiable>(...)` is a static bounded-generic method. |
| Dynamic dispatch and overriding | Concrete sensors implement measurement/copy behavior; crew roles supply role and skill behavior; actions implement `CorrectiveAction.execute`; meshes provide distinct geometry to the renderer; report visitors implement output formats. The fault factory intentionally uses a `switch` on `FaultType` to choose a concrete fault at construction time; the resulting `Fault` objects retain subtype-specific behavior. Not every selection in the engine is polymorphic—some command and enum dispatch remains explicit. |
| Overloading and constructor chaining | `fault/FaultInjector` exposes overloads for fault injection; `SimulationEngine()` delegates with `this(MissionConfig.defaults())`; concrete sensor/subsystem/action constructors chain through `super(...)`. |
| Hooks and immutability | `CrewMember`, `Subsystem`, `View`, `Mesh`, and `ResponseHandler` expose protected abstract or overridable hooks. Classes and value fields are final where appropriate; model state is private. Snapshot and record constructors validate inputs and copy mutable collections/arrays at boundaries. |

## Encapsulation, values, and enums

- **Encapsulation and defensive copies:** `model/Spacecraft`, `sensor/BaseSensor`, and `util/RingBuffer` own mutable state; `RingBuffer.snapshot()` and snapshot records return stable views rather than exposing internal storage. `gui/render3d/Mesh` copies vertex and face data both on construction and access.
- **Immutable values:** API snapshots and event/configuration values are Java records, for example `api/MissionEvent`, `api/MissionConfig`, `api/SimulationSnapshot`, and `action/ActionResult`. `MissionConfig` validates its required enum values and scenario index in its compact constructor.
- **Enum data and behavior:** `config/Difficulty`, `config/Destination`, and `scoring/Grade` store values and declare abstract methods implemented by each constant (`guidance()`, `profile()`, and `description()` respectively).
- **Value equality and ordering:** records supply value equality, `advisor/Recommendation` implements `Comparable` and explicit `equals`/`hashCode`, and `gui/render3d/Renderer` uses a depth comparator for painter ordering.

## Generics and nested types

- `util/RingBuffer<T>` implements `Iterable<T>`; its iterator preserves the ring's logical order.
- `event/EventBus.Topic<T>` carries a typed event payload; listener storage uses wildcard topic/listener types.
- `util/Identifiable.requireId<T extends Identifiable>(...)` is a bounded generic helper.
- `api/MissionConfig.Builder` is a static nested builder; the compatibility `config/MissionConfigBuilder` delegates to it.
- `RingBuffer` includes an iterator implementation, while listeners and view factories use lambdas and the event bus also accepts ordinary listener objects.
- `event/EventBus` uses static and instance initialization blocks to set up a monotonic static ID source and assign each instance an ID.

## Exceptions and Java language features

- `exception/OrbitalIXException` is the checked root and `exception/ActionNotPossibleException` demonstrates cause chaining. Methods that perform file I/O declare `throws IOException`.
- `app/Main` uses try-with-resources with `event/MissionRecorder`, which implements `AutoCloseable`. `simulation/SelfTest` exercises cleanup in a `finally` block; GUI and audio boundaries use multi-catch where the alternatives share handling.
- `simulation/SimulationEngine` translates invalid console-command runtime failures to a readable `Error:` result. The public controller facade otherwise preserves failures from the engine rather than silently replacing them.
- `detection` uses `Optional` for absent anomalies and predictions; analysis, reporting, and rendering use streams, collectors, and comparators.
- Java 17 pattern matching for `instanceof` appears in renderer/layout and value equality code; switch expressions appear in fault construction, command handling, and enum/task logic.
- `report/HtmlReportVisitor` uses Java text blocks for its HTML template. Local-variable type inference (`var`) is not needed in the core paths.

## Dependency direction

`api` defines the UI-facing simulation contract. `gui` depends on that contract and `gui/theme`; `simulation` implements it and composes model, detection, advice, autonomy, event, replay, scoring, and reporting types. The `SimulationEngine` itself has no GUI dependency. `simulation/SelfTest` deliberately imports GUI types for end-to-end interaction regressions.

## Related source maps

- [Architecture overview](ARCHITECTURE.md)
- [Model and crew](puml/model-crew.puml)
- [Advisor and autonomy](puml/advisor-autonomy.puml)
- [Patterns](puml/patterns-showcase.puml)
- [GUI and API](puml/gui-api.puml)
