package kr.co.seedit.domain.commoncd.application;

import kr.co.seedit.domain.mapper.seedit.CommoncdDao;
import kr.co.seedit.domain.commoncd.dto.CommonCodeDetailDto;
import kr.co.seedit.domain.commoncd.dto.CommonCodeDto;
import kr.co.seedit.domain.company.application.CompanyService;
import kr.co.seedit.domain.mapper.seedit.CompanyDao;
import kr.co.seedit.domain.company.dto.CompanyDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommoncdService {

    private final CommoncdDao commoncdDao;
    private final CompanyDao companyDao;
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);


  /**
   * 공통코드 전체리스트 조회
   */
  @Transactional
  public List<CommonCodeDto> getCodeAllList() throws Exception {
	  
	  CompanyDto companyDto = new CompanyDto();
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      String userName = ((UserDetails) principal).getUsername();
      companyDto.setEmailId(userName);
      CompanyDto info = companyDao.selectTokenInfo(companyDto);
      Integer companyId = info.getCompanyId();
	  
    return commoncdDao.findCodeAllList(companyId);
  }

  /**
   * 공통코드 리스트 Search 목록
   */
  @Transactional
  public List<CommonCodeDto> getCommonCodelistSearch(CommonCodeDto commonCodeDto) throws Exception {
	  
	  CompanyDto companyDto = new CompanyDto();
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      String userName = ((UserDetails) principal).getUsername();
      companyDto.setEmailId(userName);
      CompanyDto info = companyDao.selectTokenInfo(companyDto);
      commonCodeDto.setCompanyId(info.getCompanyId());
	  
    return commoncdDao.commonCodelistSearch(commonCodeDto);
  }

  /**
   * 공통 모듈 리스트 조회
   */
  @Transactional
  public List<CommonCodeDto> getCommonModuleList() throws Exception {
    return commoncdDao.findCommonModuleList();
  }
    /**
   * 공통코드 SYS, COM 조회
   */
  @Transactional
  public ResponseDto getCodeDetailList(CommonCodeDetailDto commonCodeDetailDto) throws Exception {
      ResponseDto responseDto = ResponseDto.builder().build();
      Map<String, Object> data = new HashMap<String, Object>();
      List<CommonCodeDetailDto> codeSysList = new ArrayList();
      List<CommonCodeDetailDto> codeComList = new ArrayList();


      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      CompanyDto companyDto = new CompanyDto();
      String userName = ((UserDetails) principal).getUsername();
      companyDto.setEmailId(userName);
      CompanyDto info = companyDao.selectTokenInfo(companyDto);
      commonCodeDetailDto.setCompanyId(info.getCompanyId());
      List<CommonCodeDetailDto> commonCodeDetailDtoList = commoncdDao.findCodeDetailList(commonCodeDetailDto);

      for (CommonCodeDetailDto dto: commonCodeDetailDtoList){
            if(dto.getDefinedType().equals("SYS")){
                codeSysList.add(dto);
                data.put("codeDetailListSys", codeSysList);
            } else if(dto.getDefinedType().equals("COM")){
                codeComList.add(dto);
                data.put("codeDetailListCom", codeComList);
            } else {
                throw new CustomException(CustomException.ERR_3002);
            }
        }
      responseDto.setCode("200");
      responseDto.setData(data);
      return responseDto;
  }

   /***
   * 공통 Detail Code COM Add
   * @param
   * @return
   * @throws Exception
   */
    @Transactional
    public ResponseDto codeDetailComAdd(CommonCodeDetailDto commonCodeDetailDto) throws Exception {

    ResponseDto responseDto = ResponseDto.builder().build();

    CompanyDto companyDto = new CompanyDto();

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String userName = ((UserDetails) principal).getUsername();
    companyDto.setEmailId(userName);
    CompanyDto info = companyDao.selectTokenInfo(companyDto);
    commonCodeDetailDto.setCompanyId(info.getCompanyId());
    commonCodeDetailDto.setLastUpdatedBy(info.getUserId());
    try {
        List<CommonCodeDetailDto> commonCodeDetailResponse = commoncdDao.findCodeDetailList(commonCodeDetailDto);
        if (commonCodeDetailResponse != null && !commonCodeDetailResponse.equals("")) {
            commoncdDao.insertCodeDetailComAdd(commonCodeDetailDto);
            responseDto.setCode("200");
            responseDto.setSuccess(true);
        } else {
            throw new CustomException(CustomException.ERR_9999);
        }
    } catch (Exception e) {
        logger.error("Exception", e);
        throw e;
    }
      return responseDto;
  }

     /***
   * 공통 Detail Code COM Update
   * @param
   * @return
   * @throws Exception
   */
    @Transactional
    public ResponseDto codeDetailComUpdate(CommonCodeDetailDto commonCodeDetailDto) throws Exception {

    ResponseDto responseDto = ResponseDto.builder().build();

    CompanyDto companyDto = new CompanyDto();

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String userName = ((UserDetails) principal).getUsername();
    companyDto.setEmailId(userName);
    CompanyDto info = companyDao.selectTokenInfo(companyDto);
    commonCodeDetailDto.setCompanyId(info.getCompanyId());
    commonCodeDetailDto.setLastUpdatedBy(info.getUserId());
    try {
        List<CommonCodeDetailDto> commonCodeDetailResponse = commoncdDao.findCodeDetailList(commonCodeDetailDto);
        if (commonCodeDetailResponse != null && !commonCodeDetailResponse.equals("")) {
            commoncdDao.codeDetailComUpdate(commonCodeDetailDto);
            responseDto.setCode("200");
            responseDto.setSuccess(true);
        } else {
            throw new CustomException(CustomException.ERR_9999);
        }
    } catch (Exception e) {
        logger.error("Exception", e);
        throw e;
    }
      return responseDto;
  }

   /***
   * 공통 Detail Code COM Delete
   * @param
   * @return
   * @throws Exception
   */
    @Transactional
    public ResponseDto codeDetailComDelete(CommonCodeDetailDto commonCodeDetailDto) throws Exception {

    ResponseDto responseDto = ResponseDto.builder().build();

    try {
        commoncdDao.codeDetailComDelete(commonCodeDetailDto);
        responseDto.setCode("200");
        responseDto.setSuccess(true);
    } catch (Exception e) {
        logger.error("Exception", e);
        throw e;
    }
      return responseDto;
  }
}
