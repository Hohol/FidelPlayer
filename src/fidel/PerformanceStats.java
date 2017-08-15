package fidel;

import fidel.common.LevelType;

import java.time.Duration;

public class PerformanceStats {
    final Duration duration;
    final int levelIndex;
    final LevelType levelType;

    public PerformanceStats(Duration duration, int levelIndex, LevelType levelType) {
        this.duration = duration;
        this.levelIndex = levelIndex;
        this.levelType = levelType;
    }

    @Override
    public String toString() {
        return "PerformanceStats{" +
                "duration=" + duration +
                ", levelIndex=" + levelIndex +
                ", levelType=" + levelType +
                '}';
    }
}
