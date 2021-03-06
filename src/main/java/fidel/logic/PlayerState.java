package fidel.logic;

public class PlayerState {
    public final int gold;
    public final int xp;
    final int streak;
    final boolean afterTriple;
    public final int hp;
    final int poison;
    public final int maxHp;
    final boolean switchUsed;
    final int buttonsPressed;
    final int robotBars;
    public final int bossHp;
    final boolean usedBomb;
    final int chessLost;
    final int chessKilledCnt;

    PlayerState(int gold, int xp, int streak, boolean afterTriple, int hp, int poison, int maxHp, boolean switchUsed, int buttonsPressed, int robotBars, int bossHp, boolean usedBomb, int chessLost, int chessKilledCnt) {
        this.gold = gold;
        this.xp = xp;
        this.streak = streak;
        this.afterTriple = afterTriple;
        this.hp = hp;
        this.poison = poison;
        this.maxHp = maxHp;
        this.switchUsed = switchUsed;
        this.buttonsPressed = buttonsPressed;
        this.robotBars = robotBars;
        this.bossHp = bossHp;
        this.usedBomb = usedBomb;
        this.chessLost = chessLost;
        this.chessKilledCnt = chessKilledCnt;
    }

    @Override
    public String toString() {
        return "PlayerState{" +
                "gold=" + gold +
                ", xp=" + xp +
                ", streak=" + streak +
                ", afterTriple=" + afterTriple +
                ", hp=" + hp +
                ", poison=" + poison +
                ", maxHp=" + maxHp +
                ", switchUsed=" + switchUsed +
                ", buttonsPressed=" + buttonsPressed +
                ", robotBars=" + robotBars +
                ", bossHp=" + bossHp +
                '}';
    }
}
