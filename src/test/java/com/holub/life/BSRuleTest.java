package com.holub.life;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BSRuleTest {

    @Test // 기존 BSRule 존재여부 확인
    public void testConwayRegistration() {
        BSRule rule = BSRule.get("CONWAY");

        assertNotNull(rule);
        assertEquals("CONWAY", rule.name());
        assertEquals("B3/S23", rule.notation());
    }

    @Test // 기존 BSRule 정상실행여부 확인
    public void testRuleExecution() {
        BSRule rule = BSRule.get("Seeds");

        assertNotNull(rule);
        //Seeds : B2/S
        for (int neighbors = 0; neighbors <= 8; neighbors++) {
            assertEquals(
                neighbors == 2,
                rule.nextState(false, neighbors)
            );

            assertFalse(rule.nextState(true, neighbors));
        }
    }

    @Test // 사용자 지정 rule 추가 및 test 성립여부 확인
    public void testCustomRuleRegistration() {
        BSRule.register("TEST_RULE", "B36/S23");

        BSRule rule = BSRule.get("TEST_RULE");

        assertNotNull(rule);
        assertEquals("B36/S23", rule.notation());
        assertTrue(rule.nextState(false, 3));
        assertTrue(rule.nextState(false, 6));
        assertFalse(rule.nextState(false, 5));
    }

    @Test // 잘못된 형식의 경우 예외처리 확인
    public void testInvalidRuleRegistration() {
        String name = "INVALID_RULE_TEST";

        try {
            BSRule.register(name, "B3/S2X");
            fail("잘못된 규칙이 등록되었습니다.");
        } catch (RuntimeException e) {
            assertNull(BSRule.get(name));
        }
    }

    @Test // 합의된 API: parse는 등록하지 않음, CONWAY 상수, all()은 등록 순서, 형식 오류는 IllegalArgumentException
    public void testParseAllAndConwayConstant() {
        BSRule parsed = BSRule.parse("B36/S23");
        assertEquals("B36/S23", parsed.notation());
        assertTrue(parsed.nextState(false, 6));
        assertNull(BSRule.get("B36/S23"));

        assertSame(BSRule.CONWAY, LifeRule.CONWAY);
        assertSame(BSRule.CONWAY, BSRule.all().get(0));
        assertTrue(BSRule.all().contains(BSRule.get("Day&Night")));

        assertThrows(IllegalArgumentException.class, () -> BSRule.parse("B9/S23"));
        assertThrows(IllegalArgumentException.class, () -> BSRule.parse("3/23"));
    }
}
