package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * @param loginId
     * @param password
     * @return null 로그인 실패
     */
    public Member login(String loginId , String password){
//        Optional<Member> memberOptional = memberRepository.findByLoginId(loginId);
//        Member member = memberOptional.get();
//        if(member.getPassword().equals(password)){
//            return member;
//        }
//        return null;

        return memberRepository.findByLoginId(loginId)
                                .filter(m -> m.getPassword().equals(password))
                                .orElse(null);
    }
}
