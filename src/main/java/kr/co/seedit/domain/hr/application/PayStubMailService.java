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
	        helper = new MimeMessageHelper(message, true, "UTF-8");
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
				helper.addAttachment("급여명세서.html", new ByteArrayResource(sampleContent.getBytes("utf-8"),"euc-kr"));
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


	private static String sampleContent = "<html xmlns:o=\"urn:schemas-microsoft-com:office:office\"\r\n"
			+ "xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\r\n"
			+ "xmlns=\"http://www.w3.org/TR/REC-html40\">\r\n"
			+ "\r\n"
			+ "<head>\r\n"
			+ "<meta http-equiv=Content-Type content=\"text/html; charset=ks_c_5601-1987\">\r\n"
			+ "<meta name=ProgId content=Excel.Sheet>\r\n"
			+ "<meta name=Generator content=\"Microsoft Excel 15\">\r\n"
			+ "<link rel=File-List href=\"메일용.files/filelist.xml\">\r\n"
			+ "<style id=\"통합 문서8_24840_Styles\">\r\n"
			+ "<!--table\r\n"
			+ "	{mso-displayed-decimal-separator:\"\\.\";\r\n"
			+ "	mso-displayed-thousand-separator:\"\\,\";}\r\n"
			+ ".xl6324840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:11.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:\"맑은 고딕\", monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:general;\r\n"
			+ "	vertical-align:bottom;\r\n"
			+ "	mso-background-source:auto;\r\n"
			+ "	mso-pattern:auto;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl6424840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:14.0pt;\r\n"
			+ "	font-weight:700;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl6524840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:돋움체, monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:.5pt solid black;\r\n"
			+ "	border-right:none;\r\n"
			+ "	border-bottom:none;\r\n"
			+ "	border-left:.5pt solid black;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl6624840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:.5pt solid black;\r\n"
			+ "	border-right:none;\r\n"
			+ "	border-bottom:none;\r\n"
			+ "	border-left:none;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl6724840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:돋움체, monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:.5pt solid black;\r\n"
			+ "	border-right:none;\r\n"
			+ "	border-bottom:none;\r\n"
			+ "	border-left:none;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl6824840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:.5pt solid black;\r\n"
			+ "	border-right:.5pt solid black;\r\n"
			+ "	border-bottom:none;\r\n"
			+ "	border-left:none;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl6924840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:돋움체, monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:none;\r\n"
			+ "	border-right:none;\r\n"
			+ "	border-bottom:.5pt solid black;\r\n"
			+ "	border-left:.5pt solid black;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7024840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:none;\r\n"
			+ "	border-right:none;\r\n"
			+ "	border-bottom:.5pt solid black;\r\n"
			+ "	border-left:none;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7124840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:돋움체, monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:none;\r\n"
			+ "	border-right:none;\r\n"
			+ "	border-bottom:.5pt solid black;\r\n"
			+ "	border-left:none;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7224840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:none;\r\n"
			+ "	border-right:.5pt solid black;\r\n"
			+ "	border-bottom:.5pt solid black;\r\n"
			+ "	border-left:none;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7324840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:돋움체, monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:center;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border:.5pt solid black;\r\n"
			+ "	background:silver;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7424840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:8.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:center;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:.5pt solid black;\r\n"
			+ "	border-right:.5pt solid black;\r\n"
			+ "	border-bottom:none;\r\n"
			+ "	border-left:.5pt solid black;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7524840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:8.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:\"\\#\\#\\,\\#\\#\\#\";\r\n"
			+ "	text-align:right;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:.5pt solid black;\r\n"
			+ "	border-right:.5pt solid black;\r\n"
			+ "	border-bottom:none;\r\n"
			+ "	border-left:.5pt solid black;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7624840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:8.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:center;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:none;\r\n"
			+ "	border-right:.5pt solid black;\r\n"
			+ "	border-bottom:none;\r\n"
			+ "	border-left:.5pt solid black;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7724840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:8.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:\"\\#\\#\\,\\#\\#\\#\";\r\n"
			+ "	text-align:right;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:none;\r\n"
			+ "	border-right:.5pt solid black;\r\n"
			+ "	border-bottom:none;\r\n"
			+ "	border-left:.5pt solid black;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7824840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:8.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:돋움체, monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:none;\r\n"
			+ "	border-right:.5pt solid black;\r\n"
			+ "	border-bottom:.5pt solid black;\r\n"
			+ "	border-left:.5pt solid black;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl7924840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:8.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:돋움체, monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border-top:none;\r\n"
			+ "	border-right:.5pt solid black;\r\n"
			+ "	border-bottom:none;\r\n"
			+ "	border-left:.5pt solid black;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl8024840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:10.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:\"\\#\\#\\,\\#\\#\\#\";\r\n"
			+ "	text-align:right;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	border:.5pt solid black;\r\n"
			+ "	background:silver;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl8124840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:9.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:돋움체, monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:left;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ ".xl8224840\r\n"
			+ "	{padding-top:1px;\r\n"
			+ "	padding-right:1px;\r\n"
			+ "	padding-left:1px;\r\n"
			+ "	mso-ignore:padding;\r\n"
			+ "	color:black;\r\n"
			+ "	font-size:9.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:바탕체, serif;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-number-format:General;\r\n"
			+ "	text-align:right;\r\n"
			+ "	vertical-align:middle;\r\n"
			+ "	background:white;\r\n"
			+ "	mso-pattern:black none;\r\n"
			+ "	white-space:nowrap;}\r\n"
			+ "ruby\r\n"
			+ "	{ruby-align:left;}\r\n"
			+ "rt\r\n"
			+ "	{color:windowtext;\r\n"
			+ "	font-size:8.0pt;\r\n"
			+ "	font-weight:400;\r\n"
			+ "	font-style:normal;\r\n"
			+ "	text-decoration:none;\r\n"
			+ "	font-family:\"맑은 고딕\", monospace;\r\n"
			+ "	mso-font-charset:129;\r\n"
			+ "	mso-char-type:none;}\r\n"
			+ "-->\r\n"
			+ "</style>\r\n"
			+ "</head>\r\n"
			+ "\r\n"
			+ "<body>\r\n"
			+ "<!--[if !excel]>　　<![endif]-->\r\n"
			+ "<!--다음 내용은 Microsoft Excel의 웹 페이지로 게시 마법사를 사용하여 작성되었습니다.-->\r\n"
			+ "<!--같은 내용의 항목이 다시 게시되면 DIV 태그 사이에 있는 내용이 변경됩니다.-->\r\n"
			+ "<!----------------------------->\r\n"
			+ "<!--Excel의 웹 페이지 마법사로 게시해서 나온 결과의 시작 -->\r\n"
			+ "<!----------------------------->\r\n"
			+ "\r\n"
			+ "<div id=\"통합 문서8_24840\" align=center x:publishsource=\"Excel\">\r\n"
			+ "\r\n"
			+ "<table border=0 cellpadding=0 cellspacing=0 width=644 class=xl6324840\r\n"
			+ " style='border-collapse:collapse;table-layout:fixed;width:485pt'>\r\n"
			+ " <col class=xl6324840 width=70 style='mso-width-source:userset;mso-width-alt:\r\n"
			+ " 2240;width:53pt'>\r\n"
			+ " <col class=xl6324840 width=126 style='mso-width-source:userset;mso-width-alt:\r\n"
			+ " 4032;width:95pt'>\r\n"
			+ " <col class=xl6324840 width=70 style='mso-width-source:userset;mso-width-alt:\r\n"
			+ " 2240;width:53pt'>\r\n"
			+ " <col class=xl6324840 width=126 style='mso-width-source:userset;mso-width-alt:\r\n"
			+ " 4032;width:95pt'>\r\n"
			+ " <col class=xl6324840 width=84 style='mso-width-source:userset;mso-width-alt:\r\n"
			+ " 2688;width:63pt'>\r\n"
			+ " <col class=xl6324840 width=168 style='mso-width-source:userset;mso-width-alt:\r\n"
			+ " 5376;width:126pt'>\r\n"
			+ " <tr height=23 style='mso-height-source:userset;height:17.25pt'>\r\n"
			+ "  <td height=23 class=xl6324840 width=70 style='height:17.25pt;width:53pt'></td>\r\n"
			+ "  <td class=xl6324840 width=126 style='width:95pt'></td>\r\n"
			+ "  <td class=xl6324840 width=70 style='width:53pt'></td>\r\n"
			+ "  <td class=xl6424840 width=126 style='width:95pt'>2023년 8월<span\r\n"
			+ "  style='display:none'>분 급여명세서</span></td>\r\n"
			+ "  <td class=xl6324840 width=84 style='width:63pt'></td>\r\n"
			+ "  <td class=xl6324840 width=168 style='width:126pt'></td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=13 style='mso-height-source:userset;height:9.75pt'>\r\n"
			+ "  <td height=13 class=xl6324840 style='height:9.75pt'></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=28 style='mso-height-source:userset;height:21.0pt'>\r\n"
			+ "  <td height=28 class=xl6524840 style='height:21.0pt'>사원코드 :</td>\r\n"
			+ "  <td class=xl6624840>3333</td>\r\n"
			+ "  <td class=xl6724840>사 원 명 :</td>\r\n"
			+ "  <td class=xl6624840>박보라</td>\r\n"
			+ "  <td class=xl6724840>입 사 일 :</td>\r\n"
			+ "  <td class=xl6824840>2023-08-01</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=28 style='mso-height-source:userset;height:21.0pt'>\r\n"
			+ "  <td height=28 class=xl6924840 style='height:21.0pt'>부<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp;&nbsp;&nbsp; </span>서 :</td>\r\n"
			+ "  <td class=xl7024840>임원실</td>\r\n"
			+ "  <td class=xl7124840>직<span style='mso-spacerun:yes'>&nbsp;&nbsp;&nbsp;\r\n"
			+ "  </span>급 :</td>\r\n"
			+ "  <td class=xl7024840>이사</td>\r\n"
			+ "  <td class=xl7124840>호<span style='mso-spacerun:yes'>&nbsp;&nbsp;&nbsp;\r\n"
			+ "  </span>봉 :</td>\r\n"
			+ "  <td class=xl7224840>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=28 style='mso-height-source:userset;height:21.0pt'>\r\n"
			+ "  <td height=28 class=xl7324840 style='height:21.0pt;border-top:none'>지<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp;&nbsp; </span>급<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp;&nbsp; </span>내<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp;&nbsp; </span>역</td>\r\n"
			+ "  <td colspan=2 class=xl7324840 style='border-left:none'>지<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp;&nbsp; </span>급<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp;&nbsp; </span>액</td>\r\n"
			+ "  <td colspan=2 class=xl7324840 style='border-left:none'>공<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp; </span>제<span style='mso-spacerun:yes'>&nbsp;\r\n"
			+ "  </span>내<span style='mso-spacerun:yes'>&nbsp; </span>역</td>\r\n"
			+ "  <td class=xl7324840 style='border-top:none;border-left:none'>공<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp; </span>제<span style='mso-spacerun:yes'>&nbsp;\r\n"
			+ "  </span>액</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7424840 style='height:10.5pt;border-top:none'>기본급</td>\r\n"
			+ "  <td colspan=2 class=xl7524840 style='border-left:none'>5,300,000</td>\r\n"
			+ "  <td colspan=2 class=xl7424840 style='border-left:none'>국민연금</td>\r\n"
			+ "  <td class=xl7524840 style='border-top:none;border-left:none'>120,000</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>연차수당</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>건강보험</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>115,000</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>연장수당1</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>고용보험</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>72,000</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>연장수당2</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>1,900,000</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>장기요양보험료</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>14,730</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>야간수당1</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>소득세</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>959,500</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>야간수당2</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>800,000</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>지방소득세</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>95,950</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>휴일수당1</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>가불금</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>휴일수당2</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>기타공제</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>직책수당</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>경조비</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>50,000</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>기타수당</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>연말정산</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>보조금</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>건강보험정산</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>교통비</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>장기요양보험정산</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>식대</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>연차수당</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>　</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>　</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=14 style='mso-height-source:userset;height:10.5pt'>\r\n"
			+ "  <td height=14 class=xl7624840 style='height:10.5pt'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7624840 style='border-left:none'>　</td>\r\n"
			+ "  <td class=xl7724840 style='border-left:none'>　</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=31 style='mso-height-source:userset;height:23.25pt'>\r\n"
			+ "  <td height=31 class=xl7824840 style='height:23.25pt'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7924840 style='border-left:none'>　</td>\r\n"
			+ "  <td colspan=2 class=xl7324840 style='border-left:none'>공<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp; </span>제<span style='mso-spacerun:yes'>&nbsp;\r\n"
			+ "  </span>액<span style='mso-spacerun:yes'>&nbsp; </span>계</td>\r\n"
			+ "  <td class=xl8024840 style='border-left:none'>1,427,180</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=28 style='mso-height-source:userset;height:21.0pt'>\r\n"
			+ "  <td height=28 class=xl7324840 style='height:21.0pt;border-top:none'>지<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp; </span>급<span style='mso-spacerun:yes'>&nbsp;\r\n"
			+ "  </span>액<span style='mso-spacerun:yes'>&nbsp; </span>계</td>\r\n"
			+ "  <td colspan=2 class=xl8024840 style='border-left:none'>8,000,000</td>\r\n"
			+ "  <td colspan=2 class=xl7324840 style='border-left:none'>차 인 지 급 액</td>\r\n"
			+ "  <td class=xl8024840 style='border-top:none;border-left:none'>6,572,820</td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=2 style='mso-height-source:userset;height:1.5pt'>\r\n"
			+ "  <td height=2 class=xl6324840 style='height:1.5pt'></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ " </tr>\r\n"
			+ " <tr height=20 style='mso-height-source:userset;height:15.0pt'>\r\n"
			+ "  <td height=20 class=xl8124840 style='height:15.0pt'>※<span\r\n"
			+ "  style='mso-spacerun:yes'>&nbsp; </span>귀하의 <span style='display:none'>노고에\r\n"
			+ "  감사드립니다</span></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl6324840></td>\r\n"
			+ "  <td class=xl8224840>디에이치(주) 평택공장</td>\r\n"
			+ " </tr>\r\n"
			+ " <![if supportMisalignedColumns]>\r\n"
			+ " <tr height=0 style='display:none'>\r\n"
			+ "  <td width=70 style='width:53pt'></td>\r\n"
			+ "  <td width=126 style='width:95pt'></td>\r\n"
			+ "  <td width=70 style='width:53pt'></td>\r\n"
			+ "  <td width=126 style='width:95pt'></td>\r\n"
			+ "  <td width=84 style='width:63pt'></td>\r\n"
			+ "  <td width=168 style='width:126pt'></td>\r\n"
			+ " </tr>\r\n"
			+ " <![endif]>\r\n"
			+ "</table>\r\n"
			+ "\r\n"
			+ "</div>\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "<!----------------------------->\r\n"
			+ "<!--Excel의 웹 페이지 마법사로 게시해서 나온 결과의 끝-->\r\n"
			+ "<!----------------------------->\r\n"
			+ "</body>\r\n"
			+ "\r\n"
			+ "</html>\r\n";
}
