package com.holub.life;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.awt.Rectangle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JUnit 환경이 정상 동작하는지, 그리고 Life 모델(Neighborhood/Resident)을
 * Swing 화면(Universe, Clock) 없이 단독으로 돌릴 수 있는지 확인하는 스모크 테스트.
 * 좌표는 (x = 열, y = 행).
 */
class LifeSmokeTest
{
	private static final int GRID = 8;

	/** 8x8 Neighborhood 하나로 이루어진 작은 보드 */
	private static Cell newBoard()
	{	return new Neighborhood( GRID, new Resident() );
	}

	/** 화면 클릭 대신, 1셀 = 1픽셀인 가상 표면을 클릭해 셀을 토글한다 */
	private static void toggle( Cell board, int column, int row )
	{	board.userClicked( new Point(column, row), new Rectangle(0, 0, GRID, GRID) );
	}

	/** Universe가 Clock tick마다 하는 일과 동일: 다음 상태 계산 후 전이 */
	private static void step( Cell board )
	{	Cell d = Cell.DUMMY;
		if( board.figureNextState( d, d, d, d, d, d, d, d ) )
			board.transition();
	}

	private static boolean isAlive( Cell board, int column, int row )
	{	Cell.Memento snapshot = (Cell.Memento) board.createMemento();
		return snapshot.isAlive( new Point(column, row) );
	}

	@Test
	@DisplayName("빈 보드는 한 세대가 지나도 비어 있다")
	void emptyBoardStaysEmpty()
	{	Cell board = newBoard();
		step( board );
		for( int row = 0; row < GRID; ++row )
			for( int column = 0; column < GRID; ++column )
				assertFalse( isAlive(board, column, row) );
	}

	@Test
	@DisplayName("블링커는 가로 → 세로 → 가로로 주기 2 진동한다")
	void blinkerOscillatesWithPeriodTwo()
	{	Cell board = newBoard();
		toggle( board, 2, 3 );
		toggle( board, 3, 3 );
		toggle( board, 4, 3 );

		step( board );		// 세로
		assertTrue ( isAlive(board, 3, 2) );
		assertTrue ( isAlive(board, 3, 3) );
		assertTrue ( isAlive(board, 3, 4) );
		assertFalse( isAlive(board, 2, 3) );
		assertFalse( isAlive(board, 4, 3) );

		step( board );		// 다시 가로
		assertTrue ( isAlive(board, 2, 3) );
		assertTrue ( isAlive(board, 3, 3) );
		assertTrue ( isAlive(board, 4, 3) );
		assertFalse( isAlive(board, 3, 2) );
		assertFalse( isAlive(board, 3, 4) );
	}
}
