package com.holub.life;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JUnit 환경이 정상 동작하는지, 그리고 Life 모델(LifeFacade)을
 * Swing 화면(UniversePanel, Clock) 없이 단독으로 돌릴 수 있는지 확인하는 스모크 테스트.
 * 좌표는 (x = 열, y = 행).
 */
class LifeSmokeTest
{
	private static final int GRID = 8;

	private static LifeFacade newBoard()
	{	return new UniverseBuilder().blockSize(GRID).levels(1).build();
	}

	@Test
	@DisplayName("빈 보드는 한 세대가 지나도 비어 있다")
	void emptyBoardStaysEmpty()
	{	LifeFacade board = newBoard();
		board.tick();
		for( int row = 0; row < GRID; ++row )
			for( int column = 0; column < GRID; ++column )
				assertFalse( board.isAlive(column, row) );
	}

	@Test
	@DisplayName("블링커는 가로 → 세로 → 가로로 주기 2 진동한다")
	void blinkerOscillatesWithPeriodTwo()
	{	LifeFacade board = newBoard();
		board.toggle(2, 3 );
		board.toggle(3, 3 );
		board.toggle(4, 3 );

		board.tick();		// 세로
		assertTrue ( board.isAlive(3, 2) );
		assertTrue ( board.isAlive(3, 3) );
		assertTrue ( board.isAlive(3, 4) );
		assertFalse( board.isAlive(2, 3) );
		assertFalse( board.isAlive(4, 3) );

		board.tick();		// 다시 가로
		assertTrue ( board.isAlive(2, 3) );
		assertTrue ( board.isAlive(3, 3) );
		assertTrue ( board.isAlive(4, 3) );
		assertFalse( board.isAlive(3, 2) );
		assertFalse( board.isAlive(3, 4) );
	}
}
