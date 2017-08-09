package fidel.logic;

class PlayerState {
    final int gold;
    final int xp;
    final int streak;
    final boolean afterTriple;
    final int hp;
    final int poison;
    final int maxHp;
    final boolean switchUsed;
    final int buttonsPressed;
    final int robotBars;
    final int bossHp;

    PlayerState(int gold, int xp, int streak, boolean afterTriple, int hp, int poison, int maxHp, boolean switchUsed, int buttonsPressed, int robotBars, int bossHp) {
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
