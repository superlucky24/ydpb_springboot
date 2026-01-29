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
    bindFileNameDisplay("slide_file");
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

    $(document).ready(function() {
        // 사이드메뉴 클릭 이벤트 : 20251218 윤성민 추가 / 최연수 20260120 민원안내 추가 / 2차 수정 : 최연수
        $('.side_list_menu > div').on('click', function () {
            const $thisMenu = $(this).parent('.side_list_menu');
            const $thisSubList = $(this).next('.sub_list');

            $thisMenu.siblings().removeClass('open').find('.sub_list').removeClass('show');
            $thisMenu.siblings().find('.sub_group, .sub_complaint').removeClass('show');

            $thisMenu.toggleClass('open');
            $thisSubList.toggleClass('show');

            if ($thisSubList.hasClass('show')) {
                $thisSubList.find('> ul > li, > .sub_group, > .sub_complaint, > li').addClass('show');
            }
        });

        // 2뎁스/3뎁스 타이틀 클릭 이벤트 (분야별민원 3뎁스 대응) : 20260129 추가
        $(document).on('click', '.sub_group_title, .sub_complaint_title', function (e) {
            const $groupList = $(this).next('.sub_group_list');
            if ($groupList.length > 0) {
                // 하위 메뉴가 있는 경우만 제어
                if($(this).find('a').length === 0 || $(this).find('a').attr('href') === '#') {
                    e.preventDefault();
                }
                $('.sub_group_title').not(this).removeClass('open');
                $('.sub_complaint_title').not(this).removeClass('open');
                $('.sub_group_list').not($groupList).removeClass('show');

                $(this).toggleClass('open');
                $groupList.toggleClass('show');
            }
        });

        // 로케이션 공유 버튼 클릭 이벤트 : 20251219 윤성민 추가
        $('.loc_sns').on('click', function () {
            $(this).toggleClass('checked');
            $('.sns_list').toggleClass('show');
        });

        // menuName 변수가 있을 경우 해당 값에 해당하는 사이드메뉴 열기 : 20251218 최상림 추가
        // 민원안내 이벤트 추가 및 로직 수정: 20260120 최연수 / 20260129 3뎁스 역추적 보정
        $(window).on('load', function () {
            if (typeof menuName != 'undefined' && menuName.trim() != '') {
                const locationPathText = $('.location_path').text().trim();

                const $allPotentialTargets = $('.side_list_menu a, .sub_group_title, .sub_complaint_title').filter(function () {
                    return $(this).text().trim() === menuName;
                });

                let $target = null;
                $allPotentialTargets.each(function() {
                    const containerTitle = $(this).closest('.side_list_menu').find('> div span').text().trim();
                    if (locationPathText.includes(containerTitle)) {
                        $target = $(this);
                        return false;
                    }
                });

                if (!$target && $allPotentialTargets.length > 0) $target = $allPotentialTargets.first();

                if ($target && $target.length > 0) {
                    $('.side_list_menu').removeClass('open');
                    $('.sub_list, .sub_group, .sub_complaint, .sub_group_list').removeClass('show');
                    $('.sub_group_title, .sub_complaint_title').removeClass('open side_active');
                    $('.side_list_menu li').removeClass('side_active');

                    if ($target.is('a')) {
                        $target.closest('li').addClass('side_active');
                        if($target.parent().hasClass('sub_complaint_title')) {
                            $target.parent().addClass('side_active open');
                        }
                    } else {
                        $target.addClass('side_active open');
                    }

                    const $mySubList = $target.closest('.sub_list');
                    const $mySideMenu = $target.closest('.side_list_menu');

                    $mySideMenu.addClass('open');
                    $mySubList.addClass('show');
                    $mySubList.find('> ul > li, > .sub_group, > .sub_complaint, > li').addClass('show');

                    const $myGroupList = $target.closest('.sub_group_list');
                    if ($myGroupList.length > 0) {
                        $myGroupList.addClass('show');
                        $myGroupList.prev().addClass('open side_active');
                    }
                }
            }
        });
    });

    // #weather_wrap 영역 있을 경우에, 날씨 api 실행
    if($('#weather_wrap').length > 0) {
        // 날씨 api 연동 : 20260122 최상림
        $.getJSON('/weather/status', function(data) {
            // 결과 데이터가 30분 단위로 다수의 데이터를 포함하고 있으므로, 필요한 정보의 최신값만 Map 객체에 담아 사용
            const dataArray = data.response.body.items.item;
            const map = new Map();
            dataArray.forEach(item => {
                if(!map.has(item.category) && /T1H|SKY|PTY|LGT/.test(item.category)) {
                    map.set(item.category, item);
                }
            });
            const firstByCategory = Array.from(map.values()).map(item => ({category: item.category, value: item.fcstValue}));
            const lgt = firstByCategory.filter(item => item.category === 'LGT')[0].value;   // 낙뢰 정보
            const pty = firstByCategory.filter(item => item.category === 'PTY')[0].value;   // 비, 눈 정보
            const sky = firstByCategory.filter(item => item.category === 'SKY')[0].value;   // 구름 상태
            const t1h = firstByCategory.filter(item => item.category === 'T1H')[0].value;   // 온도

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
            const dustInfo = dataArray[0];
            const pm10Grade = Number(dustInfo.pm10Grade);   // 먼지 등급 1~4
            const pm25Grade = Number(dustInfo.pm25Grade);   // 미세먼지 등급 1~4
            const gradeText = ['좋음', '보통', '나쁨', '매우나쁨'];

            $('#air_dust1').addClass('dust_0' + pm10Grade).text(gradeText[pm10Grade - 1]);
            $('#air_dust2').addClass('dust_0' + pm25Grade).text(gradeText[pm25Grade - 1]);
            $('#weather_wrap .dust_status').addClass('active');
        })
        .fail(function(xhr, status, err) {
            console.log(err);
        });
    }
