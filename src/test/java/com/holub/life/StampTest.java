package com.holub.life;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StampTest {
    private UniverseModel board;
    private LifeFacade facade;

    private static final class FixedStamp extends Stamp {
        private final Pattern fixedPattern;
        private int factoryCalls;

        FixedStamp(LifeFacade facade, Pattern fixedPattern) {
            this.facade = facade; 
            this.fixedPattern = fixedPattern;
        }

        @Override
        public Pattern createPattern() {
            factoryCalls++;
            return fixedPattern;
        }
    }

    @BeforeEach
    public void setUp() {
        HistoryManager.clearHistory();
        HistoryManager.setIgnoreSave(false);
        facade = new UniverseBuilder().build();
        board = facade.model();
    }

    @Test //논리적 패턴 크기, 생존 좌표 순회여부 확인
    public void patternDimensionsAndIterator() {
        Pattern pattern = glider();
        assertEquals(3, pattern.width());
        assertEquals(3, pattern.height());

        List<Point> cells = new ArrayList<>();
        Iterator<Point> iterator = pattern.getIterator();
        while (iterator.hasNext()) {
            cells.add(iterator.next());
        }
        assertEquals(5, cells.size());
        assertTrue(cells.contains(new Point(1, 0)));
        assertTrue(cells.contains(new Point(2, 2)));
    }

    @Test //임의좌표 glider -> cell 활성화여부 확인
    public void gliderIsPlacedAroundClickCoordinate() {
        FixedStamp stamp = new FixedStamp(facade, glider());
        stamp.stamp(board, 10, 10);

        assertEquals(1, stamp.factoryCalls);
        assertTrue(board.isAlive(10, 9));
        assertTrue(board.isAlive(11, 10));
        assertTrue(board.isAlive(9, 11));
        assertTrue(board.isAlive(10, 11));
        assertTrue(board.isAlive(11, 11));
        assertFalse(board.isAlive(9, 9));
        assertEquals(5, liveCount());
    }

    @Test // stamp가 board를 넘어갈경우 clipping 여부 확인
    public void outOfBoundsCellsAreClipped() {
        FixedStamp stamp = new FixedStamp(facade, glider());
        stamp.stamp(board, 0, 0);

        assertTrue(board.isAlive(1, 0));
        assertTrue(board.isAlive(0, 1));
        assertTrue(board.isAlive(1, 1));
        assertEquals(3, liveCount());
    }

    @Test //살아있는 Cell의 Stamp시 기존 cell 유지여부 확인
    public void stampingDoesNotClearUnrelatedExistingCells() {
        facade.setAlive(20, 20, true);
        FixedStamp stamp = new FixedStamp(facade, glider());
        stamp.stamp(board, 10, 10);

        assertTrue(board.isAlive(20, 20));
        assertEquals(6, liveCount());
    }

    @Test //stamp() 2회 호출 - createPattern() 2회 호출 및 배치 확인
    public void factoryMethodRunsOnEachStampInvocation() {
        FixedStamp stamp = new FixedStamp(facade, glider());
        stamp.stamp(board, 10, 10);
        stamp.stamp(board, 20, 20);

        assertEquals(2, stamp.factoryCalls);
        assertEquals(10, liveCount());
    }

    private static Pattern glider() {
        return new Pattern(List.of(
                new Point(1, 0),
                new Point(2, 1),
                new Point(0, 2),
                new Point(1, 2),
                new Point(2, 2)));
    }

    private int liveCount() {
        final int[] count = {0};
        board.visitCells((x, y, alive) -> {
            if (alive) count[0]++;
        });
        return count[0];
    }

    

	private Set<Point> livingCells() {
	    Set<Point> cells = new HashSet<>();
	
	    board.visitCells((x, y, alive) -> {
	        if (alive) {
	            cells.add(new Point(x, y));
	        }
	    });
	
	    return cells;
	}

    
	@Test // Glider를 배치하고 4세대, 8세대 진행했을 때 동일한 형태로 대각선 한 칸씩 이동하는지 확인
	public void gliderMovesEveryFourGenerations() {
	    FixedStamp stamp = new FixedStamp(facade, glider());
	
	    stamp.stamp(board, 10, 10);
	
	    assertEquals(Set.of(
	            new Point(10, 9),
	            new Point(11, 10),
	            new Point(9, 11),
	            new Point(10, 11),
	            new Point(11, 11)
	    ), livingCells());
	
	    for (int i = 0; i < 4; i++) {
	        facade.tick();
	    }
	
	    assertEquals(4L, board.generation());
	    assertEquals(Set.of(
	            new Point(11, 10),
	            new Point(12, 11),
	            new Point(10, 12),
	            new Point(11, 12),
	            new Point(12, 12)
	    ), livingCells());
	
	    for (int i = 0; i < 4; i++) {
	        facade.tick();
	    }
	
	    assertEquals(8L, board.generation());
	    assertEquals(Set.of(
	            new Point(12, 11),
	            new Point(13, 12),
	            new Point(11, 13),
	            new Point(12, 13),
	            new Point(13, 13)
	    ), livingCells());
	}

}
