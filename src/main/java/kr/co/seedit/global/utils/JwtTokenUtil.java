package kr.co.seedit.global.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.co.seedit.domain.mapper.seedit.CompanyDao;
import kr.co.seedit.domain.company.dto.CompanyDto;
import kr.co.seedit.global.config.CommonProperties;
import kr.co.seedit.global.error.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    @Serial
    private static final long serialVersionUID = 7373874946577220307L;
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private final CommonProperties commonProperties;

    public JwtTokenUtil(CommonProperties commonProperties) {
        this.commonProperties = commonProperties;
    }

    public String getUsernameFromToken(String token) {

        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {

        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(commonProperties.getJwtSecret()).parseClaimsJws(token).getBody();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // get or set member's role
        claims.put("role", "admin");
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // set the expiry time
                .setExpiration(new Date(Timestamp.valueOf(LocalDateTime.now().plusSeconds(commonProperties.getJwtExpirationIn().longValue())).getTime()))
                .signWith(SignatureAlgorithm.HS512, commonProperties.getJwtSecret()).compact();
    }

    public String generateToken(CompanyDto companyDto, UserDetails userDetails) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        // get or set member's role
        int chk = companyDao.selectMemberAccountChk(companyDto);
        if (chk != 1) {
            throw new CustomException(CustomException.ERR_8999);
        }

        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        claims.put("role", "admin");
        claims.put("memberId", info.getEmailId());
//        claims.put("clientIp", getClientIp(request));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(info.getEmailId())
                .setIssuer(commonProperties.getJwtIssuer())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(Timestamp.valueOf(LocalDateTime.now().plusSeconds(commonProperties.getJwtExpirationIn().longValue())).getTime()))
                .signWith(SignatureAlgorithm.HS256, commonProperties.getJwtSecret())
                .compact();
    }
//    public String getClientIp(HttpServletRequest request) {
//        String remoteAddr = "";
//        if (request != null) {
//            remoteAddr = request.getHeader("X-FORWARDED-FOR");
//            if (remoteAddr == null || "".equals(remoteAddr)) {
//                remoteAddr = request.getRemoteAddr();
//            }
//        }
//        return remoteAddr;
//    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public Date getExpirationDateFromToken(String token) {

        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {

        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}