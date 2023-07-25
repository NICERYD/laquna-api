package kr.co.seedit.domain.jwtAuth.api;

import kr.co.seedit.domain.company.application.CompanyService;
import kr.co.seedit.domain.mapper.seedit.CompanyDao;
import kr.co.seedit.domain.company.dto.CompanyDto;
import kr.co.seedit.domain.jwtAuth.application.JwtAuthenticationService;
import kr.co.seedit.global.common.application.CommonService;
import kr.co.seedit.global.common.application.JwtUserDetailsService;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * ************************************************************[ JwtAuthenticationController ]
 *
 * @파일명: JwtAuthenticationController.java
 * @작성일자 : 2023. 6. 07.
 * @작성자 : 박승현
 * @클래스 이름 : JwtAuthenticationController
 * @클래스 설명 : 토큰 인증을 위한 로그인 임시 컨트롤러
 * @변경이력 :
 * **************************************[ SeedIT ]*******************************
 */
@SuppressWarnings({"unused", "rawtypes"})
@RestController
@RequiredArgsConstructor
public class JwtAuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final CommonService commonService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final CompanyDao companyDao;
    private final JwtAuthenticationService jwtAuthenticationService;


    /**
     * 로그인 계정 인증
     *
     * @param companyDto
     * @return ResponseDto
     * @throws Exception
     */
    @PostMapping(value = "/api/v1/authenticate")
    public ResponseEntity<ResponseDto> auth(HttpServletRequest request,
                                            @RequestBody CompanyDto companyDto) throws Exception {
        ResponseDto ResponseDto = jwtAuthenticationService.signInCompany(companyDto);

        return new ResponseEntity<>(ResponseDto, HttpStatus.OK);
    }

}


