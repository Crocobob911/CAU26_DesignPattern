package com.holub.life;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 기존 동작 고정 테스트 (characterization test).
 *
 * 리팩터링 전 원본 Holub 코드의 동작을 그대로 기록한다.
 * 리팩터링 후에도 이 테스트가 모두 통과해야 "기존 기능이 그대로 동작한다"고 말할 수 있다.
 *
 * - 원본 Universe와 같은 구조(8x8 Neighborhood 안에 8x8 Neighborhood = 64x64)를 사용한다.
 * - Universe/Clock(Swing)은 쓰지 않고, Universe가 하는 일(클릭, tick, Clear, Load/Store)을
 *   모델 객체에 직접 호출해서 재현한다.
 * - 좌표는 Point(x = 열, y = 행).
 *
 * 리팩터링으로 API가 바뀌면 아래 "보드 조작 헬퍼" 부분만 새 API에 맞게 고치고,
 * 각 테스트의 기대값(패턴 좌표)은 절대 바꾸지 말 것.
 */
class OriginalBehaviorTest
{
	private static final int GRID  = 8;					// Neighborhood 한 변의 칸 수
	private static final int WIDTH = GRID * GRID;		// 전체 보드 한 변 = 64

	// ------------------------------------------------------------------
	// 보드 조작 헬퍼 (리팩터링 시 이 부분만 수정)
	// ------------------------------------------------------------------

	/** 원본 Universe 생성자와 같은 2단 중첩 구조 */
	private static Cell newUniverse()
	{	return new Neighborhood( GRID, new Neighborhood( GRID, new Resident() ) );
	}

	/** Universe.mousePressed와 동일: 1칸 = 1픽셀인 64x64 표면을 클릭 */
	private static void click( Cell board, int x, int y )
	{	board.userClicked( new Point(x, y), new Rectangle(0, 0, WIDTH, WIDTH) );
	}

	private static void place( Cell board, Set<Point> cells )
	{	for( Point p : cells )
			click( board, p.x, p.y );
	}

	/** Universe의 Clock 리스너와 동일한 한 세대 진행 */
	private static void tick( Cell board )
	{	Cell d = Cell.DUMMY;
		if( board.figureNextState( d, d, d, d, d, d, d, d ) )
			board.transition();
	}

	private static void tick( Cell board, int generations )
	{	for( int i = 0; i < generations; ++i )
			tick( board );
	}

	/** 현재 살아 있는 셀의 좌표 집합 */
	private static Set<Point> liveCells( Cell board )
	{	Cell.Memento snapshot = (Cell.Memento) board.createMemento();
		Set<Point> live = new HashSet<>();
		for( int y = 0; y < WIDTH; ++y )
			for( int x = 0; x < WIDTH; ++x )
				if( snapshot.isAlive( new Point(x, y) ) )
					live.add( new Point(x, y) );
		return live;
	}

	/** Universe.doStore와 동일: 메멘토를 만들어 스트림에 기록 */
	private static byte[] store( Cell board ) throws IOException
	{	Storable memento = board.createMemento();
		board.transfer( memento, new Point(0, 0), Cell.STORE );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		memento.flush( out );
		return out.toByteArray();
	}

	/** Universe.doLoad와 동일: 보드를 비우고 스트림에서 읽어 반영 */
	private static void load( Cell board, byte[] data ) throws IOException
	{	board.clear();
		Storable memento = board.createMemento();
		memento.load( new ByteArrayInputStream(data) );
		board.transfer( memento, new Point(0, 0), Cell.LOAD );
	}

	// ------------------------------------------------------------------
	// 패턴 유틸
	// ------------------------------------------------------------------

	private static Set<Point> cells( int... xy )
	{	Set<Point> s = new HashSet<>();
		for( int i = 0; i < xy.length; i += 2 )
			s.add( new Point( xy[i], xy[i+1] ) );
		return s;
	}

	private static Set<Point> shift( Set<Point> pattern, int dx, int dy )
	{	Set<Point> s = new HashSet<>();
		for( Point p : pattern )
			s.add( new Point( p.x + dx, p.y + dy ) );
		return s;
	}

	/** 남동쪽으로 이동하는 글라이더 (4세대마다 (+1, +1) 이동) */
	private static Set<Point> gliderAt( int x, int y )
	{	return shift( cells( 1,0,  2,1,  0,2, 1,2, 2,2 ), x, y );
	}

	// ------------------------------------------------------------------
	// 테스트
	// ------------------------------------------------------------------

	@Test
	@DisplayName("클릭하면 셀이 살아나고, 같은 셀을 다시 클릭하면 죽는다")
	void clickTogglesCell()
	{	Cell board = newUniverse();
		click( board, 10, 20 );
		assertEquals( cells(10,20), liveCells(board) );

		click( board, 10, 20 );
		assertEquals( cells(), liveCells(board) );
	}

