package one.dfy.bily.api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LocalUserInterceptor implements HandlerInterceptor {

    @Value("${dev.fallback-user-id}")
    private Long fallbackUserId;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        // Origin 이 localhost:3001 인지 확인
        String origin = request.getHeader("Origin");
        if (origin != null && origin.contains("localhost:3001")) {
            // 컨트롤러에서 꺼내 쓸 수 있도록 request 속성에 저장
            request.setAttribute("FALLBACK_USER_ID", fallbackUserId);
        }
        return true;
    }
}

