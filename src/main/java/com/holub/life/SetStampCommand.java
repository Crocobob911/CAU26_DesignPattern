package com.holub.life;

public class SetStampCommand implements Command {

	private final Stamp targetStamp;
	SetStampCommand(Stamp stamp){
		targetStamp = stamp;
	}
	
	@Override
	public void execute() {
		Stamp.setCurr(targetStamp);
	}

}
