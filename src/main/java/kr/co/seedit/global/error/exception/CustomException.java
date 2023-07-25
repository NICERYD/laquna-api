package kr.co.seedit.global.error.exception;


import java.io.Serial;

public class CustomException extends RuntimeException {
  /**
   *
   */
  @Serial
  private static final long serialVersionUID = -5967682556645022196L;
  public static final String ERR_0001 = "이미 계정 신청된 메일이 존재합니다.";
  public static final String ERR_0002 = "이미 활성화 된 메일이 존재합니다.";
  public static final String ERR_0000 = "";
//  public static final String ERR_0001 = "0001,알 수 없는 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.";
//  public static final String ERR_0002 = "0002,알 수 없는 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.";

  public static final String ERR_1200 = "1200,새 비밀번호는 현재 비밀번호와 다르게 입력해주세요.";
  public static final String ERR_1201 = "1201,비밀번호 변경이 실패하였습니다.";
  public static final String ERR_1202 = "1202,현재 비밀번호를 확인해주세요.";
  public static final String ERR_1203 = "1203,현재 비밀번호를 입력해주세요.";
  public static final String ERR_1204 = "1204,새 비밀번호를 입력해주세요.";
  public static final String ERR_1205 = "1205,새 비밀번호는 10자 이상, 영문/숫자/특수문자 중 2개 이상 조합으로 입력해주세요.";
  public static final String ERR_1210 = "1210,새 비밀번호는 40자 이내로 입력해주세요.";
  public static final String ERR_1301 = "1301,파일 조회에 실패했습니니다.";
  public static final String ERR_1302 = "1302,다운받을 파일이 존재하지 않습니다.";

  public static final String ERR_3001 = "3001,처리할 데이터 찾을수 없음";
  public static final String ERR_3002 = "3002,데이터가 없습니다.";

  public static final String ERR_4001 = "4001,파일 업로드에 실패 하였습니다.";
  public static final String ERR_4002 = "4002,이미지 업로드 정보 조회에 실패하였습니다.";
  public static final String ERR_4003 = "4003,최대 500장 까지 업로드 할수 있습니다.";
  public static final String ERR_4004 = "4004,1장당 최대 15MB(대표이미지5MB), 전체 500MB 까지 업로드 할수 있습니다..";

  public static final String ERR_8976 = "8976,비밀번호는 최소 1일 이상 사용해야 합니다";
  public static final String ERR_8977 = "8977,기존 비밀번호와 새 비밀번호가 일치합니다";
  public static final String ERR_8978 = "8978,로그인을 5회 이상 실패하여 10분동안 로그인이 불가합니다 \n 아이디와 비밀번호를 확인 후 10분뒤에 다시 시도 해주시길바랍니다";
  public static final String ERR_8979 = "8979,로그인한지 90일이 지났습니다";
  public static final String ERR_8980 = "8980,비밀번호 재설정이  90일이 지났습니다";

  public static final String ERR_8999 = "8999,아이디 또는 비밀번호를 확인해주세요";
  public static final String ERR_8004 = "8004,승인필수 제품군이 선택되지 않았습니다.";
  public static final String ERR_9787 = "9787,일치하는 정보가없습니다.";
  public static final String ERR_9788 = "9788,토큰 인증정보가 없습니다.";
  public static final String ERR_9789 = "9789,로그인이 만료 되었습니다. 다시 로그인해주세요";

  public static final String ERR_9996 = "9996,중복된 아이디 입니다.";
  public static final String ERR_9997 = "9997,진행 가능한 상품이 아닙니다.";
  public static final String ERR_9998 = "9998,정의되지않은 에러코드.";
  public static final String ERR_9999 = "9999,서버 에러입니다.";
  public static final String PC04_A09 = "0409,비밀번호가 재설정되었습니다.";
  public static final String PC04_A16 = "0416,비밀번호 재설정을 실패하였습니다.";
  public static final String PC04_A17 = "0417,두 비밀번호가 일치하지않습니다.";
  public static final String PC04_A18 = "0418,비밀번호는 10자 이상입니다.";
  public static final String PC04_A10 = "0410,입력이 잘못되었습니다.다시 확인해 주세요";
  public static final String PC04_A15 = "0415,인증정보가 일치하지 않습니다.";
  public static final String PC04_A19 = "0419,비밀번호 변경일자 최신화 실패.";
  public static final String PC02_A04 = "A04,잘못된 또는 중복된 휴대폰 번호 입니다. 확인 후 다시 시도해 주세요.";


    public CustomException(String msg) {
    super(msg);
  }
}