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


    $('.slider_list').slick({
        infinite: true,
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 3000,
        dots: false,
        arrows: false
    });

    var $slider = $('.slider_list');
    var $toggleBtn = $('.btn-toggle');

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