package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");
        // ServletRequest , ServletResponse는 HttpServletRequest , HttpServletResponse의 부모 인터페이스 이다
        // 부모 인터페이스에는 기능이 많이 부족하기 때문에 다운 캐스팅을 해서 쓰자
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        try{
            log.info("REQUEST [{}][{}]" , uuid , requestURI);

            // doFilter를 호출하지 않으면 멈춰버린다
            // 다음 필터가 있으면 다음 필터가 호출되고 없으면 서블릿이 호출된다
            chain.doFilter(req , res);
        } catch(Exception e){
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}]" , uuid , requestURI);
        }

    }

    @Override
    public void destroy() {
        log.info("log filter destory");
    }
}
