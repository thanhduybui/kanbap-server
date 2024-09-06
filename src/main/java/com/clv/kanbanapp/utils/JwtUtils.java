package com.clv.kanbanapp.utils;

import com.clv.kanbanapp.entity.AppUser;
import com.clv.kanbanapp.repository.AppUserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
public class JwtUtils {
    private final AppUserRepository appUserRepository;
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    private static final String SECRET_KEY = "your-32-character-minimum-secret-" +
            "key-herefdjffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
            "ffffffffffffffffffff" +
            "ffffffffffffffffffffffffffffffffffffffffffffffffffffff";

    public static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public static String generateToken(AppUser user) {
        Date now = new Date();
        Date expiredTime = new Date(now.getTime() + EXPIRATION_TIME);
        Key key = getSigningKey();


        return Jwts.builder().issuedAt(now)
                .subject(user.getEmail())
                .expiration(expiredTime)
                .signWith(key)
                .compact();


    }

    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.error("Expired token {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token is not in the right format {}", e.getMessage());
        }
        return null;
    }

    public static String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return (claims != null) ? claims.getSubject() : null;
    }

    public static boolean isTokenValid(String token) {
        return parseToken(token) != null;
    }

}
