package com.pulsepay.payment_service.service;

import com.pulsepay.payment_service.entity.Role;
import com.pulsepay.payment_service.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.SuperBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {

        // 1. roles -> list of text
        List<String> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roles.add(role.getName().name());
        }

        // 2. times
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        // 3. build + sign the token
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }


    private Claims parseClaims(String token) // actually checaking happens here in this method 
    {
        return Jwts.parser()
                .verifyWith(secretKey) // "use OUR secret key"
                .build()
                .parseSignedClaims(token) // split → recalculate → compare → check exp // here the checking proc ess happens
                .getPayload();  // all OK → give back the payload

    }

    public boolean isTokenValid(String token) // check token valid or not
    {
        try
        {
            parseClaims(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e)
        {
            return false;
        }
    }

    public String getEmailFromToken(String token)
    {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }


    @@SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token)
    {
        Claims claims = parseClaims(token);
        return claims.get("roles",List.class);
    }
}