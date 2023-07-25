package kr.co.seedit;

import kr.co.seedit.global.config.CommonProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserTest {
    
    @Autowired
    CommonProperties commonProperties;

//    @Test
//    public void createJWT(){
//        CompanyDto companyDto = CompanyDto.builder()
//                .emailId("dev_test@seedit.co.kr")
//                .representativeName("박승현")
//                .build();
//
//        Date date = new Date(Timestamp.valueOf(LocalDateTime.now().plusSeconds(commonProperties.getJwtExpirationIn().longValue())).getTime());
//        String token = Jwts.builder()
//                .setSubject(companyDto.getEmailId())
//                .setIssuedAt(new Date())
//                .setExpiration(date)
//                .signWith(SignatureAlgorithm.HS256, commonProperties.getJwtSecret())
//                .claim("userId", companyDto.getEmailId())
//                .claim("name", companyDto.getRepresentativeName())
//                .compact();
//    }

}
