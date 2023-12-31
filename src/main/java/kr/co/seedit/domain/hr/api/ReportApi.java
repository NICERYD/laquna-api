package kr.co.seedit.domain.hr.api;

import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.seedit.domain.hr.application.ReportService;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReportApi {

    private final ReportService reportService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @PostMapping("/hr/downloadReport")
    public ResponseEntity<Resource> downloadERPIU(@RequestBody ReportParamsDto reportParamsDto, HttpServletResponse response) throws Exception {
    	ResponseEntity<Resource> result = reportService.downloadFile(reportParamsDto);
    	
        return result;
    }
    
//    @PostMapping("/hr/monthlyKeunTaeReport")
//    public ResponseEntity<ResponseDto> monthlyKeunTaeReport(@RequestBody MonthlyKeunTaeDto monthlyKeunTaeDto, HttpServletResponse response) throws Exception {
//        ResponseDto responseDto = reportService.monthlyKeunTaeReport(monthlyKeunTaeDto, response);
//        
//        return new ResponseEntity<>(responseDto
//                .builder()
//                .code("200").build(), HttpStatus.OK);
//    }
    
    
}
