package kr.go.ydpb.controller;

import com.google.firebase.auth.FirebaseToken;
import kr.go.ydpb.service.FirebaseAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final FirebaseAuthService firebaseAuthService;

    public AuthController(FirebaseAuthService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    @PostMapping("/phone")
    public ResponseEntity<?> phoneAuth(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken"); // JSON 객체에서 idToken 추출

        FirebaseToken token = firebaseAuthService.verifyToken(idToken);
        String uid = token.getUid();
        String phoneNumber = token.getClaims().get("phone_number").toString();

        return ResponseEntity.ok(Map.of(
                "uid", uid,
                "phoneNumber", phoneNumber
        ));
    }

}