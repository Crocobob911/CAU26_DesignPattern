package com.holub.life;

import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

public class StampGlider extends Stamp {

	StampGlider(LifeMediator Mediator){
		facade = Mediator.facade();
	}
	@Override
	public Pattern createPattern() {
		return new Pattern(List.of(
				new Point(0, 1),
				new Point(1, 2),
				new Point(2, 0),
				new Point(2, 1),
				new Point(2, 2)
			));
	}
}

