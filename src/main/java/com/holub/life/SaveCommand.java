package com.holub.life;

import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;
import com.holub.io.Files;

public class SaveCommand implements Command {
    private final LifeMediator mediator;

    public SaveCommand(LifeMediator mediator) { 
        this.mediator = mediator; 
    }

    @Override
    public void execute() {
        File file;
        try {
            file = Files.userSelected(".", null, "Save Game (.life, .cells, .rle)", "Write");
        } catch (FileNotFoundException cancelled) {
            return;
        }

        try {
            mediator.store(FileFormat.forFileName(file.getName()), file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Save Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}