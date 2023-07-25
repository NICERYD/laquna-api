package kr.co.seedit.global.common.application;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import kr.co.seedit.domain.mapper.seedit.CompanyDao;
import kr.co.seedit.domain.company.dto.CompanyDto;
import kr.co.seedit.global.error.exception.CustomException;
import kr.co.seedit.global.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommonService {
  private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

  private final CompanyDao companyDao;
  private final JwtTokenUtil jwtTokenUtil;

  public Claims getClaims(HttpServletRequest request) throws Exception {
    String requestTokenHeader = request.getHeader("Authorization");
    Claims claims = null;
    if (requestTokenHeader == null) {
      claims = null;
    } else {
      String jwtToken = requestTokenHeader.substring(7);
      try {
        claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
      } catch (IllegalArgumentException e) {
        logger.debug("Unable to get JWT Token");
      } catch (ExpiredJwtException e) {
        logger.debug("JWT Token has expired");
      }
    }

    return claims;
  }

      /***
   * 보안 체크
   *
   * @param gbn            사용할구분
   * @param emailId 암화화된 사용장 id
   * @param nowPassword    신규 패스워드
   * @return
   * @throws Exception 에러 코드와 메시지를 변경이 가능해서 내부적으로 pwdAuth -- L90 : 로그인 90일 -- P90
   *                   : 패스워드 90일
   */
  @Transactional
  public Map<String, Object> getSecurityCk(String gbn, String emailId, String nowPassword) throws Exception {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    CompanyDto companyDto = companyDao.selectFindByUsername(emailId);
    boolean flag = true;

    if (companyDto == null) {
      String errorCode = CustomException.ERR_8999.split(",")[0];
      String errorMessage = CustomException.ERR_8999.split(",")[1];
      returnMap.put("code", errorCode);
      returnMap.put("message", errorMessage);
      flag = false;
    }

// TODO 향후 개발 예정
//    if (gbn.equals("login")) {
//      if (flag) {
//        // 로그인 5회 이상
//        int loginFailCnt = userDto.getLoginFailCnt();
//        if (loginFailCnt > 4) {
//          // 요청시간 String
//          String reqDateStr = userDto.getLoginFailAt();
//          DateUtils dateUtils = new DateUtils();
//          long minute = dateUtils.getDateMminuteBetween(reqDateStr);
//          if (minute > 9) {
//            // 패스워드 5회이상 초기화
//            UserDto userVoFailCnt = userDao.selectFindByUsername(emailId);
//            userVoFailCnt.setLoginFailCnt(0);
//            userDao.updateInternalLoginFailCnt(userVoFailCnt);
//            flag = true;
//          } else {
//            String errorCode = CustomException.ERR_8978.split(",")[0];
//            String errorMessage = CustomException.ERR_8978.split(",")[1];
//            returnMap.put("code", errorCode);
//            returnMap.put("message", errorMessage);
//            flag = false;
//          }
//        }
//      }
//    }
    if (flag) {
      returnMap.put("code", "200");
      returnMap.put("message", "");
    }

    return returnMap;
  }

}
