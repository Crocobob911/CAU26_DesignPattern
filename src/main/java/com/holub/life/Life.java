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

        UniverseModel model = new UniverseBuilder().buildModel(LifeRule.CONWAY);
        LifeMediator mediator = new LifeMediator(new LifeFacade(model), () -> Clock.instance().stop());
        FileCommands fileCommands = new FileCommands(model);
        ClockCommands clockCommands = new ClockCommands(model);
        GridCommands gridCommands = new GridCommands(model);
        RuleCommands ruleCommands = new RuleCommands(model);
        StampCommands stampCommands = new StampCommands(mediator);
        UniversePanel panel = new UniversePanel(mediator, fileCommands, clockCommands, gridCommands, stampCommands, ruleCommands);
        panel.installMenus();
        Clock.instance().addClockListener(mediator::tick);
        StatsPanel stats = new StatsPanel(mediator.facade(),
                new BoardStatistic(mediator), new BoardCycleDetector(mediator));

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        Dimension board = panel.getPreferredSize();
        var screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        scroll.setPreferredSize(new Dimension(Math.min(board.width, screen.width - 48),
                Math.min(board.height, screen.height - 96)));
        add(scroll, BorderLayout.CENTER);
        add(stats, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
}
