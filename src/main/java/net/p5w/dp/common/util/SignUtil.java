package net.p5w.dp.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SignUtil {

    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) sb.append("0");
                sb.append(hex);
            }
            return sb.toString().toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }
}