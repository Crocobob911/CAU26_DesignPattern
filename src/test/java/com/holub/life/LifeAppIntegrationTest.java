package com.holub.life;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Integration tests: user scenarios run through the same objects the application wires together
 * ({@link LifeWiring}): menu Commands -> Mediator -> Facade -> Model, the singleton Clock, the E4 clock state,
 * Undo history, file formats and the Observers (statistics, cycle detection, cell ages).
 * Only the window and the file dialog are left out; the steps after the dialog are the same as
 * {@link LoadCommand} and {@link SaveCommand}.
 */
@Tag("integration")
class LifeAppIntegrationTest {
    private static final Set<Point> GLIDER_AT_ORIGIN = Set.of(
            new Point(1, 0), new Point(2, 1), new Point(0, 2), new Point(1, 2), new Point(2, 2));

    @TempDir
    Path dir;

    private LifeWiring app;
    private BoardStatistic statistic;
    private BoardCycleDetector detector;
    private CellAgeTracker ages;

    @BeforeEach
    void setUp() {
        Clock.instance().stop();
        Clock.instance().clearInstanceForTest();
        HistoryManager.clearHistory();
        HistoryManager.setIgnoreSave(false);

        app = LifeWiring.create();
        statistic = new BoardStatistic(app.mediator());
        detector = new BoardCycleDetector(app.mediator());
        ages = new CellAgeTracker(app.facade());
    }

    @AfterEach
    void tearDown() {
        Clock.instance().stop();
        HistoryManager.setIgnoreSave(false);
    }

    @Test
    @DisplayName("실행 중 .rle 파일을 Load하면 시계가 멈추고, Undo 기록·통계·셀 나이가 새로 시작되며, 불러온 패턴이 이어서 진화한다")
    void loadingWhileRunningHaltsClockAndStartsOver() throws Throwable {
        File file = dir.resolve("glider.rle").toFile();
        Files.writeString(file.toPath(), "#N Glider\nx = 3, y = 3, rule = B3/S23\nbo$2bo$3o!\n");

        onEventThread(() -> {
            app.facade().setAlive(30, 30, true);
            app.facade().setAlive(31, 30, true);
            app.facade().setAlive(32, 30, true);
            app.clockCommands().runFast().execute();
            app.clockCommands().getTick().execute();
            app.clockCommands().getTick().execute();
            assertTrue(app.clockCommands().control().isRunning());
            assertEquals(2L, statistic.getGeneration());

            loadThroughMenuPath(file);

            assertFalse(app.clockCommands().control().isRunning(), "Load는 실행 중인 시계를 멈춰야 함");
            assertFalse(HistoryManager.isIgnoringSave(), "멈춘 뒤에는 Undo 기록이 다시 쌓여야 함");
            assertFalse(HistoryManager.canUndo(), "불러온 보드는 새 기록으로 시작해야 함");
            assertEquals(GLIDER_AT_ORIGIN, liveCells());
            assertEquals(0L, statistic.getGeneration());
            assertEquals(5, statistic.getAlive());
            assertEquals(-1, detector.getCycle());
            assertEquals(1, ages.ageAt(1, 0));

            for (int i = 0; i < 4; i++)
                app.clockCommands().getTick().execute();
            assertEquals(shift(GLIDER_AT_ORIGIN, 1, 1), liveCells());
            assertEquals(4L, statistic.getGeneration());

            for (int i = 0; i < 4; i++)
                app.gridCommands().getUndo().execute();
            assertEquals(GLIDER_AT_ORIGIN, liveCells());
            assertFalse(HistoryManager.canUndo(), "Load 이전(블링커) 상태로는 Undo할 수 없어야 함");
        });
    }

