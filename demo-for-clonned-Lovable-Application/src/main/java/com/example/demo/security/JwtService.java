package com.example.demo.security;

import com.example.demo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

@Service
public class JwtService
{
    @Value("${jwt.secreateKey}")
    private String secreateKey;

    private SecretKey secretKey()
    {
        return Keys.hmacShaKeyFor(secreateKey.getBytes(StandardCharsets.UTF_8));
    }

    public  String generateJWT(User user)
    {
        return Jwts.builder()
                .claim("email",user.getEmail())
                .claim("name",user.getName())
                .subject(user.getUserId().toString())
                .signWith(secretKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*5))
                .compact();
    }

    public JwtUserPrincipal validateToken(String jwtToken)
    {
        Claims claims=Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

        Long userId= Long.parseLong(claims.getSubject());
        String userName=claims.get("email",String.class);
        return new JwtUserPrincipal(userId,userName,new ArrayList<>());
    }

    public Long getCurrentUser()
    {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof JwtUserPrincipal userPrincipal))
        {
            throw new AuthenticationCredentialsNotFoundException("Jwt Token is null");
        }
        return userPrincipal.userId();
    }

}
