package com.holub.life;

public class ClockCommands {
    // 정지/실행 상태(E4 State)를 가진 문맥 객체. Run/Halt/속도 변경은 모두 이 객체를 거칩니다.
    private final ClockControl clock;
    
    // 상태가 없는 커맨드들은 생성 시점에 한 번만 만들어 캐싱합니다 (Flyweight)
    private final Command tickCommand;
    private final Command haltCommand;

    // 생성자를 통해 UniverseModel을 주입받습니다.
    public ClockCommands(UniverseModel model) {
        this.clock = new ClockControl(model);
        this.tickCommand = new TickCommand();
        this.haltCommand = new HaltCommand(clock);
    }

    public ClockControl control() {
        return clock;
    }

    // 캐싱된 객체 반환
    public Command getTick() {
        return tickCommand;
    }

    public Command getHalt() {
        return haltCommand;
    }
    
    // 슬라이더 UI와 연결을 위한 동적 커맨드 생성
    public Command createRun(int delay) {
        return new RunCommand(clock, delay);
    }

    // 속도 슬라이더: 정지 중이면 속도만 기억하고, 실행 중이면 그 속도로 계속 실행
    public Command changeSpeed(int delay) {
        return () -> clock.changeSpeed(delay);
    }

    // 레거시 UI 메뉴와 연동되는 프리셋들 (매번 생성하거나 캐싱해도 됨)
    public Command runAgonizing() { return new RunCommand(clock, 500); }
    public Command runSlow()      { return new RunCommand(clock, 150); }
    public Command runMedium()    { return new RunCommand(clock, 70); }
    public Command runFast()      { return new RunCommand(clock, 30); }
}