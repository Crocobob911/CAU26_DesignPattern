package com.holub.life;

import java.io.File;
import javax.swing.JOptionPane;
import com.holub.io.Files;

public class SaveCommand implements Command {
    private final UniverseModel model;

    public SaveCommand(UniverseModel model) { 
        this.model = model; 
    }

    @Override
    public void execute() {
        try {
            File file = Files.userSelected(".", null, "Save Game (.life, .cells)", "Write");
            if (file == null) return; 

            FileFormat format = getStrategy(file.getName());
            format.save(file, model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Save Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private FileFormat getStrategy(String fileName) {
        if (fileName.endsWith(".life"))  return new LifeFormat();
        if (fileName.endsWith(".cells")) return new PlaintextFormat();
        if (fileName.endsWith(".rle"))   return new RleFormat(); 
        
        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + fileName);
    }
}