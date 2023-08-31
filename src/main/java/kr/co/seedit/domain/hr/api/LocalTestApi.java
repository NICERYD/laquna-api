package kr.co.seedit.domain.hr.api;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.seedit.domain.hr.application.PayStubMailService;
import kr.co.seedit.domain.hr.dto.PayStubMailDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LocalTestApi {

//    private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final PayStubMailService payStubMailService;
	
    /**
     * local exeel file make test url
     * @param in
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/hr/testLocal")
	public ResponseEntity<ResponseDto> testLocal01(@RequestBody Map<String, Object> in, HttpServletResponse response) throws Exception {

//		ResponseDto responseDto = payrollService.createPayroll6InPageLocal(in, response);
    	
//    	JavaMailSender javaMailSender;
//    	MailBodyUtil mailBodyUtil = new MailBodyUtil();
//    	mailService.send(in.get("to").toString());
        
        return new ResponseEntity<>(ResponseDto
                .builder()
                .code("200").build(), HttpStatus.OK);
    }
    
	@PostMapping(value = "/hr/testLocalMail", produces = "text/plain;charset=UTF-8")
	public String sendEmail(@RequestBody PayStubMailDto mailDto /*Map<String, Object> in*/) {
//		MailDto mailDto = new MailDto();
//		mailDto.setAddress(in.get("address"));
//		mailDto.setCcAddress(in.get("ccaddress"));
//		mailDto.setContent(in.get("content").toString());
//		mailDto.setTemplate(in.get("template").toString());
//		mailDto.setFrom(in.get("from").toString());
//		mailDto.setTo(in.get("to").toString());

		
		payStubMailService.sendMail(mailDto);
		
//		mailDto.setHost("smtp.mailplug.co.kr");
//	    mailDto.setPort("465");
//	    mailDto.setUsername("euijin.ha@dhpic.co.kr");
//	    mailDto.setPassword("dhpic11!");
//		mailService.sendMail(mailDto);

//		mailDto.setHost("smtp.gmail.com");
//	    mailDto.setPort("587");
//	    mailDto.setUsername("lhkyu1@gmail.com");
//	    mailDto.setPassword("minreyqgmyazgfeh");
//	    mailDto.setSsl("false");
//		mailService.sendMail(mailDto);
		
		return "END";
	}


}
