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

    $(document).ready(function() {
        // 사이드메뉴 클릭 이벤트 : 20251218 윤성민 추가 / 최연수 20260120 민원안내 추가
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

        // 2뎁스/3뎁스 그룹 클릭 이벤트 (3뎁스 대응 로직 추가)
        $(document).on('click', '.sub_group_title, .sub_complaint_title', function (e) {
            const $groupList = $(this).next('.sub_group_list');

            // 하위 메뉴(3뎁스)가 있는 경우에만 동작
            if ($groupList.length > 0) {
                // a 태그가 없거나 href가 #인 경우 링크 이동 방지
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
        // 민원안내 이벤트 추가 및 로직 수정: 20260120 최연수
        $(window).on('load', function () {
            if (typeof menuName != 'undefined' && menuName.trim() != '') {

                const locationPathText = $('.location_path').text().trim();

                // 1. 이름이 일치하는 메뉴 검색
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
                    // 초기화
                    $('.side_list_menu').removeClass('open');
                    $('.sub_list, .sub_group, .sub_complaint, .sub_group_list').removeClass('show');
                    $('.sub_group_title, .sub_complaint_title').removeClass('open side_active');
                    $('.side_list_menu li').removeClass('side_active');

                    // 2. 강조 처리 (a 태그면 부모 li 또는 p 강조)
                    if ($target.is('a')) {
                        $target.closest('li').addClass('side_active');
                        // 민원안내(sub_complaint_title) 직속 a 태그인 경우 처리
                        if($target.parent().hasClass('sub_complaint_title')) {
                            $target.parent().addClass('side_active open');
                        }
                    } else {
                        $target.addClass('side_active open');
                    }

                    // 3. 부모 계층 역추적 오픈
                    const $mySubList = $target.closest('.sub_list');
                    const $mySideMenu = $target.closest('.side_list_menu');

                    $mySideMenu.addClass('open');
                    $mySubList.addClass('show');

                    // 4.새로 추가한 민원안내 구조(sub_complaint)와 영등포본동 구조 모두 강제 노출
                    $mySubList.find('> ul > li, > .sub_group, > .sub_complaint, > li').addClass('show');

                    // 5. 3뎁스 리스트 처리 (분야별민원 등 그룹 하위 메뉴일 경우)
                    const $myGroupList = $target.closest('.sub_group_list');
                    if ($myGroupList.length > 0) {
                        $myGroupList.addClass('show');
                        $myGroupList.prev().addClass('open side_active');
                    }

                    if ($target.hasClass('sub_group_title') || $target.hasClass('sub_complaint_title')) {
                        $target.next('.sub_group_list').addClass('show');
                    }
                }
            }
        });
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
    if($('.layer_alert').length == 0) {
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