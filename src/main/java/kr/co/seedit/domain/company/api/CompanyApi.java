package kr.co.seedit.domain.company.api;

import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;

import kr.co.seedit.domain.company.application.CompanyService;
import kr.co.seedit.domain.company.dto.CompanyDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CompanyApi {

  private final CompanyService companyService;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * 회원 가입 신청
   *
   * @return JsonView
   * @throws Exception 메뉴명 : 회원 가입 > 회원 가입등록
   *                   기능 : 생성
   */
  @PostMapping(value = "/api/v1/join/signup")
  public ResponseEntity<ResponseDto> signupCompany(@RequestBody CompanyDto companyDto, HttpServletRequest request) throws Exception {
    log.info("userDto : {}", companyDto.toString());
    ResponseDto responseDto = companyService.signupCompany(companyDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * Company ID 중복확인
   *
   * @return JsonView
   */
  @PostMapping(value = "/api/v1/join/companyidchk")
  public ResponseEntity<ResponseDto> userIdChk(@RequestBody CompanyDto companyDto, HttpServletRequest request) throws Exception {
    log.info("companyDto : {}", companyDto.toString());
    ResponseDto responseDto = companyService.userIdChk(companyDto, request);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 휴대폰 인증번호 받기
   *
   * @return JsonView
   */
//  @PostMapping(value = "/api/v1/join/usertelsend")
//  @ApiOperation(value = "휴대폰 인증번호 받기 ", notes = "휴대폰 인증번호 받기할 때 사용한다")
//  @ApiImplicitParams({
//  @ApiImplicitParam(name = "userVo", value = "휴대폰 인증번호 받기 VO", required = false)
//  })
//  @ApiResponses({@ApiResponse(code = 200, message = "생성 성공"),
//  @ApiResponse(code = 500, message = "서버 에러입니다.", response = ResponseVo.class, responseContainer = "responseVo")
//  })
//  public ResponseEntity<ResponseVo> userTel(@RequestBody UserVo userVo) throws Exception {
//    log.info("userVo : {}", userVo.toString());
//    ResponseVo responseVo = userService.userNumChk(userVo);
//    return new ResponseEntity<>(responseVo, HttpStatus.OK);
//
//  }

  /**
   * 휴대폰 인증번호 확인
   *
   * @return JsonView
   */
//  @ApiIgnore
//  @PostMapping(value = "/api/v1/join/usertelchk")
//  @ApiOperation(value = "휴대폰 인증번호 확인 ", notes = "휴대폰 인증번호 확인할 때 사용한다")
//  @ApiImplicitParams({
//  @ApiImplicitParam(name = "userVo", value = "휴대폰 인증번호 확인VO", required = false)
//  })
//  @ApiResponses({@ApiResponse(code = 200, message = "생성 성공"),
//  @ApiResponse(code = 500, message = "서버 에러입니다.", response = ResponseVo.class, responseContainer = "responseVo")
//  })
//  public ResponseEntity<ResponseVo> userTelChk(@RequestBody UserVo userVo) throws Exception {
//    log.info("userVo : {}", userVo.toString());
//    ResponseVo responseVo = userService.userNumChk(userVo);
//    return new ResponseEntity<>(responseVo, HttpStatus.OK);
//  }

  /**
   * 에미일 중복확인
   *
   * @return JsonView
   */
//  @PostMapping(value = "/api/v1/join/useremailchk")
//  @ApiOperation(value = "에미일 중복확인 ", notes = "에미일 중복확인할 때 사용한다")
//  @ApiImplicitParams({
//  @ApiImplicitParam(name = "userVo", value = "에미일 중복확인VO", required = false)
//  })
//  @ApiResponses({@ApiResponse(code = 200, message = "생성 성공"),
//  @ApiResponse(code = 500, message = "서버 에러입니다.", response = ResponseVo.class, responseContainer = "responseVo")
//  })
//  public ResponseEntity<ResponseVo> userEmailChk(@RequestBody UserVo userVo, HttpServletRequest request) throws Exception {
//    log.info("userVo : {}", userVo.toString());
//    ResponseVo responseVo = userService.userEmailChk(userVo);
//    return new ResponseEntity<>(responseVo, HttpStatus.OK);
//  }

  /**
   * 사업자등록 번호 인증번호 받기
   *
   * @return JsonView
   */
//  @ApiIgnore
//  @PostMapping(value = "/api/v1/partnercenter/partnerbusinessnumchk")
//  public ResponseEntity<ResponseVo> partnerBusinessNumChk(@RequestBody PartnerVo partnerVo) throws Exception {
//
//    log.info("PartnerVo : {}", partnerVo.toString());
//
//    ResponseVo responseVo = ResponseVo.builder()
//      .build();
//
//    return new ResponseEntity<>(responseVo, HttpStatus.OK);
//  }


  /**
   * 로그인 일자 now업데이트
   *
   * @return JsonView
   */
//  @PostMapping(value = "/api/v1/updateloginat")
//  @ApiOperation(value = "로그인 일자 업데이트 ", notes = "로그인 일자 업데이트할 때 사용한다")
//  @ApiImplicitParams({
//  @ApiImplicitParam(name = "fVo", value = "로그인 일자 업데이트VO", required = false)
//  })
//  @ApiResponses({@ApiResponse(code = 200, message = "생성 성공"),
//  @ApiResponse(code = 500, message = "서버 에러입니다.", response = ResponseVo.class, responseContainer = "responseVo")
//  })
//  public ResponseEntity<ResponseVo> updateLoginAt(@RequestBody FindVo fVo, HttpServletRequest request) throws Exception {
//    log.info("updateLoginAt : {}", fVo.toString());
//    ResponseVo responseVo = userService.updateLoginAt(fVo, request);
//    return new ResponseEntity<>(responseVo, HttpStatus.OK);
//  }

  /**
   * 아이디 비밀번호찾기
   *
   * @return JsonView
   * @throws Exception 메뉴명 : 로그인하기 > 파트너 로그인 > 파트너 아이디찾기 / 비밀번호 찾기
   *                   기능 : 조회
   */
//  @PostMapping(value = "/api/v1/findpartneridpwd")
//  @ApiOperation(value = "아이디 비밀번호찾기", notes = "아이디 비밀번호찾기할 때 사용한다")
//  @ApiImplicitParams({
//  @ApiImplicitParam(name = "fVo", value = "아이디 비밀번호찾기VO", required = false)
//  })
//  @ApiResponses({@ApiResponse(code = 200, message = "생성 성공"),
//  @ApiResponse(code = 500, message = "서버 에러입니다.", response = ResponseVo.class, responseContainer = "responseVo")
//  })
//  public ResponseEntity<ResponseVo> findParnterIdPwd(@RequestBody FindVo fVo, HttpServletRequest request) throws Exception {
//    log.info("findParnterIdPwd : {}", fVo.toString());
//    ResponseVo responseVo = userService.findParnterIdPwd(fVo, request);
//    commonService.insertAccessFormLog(fVo, responseVo, 1);
//    return new ResponseEntity<>(responseVo, HttpStatus.OK);
//  }

  /**
   * 비밀번호 변경(임시비번으로 로그인햇을때)
   *
   * @return JsonView
   */
//  @ApiIgnore
//  @PostMapping(value = "/api/v1/pwdupdatetemporary")
//  public ResponseEntity<ResponseVo> pwdUpdateTemporary(@RequestBody FindVo fVo, HttpServletRequest request) throws Exception {
//    log.info("pwdUpdateTemporary : {}", fVo.toString());
//    ResponseVo responseVo = userService.pwdUpdateTemporary(fVo, request);
//    return new ResponseEntity<>(responseVo, HttpStatus.OK);
//  }

  /**
   * 비밀번호 변경
   *
   * @return JsonView
   */
//  @PostMapping(value = "/api/v1/pwdupdate")
//  @ApiOperation(value = "비밀번호 변경", notes = "비밀번호 변경할 때 사용한다")
//  @ApiImplicitParams({
//  @ApiImplicitParam(name = "fVo", value = "비밀번호 변경VO", required = false)
//  })
//  @ApiResponses({@ApiResponse(code = 200, message = "생성 성공"),
//  @ApiResponse(code = 500, message = "서버 에러입니다.", response = ResponseVo.class, responseContainer = "responseVo")
//  })
//  public ResponseEntity<ResponseVo> pwdUpdate(@RequestBody FindVo fVo, HttpServletRequest request) throws Exception {
//    log.info("FindVo : {}", fVo.toString());
//    ResponseVo responseVo = userService.pwdUpdate(fVo, request);
//    return new ResponseEntity<>(responseVo, HttpStatus.OK);
//  }

}

