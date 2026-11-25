package com.holub.life;

public class StampFile extends Stamp {

	private TextToMementoAdapter loadedFile;
	
	StampFile(LifeMediator Mediator, TextToMementoAdapter loaded){
		facade = Mediator.facade();
		loadedFile = loaded;
	}
	
	@Override
	public Pattern createPattern() {
		return new Pattern(loadedFile.pointList());
	}

}
