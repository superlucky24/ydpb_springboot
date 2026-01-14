package kr.go.ydpb.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageDTO {
    private int startPage;  // 현재화면 시작번호
    private int endPage;    // 현재화면 끝번호
    private int realEnd;    // 전체 페이징 끝번호
    private boolean prev;   // 이전 유무
    private boolean next;   // 다음 유무
    private int total;      // 전체데이터 수
    private Criteria cri;   // pageNum(현재페이지번호), amount(게시글 보여주는 갯수)

    // 생성자 오버로드 = 페이지 관련 데이터 공식
    public PageDTO(Criteria cri, int total) {
        this.cri = cri;
        this.total = total;
        // 끝번호(endPage) 계산공식
        this.endPage = (int) (Math.ceil(cri.getPageNum() / 10.0)) * 10;
        // 시작번호(startPage) 계산공식
        this.startPage = this.endPage - 9;
        // 전체데이터수(total)을 이용해 진짜 끝페이지(realEnd)가 몇번까지인지 계산
        this.realEnd = (int) (Math.ceil((total * 1.0) / cri.getAmount()));
        // 진짜 끝페이지(realEnd)가 구해둔 끝번호(endPage)보다 작다면, 끝번호는 작은값이 되어야 함
        if (this.realEnd < this.endPage) {
            this.endPage = this.realEnd;
        }
        // 이전(prev) 계산 = 시작번호(startPage)가 1보다 큰경우라면 존재
        this.prev = this.startPage > 1;
        // 다음(next) 계산 = 다음으로 가는 링크의 경우 realEnd가 끝번호(endPage)보다 큰경우에만 존재
        this.next = this.endPage < this.realEnd;
    }
}
