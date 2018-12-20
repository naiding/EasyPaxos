package cool.naiding.easyPaxos.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class LoggerHelper {
    public static FileHandler getFileHandler(String filename, boolean append, Level level) {
        try {
            File file = new File(filename);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileHandler fileHandler = new FileHandler(filename, append);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord lr) {
                    return lr.getMessage() + System.lineSeparator();
                }
            });
            fileHandler.setLevel(level);
            return fileHandler;
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