	/** 가운데 셀 주변 8칸 (이 순서대로 n개를 살려 이웃 수를 맞춘다) */
	private static final int[][] AROUND =
		{ {-1,-1}, {0,-1}, {1,-1}, {-1,0}, {1,0}, {-1,1}, {0,1}, {1,1} };

	@Test
	@DisplayName("Conway 규칙 B3/S23: 이웃 수 0~8 전체에 대해 다음 세대 가운데 셀 상태가 맞다")
	void conwayRuleTable()
	{	final int cx = 20, cy = 20;
		for( int n = 0; n <= 8; ++n )
			for( boolean centerAlive : new boolean[]{ false, true } )
			{	Cell board = newUniverse();
				if( centerAlive )
					click( board, cx, cy );
				for( int i = 0; i < n; ++i )
					click( board, cx + AROUND[i][0], cy + AROUND[i][1] );

				tick( board );

				boolean expected = centerAlive ? (n == 2 || n == 3) : (n == 3);
				assertEquals( expected, liveCells(board).contains( new Point(cx, cy) ),
							  "가운데 셀이 " + (centerAlive ? "산" : "죽은") +
							  " 상태, 이웃 " + n + "개" );
			}
	}

	@Test
	@DisplayName("블록(정물)은 Neighborhood 경계에 걸쳐 있어도 변하지 않는다")
	void blockStillLifeAcrossBoundary()
	{	Cell board = newUniverse();
		Set<Point> block = cells( 7,7, 8,7, 7,8, 8,8 );	// 4개의 하위 Neighborhood에 걸침
		place( board, block );
		tick( board, 5 );
		assertEquals( block, liveCells(board) );
	}

	@Test
	@DisplayName("블링커는 Neighborhood 경계에서도 주기 2로 진동한다")
	void blinkerAcrossBoundary()
	{	Cell board = newUniverse();
		Set<Point> horizontal = cells( 7,20, 8,20, 9,20 );	// x=7|8 경계에 걸침
		Set<Point> vertical   = cells( 8,19, 8,20, 8,21 );
		place( board, horizontal );

		tick( board );
		assertEquals( vertical, liveCells(board) );
		tick( board );
		assertEquals( horizontal, liveCells(board) );
	}

	@Test
	@DisplayName("글라이더는 Neighborhood 경계를 넘어 4세대마다 대각선으로 1칸 이동한다")
	void gliderCrossesNeighborhoodBoundaries()
	{	Cell board = newUniverse();
		place( board, gliderAt(4, 4) );

		// 4세대씩 진행하며 확인: (5,5) → ... → (12,12)로 하위 Neighborhood 경계(8)를 통과
		for( int k = 1; k <= 8; ++k )
		{	tick( board, 4 );
			assertEquals( gliderAt(4 + k, 4 + k), liveCells(board),
						  "글라이더 위치가 다름: " + (4*k) + "세대 후" );
		}
	}

	@Test
	@DisplayName("보드 바깥은 항상 죽은 셀로 취급된다 (반대편으로 이어지지 않음)")
	void outsideOfUniverseIsDead()
	{	Cell board = newUniverse();
		place( board, cells( 3,0, 4,0, 5,0 ) );	// 맨 윗줄 블링커

		tick( board );
		assertEquals( cells( 4,0, 4,1 ), liveCells(board),
					  "위쪽 셀(4,-1)이 생기지 않아야 하고, 맨 아랫줄로 넘어가지도 않아야 함" );
		tick( board );
		assertEquals( cells(), liveCells(board) );
	}

	@Test
	@DisplayName("Clear하면 모든 셀이 죽고, 이후 세대가 지나도 비어 있다")
	void clearKillsEverything()
	{	Cell board = newUniverse();
		place( board, gliderAt(20, 20) );
		place( board, cells( 7,7, 8,7, 7,8, 8,8 ) );
		tick( board, 3 );

		board.clear();
		assertEquals( cells(), liveCells(board) );
		tick( board, 3 );
		assertEquals( cells(), liveCells(board) );
	}

	@Test
	@DisplayName("Store 후 Load하면 같은 패턴이 복원되고, 복원된 패턴도 정상적으로 진화한다")
	void storeThenLoadRoundTrip() throws IOException
	{	Cell original = newUniverse();
		Set<Point> pattern = new HashSet<>( gliderAt(30, 30) );
		pattern.addAll( cells( 7,20, 8,20, 9,20 ) );			// 블링커
		place( original, pattern );

		byte[] saved = store( original );
		assertTrue( saved.length > 0 );

		Cell restored = newUniverse();
		place( restored, cells( 50,50, 51,50 ) );			// Load 전에 있던 셀은 지워져야 함
		load( restored, saved );
		assertEquals( pattern, liveCells(restored) );

		// 복원된 보드와 원래 보드가 같은 방식으로 진화하는지
		tick( original, 8 );
		tick( restored, 8 );
		assertEquals( liveCells(original), liveCells(restored) );
	}
}
