package com.holub.life;

import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * E3 status bar. Shows what {@link BoardStatistic} and {@link BoardCycleDetector} have already
 * computed; it never counts cells or detects cycles itself.
 */
final class StatsPanel extends JPanel {
    private final BoardStatistic statistic;
    private final BoardCycleDetector detector;
    private final JLabel generation = new JLabel();
    private final JLabel alive = new JLabel();
    private final JLabel status = new JLabel();

    StatsPanel(LifeFacade facade, BoardStatistic statistic, BoardCycleDetector detector) {
        super(new FlowLayout(FlowLayout.LEFT, 16, 4));
        this.statistic = Objects.requireNonNull(statistic);
        this.detector = Objects.requireNonNull(detector);
        add(generation);
        add(alive);
        add(status);
        refresh();
        // Events can arrive from the clock thread, so labels are only touched on the EDT.
        facade.addListener(event -> SwingUtilities.invokeLater(this::refresh));
    }

    private void refresh() {
        generation.setText("세대: " + statistic.getGeneration());
        alive.setText("살아 있는 셀: " + statistic.getAlive());
        status.setText("상태: " + statusText(detector.getCycle()));
    }

    /** Maps {@link BoardCycleDetector#getCycle()} (-1 = none, 1 = still life, N = period) to a label. */
    static String statusText(int cycle) {
        if (cycle < 0)
            return "변화 중";
        if (cycle == 1)
            return "정지 상태";
        return "주기 " + cycle + " 반복";
    }
}
