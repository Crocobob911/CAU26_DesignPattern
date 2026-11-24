package com.holub.life;

public class StampCommands {
	private final SetStampCommand defStamp;
	private final SetStampCommand glider;
	private final SetStampCommand block;
	private final SetStampCommand blink;
	private final SetStampCommand LWSS;
	
	StampCommands(LifeMediator mediator){
		Stamp init = new StampDefault(mediator); 
		Stamp.setCurr(init);
		defStamp = new SetStampCommand(init);
		glider = new SetStampCommand(new StampGlider(mediator));
		block = new SetStampCommand(new StampBlock(mediator));
		blink = new SetStampCommand(new StampBlink(mediator));
		LWSS = new SetStampCommand(new StampLWSS(mediator));
	}
	
	public SetStampCommand setStamp(String stampName) {
		if (stampName.equals("glider"))return glider;
		if (stampName.equals("block")) return block;
		if(stampName.equals("blink")) return blink;
		if (stampName.equals("LWSS")) return LWSS;
		return defStamp;
	}
	
	public StampingCommand stampAct(UniverseModel model, int x, int y) {
		return new StampingCommand(model, Stamp.getCurr(), x, y);
	}
	
	
}
