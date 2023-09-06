package kr.co.seedit.domain.hr.api;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.seedit.domain.hr.application.Payroll6InPageService;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class Payroll6InPageLocalApi {

    private final Payroll6InPageService payrollService;
//    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * local exeel file make test url
     * @param in
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/hr/payroll6InPageLocal")
	public ResponseEntity<ResponseDto> payrollPayroll(@RequestBody Map<String, Object> in, HttpServletResponse response) throws Exception {
		ResponseDto responseDto = payrollService.createPayroll6InPageLocal(in, response);
        
        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }
    
    @PostMapping("/hr/payroll6InPageLocalDowntest")
    public ResponseEntity<Resource> downloadERPIU(@RequestBody Map<String, Object> in, HttpServletResponse response) throws Exception {
        return ResponseEntity.ok()
                .header("Content-Transfer-Encoding", "binary")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Payroll6InPageLocalTest.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(payrollService.createPayroll6InPageLocalTest(in, response));
//                .body(reportService.downloadFile(reportParamsDto));
    }

}
