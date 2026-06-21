package net.p5w.dp.common.util;

import java.util.UUID;

public class UuidUtil {
    public static String getRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}