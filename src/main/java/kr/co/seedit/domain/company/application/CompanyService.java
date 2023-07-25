package kr.co.seedit.domain.company.application;

import kr.co.seedit.domain.mapper.seedit.CompanyDao;
import kr.co.seedit.domain.company.dto.CompanyDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class CompanyService {
  private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

  private final CompanyDao companyDao;


   /***
   * 회원가입
   * @param companyDto
   * @return
   * @throws Exception
   */
    @Transactional
    public ResponseDto signupCompany(CompanyDto companyDto) throws Exception {

    ResponseDto responseDto = ResponseDto.builder().build();

    try {
        CompanyDto companyResponse = companyDao.signUpCheck(companyDto);
        if (companyResponse == null) {
            String password = new BCryptPasswordEncoder().encode(companyDto.getPassword());
            companyDto.setPassword(password);
            companyDto.setSalt(password.split("\\$")[3].substring(0, 22));
            companyDao.insertUserTbJoinInfo(companyDto);
            companyDao.insertCompanyTbJoinInfo(companyDto);
            companyDao.insertApproveTbJoinInfo(companyDto);
            companyDao.insertPasswordTbJoinInfo(companyDto);
            responseDto.setCode("200");
            responseDto.setMessage("계정신청이 완료되었습니다. 승인 후 사용가능합니다.");
            responseDto.setSuccess(true);
        } else {
            if (companyResponse.getApproveStatus().equals("00")) {
                throw new CustomException(CustomException.ERR_0001);
            } else if (companyResponse.getApproveStatus().equals("01")) {
                throw new CustomException(CustomException.ERR_0002);
            }
        }
    } catch (Exception e) {
        logger.error("Exception", e);
        throw e;
    }
      return responseDto;
  }

    @Transactional(readOnly = true)
    public ResponseDto userIdChk(CompanyDto companyDto, HttpServletRequest request) throws CustomException {
      ResponseDto responseDto = ResponseDto.builder().build();
      boolean flagLogin = true;
      String message = "";

      if (companyDao.selectIdCnt(companyDto) > 0) {
        flagLogin = false;
        message = CustomException.ERR_9996.split(",")[1];
      }
      responseDto.setSuccess(flagLogin);
      responseDto.setMessage(message);
      return responseDto;
    }

}


