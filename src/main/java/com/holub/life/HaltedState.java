package com.holub.life;

/** The clock is stopped. Run starts it; Halt only makes sure it is stopped. */
final class HaltedState implements ClockState {
    @Override
    public void run(ClockControl clock, int delay) {
        // One snapshot before the run, none while running: a single Undo returns to this board.
        HistoryManager.saveState(clock.model());
        HistoryManager.setIgnoreSave(true);
        clock.startTicking(delay);
        clock.changeState(RUNNING);
    }

    @Override
    public void halt(ClockControl clock) {
        clock.stopTicking();
        HistoryManager.setIgnoreSave(false);
    }

    @Override
    public void changeSpeed(ClockControl clock, int delay) {
        clock.rememberDelay(delay);
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public String label() {
        return "정지";
    }
}
