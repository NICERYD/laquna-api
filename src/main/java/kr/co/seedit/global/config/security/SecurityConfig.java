package kr.co.seedit.global.config.security;

import kr.co.seedit.global.common.application.JwtUserDetailsService;
import kr.co.seedit.global.config.CommonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsUtils;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CommonProperties commonProperties;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

     @Bean
     public BCryptPasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
      }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .httpBasic().disable() // rest api 만을 고려하여 기본설정 해제
            .csrf().disable()
            .authorizeRequests() // 요청에 대한 사용 권한 체크
            .antMatchers("/api/v1/join/**").permitAll()
            .antMatchers("/api/v1/common/getCommonModuleList").permitAll()
            .antMatchers("/api/v1/common/getcodeDetaillist").permitAll()
            .antMatchers("/api/v1/common/getcodelist").permitAll()
            .antMatchers("/api/v1/common/getCommonCodelistSearch").permitAll()
            .antMatchers("/api/v1/authenticate").permitAll()
            .requestMatchers(request -> CorsUtils.isPreFlightRequest(request)).permitAll()
            .anyRequest().authenticated()
//            .anyRequest().permitAll()
            .and()
            // store user's state.jwtAuthenticationEntryPoint
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(corsConfig.corsFilter()) // ** CorsFilter 등록 **
            .addFilterAfter(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}