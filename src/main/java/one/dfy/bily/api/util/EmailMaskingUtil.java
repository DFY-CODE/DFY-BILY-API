package one.dfy.bily.api.util;

public class EmailMaskingUtil {
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email; // 유효하지 않으면 그대로 반환
        }

        String[] parts = email.split("@", 2);
        String idPart = parts[0];
        String domainPart = parts[1];

        // ID 마스킹
        String maskedId = idPart.length() > 1
                ? idPart.charAt(0) + "*".repeat(idPart.length() - 1)
                : idPart;

        // 도메인 마스킹
        int dotIndex = domainPart.indexOf(".");
        if (dotIndex == -1) {
            return maskedId + "@" + domainPart; // 도메인에 점이 없으면 그대로
        }

        String domainName = domainPart.substring(0, dotIndex);
        String domainSuffix = domainPart.substring(dotIndex); // .com, .co.kr 등

        String maskedDomain = domainName.length() > 1
                ? domainName.charAt(0) + "*".repeat(domainName.length() - 1)
                : domainName;

        return maskedId + "@" + maskedDomain + domainSuffix;
    }
}
