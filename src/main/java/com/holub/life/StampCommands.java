package com.holub.life;

public class StampCommands {
	private SetStampCommand defStamp;
	private SetStampCommand glider;
	private SetStampCommand toad;
	private SetStampCommand block;
	private SetStampCommand blink;
	private SetStampCommand LWSS;
	private String currName;
	
	StampCommands(LifeMediator mediator){
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
	
	
}
