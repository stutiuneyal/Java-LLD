package util;

import java.util.UUID;

public class IdUtil {
    private IdUtil() {
    }

    public static String newId(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }
}
