package kr.co.seedit.domain.jwtAuth.application;

import kr.co.seedit.domain.mapper.seedit.CompanyDao;
import kr.co.seedit.domain.company.dto.CompanyAuthInfoDto;
import kr.co.seedit.domain.company.dto.CompanyDto;
import kr.co.seedit.domain.jwtAuth.dto.JwtRequest;
import kr.co.seedit.domain.mapper.seedit.MenuDao;
import kr.co.seedit.domain.menu.dto.MenuDto;
import kr.co.seedit.global.common.application.CommonService;
import kr.co.seedit.global.common.application.JwtUserDetailsService;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.error.exception.CustomException;
import kr.co.seedit.global.utils.JwtTokenUtil;
import kr.co.seedit.global.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final CommonService commonService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CompanyDao companyDao;
    private final MenuDao menuDao;

    @Transactional
    public ResponseDto signInCompany(CompanyDto companyDto) throws Exception {
//      ResponseDto responseDto = ResponseDto.builder().build();
        ResponseDto responseDto = createAuthenticationToken(companyDto);
        Map<String, Object> data = new HashMap<String, Object>();
        List<String> sub = new ArrayList<>();

        if (responseDto.isSuccess()){
            CompanyAuthInfoDto companyAuthInfoDto = companyDao.selectUserAuthInfo(companyDto);
            data.put("info", companyAuthInfoDto);
            data.put("token", responseDto.getData());
//            responseDto.setData(data);

            //메뉴리스트 조립
//            MenuDto menuDto = new MenuDto();
            List<MenuDto> menuList = menuDao.selectMenuList(companyAuthInfoDto);
//            for (MenuDto mdo: menuList) {
//
//                if(mdo.getMenuType().equals("00")){
////                    data.put("main", mdo);
//                }
//                else {
//                    sub.add(mdo.getMenuType());
//                    data.put("sub", sub);
//                }
//            }
            data.put("menuList", menuList);
            responseDto.setData(data);
        } else {
            throw new CustomException(CustomException.ERR_3002);
        }
        return responseDto;
    }
    @Transactional
    public ResponseDto createAuthenticationToken(CompanyDto companyDto) throws Exception {
        ResponseDto responseDto = ResponseDto.builder().build();
        boolean flagLogin = true;
        String message = "";
        Map<String, Object> returnMap = new HashMap<String, Object>();

        String emailId = companyDto.getEmailId();
        String password = companyDto.getPassword();

        JwtRequest authenticationRequest = new JwtRequest();
        authenticationRequest.setEmailId(emailId);
        authenticationRequest.setPassword(password);

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(
                authenticationRequest.getEmailId());

        //사용자가 없을때
        if (userDetails.getPassword().equals("")){
            message = CustomException.ERR_8999.split(",")[1];
            String code = CustomException.ERR_8999.split(",")[0];
            responseDto.setCode(code);
            responseDto.setMessage(message);
            responseDto.setSuccess(false);
        } else {
            String securityCode = "";
            Map<String, Object> securityMap = new HashMap<>();

            CompanyDto info = companyDao.selectTokenInfo(companyDto);

            securityMap = commonService.getSecurityCk("login", emailId, "");
            securityCode = StringUtils.normalizeNull(securityMap.get("code"));

            if (securityCode.equals("200")){
                if (!passwordEncoder.matches(companyDto.getPassword(), userDetails.getPassword())) {
                    message = CustomException.ERR_8999.split(",")[1];
                    String code = CustomException.ERR_8999.split(",")[0];
                    responseDto.setCode(code);
                    responseDto.setMessage(message);
                    responseDto.setSuccess(false);
                } else {
                    authenticate(emailId, password);
                    //로그인 성공
//                    message = StringUtils.normalizeNull(securityMap.get("message"));
                    final String token = jwtTokenUtil.generateToken(companyDto, userDetails);
                    returnMap.put("token", token);
                    returnMap.put("emailId", info.getEmailId());
                    returnMap.put("nickName", info.getNickName());
                    responseDto.setCode(securityCode);
                    responseDto.setMessage("로그인 성공");
                    responseDto.setData(returnMap);
                    return responseDto;
                }
            } else {
                message = StringUtils.normalizeNull(securityMap.get("message"));
                responseDto.setCode(securityCode);
                responseDto.setMessage(message);
                responseDto.setSuccess(false);
                return responseDto;
            }
        }
        return responseDto;
    }

    @SuppressWarnings("unused")
    private void authenticate(String emailId, String password) throws Exception {
        Objects.requireNonNull(emailId);
        Objects.requireNonNull(password);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailId, password));
            // 실제 SecurityContext 에 authentication 정보를 등록한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
