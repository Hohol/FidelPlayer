package fidel.common;

import java.util.concurrent.Callable;

public class Tryy {
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
}
