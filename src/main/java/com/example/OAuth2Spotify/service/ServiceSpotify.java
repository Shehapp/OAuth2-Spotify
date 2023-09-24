package com.example.OAuth2Spotify.service;


import com.example.OAuth2Spotify.sporify.Artist;
import com.example.OAuth2Spotify.sporify.Big;
import com.example.OAuth2Spotify.sporify.BigTrack;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ServiceSpotify {
    private final RestTemplate restTemplate;


    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomStringBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            randomStringBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomStringBuilder.toString();
    }


    public List<Artist> getArtists(String url, String token, HashMap<Artist, Long> freq) {
        // base case
        if (url == null || url.isEmpty()) {
            List<Artist> topArtists = getTop10(freq);
            for (Artist artist : topArtists)
                artist.setCount(freq.get(artist));
            return getTop10(freq);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<Big> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Big.class);

        this.updateFreq(responseEntity.getBody(), freq);

        // transaction
        return getArtists(responseEntity.getBody().getNext(), token, freq);

    }

    void updateFreq(Big big, HashMap<Artist, Long> freq) {
        List<BigTrack> bigTracks = big.getItems();
        for (BigTrack bigTrack : bigTracks) {
            for (Artist artist : bigTrack.getTrack().getArtists()) {
                if (freq.containsKey(artist)) {
                    freq.put(artist, freq.get(artist) + 1L);
                } else {
                    freq.put(artist, 1L);
                }
            }
        }
    }

    List<Artist> getTop10(HashMap<Artist, Long> freq) {
        return freq.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(e -> e.getKey())
                .limit(10)
                .collect(Collectors.toList());
    }
}
