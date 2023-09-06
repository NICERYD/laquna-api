package kr.co.seedit.domain.hr.application;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import kr.co.seedit.domain.hr.dto.PayStubMailDto;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;
import kr.co.seedit.domain.hr.dto.ReportPayrollDto;
import kr.co.seedit.domain.mapper.seedit.ReportDao;
import kr.co.seedit.global.error.exception.CustomException;
import kr.co.seedit.global.utils.Aes256;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PayStubMailService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final byte[] iv = "0123456789abcdef".getBytes();
	
	private final ReportDao reportDao;
	
	public ReportPayrollDto getPayStubData () throws Exception {
		ReportParamsDto reportParamsDto = new ReportParamsDto();
		reportParamsDto.setCompanyId(5);
		reportParamsDto.setEstId(999);
		reportParamsDto.setYyyymm("202306");
		reportParamsDto.setEmployeeNumber("D00001");
		ReportPayrollDto data = reportDao.findPayStubForMail(reportParamsDto);
		return data;
	}
	
	public void sendMail(PayStubMailDto mailDto) {
		ReportPayrollDto payStubData = new ReportPayrollDto();
			try {
				payStubData = getPayStubData();
				log.info("payStubData>>>>>>>>>"+payStubData.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

		JavaMailSenderImpl emailSender = new JavaMailSenderImpl();
		
		Properties properties = null;
		try {
		    if (mailDto.getEncoding().isEmpty()) throw new CustomException("empty input. encoding");
		    if (mailDto.getAuth    ().isEmpty()) throw new CustomException("empty input. auth    ");
		    if (mailDto.getSsl     ().isEmpty()) throw new CustomException("empty input. ssl     ");
		    if (mailDto.getStarttls().isEmpty()) throw new CustomException("empty input. starttls");
		    if (mailDto.getDebug   ().isEmpty()) throw new CustomException("empty input. debug   ");
		    if (mailDto.getHost    ().isEmpty()) throw new CustomException("empty input. host    ");
		    if (mailDto.getPort    ().isEmpty()) throw new CustomException("empty input. port    ");
		    if (mailDto.getUsername().isEmpty()) throw new CustomException("empty input. username");
		    if (mailDto.getPassword().isEmpty()) throw new CustomException("empty input. password");
		    if (mailDto.getEncoding().isEmpty()) throw new CustomException("empty input. encoding");

			// SMTP 설정
			properties = System.getProperties();
			properties.setProperty("mail.smtp.starttls.enable", mailDto.getStarttls());
			properties.setProperty("mail.smtp.debug", mailDto.getDebug());
	        properties.setProperty("mail.transport.protocol", mailDto.getProtocol());
	        properties.setProperty("mail.smtp.auth", mailDto.getAuth());
	        properties.setProperty("mail.smtp.ssl.enable", mailDto.getSsl());	        
			properties.setProperty("mail.smtp.host", mailDto.getHost());
			properties.setProperty("mail.smtp.port", mailDto.getPort());
	
			emailSender.setHost(mailDto.getHost());
			emailSender.setUsername(mailDto.getUsername());
			emailSender.setPassword(mailDto.getPassword());
			emailSender.setPort(Integer.valueOf(mailDto.getPort()));
			emailSender.setJavaMailProperties(properties);
			emailSender.setDefaultEncoding(mailDto.getEncoding());

			emailSender.setJavaMailProperties(properties);
			emailSender.setSession(Session.getDefaultInstance(properties));
		} catch (SecurityException e) {
			//log.error("mail setting fail. SecurityException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("mail smtp setting fail");
		} catch (NumberFormatException e) {
			//log.error("mail setting fail. NumberFormatException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("mail setting fail");
		}
		

		MimeMessage message = null;
        MimeMessageHelper helper = null;
		try {
			message = emailSender.createMimeMessage();
	        helper = new MimeMessageHelper(message, true, "utf-8");
		} catch (SecurityException e) {
			//log.error("Default setting fail. SecurityException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Default setting fail");
		} catch (MessagingException e) {
			//log.error("Default setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Default setting fail");
		}

		try {
			//메일 제목 설정
			if (!"".equals(mailDto.getSubject())) {
				helper.setSubject(mailDto.getSubject());
			} else {
//				helper.setSubject("NoTitle");
				throw new CustomException("Subject is empty");
			}
		} catch (MessagingException e) {
			//log.error("Subject setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Subject setting fail");
		}

		try {
			//수신자 설정
			if (null != mailDto.getAddress() && 0 < mailDto.getAddress().length) {
				helper.setTo(mailDto.getAddress());
			} else {
				throw new CustomException("empty input. Address");
			}
			//참조자 설정
			if (null != mailDto.getCcAddress() && 0 < mailDto.getCcAddress().length) {
				helper.setCc(mailDto.getCcAddress());
			}
		} catch (MessagingException e) {
			//log.error("Receiver setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Receiver setting fail");
		}

		try {
	        //메일 내용 설정
			if (!"".equals(mailDto.getContent())) {
				helper.setText(mailDto.getContent(), true);
			}
		} catch (MessagingException e) {
			//log.error("default setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("default setting fail");
		}
		
		try {
			String key = "750122";
			StringBuilder body = new StringBuilder();
			body.append("<div id=\"dec\">")
				.append("<!--제목--->\r\n")
				.append("<table width=\"740px\">\r\n")
				.append("	<tbody>\r\n")
				.append("		<tr align=\"center\">\r\n")
				.append("			<td style=\"font-size: 16px;font-family: 돋음, dotum;color: #444444;padding:10px;\"> <b> ")
				.append("2023년 8월")	//yyyymm
				.append("급여 명세서</b>\r\n")
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
				.append("")	//estName
				.append("</p>\r\n")
				.append("			</td>\r\n")
				.append("			<td class=\"txtrgt\">\r\n")
				.append("				<p> <em> 지급일</em> ")
				.append("2023.09.15 </p>\r\n")	//dt_pay
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
				.append("			<td> "+payStubData.getEmployeeNumber()+"</td>\r\n")
				.append("			<th> <span> 사원명</span> </th>\r\n")
				.append("			<td> "+payStubData.getKoreanName()+"</td>\r\n")	//koreanName
				.append("			<th> <span> 생년월일</span> </th>\r\n")
				.append("			<td> 1989년11월27일</td>\r\n")	//birth
				.append("		</tr>\r\n")
				.append("		<tr>\r\n")
				.append("			<th> <span> 부서</span> </th>\r\n")
				.append("			<td> "+payStubData.getDepartmentName()+"</td>\r\n")
				.append("			<th> <span> 직급</span> </th>\r\n")
				.append("			<td> "+payStubData.getDefinedName()+"</td>\r\n")
				.append("			<th> <span> 호봉</span> </th>\r\n")
				.append("			<td> </td>\r\n")
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
				.append("			<td> </td>\r\n")
				.append("			<td> </td>\r\n")
				.append("			<td> </td>\r\n")
				.append("			<td colspan=\"2\"> </td>\r\n")
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
				.append("							<th width=\"14%\"> 연장수당1</th>\r\n")
				.append("							<th width=\"14%\"> 연장수당2</th>\r\n")
				.append("							<th width=\"14%\"> 야간수당2</th>\r\n")
				.append("							<th width=\"14%\"> </th>\r\n")
				.append("							<th width=\"14%\"> </th>\r\n")
				.append("						</tr>\r\n")
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 2,000,000</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 20,000</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 500,000</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 500,000</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
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
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
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
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 131,220</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 103,390</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 27,180</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 13,240</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 76,060</td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> 7,600</td>\r\n")
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
				.append("						<tr bgcolor=\"#ffffff\" height=\"22px\" align=\"center\"\r\n")
				.append("							style=\"font-size: 12px;font-family: 돋음, dotum;color: #000000;\">\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
				.append("							<td style=\"border-bottom:1px solid #eee;\"> </td>\r\n")
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
				.append("							<td width=\"14%\"> 3,020,000</td>\r\n")
				.append("							<td width=\"14%\"> 358,690</td>\r\n")
				.append("							<td width=\"14%\"> </td>\r\n")
				.append("							<td width=\"14%\"> 2,661,310</td>\r\n")
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
				.append("	<tbody>\r\n")
				.append("		<tr>\r\n")
				.append("			<td> 기본급</td>\r\n")
				.append("			<td> 통상시급 * 209</td>\r\n")
				.append("			<td> 2,000,000</td>\r\n")
				.append("		</tr>\r\n")
				.append("		<tr>\r\n")
				.append("			<td> 연장수당1</td>\r\n")
				.append("			<td> 연장근로시간 * 통상시급 * 1.5</td>\r\n")
				.append("			<td> 20,000</td>\r\n")
				.append("		</tr>\r\n")
				.append("	</tbody>\r\n")
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
			

			Aes256 aes256 = new Aes256();
String enc= aes256.encrypt(key, iv, body.toString());
System.out.println("aes256.encrypt(body) [" + enc + "]");
//if (!enc.isEmpty()) return;

			// 첨부파일
			StringBuilder content = new StringBuilder();
			content.append("<html><head> <title>:: 2023년 8월 급여 명세서 ::</title><meta http-equiv='Content-Type' content='text/html; charset=utf-8'><meta id='viewport' name='viewport' content='width=642'><style>@font-face {font-family: 'douzone';src: local('DOUZONEText10'),url('https://static.wehago.com/fonts/douzone/DOUZONEText10.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText10.woff') format('woff');font-weight: normal;font-display: fallback;}@font-face {font-family: 'douzone';src: local('DOUZONEText30'),url('https://static.wehago.com/fonts/douzone/DOUZONEText30.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText30.woff') format('woff');font-weight: bold;font-display: fallback;}@font-face {font-family: 'douzone';src: local('DOUZONEText50'),url('https://static.wehago.com/fonts/douzone/DOUZONEText50.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText50.woff') format('woff');font-weight: 900;font-display: fallback;}body,p,h1,h2,h3,h4,h5,h6,ul,ol,li,dl,dt,dd,table,th,td,form,fieldset,legend,input,textarea,img,button,select{margin:0;padding:0}body,h1,h2,h3,h4,h5,h6,ul,ol,li,dl,button{font-family:douzone,'Microsoft?YaHei','PingFang?SC','MS?PGothic','Hiragino?Kaku?Gothic?ProN','굴림',gulim,'Apple?SD?Gothic?Neo',sans-serif}body{min-width:642px;-webkit-text-size-adjust:none}img,fieldset{border:0;vertical-align:top}a{color:#1a1a1a}em,address{font-style:normal}ul,ol,li{list-style:none}label,button{cursor:pointer}input::-ms-clear{display:none}.blind{position:absolute !important;clip:rect(0 0 0 0) !important;width:1px !important;height:1px !important;margin:-1px !important;overflow:hidden !important}table{width:100%;table-layout: fixed;border-collapse:collapse;border-spacing: 0;width:100%}table thead.blind{position:static;font-size:0} /* 테이블 thead blind 버그해결 */.clearbx:after,.clearfix:after{content:'';clear:both;display:table}.dz_font,.dz_font *{font-family:douzone,'Microsoft?YaHei','PingFang?SC','MS?PGothic','Hiragino?Kaku?Gothic?ProN','굴림',gulim,'Apple?SD?Gothic?Neo',sans-serif}body{margin:70px auto;width:642px}.origin_tbl{border:1px solid #eaeaea;margin-top:10px}.origin_tbl + .origin_tbl{margin-top:5px}.head_title{font-size:15px;color:#191919;line-height:20px;text-align:center;font-weight:900;margin-bottom:20px}.topdate{font-size:11px;color:#4a4a4a;line-height:12px;letter-spacing: -.5px;font-weight:bold;text-align:right;margin-bottom:6px}.topdate > em {font-weight:900;}.txtlft {text-align:left !important}.txtrgt {text-align:right !important}.top_basictbl{border:0;table-layout: fixed;border-spacing: 0;margin-bottom:6px}.top_basictbl td{font-size: 11px;color: #4a4a4a;line-height: 12px;letter-spacing: -.5px;font-weight: bold}.top_basictbl td em{font-weight:900}.userinfo_tbl{border:2px solid #eee}.userinfo_tbl th{font-size:11px;color:#000;letter-spacing: -.5px;line-height:12px;font-weight:900;text-align:left;padding:6px 2px;vertical-align:top}.userinfo_tbl td{font-size:11px;color:#4a4a4a;letter-spacing: -.5px;line-height:12px;font-weight:bold;text-align:left;padding:6px 2px;vertical-align:top}.userinfo_tbl th > span{position:relative;display:block;padding-left:6px}.userinfo_tbl th > span:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.userinfo_tbl tr:first-of-type th,.userinfo_tbl tr:first-of-type td{padding-top:18px}.userinfo_tbl tr:last-of-type th,.userinfo_tbl tr:last-of-type td{padding-bottom:16px}.userinfo_tbl tr th:first-of-type{padding-left:24px}.userwork_tbl{border:1px solid #eaeaea;margin-top:10px}.userwork_tbl th{font-size:11px;color:#4a4a4a;line-height:12px;letter-spacing: -.5px;background:#f8f8f8;height:30px;border:1px solid #eaeaea}.userwork_tbl td{font-size:11px;color:#000;line-height:12px;letter-spacing: -.5px;background:#fff;height:30px;text-align:center;border:1px solid #eaeaea}.pay_details_tbl{margin-top:30px}.pay_details_tbl th{background:#f8f8f8;font-size:11px;font-weight:bold;letter-spacing: -.5px;color:#000;height:30px;border:1px solid #eaeaea;border-right:0;text-align:left;padding-left:12px}.pay_details_tbl td{background:#fff;font-size:11px;font-weight:bold;letter-spacing: -.5px;color:#000;text-align:right;height:30px;border:1px solid #eaeaea;border-left:0;padding-right:12px}.pay_details_tbl td.empty{border:0;background:#fff !important}.pay_details_tbl th.tit{background:#fff;border:0;font-size:12px;color:#000;font-weight:900;padding:0}.pay_details_tbl th.tit > span{position:relative;display:block;padding-left:6px}.pay_details_tbl th.tit > span:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.pay_details_tbl .total th,.pay_details_tbl .total td{border-color:#e6ebf2;background:#f2f6fa}.pay_details_tbl .total td{font-weight:900;font-size:12px}.realpay_dl {position:relative;border:1px solid #1c90fb;border-radius:6px;box-shadow:0 2px 6px rgba(0,0,0,.07);background:#f2f9ff;margin-top:16px;padding:16px 16px 14px;line-height:18px;clear:both;overflow:hidden}.realpay_dl dt{float:left;font-size:12px;color:#000;letter-spacing: -.55px;font-weight:bold}.realpay_dl dd{float:right;font-size:14px;color:#1c90fb;letter-spacing: -.7px;font-weight:900}.calcrule_tbl {margin-top:32px}.calcrule_tbl caption{position:relative;font-size:12px;color:#000;letter-spacing: -.33px;line-height:14px;text-align:left;font-weight:900;padding-left:6px;margin-bottom:10px}.calcrule_tbl caption:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.calcrule_tbl th,.calcrule_tbl td {font-size:11px;letter-spacing: -.5px;font-weight:bold;height:30px;border:1px solid #eaeaea;text-align:center}.calcrule_tbl th {color:#4a4a4a;background:#f8f8f8}.calcrule_tbl td {color:#000}.thx_text{font-size:12px;color:#000;font-weight:bold;line-height:14px;text-align:center;margin-top:24px}</style></head><body leftmargin='0' topmargin='0' style='font-face:맑은고딕,Malgun Gothic, 돋음, dotum;' align='center'>")
				.append("<div id='precheck' style='font-size: 16px;font-family: 돋음, dotum;color: #444444;padding:10px;'>")
				.append("본 메일은 고객님의 정보보호를 위해 암호화된 보안메일입니다.</br></br>내용을 확인하시려면 생년월일 6자리를 입력 후 확인해 주시기 바랍니다.</br></br><input type=\"text\" id=\"infor\" class=\"inputButton\"/><button onclick=\"viewdetail()\">명세서 보기</button></div>")
				.append("<div id=\"detail\" style=\"visibility: hidden;\"></div>")
				.append("</body><script src=\"https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/crypto-js.min.js\"></script>")
				.append("<script type = \"text/javascript\">var input = document.getElementById(\"infor\");input.addEventListener(\"keyup\", function (event) { if (event.keyCode === 13) { if (document.getElementById(\"infor\").value != '') { event.preventDefault();viewdetail();}}});function viewdetail() { let input = document.getElementById(\"infor\").value;if (input == '' || input.length != 6) { alert(\"생년월일 6자리를 입력해주세요.\");document.getElementById(\"infor\").focus();return false;}var key=input.padEnd(16, \" \");var Iv = \"0123456789abcdef\";var data = '")
				.append(aes256.encrypt(key, iv, body.toString()))
				.append("';const cipher = CryptoJS.AES.decrypt(data, CryptoJS.enc.Utf8.parse(key), { iv: CryptoJS.enc.Utf8.parse(Iv), padding: CryptoJS.pad.Pkcs7, mode: CryptoJS.mode.CBC }); var html = cipher.toString(CryptoJS.enc.Utf8); document.getElementById('detail').innerHTML = html; if (null != document.getElementById('dec')) { document.getElementById(\"precheck\").remove(); document.getElementById(\"dec\").style.visibility = 'visible'; } else { alert(\"비밀번호가 일치하지 않습니다\"); } } </script></html>");
			helper.addAttachment("급여명세서.html", new ByteArrayResource(content.toString().getBytes(),"utf-8"));

		} catch (SecurityException e) {
			//log.error("Attachment fail. Permission check. SecurityException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Attachment fail. Permission check");
		} catch (IllegalStateException e) {
			//log.error("Attachment fail. IllegalStateException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Attachment fail. File State check");
		} catch (MessagingException e) {
			//log.error("Attachment fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Attachment fail");
		}

		try {
	        log.debug("Send mail start. " + mailDto.toString());

	        //메일 보내기
	        emailSender.send(message);
	        
	        log.debug("Send mail done. " + mailDto.toString());
		} catch (MailAuthenticationException e) {
			//log.error("Email authentication fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Email authentication fail");
		} catch (MailSendException e) {
			//log.error("Email send fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Email send fail");
		} catch (MailException e) {
			//log.error("Email send() error. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Email send() error");
		}
	}
	


}
