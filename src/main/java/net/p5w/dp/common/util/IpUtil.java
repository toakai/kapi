package net.p5w.dp.common.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取客户端真实IP工具类
 * 自动把本地IPv6 ::1 转为 127.0.0.1
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCAL_IPV4 = "127.0.0.1";
    private static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && !UNKNOWN.equalsIgnoreCase(ip) && ip.length() > 0) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return convertLocalIp(ip);
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !UNKNOWN.equalsIgnoreCase(ip)) {
            return convertLocalIp(ip);
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !UNKNOWN.equalsIgnoreCase(ip)) {
            return convertLocalIp(ip);
        }

        // 最后获取原始地址
        ip = request.getRemoteAddr();
        return convertLocalIp(ip);
    }

    /**
     * 本地IPv6地址转为IPv4
     */
    private static String convertLocalIp(String ip) {
        if (LOCAL_IPV6.equals(ip)) {
            return LOCAL_IPV4;
        }
        return ip;
    }
}