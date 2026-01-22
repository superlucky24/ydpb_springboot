package kr.go.ydpb.domain;

import lombok.Data;

@Data
public class NaverTokenResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
}
