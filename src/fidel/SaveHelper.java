package fidel;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class SaveHelper {

    public static final String SAVE_LOCATION = "E:\\Users\\Hohol\\AppData\\Roaming\\fidel\\Local Store\\save.txt";

    public static void main(String[] args) {
        dump("tmp");
        //undump("tmp");
    }

    private static void undump(String name) {
        Tryy.tryy(() -> {
            FileUtils.copyFile(new File(dumpName(name)), new File(SAVE_LOCATION));
        });
    }

    public static void dump(String name) {
        Tryy.tryy(() -> {
            FileUtils.copyFile(new File(SAVE_LOCATION), new File(dumpName(name)));
        });
    }

    private static String dumpName(String name) {
        return "saves/" + name + ".txt";
    }
}
