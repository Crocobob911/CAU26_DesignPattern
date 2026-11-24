package com.holub.life;

public class StampingCommand implements Command {

	private final UniverseModel model;
	private final Stamp stamp;
	private final int x, y;
	
	StampingCommand(UniverseModel _model, Stamp _stamp,int _x,int _y){
		model = _model;
		stamp = _stamp;
		x = _x;
		y = _y;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		stamp.stamp(model, x, y);
	}
}
