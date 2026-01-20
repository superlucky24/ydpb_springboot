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
        window.recaptchaVerifier = new firebase.auth.RecaptchaVerifier('recaptcha-container', {
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
                            document.getElementById("status").innerText = "✅ 서버 인증 완료!";
                        } else {
                            document.getElementById("status").innerText = "❌ 서버 인증 실패!";
                        }
                    });
                });
            })
            .catch((error) => {
                console.error(error);
                document.getElementById("status").innerText = "❌ 인증번호 확인 실패!";
            });
    }

    return {
        sendCode: sendCode,
        verifyCode: verifyCode
    }
})();