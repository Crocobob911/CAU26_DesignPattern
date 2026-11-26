package com.holub.life;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** E3 statistics and cycle detection driven through the facade, without any Swing window. */
@Tag("integration")
class StatsIntegrationTest {
    private LifeFacade facade;
    private LifeMediator mediator;
    private BoardStatistic statistic;
    private BoardCycleDetector detector;

    @BeforeEach
    void setUp() {
        HistoryManager.clearHistory();
        HistoryManager.setIgnoreSave(false);
        facade = new UniverseBuilder().build();
        mediator = new LifeMediator(facade, () -> { });
        statistic = new BoardStatistic(mediator);
        detector = new BoardCycleDetector(mediator);
    }

    @Test
    void blockIsStill() {
        set(2, 2, 3, 2, 2, 3, 3, 3);
        facade.tick();

        assertEquals(1, detector.getCycle());
        assertEquals("정지 상태", StatsPanel.statusText(detector.getCycle()));
        assertEquals(4, statistic.getAlive());
    }

    @Test
    void blinkerHasPeriodTwo() {
        set(9, 10, 10, 10, 11, 10);
        facade.tick();
        assertEquals("변화 중", StatsPanel.statusText(detector.getCycle()));
        facade.tick();

        assertEquals(2, detector.getCycle());
        assertEquals("주기 2 반복", StatsPanel.statusText(detector.getCycle()));
    }

    @Test
    void movingGliderHasNoCycle() {
        set(1, 0, 2, 1, 0, 2, 1, 2, 2, 2);
        for (int generation = 1; generation <= 40; generation++) {
            facade.tick();
            assertEquals(-1, detector.getCycle(), "generation " + generation);
        }

        assertEquals("변화 중", StatsPanel.statusText(detector.getCycle()));
        assertEquals(40L, statistic.getGeneration());
        assertEquals(5, statistic.getAlive());
    }

    @Test
    void clearResetsStatisticsAndCycleHistory() {
        set(9, 10, 10, 10, 11, 10);
        facade.tick();
        facade.tick();
        assertEquals(2, detector.getCycle());
        assertEquals(2L, statistic.getGeneration());

        mediator.clear();

        assertEquals(0L, statistic.getGeneration());
        assertEquals(0, statistic.getAlive());
        assertEquals(-1, detector.getCycle());
    }

    private void set(int... xy) {
        for (int i = 0; i < xy.length; i += 2)
            facade.setAlive(xy[i], xy[i + 1], true);
    }
}
