package com.codevui.realworldapp.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.codevui.realworldapp.entity.User;
import com.codevui.realworldapp.model.TokenPayload;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

    @Value("${JWT_SECRET_KEY}")
    private String secret;

    public String generateToken(User user, long expiredDate) {
        Map<String, Object> claims = new HashMap<>();
        TokenPayload tokenPayload = TokenPayload.builder().userId(user.getId()).email(user.getEmail()).build();
        claims.put("payload", tokenPayload);
        return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredDate * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public TokenPayload getTokenPayLoad(String token) {
        return getClaimsFromToken(token, (Claims claim) -> {
            Map<String, Object> mapResults = (Map<String, Object>) claim.get("payload");
            return TokenPayload.builder().userId((int) mapResults.get("userId")).email((String) mapResults.get("email"))
                    .build();
        });
    }

    private <T> T getClaimsFromToken(String token, Function<Claims, T> claimResolver) {
        final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claimResolver.apply(claims);
    }

    public boolean validate(String token, User user) {
        TokenPayload tokenPayload = getTokenPayLoad(token);

        return tokenPayload.getUserId() == user.getId() && tokenPayload.getEmail().equals(user.getEmail())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expirDate = getClaimsFromToken(token, Claims::getExpiration);
        return expirDate.before(new Date());
    }
}
