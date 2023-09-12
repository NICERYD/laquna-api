package kr.co.seedit.domain.hr.api;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.seedit.domain.hr.application.PayStubMailService;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PayStubMailApi {

//    private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final PayStubMailService payStubMailService;
	
	@PostMapping(value = "/hr/testLocalMail", produces = "text/plain;charset=UTF-8")
	public String sendEmail(@RequestBody ReportParamsDto reportParamsDto /*Map<String, Object> in*/) {
//		MailDto mailDto = new MailDto();
//		mailDto.setAddress(in.get("address"));
//		mailDto.setCcAddress(in.get("ccaddress"));
//		mailDto.setContent(in.get("content").toString());
//		mailDto.setTemplate(in.get("template").toString());
//		mailDto.setFrom(in.get("from").toString());
//		mailDto.setTo(in.get("to").toString());
		
//		payStubMailService.runEemailSender(mailDto);
//		payStubMailService.sendMailLocalTest(mailDto);
		
		return "do nothing";
	}

    @PostMapping("/hr/sendPayStubMailDH")
    public ResponseEntity<ResponseDto> sendPayStubMailDH(@RequestBody ReportParamsDto reportParamsDto, HttpServletResponse response) throws Exception {

    	ResponseDto responseDto = ResponseDto.builder().build();
    	
    	responseDto = payStubMailService.callPayStubMailSendDH(reportParamsDto);

    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    @PostMapping("/hr/sendPayStubMailApiDH")
    public ResponseEntity<ResponseDto> sendPayStubMailApiDH(@RequestBody ReportParamsDto reportParamsDto, HttpServletResponse response) throws Exception {

    	ResponseDto responseDto = ResponseDto.builder().build();
    	responseDto = payStubMailService.sendPayStubMailDH(reportParamsDto);
    	
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