    @ParameterizedTest(name = "{0} 형식")
    @ValueSource(strings = {"board.life", "board.cells", "board.rle"})
    @DisplayName("실행 중 Store하면 시계가 멈추고, Clear 후 같은 파일을 Load하면 보드가 그대로 복원된다")
    void storeClearLoadRoundTrip(String fileName) throws Throwable {
        File file = dir.resolve(fileName).toFile();

        onEventThread(() -> {
            for (Point p : shift(GLIDER_AT_ORIGIN, 30, 30))
                app.facade().setAlive(p.x, p.y, true);
            app.facade().setAlive(7, 20, true);
            app.facade().setAlive(8, 20, true);
            app.facade().setAlive(9, 20, true);
            app.clockCommands().runFast().execute();
            app.clockCommands().getTick().execute();
            app.clockCommands().getTick().execute();
            Set<Point> stored = liveCells();

            try {
                app.mediator().store(FileFormat.forFileName(file.getName()), file);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
            assertFalse(app.clockCommands().control().isRunning(), "Store는 실행 중인 시계를 먼저 멈춰야 함");
            assertEquals(stored, liveCells(), "Store가 보드를 바꾸면 안 됨");

            app.gridCommands().getClear().execute();
            assertEquals(Set.of(), liveCells());
            assertEquals(0, statistic.getAlive());

            loadThroughMenuPath(file);
            assertEquals(stored, liveCells());
            assertEquals(stored.size(), statistic.getAlive());
        });
    }

    @Test
    @DisplayName("Rule 메뉴로 규칙을 바꾸면 다음 세대부터 그 규칙으로 진화하고, add rule로 추가한 규칙도 바로 쓸 수 있다")
    void ruleMenuChangesEvolution() {
        app.facade().setAlive(10, 10, true);
        app.facade().setAlive(11, 10, true);

        app.ruleCommands().setRule("Seeds").execute();      // B2/S
        app.clockCommands().getTick().execute();
        assertEquals(Set.of(new Point(10, 9), new Point(11, 9), new Point(10, 11), new Point(11, 11)),
                liveCells());

        app.gridCommands().getClear().execute();
        app.facade().setAlive(40, 40, true);
        app.ruleCommands().addRule("IT Life without Death", "B3/S012345678").execute();
        app.ruleCommands().setRule("IT Life without Death").execute();
        app.clockCommands().getTick().execute();
        assertEquals(Set.of(new Point(40, 40)), liveCells(), "S0이 있으면 이웃 없는 셀도 살아남아야 함");

        app.ruleCommands().setRule("CONWAY").execute();
        app.clockCommands().getTick().execute();
        assertEquals(Set.of(), liveCells(), "Conway로 돌아오면 고립된 셀은 죽어야 함");
    }

    @Test
    @DisplayName("Stamp 메뉴에서 글라이더를 고르고 클릭하면 통계에 반영되고, Undo 한 번에 통째로 사라진다")
    void stampMenuThenClick() {
        app.stampCommands().setStamp("glider").execute();
        app.stampCommands().stampAct(app.model(), 20, 20).execute();

        assertEquals("glider", app.stampCommands().currStr());
        assertEquals(5, statistic.getAlive());
        assertEquals(5, liveCells().size());

        app.gridCommands().getUndo().execute();
        assertEquals(0, statistic.getAlive());
        assertFalse(HistoryManager.canUndo());
    }

    /** What {@link LoadCommand#execute()} does after the user picked a file. */
    private void loadThroughMenuPath(File file) {
        try {
            app.mediator().load(FileFormat.forFileName(file.getName()).load(file));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private Set<Point> liveCells() {
        Set<Point> cells = new HashSet<>();
        app.facade().visitCells((x, y, alive) -> {
            if (alive)
                cells.add(new Point(x, y));
        });
        return cells;
    }

    private static Set<Point> shift(Set<Point> cells, int dx, int dy) {
        Set<Point> shifted = new HashSet<>();
        for (Point p : cells)
            shifted.add(new Point(p.x + dx, p.y + dy));
        return shifted;
    }

    /** The Swing Timer started by Run ticks on the EDT, so scenarios that run the clock stay on that thread. */
    private static void onEventThread(Runnable scenario) throws Throwable {
        try {
            SwingUtilities.invokeAndWait(scenario);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
