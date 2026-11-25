package com.holub.life;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClockCommandsTest {

    private UniverseModel dummyModel;
    private ClockCommands clockRegistry;
    private boolean isTickFired;

    @BeforeEach
    public void setUp() {
        // 1. 싱글톤 및 정적 히스토리 상태 초기화
        Clock.instance().clearInstanceForTest();
        HistoryManager.clearHistory();
        HistoryManager.setIgnoreSave(false);

        // 2. 보드 시뮬레이션을 돌리지 않으므로 가벼운 Cell.DUMMY로 모델 구성
        Neighborhood board = new Neighborhood(8, new Neighborhood(8, Cell.DUMMY));
        dummyModel = new UniverseModel(board, 64, 8, 2, 8, LifeRule.CONWAY);

        // 3. 테스트 대상인 ClockCommands 레지스트리 생성
        clockRegistry = new ClockCommands(dummyModel);
        isTickFired = false;
    }

    @Test
    public void testFlyweightCachingAndFactory() {
        // 상태가 없는 Tick, Halt 커맨드는 캐싱되어 동일 인스턴스를 반환해야 함
        assertSame(clockRegistry.getTick(), clockRegistry.getTick());
        assertSame(clockRegistry.getHalt(), clockRegistry.getHalt());

        // 딜레이와 모델을 갖는 Run 커맨드는 호출 시마다 새 인스턴스를 생성해야 함
        assertNotSame(clockRegistry.createRun(100), clockRegistry.createRun(100));
        assertNotSame(clockRegistry.runFast(), clockRegistry.runFast());
    }

    @Test
    public void testTickCommandNotifiesListener() {
        Clock.instance().addClockListener(() -> isTickFired = true);

        clockRegistry.getTick().execute();

        assertTrue(isTickFired, "TickCommand 실행 시 Clock 리스너가 호출되어야 합니다.");
    }

    @Test
    public void testRunCommandSavesStateAndIgnoresSubsequentSaves() {
        // RunCommand 실행 시 시작 직전 상태를 저장하고, 자동 진행 중 추가 저장을 막아야 함
        clockRegistry.createRun(100).execute();

        assertTrue(HistoryManager.canUndo(), "Run 시작 시점의 초기 상태가 Undo 스택에 저장되어야 합니다.");

        // ignoreSave(true)가 정상 작동했다면, 이후 saveState 호출은 무시되어야 함
        HistoryManager.saveState(dummyModel);
        HistoryManager.popUndo(); // 처음에 저장된 1개만 꺼냄
        assertFalse(HistoryManager.canUndo(), "Run 실행 중에는 추가 상태 저장이 무시되어야 합니다.");

        // 백그라운드 타이머 정리
        Clock.instance().stop();
    }

    @Test
    public void testHaltCommandStopsClockAndReenablesSave() {
        // 먼저 시계를 가동해 ignoreSave를 true 상태로 만듦
        clockRegistry.runSlow().execute();

        // HaltCommand 실행
        clockRegistry.getHalt().execute();

        // ignoreSave(false)로 복구되었으므로 다시 상태 저장이 가능해야 함
        HistoryManager.clearHistory();
        HistoryManager.saveState(dummyModel);
        assertTrue(HistoryManager.canUndo(), "Halt 이후에는 다시 상태 저장이 활성화되어야 합니다.");
    }

    @Test // E4 State: 정지 → Run → (실행 중 Run은 속도만 변경) → Halt → 정지
    public void testRunAndHaltSwitchClockState() {
        ClockControl clock = clockRegistry.control();
        assertFalse(clock.isRunning());

        clockRegistry.createRun(100).execute();
        assertTrue(clock.isRunning());
        assertEquals(100, clock.delay());

        clockRegistry.runFast().execute();
        assertTrue(clock.isRunning());
        assertEquals(30, clock.delay());
        HistoryManager.popUndo();
        assertFalse(HistoryManager.canUndo(), "실행 중 Run은 Undo 기록을 새로 남기지 않아야 합니다.");

        clockRegistry.getHalt().execute();
        assertFalse(clock.isRunning());
        assertFalse(HistoryManager.isIgnoringSave());
    }

    @Test // E4 속도 슬라이더: 정지 중에는 속도만 기억하고 시작하지 않음, 다음 Run이 그 속도를 씀
    public void testSpeedChangeWhileHaltedOnlyRemembersDelay() {
        ClockControl clock = clockRegistry.control();
        clockRegistry.changeSpeed(70).execute();

        assertFalse(clock.isRunning());
        assertEquals(70, clock.delay());
        assertFalse(HistoryManager.canUndo());

        clockRegistry.createRun(clock.delay()).execute();
        clockRegistry.changeSpeed(250).execute();
        assertTrue(clock.isRunning());
        assertEquals(250, clock.delay());
    }

    @AfterEach
    public void tearDown() {
        Clock.instance().stop();
    }
}