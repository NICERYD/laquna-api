package kr.co.seedit.domain.hr.application;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.seedit.domain.hr.dto.ReportParamsDto;

@Aspect
@Component
public class PayStubMailEventConfig {
	
	@Autowired
	PayStubMailService payStubMailService;

    @Pointcut(value="execution(public * kr.co.seedit.domain.hr.api.PayStubMailApi.sendPayStubMailDH(..))")
    public void callPayStubMailSendDHServiceExcution() { }
    
	@AfterReturning(pointcut = "callPayStubMailSendDHServiceExcution()", returning = "result")
	public void runPayStubMailSendDH(JoinPoint joinPoint, Object result) {

		payStubMailService.sendPayStubMailDH((ReportParamsDto)joinPoint.getArgs()[0]);
	}

}
