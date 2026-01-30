/**
 * 주소찾기 팝업, 데이터 입력 함수 (다음 카카오)
 */
//  공통으로 사용될 수 있도록 구조 수정 0130 귀환
function execDaumPostcode(target) {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 도로명 주소의 노출 규칙에 따라 주소를 표시한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var roadAddr = data.roadAddress; // 도로명 주소 변수
            var extraRoadAddr = ''; // 참고 항목 변수

            // 법정동명이 있을 경우 추가한다. (법정리는 제외)
            // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
            if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                extraRoadAddr += data.bname;
            }
            // 건물명이 있고, 공동주택일 경우 추가한다.
            if(data.buildingName !== '' && data.apartment === 'Y'){
                extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
            }
            // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
            if(extraRoadAddr !== ''){
                extraRoadAddr = ' (' + extraRoadAddr + ')';
            }

            // 참고항목 문자열이 있을 경우, 해당 최종 문자열을 주소 정보에 추가한다.
            if(extraRoadAddr !== '') {
                roadAddr  = roadAddr + ' ' + extraRoadAddr;
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            // document.getElementById("memAddress").value = roadAddr;

            // 0120 이벤트 연결용 추가 귀환
            // document.getElementById("memAddress").dispatchEvent(new Event("input"));

            const targetElement = document.querySelector(target);
            if (targetElement) {
                targetElement.value = roadAddr;
                // 수동 입력 이벤트 발생 (유효성 체크 연동용)
                targetElement.dispatchEvent(new Event("input"));

                // 상세주소로 자동 이동 (규칙: 주소ID + Detail)
                const detailField = document.getElementById(target + "Detail");
                if (detailField) detailField.focus();
            }

        }
    }).open();
}