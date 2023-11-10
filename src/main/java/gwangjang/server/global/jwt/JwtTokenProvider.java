package gwangjang.server.global.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final String ADDITIONAL_INFO = "isAdditionalInfoProvided";

    @Value("${token.access-expired-time}")
    private long accessTokenValidityTime;

    @Value("${token.refresh-expired-time}")
    private long refreshTokenValidityTime;

    @Value("${token.secret}")
    private String secretKey;

    public String createJwtAccessToken(String socialId, boolean isAdditionalInfoProvided,String role) {
        // claim 생성
        Claims claims = getClaims(socialId, isAdditionalInfoProvided);
        claims.put("role", role);


        Date now = new Date();
        Date accessTokenValidity = new Date(now.getTime() + this.accessTokenValidityTime);
        Date refreshTokenValidity = new Date(now.getTime() + this.refreshTokenValidityTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessTokenValidity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String createJwtRefreshToken(String socialId, boolean isAdditionalInfoProvided,String role) {
        Claims claims = getClaims(socialId, isAdditionalInfoProvided);
        claims.put("role", role);


        Date now = new Date();
        Date accessTokenValidity = new Date(now.getTime() + this.accessTokenValidityTime);
        Date refreshTokenValidity = new Date(now.getTime() + this.refreshTokenValidityTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(refreshTokenValidity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

    }

    private static Claims getClaims(String socialId, boolean isAdditionalInfoProvided) {
        // claim 에 socialId 정보 추가
        Claims claims = Jwts.claims().setSubject(socialId);
        claims.put(ADDITIONAL_INFO, isAdditionalInfoProvided);
        return claims;
    }

    public String getUserId(String token) {
        return getClaimsFromJwtToken(token).getSubject();
    }

    private Claims getClaimsFromJwtToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String getRefreshTokenId(String token) {
        return getClaimsFromJwtToken(token).get("value").toString();
    }

    public List<String> getRoles(String token) {
        return (List<String>) getClaimsFromJwtToken(token).get("role");
    }

    public void validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw e ;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw e;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw e;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw e;
        }
    }


    public boolean equalRefreshTokenId(String refreshTokenId, String refreshToken) {
        String compareToken = this.getRefreshTokenId(refreshToken);
        return refreshTokenId.equals(compareToken);
    }

    public String getSocialId(String token) {
        log.info("getsocialId! ->{}",Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

}
