package kr.co.seedit.domain.hr.api;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.seedit.domain.hr.application.Payroll6InTableService;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class Payroll6InTableLocalApi {

    private final Payroll6InTableService payrollService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * local exeel file make test url
     * @param in
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/hr/payroll")
	public ResponseEntity<ResponseDto> payrollPayroll(
			@RequestBody Map<String, Object> in, HttpServletResponse response) throws Exception {
		ResponseDto responseDto = payrollService.createPayroll6InTableLocal(in, response);
        
        return new ResponseEntity<>(responseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }

}
