package com.example.OAuth2Spotify.sporify;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Big {
    private List<BigTrack> items;
    private String next;
}
