package fidel.logic.evaluators;

import org.testng.annotations.Test;

import com.google.common.truth.Truth;

import fidel.common.LevelType;

@Test
public class SpeedRunEvaluatorTest {

    @Test
    void beforeAlien() {
        beforeAlienBetter(58, 59);
        beforeAlienBetter(59, 60);
        beforeAlienBetter(58, 57);
        beforeAlienBetter(57, 56);
        beforeAlienBetter(60, 56);
    }

    private void beforeAlienBetter(int xp1, int xp2) {
        SpeedRunEvaluator evaluator = new SpeedRunEvaluator(0, LevelType.BEFORE_ALIEN);
        Truth.assertThat(
                evaluator.evaluate(0, xp1)
        ).isGreaterThan(
                evaluator.evaluate(0, xp2)
        );
    }
}