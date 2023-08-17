package kr.co.seedit.domain.hr.api;

import kr.co.seedit.domain.hr.application.ReportService;
import kr.co.seedit.domain.hr.application.SalaryService;
import kr.co.seedit.domain.hr.dto.BasicSalaryDto;
import kr.co.seedit.domain.hr.dto.MonthlyKeunTaeDto;
import kr.co.seedit.domain.hr.dto.ReportPayrollDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReportApi {

    private final ReportService reportService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @PostMapping("/hr/downloadSalaryExcel")
    public ResponseEntity<ResponseDto> downloadSalaryExcel(@RequestBody BasicSalaryDto basicSalaryDto, HttpServletResponse response) throws Exception {
        ResponseDto responseDto = reportService.downloadSalaryExcel(basicSalaryDto, response);
        
        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }
    
    @PostMapping("/hr/salaryReport")
    public ResponseEntity<ResponseDto> downloadSalaryExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResponseDto responseDto = reportService.salaryReport(request, response);

        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }
    
//    @PostMapping("/hr/monthlyKeunTaeReport")
//    public ResponseEntity<ResponseDto> monthlyKeunTaeReport(@RequestBody MonthlyKeunTaeDto monthlyKeunTaeDto, HttpServletResponse response) throws Exception {
//        ResponseDto responseDto = reportService.monthlyKeunTaeReport(monthlyKeunTaeDto, response);
//        
//        return new ResponseEntity<>(responseDto
//                .builder()
//                .code("200").build(), HttpStatus.OK);
//    }
    
    @PostMapping("/hr/payrollReport")
    public ResponseEntity<ResponseDto> payrollReport(@RequestBody ReportPayrollDto reportPayrollDto, HttpServletResponse response) throws Exception {
        ResponseDto responseDto = reportService.payrollReport(reportPayrollDto, response);
        
        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }
}