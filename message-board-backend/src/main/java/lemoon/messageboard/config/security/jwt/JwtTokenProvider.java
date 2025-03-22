package lemoon.messageboard.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final long SESSION_TOKEN_VALIDITY = 24 * 60 * 60 * 1000L; // 1天（会话token）
    private static final long REMEMBER_TOKEN_VALIDITY = 30 * 24 * 60 * 60 * 1000L; // 30天（记住我token）

    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * 创建令牌（默认会话token）
     */
    public String createToken(Authentication authentication) {
        return createToken(authentication, false);
    }

    /**
     * 创建带有不同过期时间的令牌
     * @param authentication 认证信息
     * @param rememberMe 是否记住我
     * @return JWT令牌
     */
    public String createToken(Authentication authentication, boolean rememberMe) {
        long now = System.currentTimeMillis();
        // 根据rememberMe状态选择不同的过期时间
        long tokenValidity = rememberMe ? REMEMBER_TOKEN_VALIDITY : SESSION_TOKEN_VALIDITY;
        Date validity = new Date(now + tokenValidity);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .signWith(key)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities = List.of();
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT token 无效", e);
            return false;
        }
    }
} 