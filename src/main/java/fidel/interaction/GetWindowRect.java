package fidel.interaction;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.awt.*;

public class GetWindowRect {

    interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class,
                W32APIOptions.DEFAULT_OPTIONS);

        HWND FindWindow(String lpClassName, String lpWindowName);

        int GetWindowRect(HWND handle, int[] rect);
    }

    public static Rectangle getRect(String windowName) {
        HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
        if (hwnd == null) {
            throw new RuntimeException("window not found: " + windowName);
        }

        int[] rect = {0, 0, 0, 0};
        int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
        if (result == 0) {
            throw new RuntimeException("something went wrong: " + windowName);
        }
        int x = rect[0];
        int y = rect[1];
        int w = rect[2] - x + 1;
        int h = rect[3] - y + 1;
        return new Rectangle(x, y, w, h);
    }
}