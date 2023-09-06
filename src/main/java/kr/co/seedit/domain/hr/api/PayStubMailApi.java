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
public class PayStubMailApi {

//    private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final PayStubMailService payStubMailService;
	
	@PostMapping(value = "/hr/testLocalMail", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<ResponseDto> sendEmail(@RequestBody PayStubMailDto mailDto /*Map<String, Object> in*/) {
//		MailDto mailDto = new MailDto();
//		mailDto.setAddress(in.get("address"));
//		mailDto.setCcAddress(in.get("ccaddress"));
//		mailDto.setContent(in.get("content").toString());
//		mailDto.setTemplate(in.get("template").toString());
//		mailDto.setFrom(in.get("from").toString());
//		mailDto.setTo(in.get("to").toString());
		
		payStubMailService.sendMail(mailDto);
		
        return new ResponseEntity<>(ResponseDto
                .builder()
                .code("200").build(), HttpStatus.OK);

	}

}
