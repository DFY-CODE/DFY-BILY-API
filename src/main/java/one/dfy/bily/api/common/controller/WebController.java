package one.dfy.bily.api.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    // 모든 클라이언트 사이드 라우팅 경로를 명시
    @GetMapping(value = {
            "/",
            "/login",
            "/emailver",
            "/dashboard",
            "/register",
            "/profile",
            "/space-modify/**",
            "/scheduling" // React 라우트 패턴
    })
    public String forwardToReact() {
        return "forward:/index.html";
    }


}
