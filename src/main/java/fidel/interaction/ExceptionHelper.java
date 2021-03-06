package fidel.interaction;

import java.util.concurrent.Callable;

public class ExceptionHelper {
    public static void tryy(MyRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T tryy(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface MyRunnable {
        void run() throws Exception;
    }

    public static void fail() {
        fail("");
    }

    public static void fail(String msg) {
        throw new RuntimeException(msg);
    }
}
