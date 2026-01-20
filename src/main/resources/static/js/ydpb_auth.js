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

    firebase.initializeApp(firebaseConfig);

    let confirmationResult;

    // 인증번호 전송
    function sendCode() {
        const phoneNumber = document.getElementById("phoneNumber").value;

        // reCAPTCHA 생성
        window.recaptchaVerifier = new firebase.auth.RecaptchaVerifier('recaptchaContainer', {
            size: 'invisible',
            callback: (response) => {
                console.log("reCAPTCHA solved");
            }
        });

        const appVerifier = window.recaptchaVerifier;

        firebase.auth().signInWithPhoneNumber(phoneNumber, appVerifier)
            .then((result) => {
                confirmationResult = result;
                document.getElementById("status").innerText = "인증번호 전송 완료!";
            })
            .catch((error) => {
                console.error(error);
                document.getElementById("status").innerText = "오류: " + error.message;
            });
    }

    // 인증번호 확인
    function verifyCode() {
        const code = document.getElementById("verificationCode").value;

        confirmationResult.confirm(code)
            .then((result) => {
                const user = result.user;
                console.log("로그인 성공:", user);

                // 서버에 idToken 전송
                user.getIdToken().then(idToken => {
                    fetch('/auth/phone', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ idToken: idToken })
                    }).then(res => {
                        if (res.ok) {
                            document.getElementById("status").innerText = "서버 인증 완료!";
                        } else {
                            document.getElementById("status").innerText = "서버 인증 실패!";
                        }
                    });
                });
            })
            .catch((error) => {
                console.error(error);
                document.getElementById("status").innerText = "인증번호 확인 실패!";
            });
    }

    $('#sendCodeBtn').on('click', function() {
        sendCode();
    });
    $('#verifyCodeBtn').on('click', function() {
        verifyCode();
    });

    // 이름 입력 체크
    $('#memName').on('keyup', function() {
        let val = this.value;
        let textBox = $('.auth_text[data-match="'+ this.id +'"]');
        this.value = val.replace(/[^\p{L}]/gu, '');
        if(this.value) {
            $(this).attr('data-check', 'ok');
            $('#authTitle').html('<b>생년월일\/성별</b>을<br>입력해 주세요');
            $('#authStep2').show();
            textBox.empty();
        }
        else {
            $(this).attr('data-check', 'fail');
            textBox.text('이름을 입력해주세요');
        }
    });

    // 주민번호 앞자리 체크
    $('#memJumin1').on('keyup', function() {
        let val = this.value;
        let textBox = $('.auth_text[data-match="'+ this.id +'"]');
        this.value = val.replace(/[^\p{N}]/gu, '');
        if(this.value.size > 5) {
            $(this).attr('data-check', 'ok');
            $('#authTitle').html('<b>휴대폰번호</b>를<br>입력해 주세요');
            $('#authStep3').show();
            textBox.empty();
        }
        else {
            $(this).attr('data-check', 'fail');
            textBox.text('생년월일을 정확히 입력해주세요');
        }
    });
})();