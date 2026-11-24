package com.holub.life;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClockTest {


    // 시작 전 Clock 초기화
    @BeforeEach
    public void setUp() {
        Clock.instance().stop();
    }

    // 싱글톤 패턴 검증
    @Test
    public void testSingletonInstance() {
        Clock clock1 = Clock.instance();
        Clock clock2 = Clock.instance();

        assertSame(clock1, clock2, "Clock은 싱글톤이어야 합니다.");
    }

    // Clock의 start/stop 동작 검증
    @Test
    public void testClockStartAndStop() {
        Clock clock = Clock.instance();
        assertDoesNotThrow(() -> {
            clock.startTicking(100);
            clock.stop();
        }, "Clock의 start/stop 제어 중 예외가 발생하면 안 됩니다.");
    }
}