package com.holub.life;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GridCommandsTest {

    private UniverseModel model;
    private GridCommands gridCommands;
    private ClockCommands clockCommands;

    @BeforeEach
    public void setUp() {
        // 1. 싱글톤 Clock 및 정적 HistoryManager 초기화
        Clock.instance().clearInstanceForTest();
        HistoryManager.clearHistory();
        HistoryManager.setIgnoreSave(false);

        // 2. 실제 셀 생존 및 세대 전환 검증을 위해 Resident로 64x64 보드 구성
        Neighborhood board = new Neighborhood(8, new Neighborhood(8, new Resident()));
        model = new UniverseModel(board, 64, 8, 2, 8, LifeRule.CONWAY);

        // 3. 시계 틱 신호가 모델로 전달되도록 구독 연결
        Clock.instance().addClockListener(model::tick);

        // 4. 커맨드 레지스트리 생성
        gridCommands = new GridCommands(model);
        clockCommands = new ClockCommands(model);
    }

    @AfterEach
    public void tearDown() {
        // 테스트 실패 시에도 백그라운드 Swing Timer가 남지 않도록 정리
        Clock.instance().stop();
    }

    @Test
    public void testEditAndSingleTickUndoRedo() {
        // Given: Blinker 패턴(세로 3칸) 배치 -> setAlive 호출마다 상태 저장됨
        model.setAlive(1, 0, true);
        model.setAlive(1, 1, true);
        model.setAlive(1, 2, true);
        assertEquals(0, model.generation());

        // When 1: TickCommand 실행 -> 1세대로 진행 (가로 3칸으로 전환)
        clockCommands.getTick().execute();
        assertEquals(1, model.generation());
        assertTrue(model.isAlive(0, 1));
        assertTrue(model.isAlive(1, 1));
        assertTrue(model.isAlive(2, 1));
        assertFalse(model.isAlive(1, 0));

        // When 2: UndoCommand 실행 -> 0세대(세로 3칸)로 복원
        gridCommands.getUndo().execute();
        assertEquals(0, model.generation());
        assertTrue(model.isAlive(1, 0));
        assertTrue(model.isAlive(1, 1));
        assertTrue(model.isAlive(1, 2));
        assertFalse(model.isAlive(0, 1));

        // When 3: RedoCommand 실행 -> 다시 1세대(가로 3칸)로 복원
        gridCommands.getRedo().execute();
        assertEquals(1, model.generation());
        assertTrue(model.isAlive(0, 1));
        assertTrue(model.isAlive(2, 1));
    }

    @Test
    public void testClearCommandCanBeUndone() {
        model.setAlive(5, 5, true);
        gridCommands.getClear().execute();
        assertFalse(model.isAlive(5, 5));

        gridCommands.getUndo().execute();
        assertTrue(model.isAlive(5, 5));
    }

    @Test
    public void testUndoHistoryKeepsOnlyTheNewestSteps() {
        int edits = HistoryManager.MAX_HISTORY + 20;
        for (int i = 0; i < edits; i++)
            model.setAlive(i % 64, i / 64, true);

        int undone = 0;
        while (HistoryManager.canUndo()) {
            gridCommands.getUndo().execute();
            undone++;
        }

        assertEquals(HistoryManager.MAX_HISTORY, undone);
        // the 20 oldest edits can no longer be undone
        assertTrue(model.isAlive(19, 0));
        assertFalse(model.isAlive(20, 0));
    }

    /**
     * RunCommand가 켠 Swing Timer는 EDT에서 model.tick()을 호출한다.
     * 테스트 스레드에서 시나리오를 돌리면 그 tick과 동시에 모델·HistoryManager를 건드려 결과가 흔들리므로,
     * 앱과 같이 EDT 한 스레드에서 실행한다. (Timer tick은 큐에서 기다리다가 Halt 때 취소된다)
     */
    private static void onEventThread(Runnable scenario) throws Throwable {
        try {
            SwingUtilities.invokeAndWait(scenario);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testRunBypassAndHaltRestoreFlow() throws Throwable {
        onEventThread(this::runBypassAndHaltRestoreFlow);
    }

    private void runBypassAndHaltRestoreFlow() {
        // Given: 0세대 초기 패턴(Blinker) 세팅 후 이전 편집 히스토리 정리
        model.setAlive(1, 0, true);
        model.setAlive(1, 1, true);
        model.setAlive(1, 2, true);
        HistoryManager.clearHistory();

        // When 1: RunCommand 실행 (실행 직전 0세대 상태 1회 저장 + ignoreSave = true 설정)
        clockCommands.createRun(100).execute();
        assertTrue(HistoryManager.canUndo(), "Run 시작 직전 상태가 Undo 스택에 저장되어야 합니다.");

        // Run 상태에서 여러 번 틱이 진행됨 (bypass가 켜져 있으므로 중간 세대는 저장되지 않아야 함)
        clockCommands.getTick().execute();
        clockCommands.getTick().execute();
        clockCommands.getTick().execute();

        // When 2: HaltCommand 실행 (타이머 정지 + ignoreSave = false 복구)
        clockCommands.getHalt().execute();
        long generationAtHalt = model.generation();
        assertTrue(generationAtHalt >= 3, "Run 중 발생한 틱만큼 세대가 진행되어 있어야 합니다.");

        // Then 1: 단 한 번의 UndoCommand로 중간 세대를 모두 건너뛰고 0세대로 복귀하는지 검증
        gridCommands.getUndo().execute();
        assertEquals(0, model.generation(), "단 한 번의 Undo로 Run 시작 전(0세대)으로 돌아가야 합니다.");
        assertTrue(model.isAlive(1, 0));
        assertTrue(model.isAlive(1, 1));
        assertTrue(model.isAlive(1, 2));
        assertFalse(HistoryManager.canUndo(), "Bypass 덕분에 중간 틱 상태들이 스택에 쌓이지 않았어야 합니다.");

        // Then 2: RedoCommand 실행 시 Halt 시점의 세대로 정확히 복귀하는지 검증
        gridCommands.getRedo().execute();
        assertEquals(generationAtHalt, model.generation(), "Redo 실행 시 Halt 직전 세대로 돌아가야 합니다.");

        // Then 3: Halt 이후 bypass(ignoreSave)가 false로 돌아와 새 작업이 정상 저장되는지 검증
        clockCommands.getTick().execute();
        assertTrue(HistoryManager.canUndo(), "Halt 이후에는 다시 상태 저장이 활성화되어야 합니다.");
        assertFalse(HistoryManager.canRedo(), "새로운 상태가 저장되면 기존 Redo 스택은 비워져야 합니다.");
    }
}