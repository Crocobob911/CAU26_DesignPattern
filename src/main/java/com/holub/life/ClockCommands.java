package com.holub.life;

public class ClockCommands {
    private static final Command TICK = new TickCommand();
    private static final Command HALT = new HaltCommand();

    private ClockCommands() {}

    public static Command getTick() {
        return TICK;
    }

    public static Command getHalt() {
        return HALT;
    }
    
    // 슬라이더 UI와 연결을 위한 커스텀 속도
    public static Command createRun(int delay) {
        return new RunCommand(delay);
    }

    // 레거시 UI 메뉴와 연동되는 프리셋들
    public static Command runAgonizing() { return new RunCommand(500); }
    public static Command runSlow()      { return new RunCommand(150); }
    public static Command runMedium()    { return new RunCommand(70); }
    public static Command runFast()      { return new RunCommand(30); }
}
