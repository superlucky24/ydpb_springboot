package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GalleryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
public class AdminMapperTests {
    @Autowired
    private GalleryMapper galleryMapper;


    @Test
    public void testGetList(){
        GalleryVO gvo = galleryMapper.read(1);
        System.out.println("조회 테스트 => " + gvo);
    }
}
