package kr.go.ydpb.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class NaverUserResponse {
    private String resultcode;
    private String message;
    private Response response;

    @Getter
    @Setter
    public static class Response {
        private String id;
        private String name;
        private String email;
        private String mobile;
        private String gender;
        private String birthyear;
        private String birthday;
    }
}
