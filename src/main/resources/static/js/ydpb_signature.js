$(document).ready(function (){
    const d = new Date();
    $('.f_year').val(d.getFullYear());
    $('.f_month').val(d.getMonth() + 1);
    $('.f_day').val(d.getDate());

    // 캔버스 설정
    const canvasIds = ['sig-pad-1', 'sig-pad-2', 'sig-pad-prev', 'sig-pad-curr'];
    const ctxs = {};

    canvasIds.forEach(id => {
        const canvas = document.getElementById(id);
        if(!canvas) return;

        const ctx = canvas.getContext('2d');
        ctxs[id] = ctx;
        let drawing = false;

        function resize() {
            canvas.width = $(canvas).parent().width();
            canvas.height = $(canvas).parent().height();
            ctx.lineWidth = 2;
            ctx.strokeStyle = "#000";
            ctx.lineCap = "round";
        }
        $(window).on('load resize', resize);

        function getPos(e) {
            const rect = canvas.getBoundingClientRect();
            const clientX = e.clientX || (e.originalEvent.touches && e.originalEvent.touches[0].clientX);
            const clientY = e.clientY || (e.originalEvent.touches && e.originalEvent.touches[0].clientY);
            return { x: clientX - rect.left, y: clientY - rect.top };
        }

        $(canvas).on('mousedown touchstart', function(e){
            drawing = true;
            ctx.beginPath();
            const pos = getPos(e);
            ctx.moveTo(pos.x, pos.y);
            if(e.type==='touchstart') e.preventDefault();
        }).on('mousemove touchmove', function(e){
            if(!drawing) return;
            const pos = getPos(e);
            ctx.lineTo(pos.x, pos.y);
            ctx.stroke();
            if(e.type==='touchmove') e.preventDefault();
        }).on('mouseup mouseleave touchend', function(){ drawing = false; });
    });

    // 체크박스
    $('.check-box').on('click', function (){
        const group = $(this).data('group');
        $(`.check-box[data-group="${group}"]`).not(this).removeClass('active');
        $(this).toggleClass('active');
    });

    // 엔터 키 제어
    const rowStartTop = 44.2;
    const rowGap = 3.2;

    $(document).on('keydown', '.form-field', function(e){
        if(e.key === 'Enter'){
            e.preventDefault();
            const $this = $(this);

            if($this.hasClass('row-input')){
                const col = parseInt($this.data('col'));
                const row = parseInt($this.closest('.target-row').data('row'));
                if(col < 5){
                    $this.closest('.target-row').find(`[data-col="${col+1}"]`).focus();
                } else {
                    if (row < 5) {
                        addNewRow(row + 1);
                    }
                }
            }
            else if($this.attr('tabindex')){
                const nextIdx = parseInt($this.attr('tabindex')) + 1;
                const $next = $(`.form-field[tabindex="${nextIdx}"]`);
                if($next.length > 0) $next.focus();
                else $('.f_report_content').focus();
            }
            else if($this.hasClass('f_report_content')) {
                $('.f_prev_master').focus();
            }
            else if($this.hasClass('f_prev_master')) {
                $('.f_curr_master').focus();
            }
            else if($this.hasClass('f_curr_master')) {
                $('.target-row[data-row="1"] .r_rel').focus();
            }
        }
    });

    function addNewRow(num) {
        if(num > 7) return;
        const top = rowStartTop + ((num - 1) * rowGap);
        const html = `
                    <div class="target-row" data-row="${num}">
                        <input type="text" class="form-field row-input r_rel" data-col="1" style="top:${top}%">
                        <input type="text" class="form-field row-input r_name" data-col="2" style="top:${top}%">
                        <input type="text" class="form-field row-input r_jumin" data-col="3" style="top:${top}%">
                        <input type="text" class="form-field row-input r_pre" data-col="4" style="top:${top}%">
                        <input type="text" class="form-field row-input r_post" data-col="5" style="top:${top}%">
                    </div>`;
        $('#dynamicRows').append(html);
        $(`.target-row[data-row="${num}"] .r_rel`).focus();
    }

    // 초기화 버튼
    $('#clearBtn').on('click', function (){
        if(confirm('모든 입력 내용과 서명을 초기화 하시겠습니까?')){
            $('.form-field').val('');
            $('.check-box').removeClass('active');
            canvasIds.forEach(id => {
                const canvas = document.getElementById(id);
                if(canvas) ctxs[id].clearRect(0, 0, canvas.width, canvas.height);
            });
            $('#dynamicRows').html($('.target-row[data-row="1"]'));
        }
    });

    // 주소 입력 부분 포커스 시 주소 입력 API 호출 0130 귀환
    const addrInput = document.querySelector('.f_addr');
    // 포커스 이벤트 리스너 추가
    $('.f_addr').on('focus', function() {
        if (!this.value.trim()) {

            execDaumPostcode('.f_addr');
            console.log('주소찾기 api 실행');
            // 포커스 시 브라우저 기본 파란 테두리가 남지 않도록 포커스 해제 (선택 사항)
            this.blur();
        }
    });


    // 주민등록 신고서 제출 로직 추가 - 최연수
    $('#submitBtn').off('click').on('click', function (e) {
        e.preventDefault();

        // 1. 유효성 검사 (Validation)
        const reporterName = $('.f_name').val().trim();
        if (!reporterName) {
            alert("신고인 성명을 입력해주세요.");
            $('.f_name').focus();
            return false;
        }

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
            delegateName: $('.f_delegate_name').val().trim(),
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

        console.log("전 세대주 서명란 길이 : "+formData.sigPrev.length );
        console.log("세대주 서명란 길이 : "+formData.sigCurr.length );
        console.log("신고인 서명란 길이 : "+formData.sigPrev.length );
        console.log("위임한 세대주 서명란 길이 : "+formData.sigDelegate.length );

        //  유효성 체크 메서드 - 귀환
        function validateForm(data) {
            // 신고인 이름 체크
            if (!data.reporterName || data.reporterName.trim() === "") {
                alert("신고인 성명을 입력해주세요.");
                return false;
            }
            const nameReg = /^(?:[가-힣]{1,16})$/;
            if (!nameReg.test(data.reporterName)) {
                alert("이름 형식이 올바르지 않습니다. \n16자 이내 한글을 입력해주세요");
                $('.f_name').focus();
                return false;
            }


            // 주민등록번호 확인
            if (!data.reporterJumin || data.reporterJumin.trim() === "") {
                alert("주민등록번호를 입력해주세요.");
                $('.f_jumin').focus();
                return false;
            }
            const juminReg = /^[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])-[1-4][0-9]{6}$/;
            if (!juminReg.test(data.reporterJumin)) {
                alert("주민등록번호 형식이 올바르지 않습니다.");
                $('.f_jumin').focus();
                return false;
            }
            //세대주와의 관계
            if (!data.reporterRel || data.reporterRel.trim() === "") {
                alert("세대주와의 관계를 입력해주세요.");
                $('.f_rel').focus();
                return false;
            }
            const relReg = /^([가-힣]{1,10})$/;
            if (!relReg.test(data.reporterRel)) {
                alert("세대주와의 형식이 올바르지 않습니다. \n10자 이내 한글을 입력해주세요");
                $('.f_rel').focus();
                return false;
            }

            //일반전화 유효성
            const telReg = /^(02|03[1-3]|04[1-4]|05[1-5]|06[1-4])-(\d{3,4})-(\d{4})$/;
            if(data.reporterTel){
                if (!telReg.test(data.reporterTel)) {
                    alert("일반전화 형식이 올바르지 않습니다.\n다시 입력해주세요");
                    $('.f_tel').focus();
                    return false;
                }
            }

            //휴대 전화
            if (!data.reporterPhone || data.reporterPhone.trim() === "") {
                alert("휴대전화번호를 입력해주세요.");
                $('.f_phone').focus();
                return false;
            }
            const phoneReg = /^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$/;
            if (!phoneReg.test(data.reporterPhone.trim())) {
                alert("휴대전화 형식이 올바르지 않습니다.\n다시 입력해주세요");
                $('.f_phone').focus();
                return false;
            }

            // 주소 체크
            if (!data.reporterAddr || data.reporterAddr.trim() === "") {
                alert("기본 주소를 입력해주세요.");
                $('.f_addr').focus();
                return false;
            }

            // 신고내용
            if (!data.reportContent || data.reportContent.trim() === "") {
                alert("신고 내용을 입력해주세요.");
                $('.f_report_content').focus();
                return false;
            }

            //세대주명
            if (!data.prevMaster  || data.prevMaster.trim() === "") {
                alert("전 세대주명을 입력해주세요.");
                $('.f_prev_master').focus();
                return false;
            }
            const prevOwnerReg = /^(?:[가-힣]{1,16})$/;
            if (!prevOwnerReg.test(data.prevMaster)) {
                alert("이름 형식이 올바르지 않습니다. \n16자 이내 한글을 입력해주세요");
                $('.f_prev_master').focus();
                return false;
            }

            if (!data.currMaster || data.currMaster.trim() === "") {
                alert("현 세대주 명을 입력해주세요.");
                $('.f_curr_master').focus();
                return false;
            }
            const currOwnerReg = /^(?:[가-힣]{1,16})$/;
            if (!currOwnerReg.test(data.currMaster)) {
                alert("이름 형식이 올바르지 않습니다. \n16자 이내 한글을 입력해주세요");
                $('.f_curr_master').focus();
                return false;
            }

            // 대상자 목록이 비어있는지 확인
            if (!data.targets || data.targets.length === 0) {
                alert("신고 대상자를 최소 한 명 이상 추가해주세요.");
                $('.r_rel').focus();
                return false;
            }

            const rRel =$('.r_rel').val().trim();
            const rName =$('.r_name').val().trim();
            const rJumin =$('.r_jumin').val().trim();
            const rPre =$('.r_pre').val().trim();
            const rPost =$('.r_post').val().trim();
            if (!rRel || rRel === "") {
                alert("세대주와의 관계를 입력해주세요");
                $('.r_rel').focus();
                return false;
            }if (!rName || rName === "") {
                alert("이름을 입력해주세요.");
                $('.r_name').focus();
                return false;
            }if (!juminReg.test(rJumin)) {
                alert("주민등록번호 형식이 올바르지 않습니다.");
                $('.r_jumin').focus();
                return false;
            }if (!rPre || rPre === "") {
                alert("정정 전 내용을 입력해주세요.");
                $('.r_pre').focus();
                return false;
            }if (!rPost || rPost === "") {
                alert("정정 후 내용을 입력해주세요");
                $('.r_post').focus();
                return false;
            }

            if (!data.topType  || data.topType.trim() === "") {
                alert("최상단의 신고 종류를 체크해주세요.");
                const target = document.querySelector('.c_top_1');
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });

                // 시각적 강조
                $('[data-group="top_type"]').css('border', '5px solid red');
                setTimeout(() => $('[data-group="top_type"]').css('border', 'none'), 5000);

                return false;
            }
            if (!data.midType || data.midType.trim() === "") {
                alert("신고서 하단의 신고 종류를 체크해주세요.");

                const target = document.querySelector('.c_mid_1');
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });

                // 시각적 강조
                $('[data-group="mid_type"]').css('border', '5px solid red');
                setTimeout(() => $('[data-group="mid_type"]').css('border', 'none'), 5000);
                return false;
            }
            if (!data.btmType || data.btmType.trim() === "") {
                alert("위임장 아래 신고 종류를 체크해주세요.");

                const target = document.querySelector('.c_btm_1');
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });

                // 시각적 강조
                $('[data-group="btm_type"]').css('border', '5px solid red');
                setTimeout(() => $('[data-group="btm_type"]').css('border', 'none'), 5000);
                return false;
            }
            // if (!data.submitYear || data.submitYear.trim() === "") {
            //     alert("제출 연도를 입력해주세요.");
            //     $('.f_year').focus();
            //     return false;
            // }
            // if (!data.submitMonth || data.submitMonth.trim() === "") {
            //     alert("제출 월을 입력해주세요.");
            //     $('.f_month').focus();
            //     return false;
            // }
            // if (!data.submitDay  || data.submitDay.trim() === "") {
            //     alert("제출 일을 입력해주세요.");
            //     $('.f_day').focus();
            //     return false;
            // }

            //
            const fReporterName =$('.f_reporter_name').val().trim();
            if (!fReporterName || fReporterName === "") {
                alert("신고인을 입력해주세요");
                $('.f_reporter_name').focus();
                return false;
            }
            const fDelegateName =$('.f_delegate_name').val().trim();
            if (!fDelegateName || fDelegateName === "") {
                alert("위임한 사람을 입력해주세요");
                $('.f_delegate_name').focus();
                return false;
            }

            //  서명 유무 체크 : Canvas 데이터가 초기값인지 확인
            // 빈 캔버스의 dataURL 길이는 보통 아주 짧음 대략 2000~3000자 이하

            if (data.sigReporter.length < 1000) {
                alert("신고인 서명이 누락되었습니다.");
                const target = document.querySelector('#sig-pad-1');
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });

                // 시각적 강조
                target.style.outline = "5px solid red";
                setTimeout(() => target.style.outline = "none", 5000);
                return false;
            }
            if (data.sigDelegate.length < 1000) {
                alert("위임한 사람의 서명이 누락되었습니다.");
                const target = document.querySelector('#sig-pad-2');
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });

                // 시각적 강조
                target.style.outline = "5px solid red";
                setTimeout(() => target.style.outline = "none", 5000);
                return false;
            }
            if (data.sigPrev.length < 1000) {
                alert("전 세대주 서명이 누락되었습니다.");
                const target = document.querySelector('#sig-pad-prev');
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });

                // 시각적 강조
                target.style.outline = "5px solid red";
                setTimeout(() => target.style.outline = "none", 5000);
                return false;
            }
            if (data.sigCurr.length < 1000) {
                alert("세대주 서명이 누락되었습니다.");
                const target = document.querySelector('#sig-pad-curr');
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });

                // 시각적 강조
                target.style.outline = "5px solid red";
                setTimeout(() => target.style.outline = "none", 5000);
                return false;
            }

            return true; // 모든 통과 시 true 반환
        }

        // 유효성 처리
        if (!validateForm(formData)) {
            return; // 유효성 검사 실패 시 여기서 중단 (AJAX 실행 안 함)
        }


        // 3. 서버 전송
        if (confirm('작성하신 내용으로 신고서를 제출하시겠습니까?')) {
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
                    const link = document.createElement('a');
                    link.href = url;
                    // 파일명을 현재 시간과 조합하여 고유하게 설정
                    const fileName = '주민등록신고서_' + new Date().toISOString().slice(0,10).replace(/-/g, '') + '.pdf';
                    link.download = fileName;
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);

                    // 4. 완료 메시지 및 페이지 이동
                    alert('신고서가 성공적으로 생성되었습니다.');

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
});