package kr.hhplus.be.server.support.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private static final String QUEUE_TOKEN_HEADER = "X-Queue-token";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String token = request.getHeader(QUEUE_TOKEN_HEADER);

        if("/queue/waiting".equals(path) && "POST".equalsIgnoreCase(method)){
            if(token == null || token.isEmpty()){
                log.error("토큰이 없습니다.");
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "토큰이 필요합니다.");
                return false;
            }

            if(!validateQueue(token)){
                log.error("유효한 토큰({})이 아닙니다.", token);
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "유효하지 않는 토큰입니다.");
                return false;
            }
        } else if (path.matches("^/queue/\\d+$") && "GET".equalsIgnoreCase(method)) {
            if(token != null && !token.isEmpty()){
                log.error("토큰이 존재합니다.");
                response.sendError(HttpStatus.BAD_REQUEST.value(), "토큰이 없어야 합니다.");
                return false;
            }
        }

        return true;
    }

    private boolean validateQueue(String token){
        try {
            UUID.fromString(token);
            return true; // 유효한 UUID 형식
        } catch (IllegalArgumentException e) {
            return false; // 잘못된 UUID 형식
        }
    }
}
