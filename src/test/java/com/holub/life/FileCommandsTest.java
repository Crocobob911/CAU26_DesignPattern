package com.holub.life;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileCommandsTest {

    @Test
    public void testFileCommandsRegistry() {

        Neighborhood board = new Neighborhood(8, new Neighborhood(8, Cell.DUMMY));
        UniverseModel dummyModel = new UniverseModel(board, 64, 8, 2, 8, LifeRule.CONWAY);
        
        FileCommands commands = new FileCommands(new LifeMediator(new LifeFacade(dummyModel), () -> { }));

        assertNotNull(commands.getSave(), "SaveCommand가 생성되어야 합니다.");
        assertNotNull(commands.getLoad(), "LoadCommand가 생성되어야 합니다.");
        assertTrue(commands.getSave() instanceof Command);
        assertTrue(commands.getLoad() instanceof Command);
    }
}