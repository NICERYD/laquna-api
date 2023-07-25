package kr.co.seedit.global.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "seedit.common")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter@Setter
public class CommonProperties {
    private String passwordEncryptKey;
    private String jwtSecret;
    private String jwtIssuer;
    private Integer jwtExpirationIn;
    private String jwtRefreshSecret;
    private Integer jwtRefreshExpirationIn;
    private Integer jwtRefreshReissueIn;
    private String databaseEncryptKey;
}