$(document).ready(function(){

    $('.main3_dong').on('click', function() {
        $('.main3_title>li').removeClass('checked');
        $(this).addClass('checked');
        $('.main3_contents').removeClass('show');
        $('.m3dong').addClass('show');
    });
    $('.main3_gu').on('click', function() {
        $('.main3_title>li').removeClass('checked');
        $(this).addClass('checked');
        $('.main3_contents').removeClass('show');
        $('.m3gu').addClass('show');
    });

    $('.loc_sns').on('click', function() {
        $(this).toggleClass('checked');
        $('.sns_list').toggleClass('show');
    });

});

/**
 * 메인화면 최신글 연동을 위한 함수 : 20260127 최상림
 * @param url : (String) 루트 디렉토리 url
 */
function mainRecent(url) {
    // 우리동소식 최신글 불러오기
    $.getJSON(url + 'dongnews/recent', function(data) {
        let temp = '';
        data.forEach(item => {
            temp += `<li>
                        <a href="${url}dongnews/view?dnewsId=${item.dnewsId}" class="underline_text">
                            <span class="main3_c_title">${item.dnewsTitle}</span>
                            <div class="main3_c_date">${item.dnewsDate.slice(0, 10)}</div>
                        </a>
                    </li>`;
        });
        const listWrap = $('#mainDongNews');
        listWrap.html(temp);
        listWrap.addClass('complete');
        setTimeout(function() {
            listWrap.removeClass('loading');
        }, 300);
    })
    .fail(function (xhr, status, err) {
        console.log(err);
    });
}