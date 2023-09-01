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
			// 첨부파일
			String content = "<html><head> <title>:: "
					+ "2023년 8"
					+ "월 급여 명세서 ::</title><meta http-equiv='Content-Type' content='text/html; charset=euc-kr'><meta id='viewport' name='viewport' content='width=642'><style>@font-face {font-family: 'douzone';src: local('DOUZONEText10'),url('https://static.wehago.com/fonts/douzone/DOUZONEText10.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText10.woff') format('woff');font-weight: normal;font-display: fallback;}@font-face {font-family: 'douzone';src: local('DOUZONEText30'),url('https://static.wehago.com/fonts/douzone/DOUZONEText30.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText30.woff') format('woff');font-weight: bold;font-display: fallback;}@font-face {font-family: 'douzone';src: local('DOUZONEText50'),url('https://static.wehago.com/fonts/douzone/DOUZONEText50.woff2') format('woff2'),url('https://static.wehago.com/fonts/douzone/DOUZONEText50.woff') format('woff');font-weight: 900;font-display: fallback;}body,p,h1,h2,h3,h4,h5,h6,ul,ol,li,dl,dt,dd,table,th,td,form,fieldset,legend,input,textarea,img,button,select{margin:0;padding:0}body,h1,h2,h3,h4,h5,h6,ul,ol,li,dl,button{font-family:douzone,'Microsoft?YaHei','PingFang?SC','MS?PGothic','Hiragino?Kaku?Gothic?ProN','굴림',gulim,'Apple?SD?Gothic?Neo',sans-serif}body{min-width:642px;-webkit-text-size-adjust:none}img,fieldset{border:0;vertical-align:top}a{color:#1a1a1a}em,address{font-style:normal}ul,ol,li{list-style:none}label,button{cursor:pointer}input::-ms-clear{display:none}.blind{position:absolute !important;clip:rect(0 0 0 0) !important;width:1px !important;height:1px !important;margin:-1px !important;overflow:hidden !important}table{width:100%;table-layout: fixed;border-collapse:collapse;border-spacing: 0;width:100%}table thead.blind{position:static;font-size:0} /* 테이블 thead blind 버그해결 */.clearbx:after,.clearfix:after{content:'';clear:both;display:table}.dz_font,.dz_font *{font-family:douzone,'Microsoft?YaHei','PingFang?SC','MS?PGothic','Hiragino?Kaku?Gothic?ProN','굴림',gulim,'Apple?SD?Gothic?Neo',sans-serif}body{margin:70px auto;width:642px}.origin_tbl{border:1px solid #eaeaea;margin-top:10px}.origin_tbl + .origin_tbl{margin-top:5px}.head_title{font-size:15px;color:#191919;line-height:20px;text-align:center;font-weight:900;margin-bottom:20px}.topdate{font-size:11px;color:#4a4a4a;line-height:12px;letter-spacing: -.5px;font-weight:bold;text-align:right;margin-bottom:6px}.topdate > em {font-weight:900;}.txtlft {text-align:left !important}.txtrgt {text-align:right !important}.top_basictbl{border:0;table-layout: fixed;border-spacing: 0;margin-bottom:6px}.top_basictbl td{font-size: 11px;color: #4a4a4a;line-height: 12px;letter-spacing: -.5px;font-weight: bold}.top_basictbl td em{font-weight:900}.userinfo_tbl{border:2px solid #eee}.userinfo_tbl th{font-size:11px;color:#000;letter-spacing: -.5px;line-height:12px;font-weight:900;text-align:left;padding:6px 2px;vertical-align:top}.userinfo_tbl td{font-size:11px;color:#4a4a4a;letter-spacing: -.5px;line-height:12px;font-weight:bold;text-align:left;padding:6px 2px;vertical-align:top}.userinfo_tbl th > span{position:relative;display:block;padding-left:6px}.userinfo_tbl th > span:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.userinfo_tbl tr:first-of-type th,.userinfo_tbl tr:first-of-type td{padding-top:18px}.userinfo_tbl tr:last-of-type th,.userinfo_tbl tr:last-of-type td{padding-bottom:16px}.userinfo_tbl tr th:first-of-type{padding-left:24px}.userwork_tbl{border:1px solid #eaeaea;margin-top:10px}.userwork_tbl th{font-size:11px;color:#4a4a4a;line-height:12px;letter-spacing: -.5px;background:#f8f8f8;height:30px;border:1px solid #eaeaea}.userwork_tbl td{font-size:11px;color:#000;line-height:12px;letter-spacing: -.5px;background:#fff;height:30px;text-align:center;border:1px solid #eaeaea}.pay_details_tbl{margin-top:30px}.pay_details_tbl th{background:#f8f8f8;font-size:11px;font-weight:bold;letter-spacing: -.5px;color:#000;height:30px;border:1px solid #eaeaea;border-right:0;text-align:left;padding-left:12px}.pay_details_tbl td{background:#fff;font-size:11px;font-weight:bold;letter-spacing: -.5px;color:#000;text-align:right;height:30px;border:1px solid #eaeaea;border-left:0;padding-right:12px}.pay_details_tbl td.empty{border:0;background:#fff !important}.pay_details_tbl th.tit{background:#fff;border:0;font-size:12px;color:#000;font-weight:900;padding:0}.pay_details_tbl th.tit > span{position:relative;display:block;padding-left:6px}.pay_details_tbl th.tit > span:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.pay_details_tbl .total th,.pay_details_tbl .total td{border-color:#e6ebf2;background:#f2f6fa}.pay_details_tbl .total td{font-weight:900;font-size:12px}.realpay_dl {position:relative;border:1px solid #1c90fb;border-radius:6px;box-shadow:0 2px 6px rgba(0,0,0,.07);background:#f2f9ff;margin-top:16px;padding:16px 16px 14px;line-height:18px;clear:both;overflow:hidden}.realpay_dl dt{float:left;font-size:12px;color:#000;letter-spacing: -.55px;font-weight:bold}.realpay_dl dd{float:right;font-size:14px;color:#1c90fb;letter-spacing: -.7px;font-weight:900}.calcrule_tbl {margin-top:32px}.calcrule_tbl caption{position:relative;font-size:12px;color:#000;letter-spacing: -.33px;line-height:14px;text-align:left;font-weight:900;padding-left:6px;margin-bottom:10px}.calcrule_tbl caption:before{content:'';position:absolute;top:50%;left:0;width:2px;height:2px;border-radius:50%;background:#000;margin-top:-2px}.calcrule_tbl th,.calcrule_tbl td {font-size:11px;letter-spacing: -.5px;font-weight:bold;height:30px;border:1px solid #eaeaea;text-align:center}.calcrule_tbl th {color:#4a4a4a;background:#f8f8f8}.calcrule_tbl td {color:#000}.thx_text{font-size:12px;color:#000;font-weight:bold;line-height:14px;text-align:center;margin-top:24px}</style></head><body leftmargin='0' topmargin='0' style='font-face:맑은고딕,Malgun Gothic, 돋음, dotum;' align='center'>"
					+ "<div id='precheck' style='font-size: 16px;font-family: 돋음, dotum;color: #444444;padding:10px;'>본 메일은 고객님의 정보보호를 위해 암호화된 보안메일입니다.</br></br>내용을 확인하시려면 생년월일 6자리를 입력 후 확인해 주시기 바랍니다.</br><input type=\"text\" id=\"infor\" class=\"inputButton\"/><button onclick=\"viewdetail()\">명세서 보기</button></div><div id=\"detail\" style=\"visibility: hidden;\"></div>"
					+ "</body><script type = \"text/javascript\">var Aes ={};Aes.cipher = function(input, w){var Nb = 4; var Nr = w.length/Nb - 1; var state = [[],[],[],[]]; for (var i=0; i<4*Nb; i++){state[i%4][Math.floor(i/4)] = input[i];}state = Aes.addRoundKey(state, w, 0, Nb); for (var round=1; round<Nr; round++){state = Aes.subBytes(state, Nb); state = Aes.shiftRows(state, Nb); state = Aes.mixColumns(state, Nb); state = Aes.addRoundKey(state, w, round, Nb);}state = Aes.subBytes(state, Nb); state = Aes.shiftRows(state, Nb); state = Aes.addRoundKey(state, w, Nr, Nb); var output = new Array(4*Nb); for (var i=0; i<4*Nb; i++){output[i] = state[i%4][Math.floor(i/4)];}return output;};Aes.keyExpansion = function(key){var Nb = 4; var Nk = key.length/4; var Nr = Nk + 6; var w = new Array(Nb*(Nr+1)); var temp = new Array(4); for (var i=0; i<Nk; i++){var r = [key[4*i], key[4*i+1], key[4*i+2], key[4*i+3]]; w[i] = r;}for (var i=Nk; i<(Nb*(Nr+1)); i++){w[i] = new Array(4); for (var t=0; t<4; t++){temp[t] = w[i-1][t];}if (i % Nk == 0){temp = Aes.subWord(Aes.rotWord(temp)); for (var t=0; t<4; t++){temp[t] ^= Aes.rCon[i/Nk][t];}}else if (Nk > 6 && i%Nk == 4){temp = Aes.subWord(temp);}for (var t=0; t<4; t++){w[i][t] = w[i-Nk][t] ^ temp[t];}}return w;};Aes.subBytes = function(s, Nb){for (var r=0; r<4; r++){for (var c=0; c<Nb; c++){s[r][c] = Aes.sBox[s[r][c]];}}return s;};Aes.shiftRows = function(s, Nb){var t = new Array(4); for (var r=1; r<4; r++){for (var c=0; c<4; c++){t[c] = s[r][(c+r)%Nb];}for (var c=0; c<4; c++){s[r][c] = t[c];}}return s;};Aes.mixColumns = function(s, Nb){for (var c=0; c<4; c++){var a = new Array(4); var b = new Array(4); for (var i=0; i<4; i++){a[i] = s[i][c]; b[i] = s[i][c]&0x80 ? s[i][c]<<1 ^ 0x011b : s[i][c]<<1;}s[0][c] = b[0] ^ a[1] ^ b[1] ^ a[2] ^ a[3]; s[1][c] = a[0] ^ b[1] ^ a[2] ^ b[2] ^ a[3]; s[2][c] = a[0] ^ a[1] ^ b[2] ^ a[3] ^ b[3]; s[3][c] = a[0] ^ b[0] ^ a[1] ^ a[2] ^ b[3];}return s;};Aes.addRoundKey = function(state, w, rnd, Nb){for (var r=0; r<4; r++){for (var c=0; c<Nb; c++){state[r][c] ^= w[rnd*4+c][r];}}return state;};Aes.subWord = function(w){for (var i=0; i<4; i++){w[i] = Aes.sBox[w[i]];}return w;};Aes.rotWord = function(w){var tmp = w[0]; for (var i=0; i<3; i++){w[i] = w[i+1];}w[3] = tmp; return w;};Aes.sBox = [0x63,0x7c,0x77,0x7b,0xf2,0x6b,0x6f,0xc5,0x30,0x01,0x67,0x2b,0xfe,0xd7,0xab,0x76, 0xca,0x82,0xc9,0x7d,0xfa,0x59,0x47,0xf0,0xad,0xd4,0xa2,0xaf,0x9c,0xa4,0x72,0xc0, 0xb7,0xfd,0x93,0x26,0x36,0x3f,0xf7,0xcc,0x34,0xa5,0xe5,0xf1,0x71,0xd8,0x31,0x15, 0x04,0xc7,0x23,0xc3,0x18,0x96,0x05,0x9a,0x07,0x12,0x80,0xe2,0xeb,0x27,0xb2,0x75, 0x09,0x83,0x2c,0x1a,0x1b,0x6e,0x5a,0xa0,0x52,0x3b,0xd6,0xb3,0x29,0xe3,0x2f,0x84, 0x53,0xd1,0x00,0xed,0x20,0xfc,0xb1,0x5b,0x6a,0xcb,0xbe,0x39,0x4a,0x4c,0x58,0xcf, 0xd0,0xef,0xaa,0xfb,0x43,0x4d,0x33,0x85,0x45,0xf9,0x02,0x7f,0x50,0x3c,0x9f,0xa8, 0x51,0xa3,0x40,0x8f,0x92,0x9d,0x38,0xf5,0xbc,0xb6,0xda,0x21,0x10,0xff,0xf3,0xd2, 0xcd,0x0c,0x13,0xec,0x5f,0x97,0x44,0x17,0xc4,0xa7,0x7e,0x3d,0x64,0x5d,0x19,0x73, 0x60,0x81,0x4f,0xdc,0x22,0x2a,0x90,0x88,0x46,0xee,0xb8,0x14,0xde,0x5e,0x0b,0xdb, 0xe0,0x32,0x3a,0x0a,0x49,0x06,0x24,0x5c,0xc2,0xd3,0xac,0x62,0x91,0x95,0xe4,0x79, 0xe7,0xc8,0x37,0x6d,0x8d,0xd5,0x4e,0xa9,0x6c,0x56,0xf4,0xea,0x65,0x7a,0xae,0x08, 0xba,0x78,0x25,0x2e,0x1c,0xa6,0xb4,0xc6,0xe8,0xdd,0x74,0x1f,0x4b,0xbd,0x8b,0x8a, 0x70,0x3e,0xb5,0x66,0x48,0x03,0xf6,0x0e,0x61,0x35,0x57,0xb9,0x86,0xc1,0x1d,0x9e, 0xe1,0xf8,0x98,0x11,0x69,0xd9,0x8e,0x94,0x9b,0x1e,0x87,0xe9,0xce,0x55,0x28,0xdf, 0x8c,0xa1,0x89,0x0d,0xbf,0xe6,0x42,0x68,0x41,0x99,0x2d,0x0f,0xb0,0x54,0xbb,0x16]; ;Aes.rCon = [ [0x00, 0x00, 0x00, 0x00], [0x01, 0x00, 0x00, 0x00], [0x02, 0x00, 0x00, 0x00], [0x04, 0x00, 0x00, 0x00], [0x08, 0x00, 0x00, 0x00], [0x10, 0x00, 0x00, 0x00], [0x20, 0x00, 0x00, 0x00], [0x40, 0x00, 0x00, 0x00], [0x80, 0x00, 0x00, 0x00], [0x1b, 0x00, 0x00, 0x00], [0x36, 0x00, 0x00, 0x00] ]; ;Aes.Ctr ={}; Aes.Ctr.convert = function(ch, key, n){var blockSize = 16; if (!(n==128 || n==192 || n==256)){return '';}ch = Base64.decode(ch); key = Utf8.encode(key); var nBytes = n/8; var pwBytes = new Array(nBytes); for (var i=0; i<nBytes; i++){pwBytes[i] = isNaN(key.charCodeAt(i)) ? 0 : key.charCodeAt(i);}var key = Aes.cipher(pwBytes, Aes.keyExpansion(pwBytes)); key = key.concat(key.slice(0, nBytes-16)); var counterBlock = new Array(8); ctrTxt = ch.slice(0, 8); for (var i=0; i<8; i++){counterBlock[i] = ctrTxt.charCodeAt(i);}var keySchedule = Aes.keyExpansion(key); var nBlocks = Math.ceil((ch.length-8) / blockSize); var ct = new Array(nBlocks); for (var b=0; b<nBlocks; b++){ct[b] = ch.slice(8+b*blockSize, 8+b*blockSize+blockSize);}ch = ct; var plaintxt = new Array(ch.length); for (var b=0; b<nBlocks; b++){for (var c=0; c<4; c++){counterBlock[15-c] = ((b) >>> c*8) & 0xff;}for (var c=0; c<4; c++){counterBlock[15-c-4] = (((b+1)/0x100000000-1) >>> c*8) & 0xff;}var cipherCntr = Aes.cipher(counterBlock, keySchedule); var plaintxtByte = new Array(ch[b].length); for (var i=0; i<ch[b].length; i++){plaintxtByte[i] = cipherCntr[i] ^ ch[b].charCodeAt(i); plaintxtByte[i] = String.fromCharCode(plaintxtByte[i]);}plaintxt[b] = plaintxtByte.join('');}var plaintext = plaintxt.join(''); plaintext = Utf8.decode(plaintext); return plaintext;};var Base64 ={}; Base64.code = \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=\"; ;Base64.encode = function(str, utf8encode){utf8encode = (typeof utf8encode == 'undefined') ? false : utf8encode; var o1, o2, o3, bits, h1, h2, h3, h4, e=[], pad = '', c, plain, coded; var b64 = Base64.code; plain = utf8encode ? str.encodeUTF8() : str; c = plain.length % 3; if (c > 0){while (c++ < 3){pad += '='; plain += '\\0';}}for (c=0; c<plain.length; c+=3){o1 = plain.charCodeAt(c); o2 = plain.charCodeAt(c+1); o3 = plain.charCodeAt(c+2); bits = o1<<16 | o2<<8 | o3; h1 = bits>>18 & 0x3f; h2 = bits>>12 & 0x3f; h3 = bits>>6 & 0x3f; h4 = bits & 0x3f; e[c/3] = b64.charAt(h1) + b64.charAt(h2) + b64.charAt(h3) + b64.charAt(h4);}coded = e.join(''); coded = coded.slice(0, coded.length-pad.length) + pad; return coded;};Base64.decode = function(str, utf8decode){utf8decode = (typeof utf8decode == 'undefined') ? false : utf8decode; var o1, o2, o3, h1, h2, h3, h4, bits, d=[], plain, coded; var b64 = Base64.code; coded = utf8decode ? str.decodeUTF8() : str; for (var c=0; c<coded.length; c+=4){h1 = b64.indexOf(coded.charAt(c)); h2 = b64.indexOf(coded.charAt(c+1)); h3 = b64.indexOf(coded.charAt(c+2)); h4 = b64.indexOf(coded.charAt(c+3)); bits = h1<<18 | h2<<12 | h3<<6 | h4; o1 = bits>>>16 & 0xff; o2 = bits>>>8 & 0xff; o3 = bits & 0xff; d[c/4] = String.fromCharCode(o1, o2, o3); if (h4 == 0x40){d[c/4] = String.fromCharCode(o1, o2);}if (h3 == 0x40){d[c/4] = String.fromCharCode(o1);}}plain = d.join(''); return utf8decode ? plain.decodeUTF8() : plain;};var Utf8 ={};Utf8.encode = function(strUni){var strUtf = strUni.replace( /[\\u0080-\\u07ff]/g, function(c){var cc = c.charCodeAt(0); return String.fromCharCode(0xc0 | cc>>6, 0x80 | cc&0x3f);}); strUtf = strUtf.replace( /[\\u0800-\\uffff]/g, function(c){var cc = c.charCodeAt(0); return String.fromCharCode(0xe0 | cc>>12, 0x80 | cc>>6&0x3F, 0x80 | cc&0x3f);}); return strUtf;};Utf8.decode = function(strUtf){var strUni = strUtf.replace( /[\\u00e0-\\u00ef][\\u0080-\\u00bf][\\u0080-\\u00bf]/g, function(c){var cc = ((c.charCodeAt(0)&0x0f)<<12) | ((c.charCodeAt(1)&0x3f)<<6) | ( c.charCodeAt(2)&0x3f); return String.fromCharCode(cc);}); strUni = strUni.replace( /[\\u00c0-\\u00df][\\u0080-\\u00bf]/g, function(c){var cc = (c.charCodeAt(0)&0x1f)<<6 | c.charCodeAt(1)&0x3f; return String.fromCharCode(cc);}); return strUni;};var input = document.getElementById(\"infor\"); input.addEventListener(\"keyup\", function (event){if (event.keyCode === 13){if (document.getElementById(\"infor\").value != ''){event.preventDefault(); viewdetail();}}});"
					+ "function viewdetail() { let input = document.getElementById(\"infor\").value; if(input == ''){ alert(\"비밀번호를 바르게 입력해주세요\"); document.getElementById(\"infor\").focus(); return false; };"
					+ "document.getElementById('detail').innerHTML = Aes.Ctr.convert('"
					// base64, urlencoding
					+ "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"
					+ "',input,256);"
					+ "if (null != document.getElementById('dec')) {document.getElementById(\"precheck\").remove();document.getElementById(\"dec\").style.visibility ='visible';}else{alert(\"비밀번호가 일치하지 않습니다\");}}</script></html>";
			helper.addAttachment("급여명세서.html", new ByteArrayResource(content.getBytes("utf-8"),"utf-8"));
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
}
