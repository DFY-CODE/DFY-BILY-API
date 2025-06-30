package one.dfy.bily.api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import one.dfy.bily.api.user.constant.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;

    /* --------------------- 만료 시간 상수 --------------------- */
    // 1시간 = 3 600 000 ms
    private static final long ACCESS_TOKEN_EXP   = 60 * 60 * 1000L;
    // 3시간 = 10 800 000 ms
    private static final long REFRESH_TOKEN_EXP  = 3 * 60 * 60 * 1000L;
    /* -------------------------------------------------------- */

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Access Token 생성 : 1시간 유효 */
    public String createAccessToken(Long userId, Role role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("roles", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Refresh Token 생성 : 3시간 유효 */
    public String createRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
