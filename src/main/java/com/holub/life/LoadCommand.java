package com.holub.life;
import java.io.File;
import javax.swing.JOptionPane;
import com.holub.io.Files;

public class LoadCommand implements Command {
    private final UniverseModel model;

    public LoadCommand(UniverseModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        try {

            File file = Files.userSelected(".", null, "Life Game Files (.life, .cells, .rle)", "Load");
            if (file == null) return;

            FileFormat strategy = getStrategy(file.getName());
            Storable memento = strategy.load(file);

            Clock.instance().stop();
            HistoryManager.setIgnoreSave(false);
            HistoryManager.clearHistory();
            model.restore(memento, 0, LifeFacade.Change.LOAD);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Load Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private FileFormat getStrategy(String fileName) {
        if (fileName.endsWith(".life"))  return new LifeFormat();
        if (fileName.endsWith(".cells")) return new PlaintextFormat();
        if (fileName.endsWith(".rle"))   return new RleFormat();
        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + fileName);
    }
}