// 주민등록 신고서 제출 로직 추가 - 최연수
    $('#submitBtn').off('click').on('click', function (e) {
        e.preventDefault();

        // 1. 유효성 검사 (Validation)
        const reporterName = $('.f_name').val().trim();
        // if (!reporterName) {
        //     alert("신고인 성명을 입력해주세요.");
        //     $('.f_name').focus();
        //     return false;
        // }

        // 2. 데이터 수집 (HTML의 모든 필드 누락 없이)
        const targetList = [];
        $('.target-row').each(function() {
            const row = {
                rel: $(this).find('.r_rel').val().trim(),
                name: $(this).find('.r_name').val().trim(),
                jumin: $(this).find('.r_jumin').val().trim(),
                pre: $(this).find('.r_pre').val().trim(),
                post: $(this).find('.r_post').val().trim()
            };
            if(row.name) targetList.push(row);
        });

        const formData = {
            reporterName: reporterName,
            reporterJumin: $('.f_jumin').val().trim(),
            reporterRel: $('.f_rel').val().trim(),
            reporterTel: $('.f_tel').val().trim(),
            reporterPhone: $('.f_phone').val().trim(),
            reporterAddr: $('.f_addr').val().trim(),
            reportContent: $('.f_report_content').val().trim(),
            prevMaster: $('.f_prev_master').val().trim(),
            currMaster: $('.f_curr_master').val().trim(),
            targets: targetList,
            topType: $('.c_top_1.active, .c_top_2.active, .c_top_3.active').attr('class') || "",
            midType: $('.c_mid_1.active, .c_mid_2.active, .c_mid_3.active').attr('class') || "",
            btmType: $('.c_btm_1.active, .c_btm_2.active, .c_btm_3.active').attr('class') || "",
            submitYear: $('.f_year').first().val(),
            submitMonth: $('.f_month').first().val(),
            submitDay: $('.f_day').first().val(),
            sigReporter: document.getElementById('sig-pad-1').toDataURL('image/png'),
            sigDelegate: document.getElementById('sig-pad-2').toDataURL('image/png'),
            sigPrev: document.getElementById('sig-pad-prev').toDataURL('image/png'),
            sigCurr: document.getElementById('sig-pad-curr').toDataURL('image/png')
        };

        // 3. 서버 전송
        if (true) {
            $.ajax({
                url: '/sub/submit',
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(formData),
                xhrFields: {
                    responseType: 'blob' // [추가] PDF 바이너리 데이터를 받기 위함
                },
                success: function(blob) {
                    // [추가] PDF 새 창 열기 및 다운로드 로직
                    // 1. Blob 객체를 사용하여 임시 URL 생성
                    const url = window.URL.createObjectURL(blob);

                    // 2. 새 창으로 PDF 미리보기 열기
                    const newWindow = window.open(url, '_blank');
                    if(!newWindow || newWindow.closed || typeof newWindow.closed=='undefined') {
                        alert('팝업 차단이 설정되어 있습니다. 팝업을 허용해주세요.');
                    }

                    // 3. 자동 다운로드 실행
                    // const link = document.createElement('a');
                    // link.href = url;
                    // // 파일명을 현재 시간과 조합하여 고유하게 설정
                    // const fileName = '주민등록신고서_' + new Date().toISOString().slice(0,10).replace(/-/g, '') + '.pdf';
                    // link.download = fileName;
                    // document.body.appendChild(link);
                    // link.click();
                    // document.body.removeChild(link);

                    // 4. 완료 메시지 및 페이지 이동
                    // alert('신고서가 성공적으로 생성되었습니다.');

                    // 임시 URL 해제 (메모리 관리)
                    setTimeout(function() {
                        window.URL.revokeObjectURL(url);
                    }, 100);

                    // location.href = '/';
                },
                error: function(xhr, status, error) {
                    console.error("제출 에러 상세:", error);
                    alert('제출 중 오류가 발생했습니다. 서버 로그를 확인해주세요.');
                }
            });
        }
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