$(document).ready(function(){
    // 메인 동/구 클릭시 관련 리스트 나오게 하는 코드
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
    // sns 클릭시 관련 아이콘 나오게 하는 코드
    $('.loc_sns').on('click', function() {
        $(this).toggleClass('checked');
        $('.sns_list').toggleClass('show');
    });

    // 슬라이더 기능 추가
    var $slider = $('.slider_list');
    var $toggleBtn = $('.btn-toggle');
    var $current = $('.slide-counter .current');
    var $total = $('.slide-counter .total');

    $slider.on('init', function(event, slick){
        $total.text(slick.slideCount);
        $current.text(slick.currentSlide + 1);
    });

    $('.slider_list').slick({
        infinite: true,
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 3000,
        dots: false,
        arrows: false
    });

    $slider.on('afterChange', function(event, slick, currentSlide){
        $current.text(currentSlide + 1);
    });

    // 버튼 이벤트
    $('.btn-prev').on('click', function(){
        $slider.slick('slickPrev');
    });

    $('.btn-next').on('click', function(){
        $slider.slick('slickNext');
    });

    // ▶ / ❚❚ 토글 버튼
    $toggleBtn.on('click', function(){
        var state = $(this).attr('data-state');

        if (state === 'play') {
            $slider.slick('slickPause');
            $(this)
                .attr('data-state', 'pause')
                .text('▶');
        } else {
            $slider.slick('slickPlay');
            $(this)
                .attr('data-state', 'play')
                .text('❚❚');
        }
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

    // 구청소식 최신글 불러오기
    $.getJSON(url + 'gunews/recent', function(data) {
        let temp = '';
        data.forEach(item => {
            temp += `<li>
                        <a href="${url}gunews/view?gnewsId=${item.gnewsId}" class="underline_text">
                            <span class="main3_c_title">${item.gnewsTitle}</span>
                            <div class="main3_c_date">${item.gnewsDate.slice(0, 10)}</div>
                        </a>
                    </li>`;
        });
        const listWrap = $('#mainGuNews');
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