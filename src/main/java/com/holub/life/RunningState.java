package com.holub.life;

/** The clock is ticking. Run or a speed change only changes the speed; Halt stops it. */
final class RunningState implements ClockState {
    @Override
    public void run(ClockControl clock, int delay) {
        clock.startTicking(delay);
    }

    @Override
    public void halt(ClockControl clock) {
        clock.stopTicking();
        HistoryManager.setIgnoreSave(false);
        clock.changeState(HALTED);
    }

    @Override
    public void changeSpeed(ClockControl clock, int delay) {
        clock.startTicking(delay);
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public String label() {
        return "실행 중";
    }
}
