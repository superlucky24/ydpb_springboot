$(document).ready(function() {

    initUi();

});

/**
 * ui 스크립트 초기화 : 20251218 최상림 추가
 * !!!공통영역 load 호출 이후 스크립트 추가를 위해 함수화 한 것이므로 백엔드 개발 시 수정할 수 있음을 인지할 것!!!
 */
function initUi() {
    // 링크 # 처리한 a 태그 클릭 이벤트 시 경고창 출력 : 20251216 최상림 추가
    $('a[href="#"]').click(function(e) {
        e.preventDefault();
        layerAlert('죄송합니다.<br> 현재 준비중인 메뉴입니다.');
    });

    // 액션 # 처리한 form 태그 submit 이벤트 시 경고창 출력 : 20251216 최상림 추가
    $('form[action="#"]').submit(function(e) {
        e.preventDefault();
        layerAlert('죄송합니다.<br> 현재 준비중인 메뉴입니다.');
    });

    // **파일명 표시 로직 추가** 260121 윤성민 추가
    bindFileNameDisplay("file_1");
    bindFileNameDisplay("file_2");
    $('form').on('submit', function(e) {
        const fileInputs = $('input[type="file"]');

        // 파일이 하나라도 있으면 multipart 설정
        const hasFile = fileInputs.toArray().some(input => input.value !== "");

        // 파일이 있으면 enctype 설정, 없으면 enctype 제거
        if (hasFile) {
            $(this).attr('enctype', 'multipart/form-data');
        } else {
            $(this).removeAttr('enctype');
        }

        // 빈 파일 input은 name 속성 제거
        fileInputs.each(function() {
            if (!$(this).val()) {
                $(this).removeAttr('name');
            }
        });
    });








    // 헤더,푸터 251218 박귀환 추가
    // Header 
    // .global_menu .top_func 클릭 스크립트
    $('.top_func > li .active_box button').on('click',function () {

        //active 클래스가 있으면
        if($(this).hasClass('active')){
            $(this).removeClass('active');
            $(this).next().slideUp();
        }
        else{//active 클래스가 없으면
            // 모든 active 클래스 제거 및 리스트 숨김
            $('.top_func > li .active_box button').removeClass('active');
            $('.top_func > li .active_box button').next().slideUp();
            // 선택한 요소만 클래스 추가 후 리스트 보여줌
            $(this).addClass('active');
            $(this).next().slideDown();
        }
    })
    // gnb 메뉴 스크립트
    $('.top_menu > ul > li > a').on('mouseenter', function () {
        $('.top_menu > ul > li').removeClass('active');
        $(this).parent().addClass('active');
        $('.black_opacity').show();

        // 관리자 헤더 숨기기 20260122 윤성민 추가
        $('#adminHeader').hide();
    });
    $('.top_menu > ul > li').on('mouseleave', function () {
        $('.top_menu > ul > li').removeClass('active');
        $('.black_opacity').hide();

        // 관리자 헤더 다시 보이기 20260122 윤성민 추가
        $('#adminHeader').show();
    });
    
    // Footer
    $('.site_list .active_box button').on('click',function () {
        //active 클래스가 있으면
        if($(this).hasClass('active')){
            $(this).removeClass('active');
            $(this).next().slideUp();
        }
        else{//active 클래스가 없으면
            // 모든 active 클래스 제거 및 리스트 숨김
            $('.site_list .active_box button').removeClass('active');
            $('.site_list .active_box button').next().slideUp();
            // 선택한 요소만 클래스 추가 후 리스트 보여줌
            $(this).addClass('active');
            $(this).next().slideDown();
        }
    });

    // 사이드메뉴 클릭 이벤트 : 20251218 윤성민 추가
    $('.side_list_menu>div').on('click', function(){        
        $(this).parent('.side_list_menu').siblings().removeClass('open');
        $(this).parent().siblings().find('.sub_list').removeClass('show');
        $(this).parent().siblings().find('.sub_group').removeClass('show');
        $(this).next('.sub_list').toggleClass('show');
        $(this).next().find('.sub_group').toggleClass('show');
        $(this).parent().toggleClass('open');
    });

    $('.sub_group_title').on('click', function(){
        $('.sub_group_title').not(this).removeClass('open');
        $('.sub_group_list').not($(this).next('.sub_group_list')).removeClass('show');
        $(this).toggleClass('open');
        $(this).next('.sub_group_list').toggleClass('show');
    });

    // 로케이션 공유 버튼 클릭 이벤트 : 20251219 윤성민 추가
    $('.loc_sns').on('click', function() {
        $(this).toggleClass('checked');
        $('.sns_list').toggleClass('show');
    });

    // menuName 변수가 있을 경우 해당 값에 해당하는 사이드메뉴 열기 : 20251218 최상림 추가
    // 사이드메뉴를 jQuery load 메소드로 추가하고, 해당 페이지 메뉴 항목을 열기 위한 코드
    if(typeof menuName != 'undefined' && menuName.trim() !== '') {
        console.log('현재 메뉴명 => ' + menuName);
        const subList = $('.side_list .side_list_menu').eq(0).children('.sub_list');
        const subGroupListItems = subList.find('.sub_group_list > li');
        let thisItem;
        for(let i = 0; i < subGroupListItems.length; i++) {
            if(menuName === subGroupListItems.eq(i).find('a').text().trim()) {
                thisItem = subGroupListItems.eq(i);
                break;
            }
        }
        if(thisItem.length > 0) {
            subGroupListItems.removeClass('side_active');
            subList.find('.sub_group_list.show').removeClass('show');
            subList.find('.sub_group_title.open').removeClass('open');
            thisItem.addClass('side_active');
            thisItem.closest('.sub_group_list').addClass('show');
            thisItem.closest('.sub_group_list').siblings('.sub_group_title').addClass('open');
        }
    }

    // 날씨 api 연동 : 20260122 최상림
    $.getJSON('/weather/status', function(data) {
        const dataArray = data.response.body.items.item;
        const map = new Map();
        dataArray.forEach(item => {
            if(!map.has(item.category) && /T1H|SKY|PTY|LGT/.test(item.category)) {
                map.set(item.category, item);
            }
        });
        const firstByCategory = Array.from(map.values()).map(item => ({category: item.category, value: item.fcstValue}));
        const lgt = firstByCategory.filter(item => item.category === 'LGT')[0].value;
        const pty = firstByCategory.filter(item => item.category === 'PTY')[0].value;
        const sky = firstByCategory.filter(item => item.category === 'SKY')[0].value;
        const t1h = firstByCategory.filter(item => item.category === 'T1H')[0].value;

        let weatherText = '맑음';
        let weatherImg = 'weather_01.png';

        // 낙뢰 있을 시
        if(lgt > 0) {
            weatherText = '낙뢰주의';
            weatherImg = 'weather_08.png';
        }
        else {
            // 비 또는 눈 있을 때
            if(pty%4 > 0) {
                if(pty%4 === 3) {
                    weatherText = '눈';
                    weatherImg = 'weather_07.png';
                }
                else if(pty%4 === 2) {
                    weatherText = '비/눈';
                    weatherImg = 'weather_06.png';
                }
                else if(pty%4 === 1) {
                    weatherText = '비';
                    weatherImg = 'weather_05.png';
                }
            }
            else {
                // 구름 상태
                if(sky === 4) {
                    weatherText = '흐림';
                    weatherImg = 'weather_04.png';
                }
                else if(sky === 3) {
                    weatherText = '구름많음';
                    weatherImg = 'weather_03.png';
                }
            }
        }

        const weatherImgEl = $('#weather_temperature_img');
        weatherImgEl.attr({'src': weatherImgEl.attr('data-path') + weatherImg, 'alt': weatherText});
        $('#weather_temperature_num').text(t1h + '˚C');
        $('#weather_temperature_text').text(weatherText);
        $('#weather_wrap .icon, #weather_wrap .weather_status').addClass('active');

    })
    .fail(function(xhr, status, err) {
        console.log(err);
    });

    // 미세먼지 api 연동 : 20260123 최상림
    $.getJSON('/weather/dust', function(data) {
        const dataArray = data.response.body.items;
        const dustInfo = dataArray[dataArray.length - 1];
        const pm10Grade = Number(dustInfo.pm10Grade);
        const pm25Grade = Number(dustInfo.pm25Grade);
        const gradeText = ['좋음', '보통', '나쁨', '매우나쁨'];
        $('#air_dust1').removeClass('dust_02').addClass('dust_0' + pm10Grade).text(gradeText[pm10Grade - 1]);
        $('#air_dust2').removeClass('dust_02').addClass('dust_0' + pm25Grade).text(gradeText[pm25Grade - 1]);
        $('#weather_wrap .dust_status').addClass('active');
    })
    .fail(function(xhr, status, err) {
        console.log(err);
    });

}

/**
 * 레이어 경고창 생성 함수 : 20251216 최상림 추가
 * @param {String} text : 경고창 텍스트
 */
function layerAlert(text) {
    const fadeInTime = 600;
    const fadeOutTime = 600;
    const delayTime = 1000;

    // 현재 경고창이 없을 때만 동작
    if($('.layer_alert').length === 0) {
        let html = '<div class="layer_alert">' + text + '</div>';
        $('body').append(html);
        $('.layer_alert').stop().fadeIn(fadeInTime);
        setTimeout(function() {
            $('.layer_alert').stop().fadeOut(fadeOutTime, function() {
                $(this).remove();
            });
        }, delayTime);
    }
}

/* 260121 윤성민 추가 파일 업로드 관련 */
function bindFileNameDisplay(fileInputId) {
    const fileInput = $("#" + fileInputId);

    fileInput.on("change", function () {
        const fileName = this.files && this.files.length ? this.files[0].name : "";
        // input disabled span에 파일명 표시
        $(this).siblings("span.input").text(fileName);
    });

    // 삭제 버튼 기능
    fileInput.closest(".file_group").find(".clear_file").on("click", function () {
        fileInput.val("");
        fileInput.siblings("span.input").text("");
    });
}