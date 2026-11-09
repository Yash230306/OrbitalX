package com.orbitalix.gui.kit;

import com.orbitalix.gui.theme.Theme;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.Consumer;

public final class SegmentedControl extends JPanel {
    private final List<JToggleButton> buttons;

    public SegmentedControl(List<String> labels, int selected, Theme theme, Consumer<String> onSelect) {
        super(new FlowLayout(FlowLayout.LEFT, 2, 2));
        if (labels.isEmpty() || selected < 0 || selected >= labels.size()) throw new IllegalArgumentException("Invalid segment selection");
        setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        buttons = labels.stream().map(label -> {
            JToggleButton button = new JToggleButton(label);
            button.setForeground(theme.foreground());
            button.setBackground(theme.panelRaised());
            button.setFocusPainted(false);
            button.setSelected(buttonsSelectedIndex(labels, label) == selected);
            button.addActionListener(event -> onSelect.accept(label));
            group.add(button);
            add(button);
            return button;
        }).toList();
    }

    private static int buttonsSelectedIndex(List<String> labels, String label) { return labels.indexOf(label); }
    public String selected() { return buttons.stream().filter(JToggleButton::isSelected).map(JToggleButton::getText).findFirst().orElseThrow(); }
}
