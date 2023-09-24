package com.example.OAuth2Spotify.controller;


import com.example.OAuth2Spotify.service.ServiceSpotify;
import com.example.OAuth2Spotify.sporify.Artist;

import com.example.OAuth2Spotify.sporify.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;



@Controller
public class SpotifyController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServiceSpotify serviceSpotify;

    @Value("${spotify.OAuth2.client_id}")
    private String clientId;

    @Value("${spotify.OAuth2.client_secret}")
    private String clientSecret;

    @Value("${spotify.OAuth2.authorization_uri}")
    private String authorizationUri;

    @Value("${spotify.OAuth2.token_uri}")
    private String tokenUri;

    @Value("${spotify.OAuth2.redirect_uri}")
    private String redirectUri;

    @Value("${spotify.OAuth2.scope}")
    private String scope;

    @Value("${spotify.OAuth2.user_info_uri}")
    private String userInfoUri;


    @GetMapping("/")
    String authorize(Model model) {
        String url = authorizationUri +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&scope=" + scope +
                "&redirect_uri=" + redirectUri +
                "&state=" + serviceSpotify.generateRandomString(16);
        model.addAttribute("url", url);
        return "home";
    }


    @GetMapping("/callback")
    String getToken(@RequestParam String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", code);
        requestBody.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        Token token = restTemplate.postForObject(tokenUri, requestEntity, Token.class);

        assert token != null;

        return "redirect:/top-tracks/" + token.getAccess_token();
    }


    @GetMapping("/top-tracks/{token}")
    String getTopTracks(@PathVariable String token, Model model) {

        List<Artist> artists = serviceSpotify.getArtists(userInfoUri, token, new HashMap<>());

        model.addAttribute("artists", artists);
        return "top-artists";
    }

}
