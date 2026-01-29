(function() {
    // Firebase 설정
    const firebaseConfig = {
        apiKey: "AIzaSyCb6wEr9ueleYgHQRcPlrBEudsIzLCLz_w",
        authDomain: "springboot-53fc2.firebaseapp.com",
        projectId: "springboot-53fc2",
        storageBucket: "springboot-53fc2.firebasestorage.app",
        messagingSenderId: "869829134553",
        appId: "1:869829134553:web:32d8967d3bdc316ec596c8"
    };

    // Firebase 초기화 (구글에서 제공)
    firebase.initializeApp(firebaseConfig);

    let confirmationResult;

    // reCAPTCHA 생성
    window.recaptchaVerifier = new firebase.auth.RecaptchaVerifier('recaptchaContainer', {
        size: 'invisible',
        callback: (response) => {
            console.log("reCAPTCHA solved");
        }
    });
    const appVerifier = window.recaptchaVerifier;

    const $sendCodeBtn = $('#sendCodeBtn');     // 인증번호 전송 버튼
    const $verifyCodeBtn = $('#verifyCodeBtn'); // 인증번호 확인 버튼

    // 인증번호 전송
    function sendCode() {
        const phoneNumber = document.getElementById("phoneNumber").value;
        $sendCodeBtn.addClass('loading');

        firebase.auth().signInWithPhoneNumber(phoneNumber, appVerifier)
            .then((result) => {
                // 유효한 전화번호 사용으로 인증번호 전송 성공 시
                confirmationResult = result;
                document.getElementById("status").innerText = "인증번호 전송 완료!";
                // 현재 화면 숨기고 다음 단계 화면으로 이행
                $('#authSec1').hide();
                $('#authSec2').show();
                $sendCodeBtn.removeClass('loading');
            })
            .catch((error) => {
                // 인증번호 전송 실패 시
                console.error(error);
                let errorMsg = error.message;
                if(/invalid-phone-number/.test(errorMsg)) {
                    errorMsg = "유효한 전화번호가 아닙니다";
                }
                document.getElementById("status").innerText = "오류: " + errorMsg;
                $sendCodeBtn.removeClass('loading');
            });
    }

    // 인증번호 확인
    function verifyCode() {
        const code = document.getElementById("verificationCode").value;
        $verifyCodeBtn.addClass('loading');

        // 인증번호 확인을 위한 중간단계 객체
        confirmationResult.confirm(code)
            .then((result) => {
                // 인증 성공 한 사용자 지정
                const user = result.user;

                // 서버에 idToken 전송
                user.getIdToken()
                    .then(idToken => {
                        fetch('/auth/phone', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ idToken: idToken })
                        })
                        .then(res => {
                            if (res.ok) {
                                document.getElementById("status").innerText = "서버 인증 완료!";

                                // 인증 성공 시 부모창으로 데이터 전송
                                if (window.opener && !window.opener.closed) {
                                    const data = {
                                        name: $('#memName').val(),
                                        birth: $('#memBirth').val(),
                                        gender: $('#memJumin2').val(),
                                        phone: $('#phoneNumberOrigin').val()
                                    }
                                    // 부모창의 receiveData 함수 실행
                                    window.opener.receiveData(data);
                                }
                                // 현재 창 닫기
                                window.close();
                            } else {
                                document.getElementById("status").innerText = "서버 인증 실패!";
                            }
                            $verifyCodeBtn.removeClass('loading');
                        });
                    });
            })
            .catch((error) => {
                console.error(error);
                document.getElementById("status").innerText = "인증번호 확인 실패!";
                $verifyCodeBtn.removeClass('loading');
            });
    }

    // 인증번호 전송 버튼 클릭 이벤트
    $sendCodeBtn.on('click', function() {
        // 로딩 중일 때 이벤트 중지
        if($(this).hasClass('loading')) {
            return false;
        }

        // 입력값 오류 있을 시 전송 실패
        const resultCount = $('#authSec1 input[data-check="fail"]').length;
        if(resultCount > 0) {
            document.getElementById("status").innerText = "입력정보를 다시 확인해주세요!";
            return false;
        }

        // 인증번호 전송
        sendCode();
    });

    // 인증번호 확인 버튼 클릭 이벤트
    $verifyCodeBtn.on('click', function() {
        // 로딩 중일 때 이벤트 중지
        if($(this).hasClass('loading')) {
            return false;
        }

        // 인증번호 확인
        verifyCode();
    });

    // 이름 입력 체크
    $('#memName').on('input', function() {
        let textBox = $('.auth_text[data-match="'+ this.id +'"]');
        this.value = this.value.replace(/[^\p{L}]/gu, '');
        let isPassed = /^(?:[가-힣]{1,16}|[A-Za-z]{1,50})$/.test(this.value);
        if(this.value && isPassed) {
            $(this).attr('data-check', 'ok');
            $('#authTitle').html('<b>생년월일\/성별</b>을<br>입력해 주세요');
            $('#authStep2').show();
            textBox.empty();
        }
        else {
            $(this).attr('data-check', 'fail');
            textBox.text('올바른 이름을 입력해주세요.');
        }
    });

    // 주민번호 앞자리 체크
    $('#memJumin1').on('input', function() {
        let textBox = $('.auth_text[data-match="'+ this.id +'"]');
        this.value = this.value.slice(0, 6);

        if(this.value.length > 5 && !normalizeWithCheck(this.value).wasInvalid) {
            $(this).attr('data-check', 'ok');
            const birthDate = normalizeWithCheck(this.value).normalized
            $('#memBirth').val(birthDate);
            textBox.empty();

            const today = toDateOnly(new Date());
            const target = toDateOnly(new Date(birthDate));
            if(today <= target) {
                $(this).attr('data-check', 'fail');
                textBox.text('오늘 이전 날짜를 입력해주세요.');
            }

            if($('#memJumin2').attr('data-check') === 'ok') {
                $('#authStep3').show();
                $('#authTitle').html('<b>휴대폰번호</b>를<br>입력해 주세요');
            }
        }
        else {
            $(this).attr('data-check', 'fail');
            textBox.text('정확한 생년월일을 입력해주세요.');
        }
    });

    // 생년월일 체크
    function normalizeWithCheck(input) {
        const formatted = input.replace(/(\d{2})(\d{2})(\d{2})/, "$1/$2/$3");
        const [yy, mm, dd] = formatted.split('/').map(Number);
        const year = yy < 50 ? 2000 + yy : 1900 + yy;
        const date = new Date(Date.UTC(year, mm - 1, dd));
        const changed =
            date.getMonth() !== mm - 1 ||
            date.getDate() !== dd;

        return {
            normalized: date.toISOString().slice(0, 10),
            wasInvalid: changed
        };
    }

    // 년월일 Date 타입으로 반환
    function toDateOnly(date) {
        return new Date(
            date.getFullYear(),
            date.getMonth(),
            date.getDate()
        );
    }

    // 주민번호 뒷자리 체크
    $('#memJumin2').on('input', function() {
        this.value = this.value.replace(/[^1-4]/gu, '').slice(0, 1);
        if(this.value.length > 0) {
            $(this).attr('data-check', 'ok');
            if($('#memJumin1').attr('data-check') === 'ok') {
                $('#authStep3').show();
            }
        }
        else {
            $(this).attr('data-check', 'fail');
        }
    });

    // 전화번호 입력창 체크
    $('#phoneNumberOrigin').on('input', function() {
        this.value = this.value.slice(0, 11);
        let val = this.value;
        $('#phoneNumber').val(val.replace(/^0/, '+82'));
        if(this.value.length > 10) {
            $(this).attr('data-check', 'ok');
        }
        else {
            $(this).attr('data-check', 'fail');
        }
    });
})();