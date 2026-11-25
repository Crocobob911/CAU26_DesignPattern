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
            File file = Files.userSelected(".", "", "Save Game (Please type extension: .life, .cells, or .rle)", "Write");
            if (file == null) return; 

            FileFormat format = getStrategy(file.getName());
            format.save(file, model);

        try {
            mediator.store(FileFormat.forFileName(file.getName()), file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Save Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}