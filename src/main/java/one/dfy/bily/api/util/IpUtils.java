package one.dfy.bily.api.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    private static final String DEFAULT_IP = "UNKNOWN";

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) return DEFAULT_IP;

        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        String fallbackIp = request.getRemoteAddr();
        return (fallbackIp != null && !fallbackIp.isBlank()) ? fallbackIp : DEFAULT_IP;
    }

    private IpUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}
