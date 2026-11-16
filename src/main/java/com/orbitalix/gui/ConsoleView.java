package com.orbitalix.gui;

import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.core.View;
import com.orbitalix.gui.theme.Theme;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/** Interactive onboard terminal with command recall, completion, and typed output. */
public final class ConsoleView extends View {
    private final JTextPane output = new JTextPane();
    private final JTextField input = new JTextField();
    private final List<String> history = new ArrayList<>();
    private int historyIndex;

    public ConsoleView(SimulationController controller, Theme theme) {
        super(controller, theme);
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(18, 24, 20, 24));
        JLabel heading = new JLabel("ONBOARD COMMAND TERMINAL");
        heading.setForeground(theme.foreground()); heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(heading, BorderLayout.NORTH);
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        output.setBackground(theme.panel()); output.setForeground(theme.nominal());
        output.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createLineBorder(theme.grid()));
        add(scroll, BorderLayout.CENTER);
        input.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        input.setBackground(theme.panelRaised()); input.setForeground(theme.foreground());
        input.setCaretColor(theme.accent());
        input.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(theme.accent()),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        input.setToolTipText("Enter command; Up/Down recalls, Tab completes");
        input.setFocusTraversalKeysEnabled(false);
        input.addActionListener(event -> submit());
        input.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_UP) recall(-1);
                else if (event.getKeyCode() == KeyEvent.VK_DOWN) recall(1);
                else if (event.getKeyCode() == KeyEvent.VK_TAB) complete();
            }
        });
        add(input, BorderLayout.SOUTH);
        append("ORBITALX FLIGHT COMPUTER / CORE LINK ESTABLISHED", theme.accent());
        append("Type help to list commands.", theme.muted());
    }

    public JTextField inputField() { return input; }
    public String consoleText() { return output.getText(); }

    @Override protected void paintView(Graphics2D g) {
        g.setColor(theme.background()); g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void submit() {
        String command = input.getText().trim();
        if (command.isEmpty()) return;
        history.add(command); historyIndex = history.size();
        append("> " + command, theme.accent());
        try {
            String result = controller.executeCommand(command);
            append(result.isBlank() ? "Command accepted." : result, theme.foreground());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            append("ERROR: " + ex.getMessage(), theme.critical());
        }
        input.setText("");
    }

    private void recall(int direction) {
        if (history.isEmpty()) return;
        historyIndex = Math.max(0, Math.min(history.size(), historyIndex + direction));
        input.setText(historyIndex == history.size() ? "" : history.get(historyIndex));
    }

    private void complete() {
        String prefix = input.getText().trim();
        if (prefix.isEmpty() || prefix.contains(" ")) return;
        List<String> matches = List.of("help", "status", "throttle", "inject", "autonomy", "heal", "uplink",
                "exec", "dismiss", "limits", "crew", "task", "preview", "scenario", "seek", "report", "clear")
                .stream().filter(command -> command.startsWith(prefix.toLowerCase(java.util.Locale.ROOT))).toList();
        if (matches.size() == 1) input.setText(matches.get(0));
        else if (!matches.isEmpty()) append(String.join("  ", matches), theme.muted());
    }

    private void append(String text, Color color) {
        StyledDocument document = output.getStyledDocument();
        Style style = output.addStyle("line-" + document.getLength(), null);
        StyleConstants.setForeground(style, color);
        try { document.insertString(document.getLength(), text + System.lineSeparator(), style); }
        catch (BadLocationException ex) { throw new IllegalStateException("Could not append console output", ex); }
        output.setCaretPosition(document.getLength());
    }

}
