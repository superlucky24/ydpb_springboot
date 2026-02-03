package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.PaymentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

    // 결제 내역 저장
    int insertPayment(PaymentVO payment);

    // 이미 결제한 내역있는지 확인 (재출력용)
    int checkPaymentExists(@Param("memId") String memId, @Param("docType") String docType);

    // 특정 유저의 특정 서류 마지막 결제 내역 조회
    PaymentVO getLatestPayment(@Param("memId") String memId, @Param("docType") String docType);
}
