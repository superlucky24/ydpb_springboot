/*
    rootPath = admin_index.html > head 영역에 선언한 변수
        스프링부트 프로젝트 루트 경로를 받아오기 위해 html 영역에 타임리프 사용해서 값 할당
        루트 경로가 항상 '/' 일 경우 해당 변수 삭제하고, 이하 함수 내부의 rootPath 변수 제거, url 값 앞에 '/' 붙여서 사용해도 무관
 */
$(document).ready(function() {
    // 페이지 시작 checked 되어 있는 radio 의 값으로 게시글 수 가져오기
    const $categoryType = $('#categoryType');   // 게시글 수 카테고리 표시할 제목 영역
    const startCategory = $('input[name="countRecent"]:checked').val();
    setPeriodCount(startCategory);
    $categoryType.text('('+ startCategory +')');

    // 게시글 수 현황 라디오 값 변경 시 이벤트
    $('input[name="countRecent"]').on('change', function() {
        setPeriodCount(this.value);
        $categoryType.text('(' + this.value + ')');
    });

    // 게시판별 최신글 불러오기
    adminRecent('Member');
    adminRecent('DongNews');
    adminRecent('GuNews');
    adminRecent('Gallery');
    adminRecent('Community');
    adminRecent('Complaint');
});

/**
 * 게시판 일정기간 등록된 게시글 수 가져오기
 * @param startDate : {String} 조회 시작일 (포함)
 * @param endDate : {String} 조회 종료일 (미포함, 종료일 전날까지)
 */
function adminPeriodCount(startDate, endDate) {
    const url = rootPath + 'admin/statistics/period/' + startDate + '/' + endDate;
    const $list = $('#countList');
    $list.removeClass('complete').addClass('loading');
    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            $list.addClass('complete');
            $('#countMember').text(data.member);
            $('#countComplaint').text(data.complaint);
            $('#countDongNews').text(data.dongNews);
            $('#countGuNews').text(data.guNews);
            $('#countGallery').text(data.gallery);
            $('#countCommunity').text(data.community);
        },
        error: function(xhr, status, err) {
            $list.removeClass('complete').addClass('loading');
            console.log(err);
        }
    });
}

/**
 * 게시글 수 현황 체크박스 값에 맞는 기간 지정하고, adminPeriodCount 함수 호출
 * @param type : {String} 기간 검색 구분자
 *               일 = 오늘, 월 = 이번달, 년 = 올해
 */
function setPeriodCount(type) {
    let startDate;
    let endDate;
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth();
    switch(type) {
        case '월':
            startDate = formatDate(new Date(year, month));
            endDate = formatDate(new Date(year, month + 1));
            break;
        case '년':
            startDate = formatDate(new Date(year, 0));
            endDate = formatDate(new Date(year + 1, 0));
            break;
        default:
            const nextDay = new Date(year, month, today.getDate() + 1);
            startDate = formatDate(today);
            endDate = formatDate(nextDay);
            break;
    }
    adminPeriodCount(startDate, endDate);
}

/**
 * 관리자 메인화면 최신글 불러오기
 * @param type : {String} 최신글 종류
 *                  Member = 회원정보, DongNews = 우리동소식, GuNews = 구청소식, Gallery = 포토갤러리, Community = 자치회관, Complaint = 민원신청
 */
function adminRecent(type) {
    const url = rootPath + 'admin/' + type.toLowerCase() + '/recent';
    $.getJSON(url, function(data) {
        let title = '';
        let loginType = '';
        let regDate = '';
        let link = '';
        let temp = '';

        for(let i = 0; i < data.length; i++) {
            const dataRow = data[i];

            // 인자로 받은 type 값으로 데이터 분기
            switch(type) {
                case 'Member':
                    title = dataRow.memId + ' / ' + dataRow.memName;
                    loginType = dataRow.loginType ? dataRow.loginType : '일반';
                    regDate = formatDate(dataRow.memRegDate);
                    link = rootPath + 'admin/member/view?memId=' + dataRow.memId;
                    break;
                case 'DongNews':
                    title = dataRow.dnewsTitle;
                    regDate = formatDate(dataRow.dnewsDate);
                    link = rootPath + 'admin/dongnews/view?dnewsId=' + dataRow.dnewsId;
                    break;
                case 'GuNews':
                    title = dataRow.gnewsTitle;
                    regDate = formatDate(dataRow.gnewsDate);
                    link = rootPath + 'admin/gunews/view?gnewsId=' + dataRow.gnewsId;
                    break;
                case 'Gallery':
                    title = dataRow.galTitle;
                    regDate = formatDate(dataRow.galDate);
                    link = rootPath + 'admin/gallery/view?galId=' + dataRow.galId;
                    break;
                case 'Community':
                    title = dataRow.cmntTitle;
                    regDate = formatDate(dataRow.cmntDate);
                    link = rootPath + 'admin/community/view?cmntId=' + dataRow.cmntId;
                    break;
                case 'Complaint':
                    title = dataRow.comTitle;
                    regDate = formatDate(dataRow.comDate);
                    link = rootPath + 'admin/complaint/view?comId=' + dataRow.comId;
                    break;
                default:
                    break;
            }

            temp += '<li>';
            temp += '    <a href="'+ link +'">';
            temp += '        <span class="recent_title">'+ title +'</span>';
            if(type === 'Member') {
                temp += '        <span class="recent_type">'+ loginType +'</span>';
            }
            temp += '        <time datetime="'+ regDate +'">'+ regDate +'</time>';
            temp += '    </a>';
            temp += '</li>';
        }
        $('#recent' + type).html(temp);
    }).fail(function(xhr, status, err) {
        console.log(err);
    });
}

/**
 * 날짜 포맷 변경 메소드
 * @param regDate : {Date} Date 타입 날짜
 * @returns {string} : yyyy-MM-dd 형식으로 반환
 */
function formatDate(regDate) {
    const date = new Date(regDate);
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
}