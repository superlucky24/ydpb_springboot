package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.Criteria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
public class AdminMapperTests {
    @Autowired
    private AdminMapper adminMapper;

    @Test
    public void testGetList(){
        Criteria cri = new Criteria();
        System.out.println("민원  : "+adminMapper.getAllCount(cri));
    }
}
