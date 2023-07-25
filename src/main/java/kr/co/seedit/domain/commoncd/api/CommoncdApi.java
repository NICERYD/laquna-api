package kr.co.seedit.domain.commoncd.api;

import kr.co.seedit.domain.commoncd.application.CommoncdService;
import kr.co.seedit.domain.commoncd.dto.CommonCodeDetailDto;
import kr.co.seedit.domain.commoncd.dto.CommonCodeDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class CommoncdApi {

    private final CommoncdService commoncdService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * 전체공통코드 리스트 목록
   *
   * @return
   * @throws Exception
   */
  @PostMapping(value = "/api/v1/common/getcodelist")
  public ResponseEntity<ResponseDto> getCodeList() throws Exception {
    log.info("Call [/api/v1/common/getcodelist]");
//    ResponseDto responseDto = commoncdService.getCodeList(commonCodeDto);
    return new ResponseEntity<>(ResponseDto
            .builder()
            .code("200")
            .data(commoncdService.getCodeAllList()).build(), HttpStatus.OK);
  }

   /**
   * 전체공통코드 리스트 Search 목록
   *
   * @return
   * @throws Exception
   */
  @PostMapping(value = "/api/v1/common/getCommonCodelistSearch")
  public ResponseEntity<ResponseDto> getCommonCodelistSearch(@RequestBody CommonCodeDto commonCodeDto) throws Exception {
    log.info("commonCodeDto : {}", commonCodeDto.toString());
    return new ResponseEntity<>(ResponseDto
            .builder()
            .code("200")
            .data(commoncdService.getCommonCodelistSearch(commonCodeDto)).build(), HttpStatus.OK);
  }

    /**
   * 공통모듈 리스트 목록
   *
   * @return
   * @throws Exception
   */
  @PostMapping(value = "/api/v1/common/getCommonModuleList")
  public ResponseEntity<ResponseDto> getCommonModuleList() throws Exception {
    log.info("Call [/api/v1/common/getCommonModuleList]");
    return new ResponseEntity<>(ResponseDto
            .builder()
            .code("200")
            .data(commoncdService.getCommonModuleList()).build(), HttpStatus.OK);
  }

    /**
   * 전체공통코드Detail 리스트 목록
   *
   * @return
   * @throws Exception
   */
  @PostMapping(value = "/api/v1/common/getcodeDetaillist")
  public ResponseEntity<ResponseDto> getCodeDetailList(HttpServletRequest request, @RequestBody CommonCodeDetailDto commonCodeDetailDto) throws Exception {
    log.info("commonCodeDetailDto : {}", commonCodeDetailDto.toString());
    ResponseDto responseDto = commoncdService.getCodeDetailList(commonCodeDetailDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);

  }

    /**
   * 공통코드 COM Detail 리스트 추가
   *
   * @return
   * @throws Exception
   */
  @PostMapping(value = "/api/v1/common/codeDetailComAdd")
  public ResponseEntity<ResponseDto> codeDetailComAdd(@RequestBody CommonCodeDetailDto commonCodeDetailDto) throws Exception {
    log.info("commonCodeDetailDto : {}", commonCodeDetailDto.toString());
    ResponseDto responseDto = commoncdService.codeDetailComAdd(commonCodeDetailDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

   /**
   * 공통코드 COM Detail 리스트 수정
   *
   * @return
   * @throws Exception
   */
  @PostMapping(value = "/api/v1/common/codeDetailComUpdate")
  public ResponseEntity<ResponseDto> codeDetailComUpdate(@RequestBody CommonCodeDetailDto commonCodeDetailDto) throws Exception {
    log.info("commonCodeDetailDto : {}", commonCodeDetailDto.toString());
    ResponseDto responseDto = commoncdService.codeDetailComUpdate(commonCodeDetailDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
  
   /**
   * 공통코드 COM Detail 리스트 삭제
   *
   * @return
   * @throws Exception
   */
  @PostMapping(value = "/api/v1/common/codeDetailComDelete")
  public ResponseEntity<ResponseDto> codeDetailComDelete(@RequestBody CommonCodeDetailDto commonCodeDetailDto, HttpServletRequest request) throws Exception {
    log.info("commonCodeDetailDto : {}", commonCodeDetailDto.toString());
    ResponseDto responseDto = commoncdService.codeDetailComDelete(commonCodeDetailDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
