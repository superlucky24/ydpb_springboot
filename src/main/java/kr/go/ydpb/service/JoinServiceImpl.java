package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.JoinMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JoinServiceImpl implements JoinService{

    @Setter(onMethod_ = @Autowired)
    private JoinMapper joinMapper;


    @Override
    public void addMember(MemberVO vo) {
        joinMapper.insertMember(vo);
    }
}
