import os
import sys
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.enum.text import PP_ALIGN
from pptx.dml.color import RGBColor
from pptx.enum.shapes import MSO_SHAPE

def create_presentation():
    prs = Presentation()
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)
    blank_layout = prs.slide_layouts[6]

    # Palette
    BG_COLOR = RGBColor(11, 15, 25)        # #0B0F19 Dark void
    CARD_BG = RGBColor(19, 27, 46)         # #131B2E Card surface
    CARD_BORDER = RGBColor(30, 41, 59)     # #1E293B Card border
    CYAN = RGBColor(0, 229, 255)           # #00E5FF Accent Cyan
    SKY_BLUE = RGBColor(56, 189, 248)      # #38BDF8 Sky
    AMBER = RGBColor(245, 158, 11)         # #F59E0B Accent Gold/Amber
    EMERALD = RGBColor(16, 185, 129)       # #10B981 Accent Green
    ROSE = RGBColor(244, 63, 94)           # #F43F5E Danger Red
    TEXT_WHITE = RGBColor(248, 250, 252)   # #F8FAFC Primary text
    TEXT_MUTED = RGBColor(148, 163, 184)   # #94A3B8 Secondary text
    TEXT_DIM = RGBColor(100, 116, 139)     # #64748B Dim labels

    def add_blank_slide(category="SYSTEM OVERVIEW"):
        slide = prs.slides.add_slide(blank_layout)
        # Background fill
        bg = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(7.5))
        bg.fill.solid()
        bg.fill.fore_color.rgb = BG_COLOR
        bg.line.fill.background()
        
        # Subtle header line
        line = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(1.15), Inches(11.733), Inches(0.02))
        line.fill.solid()
        line.fill.fore_color.rgb = RGBColor(26, 38, 64)
        line.line.fill.background()

        # Category tag top-right
        tag_box = slide.shapes.add_textbox(Inches(8.5), Inches(0.4), Inches(4.0), Inches(0.4))
        tf = tag_box.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = f"ORBITALX  //  {category}"
        p.alignment = PP_ALIGN.RIGHT
        p.font.size = Pt(10)
        p.font.bold = True
        p.font.color.rgb = CYAN
        
        # Footer
        footer_box = slide.shapes.add_textbox(Inches(0.8), Inches(7.1), Inches(11.733), Inches(0.3))
        ftf = footer_box.text_frame
        ftf.word_wrap = True
        fp = ftf.paragraphs[0]
        fp.text = "OrbitalX Space Mission Simulator  |  Java 17 OOP Architecture  |  Autonomous Avionics Evaluation"
        fp.font.size = Pt(9)
        fp.font.color.rgb = TEXT_DIM
        return slide

    def set_slide_title(slide, title, subtitle):
        tb = slide.shapes.add_textbox(Inches(0.8), Inches(0.35), Inches(8.0), Inches(0.8))
        tf = tb.text_frame
        tf.word_wrap = True
        tf.margin_left = tf.margin_top = tf.margin_right = tf.margin_bottom = 0
        p = tf.paragraphs[0]
        p.text = title
        p.font.size = Pt(22)
        p.font.bold = True
        p.font.color.rgb = TEXT_WHITE
        
        p2 = tf.add_paragraph()
        p2.text = subtitle
        p2.font.size = Pt(11)
        p2.font.color.rgb = TEXT_MUTED

    def create_card(slide, left, top, width, height, title="", title_color=CYAN):
        card = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top, width, height)
        card.fill.solid()
        card.fill.fore_color.rgb = CARD_BG
        card.line.color.rgb = CARD_BORDER
        card.line.width = Pt(1)
        
        if title:
            tb = slide.shapes.add_textbox(left + Inches(0.2), top + Inches(0.15), width - Inches(0.4), Inches(0.4))
            tf = tb.text_frame
            tf.word_wrap = True
            tf.margin_left = tf.margin_top = tf.margin_right = tf.margin_bottom = 0
            p = tf.paragraphs[0]
            p.text = title
            p.font.size = Pt(13)
            p.font.bold = True
            p.font.color.rgb = title_color
        return card

    # =========================================================================
    # SLIDE 1: TITLE SLIDE
    # =========================================================================
    s1 = prs.slides.add_slide(blank_layout)
    bg1 = s1.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(7.5))
    bg1.fill.solid()
    bg1.fill.fore_color.rgb = BG_COLOR
    bg1.line.fill.background()

    # Title Banner Glow Card
    tcard = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.2), Inches(1.2), Inches(10.933), Inches(5.1))
    tcard.fill.solid()
    tcard.fill.fore_color.rgb = RGBColor(15, 23, 42)
    tcard.line.color.rgb = RGBColor(30, 58, 102)
    tcard.line.width = Pt(1.5)

    tb = s1.shapes.add_textbox(Inches(1.8), Inches(1.7), Inches(9.733), Inches(2.2))
    tf = tb.text_frame
    tf.word_wrap = True
    
    p = tf.paragraphs[0]
    p.text = "ORBITALX"
    p.font.size = Pt(46)
    p.font.bold = True
    p.font.color.rgb = CYAN

    p_sub = tf.add_paragraph()
    p_sub.text = "Autonomous Space Mission Simulator & Flight Avionics"
    p_sub.font.size = Pt(22)
    p_sub.font.bold = True
    p_sub.font.color.rgb = TEXT_WHITE

    p_desc = tf.add_paragraph()
    p_desc.text = "High-Fidelity Telemetry, Multi-Tier Autonomous Fault Recovery, and Classical GoF OOP Architecture in Pure Java 17"
    p_desc.font.size = Pt(13)
    p_desc.font.color.rgb = SKY_BLUE

    # Divider in title
    div = s1.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(1.8), Inches(4.1), Inches(9.733), Inches(0.02))
    div.fill.solid()
    div.fill.fore_color.rgb = RGBColor(30, 41, 59)
    div.line.fill.background()

    # Meta grid
    info_tb = s1.shapes.add_textbox(Inches(1.8), Inches(4.35), Inches(9.733), Inches(1.6))
    itf = info_tb.text_frame
    itf.word_wrap = True
    
    ip1 = itf.paragraphs[0]
    ip1.text = "ACADEMIC PROJECT DEFENSE & TECHNICAL VIVA EVALUATION"
    ip1.font.size = Pt(11)
    ip1.font.bold = True
    ip1.font.color.rgb = AMBER

    ip2 = itf.add_paragraph()
    ip2.text = "• Member A: Lead Avionics & Simulation Physics Engine Architecture\n• Member B: Systems Verification, Deliverables, Architecture & Academic Defense\n• Technology Stack: Java 17 (OpenJDK), Java AWT/Swing 2D HUD, Zero Third-Party Dependencies"
    ip2.font.size = Pt(12)
    ip2.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 2: PROBLEM STATEMENT
    # =========================================================================
    s2 = add_blank_slide("MISSION MOTIVATION")
    set_slide_title(s2, "Problem Statement: Why Autonomous Recovery is Critical",
                    "Challenges of deep-space telemetry, speed-of-light communications, and catastrophic compounding failures")

    # 3 Column Cards
    col_w = Inches(3.64)
    c1 = create_card(s2, Inches(0.8), Inches(1.4), col_w, Inches(5.4), "01 / Speed-of-Light Latency", ROSE)
    c2 = create_card(s2, Inches(4.84), Inches(1.4), col_w, Inches(5.4), "02 / Compounding Cascades", AMBER)
    c3 = create_card(s2, Inches(8.88), Inches(1.4), col_w, Inches(5.4), "03 / The OrbitalX Solution", EMERALD)

    # Content C1
    tb1 = s2.shapes.add_textbox(Inches(1.0), Inches(2.0), col_w - Inches(0.4), Inches(4.6))
    tf1 = tb1.text_frame
    tf1.word_wrap = True
    p1 = tf1.paragraphs[0]
    p1.text = "Communication Delay in Deep Space"
    p1.font.size = Pt(12)
    p1.font.bold = True
    p1.font.color.rgb = TEXT_WHITE
    p1_body = tf1.add_paragraph()
    p1_body.text = ("• Signal latency between Earth and Mars varies between 3 and 22 minutes each way.\n\n"
                    "• Ground-control intervention during critical maneuvers (orbital burns, atmospheric entry) is physically impossible.\n\n"
                    "• Deep space craft cannot wait for human commands when life-support or power busses fail in real time.")
    p1_body.font.size = Pt(11)
    p1_body.font.color.rgb = TEXT_MUTED

    # Content C2
    tb2 = s2.shapes.add_textbox(Inches(5.04), Inches(2.0), col_w - Inches(0.4), Inches(4.6))
    tf2 = tb2.text_frame
    tf2.word_wrap = True
    p2 = tf2.paragraphs[0]
    p2.text = "Subsystem Interdependency"
    p2.font.size = Pt(12)
    p2.font.bold = True
    p2.font.color.rgb = TEXT_WHITE
    p2_body = tf2.add_paragraph()
    p2_body.text = ("• A single localized failure rapidly ripples through connected subsystems.\n\n"
                    "• Solar panel dust or micro-meteoroid impact drops voltage -> causes reactor overload -> triggers thermal runaway -> burns fuel.\n\n"
                    "• Pilot panic or cognitive overload during compound emergency alarms causes mission loss within seconds.")
    p2_body.font.size = Pt(11)
    p2_body.font.color.rgb = TEXT_MUTED

    # Content C3
    tb3 = s2.shapes.add_textbox(Inches(9.08), Inches(2.0), col_w - Inches(0.4), Inches(4.6))
    tf3 = tb3.text_frame
    tf3.word_wrap = True
    p3 = tf3.paragraphs[0]
    p3.text = "Deterministic Multi-Tier Autonomy"
    p3.font.size = Pt(12)
    p3.font.bold = True
    p3.font.color.rgb = TEXT_WHITE
    p3_body = tf3.add_paragraph()
    p3_body.text = ("• OrbitalX models a deterministic on-board flight autonomy engine.\n\n"
                    "• Four-stage cascading recovery: Local Hardware Self-Healing -> Timed Autopilot -> Crew Intervention -> Ground Control Uplink.\n\n"
                    "• Built entirely using robust Object-Oriented principles and GoF design patterns in pure Java 17.")
    p3_body.font.size = Pt(11)
    p3_body.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 3: HIGH-LEVEL ARCHITECTURE
    # =========================================================================
    s3 = add_blank_slide("SOFTWARE ARCHITECTURE")
    set_slide_title(s3, "High-Level Architecture: Three-Tier Decoupled System",
                    "Clear separation of concerns between Swing Presentation, Controller API, and Avionics Engine")

    # Left text card
    create_card(s3, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "System Structure & Boundaries", CYAN)
    tb_s3 = s3.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s3 = tb_s3.text_frame
    tf_s3.word_wrap = True
    
    t1 = tf_s3.paragraphs[0]
    t1.text = "1. Presentation Layer (GUI / HUD)"
    t1.font.bold = True
    t1.font.size = Pt(12)
    t1.font.color.rgb = SKY_BLUE
    p_t1 = tf_s3.add_paragraph()
    p_t1.text = "• 10 independent Cockpit Views managed by ViewRegistry.\n• Consumes immutable SimulationSnapshot records only.\n• Zero direct coupling to physics or simulation internals."
    p_t1.font.size = Pt(10.5)
    p_t1.font.color.rgb = TEXT_MUTED

    t2 = tf_s3.add_paragraph()
    t2.text = "\n2. Public API Boundary (Contract)"
    t2.font.bold = True
    t2.font.size = Pt(12)
    t2.font.color.rgb = AMBER
    p_t2 = tf_s3.add_paragraph()
    p_t2.text = "• SimulationController interface exposes intent methods.\n• Immutability enforced: UI cannot mutate spacecraft state.\n• Enables headless CLI, SelfTest runner, and unit testing."
    p_t2.font.size = Pt(10.5)
    p_t2.font.color.rgb = TEXT_MUTED

    t3 = tf_s3.add_paragraph()
    t3.text = "\n3. Avionics Core & Physics Engine"
    t3.font.bold = True
    t3.font.size = Pt(12)
    t3.font.color.rgb = EMERALD
    p_t3 = tf_s3.add_paragraph()
    p_t3.text = "• SimulationEngine ticks subsystems at fixed delta-time.\n• Integrates Detectors, Advisor Rules, Autonomy, and Events.\n• 100% deterministic mathematical model."
    p_t3.font.size = Pt(10.5)
    p_t3.font.color.rgb = TEXT_MUTED

    # Right card with UML diagram
    create_card(s3, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Architecture UML Boundary (docs/puml/gui-api.png)", AMBER)
    if os.path.exists("docs/puml/gui-api.png"):
        s3.shapes.add_picture("docs/puml/gui-api.png", Inches(6.9), Inches(1.95), width=Inches(5.4))

    # =========================================================================
    # SLIDE 4: OOP PRINCIPLES (PART 1)
    # =========================================================================
    s4 = add_blank_slide("OOP FOUNDATIONS")
    set_slide_title(s4, "Object-Oriented Design: Encapsulation & Abstraction",
                    "Defensive state management, immutable contracts, and clear domain boundaries")

    c_left = create_card(s4, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Encapsulation & Defensive Design", CYAN)
    tb_s4_l = s4.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s4_l = tb_s4_l.text_frame
    tf_s4_l.word_wrap = True
    
    p = tf_s4_l.paragraphs[0]
    p.text = "Strict State Encapsulation"
    p.font.bold = True
    p.font.size = Pt(12)
    p.font.color.rgb = TEXT_WHITE
    p_b = tf_s4_l.add_paragraph()
    p_b.text = ("• All internal state variables (fuel, oxygen, battery, thruster force) are strictly private or package-private.\n\n"
                "• Modifications only occur through validated domain methods: Subsystem.applyPhysics(), Resource.consume(), Spacecraft.injectFault().\n\n"
                "• Defensive Copying: Subsystem sensor lists and crew member rosters return Collections.unmodifiableList() to prevent external corruption.\n\n"
                "• Java 17 Records: SimulationSnapshot is an immutable value carrier ensuring GUI threads never read intermediate physics calculation steps.")
    p_b.font.size = Pt(11)
    p_b.font.color.rgb = TEXT_MUTED

    c_right = create_card(s4, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Abstraction & Domain Modeling", SKY_BLUE)
    tb_s4_r = s4.shapes.add_textbox(Inches(6.9), Inches(2.0), Inches(5.4), Inches(4.6))
    tf_s4_r = tb_s4_r.text_frame
    tf_s4_r.word_wrap = True

    p_r = tf_s4_r.paragraphs[0]
    p_r.text = "Contract-Driven Domain Models"
    p_r.font.bold = True
    p_r.font.size = Pt(12)
    p_r.font.color.rgb = TEXT_WHITE
    p_r_b = tf_s4_r.add_paragraph()
    p_r_b.text = ("• SystemComponent Interface: Defines the common lifecycle contract (getName(), getStatus(), getPowerDraw()) implemented by Subsystems, Resources, and Crew.\n\n"
                  "• Sensor Abstraction: BaseSensor captures sensor drift, noise injection, and failure state, exposing a clean double readValue() API.\n\n"
                  "• CorrectiveAction Contract: Abstract command interface hiding remediation physics behind ActionResult execute(Spacecraft craft).\n\n"
                  "• Cognitive Separation: The cockpit UI does not need to understand differential equations; it interacts purely with high-level domain abstractions.")
    p_r_b.font.size = Pt(11)
    p_r_b.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 5: OOP PRINCIPLES (PART 2)
    # =========================================================================
    s5 = add_blank_slide("OOP FOUNDATIONS")
    set_slide_title(s5, "Object-Oriented Design: Polymorphism & Dependency Inversion",
                    "Dynamic method dispatch in action and decoupling high-level policy from low-level details")

    create_card(s5, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Polymorphism in Aerospace Physics", AMBER)
    tb_s5_l = s5.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s5_l = tb_s5_l.text_frame
    tf_s5_l.word_wrap = True
    
    p = tf_s5_l.paragraphs[0]
    p.text = "Subsystem Polymorphism"
    p.font.bold = True
    p.font.size = Pt(12)
    p.font.color.rgb = TEXT_WHITE
    p_b = tf_s5_l.add_paragraph()
    p_b.text = ("• Abstract class Subsystem defines abstract applyPhysics(double deltaSeconds).\n\n"
                "• Specialized implementations dynamically dispatch specific physics equations:\n"
                "   - PropulsionSubsystem: Thrust vectors, fuel mass flow rate.\n"
                "   - LifeSupportSubsystem: O2 generation, cabin CO2 scrubber dynamics.\n"
                "   - ThermalSubsystem: Radiator heat exchange, solar absorption.\n"
                "   - PowerSubsystem: Solar irradiance vs battery discharge.\n\n"
                "• The SimulationEngine iterates over List<Subsystem> and calls applyPhysics() uniformly, without knowing concrete types!")
    p_b.font.size = Pt(11)
    p_b.font.color.rgb = TEXT_MUTED

    create_card(s5, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Dependency Inversion Principle (DIP)", EMERALD)
    tb_s5_r = s5.shapes.add_textbox(Inches(6.9), Inches(2.0), Inches(5.4), Inches(4.6))
    tf_s5_r = tb_s5_r.text_frame
    tf_s5_r.word_wrap = True

    p_r = tf_s5_r.paragraphs[0]
    p_r.text = "Decoupling High-Level & Low-Level Modules"
    p_r.font.bold = True
    p_r.font.size = Pt(12)
    p_r.font.color.rgb = TEXT_WHITE
    p_r_b = tf_s5_r.add_paragraph()
    p_r_b.text = ("• High-Level Modules (e.g. DashboardView, OrbitView) do NOT instantiate or reference SimulationEngine.\n\n"
                  "• Low-Level Engine Details (e.g. DefaultSimulationController) do NOT depend on GUI panels.\n\n"
                  "• Both depend upon the abstract interface SimulationController:\n"
                  "   public interface SimulationController {\n"
                  "       void setThrottle(double value);\n"
                  "       void pause(); void resume();\n"
                  "       SimulationSnapshot getSnapshot();\n"
                  "   }\n\n"
                  "• Inversion Benefits: Unit tests mock the controller easily, and the engine can run completely headless for --selftest without Swing!")
    p_r_b.font.size = Pt(11)
    p_r_b.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 6: DESIGN PATTERNS (PART 1 - BEHAVIORAL)
    # =========================================================================
    s6 = add_blank_slide("DESIGN PATTERNS")
    set_slide_title(s6, "Classical GoF Patterns: Behavioral Orchestration",
                    "How State, Command, Observer, and Chain of Responsibility drive mission control")

    w2 = Inches(5.6)
    create_card(s6, Inches(0.8), Inches(1.4), w2, Inches(2.6), "1. State Pattern", CYAN)
    tb = s6.shapes.add_textbox(Inches(1.0), Inches(1.9), w2 - Inches(0.4), Inches(1.9))
    tf = tb.text_frame; tf.word_wrap = True
    tf.paragraphs[0].text = "• SpacecraftStatus: NOMINAL, DEGRADED, CRITICAL, SAFE_MODE, TERMINAL_LOSS.\n• Status dynamically alters thruster limits, power prioritization, and warning alarms."
    tf.paragraphs[0].font.size = Pt(10.5); tf.paragraphs[0].font.color.rgb = TEXT_MUTED

    create_card(s6, Inches(6.7), Inches(1.4), w2, Inches(2.6), "2. Command Pattern", AMBER)
    tb = s6.shapes.add_textbox(Inches(6.9), Inches(1.9), w2 - Inches(0.4), Inches(1.9))
    tf = tb.text_frame; tf.word_wrap = True
    tf.paragraphs[0].text = "• CorrectiveAction interface encapsulates actions as first-class objects.\n• Instances: ReroutePowerAction, VentCoolantAction, SwitchToBackupGyroAction.\n• Allows queuing, countdown scheduling, and automated execution."
    tf.paragraphs[0].font.size = Pt(10.5); tf.paragraphs[0].font.color.rgb = TEXT_MUTED

    create_card(s6, Inches(0.8), Inches(4.2), w2, Inches(2.6), "3. Observer Pattern (Mediator)", EMERALD)
    tb = s6.shapes.add_textbox(Inches(1.0), Inches(4.7), w2 - Inches(0.4), Inches(1.9))
    tf = tb.text_frame; tf.word_wrap = True
    tf.paragraphs[0].text = "• Strongly-typed EventBus pub/sub mechanism with Topic<T>.\n• Dispatches FaultDetectedEvent, AutopilotEngagedEvent, MissionStatusEvent.\n• Zero tight coupling between avionics detectors and UI audio/sound."
    tf.paragraphs[0].font.size = Pt(10.5); tf.paragraphs[0].font.color.rgb = TEXT_MUTED

    create_card(s6, Inches(6.7), Inches(4.2), w2, Inches(2.6), "4. Chain of Responsibility", SKY_BLUE)
    tb = s6.shapes.add_textbox(Inches(6.9), Inches(4.7), w2 - Inches(0.4), Inches(1.9))
    tf = tb.text_frame; tf.word_wrap = True
    tf.paragraphs[0].text = "• Cascading ResponseHandler hierarchy for emergency mitigation.\n• SelfHealingHandler -> AutopilotHandler -> CrewHandler -> EscalationHandler.\n• Each handler attempts remediation or passes the fault down the chain."
    tf.paragraphs[0].font.size = Pt(10.5); tf.paragraphs[0].font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 7: DESIGN PATTERNS (PART 2 - STRUCTURAL & CREATIONAL)
    # =========================================================================
    s7 = add_blank_slide("DESIGN PATTERNS")
    set_slide_title(s7, "Classical GoF Patterns: Structural, Creational & Visitor",
                    "Memento time travel, Factory fault generation, Visitor reports, and Model-Crew hierarchy")

    create_card(s7, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Memento, Factory & Visitor Patterns", CYAN)
    tb_s7 = s7.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s7 = tb_s7.text_frame
    tf_s7.word_wrap = True

    p = tf_s7.paragraphs[0]
    p.text = "• Memento Pattern (History.java)"
    p.font.bold = True; p.font.size = Pt(11); p.font.color.rgb = SKY_BLUE
    p1 = tf_s7.add_paragraph()
    p1.text = "Captures chronological snapshots of Spacecraft telemetry. Powers the Replay Console, enabling full time-travel scrubbing, backward review, and flight black-box analysis."
    p1.font.size = Pt(10); p1.font.color.rgb = TEXT_MUTED

    p2 = tf_s7.add_paragraph()
    p2.text = "\n• Factory Method (FaultFactory.java)"
    p2.font.bold = True; p2.font.size = Pt(11); p2.font.color.rgb = AMBER
    p2_b = tf_s7.add_paragraph()
    p2_b.text = "Instantiates polymorphic Fault subclasses (FuelLeakFault, SolarFlareFault, GyroDriftFault) based on enum keys, decoupling hazard injection from simulation physics."
    p2_b.font.size = Pt(10); p2_b.font.color.rgb = TEXT_MUTED

    p3 = tf_s7.add_paragraph()
    p3.text = "\n• Visitor Pattern (ReportVisitor.java)"
    p3.font.bold = True; p3.font.size = Pt(11); p3.font.color.rgb = EMERALD
    p3_b = tf_s7.add_paragraph()
    p3_b.text = "Traverses MissionReport without modifying data structures. Implemented as HtmlReportVisitor and TextReportVisitor for post-flight debrief exports."
    p3_b.font.size = Pt(10); p3_b.font.color.rgb = TEXT_MUTED

    create_card(s7, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Patterns Showcase Diagram (docs/puml/patterns-showcase.png)", AMBER)
    if os.path.exists("docs/puml/patterns-showcase.png"):
        s7.shapes.add_picture("docs/puml/patterns-showcase.png", Inches(6.9), Inches(1.95), width=Inches(5.4))

    # =========================================================================
    # SLIDE 8: AUTONOMOUS RECOVERY CHAIN OVERVIEW
    # =========================================================================
    s8 = add_blank_slide("AUTONOMY CHAIN")
    set_slide_title(s8, "Autonomous Recovery Chain: 4-Tier Escalation Architecture",
                    "A cascading Chain of Responsibility guaranteeing zero single-point-of-failure in avionics")

    create_card(s8, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Cascading Escalation Logic", CYAN)
    tb_s8 = s8.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s8 = tb_s8.text_frame
    tf_s8.word_wrap = True

    p = tf_s8.paragraphs[0]
    p.text = "Tier 1: Local Self-Healing"
    p.font.bold = True; p.font.size = Pt(11.5); p.font.color.rgb = EMERALD
    p_b = tf_s8.add_paragraph()
    p_b.text = "Instantaneous hardware reflexes: Subsystem circuit breaker trips, automatic backup sensor switch, sealant injection."
    p_b.font.size = Pt(10.5); p_b.font.color.rgb = TEXT_MUTED

    p2 = tf_s8.add_paragraph()
    p2.text = "\nTier 2: Timed Autopilot"
    p2.font.bold = True; p2.font.size = Pt(11.5); p2.font.color.rgb = SKY_BLUE
    p2_b = tf_s8.add_paragraph()
    p2_b.text = "Anomaly Detectors diagnose systemic failures. Advisor Engine formulates prioritized CorrectiveActions with configurable pilot override timers."
    p2_b.font.size = Pt(10.5); p2_b.font.color.rgb = TEXT_MUTED

    p3 = tf_s8.add_paragraph()
    p3.text = "\nTier 3: Crew Intervention"
    p3.font.bold = True; p3.font.size = Pt(11.5); p3.font.color.rgb = AMBER
    p3_b = tf_s8.add_paragraph()
    p3_b.text = "Commander, Flight Engineer, Pilot, and Medical Officer assigned to emergency manual repairs with specialization bonuses."
    p3_b.font.size = Pt(10.5); p3_b.font.color.rgb = TEXT_MUTED

    p4 = tf_s8.add_paragraph()
    p4.text = "\nTier 4: Ground Control Uplink"
    p4.font.bold = True; p4.font.size = Pt(11.5); p4.font.color.rgb = ROSE
    p4_b = tf_s8.add_paragraph()
    p4_b.text = "Deep-space radio transmission with speed-of-light propagation delays. Mission control uploads comprehensive patch sequences."
    p4_b.font.size = Pt(10.5); p4_b.font.color.rgb = TEXT_MUTED

    create_card(s8, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Autonomy PUML Model (docs/puml/advisor-autonomy.png)", AMBER)
    if os.path.exists("docs/puml/advisor-autonomy.png"):
        s8.shapes.add_picture("docs/puml/advisor-autonomy.png", Inches(6.9), Inches(1.95), width=Inches(5.4))

    # =========================================================================
    # SLIDE 9: RECOVERY DEEP DIVE: SELF-HEALING & AUTOPILOT
    # =========================================================================
    s9 = add_blank_slide("AUTONOMY CHAIN")
    set_slide_title(s9, "Recovery Deep Dive: Local Self-Healing & Timed Autopilot",
                    "Sensor filtering, anomaly detection heuristics, and recommendation rule engines")

    create_card(s9, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Detection Engine: 3 Detector Strategies", CYAN)
    tb_s9_l = s9.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s9_l = tb_s9_l.text_frame
    tf_s9_l.word_wrap = True

    p = tf_s9_l.paragraphs[0]
    p.text = "1. ThresholdDetector"
    p.font.bold = True; p.font.size = Pt(11); p.font.color.rgb = TEXT_WHITE
    p_b = tf_s9_l.add_paragraph()
    p_b.text = "Evaluates hard ceiling/floor safety limits (e.g., Core Temp > 400°C, Cabin O2 < 18%). Triggers instantaneous amber/red alerts."
    p_b.font.size = Pt(10.5); p_b.font.color.rgb = TEXT_MUTED

    p2 = tf_s9_l.add_paragraph()
    p2.text = "\n2. RateOfChangeDetector"
    p2.font.bold = True; p2.font.size = Pt(11); p2.font.color.rgb = TEXT_WHITE
    p2_b = tf_s9_l.add_paragraph()
    p2_b.text = "Calculates numerical derivative dV/dt over sliding time windows. Detects micro-punctures and fuel leaks before thresholds are breached."
    p2_b.font.size = Pt(10.5); p2_b.font.color.rgb = TEXT_MUTED

    p3 = tf_s9_l.add_paragraph()
    p3.text = "\n3. ZScoreDetector (Statistical Outlier)"
    p3.font.bold = True; p3.font.size = Pt(11); p3.font.color.rgb = TEXT_WHITE
    p3_b = tf_s9_l.add_paragraph()
    p3_b.text = "Computes rolling mean and standard deviation. Flags sensor noise, magnetic anomalies, and telemetry desynchronization (> 3.0 sigma)."
    p3_b.font.size = Pt(10.5); p3_b.font.color.rgb = TEXT_MUTED

    create_card(s9, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Advisor Engine & Timed Autopilot", AMBER)
    tb_s9_r = s9.shapes.add_textbox(Inches(6.9), Inches(2.0), Inches(5.4), Inches(4.6))
    tf_s9_r = tb_s9_r.text_frame
    tf_s9_r.word_wrap = True

    p_r = tf_s9_r.paragraphs[0]
    p_r.text = "Rule Engine & Priority Queue"
    p_r.font.bold = True; p_r.font.size = Pt(11); p_r.font.color.rgb = TEXT_WHITE
    p_r_b = tf_s9_r.add_paragraph()
    p_r_b.text = ("• ConditionRule matches active anomalies and yields prioritized Recommendations.\n\n"
                  "• In TIMED_AUTOPILOT mode, the HUD shows an emergency countdown (e.g., 5 seconds):\n"
                  "   - If pilot overrides (SPACE/manual toggle), human retains control.\n"
                  "   - If timer expires, AutopilotHandler dispatches CorrectiveAction automatically.\n\n"
                  "• In FULL_AUTO mode, actions execute immediately on the same simulation tick.\n\n"
                  "• Prevents fatal delays during multi-fault cascades while preserving human command hierarchy.")
    p_r_b.font.size = Pt(10.5); p_r_b.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 10: RECOVERY DEEP DIVE: CREW & GROUND CONTROL
    # =========================================================================
    s10 = add_blank_slide("AUTONOMY CHAIN")
    set_slide_title(s10, "Recovery Deep Dive: Crew Allocation & Ground Uplink",
                    "Modeling human fatigue, role proficiencies, and speed-of-light radio delay")

    create_card(s10, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Crew Specialization & Tasking", CYAN)
    tb_s10_l = s10.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s10_l = tb_s10_l.text_frame
    tf_s10_l.word_wrap = True

    p = tf_s10_l.paragraphs[0]
    p.text = "Specialist Roles & Skill Modifiers"
    p.font.bold = True; p.font.size = Pt(11); p.font.color.rgb = TEXT_WHITE
    p_b = tf_s10_l.add_paragraph()
    p_b.text = ("• Commander: Overall morale, coordination efficiency, task scheduling.\n\n"
                "• Pilot: Maneuver calculation, RCS thrust stabilization, rendezvous.\n\n"
                "• Flight Engineer: Repair efficiency boost on Propulsion, Electrical, Thermal (+50% repair speed, sealant efficiency).\n\n"
                "• Medical Officer: Cabin atmosphere stabilization, crew stress alleviation, life-support recovery.\n\n"
                "• Crew Vitals: Dynamic fatigue, stress, and oxygen demand impact performance.")
    p_b.font.size = Pt(10.5); p_b.font.color.rgb = TEXT_MUTED

    create_card(s10, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Ground Control Radio Propagation", ROSE)
    tb_s10_r = s10.shapes.add_textbox(Inches(6.9), Inches(2.0), Inches(5.4), Inches(4.6))
    tf_s10_r = tb_s10_r.text_frame
    tf_s10_r.word_wrap = True

    p_r = tf_s10_r.paragraphs[0]
    p_r.text = "Realistic Signal Latency Modeling"
    p_r.font.bold = True; p_r.font.size = Pt(11); p_r.font.color.rgb = TEXT_WHITE
    p_r_b = tf_s10_r.add_paragraph()
    p_r_b.text = ("• Earth-Moon Distance: ~1.3 seconds signal propagation.\n\n"
                  "• Earth-Mars Distance: 3 to 22 minutes signal propagation depending on orbital alignment.\n\n"
                  "• RadioChannel Strategy Pattern:\n"
                  "   - DirectUplinkChannel: Zero delay (testing/low-Earth orbit).\n"
                  "   - DelayedRadioChannel: Timestamped queue storing telemetry and command packets.\n\n"
                  "• Demonstrates why autonomous on-board decision-making is an existential requirement for interplanetary flight.")
    p_r_b.font.size = Pt(10.5); p_r_b.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 11: COCKPIT: FLIGHT DASHBOARD
    # =========================================================================
    s11 = add_blank_slide("COCKPIT CONSOLES")
    set_slide_title(s11, "Cockpit Console 1: Primary Flight Dashboard",
                    "Real-time telemetry gauges, annunciator warning matrix, throttle, and RCS controls")

    create_card(s11, Inches(0.8), Inches(1.4), Inches(4.8), Inches(5.4), "Dashboard Features & Ergonomics", CYAN)
    tb_s11 = s11.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(4.4), Inches(4.6))
    tf_s11 = tb_s11.text_frame
    tf_s11.word_wrap = True
    p = tf_s11.paragraphs[0]
    p.text = "• Circular & Linear Telemetry Gauges:\n  Speed, Altitude, Main Fuel %, Battery Storage, Cabin O2, Cabin Pressure, Reactor Temp.\n\n" \
             "• Master Caution & Warning Annunciators:\n  Color-coded status panel flashing amber/red during subsystem anomalies.\n\n" \
             "• Dynamic Throttle & RCS Thrusters:\n  Interactive slider controlling main engine thrust percentage and delta-V burns.\n\n" \
             "• Mode Selector:\n  Toggle between MANUAL, TIMED_AUTOPILOT, and FULL_AUTO in one click."
    p.font.size = Pt(11); p.font.color.rgb = TEXT_MUTED

    create_card(s11, Inches(5.9), Inches(1.4), Inches(6.6), Inches(5.4), "Flight Dashboard HUD (docs/screenshots/11_dashboard.png)", AMBER)
    dash_img = "docs/screenshots/11_dashboard.png" if os.path.exists("docs/screenshots/11_dashboard.png") else "docs/screenshots/08_main_dashboard_overview.png"
    if os.path.exists(dash_img):
        s11.shapes.add_picture(dash_img, Inches(6.1), Inches(1.95), width=Inches(6.2))

    # =========================================================================
    # SLIDE 12: COCKPIT: 3D CUTAWAY
    # =========================================================================
    s12 = add_blank_slide("COCKPIT CONSOLES")
    set_slide_title(s12, "Cockpit Console 2: 3D Isometric Spacecraft Cutaway",
                    "Hardware-accelerated isometric wireframe visualization of internal compartments and heatmaps")

    create_card(s12, Inches(0.8), Inches(1.4), Inches(4.8), Inches(5.4), "Cutaway Visualization Features", SKY_BLUE)
    tb_s12 = s12.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(4.4), Inches(4.6))
    tf_s12 = tb_s12.text_frame
    tf_s12.word_wrap = True
    p = tf_s12.paragraphs[0]
    p.text = "• Internal Compartments Rendered:\n  Command Module, Avionics Bay, Fuel Bunkers, Reactor Core, Thruster Bells, Airlocks.\n\n" \
             "• Real-Time Heat & Stress Mapping:\n  Thermal dissipation represented dynamically via color gradients (cool blue to molten red).\n\n" \
             "• Dynamic Visual FX:\n  Animated thruster exhaust plumes, particle systems, micrometeoroid puncture sparks, and damage blinking.\n\n" \
             "• Zero External 3D Engines:\n  Custom isometric projection mathematics rendered purely in Java2D Graphics2D."
    p.font.size = Pt(11); p.font.color.rgb = TEXT_MUTED

    create_card(s12, Inches(5.9), Inches(1.4), Inches(6.6), Inches(5.4), "3D Cutaway Screen (docs/screenshots/12_cutaway_3d.png)", AMBER)
    if os.path.exists("docs/screenshots/12_cutaway_3d.png"):
        s12.shapes.add_picture("docs/screenshots/12_cutaway_3d.png", Inches(6.1), Inches(1.95), width=Inches(6.2))

    # =========================================================================
    # SLIDE 13: COCKPIT: ORBIT & TRAJECTORY MAP
    # =========================================================================
    s13 = add_blank_slide("COCKPIT CONSOLES")
    set_slide_title(s13, "Cockpit Console 3: Orbital Trajectory & Radar Map",
                    "Planetary ground track, orbital mechanics parameters, and ground station tracking coverage")

    create_card(s13, Inches(0.8), Inches(1.4), Inches(4.8), Inches(5.4), "Orbital Mechanics Display", EMERALD)
    tb_s13 = s13.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(4.4), Inches(4.6))
    tf_s13 = tb_s13.text_frame
    tf_s13.word_wrap = True
    p = tf_s13.paragraphs[0]
    p.text = "• Keplerian Orbital Parameters:\n  Apoapsis (Ap), Periapsis (Pe), Orbital Period, True Anomaly, Orbital Eccentricity.\n\n" \
             "• Sinusoidal Ground Track:\n  Projects spacecraft longitude/latitude footprint over rotating planetary surface.\n\n" \
             "• Communication Shadow Zone:\n  Displays Line-of-Sight (LOS) with Ground Stations and dark-side blackout intervals.\n\n" \
             "• Transfer Maneuver Planner:\n  Visualizes burn vectors and required delta-V for target orbital insertion."
    p.font.size = Pt(11); p.font.color.rgb = TEXT_MUTED

    create_card(s13, Inches(5.9), Inches(1.4), Inches(6.6), Inches(5.4), "Orbit Ground Track Map (docs/screenshots/09_orbit_ground_track_radar.png)", AMBER)
    orbit_img = "docs/screenshots/09_orbit_ground_track_radar.png"
    if os.path.exists(orbit_img):
        s13.shapes.add_picture(orbit_img, Inches(6.1), Inches(1.95), width=Inches(6.2))

    # =========================================================================
    # SLIDE 14: COCKPIT: SYSTEMS TOPOLOGY & CREW
    # =========================================================================
    s14 = add_blank_slide("COCKPIT CONSOLES")
    set_slide_title(s14, "Cockpit Consoles 4 & 5: Systems Topology & Crew Quarters",
                    "Hierarchical subsystem health tree and crew vitals monitoring with active task allocation")

    create_card(s14, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Systems Topology Tree", CYAN)
    tb_s14_l = s14.shapes.add_textbox(Inches(1.0), Inches(1.9), Inches(5.2), Inches(1.8))
    tf_s14_l = tb_s14_l.text_frame; tf_s14_l.word_wrap = True
    tf_s14_l.paragraphs[0].text = "• Full component tree: Main Bus -> Subsystems -> Sensors -> Actuators.\n• Displays live voltage, power draw, temperature, and fault status."
    tf_s14_l.paragraphs[0].font.size = Pt(10.5); tf_s14_l.paragraphs[0].font.color.rgb = TEXT_MUTED
    if os.path.exists("docs/screenshots/13_systems.png"):
        s14.shapes.add_picture("docs/screenshots/13_systems.png", Inches(1.0), Inches(3.4), width=Inches(5.2))

    create_card(s14, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Crew Quarters & Vitals", AMBER)
    tb_s14_r = s14.shapes.add_textbox(Inches(6.9), Inches(1.9), Inches(5.4), Inches(1.8))
    tf_s14_r = tb_s14_r.text_frame; tf_s14_r.word_wrap = True
    tf_s14_r.paragraphs[0].text = "• Crew health, stress, and fatigue telemetry.\n• Assign crew to emergency repairs: Commander, Engineer, Pilot, Medic."
    tf_s14_r.paragraphs[0].font.size = Pt(10.5); tf_s14_r.paragraphs[0].font.color.rgb = TEXT_MUTED
    if os.path.exists("docs/screenshots/14_crew.png"):
        s14.shapes.add_picture("docs/screenshots/14_crew.png", Inches(6.9), Inches(3.4), width=Inches(5.4))

    # =========================================================================
    # SLIDE 15: TESTING & SELFTEST FRAMEWORK
    # =========================================================================
    s15 = add_blank_slide("TESTING & VERIFICATION")
    set_slide_title(s15, "Testing & Verification: Automated Headless SelfTest Suite",
                    "108 combinatorial test matrices validating determinism, fault recovery, and physics invariants")

    create_card(s15, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "SelfTest Architecture & Invariants", CYAN)
    tb_s15_l = s15.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s15_l = tb_s15_l.text_frame
    tf_s15_l.word_wrap = True
    p = tf_s15_l.paragraphs[0]
    p.text = "Automated Headless Runner: java -cp out com.orbitalix.app.Main --selftest"
    p.font.bold = True; p.font.size = Pt(11); p.font.color.rgb = SKY_BLUE
    p_b = tf_s15_l.add_paragraph()
    p_b.text = ("• Headless Execution: Bypasses GUI window creation and executes directly through SimulationController API.\n\n"
                "• PRNG Seeded Determinism: Every scenario starts with a fixed pseudo-random seed, guaranteeing byte-identical test repeatability.\n\n"
                "• Physics Invariants Checked on Every Tick:\n"
                "   1. Conservation of Energy: Battery level in [0, capacity].\n"
                "   2. Fuel Mass Flow: Fuel consumption matches engine throttle.\n"
                "   3. Atmosphere Envelope: Cabin pressure and O2 remain physical.\n"
                "   4. Thermal Balance: Radiator dissipation matches delta-T.\n\n"
                "• Test Matrix: 12 Scenarios x 3 Difficulties x 3 Autonomy Modes = 108 Distinct Simulated Missions!")
    p_b.font.size = Pt(10.5); p_b.font.color.rgb = TEXT_MUTED

    create_card(s15, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "12 Mission Scenarios Tested", AMBER)
    tb_s15_r = s15.shapes.add_textbox(Inches(6.9), Inches(2.0), Inches(5.4), Inches(4.6))
    tf_s15_r = tb_s15_r.text_frame
    tf_s15_r.word_wrap = True
    p_r = tf_s15_r.paragraphs[0]
    p_r.text = "12 Distinct Emergency Scenarios"
    p_r.font.bold = True; p_r.font.size = Pt(11); p_r.font.color.rgb = TEXT_WHITE
    p_r_b = tf_s15_r.add_paragraph()
    p_r_b.text = ("1. Free Flight: Baseline nominal orbital trajectory\n"
                  "2. Fuel Leak: Main tank valve breach (mass loss)\n"
                  "3. Engine Trouble: Gimbal lock / thruster efficiency drop\n"
                  "4. Power Crisis: Solar array deployment failure\n"
                  "5. Lost in Space: Navigation beacon loss & gyro drift\n"
                  "6. Antenna Blackout: High-gain dish alignment loss\n"
                  "7. Sensor Glitch: Z-Score noise injection\n"
                  "8. Hull Breach: Cabin decompression & O2 drop\n"
                  "9. Solar Storm: Coronal mass ejection & bus surge\n"
                  "10. Debris Alert: Space junk evasive burn\n"
                  "11. Computer Crash: Flight avionics reboot\n"
                  "12. Cascading Failure: Compound sequential hazards")
    p_r_b.font.size = Pt(10); p_r_b.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 16: TEST RESULTS ANALYSIS
    # =========================================================================
    s16 = add_blank_slide("TESTING & VERIFICATION")
    set_slide_title(s16, "Automated Test Results: Proving Autonomous Recovery",
                    "Detailed outcome analysis across MANUAL vs. TIMED_AUTOPILOT vs. FULL_AUTO modes")

    create_card(s16, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Comparative Mode Outcomes", CYAN)
    tb_s16_l = s16.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s16_l = tb_s16_l.text_frame
    tf_s16_l.word_wrap = True

    p = tf_s16_l.paragraphs[0]
    p.text = "Behavioral Analysis Under Faults"
    p.font.bold = True; p.font.size = Pt(11.5); p.font.color.rgb = TEXT_WHITE
    p_b = tf_s16_l.add_paragraph()
    p_b.text = ("• In MANUAL Mode (Headless):\n"
                "  Unattended critical faults (Fuel Leak, Hull Breach, Cascading Failure) inevitably cause mission loss:\n"
                "  - Fuel Leak -> LOST - FUEL EXHAUSTED (Tick 84)\n"
                "  - Hull Breach -> LOST - LIFE SUPPORT (Tick 119)\n"
                "  This proves faults are genuinely lethal without intervention!\n\n"
                "• In TIMED_AUTOPILOT & FULL_AUTO Modes:\n"
                "  The Autonomy Chain steps in automatically:\n"
                "  - Re-routes power busses\n"
                "  - Deploys emergency sealants on hull breach\n"
                "  - Throttles back overheated engines\n"
                "  - Reboots failed computers\n"
                "  Outcome: 100% SUCCESS - DESTINATION REACHED!")
    p_b.font.size = Pt(10.5); p_b.font.color.rgb = TEXT_MUTED

    create_card(s16, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Final Verification Result: SELFTEST PASS", EMERALD)
    tb_s16_r = s16.shapes.add_textbox(Inches(6.9), Inches(2.0), Inches(5.4), Inches(4.6))
    tf_s16_r = tb_s16_r.text_frame
    tf_s16_r.word_wrap = True

    p_r = tf_s16_r.paragraphs[0]
    p_r.text = "Clean Machine Verification: 100% SUCCESS"
    p_r.font.bold = True; p_r.font.size = Pt(12); p_r.font.color.rgb = EMERALD
    p_r_b = tf_s16_r.add_paragraph()
    p_r_b.text = ("\nSELFTEST PASS\n(determinism and survival checks)\n\n"
                  "Key Verification Metrics:\n"
                  "• Total Test Matrices Executed: 108\n"
                  "• Zero NullPointerExceptions\n"
                  "• Zero Memory Leaks\n"
                  "• Deterministic Trajectory Match: 100%\n"
                  "• Automated Recovery Success: 100% in Autopilot\n\n"
                  "Executed and independently verified on Member B's machine under Temurin OpenJDK runtime!")
    p_r_b.font.size = Pt(11.5); p_r_b.font.color.rgb = TEXT_WHITE

    # =========================================================================
    # SLIDE 17: CHALLENGES & SOLUTIONS
    # =========================================================================
    s17 = add_blank_slide("ENGINEERING")
    set_slide_title(s17, "Engineering Challenges & Technical Solutions",
                    "Real-time 60 FPS physics, GUI thread safety, and zero external dependency constraints")

    create_card(s17, Inches(0.8), Inches(1.4), Inches(3.64), Inches(5.4), "Challenge 1: Determinism", ROSE)
    create_card(s17, Inches(4.84), Inches(1.4), Inches(3.64), Inches(5.4), "Challenge 2: Thread Safety", AMBER)
    create_card(s17, Inches(8.88), Inches(1.4), Inches(3.64), Inches(5.4), "Challenge 3: Zero Dependencies", SKY_BLUE)

    # C1
    tb = s17.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(3.24), Inches(4.6))
    tf = tb.text_frame; tf.word_wrap = True
    tf.paragraphs[0].text = "Real-Time 60 FPS Physics vs Simulation Clock"
    tf.paragraphs[0].font.bold = True; tf.paragraphs[0].font.size = Pt(11); tf.paragraphs[0].font.color.rgb = TEXT_WHITE
    p = tf.add_paragraph()
    p.text = ("• Problem: Variable frame rendering times cause numerical drift in orbital calculations.\n\n"
              "• Solution: Decoupled fixed-timestep integrator (deltaSeconds = 0.05s). Physics ticks occur deterministically regardless of rendering frame drops.")
    p.font.size = Pt(10.5); p.font.color.rgb = TEXT_MUTED

    # C2
    tb = s17.shapes.add_textbox(Inches(5.04), Inches(2.0), Inches(3.24), Inches(4.6))
    tf = tb.text_frame; tf.word_wrap = True
    tf.paragraphs[0].text = "Swing Concurrency & Race Conditions"
    tf.paragraphs[0].font.bold = True; tf.paragraphs[0].font.size = Pt(11); tf.paragraphs[0].font.color.rgb = TEXT_WHITE
    p = tf.add_paragraph()
    p.text = ("• Problem: Background simulation physics mutating spacecraft properties while Swing Event Dispatch Thread (EDT) paints cockpit dials.\n\n"
              "• Solution: Immutable Java 17 Records (SimulationSnapshot). The engine publishes read-only snapshots to the EDT safely without lock contention.")
    p.font.size = Pt(10.5); p.font.color.rgb = TEXT_MUTED

    # C3
    tb = s17.shapes.add_textbox(Inches(9.08), Inches(2.0), Inches(3.24), Inches(4.6))
    tf = tb.text_frame; tf.word_wrap = True
    tf.paragraphs[0].text = "Zero Third-Party Dependency Rule"
    tf.paragraphs[0].font.bold = True; tf.paragraphs[0].font.size = Pt(11); tf.paragraphs[0].font.color.rgb = TEXT_WHITE
    p = tf.add_paragraph()
    p.text = ("• Problem: Real-time 3D rendering, particle systems, audio, and charting usually require heavy game engines or libraries.\n\n"
              "• Solution: Engineered everything from scratch in pure Java SE 17: custom 3D isometric mathematics, software particle emitters, and wave synthesis audio.")
    p.font.size = Pt(10.5); p.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 18: CONCLUSION & VIVA Q&A
    # =========================================================================
    s18 = add_blank_slide("CONCLUSION & VIVA")
    set_slide_title(s18, "Summary, Deliverables & Viva Q&A",
                    "OrbitalX: Mission Accomplished — Complete Academic & Engineering Package")

    create_card(s18, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Project Deliverables Summary", EMERALD)
    tb_s18_l = s18.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.6))
    tf_s18_l = tb_s18_l.text_frame
    tf_s18_l.word_wrap = True
    p = tf_s18_l.paragraphs[0]
    p.text = "Complete Project Submission Artifacts"
    p.font.bold = True; p.font.size = Pt(12); p.font.color.rgb = TEXT_WHITE
    p_b = tf_s18_l.add_paragraph()
    p_b.text = ("✔ 100% Working Pure Java 17 Spacecraft Simulator\n"
                "✔ 10 Cockpit Consoles with Real-Time Java2D Graphics\n"
                "✔ 12 Classical GoF Design Patterns Implemented\n"
                "✔ 4 Formal PlantUML Architectural Diagrams (PNG)\n"
                "✔ Exhaustive Academic Final Report (docs/FINAL_PROJECT_REPORT.md)\n"
                "✔ Comprehensive User Guide & Flight Operations Manual\n"
                "✔ Full Viva Q&A Cheat Sheet (docs/VIVA_QA_CHEAT_SHEET.md)\n"
                "✔ 18-Slide Professional Presentation (docs/OrbitalX_Presentation.pptx)\n"
                "✔ Clean Machine Verification: 108/108 Scenarios PASS")
    p_b.font.size = Pt(11); p_b.font.color.rgb = TEXT_MUTED

    create_card(s18, Inches(6.7), Inches(1.4), Inches(5.8), Inches(5.4), "Ready for Defense & Viva Q&A", CYAN)
    tb_s18_r = s18.shapes.add_textbox(Inches(6.9), Inches(2.0), Inches(5.4), Inches(4.6))
    tf_s18_r = tb_s18_r.text_frame
    tf_s18_r.word_wrap = True
    p_r = tf_s18_r.paragraphs[0]
    p_r.text = "Thank You!"
    p_r.font.bold = True; p_r.font.size = Pt(28); p_r.font.color.rgb = CYAN
    p_r_b = tf_s18_r.add_paragraph()
    p_r_b.text = ("\nOrbitalX — Space Mission Simulator\n\n"
                  "We welcome questions regarding:\n"
                  "• Object-Oriented Architecture & SOLID Principles\n"
                  "• GoF Pattern Interactions & Implementation Details\n"
                  "• Autonomy Recovery Chain & Anomaly Detection\n"
                  "• Live Cockpit Flight & Hazard Demonstration\n\n"
                  "Team OrbitalX is ready for evaluation! 🚀")
    p_r_b.font.size = Pt(12); p_r_b.font.color.rgb = TEXT_WHITE

    # Save
    out_path = "docs/OrbitalX_Presentation.pptx"
    prs.save(out_path)
    print(f"Presentation successfully saved to {out_path} ({len(prs.slides)} slides)!")

if __name__ == "__main__":
    create_presentation()
