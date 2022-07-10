package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        // 이렇게 request에 넣어놓으면 요청 생명주기가 끝날 때 까지 참조할 수 있다
        // (afterCompletion에서도 참조 할 수 있다)
        request.setAttribute(LOG_ID, uuid);

        // 만약 해당 요청이 @RequestMapping으로 작성된 경로로 들어온다면 해당 컨트롤러는 HandlerMethod 타입으로 처리된다
        // 정적 리소스는 ResourceHttpRequestHandler로 처리된다
        if(handler instanceof HandlerMethod){
            // 호출할 컨트롤러의 메서드의 모든 정보가 포함되어 있다
            HandlerMethod hm = (HandlerMethod) handler;
        }

        log.info("REQUEST [{}][{}][{}]" , uuid , requestURI , handler);

        // return false를 하게되면 여기서 끝난다
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("post Handle [{}]" , modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = (String) request.getAttribute(LOG_ID);

        log.info("RESPONSE [{}][{}][{}]" , uuid , requestURI , handler);
        if(ex != null){
            log.error("afterCompletion Error !!! " , ex);
        }
    }
}
