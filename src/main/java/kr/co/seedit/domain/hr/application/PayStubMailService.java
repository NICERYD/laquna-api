package kr.co.seedit.domain.hr.application;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import kr.co.seedit.domain.hr.dto.PayStubMailHistDto;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;
import kr.co.seedit.domain.hr.dto.ReportPayrollDto;
import kr.co.seedit.domain.mapper.seedit.DHPaystubmailHistDao;
import kr.co.seedit.domain.mapper.seedit.ReportDao;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.error.exception.CustomException;
import kr.co.seedit.global.utils.Aes256;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PayStubMailService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final byte[] iv = "0123456789abcdef".getBytes();

	private final String encString = "UTF-8";
	
	private final JavaMailSender emailSender;

	private final ReportDao reportDao;
	private final DHPaystubmailHistDao paystubmailHistDao;
	private final String DH_MAIL_STATUS_REQUEST = "요청";
	private final String DH_MAIL_STATUS_SUCCESS = "전송완료";
	private final String DH_MAIL_STATUS_FAIL = "전송실패";
	private final String DH_MAIL_STATUS_NODATA = "정보없음";
	private final String DH_MAIL_STATUS_SELFAIL = "조회실패";
	private final String DH_MAIL_STATUS_NOEMAIL = "이메일없음";


	/**
	 * 메일전송 요청 저장
	 * @param reportParamsDto
	 * @return
	 */
	public ResponseDto saveRequestPayStubMailSendDH(ReportParamsDto reportParamsDto) {
		ResponseDto responseDto = ResponseDto.builder().build();

		PayStubMailHistDto paystubmailHistDto = new PayStubMailHistDto();
		paystubmailHistDto.setBaseYyyymm(reportParamsDto.getYyyymm());
		paystubmailHistDto.setCompanyId(reportParamsDto.getCompanyId());
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		paystubmailHistDto.setLastRequestEmailId(((UserDetails) principal).getUsername());
		paystubmailHistDto.setLastStatus(DH_MAIL_STATUS_REQUEST);
		Date today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		paystubmailHistDto.setLastUpdatedDate(dateFormat.format(today));

		int failCount = 0;
		for (String employeeNumber : reportParamsDto.getEmployeeNumberList()) {
			paystubmailHistDto.setEmployeeNumber(employeeNumber);
			try {
				// 메일전송 이력테이블에 저장
				if (0 == paystubmailHistDao.insertDHPaystubmailHist(paystubmailHistDto)) {
					failCount++;
					System.out.println("paystubmail history insert fail. "+paystubmailHistDto.toString());
					log.error("paystubmail history insert fail. "+paystubmailHistDto.toString());
				}
			} catch (Exception e) {
				System.out.println("paystubmail history insert Exception:"+e.getLocalizedMessage());
				log.error("paystubmail history insert Exception:"+e.getLocalizedMessage());
			}
		}
		
		if (0 < failCount) {
			responseDto.setSuccess(false);
			responseDto.setMessage(""+failCount+"건에 대한 요청에 예외 상황이 발생했습니다. 잠시 후 처리 결과를 확인해주세요.");
		} else {
			responseDto.setSuccess(true);
		}
		
		return responseDto;
	}

	@Async
	public ResponseDto sendAsyncPayStubMailDH(ReportParamsDto reportParamsDto) {
		return sendPayStubMailDH(reportParamsDto);
	}

	/**
	 * 실시간 급여명세서 메일전송 요청 목록 일괄전송
	 * @param reportParamsDto
	 * @return
	 */
	public ResponseDto sendPayStubMailDH(ReportParamsDto reportParamsDto) {

		if (null == reportParamsDto.getEmployeeNumberList()
				|| 0 == reportParamsDto.getEmployeeNumberList().size()) {
			throw new CustomException("선택된 사용자가 없습니다");
		}

		// 처리결과 응답값
		ResponseDto responseDto = ResponseDto.builder().build();
		int failCount = 0;
		
		// 이력결과 저장
		PayStubMailHistDto paystubmailHistDto = new PayStubMailHistDto();
		paystubmailHistDto.setBaseYyyymm(reportParamsDto.getYyyymm());
		paystubmailHistDto.setCompanyId(reportParamsDto.getCompanyId());

		for (String employeeNumber : reportParamsDto.getEmployeeNumberList()) {

			ReportPayrollDto data = null;
			boolean isFail = false;
			try {
				paystubmailHistDto.setEmployeeNumber(employeeNumber);
				reportParamsDto.setEmployeeNumber(employeeNumber);
				// getData
				data = reportDao.findPayStubForMail(reportParamsDto);
				if (null == data) {
					failCount++;
					log.info("No data. companyId=" + reportParamsDto.getCompanyId()
							+ ", employeeNumber=" + employeeNumber
							+ ", yyyymm=" + reportParamsDto.getYyyymm());
					isFail = true;
					paystubmailHistDto.setLastStatus(DH_MAIL_STATUS_NODATA);
				}
				else if (null == data.getEmailAddress() || "".equals(data.getEmailAddress())) {
					failCount++;
					log.info("No data. companyId=" + reportParamsDto.getCompanyId()
							+ ", employeeNumber=" + employeeNumber
							+ ", yyyymm=" + reportParamsDto.getYyyymm());
					isFail = true;
					paystubmailHistDto.setLastStatus(DH_MAIL_STATUS_NOEMAIL);
				}
				else if (null == data.getResidentRegistrationNumber() || "".equals(data.getResidentRegistrationNumber())) {
					failCount++;
					log.info("No data. companyId=" + reportParamsDto.getCompanyId()
							+ ", employeeNumber=" + employeeNumber
							+ ", yyyymm=" + reportParamsDto.getYyyymm());
					isFail = true;
					paystubmailHistDto.setLastStatus(DH_MAIL_STATUS_NODATA);
				}
				else if (null == data.getYyyymm()) {
					data.setYyyymm(reportParamsDto.getYyyymm());
				}

			} catch (DataAccessException e) {
				log.error("User information select error occurred. companyId=" + reportParamsDto.getCompanyId()
						+ ", employeeNumber=" + employeeNumber
						+ ", yyyymm=" + reportParamsDto.getYyyymm());
				failCount++;
				isFail = true;
				paystubmailHistDto.setLastStatus(DH_MAIL_STATUS_SELFAIL);
				e.printStackTrace();
			}

			if (false == isFail) {
				// setting mail details
				List<String> address = new ArrayList<>();
				List<String> ccAddress = null; //new ArrayList<>();

				String subject = reportParamsDto.getYyyymm().substring(0, 4)+"년 "+reportParamsDto.getYyyymm().substring(4, 6)+"월 급여 명세서 입니다";
				String content = "<br>"
					+ "* 본 보안 메일은 개인정보 보호를 위하여 암호화있습니다.<br>"
					+ "* 암호화된 첨부파일은 인터넷이 연결된 환경에서 확인이 가능합니다.<br>"
					+ "* 다운로드한 첨부파일에 비밀번호 (생년월일 6자리)를 입력하시면 내용을 확인하실 수 있습니다.";
				String attachmentName = "securityMail.html";
				String attachmentBody = getAttachmentBodyDH(data);

				
				address.add(data.getEmailAddress()); // real logic
				//TODO:: 로컬 테스트 시 이메일 정보 변경로직 필요
//				address.clear(); address.add("lhkyu@naver.com");
//				attachmentName = data.getResidentRegistrationNumber()+".html";

				// 메일전송
				String errMsg = this.runJavaMailSender(
						address,
						ccAddress,
						subject,
						content,
						attachmentName,
						attachmentBody
						);

				if ("".equals(errMsg)) {
					paystubmailHistDto.setLastStatus(DH_MAIL_STATUS_SUCCESS);
					Date today = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					paystubmailHistDto.setLastSuccessDate(dateFormat.format(today));
				} else {
					isFail = true;
					paystubmailHistDto.setLastStatus(DH_MAIL_STATUS_FAIL);
					log.info("runJavaMailSender() fail. " +  errMsg
							+ " companyId=" + reportParamsDto.getCompanyId()
							+ ", employeeNumber=" + reportParamsDto.getEmployeeNumber()
							+ ", yyyymm=" + reportParamsDto.getYyyymm());
					failCount++;
				}
			}
			
			try {
				// 메일전송 결과를 이력테이블에 저장
				int ret = paystubmailHistDao.updateDHPaystubmailHist(paystubmailHistDto);
				if (0 == ret) {
					System.out.println("paystubmail history insert fail. "+paystubmailHistDto.toString());
					log.error("paystubmail history insert fail. "+paystubmailHistDto.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Mail send history save fail. companyId=" + reportParamsDto.getCompanyId()
				+ ", employeeNumber=" + employeeNumber
				+ ", yyyymm=" + reportParamsDto.getYyyymm()
				+ " Exception:"+ e.getMessage());
			}
		}

		if (0 < failCount) {
			responseDto.setCode("9998");
			responseDto.setMessage("성공 "+reportParamsDto.getEmployeeNumberList()+"건, 실패 " + failCount +"건");
		}

		log.info("MailSend "+reportParamsDto.getEmployeeNumberList()
				+ (0 < failCount ? " 실패 " + failCount +"건":""));
		return responseDto;
	}

	@Value("${spring.mail.username}")
	String from;
	/**
	 * 이메일 전송
	 * @param address
	 * @param ccAddress
	 * @param subject
	 * @param content
	 * @param attachmentName
	 * @param attachmentBody
	 * @return 에러메시지
	 */
	private String runJavaMailSender(
			List<String> address,
			List<String> ccAddress,
			String subject,
			String content,
			String attachmentName,
			String attachmentBody
			) {

		MimeMessage message = null;
        MimeMessageHelper helper = null;
		try {
			message = emailSender.createMimeMessage();
	        helper = new MimeMessageHelper(message, true, encString);
	        helper.setFrom(from);
		} catch (SecurityException e) {
			log.error("Default setting fail. SecurityException: "+e.getMessage());
			e.printStackTrace();
			return "Default setting fail";
			//throw new CustomException("Default setting fail");
		} catch (MessagingException e) {
			log.error("Default setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			return "Default setting fail";
			//throw new CustomException("Default setting fail");
		}

		try {
			//메일 제목 설정
			if (!"".equals(subject)) {
				helper.setSubject(subject);
			} else {
				//helper.setSubject("NoTitle");
				return "subject is empty";
				//throw new CustomException("subject is empty");
			}
		} catch (MessagingException e) {
			log.error("Subject setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			return "subject setting fail";
			//throw new CustomException("subject setting fail");
		}

		try {
			//수신자 설정
			if (null != address && 0 < address.size()) {
				String[] to = new String[address.size()];
				for(int i=0;i<address.size();i++) {
					to[i] = address.get(i);
				}
				helper.setTo(to);
			} else {
				return "Invalid address";
				//throw new CustomException("Invalid address");
			}
			//참조자 설정
			if (null != ccAddress && 0 < ccAddress.size()) {
				String[] cc = new String[ccAddress.size()];
				for(int i=0;i<ccAddress.size();i++) {
					cc[i] = ccAddress.get(i);
				}
				helper.setCc(cc);
			}
		} catch (MessagingException e) {
			log.error("Receiver setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			return "Receiver setting fail";
			//throw new CustomException("Receiver setting fail");
		}

		try {
	        //메일 내용 설정
			if (!"".equals(content)) {
				helper.setText(content, true);
			}
		} catch (MessagingException e) {
			log.error("content setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			return "content setting fail";
			//throw new CustomException("content setting fail");
		}

		try {
			if (!"".equals(attachmentBody)) {
				helper.addAttachment(attachmentName, new ByteArrayResource(attachmentBody.getBytes(),encString));
			}
		} catch (SecurityException e) {
			log.error("Attachment fail. Permission check. SecurityException:"+e.getMessage());
			e.printStackTrace();
			return "Attachment fail. Permission check";
			//throw new CustomException("Attachment fail. Permission check");
		} catch (IllegalStateException e) {
			log.error("Attachment fail. IllegalStateException: "+e.getMessage());
			e.printStackTrace();
			return "Attachment fail. File State check";
			//throw new CustomException("Attachment fail. File State check");
		} catch (MessagingException e) {
			log.error("Attachment fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			return "Attachment fail";
			//throw new CustomException("Attachment fail");
		}

		try {
	        //메일 보내기
	        emailSender.send(message);

		} catch (MailAuthenticationException e) {
			log.error("Email authentication fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			return "Email authentication fail";
			//throw new CustomException("Email authentication fail");
		} catch (MailSendException e) {
			log.error("Email send fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			return "Email send fail";
			//throw new CustomException("Email send fail");
		} catch (MailException e) {
			log.error("Email send() error. MessagingException: "+e.getMessage());
			e.printStackTrace();
			return "Email send() error";
			//throw new CustomException("Email send() error");
		}

		return "";
	}

	/**
	 * 첨부파일 내용 생성
	 * @param data
	 * @return
	 */
	private String getAttachmentBodyDH(ReportPayrollDto data) {

			String key = data.getResidentRegistrationNumber();
			String birth = "19"+key.substring(0,2)+"년"+key.substring(2,4)+"월"+key.substring(4,6)+"일";	//생년월일
			String dtPay = data.getDtPay();
			dtPay = dtPay.substring(0,4)+"."+dtPay.substring(4,6)+"."+dtPay.substring(6,8)+"";	//지급일
			if("디에이치(주)".equals(data.getEstName())) {
				data.setEstName("");
			}
//			DecimalFormat payFormat = new DecimalFormat("###,###");
			NumberFormat payFormat = NumberFormat.getInstance();
			
	        // 소수점 한 자리로 출력
	        DecimalFormat df_m1 = new DecimalFormat("#.#");
			
			StringBuilder body = new StringBuilder();
			body.append("<div id=\"dec\">")
				.append("<!--제목--->\r\n")
				.append("<table width=\"740px\">\r\n")
				.append("	<tbody>\r\n")
				.append("		<tr align=\"center\">\r\n")
				.append("			<td style=\"font-size: 16px;font-family: 돋음, dotum;color: #444444;padding:10px;\"> <b> ")
				.append(data.getYyyymm().substring(0,4)+"년 "+data.getYyyymm().substring(4,6))//yyyymm
				.append("월 급여 명세서</b>\r\n")
				.append("			</td>\r\n")
				.append("		</tr>\r\n")
				.append("	</tbody>\r\n")
				.append("</table>\r\n")
				.append("<table class=\"top_basictbl\">\r\n")
				.append("	<colgroup>\r\n")
				.append("		<col style=\"width:50%\">\r\n")
				.append("		<col>\r\n")
				.append("	</colgroup>\r\n")
				.append("	<tbody>\r\n")
				.append("		<tr>\r\n")
				.append("			<td class=\"txtlft\">\r\n")
				.append("				<p> <em> 회사명</em> 디에이치(주) ")
				.append(data.getEstName())	//사업장명
				.append("</p>\r\n")
				.append("			</td>\r\n")
				.append("			<td class=\"txtrgt\">\r\n")
				.append("				<p> <em> 지급일</em> ")
				.append(dtPay+" </p>\r\n")	//지급일
				.append("			</td>\r\n")
				.append("		</tr>\r\n")
				.append("	</tbody>\r\n")
				.append("</table> <!--사원정보 테이블-->\r\n")
				.append("<table class=\"userinfo_tbl\">\r\n")
				.append("	<colgroup>\r\n")
				.append("		<col style=\"width:80px\">\r\n")
				.append("		<col>\r\n")
				.append("		<col style=\"width:60px\">\r\n")
				.append("		<col style=\"width:18%\">\r\n")
				.append("		<col style=\"width:60px\">\r\n")
				.append("		<col style=\"width:18%\">\r\n")
				.append("	</colgroup>\r\n")
				.append("	<tbody>\r\n")
				.append("		<tr>\r\n")
				.append("			<th> <span> 사원코드</span> </th>\r\n")
				.append("			<td> "+data.getEmployeeNumber()+"</td>\r\n")
				.append("			<th> <span> 사원명</span> </th>\r\n")
				.append("			<td> "+data.getKoreanName()+"</td>\r\n")	//koreanName
				.append("			<th> <span> 생년월일</span> </th>\r\n")
				.append("			<td> "+birth+"</td>\r\n")	//birth
				.append("		</tr>\r\n")
				.append("		<tr>\r\n")
				.append("			<th> <span> 부서</span> </th>\r\n")
				.append("			<td> "+data.getDepartmentName()+"</td>\r\n")
				.append("			<th> <span> 직급</span> </th>\r\n")
				.append("			<td> "+data.getDefinedName()+"</td>\r\n")
				.append("			<th> <span> 입사일</span> </th>\r\n")
				.append("			<td> "+data.getHireDate()+"</td>\r\n")
				.append("		</tr>\r\n")
				.append("	</tbody>\r\n")
				.append("</table> <!--근로일수 및 시간 -->\r\n")
				.append("<table class=\"userwork_tbl\">\r\n")
				.append("	<colgroup>\r\n")
				.append("		<col style=\"width:20%\" span=\"5\">\r\n")
				.append("	</colgroup>\r\n")
				.append("	<thead>\r\n")
				.append("		<tr>\r\n")
				.append("			<th> 연장근로시간</th>\r\n")
				.append("			<th> 야간근로시간</th>\r\n")
				.append("			<th> 휴일근로시간</th>\r\n")
				.append("			<th colspan=\"2\"> 통상시급(원) </th>\r\n")
				.append("		</tr>\r\n")
				.append("	</thead>\r\n")
				.append("	<tbody>\r\n")
				.append("		<tr>\r\n")
				.append("			<td> " + df_m1.format(Math.floor((data.getOvertimeDaytime01())*10)/10) + "</td>\r\n")
				.append("			<td> " + df_m1.format(Math.floor((data.getNtDaytime01() + data.getNtNighttime01())*10)/10) + "</td>\r\n")
				.append("			<td> " + df_m1.format(Math.floor((data.getHolidaySatTime01() + data.getHolidaySunTime01())*80)/10) + "</td>\r\n")
				.append("			<td colspan=\"2\"> " + data.getAttribute20() + "</td>\r\n")
				.append("		</tr>\r\n")
				.append("	</tbody>\r\n")
				.append("</table> <!--지급내역/공제내역 테이블-->\r\n")
				.append("<table width=\"740px\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" class=\"origin_tbl\">\r\n")
				.append("	<tbody>\r\n")
				.append("		<tr>\r\n")
				.append("			<td bgcolor=\"#b1c5db\">\r\n")
				.append("				<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"\r\n")
				.append("					style=\"table-layout:fixed; border-collapse:collapse;word-break:break-all;\"> <!--지급내역-->\r\n")
				.append("					<tbody>\r\n")
				.append("						<tr bgcolor=\"#f7f7f7\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 11px;font-family: 돋음, dotum;color: #666677;\">\r\n")
				.append("							<td rowspan=\"8\" width=\"16%\" align=\"center\" bgcolor=\"#cedff7\"\r\n")
				.append("								style=\"font-weight: bold; font-size: 12px;font-family: 돋음, dotum; color: #333355;border-right:1px solid #b1c5db;\">\r\n")
				.append("								지급내역</td>\r\n")
				.append("							<th width=\"14%\"> 기본급</th>\r\n")
				.append("							<th width=\"14%\"> 연차수당</th>\r\n")
				.append("							<th width=\"14%\"> 연장수당1</th>\r\n")
				.append("							<th width=\"14%\"> 연장수당2</th>\r\n")
				.append("							<th width=\"14%\"> 야간수당1</th>\r\n")
				.append("							<th width=\"14%\"> 야간수당2</th>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">" + payFormat.format(data.getBasicSalary()) + "</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">" + payFormat.format(data.getAnnualAllowance()) + "</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getOvertimeAllowance01()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getOvertimeAllowance02()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getNightAllowance01()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getNightAllowance02()) +"</td>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#f7f7f7\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 11px;font-family: 돋음, dotum;color: #666677;\">\r\n")
				.append("							<th width=\"14%\"> 휴일수당1</th>\r\n")
				.append("							<th width=\"14%\"> 휴일수당2</th>\r\n")
				.append("							<th width=\"14%\"> 직책수당</th>\r\n")
				.append("							<th width=\"14%\"> 기타수당</th>\r\n")
				.append("							<th width=\"14%\"> 보조금</th>\r\n")
				.append("							<th width=\"14%\"> </th>\r\n")//식대
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getHolidayAllowance01()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getHolidayAllowance02()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getPositionAllowance()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getOtherAllowances()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getSubsidies()) +"</td>\r\n")
				.append("							<td> </td>\r\n")//.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getMealsExpenses()) +"</td>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#f7f7f7\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 11px;font-family: 돋음, dotum;color: #666677;\">\r\n")
				.append("							<th> </th>\r\n")//.append("							<th width=\"14%\"> 교통비</th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\" style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td> </td>\r\n")//.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getTransportationExpenses()) +"</td>\r\n")
				.append("							<td> </td>\r\n")
				.append("							<td> </td>\r\n")
				.append("							<td> </td>\r\n")
				.append("							<td> </td>\r\n")
				.append("							<td> </td>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#f7f7f7\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 11px;font-family: 돋음, dotum;color: #666677;\">\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\" style=\"font-size: 12px;color: #000000;\">\r\n")
				.append("							<td> </td>\r\n")
				.append("							<td> </td>\r\n")
				.append("							<td> </td>\r\n")
				.append("							<td> </td>\r\n")
				.append("							<td> </td>\r\n")
				.append("							<td> </td>\r\n")
				.append("						</tr> <!--//지급내역-->\r\n")
				.append("						<tr>\r\n")
				.append("							<td colspan=\"7\" height=\"1px\"> </td>\r\n")
				.append("						</tr> <!--공제내역-->\r\n")
				.append("						<tr bgcolor=\"#f7f7f7\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 11px;font-family: 돋음, dotum;color: #666677;\">\r\n")
				.append("							<td rowspan=\"8\" width=\"16%\" align=\"center\" bgcolor=\"#cedff7\"\r\n")
				.append("								style=\"font-weight: bold; font-size: 12px;font-family: 돋음, dotum; color: #333355;border-right:1px solid #b1c5db;\">\r\n")
				.append("								공제내역</td>\r\n")
				.append("							<th width=\"14%\"> 국민연금</th>\r\n")
				.append("							<th width=\"14%\"> 건강보험</th>\r\n")
				.append("							<th width=\"14%\"> 고용보험</th>\r\n")
				.append("							<th width=\"14%\"> 장기요양보험료</th>\r\n")
				.append("							<th width=\"14%\"> 소득세</th>\r\n")
				.append("							<th width=\"14%\"> 지방소득세</th>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getNationalPension()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getHealthInsurance()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getEmploymentInsurance()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getCareInsurance()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getIncomtax()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getResidtax()) +"</td>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#f7f7f7\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 11px;font-family: 돋음, dotum;color: #666677;\">\r\n")
				.append("							<th width=\"14%\"> 가불금</th>\r\n")
				.append("							<th width=\"14%\"> 기타공제</th>\r\n")
				.append("							<th width=\"14%\"> 경조비</th>\r\n")
				.append("							<th width=\"14%\"> 연말정산</th>\r\n")
				.append("							<th width=\"14%\"> 건강보험정산</th>\r\n")
				.append("							<th width=\"14%\"> 장기요양보험정산</th>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getAdvance()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getOtherTax()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getGyeongjobi()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getYearend()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getHealthInsuranceSettlement()) +"</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getCareInsuranceSettlement()) +"</td>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#f7f7f7\" height=\"22px\" align=\"center\" style=\"font-size: 12px;color: #666677;\">\r\n")
				.append("							<th width=\"14%\"> 연차수당</th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\">"+ payFormat.format(data.getHolidayTax()) +"</td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#f7f7f7\" height=\"22px\" align=\"center\" style=\"font-size: 12px;color: #666677;\">\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("							<th> </th>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("						</tr> <!--//공제내역-->\r\n")
				.append("					</tbody>\r\n")
				.append("				</table>\r\n")
				.append("			</td>\r\n")
				.append("		</tr>\r\n")
				.append("	</tbody>\r\n")
				.append("</table> <!--합계데이블-->\r\n")
				.append("<table width=\"740px\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" class=\"origin_tbl\">\r\n")
				.append("	<tbody>\r\n")
				.append("		<tr>\r\n")
				.append("			<td bgcolor=\"#7f9db9\">\r\n")
				.append("				<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> <!--합계-->\r\n")
				.append("					<tbody>\r\n")
				.append("						<tr bgcolor=\"#e7e7e7\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 11px;font-family: 돋음, dotum;color: #555;\">\r\n")
				.append("							<td rowspan=\"6\" width=\"16%\" align=\"center\" bgcolor=\"#a8bdd3\"\r\n")
				.append("								style=\"font-weight: bold; font-size: 12px;font-family: 돋음, dotum; color: #000;border-right:1px solid #7f9db9;\">\r\n")
				.append("								합계</td>\r\n")
				.append("							<th width=\"14%\"> </th>\r\n")
				.append("							<th width=\"14%\"> </th>\r\n")
				.append("							<th width=\"14%\"> 지급총액</th>\r\n")
				.append("							<th width=\"14%\"> 공제총액</th>\r\n")
				.append("							<th width=\"14%\"> </th>\r\n")
				.append("							<th width=\"14%\"> 차인지급액</th>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\">" + payFormat.format(data.getSalarySum()) + "</td>\r\n")
				.append("							<td width=\"14%\">" + payFormat.format(data.getTaxSum()) + "</td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\">" + payFormat.format(data.getSalarySum() - data.getTaxSum()) + "</td>\r\n")
				.append("						</tr>\r\n")
				.append("					</tbody>\r\n")
				.append("				</table>\r\n")
				.append("			</td>\r\n")
				.append("		</tr>\r\n")
				.append("	</tbody>\r\n")
				.append("</table>\r\n")
				.append("<table class=\"calcrule_tbl\">\r\n")
				.append("	<caption> 계산방법</caption>\r\n")
				.append("	<colgroup>\r\n")
				.append("		<col style=\"width:30%\">\r\n")
				.append("		<col>\r\n")
				.append("		<col style=\"width:30%\">\r\n")
				.append("	</colgroup>\r\n")
				.append("	<thead>\r\n")
				.append("		<tr>\r\n")
				.append("			<th> 구분</th>\r\n")
				.append("			<th> 산출식 또는 산출방법</th>\r\n")
				.append("			<th> 지급액</th>\r\n")
				.append("		</tr>\r\n")
				.append("	</thead>\r\n")
				.append("	<tbody>\r\n");

			if ("100".equals(data.getEmployeeType())) {
				// employee_type -- 100 연봉제
				body.append("		<tr>\r\n")
					.append("			<td> 기본급</td>\r\n")
					.append("			<td>통상시급 * 209시간</td>\r\n")//data.getTotalTime()
					.append("			<td>" + payFormat.format(data.getBasicSalary()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 연장수당1</td>\r\n")
					.append("			<td> 연차사용촉진제 시행</td>\r\n")//연장근로일수(" + data.getOvertime01() + "일) * 20,000원
					.append("			<td>" + payFormat.format(data.getOvertimeAllowance01()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 연장수당2</td>\r\n")
					.append("			<td> 포괄 임금제(연장시간 * 통상시급 * 1.5)</td>\r\n")
					.append("			<td>" + payFormat.format(data.getOvertimeAllowance02()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 야간수당1</td>\r\n")
					.append("			<td> 야간근로일수 * 30,000원</td>\r\n")
					.append("			<td>" + payFormat.format(data.getNightAllowance01()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 야간수당2</td>\r\n")
					.append("			<td> 포괄임금제(야간시간 * 통상시급 * 0.5)</td>\r\n")
					.append("			<td>" + payFormat.format(data.getNightAllowance02()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 휴일수당1</td>\r\n")
					.append("			<td> 휴일근로일수 * 50,0000원(8시간기준)</td>\r\n")//data.getHoliday01()
					.append("			<td>" + payFormat.format(data.getHolidayAllowance01()) + "</td>\r\n")
					.append("		</tr>\r\n");
			} else if ("200".equals(data.getEmployeeType())) {
				// employee_type -- 200 시급제
				body.append("		<tr>\r\n")
					.append("			<td> 기본급</td>\r\n")
					.append("			<td>통상시급 * 209</td>\r\n")//data.getTotalTime()
					.append("			<td>" + payFormat.format(data.getBasicSalary()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 연차수당</td>\r\n")
					.append("			<td> 통상시급 * 근로시간 / 연,월차 미사용시 당월정산(반차사용시 해당시간 차감)</td>\r\n")//data.getAnnualLeaveUsed()
					.append("			<td>" + payFormat.format(data.getAttribute01()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 연장수당1</td>\r\n")
					.append("			<td> 연장근로시간 * 통상시급 * 1.5</td>\r\n")
					.append("			<td>" + payFormat.format(data.getOvertimeAllowance01()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 야간수당1</td>\r\n")
					.append("			<td> 야간근로시간 * 통상시급 * 2.0</td>\r\n")
					.append("			<td>" + payFormat.format(data.getNightAllowance02()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 휴일수당1</td>\r\n")
					.append("			<td> 휴일근로시간 * 통상시급 * 1.5</td>\r\n")
					.append("			<td>" + payFormat.format(data.getHolidayAllowance01()) + "</td>\r\n")
					.append("		</tr>\r\n")
					.append("		<tr>\r\n")
					.append("			<td> 기타수당</td>\r\n")
					.append("			<td> 지각·외출·조퇴 사용시 공제 (사용시간 * 통상시급)</td>\r\n")
					.append("			<td>" + payFormat.format(data.getAttribute09()) + "</td>\r\n")
					.append("		</tr>\r\n");
					//boolean addComma = false;
					//if (0 < data.getLateTime()) {
					//	body.append("지각 " + data.getLateTime() + "시간");
					//	addComma = true;
					//}
					//if (0 < data.getOuterTime()) {
					//	if (addComma) body.append(" ,");
					//	body.append("외출 " + data.getOuterTime() + "시간");
					//	addComma = true;
					//}
					//if (0 < data.getEarlyLeaveTime()) {
					//	if (addComma) body.append(" ,");
					//	body.append("조퇴 " + data.getEarlyLeaveTime() + "시간");
					//	addComma = true;
					//}
					//if (false == addComma) body.append("0시간");
					//body.append((data.getLateTime() + data.getOuterTime() + data.getEarlyLeaveTime()) + "시간");
					//body.append(" * 통상시급)</td>\r\n")
					//.append("			<td>" + data.getAttribute09() + "</td>\r\n")
					//.append("		</tr>\r\n")
					//.append("		<tr>\r\n")
					//.append("			<td> 교통비 </td>\r\n")
					//.append("			<td> 야근 " + data.getTransportation() + "회 * 20,000</td>\r\n")
					//.append("			<td>" + data.getTransportationExpenses() + "</td>\r\n")
					//.append("		</tr>\r\n")
					//.append("		<tr>\r\n")
					//.append("			<td> 식대 </td>\r\n")
					//.append("			<td> 야근 " + data.getMeals() + "회 * 14,000</td>\r\n")
					//.append("			<td>" + data.getMealsExpenses() + "</td>\r\n")
					//.append("		</tr>\r\n");
			}
			body.append("	</tbody>\r\n")
				.append("</table> <!--하단글-->\r\n")
				.append("<table width=\"740px\">\r\n")
				.append("	<tbody>\r\n")
				.append("		<tr align=\"center\">\r\n")
				.append("			<td style=\"font-size: 12px;font-family: 돋음, dotum;color: #444;padding:10px;\">\r\n")
				.append("				<p> 귀하의 노고에 감사드립니다.</p>\r\n")
				.append("			</td>\r\n")
				.append("		</tr>\r\n")
				.append("	</tbody>\r\n")
				.append("</div>");


		// 첨부파일
		Aes256 aes256 = new Aes256();
		StringBuilder attachStr = new StringBuilder();
		attachStr.append("<html><head> <title>:: "+ data.getYyyymm().substring(0,4)+"년 "+data.getYyyymm().substring(4,6) +"월 급여 명세서 ::</title><meta http-equiv='Content-Type' content='text/html; charset=utf-8'><meta id='viewport' name='viewport' content='width=642'><style>@font-face {font-family: 'douzone';src: local('DOUZONEText10'),url('https://static.wehago.com/fonts/douzone/DOUZONEText10.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText10.woff') format('woff');font-weight: normal;font-display: fallback;}@font-face {font-family: 'douzone';src: local('DOUZONEText30'),url('https://static.wehago.com/fonts/douzone/DOUZONEText30.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText30.woff') format('woff');font-weight: bold;font-display: fallback;}@font-face {font-family: 'douzone';src: local('DOUZONEText50'),url('https://static.wehago.com/fonts/douzone/DOUZONEText50.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText50.woff') format('woff');font-weight: 900;font-display: fallback;}body,p,h1,h2,h3,h4,h5,h6,ul,ol,li,dl,dt,dd,table,th,td,form,fieldset,legend,input,textarea,img,button,select{margin:0;padding:0}body,h1,h2,h3,h4,h5,h6,ul,ol,li,dl,button{font-family:douzone,'Microsoft?YaHei','PingFang?SC','MS?PGothic','Hiragino?Kaku?Gothic?ProN','굴림',gulim,'Apple?SD?Gothic?Neo',sans-serif}body{min-width:642px;-webkit-text-size-adjust:none}img,fieldset{border:0;vertical-align:top}a{color:#1a1a1a}em,address{font-style:normal}ul,ol,li{list-style:none}label,button{cursor:pointer}input::-ms-clear{display:none}.blind{position:absolute !important;clip:rect(0 0 0 0) !important;width:1px !important;height:1px !important;margin:-1px !important;overflow:hidden !important}table{width:100%;table-layout: fixed;border-collapse:collapse;border-spacing: 0;width:100%}table thead.blind{position:static;font-size:0} /* 테이블 thead blind 버그해결 */.clearbx:after,.clearfix:after{content:'';clear:both;display:table}.dz_font,.dz_font *{font-family:douzone,'Microsoft?YaHei','PingFang?SC','MS?PGothic','Hiragino?Kaku?Gothic?ProN','굴림',gulim,'Apple?SD?Gothic?Neo',sans-serif}body{margin:70px auto;width:642px}.origin_tbl{border:1px solid #eaeaea;margin-top:10px}.origin_tbl + .origin_tbl{margin-top:5px}.head_title{font-size:15px;color:#191919;line-height:20px;text-align:center;font-weight:900;margin-bottom:20px}.topdate{font-size:11px;color:#4a4a4a;line-height:12px;letter-spacing: -.5px;font-weight:bold;text-align:right;margin-bottom:6px}.topdate > em {font-weight:900;}.txtlft {text-align:left !important}.txtrgt {text-align:right !important}.top_basictbl{border:0;table-layout: fixed;border-spacing: 0;margin-bottom:6px}.top_basictbl td{font-size: 11px;color: #4a4a4a;line-height: 12px;letter-spacing: -.5px;font-weight: bold}.top_basictbl td em{font-weight:900}.userinfo_tbl{border:2px solid #eee}.userinfo_tbl th{font-size:11px;color:#000;letter-spacing: -.5px;line-height:12px;font-weight:900;text-align:left;padding:6px 2px;vertical-align:top}.userinfo_tbl td{font-size:11px;color:#4a4a4a;letter-spacing: -.5px;line-height:12px;font-weight:bold;text-align:left;padding:6px 2px;vertical-align:top}.userinfo_tbl th > span{position:relative;display:block;padding-left:6px}.userinfo_tbl th > span:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.userinfo_tbl tr:first-of-type th,.userinfo_tbl tr:first-of-type td{padding-top:18px}.userinfo_tbl tr:last-of-type th,.userinfo_tbl tr:last-of-type td{padding-bottom:16px}.userinfo_tbl tr th:first-of-type{padding-left:24px}.userwork_tbl{border:1px solid #eaeaea;margin-top:10px}.userwork_tbl th{font-size:11px;color:#4a4a4a;line-height:12px;letter-spacing: -.5px;background:#f8f8f8;height:30px;border:1px solid #eaeaea}.userwork_tbl td{font-size:11px;color:#000;line-height:12px;letter-spacing: -.5px;background:#fff;height:30px;text-align:center;border:1px solid #eaeaea}.pay_details_tbl{margin-top:30px}.pay_details_tbl th{background:#f8f8f8;font-size:11px;font-weight:bold;letter-spacing: -.5px;color:#000;height:30px;border:1px solid #eaeaea;border-right:0;text-align:left;padding-left:12px}.pay_details_tbl td{background:#fff;font-size:11px;font-weight:bold;letter-spacing: -.5px;color:#000;text-align:right;height:30px;border:1px solid #eaeaea;border-left:0;padding-right:12px}.pay_details_tbl td.empty{border:0;background:#fff !important}.pay_details_tbl th.tit{background:#fff;border:0;font-size:12px;color:#000;font-weight:900;padding:0}.pay_details_tbl th.tit > span{position:relative;display:block;padding-left:6px}.pay_details_tbl th.tit > span:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.pay_details_tbl .total th,.pay_details_tbl .total td{border-color:#e6ebf2;background:#f2f6fa}.pay_details_tbl .total td{font-weight:900;font-size:12px}.realpay_dl {position:relative;border:1px solid #1c90fb;border-radius:6px;box-shadow:0 2px 6px rgba(0,0,0,.07);background:#f2f9ff;margin-top:16px;padding:16px 16px 14px;line-height:18px;clear:both;overflow:hidden}.realpay_dl dt{float:left;font-size:12px;color:#000;letter-spacing: -.55px;font-weight:bold}.realpay_dl dd{float:right;font-size:14px;color:#1c90fb;letter-spacing: -.7px;font-weight:900}.calcrule_tbl {margin-top:32px}.calcrule_tbl caption{position:relative;font-size:12px;color:#000;letter-spacing: -.33px;line-height:14px;text-align:left;font-weight:900;padding-left:6px;margin-bottom:10px}.calcrule_tbl caption:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.calcrule_tbl th,.calcrule_tbl td {font-size:11px;letter-spacing: -.5px;font-weight:bold;height:30px;border:1px solid #eaeaea;text-align:center}.calcrule_tbl th {color:#4a4a4a;background:#f8f8f8}.calcrule_tbl td {color:#000}.thx_text{font-size:12px;color:#000;font-weight:bold;line-height:14px;text-align:center;margin-top:24px}</style></head><body leftmargin='0' topmargin='0' style='font-face:맑은고딕,Malgun Gothic, 돋음, dotum;' align='center'>")
			.append("<div id='precheck' style='font-size: 16px;font-family: 돋음, dotum;color: #444444;padding:10px;'>")
			.append("본 메일은 고객님의 정보보호를 위해 암호화된 보안메일입니다.</br></br>내용을 확인하시려면 생년월일 6자리를 입력 후 확인해 주시기 바랍니다.</br></br><input type=\"text\" id=\"infor\" class=\"inputButton\"/><button onclick=\"viewdetail()\">명세서 보기</button></div>")
			.append("<div id=\"detail\" style=\"visibility: hidden;\"></div>")
			.append("</body><script src=\"https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/crypto-js.min.js\"></script>")
			.append("<script type = \"text/javascript\">var input = document.getElementById(\"infor\");input.addEventListener(\"keyup\", function (event) { if (event.keyCode === 13) { if (document.getElementById(\"infor\").value != '') { event.preventDefault();viewdetail();}}});function viewdetail() { let input = document.getElementById(\"infor\").value;if (input == '' || input.length != 6) { alert(\"생년월일 6자리를 입력해주세요.\");document.getElementById(\"infor\").focus();return false;}var key=input.padEnd(16, \" \");var Iv = \"0123456789abcdef\";var data = '")
			.append(aes256.encrypt(key, iv, body.toString()))
			.append("';const cipher = CryptoJS.AES.decrypt(data, CryptoJS.enc.Utf8.parse(key), { iv: CryptoJS.enc.Utf8.parse(Iv), padding: CryptoJS.pad.Pkcs7, mode: CryptoJS.mode.CBC }); var html = cipher.toString(CryptoJS.enc.Utf8); document.getElementById('detail').innerHTML = html; if (null != document.getElementById('dec')) { document.getElementById(\"precheck\").remove(); document.getElementById(\"dec\").style.visibility = 'visible'; } else { alert(\"비밀번호가 일치하지 않습니다\"); } } </script></html>");

		return attachStr.toString();
	}

	public List<PayStubMailHistDto> getDHPaystubmailHistList(PayStubMailHistDto eDTO) throws Exception {

		return paystubmailHistDao.selectDHPaystubmailHistList(eDTO);
	};

	public int addDHPaystubmailHist(PayStubMailHistDto eDTO) throws Exception {

		return paystubmailHistDao.insertDHPaystubmailHist(eDTO);
	};

	public int chgDHPaystubmailHist(PayStubMailHistDto eDTO) throws Exception {

		return paystubmailHistDao.updateDHPaystubmailHist(eDTO);
	};

	public int delDHPaystubmailHist(String keyId) throws Exception {

		return paystubmailHistDao.deleteDHPaystubmailHist(keyId);
	};
}
