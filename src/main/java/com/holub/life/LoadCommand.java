package com.holub.life;

import java.io.File;
import javax.swing.JOptionPane;
import com.holub.io.Files;

public class LoadCommand implements Command {
    private final LifeMediator mediator;

    public LoadCommand(LifeMediator mediator) {
        this.mediator = mediator;
    }

    /** Only reads the file; stopping the clock and resetting Undo is the Mediator's job. */
    @Override
    public void execute() {
        try {
            File file = Files.userSelected(".", "", "All Files (Please type extension: .life, .cells, or .rle)", "Load");
            if (file == null) return;
            
            Storable memento = FileFormat.forFileName(file.getName()).load(file);
            mediator.load(memento);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Load Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}