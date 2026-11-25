package com.holub.life;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ObserverTest {
    private LifeFacade facade;
    private LifeMediator mediator;

    @BeforeEach 
    public void setUp() {
        HistoryManager.clearHistory();
        HistoryManager.setIgnoreSave(false);
        facade = new UniverseBuilder().build();
        mediator = createMediator(facade);
    }

    private LifeMediator createMediator(LifeFacade facade) {
        return new LifeMediator(facade, () -> Clock.instance().stop());
    }

    @Test //cell 수정시 BoardStatistics의 cell 수 갱신여부 확인
    public void statisticsReflectEdits() {
        BoardStatistic stat = new BoardStatistic(mediator);
        assertEquals(0, stat.getAlive());
        assertEquals(0L, stat.getGeneration());

        facade.setAlive(2, 2, true);
        facade.setAlive(3, 2, true);
        facade.setAlive(3, 2, true); // no actual change

        assertEquals(2, stat.getAlive());
        assertEquals(0L, stat.getGeneration());
    }

    @Test //block 배치 시 세대 증가 / cell 유지 확인
    public void statisticsReflectTickAndClear() {
        facade.setAlive(2, 2, true);
        facade.setAlive(2, 3, true);
        facade.setAlive(3, 2, true);
        facade.setAlive(3, 3, true); // stable 2x2 Block

        BoardStatistic stat = new BoardStatistic(mediator);
        assertEquals(4, stat.getAlive());

        facade.tick();
        assertEquals(1L, stat.getGeneration());
        assertEquals(4, stat.getAlive());

        facade.clear();
        assertEquals(0L, stat.getGeneration());
        assertEquals(0, stat.getAlive());
    }

    @Test //block : 주기 1 탐지 여부 확인 
    public void detectsPeriodOneBlock() {
        placeBlock();
        BoardCycleDetector detector = new BoardCycleDetector(mediator);
        assertEquals(-1, detector.getCycle());

        facade.tick();
        assertEquals(1, detector.getCycle());
    }

    @Test //blink : 주기 2 탐지 여부 확인 
    public void detectsPeriodTwoBlinker() {
        facade.setAlive(9, 10, true);
        facade.setAlive(10, 10, true);
        facade.setAlive(11, 10, true);
        BoardCycleDetector detector = new BoardCycleDetector(mediator);

        facade.tick();
        assertEquals(-1, detector.getCycle());
        facade.tick();
        assertEquals(2, detector.getCycle());
    }

    @Test //주기 탐지 후 임의 수정 시 주기 초기화 여부 확인
    public void editResetsDetectedCycle() {
        placeBlock();
        BoardCycleDetector detector = new BoardCycleDetector(mediator);

        facade.tick();
        assertEquals(1, detector.getCycle());

        facade.setAlive(15, 15, true);
        assertEquals(-1, detector.getCycle());
    }

    @Test //감지 기록 허용 범위 확인 (최대 10 ~ 최대 1000)
    public void maxHistoryLengthHasBoundaries() {
        BoardCycleDetector detector = new BoardCycleDetector(mediator);
        assertFalse(detector.setMaxLen(9));
        assertTrue(detector.setMaxLen(10));
        assertTrue(detector.setMaxLen(1000));
        assertFalse(detector.setMaxLen(1001));
    }

    private void placeBlock() {
        facade.setAlive(2, 2, true);
        facade.setAlive(3, 2, true);
        facade.setAlive(2, 3, true);
        facade.setAlive(3, 3, true);
    }
}
