package com.example.OAuth2Spotify.sporify;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Artist {

    private String name;
    private Long count;

    @Override
    public int hashCode() {

        Long s = 0L;
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) != ' ') {
                s += ((name.charAt(i) - 'A') * 32);
                s %= 1000000007;
            }
        }
        return Math.toIntExact(s);
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }
}
