package com.holub.life;

import com.holub.ui.MenuSite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/** Conway's Game of Life desktop application. */
public final class Life extends JFrame {
    public static void main(String[] arguments) {
        SwingUtilities.invokeLater(Life::new);
    }

    private Life() {
        super("The Game of Life. (c)2003 Allen I. Holub <http://www.holub.com>");
        MenuSite.establish(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        LifeWiring app = LifeWiring.create();
        LifeMediator mediator = app.mediator();
        ClockCommands clockCommands = app.clockCommands();
        UniversePanel panel = new UniversePanel(mediator, app.fileCommands(), clockCommands,
                app.gridCommands(), app.stampCommands(), app.ruleCommands());
        panel.installMenus();
        StatsPanel stats = new StatsPanel(mediator.facade(),
                new BoardStatistic(mediator), new BoardCycleDetector(mediator));

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        Dimension board = panel.getPreferredSize();
        var screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        scroll.setPreferredSize(new Dimension(Math.min(board.width, screen.width - 48),
                Math.min(board.height, screen.height - 96)));
        add(new ClockPanel(clockCommands), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(stats, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
}
