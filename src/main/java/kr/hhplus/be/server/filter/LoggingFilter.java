package kr.hhplus.be.server.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Component
@Slf4j
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String method = httpServletRequest.getMethod();
        String uri = httpServletRequest.getRequestURI();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // 몇시에 어떤 uri가 들어왔는지 확인 용도
        String logWrite = "[LOGGING FILTER][" + method + "][" + uri + "][" + now + "]";
        log.info(logWrite);
        chain.doFilter(request, response);
    }
}
