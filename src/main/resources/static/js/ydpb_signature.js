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
            // [위임장 서명패드 제어] 위임인 성명이 없을 경우 서명 차단
            if(id === 'sig-pad-2' && $('.f_delegate_name').val().trim() === ""){
                alert("위임한 사람의 성명을 먼저 입력해주세요.");
                $('.f_delegate_name').focus();
                return;
            }
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

    // [추가] 위임인 성명 입력 여부에 따른 서명란 시각적 비활성화 제어
    // [추가] 위임인 성명 입력 시 위임장 날짜 자동 기입 로직 추가
    $('.f_delegate_name').on('input', function() {
        const isNotEmpty = $(this).val().trim() !== "";
        const $sigPad2 = $('#sig-pad-2');
        const $dYear = $('.f_year').last();
        const $dMonth = $('.f_month').last();
        const $dDay = $('.f_day').last();

        if(isNotEmpty) {
            $sigPad2.css({'background-color': '#fff', 'cursor': 'pointer', 'opacity': '1'});
            const now = new Date();
            $dYear.val(now.getFullYear());
            $dMonth.val(now.getMonth() + 1);
            $dDay.val(now.getDate());

            // [추가] 성명 입력 시 상단/중단 1번 즉시 실시간 체크 (작성 중 표시)
            $('.c_top_1, .c_mid_1').addClass('active');
        } else {
            const canvas = document.getElementById('sig-pad-2');
            ctxs['sig-pad-2'].clearRect(0, 0, canvas.width, canvas.height);
            $sigPad2.css({'background-color': '#f0f0f0', 'cursor': 'not-allowed', 'opacity': '0.6'});
            $dYear.val(''); $dMonth.val(''); $dDay.val('');
            $('.c_top_1, .c_mid_1').removeClass('active');
        }
    }).trigger('input');

    // [추가] 정정 전(r_pre) 입력 여부에 따른 정정 후(r_post) 활성화 제어
    $(document).on('input', '.r_pre', function() {
        const $row = $(this).closest('.target-row');
        const $postInput = $row.find('.r_post');
        if ($(this).val().trim() !== "") {
            $postInput.prop('disabled', false).css({'background-color': '#fff', 'cursor': 'text', 'opacity': '1'});
        } else {
            $postInput.val('').prop('disabled', true).css({'background-color': '#fcfcfc', 'cursor': 'not-allowed', 'opacity': '0.6'});
        }
    });

    // 체크박스
    $('.check-box').on('click', function (){
        const group = $(this).data('group');
        $(`.check-box[data-group="${group}"]`).not(this).removeClass('active');
        $(this).toggleClass('active');

        // [추가] 상단 클릭 시 중단 체크박스 실시간 시각적 동기화 (작성 화면 반영)
        if(group === 'top') {
            const isActive = $(this).hasClass('active');
            const classMatch = $(this).attr('class').match(/c_top_(\d+)/);
            if(classMatch) {
                const num = classMatch[1];
                $(`.check-box[data-group="mid"]`).removeClass('active');
                if(isActive) $(`.c_mid_${num}`).addClass('active');
            }
        }
    });

    // 엔터 키 제어 (기존 주석 및 로직 유지)
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
                    const $nextField = $this.closest('.target-row').find(`[data-col="${col+1}"]`);
                    if($nextField.prop('disabled')) { if(row < 5) addNewRow(row + 1); } else { $nextField.focus(); }
                } else { if (row < 5) { addNewRow(row + 1); } }
            }
            else if($this.attr('tabindex')){
                const nextIdx = parseInt($this.attr('tabindex')) + 1;
                const $next = $(`.form-field[tabindex="${nextIdx}"]`);
                if($next.length > 0) $next.focus(); else $('.f_report_content').focus();
            }
            else if($this.hasClass('f_report_content')) { $('.f_prev_master').focus(); }
            else if($this.hasClass('f_prev_master')) { $('.f_curr_master').focus(); }
            else if($this.hasClass('f_curr_master')) { $('.target-row[data-row="1"] .r_rel').focus(); }
        }
    });

    function addNewRow(num) {
        if(num > 7) return;
        const top = rowStartTop + ((num - 1) * rowGap);
        const html = `<div class="target-row" data-row="${num}">
            <input type="text" class="form-field row-input r_rel" data-col="1" style="top:${top}%">
            <input type="text" class="form-field row-input r_name" data-col="2" style="top:${top}%">
            <input type="text" class="form-field row-input r_jumin" data-col="3" style="top:${top}%">
            <input type="text" class="form-field row-input r_pre" data-col="4" style="top:${top}%">
            <input type="text" class="form-field row-input r_post" data-col="5" style="top:${top}%" disabled>
        </div>`;
        $('#dynamicRows').append(html);
        $(`.target-row[data-row="${num}"] .r_post`).css({'background-color': '#fcfcfc', 'cursor': 'not-allowed', 'opacity': '0.6'});
        $(`.target-row[data-row="${num}"] .r_rel`).focus();
    }

    // 초기화 버튼
    $('#clearBtn').on('click', function (){
        if(confirm('모든 입력 내용과 서명을 초기화 하시겠습니까?')){
            $('.form-field').val(''); $('.check-box').removeClass('active');
            canvasIds.forEach(id => { if(document.getElementById(id)) ctxs[id].clearRect(0, 0, document.getElementById(id).width, document.getElementById(id).height); });
            $('#dynamicRows').html($('.target-row[data-row="1"]'));
            $('.f_delegate_name').trigger('input'); $('.r_pre').trigger('input');
        }
    });

    $('.f_addr').on('focus', function() { if (!this.value.trim()) { execDaumPostcode('.f_addr'); this.blur(); } });

    // 신고서 제출 로직
    $('#submitBtn').off('click').on('click', function (e) {
        e.preventDefault();
        const reporterName = $('.f_name').val().trim();
        if (!reporterName) { alert("신고인 성명을 입력해주세요."); $('.f_name').focus(); return false; }

        const targetList = [];
        $('.target-row').each(function() {
            const row = { rel: $(this).find('.r_rel').val().trim(), name: $(this).find('.r_name').val().trim(), jumin: $(this).find('.r_jumin').val().trim(), pre: $(this).find('.r_pre').val().trim(), post: $(this).find('.r_post').val().trim() };
            if(row.name) targetList.push(row);
        });

        // 체크박스 번호 추출 함수
        const getCheckNum = (selector) => {
            const cls = $(selector).attr('class') || "";
            const match = cls.match(/_(\d+)/);
            return match ? match[1] : null;
        };

        const topNum = getCheckNum('.c_top_1.active, .c_top_2.active, .c_top_3.active');
        const midNum = getCheckNum('.c_mid_1.active, .c_mid_2.active, .c_mid_3.active');
        const btmNum = getCheckNum('.c_btm_1.active, .c_btm_2.active, .c_btm_3.active');

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
            submitYear: $('.f_year').first().val(), submitMonth: $('.f_month').first().val(), submitDay: $('.f_day').first().val(),
            sigReporter: document.getElementById('sig-pad-1').toDataURL('image/png'),
            sigDelegate: document.getElementById('sig-pad-2').toDataURL('image/png'),
            sigPrev: document.getElementById('sig-pad-prev').toDataURL('image/png'),
            sigCurr: document.getElementById('sig-pad-curr').toDataURL('image/png')
        };

        function validateForm(data) {
            // 필수 이름 및 형식 체크
            if (!data.reporterName) { alert("신고인 성명을 입력해주세요."); return false; }
            const reporterNameBottom = $('.f_reporter_name').val().trim();
            if (reporterNameBottom !== "" && data.reporterName !== reporterNameBottom) { alert("상단의 신고인 성명과 하단의 서명인 성명이 일치하지 않습니다."); return false; }

            // 필수 필드 체크
            if (!data.reporterRel || !data.reporterPhone || !data.reporterAddr || !data.reportContent) { alert("필수 입력 항목을 모두 채워주세요."); return false; }

            // [추가] 상단/중단 체크박스 일치 유효성 검사
            if (!topNum || !midNum) { alert("상단 및 하단의 신고 종류를 모두 체크해주세요."); return false; }
            if (topNum !== midNum) { alert("최상단의 신고 종류와 신고서 하단의 신고 종류가 일치하지 않습니다."); return false; }

            // [추가] 위임장 성명 입력 시 유효성 검사 (상단 vs 위임장 하단)
            if (data.delegateName !== "") {
                if (!btmNum) { alert("위임장 하단의 신고 종류를 체크해주세요."); return false; }
                if (topNum !== btmNum) { alert("최상단의 신고 종류와 위임장 하단의 신고 종류가 일치하지 않습니다."); return false; }
                if (data.sigDelegate.length < 1000) { alert("위임한 사람의 서명이 누락되었습니다."); return false; }
            }

            if (data.sigReporter.length < 1000) { alert("신고인 서명이 누락되었습니다."); return false; }
            return true;
        }

        if (!validateForm(formData)) return;

        if (confirm('작성하신 내용으로 신고서를 제출하시겠습니까?')) {
            $.ajax({
                url: '/sub/submit',
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(formData),
                xhrFields: { responseType: 'blob' },
                success: function(blob) {
                    const url = window.URL.createObjectURL(blob);
                    // 미리보기 새 창 열기
                    window.open(url, '_blank');
                    // 다운로드 실행
                    const link = document.createElement('a');
                    link.href = url;
                    link.download = '주민등록신고서.pdf';
                    link.click();
                    alert('신고서가 성공적으로 생성되었습니다.');
                }
            });
        }
    });

    $('.target-row[data-row="1"] .r_pre').trigger('input');
});