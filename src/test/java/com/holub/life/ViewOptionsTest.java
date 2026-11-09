package com.holub.life;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** E8: View menu options. The menus themselves need a window, so this checks the state and labels behind them. */
class ViewOptionsTest {
    private static UniversePanel panel() {
        return new UniversePanel(new LifeMediator(new UniverseBuilder().build(), () -> { }));
    }

    @Test
    @DisplayName("처음 상태는 원본 화면과 같음: Classic 테마, 격자선 켬, 나이 색 끔")
    void defaultsMatchOriginalLook() {
        UniversePanel panel = panel();
        assertInstanceOf(ClassicTheme.class, panel.theme());
        assertTrue(panel.showsGridLines());
        assertFalse(panel.showsCellAge());
    }

    @Test
    @DisplayName("메뉴에 올릴 테마 이름은 서로 달라야 함 (메뉴 항목을 이름으로 찾기 때문)")
    void themeNamesAreUnique() {
        Set<String> names = new HashSet<>();
        for (BoardTheme theme : UniversePanel.THEMES)
            assertTrue(names.add(theme.name()), theme.name());
        assertEquals(3, names.size());
    }

    @Test
    @DisplayName("옵션을 바꾸면 패널 상태가 바뀌고, 다시 바꾸면 원래대로")
    void optionsToggle() {
        UniversePanel panel = panel();
        BoardTheme dark = UniversePanel.THEMES.get(1);
        panel.setTheme(dark);
        panel.setShowGridLines(false);
        panel.setShowCellAge(true);
        assertSame(dark, panel.theme());
        assertFalse(panel.showsGridLines());
        assertTrue(panel.showsCellAge());

        panel.setShowGridLines(true);
        panel.setShowCellAge(false);
        assertTrue(panel.showsGridLines());
        assertFalse(panel.showsCellAge());
    }

    @Test
    @DisplayName("메뉴 글자: 고른 테마 앞에 점, 켜고 끄는 항목 뒤에 On/Off")
    void menuLabels() {
        assertEquals("• Dark", UniversePanel.choiceLabel("Dark", true));
        assertEquals("Dark", UniversePanel.choiceLabel("Dark", false));
        assertEquals("Grid Lines: On", UniversePanel.toggleLabel("Grid Lines", true));
        assertEquals("Cell Age Colors: Off", UniversePanel.toggleLabel("Cell Age Colors", false));
    }
}
