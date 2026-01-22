package kr.go.ydpb;

import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.JoinMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@SpringBootTest
@Transactional
class YdpbApplicationTests {

	@Autowired
	private JoinMapper joinMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void testInsert() {
		MemberVO vo = new MemberVO();
		vo.setMemId("testa");
		vo.setMemName("testo");
		vo.setMemBirth(null);
		vo.setMemGender("여");
		vo.setMemPassword("1111");
		vo.setMemAddress("서울시 강동구");
		vo.setMemAddressDetail("11동 303호");
		vo.setMemTel("0231513233");
		vo.setMemPhone("01023489732");
		vo.setMemEmail("test@dmkl.com");
		vo.setMemNews("N");

		joinMapper.insertMember(vo);

	}

}
