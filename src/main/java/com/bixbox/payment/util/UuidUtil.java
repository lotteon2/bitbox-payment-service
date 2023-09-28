package com.bixbox.payment.util;

import java.util.UUID;

public class UuidUtil {
    public static String getUuid() {
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();
        return uuidStr.substring(uuidStr.length() - 16).replace("-", "");
    }
}
