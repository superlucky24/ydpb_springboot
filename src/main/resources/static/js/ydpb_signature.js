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
    $('.f_delegate_name').on('input', function() {
        const isNotEmpty = $(this).val().trim() !== "";
        const $sigPad2 = $('#sig-pad-2');
        if(isNotEmpty) {
            $sigPad2.css({'background-color': '#fff', 'cursor': 'crosshair', 'opacity': '1'});
        } else {
            // 이름이 비면 서명을 초기화하고 비활성화 표시
            const canvas = document.getElementById('sig-pad-2');
            ctxs['sig-pad-2'].clearRect(0, 0, canvas.width, canvas.height);
            $sigPad2.css({'background-color': '#f0f0f0', 'cursor': 'not-allowed', 'opacity': '0.6'});
        }
    }).trigger('input'); // 페이지 로드 시 초기 상태 적용

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
            $('.f_delegate_name').trigger('input'); // 위임장 서명패드 상태 복구
        }
    });

    // 주소 입력 부분 포커스 시 주소 입력 API 호출 0130 귀환
    const addrInput = document.querySelector('.f_addr');
    // 포커스 이벤트 리스너 추가
    $('.f_addr').on('focus', function() {
        if (!this.value.trim()) {

            execDaumPostcode('.f_addr');
            console.log('주소찾기 api 실행');
            // 포커스 시 브라우저 기본 파란 테두리가 남지 않도록 포커스 해제
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
        const juminReg = /^[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])-[1-4][0-9]{6}$/;
        const targetList = [];
        $('.target-row').each(function() {
            const row = {
                el: this,
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
            // [추가] 상단 신고인 성명과 하단 서명란 성명 일치 여부 확인 - 연수
            const reporterNameTop = data.reporterName;
            const reporterNameBottom = $('.f_reporter_name').val().trim();

            if (reporterNameTop !== reporterNameBottom) {
                alert("상단의 신고인 성명과 하단의 서명인 성명이 일치하지 않습니다.\n동일한 성명을 입력해주세요.");
                $('.f_reporter_name').focus();
                return false;
            }

            // 주민등록번호 확인
            if (!data.reporterJumin || !juminReg.test(data.reporterJumin)) {
                alert("주민등록번호를 올바르게 입력해주세요.");
                $('.f_jumin').focus();
                return false;
            }

            // 필수 필드 체크 (관계, 휴대전화, 주소, 내용, 세대주)
            if (!data.reporterRel) { alert("세대주와의 관계를 입력해주세요."); $('.f_rel').focus(); return false; }
            if (!data.reporterPhone) { alert("휴대전화번호를 입력해주세요."); $('.f_phone').focus(); return false; }
            if (!data.reporterAddr) { alert("기본 주소를 입력해주세요."); $('.f_addr').focus(); return false; }
            if (!data.reportContent) { alert("신고 내용을 입력해주세요."); $('.f_report_content').focus(); return false; }
            if (!data.prevMaster) { alert("전 세대주명을 입력해주세요."); $('.f_prev_master').focus(); return false; }
            if (!data.currMaster) { alert("현 세대주 명을 입력해주세요."); $('.f_curr_master').focus(); return false; }

            // 신고 종류 체크박스 필수
            if (!data.topType) { alert("최상단의 신고 종류를 체크해주세요."); return false; }
            if (!data.midType) { alert("신고서 하단의 신고 종류를 체크해주세요."); return false; }

            // [위임장 유효성 검사] 성명 입력 시 체크박스와 서명 필수
            const currentDelegateName = $('.f_delegate_name').val().trim();
            if (currentDelegateName !== "") {
                if (!data.btmType) {
                    alert("위임한 사람의 성명을 입력하셨으므로, 위임장 아래 신고 종류를 반드시 체크해야 합니다.");
                    return false;
                }
                if (data.sigDelegate.length < 1000) {
                    alert("위임한 사람의 서명이 누락되었습니다.");
                    return false;
                }
            }

            // 서명란 체크 (신고인, 전/현 세대주)
            if (data.sigReporter.length < 1000) { alert("신고인 서명이 누락되었습니다."); return false; }
            if (data.sigPrev.length < 1000) { alert("전 세대주 서명이 누락되었습니다."); return false; }
            if (data.sigCurr.length < 1000) { alert("세대주 서명이 누락되었습니다."); return false; }

            return true;
        }

        if (!validateForm(formData)) return;

        // 3. 서버 전송
        if (confirm('작성하신 내용으로 신고서를 제출하시겠습니까?')) {
            $.ajax({
                url: '/sub/submit',
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(formData),
                xhrFields: { responseType: 'blob' },
                success: function(blob) {
                    const url = window.URL.createObjectURL(blob);
                    const newWindow = window.open(url, '_blank');
                    const link = document.createElement('a');
                    link.href = url;
                    link.download = '주민등록신고서_' + new Date().toISOString().slice(0,10).replace(/-/g, '') + '.pdf';
                    link.click();
                    alert('신고서가 성공적으로 생성되었습니다.');
                    setTimeout(() => window.URL.revokeObjectURL(url), 100);
                },
                error: function() {
                    alert('제출 중 오류가 발생했습니다.');
                }
            });
        }
    });
});