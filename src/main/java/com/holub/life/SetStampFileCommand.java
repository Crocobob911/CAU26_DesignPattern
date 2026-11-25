package com.holub.life;

import java.io.File;

import javax.swing.JOptionPane;

import com.holub.io.Files;

public class SetStampFileCommand implements Command {
	private final LifeMediator mediator;
	private boolean successed;
	SetStampFileCommand(LifeMediator _mediator){
		mediator = _mediator;
	}
	
	public boolean isSuccessed() {
		return successed;
	}
	
	@Override
	public void execute() {
        try {
            File file = Files.userSelected(".", null, "Life Game Files (.life, .cells, .rle)", "Load");
            if (file == null) return;

            FileFormat strategy = getStrategy(file.getName());
            Storable memento = strategy.load(file);

            Stamp.setCurr(new StampFile(mediator, (TextToMementoAdapter)memento));
            successed = true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Load Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            successed = false;
        }
    }

    private FileFormat getStrategy(String fileName) {
        if (fileName.endsWith(".life"))  return new LifeFormat();
        if (fileName.endsWith(".cells")) return new PlaintextFormat();
        if (fileName.endsWith(".rle"))   return new RleFormat();
        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + fileName);
    }
}
