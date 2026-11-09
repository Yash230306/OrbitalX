package com.orbitalix.report;

import com.orbitalix.api.*;
import java.util.stream.Collectors;

public final class HtmlReportVisitor implements ReportVisitor<String> {
    @Override public String visit(MissionReport report) {
        SimulationSnapshot snapshot = report.current();
        String header = """
                <html><body style='font-family:sans-serif;background:#101722;color:#e8f1ff;padding:24px'>
                <h1>OrbitalX Mission Report</h1>
                """;
        String footer = """
                </body></html>
                """;
        String timeline = report.events().stream().map(event -> "<li>T+" + event.tick() + " [" + event.severity()
                + "] " + escape(event.message()) + "</li>").collect(Collectors.joining());
        String resources = snapshot.resources().stream().map(resource -> "<tr><td>" + escape(resource.name())
                + "</td><td>" + String.format(java.util.Locale.ROOT, "%.1f", resource.level()) + " "
                + escape(resource.unit()) + "</td></tr>").collect(Collectors.joining());
        return header + "<h2>" + escape(snapshot.mission().outcome()) + "</h2><p>Ticks: "
                + snapshot.tick() + " | Progress: " + String.format(java.util.Locale.ROOT, "%.1f", snapshot.mission().progressPercent())
                + "% | Grade: " + escape(snapshot.score().grade()) + "</p><table>" + resources
                + "</table><h3>Event timeline</h3><ul>" + timeline + "</ul>" + footer;
    }
    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
