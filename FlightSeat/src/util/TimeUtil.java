package util;

import java.time.Instant;

public class TimeUtil {
    private TimeUtil() {
    }

    public static Instant now() {
        return Instant.now();
    }
}
