package fidel;

import static java.awt.event.KeyEvent.*;

public enum Command {
    UP(VK_UP), LEFT(VK_LEFT), DOWN(VK_DOWN), RIGHT(VK_RIGHT), ENTER(VK_ENTER), BARK(VK_CONTROL);

    public final int keyCode;

    Command(int keyCode) {
        this.keyCode = keyCode;
    }
}
