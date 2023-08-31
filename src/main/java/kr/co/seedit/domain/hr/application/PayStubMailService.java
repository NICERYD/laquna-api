package kr.co.seedit.domain.hr.application;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

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
import kr.co.seedit.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PayStubMailService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public void sendMail(PayStubMailDto mailDto) {

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

//			Authenticator authenticator = new Authenticator() {
//				protected PasswordAuthentication getPasswordAuthentication() {
//					return new PasswordAuthentication(dhUsername, dhPassword);
//				}
//			};

			emailSender.setJavaMailProperties(properties);
			emailSender.setSession(Session.getDefaultInstance(properties));//, authenticator));
		} catch (SecurityException e) {
			//log.debug("mail setting fail. SecurityException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("mail smtp setting fail");
		} catch (NumberFormatException e) {
			//log.debug("mail setting fail. NumberFormatException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("mail setting fail");
		}
		

		MimeMessage message = null;
        MimeMessageHelper helper = null;
		try {
			message = emailSender.createMimeMessage();
	        helper = new MimeMessageHelper(message, true, "utf-8");
		} catch (SecurityException e) {
			//log.debug("Default setting fail. SecurityException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Default setting fail");
		} catch (MessagingException e) {
			//log.debug("Default setting fail. MessagingException: "+e.getMessage());
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
			//log.debug("Subject setting fail. MessagingException: "+e.getMessage());
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
			//log.debug("Receiver setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Receiver setting fail");
		}

		try {
	        //메일 내용 설정
			if (!"".equals(mailDto.getContent())) {
				helper.setText(mailDto.getContent(), true);
			}
		} catch (MessagingException e) {
			//log.debug("default setting fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("default setting fail");
		}

		try {
			
			// TODO:: 첨부파일
//			for () {
			helper.addAttachment("급여명세서.html", new ByteArrayResource(sampleContent.getBytes("utf-8"),"utf-8"));
//			}

		} catch (SecurityException e) {
			//log.debug("File attachment fail. Permission check. SecurityException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("File attachment fail. Permission check");
		} catch (IllegalStateException e) {
			//log.debug("File attachment fail. IllegalStateException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("File attachment fail. File State check");
		} catch (MessagingException e) {
			//log.debug("File attachment fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("File attachment fail");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//log.debug("File attachment encodeing fail. UnsupportedEncodingException: "+e.getMessage());
			throw new CustomException("File attachment encodeing fail");
		}

		try {
	        log.debug("Send mail start. " + mailDto.toString());

	        //메일 보내기
	        emailSender.send(message);
	        
	        log.debug("Send mail done. " + mailDto.toString());
		} catch (MailAuthenticationException e) {
			//log.debug("Email authentication fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Email authentication fail");
		} catch (MailSendException e) {
			//log.debug("Email send fail. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Email send fail");
		} catch (MailException e) {
			//log.debug("Email send() error. MessagingException: "+e.getMessage());
			e.printStackTrace();
			throw new CustomException("Email send() error");
//		} catch (IOException e) {
//			//log.debug("Email send() error. MessagingException: "+e.getMessage());
//			e.printStackTrace();
//			throw new CustomException("Email send() error");
		}
	}


	private static String sampleContent = "<html>\r\n"
			+ "<head> <title>:: 2023년 8월 급여 명세서 ::</title>\r\n"
			+ "<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>\r\n"
			+ "<meta id='viewport' name='viewport' content='width=642'>\r\n"
			+ "<style>@font-face {font-family: 'douzone';src: local('DOUZONEText10'),url('https://static.wehago.com/fonts/douzone/DOUZONEText10.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText10.woff') format('woff');font-weight: normal;font-display: fallback;}@font-face {font-family: 'douzone';src: local('DOUZONEText30'),url('https://static.wehago.com/fonts/douzone/DOUZONEText30.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText30.woff') format('woff');font-weight: bold;font-display: fallback;}@font-face {font-family: 'douzone';src: local('DOUZONEText50'),url('https://static.wehago.com/fonts/douzone/DOUZONEText50.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText50.woff') format('woff');font-weight: 900;font-display: fallback;}body,p,h1,h2,h3,h4,h5,h6,ul,ol,li,dl,dt,dd,table,th,td,form,fieldset,legend,input,textarea,img,button,select{margin:0;padding:0}body,h1,h2,h3,h4,h5,h6,ul,ol,li,dl,button{font-family:douzone,'Microsoft?YaHei','PingFang?SC','MS?PGothic','Hiragino?Kaku?Gothic?ProN','굴림',gulim,'Apple?SD?Gothic?Neo',sans-serif}body{min-width:642px;-webkit-text-size-adjust:none}img,fieldset{border:0;vertical-align:top}a{color:#1a1a1a}em,address{font-style:normal}ul,ol,li{list-style:none}label,button{cursor:pointer}input::-ms-clear{display:none}.blind{position:absolute !important;clip:rect(0 0 0 0) !important;width:1px !important;height:1px !important;margin:-1px !important;overflow:hidden !important}table{width:100%;table-layout: fixed;border-collapse:collapse;border-spacing: 0;width:100%}table thead.blind{position:static;font-size:0} /* 테이블 thead blind 버그해결 */.clearbx:after,.clearfix:after{content:'';clear:both;display:table}.dz_font,.dz_font *{font-family:douzone,'Microsoft?YaHei','PingFang?SC','MS?PGothic','Hiragino?Kaku?Gothic?ProN','굴림',gulim,'Apple?SD?Gothic?Neo',sans-serif}body{margin:70px auto;width:642px}.origin_tbl{border:1px solid #eaeaea;margin-top:10px}.origin_tbl + .origin_tbl{margin-top:5px}.head_title{font-size:15px;color:#191919;line-height:20px;text-align:center;font-weight:900;margin-bottom:20px}.topdate{font-size:11px;color:#4a4a4a;line-height:12px;letter-spacing: -.5px;font-weight:bold;text-align:right;margin-bottom:6px}.topdate > em {font-weight:900;}.txtlft {text-align:left !important}.txtrgt {text-align:right !important}.top_basictbl{border:0;table-layout: fixed;border-spacing: 0;margin-bottom:6px}.top_basictbl td{font-size: 11px;color: #4a4a4a;line-height: 12px;letter-spacing: -.5px;font-weight: bold}.top_basictbl td em{font-weight:900}.userinfo_tbl{border:2px solid #eee}.userinfo_tbl th{font-size:11px;color:#000;letter-spacing: -.5px;line-height:12px;font-weight:900;text-align:left;padding:6px 2px;vertical-align:top}.userinfo_tbl td{font-size:11px;color:#4a4a4a;letter-spacing: -.5px;line-height:12px;font-weight:bold;text-align:left;padding:6px 2px;vertical-align:top}.userinfo_tbl th > span{position:relative;display:block;padding-left:6px}.userinfo_tbl th > span:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.userinfo_tbl tr:first-of-type th,.userinfo_tbl tr:first-of-type td{padding-top:18px}.userinfo_tbl tr:last-of-type th,.userinfo_tbl tr:last-of-type td{padding-bottom:16px}.userinfo_tbl tr th:first-of-type{padding-left:24px}.userwork_tbl{border:1px solid #eaeaea;margin-top:10px}.userwork_tbl th{font-size:11px;color:#4a4a4a;line-height:12px;letter-spacing: -.5px;background:#f8f8f8;height:30px;border:1px solid #eaeaea}.userwork_tbl td{font-size:11px;color:#000;line-height:12px;letter-spacing: -.5px;background:#fff;height:30px;text-align:center;border:1px solid #eaeaea}.pay_details_tbl{margin-top:30px}.pay_details_tbl th{background:#f8f8f8;font-size:11px;font-weight:bold;letter-spacing: -.5px;color:#000;height:30px;border:1px solid #eaeaea;border-right:0;text-align:left;padding-left:12px}.pay_details_tbl td{background:#fff;font-size:11px;font-weight:bold;letter-spacing: -.5px;color:#000;text-align:right;height:30px;border:1px solid #eaeaea;border-left:0;padding-right:12px}.pay_details_tbl td.empty{border:0;background:#fff !important}.pay_details_tbl th.tit{background:#fff;border:0;font-size:12px;color:#000;font-weight:900;padding:0}.pay_details_tbl th.tit > span{position:relative;display:block;padding-left:6px}.pay_details_tbl th.tit > span:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.pay_details_tbl .total th,.pay_details_tbl .total td{border-color:#e6ebf2;background:#f2f6fa}.pay_details_tbl .total td{font-weight:900;font-size:12px}.realpay_dl {position:relative;border:1px solid #1c90fb;border-radius:6px;box-shadow:0 2px 6px rgba(0,0,0,.07);background:#f2f9ff;margin-top:16px;padding:16px 16px 14px;line-height:18px;clear:both;overflow:hidden}.realpay_dl dt{float:left;font-size:12px;color:#000;letter-spacing: -.55px;font-weight:bold}.realpay_dl dd{float:right;font-size:14px;color:#1c90fb;letter-spacing: -.7px;font-weight:900}.calcrule_tbl {margin-top:32px}.calcrule_tbl caption{position:relative;font-size:12px;color:#000;letter-spacing: -.33px;line-height:14px;text-align:left;font-weight:900;padding-left:6px;margin-bottom:10px}.calcrule_tbl caption:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.calcrule_tbl th,.calcrule_tbl td {font-size:11px;letter-spacing: -.5px;font-weight:bold;height:30px;border:1px solid #eaeaea;text-align:center}.calcrule_tbl th {color:#4a4a4a;background:#f8f8f8}.calcrule_tbl td {color:#000}.thx_text{font-size:12px;color:#000;font-weight:bold;line-height:14px;text-align:center;margin-top:24px}</style></head>\r\n"
			+ "<body leftmargin='0' topmargin='0' style='font-face:맑은고딕,Malgun Gothic, 돋음, dotum;' align='center'>\r\n"
			+ "<!--제목--->\r\n"
			+ "<table width='740px'>\r\n"
			+ "<tr align='center'>\r\n"
			+ "<td style='font-size: 16px;font-family: 돋음, dotum;color: #444444;padding:10px;'>\r\n"
			+ "<b>2023년 8월 급여 명세서</b></td></tr></table>\r\n"
			+ "<table class='top_basictbl'>\r\n"
			+ "<colgroup>\r\n"
			+ "<col style='width:50%'>\r\n"
			+ "<col></colgroup>\r\n"
			+ "<tbody>\r\n"
			+ "<tr>\r\n"
			+ "<td class='txtlft'>\r\n"
			+ "<p>\r\n"
			+ "<em>회사명</em> 디에이치(주)</p></td>\r\n"
			+ "<td class='txtrgt'>\r\n"
			+ "<p>\r\n"
			+ "<em>지급일</em> 2023.09.15</p></td></tr></tbody></table>\r\n"
			+ "<!--사원정보 테이블-->\r\n"
			+ "<table class='userinfo_tbl'>\r\n"
			+ "<colgroup>\r\n"
			+ "<col style='width:80px'>\r\n"
			+ "<col>\r\n"
			+ "<col style='width:60px'>\r\n"
			+ "<col style='width:18%'>\r\n"
			+ "<col style='width:60px'>\r\n"
			+ "<col style='width:18%'></colgroup>\r\n"
			+ "<tbody>\r\n"
			+ "<tr>\r\n"
			+ "<th>\r\n"
			+ "<span>사원코드</span></th>\r\n"
			+ "<td>22020</td>\r\n"
			+ "<th>\r\n"
			+ "<span>사원명</span></th>\r\n"
			+ "<td>하의진</td>\r\n"
			+ "<th>\r\n"
			+ "<span>생년월일</span></th>\r\n"
			+ "<td>1989년11월27일</td></tr>\r\n"
			+ "<tr> <th>\r\n"
			+ "<span>부서</span></th>\r\n"
			+ "<td>경영지원</td>\r\n"
			+ "<th>\r\n"
			+ "<span>직급</span></th>\r\n"
			+ "<td></td>\r\n"
			+ "<th>\r\n"
			+ "<span>호봉</span></th>\r\n"
			+ "<td></td></tr></tbody></table>\r\n"
			+ "<!--근로일수 및 시간 -->\r\n"
			+ "<table class='userwork_tbl'>\r\n"
			+ "<colgroup>\r\n"
			+ "<col style='width:20%' span='5'></colgroup>\r\n"
			+ "<thead>\r\n"
			+ "<tr>\r\n"
			+ "<th>연장근로시간</th>\r\n"
			+ "<th>야간근로시간</th>\r\n"
			+ "<th>휴일근로시간</th>\r\n"
			+ "<th colspan='2'>통상시급(원) </colspan></th></tr></thead>\r\n"
			+ "<tbody>\r\n"
			+ "<tr>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td colspan='2'></colspan></td></tr></tbody></table>\r\n"
			+ "<!--지급내역/공제내역 테이블-->\r\n"
			+ "<table width='740px' border='0' cellspacing='1' cellpadding='1' class = 'origin_tbl'>\r\n"
			+ "<tr>\r\n"
			+ "<td bgcolor='#b1c5db'>\r\n"
			+ "<table width='100%' border='0' cellspacing='0' cellpadding='0' style='table-layout:fixed; border-collapse:collapse;word-break:break-all;'>\r\n"
			+ "<!--지급내역-->\r\n"
			+ "<tr bgcolor='#f7f7f7' height='22px' align='center' style='font-size: 11px;font-family: 돋음, dotum;color: #666677;'>\r\n"
			+ "<td rowspan='8' width='16%' align='center' bgcolor='#cedff7' style='font-weight: bold; font-size: 12px;font-family: 돋음, dotum; color: #333355;border-right:1px solid #b1c5db;'>지급내역</td>\r\n"
			+ "<th width='14%'>기본급</th>\r\n"
			+ "<th width='14%'>연장수당1</th>\r\n"
			+ "<th width='14%'>연장수당2</th>\r\n"
			+ "<th width='14%'>야간수당2</th>\r\n"
			+ "<th width='14%'></th>\r\n"
			+ "<th width='14%'></th></tr>\r\n"
			+ "<tr bgcolor='#ffffff' height='22px' align='center' style='font-size: 12px;font-family: 돋음, dotum;color: #000000;'>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>2,000,000</td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>20,000</td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>500,000</td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>500,000</td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td></tr>\r\n"
			+ "<tr bgcolor='#f7f7f7' height='22px' align='center' style='font-size: 11px;font-family: 돋음, dotum;color: #666677;'>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th></tr>\r\n"
			+ "<tr bgcolor='#ffffff' height='22px' align='center' style='font-size: 12px;font-family: 돋음, dotum;color: #000000;'>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td></tr>\r\n"
			+ "<tr bgcolor='#f7f7f7' height='22px' align='center' style='font-size: 11px;font-family: 돋음, dotum;color: #666677;'>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th></tr>\r\n"
			+ "<tr bgcolor='#ffffff' height='22px' align='center' style='font-size: 12px;color: #000000;' >\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td></tr>\r\n"
			+ "<tr bgcolor='#f7f7f7' height='22px' align='center' style='font-size: 11px;font-family: 돋음, dotum;color: #666677;'>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th></tr>\r\n"
			+ "<tr bgcolor='#ffffff' height='22px' align='center' style='font-size: 12px;color: #000000;' >\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td>\r\n"
			+ "<td></td></tr>\r\n"
			+ "<!--//지급내역-->\r\n"
			+ "<tr>\r\n"
			+ "<td colspan='7' height='1px'></td></tr>\r\n"
			+ "<!--공제내역-->\r\n"
			+ "<tr bgcolor='#f7f7f7' height='22px' align='center' style='font-size: 11px;font-family: 돋음, dotum;color: #666677;'>\r\n"
			+ "<td rowspan='8' width='16%' align='center' bgcolor='#cedff7' style='font-weight: bold; font-size: 12px;font-family: 돋음, dotum; color: #333355;border-right:1px solid #b1c5db;'>공제내역</td>\r\n"
			+ "<th width='14%'>국민연금</th>\r\n"
			+ "<th width='14%'>건강보험</th>\r\n"
			+ "<th width='14%'>고용보험</th>\r\n"
			+ "<th width='14%'>장기요양보험료</th>\r\n"
			+ "<th width='14%'>소득세</th>\r\n"
			+ "<th width='14%'>지방소득세</th></tr>\r\n"
			+ "<tr bgcolor='#ffffff' height='22px' align='center' style='font-size: 12px;font-family: 돋음, dotum;color: #000000;'>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>131,220</td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>103,390</td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>27,180</td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>13,240</td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>76,060</td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'>7,600</td></tr>\r\n"
			+ "<tr bgcolor='#f7f7f7' height='22px' align='center' style='font-size: 11px;font-family: 돋음, dotum;color: #666677;'>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th></tr>\r\n"
			+ "<tr bgcolor='#ffffff' height='22px' align='center' style='font-size: 12px;font-family: 돋음, dotum;color: #000000;'>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td>\r\n"
			+ "<td style='border-bottom:1px solid #eee;'></td></tr>\r\n"
			+ "<tr bgcolor='#f7f7f7' height='22px' align='center' style='font-size: 12px;color: #666677;'>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th></tr>\r\n"
			+ "<tr bgcolor='#ffffff' height='22px' align='center' style='font-size: 12px;font-family: 돋음, dotum;color: #000000;'>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td></tr>\r\n"
			+ "<tr bgcolor='#f7f7f7' height='22px' align='center' style='font-size: 12px;color: #666677;'>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th>\r\n"
			+ "<th></th></tr>\r\n"
			+ "<tr bgcolor='#ffffff' height='22px' align='center' style='font-size: 12px;font-family: 돋음, dotum;color: #000000;'>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td></tr>\r\n"
			+ "<!--//공제내역--></table></td></tr></table>\r\n"
			+ "<!--합계데이블-->\r\n"
			+ "<table width='740px' border='0' cellspacing='1' cellpadding='1' class = 'origin_tbl'>\r\n"
			+ "<tr>\r\n"
			+ "<td bgcolor='#7f9db9'>\r\n"
			+ "<table width='100%' border='0' cellspacing='0' cellpadding='0' >\r\n"
			+ "<!--합계-->\r\n"
			+ "<tr bgcolor='#e7e7e7' height='22px' align='center' style='font-size: 11px;font-family: 돋음, dotum;color: #555;'>\r\n"
			+ "<td rowspan='6' width='16%' align='center' bgcolor='#a8bdd3' style='font-weight: bold; font-size: 12px;font-family: 돋음, dotum; color: #000;border-right:1px solid #7f9db9;'>합계</td>\r\n"
			+ "<th width='14%'></th>\r\n"
			+ "<th width='14%'></th>\r\n"
			+ "<th width='14%'>지급총액</th>\r\n"
			+ "<th width='14%'>공제총액</th>\r\n"
			+ "<th width='14%'></th>\r\n"
			+ "<th width='14%'>차인지급액</th></tr>\r\n"
			+ "<tr bgcolor='#ffffff' height='22px' align='center' style='font-size: 12px;font-family: 돋음, dotum;color: #000000;'>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'>3,020,000</td>\r\n"
			+ "<td width='14%'>358,690</td>\r\n"
			+ "<td width='14%'></td>\r\n"
			+ "<td width='14%'>2,661,310</td></tr></table></td></tr></table>\r\n"
			+ "<table class='calcrule_tbl'>\r\n"
			+ "<caption>계산방법</caption>\r\n"
			+ "<colgroup>\r\n"
			+ "<col style='width:30%'>\r\n"
			+ "<col>\r\n"
			+ "<col style='width:30%'></colgroup>\r\n"
			+ "<thead>\r\n"
			+ "<tr>\r\n"
			+ "<th>구분</th>\r\n"
			+ "<th>산출식 또는 산출방법</th>\r\n"
			+ "<th>지급액</th></tr></thead>\r\n"
			+ "<tbody>\r\n"
			+ "<tr>\r\n"
			+ "<td>기본급</td>\r\n"
			+ "<td>통상시급 * 209</td>\r\n"
			+ "<td>2,000,000</td></tr>\r\n"
			+ "<tr>\r\n"
			+ "<td>연장수당1</td>\r\n"
			+ "<td>연장근로시간 * 통상시급 * 1.5</td>\r\n"
			+ "<td>20,000</td></tr></tbody></table>\r\n"
			+ "<!--하단글-->\r\n"
			+ "<table width='740px'>\r\n"
			+ "<tr align='center' >\r\n"
			+ "<td style='font-size: 12px;font-family: 돋음, dotum;color: #444;padding:10px;'>\r\n"
			+ "<p>귀하의 노고에 감사드립니다.</p></td></tr></table></body></html>\r\n";
}
