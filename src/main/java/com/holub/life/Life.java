package com.holub.life;

import com.holub.ui.MenuSite;
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

        LifeMediator mediator = new LifeMediator(new UniverseBuilder().build(), () -> Clock.instance().stop());
        UniversePanel panel = new UniversePanel(mediator);
        panel.installMenus();
        Clock.instance().addClockListener(mediator::tick);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        Dimension board = panel.getPreferredSize();
        var screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        scroll.setPreferredSize(new Dimension(Math.min(board.width, screen.width - 48),
                Math.min(board.height, screen.height - 96)));
        add(scroll);
        pack();
        setVisible(true);
    }
}
