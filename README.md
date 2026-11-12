GitHub repository: https://github.com/Yash230306/OrbitalX

# OrbitalX — Spacecraft Mission Control

OrbitalX is a Java 17 mission-control simulator for spacecraft telemetry, subsystem faults, human and autonomous recovery, replay, and reporting. The Swing/Java2D desktop console and the interactive command-line mode share the same simulation API and engine. The project has no third-party runtime dependencies.

## Features

- Deterministic missions with configurable destination, spacecraft, difficulty, seed, scenarios, and injected faults.
- Sensor history and anomaly detection, trend prediction, operating limits, and resource/subsystem/crew status.
- Manual, timed-autopilot, and full-auto responses; escalation to the crew; optional automatic self-healing.
- Delayed uplink, crew task assignment, action recommendations, what-if previews, and event history/replay.
- Ten GUI views: Dashboard, Cutaway, Orbit, Planner, Console, Settings, Systems, Crew, Replay, and Reports.
- HTML and CSV report exports, achievements and scoring, four themes, synthesized notifications, and configurable settings.
- A self-test suite and a headless 80-image screenshot matrix (10 views × 4 themes × 2 resolutions).

## Requirements

- JDK 17 or newer (`java` and `javac` on `PATH`).
- Windows: use the `.bat` scripts. Linux/macOS: use the `.sh` scripts.

## Build and run

From the repository root on Windows:

```powershell
.\compile.bat
.\run.bat
.\run.bat --cli
.\run.bat --selftest
.\run.bat --screenshots out\screenshots
```

`run.bat` compiles the active `src/main/java` source tree before launching. The supported application modes are the GUI (no arguments), `--cli`, `--selftest`, and `--screenshots <directory>`. The screenshot harness captures all registered views in four themes at 1280×720 and 1920×1080.

On Linux/macOS:

```sh
./compile.sh
./run.sh
./run.sh --cli
./run.sh --selftest
./run.sh --screenshots out/screenshots
```

## Command-line controls

Start with `help`. The CLI accepts `tick` or `tick <count>` to advance the simulation and supports the simulation commands listed by `help`, including:

```text
status
throttle 60
inject fuel-leak
exec <recommendation-id>
dismiss <recommendation-id>
autonomy manual|timed|full
heal on|off
uplink on|off
limits [type value [override]]
crew
task <crew-name-or-role> <task>
preview <recommendation-id> [ticks]
scenario <index>
config destination|craft|difficulty <name>
seek <tick>
report
quit
```

The standalone CLI also accepts `tick` and `tick <count>` to advance the mission.
Events are printed to the terminal and appended to `out/mission-events.log`; report and telemetry export controls are also available in the Reports view.

## GUI shortcuts

- `0`–`9`: select Planner, Dashboard, Cutaway, Orbit, Systems, Crew, Replay, Console, Reports, and Settings, respectively. (The small numbers printed on the navigation rail use a different mapping.)
- `Space`: start/pause the mission.
- `Ctrl+K`: focus the command field.
- `Ctrl+S`: capture the screenshot matrix into `out\screenshots`.
- `F11`: toggle fullscreen.
- `Escape`: clear the active command.

The GUI Console view's command field supports Up/Down history and Tab completion.

## Architecture and implementation notes

The UI depends on the `SimulationController` API rather than the engine. `DefaultSimulationController` is a facade over `SimulationEngine`; the engine composes the spacecraft model, detection/advice/autonomy pipeline, event bus, replay history, scoring, and report generation. The comprehensive OOP architectural guide is in [**`docs/OOP_GUIDE.md`**](docs/OOP_GUIDE.md), with further concept mapping and diagrams in [`docs/OOP_CONCEPT_MAP.md`](docs/OOP_CONCEPT_MAP.md), [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md), and [`docs/puml/`](docs/puml/).

See [`PROJECT_CONTEXT.md`](PROJECT_CONTEXT.md) for the A1–A17 requirements and delivery record. Representative UI and scenario captures are in [`docs/screenshots/`](docs/screenshots/); a generated completed-mission example is [`docs/sample-mission-report.html`](docs/sample-mission-report.html).
For a first-time walkthrough, see the [Dark Edition Beginner's Guide PDF](docs/BEGINNER_GUIDE.pdf), with a screenshot and explanation for every application view and a project-specific OOP chapter (editable HTML source: [`docs/BEGINNER_GUIDE.html`](docs/BEGINNER_GUIDE.html)).

## Screenshot gallery

The documentation directory contains 16 PNG captures: nine scenario/dashboard/orbit examples and seven current GUI views:

- [Planner](docs/screenshots/10_planner.png) · [Dashboard](docs/screenshots/11_dashboard.png) · [3D Cutaway](docs/screenshots/12_cutaway_3d.png)
- [Systems](docs/screenshots/13_systems.png) · [Crew](docs/screenshots/14_crew.png) · [Replay](docs/screenshots/15_replay.png) · [Reports](docs/screenshots/16_reports.png)

## Validation

```powershell
.\compile.bat
java -cp out com.orbitalix.app.Main --selftest
java -cp out com.orbitalix.app.Main --screenshots out\screenshots
```

The self-test suite checks deterministic mission behavior, API and model contracts, response escalation, self-healing enabled/disabled behavior, GUI interactions, replay, reports, keyboard shortcuts, and the demo story.
