package com.holub.life;

public class ClockCommands {
    // 이제 static이 아닙니다. 특정 UniverseModel 인스턴스에 종속됩니다.
    private final UniverseModel model;
    
    // 상태가 없는 커맨드들은 생성 시점에 한 번만 만들어 캐싱합니다 (Flyweight)
    private final Command tickCommand;
    private final Command haltCommand;

    // 생성자를 통해 UniverseModel을 주입받습니다.
    public ClockCommands(UniverseModel model) {
        this.model = model;
        this.tickCommand = new TickCommand();
        this.haltCommand = new HaltCommand(); // Halt는 Clock만 제어하므로 model 불필요
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
        return new RunCommand(model, delay);
    }

    // 레거시 UI 메뉴와 연동되는 프리셋들 (매번 생성하거나 캐싱해도 됨)
    public Command runAgonizing() { return new RunCommand(model, 500); }
    public Command runSlow()      { return new RunCommand(model, 150); }
    public Command runMedium()    { return new RunCommand(model, 70); }
    public Command runFast()      { return new RunCommand(model, 30); }
}