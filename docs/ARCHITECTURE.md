# OrbitalX architecture

## Runtime shape

`Main` selects the Swing application, interactive CLI, self-test suite, or screenshot harness. The GUI talks to the simulation through the `api/SimulationController` contract. `simulation/DefaultSimulationController` is the facade implementation; it delegates to `SimulationEngine` and exposes immutable snapshots, events, replay, preview, report, and settings operations.

The engine owns the mission configuration and composes the spacecraft model, scenario and deterministic random source, sensors/detection, recommendation rules, autonomy handler chain, event bus, history/replay, score keeper, and report visitors. A tick advances model and telemetry, detects and predicts anomalies, updates advice/autonomy, publishes events, then records an immutable snapshot. The application views render that API data and submit user actions through the controller; they do not manipulate engine internals.

```text
Main
 ├─ GUI ─ MainWindow ─ ViewRegistry ─ Views ─ SimulationController
 ├─ CLI ─ MissionRecorder + SimulationController
 └─ SelfTest / ScreenshotHarness

DefaultSimulationController ── SimulationEngine
                                   ├─ Spacecraft / Mission / Scenario
                                   ├─ DetectionEngine ─ TrendPredictor
                                   ├─ AdvisorEngine ─ RecommendationRules ─ CorrectiveActions
                                   ├─ AutopilotController ─ ResponseHandler chain
                                   ├─ EventBus ─ listeners
                                   ├─ History / Replay / PreviewService
                                   └─ ScoreKeeper / MissionReport / ReportVisitor
```

## Main boundaries and patterns

| Area | Main types | Role |
|---|---|---|
| API | `api/SimulationController`, `api/SimulationSnapshot`, `api/MissionEvent` | Stable boundary and immutable data exchanged with UI and integrations. |
| Domain model | `model/Spacecraft`, `model/Subsystem`, `sensor/Sensor`, `resource/Resource`, `crew/CrewMember` | Spacecraft composition, telemetry, condition, resources, and crew tasks. |
| Detection and advice | `detection/DetectionEngine`, `detection/TrendPredictor`, `advisor/AdvisorEngine`, `advisor/RecommendationRule` | Detector/rule strategies produce anomalies, forecasts, and ranked actions. |
| Recovery | `action/CorrectiveAction`, `autonomy/AutopilotController`, `autonomy/ResponseHandler` | Command objects perform actions; the handler chain tries self-healing, autopilot, crew, then escalation according to policy and context. |
| Events and history | `event/EventBus`, `event/MissionRecorder`, `replay/History` | Typed observer notifications, persistent CLI event recording, and replay snapshots. |
| Reports and rendering | `report/MissionReport`, `report/ReportVisitor`, `report/HtmlReportVisitor`, `gui/render3d/Renderer` | Visitor-based report formats and geometry-based cutaway rendering. |
| GUI | `gui/MainWindow`, `gui/View`, `gui/core/ViewRegistry`, `gui/theme/Theme` | Navigation shell, registered views, theme contract, and controller-driven interactions. |

The source diagrams below are editable PlantUML class diagrams drawn from the implementation. They are intentionally split by responsibility and omit unrelated members so that the relationships remain readable.

## Diagrams

- [Model and crew](puml/model-crew.puml)
- [Advisor, actions, and autonomy](puml/advisor-autonomy.puml)
- [Patterns showcase](puml/patterns-showcase.puml)
- [GUI and API boundary](puml/gui-api.puml)

## Operational notes

- Build target: Java 17; no external runtime dependencies.
- `compile.bat` / `compile.sh` compile `src/main/java` into `out`.
- `--selftest` runs deterministic, simulation, API, GUI-interaction, report, and autonomy regressions.
- `--screenshots <directory>` captures ten views in four themes at two resolutions (80 images).
- The CLI records events to `out/mission-events.log`; the Reports view supports HTML and CSV export.
