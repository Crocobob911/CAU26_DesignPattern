package com.holub.life;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

/** E4 controls: Run/Halt button, speed slider and the clock state. Every action goes through a Command. */
final class ClockPanel extends JPanel {
    private final ClockControl clock;
    private final JButton runHalt = new JButton();
    private final JSlider speed;
    private final JLabel state = new JLabel();

    ClockPanel(ClockCommands commands) {
        super(new FlowLayout(FlowLayout.LEFT, 8, 2));
        this.clock = commands.control();
        speed = new JSlider(ClockControl.MIN_DELAY, ClockControl.MAX_DELAY, clock.delay());
        speed.setInverted(true);   // right = shorter delay = faster

        runHalt.addActionListener(event ->
                (clock.isRunning() ? commands.getHalt() : commands.createRun(speed.getValue())).execute());
        speed.addChangeListener(event -> {
            if (speed.getValue() != clock.delay())
                commands.changeSpeed(speed.getValue()).execute();
        });

        add(runHalt);
        add(new JLabel("느림"));
        add(speed);
        add(new JLabel("빠름"));
        add(state);
        refresh();
        clock.addListener(() -> SwingUtilities.invokeLater(this::refresh));
    }

    private void refresh() {
        runHalt.setText(clock.isRunning() ? "정지" : "실행");
        if (speed.getValue() != clock.delay())
            speed.setValue(clock.delay());
        state.setText("상태: " + clock.stateLabel() + " (" + clock.delay() + "ms)");
    }
}
