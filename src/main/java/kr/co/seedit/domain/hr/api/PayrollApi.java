package kr.co.seedit.domain.hr.api;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.seedit.domain.hr.application.PayrollService;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PayrollApi {

    private final PayrollService payrollService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
   
    @PostMapping("/hr/payroll")
	public ResponseEntity<ResponseDto> payrollPayroll(
			/* @RequestBody PayrollDto payrollDto, */ HttpServletResponse response) throws Exception {
    	String in = null;
		ResponseDto responseDto = payrollService.payrollReport(/* payrollDto*/in, response);
        
        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }
    
    @PostMapping("/hr/payrollFull")
	public ResponseEntity<ResponseDto> payrollFull(
			/* @RequestBody PayrollDto payrollDto, */ HttpServletResponse response) throws Exception {
    	String in = null;
		ResponseDto responseDto = payrollService.payrollReportFull(/* payrollDto*/in, response);
        
        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }
}