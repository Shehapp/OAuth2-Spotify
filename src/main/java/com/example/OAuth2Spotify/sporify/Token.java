package com.example.OAuth2Spotify.sporify;


import lombok.Data;

@Data
public class Token {
    private String access_token;
    private String token_type;
    private String scope;
    private String expires_in;
}
