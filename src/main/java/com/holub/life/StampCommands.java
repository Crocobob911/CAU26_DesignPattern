package com.holub.life;

import com.holub.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;

public class StampCommands {
	/** Name shown in the Stamp menu while a pattern loaded from a file is the current stamp. */
	static final String FILE = "From File...";

	private final LifeMediator mediator;
	private SetStampCommand defStamp;
	private SetStampCommand glider;
	private SetStampCommand toad;
	private SetStampCommand block;
	private SetStampCommand blink;
	private SetStampCommand LWSS;
	private String currName;
	
	StampCommands(LifeMediator mediator){
		this.mediator = mediator;
		Stamp init = new StampDefault(mediator); 
		Stamp.setCurr(init);
		currName = "default";
		defStamp = new SetStampCommand(init);
		glider = new SetStampCommand(new StampGlider(mediator));
		toad = new SetStampCommand(new StampToad(mediator));
		block = new SetStampCommand(new StampBlock(mediator));
		blink = new SetStampCommand(new StampBlink(mediator));
		LWSS = new SetStampCommand(new StampLWSS(mediator));
	}
	
	public SetStampCommand setStamp(String stampName) {
		currName = stampName;
		if (stampName.equals("glider"))return glider;
		if (stampName.equals("toad")) return toad;
		if (stampName.equals("block")) return block;
		if (stampName.equals("blink")) return blink;
		if (stampName.equals("LWSS")) return LWSS;
		
		currName = "default";
		return defStamp;
	}
	public String currStr() {
		return currName;
	}
	
	public StampingCommand stampAct(UniverseModel model, int x, int y) {
		return new StampingCommand(model, Stamp.getCurr(), x, y);
	}

	/** Stamp > From File...: a .cells or .rle pattern read by the E7 formats becomes the stamp. */
	public Command loadFileStamp() {
		return () -> {
			File file;
			try {
				file = Files.userSelected(".", null, "Pattern Files (.cells, .rle)", "Use as Stamp");
			} catch (FileNotFoundException cancelled) {
				return;
			}
			try {
				useFileStamp(FileFormat.forFileName(file.getName()).load(file)).execute();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Stamp Load Failed: " + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		};
	}

	SetStampCommand useFileStamp(Storable loaded) {
		if (!(loaded instanceof TextToMementoAdapter pattern))
			throw new IllegalArgumentException("스탬프로 쓸 수 있는 파일은 .cells, .rle 입니다.");
		currName = FILE;
		return new SetStampCommand(new StampFile(mediator, pattern));
	}
	
	
}
