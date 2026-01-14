package kr.go.ydpb.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// pageNum 과 amount 값을 전달하는 용도의 클래스
@Data
@Getter
@Setter
@ToString
public class Criteria {
    private int pageNum;
    private int amount;
    // 검색 기능을 위해 추가
    private String searchType;
    private String searchKeyword;

    // 생성자를 통해서 기본값 : 한 페이지당 10개로 지정해서 처리
    public Criteria() {
        this(1,10); // 생성자 오버로딩 필요
    }
    //생성자 오버로딩
    public Criteria(int pageNum, int amount) {
        this.pageNum = pageNum;
        this.amount = amount;
    }
    /*
검색조건 처리 메서드 : 검색조건 T,W,C로 구성
검색조건을 배열로 만들어서 한번에 처리할 것임
=> 이 메서드를 이용해 MyBatis 동적태그 활용 가능
     */
    public String[] getTypeArr() {
        //삼항조건 연산자 사용
        // 입력조건 없이 null이면 배열 생성
        // 있으면 문자열 한 글자씩 분할하여 배열로 리턴
        return searchType==null?new String[] {}:searchType.split("");
    }
}
