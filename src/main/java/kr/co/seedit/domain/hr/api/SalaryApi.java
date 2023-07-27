package kr.co.seedit.domain.hr.api;

import kr.co.seedit.domain.hr.application.SalaryService;
import kr.co.seedit.domain.hr.dto.BasicSalaryDto;
import kr.co.seedit.domain.hr.dto.EmployeeInformationDto;
import kr.co.seedit.domain.hr.dto.ErpIUDto;
import kr.co.seedit.global.common.dto.RequestDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SalaryApi {
    private final SalaryService salaryService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostMapping("hr/getCompanyList")
    public ResponseEntity<ResponseDto> getCompanyList() throws Exception{
    	
    	return new ResponseEntity<>(ResponseDto
    			.builder()
    			.code("200")
    			.data(salaryService.getCompanyList()).build(), HttpStatus.OK);
    }
    
    @PostMapping("hr/getBusinessList")
    public ResponseEntity<ResponseDto> getBusinessList() throws Exception{
    	
    	return new ResponseEntity<>(ResponseDto
    			.builder()
    			.code("200")
    			.data(salaryService.getBusinessList()).build(), HttpStatus.OK);
    }

    @PostMapping("hr/getAdtList")
    public ResponseEntity<ResponseDto> getAdtList(@RequestBody EmployeeInformationDto employeeInformationDto) throws Exception{
        log.info("employeeInformationDto : {}", employeeInformationDto.toString());

    	return new ResponseEntity<>(ResponseDto
    			.builder()
    			.code("200")
    			.data(salaryService.getAdtList(employeeInformationDto)).build(), HttpStatus.OK);
    }

    /**
     * 급여계산 목록 조회
     *
     * @return
     * @throws Exception
     */
    @PostMapping("hr/getCalcSalaryList")
    public ResponseEntity<ResponseDto> getCalcSalaryList(@RequestBody BasicSalaryDto basicSalaryDto) throws Exception{
        log.info("basicSalaryDto : {}", basicSalaryDto.toString());

        return new ResponseEntity<>(ResponseDto
                .builder()
                .code("200")
                .data(salaryService.getCalcSalaryList(basicSalaryDto)).build(), HttpStatus.OK);
    }

    @PostMapping("/hr/uploadADTExcel")
    public ResponseEntity<ResponseDto> uploadADTExcel(@RequestParam(value="file01" , required=false)MultipartFile adtExcel01
            , @RequestParam(value="file02", required=false)MultipartFile adtExcel02 , @RequestParam(value="yyyymm", required=false)String yyyymm, HttpServletRequest request ,ErpIUDto.RequestDto erpIUDto ) throws IOException, InvalidFormatException {
        ResponseDto responseDto = salaryService.uploadADTExcel(adtExcel01, adtExcel02, yyyymm, erpIUDto, request);
        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }

    @PostMapping("/hr/calcSalary")
    public ResponseEntity<ResponseDto> calcSalary(@RequestBody RequestDto requestDto, HttpServletRequest request ) throws IOException, InvalidFormatException {
        ResponseDto responseDto = salaryService.calcSalary(requestDto);
        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }
    
    @PostMapping("/hr/downloadSalaryExcel")
    public ResponseEntity<ResponseDto> downloadSalaryExcel(@RequestBody BasicSalaryDto basicSalaryDto, HttpServletResponse response) throws Exception {
        ResponseDto responseDto = salaryService.downloadSalaryExcel(basicSalaryDto, response);
        
        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }
}
