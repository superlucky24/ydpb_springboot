package kr.go.ydpb.domain;

import lombok.Data;

@Data
public class PageDTO {
    private int startPage; // 시작번호
    private int endPage; // 끝번호
    private boolean prev,next; // 이전, 다음
    private int total; // 전체 게시글 갯수
    private Criteria cri; // pageNum(현재 페이지 번호), amount(보여줄 게시글 갯수)
    private int realEnd;

    //생성자 오버로드 : 페이징 관련 데이터 공식 추가
    public PageDTO(Criteria cri, int total) {
        this.cri = cri;
        this.total = total;
        // 끝번호(endPage) 계산공식
        this.endPage = (int) (Math.ceil(cri.getPageNum() / 10.0)) * 10;
        // 시작번호(startPage) 계산공식
        this.startPage = this.endPage - 9;

        // 전체데이터수(total)을 통한 끝번호(endPage) 재계산공식
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

        // 총 갯수가 0이면 모든 값을 1로 초기화
        if (total == 0) {
            this.startPage = 1;
            this.endPage = 1;
            this.realEnd = 1;
            return;
        }
    }

}