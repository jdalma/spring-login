package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form){
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form , BindingResult bindingResult , HttpServletResponse response){
        log.info("loing binding result = {}" , bindingResult);
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId() , form.getPassword());
        if(loginMember == null){
            bindingResult.reject("loginFail" , "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 - 만료 시간을 지정하지 않는 세션 쿠키 생성
        Cookie cookie = new Cookie("memberId" , String.valueOf(loginMember.getId()));
        response.addCookie(cookie);

        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logout(HttpServletResponse response){
        expiredCookie(response , "memberId");
        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form , BindingResult bindingResult , HttpServletResponse response){
        log.info("loing binding result = {}" , bindingResult);

        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId() , form.getPassword());
        if(loginMember == null){
            bindingResult.reject("loginFail" , "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공
        // 세션 관리자를 통해 세션을 생성하고 , 회원 데이터 보관
        sessionManager.createSession(loginMember , response);

        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request){
        sessionManager.expire(request);
        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form , BindingResult bindingResult , HttpServletRequest request){
        log.info("loing binding result = {}" , bindingResult);

        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId() , form.getPassword());
        if(loginMember == null){
            bindingResult.reject("loginFail" , "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공
        // 세션이 있으면 있는 세션 반환 , 없으면 신규 세션을 생성
        HttpSession session = request.getSession();

        // 세션에 로그인 회원 정보를 보관한다
        session.setAttribute(SessionConst.LOGIN_MEMBER , loginMember);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute LoginForm form , BindingResult bindingResult ,
                          @RequestParam(defaultValue = "/") String redirectURL ,
                          HttpServletRequest request){
        log.info("loing binding result = {}" , bindingResult);

        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId() , form.getPassword());
        if(loginMember == null){
            bindingResult.reject("loginFail" , "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공
        // 세션이 있으면 있는 세션 반환 , 없으면 신규 세션을 생성
        HttpSession session = request.getSession();

        // 세션에 로그인 회원 정보를 보관한다
        session.setAttribute(SessionConst.LOGIN_MEMBER , loginMember);

        // 필터에서 미인증 사용자가 로그인 화면으로 이동 후 정보 입력 후 로그인 요청 시 같이 담겨 있는 redirectURL을 넘겨준다
        return "redirect:" + redirectURL;
    }

    private void expiredCookie(HttpServletResponse response , String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
