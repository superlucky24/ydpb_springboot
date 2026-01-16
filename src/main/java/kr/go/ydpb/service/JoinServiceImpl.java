package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.JoinMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JoinServiceImpl implements JoinService{
    private final BCryptPasswordEncoder encoder; //암호화용 필드

    @Setter(onMethod_ = @Autowired)
    private JoinMapper joinMapper;


    @Override
    public void addMember(MemberVO vo) {
        //테스트를 위해 암호화 기능 주석 처리
        //vo.setMemPassword(encoder.encode(vo.getMemPassword()));
        joinMapper.insertMember(vo);
    }

    @Override
    public boolean isIdExist(String memId) {
        return joinMapper.countById(memId) > 0;
    }
}
