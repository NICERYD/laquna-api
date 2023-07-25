package kr.co.seedit.global.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  public static String nullToStr(String paramStr) {

    if (paramStr == null || paramStr.length() == 0)
      return "";
    else
      return paramStr.trim();
  }

  public static boolean isEmpty(CharSequence cs) {

    return cs == null || cs.length() == 0;
  }

  @SuppressWarnings("unchecked")
  public String getValueFromMap(Object obj, String keyName) {

    Map<String, Object> paramMap = (Map<String, Object>)obj;

    String retVal = "";

    if (paramMap == null || paramMap.isEmpty() || !paramMap.containsKey(keyName) || paramMap.get(keyName) == null) {

    } else {
      retVal = (String)paramMap.get(keyName) == null ? "" : (String)paramMap.get(keyName);

      if (retVal == null || retVal.length() == 0) {
        retVal = "";
      } else {
        retVal.trim();
      }
    }
    return retVal;
  }

  public static boolean equals(String s, String s1) {
    if (s == null && s1 == null)
      return true;
    if (s == null || s1 == null)
      return false;
    else
      return s.equals(s1);
  }

  public static String normalizeNull(Object o) {
    return normalizeNull(String.valueOf(o));
  }

  public static String normalizeNull(String s) {
    if (s == null)
      return "";
    if (s.equals("null"))
      return "";
    if (s.equals("undefined"))
      return "";
    else
      return s;
  }

  // id 가운데 글자 마스킹
  public static String idMasking(String id) throws Exception {
    String maskingId = id.replaceAll("(?<=.{4}).", "*");
    return maskingId;
  }

  // 이름 가운데 글자 마스킹
  public static String nameMasking(String name) throws Exception {
    int length = name.length();
    String middleMask = "";
    if (length > 2) {
      middleMask = name.substring(1, length - 1);
    } else {
      // 이름이 외자
      middleMask = name.substring(1, length);
    }
    String dot = "";
    for (int i = 0; i < middleMask.length(); i++) {

      dot += "*";
    }
    if (length > 2) {
      return name.substring(0, 1) + middleMask.replace(middleMask, dot) + name.substring(length - 1, length);
    } else {
      // 이름이 외자 마스킹 리턴
      return name.substring(0, 1) + middleMask.replace(middleMask, dot);
    }
  }

  // 휴대폰번호 마스킹(가운데 숫자 4자리 마스킹)
  public static String phoneMasking(String phoneNo) throws Exception {
    String regex = "(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";
    Matcher matcher = Pattern.compile(regex).matcher(phoneNo);
    if (matcher.find()) {
      String target = matcher.group(3);
      int length = target.length();
      char[] c = new char[length];
      Arrays.fill(c, '*');
      return phoneNo.replace(target, String.valueOf(c));
    }
    return phoneNo;
  }

  // ip 마스킹(가운데 숫자 4자리 마스킹)
  public static String ipMasking(String phoneNo) throws Exception {
    String[] phoneNoArray = phoneNo.split(",");
    String regex = "(\\d{1,3}).?(\\d{1,3}).?(\\d{1,3}).?(\\d{1,3})$";
    String retIpNo = "";
    for (int ii = 0; ii < phoneNoArray.length; ii++) {
      String phoneNoOne = phoneNoArray[ii];
      retIpNo += ",";
      Matcher matcher = Pattern.compile(regex).matcher(phoneNoOne);
      if (matcher.find()) {
        String target = matcher.group(4);
        int length = target.length();
        char[] c = new char[length];
        Arrays.fill(c, '*');
        retIpNo += phoneNoOne.replace(target, String.valueOf(c));
      }
    }
    return retIpNo.substring(1);
  }

  // 이메일 마스킹(앞3자리 이후 '@'전까지 마스킹)
  public static String emailMasking(String email) throws Exception {
    String regex = "^(.+)@(.+)$";
    Matcher matcher = Pattern.compile(regex).matcher(email);
    if (matcher.find()) {
      String target = matcher.group(1);
      int length = target.length();
      if (length > 3) {
        char[] c = new char[length - 3];
        Arrays.fill(c, '*');

        return email.replace(target, target.substring(0, 3) + String.valueOf(c));
      }
    }
    return email;
  }

  // 계좌번호 마스킹(뒤 5자리)
  public static String accountNoMasking(String accountNum) throws Exception {
    // 계좌번호 앞 3자리, 맨 뒤 3자리를 빼고 마스킹
    if (accountNum != null && !"".equals(accountNum)) {
      // 계좌번호 가운데 글자 마스킹 변수 선언
      String middleMask = accountNum.substring(3, accountNum.length() - 3);
      // 마스킹 변수 선언(*)
      String masking = ""; // 앞 3자리, 맨뒤 3자리를 빼고 모두 마스킹 하기위한 증감값
      for (int i = 0; i < middleMask.length(); i++) {
        masking += "*";
      }
      accountNum = accountNum.replace(middleMask, masking);
    }
    return accountNum;
  }


  //사업자등록번호 마스킹(뒤 3자리)
  public static String businessRegistrationNoMasking(String val) throws Exception {
    // 사업자등록번호
    if (val != null && !"".equals(val)) {
      // 사업자등록번호 가운데 글자 마스킹 변수 선언
      String middleMask = val.substring(7, val.length());
      // 마스킹 변수 선언(*)
      String masking = ""; // 앞 3자리, 맨뒤 3자리를 빼고 모두 마스킹 하기위한 증감값
      for (int i = 0; i < middleMask.length(); i++) {
        masking += "*";
      }
      val = val.replace(middleMask, masking);
    }
    return val;
  }

  //법인 등록번호 마스킹(뒤 3자리)
  public static String corperateRegistrationNoMasking(String val) throws Exception {
    // 법인 등록번호 앞 3자리, 맨 뒤 3자리를 빼고 마스킹
    if (val != null && !"".equals(val)) {
      // 법인 등록번호 가운데 글자 마스킹 변수 선언
      String middleMask = val.substring(6, val.length());
      // 마스킹 변수 선언(*)
      String masking = ""; // 앞 3자리, 맨뒤 3자리를 빼고 모두 마스킹 하기위한 증감값
      for (int i = 0; i < middleMask.length(); i++) {
        masking += "*";
      }
      val = val.replace(middleMask, masking);
    }
    return val;
  }


  //우편번호 (뒤자리3)
  public static String zipCdMasking(String val) throws Exception {
    // 법인 등록번호 앞 3자리, 맨 뒤 3자리를 빼고 마스킹
    if (val != null && !"".equals(val)) {
      // 법인 등록번호 가운데 글자 마스킹 변수 선언
      String middleMask = val.substring(2, val.length());
      // 마스킹 변수 선언(*)
      String masking = ""; // 앞 3자리, 맨뒤 3자리를 빼고 모두 마스킹 하기위한 증감값
      for (int i = 0; i < middleMask.length(); i++) {
        masking += "*";
      }
      val = val.replace(middleMask, masking);
    }
    return val;
  }


  // 주소 마스킹(신주소, 구주소, 도로명 주소 숫자만 전부 마스킹)
  public static String addressMasking(String address) throws Exception {
    // 신(구)주소, 도로명 주소
    String regex = "(([가-힣]+(\\d{1,5}|\\d{1,5}(,|.)\\d{1,5}|)+(읍|면|동|가|리))(^구|)((\\d{1,5}(~|-)\\d{1,5}|\\d{1,5})(가|리|)|))([ ](산(\\d{1,5}(~|-)\\d{1,5}|\\d{1,5}))|)|";
    String newRegx = "(([가-힣]|(\\d{1,5}(~|-)\\d{1,5})|\\d{1,5})+(로|길))";
    Matcher matcher = Pattern.compile(regex).matcher(address);
    Matcher newMatcher = Pattern.compile(newRegx).matcher(address);
    if (matcher.find()) {
      return address.replaceAll("[0-9]", "*");
    } else if (newMatcher.find()) {
      return address.replaceAll("[0-9]", "*");
    }
    return address;
  }

  public String getTempToken(String partnerId, String uuid, String partnerStoreNo, long millis) throws Exception {
    Map<String, Object> claims = new HashMap<>();
    Long expiredTime = 1000 * 60L * 60L * 60L * 60L * 60L * 60L * 60L * 1L; // 토큰 유효 시간 (1시간)

    //int expiredTime = (60*10000); // 토큰 유효 시간 (10분)
    Date date = new Date(); // 토큰 만료 시간
    date.setTime(date.getTime() + expiredTime);
    claims.put("partnerId", partnerId);
    claims.put("partnerStoreNo", partnerStoreNo);
    claims.put("millis", millis);
    return Jwts.builder().setClaims(claims)
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(date) // 토큰 만료 시간 설정
      .signWith(SignatureAlgorithm.HS512, "eb686003-50ad-4db1-9b3a-b82592ffe211").compact();

  }

  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey("eb686003-50ad-4db1-9b3a-b82592ffe211").parseClaimsJws(token).getBody();
  }

  public boolean validateJwt(String jwt) {
    boolean flag = true;
    try {
      Jwts.parser().setSigningKey("eb686003-50ad-4db1-9b3a-b82592ffe211").parseClaimsJws(jwt);
    } catch (Exception e) {
      flag = false;
    }
    return flag;
  }

  /***
   *  랜덤 숫자
   * @param numchars
   * @return
   */
  public String getRandomString(int numchars) {
    Random r = new Random();
    StringBuffer sb = new StringBuffer();
    while (sb.length() < numchars) {
      sb.append(Integer.toUnsignedString(r.nextInt()));
    }
    return sb.toString().substring(0, numchars);
  }

}

