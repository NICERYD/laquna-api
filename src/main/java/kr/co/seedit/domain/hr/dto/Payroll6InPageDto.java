package kr.co.seedit.domain.hr.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Payroll6InPageDto implements Serializable {

	/**
	 * DTO for Payroll6InPage service
	 */
	private static final long serialVersionUID = 1L;
	@Builder.Default
	private Double employeeId = 0d; // 사원아이디
	private String employeeNumber; // 사번
	private String hireDate;
	private String hireDiv; // RETIRE, NEW, ''
	private String definedName; // 직급
	private String departmentId; // 부서 아이디 //NOT USED
	private String departmentName; // 시급제 부서(ex.머시닝) //NOT USED
	private String koreanName; // 이름
	private String employeeType; // 100 연봉제
	@Builder.Default
	private Double salaryAmt = 0d; // 월급/시급
	@Builder.Default
	private Double salaryAmt2 = 0d; // 연봉(월급*13)/시급(1hour)
	@Builder.Default
	private Double totalTime = 0d; // hdmkt.total_time
	@Builder.Default
	private Double basicAmount = 0d; // 기본금액

	// 연장수당
	@Builder.Default
	private Double overtimeDaytime01 = 0d; // 연장시간01
	@Builder.Default
	private Double overtimeAllowance01 = 0d; // 연장수당01
	@Builder.Default
	private Double overtimeDaytime02 = 0d; // 연장시간02
	@Builder.Default
	private Double overtimeAllowance02 = 0d; // 연장수당02

	// 야간수당
	@Builder.Default
	private Double ntDaytime01 = 0d; // 야간수당01 : 주간(시급) 시간
	@Builder.Default
	private Double ntDayAllowance01 = 0d; // 야간수당01 : 주간(시급) 수당
	@Builder.Default
	private Double ntNighttime01 = 0d; // 야간수당01 : 야간(연봉/시급) 시간
	private String ns01DayCnt ; // 야간01 : 연봉제 횟수
	@Builder.Default
	private Double ntNightAllowance01 = 0d; // 야간수당01 : 야간(연봉/시급) 수당
	@Builder.Default
	private Double ntNighttime02 = 0d; // 야간수당02 시간
	@Builder.Default
	private Double ntNightAllowance02 = 0d; // 야간수당02 수당
	@Builder.Default
	private Double nightTeamPlus = 0d; // 야간수당02 수당

	// 휴일수당
	@Builder.Default
	private Double holidaySatTime01 = 0d; // 휴일수당01 : 토요일 시간
	@Builder.Default
	private Double holidaySatAllowance01 = 0d; // 휴일수당01 : 토요일 수당
	@Builder.Default
	private Double holidaySunTime01 = 0d; // 휴일수당01 : 일요일 시간
	@Builder.Default
	private Double holidaySunAllowance01 = 0d; // 휴일수당01 : 일요일 수당
	@Builder.Default
	private Double holidayTime02 = 0d; // 휴일수당02 시간
	@Builder.Default
	private Double holidayAllowance02 = 0d; // 휴일수당02 수당
	private Integer NSHoliDayCnt;

	// 연차수당
	private String annualLeaveUsedDay; // 연차사용일자
	private String annualLeaveUsed; // 연차사용
	@Builder.Default
	private Double attribute01 = 0d; // 연차수당
	// 연차정산
	private String annualLeaveCalc; //연차정산횟수 
	@Builder.Default
	private Double attribute23 = 0d; //연차정산금액
	// 연차정산2

	// 반차
	private String halfLeaveUsedDay; // 반차사용일자
	@Builder.Default
	private Double halfLeaveUsed = 0d; // 반차사용
	@Builder.Default
	private Double attribute15 = 0d; // 반차수당

	// 조퇴
	private String earlyLeaveDay; // 조퇴사용일자
	@Builder.Default
	private Double earlyLeaveTime = 0d; // 조퇴사용
	@Builder.Default
	private Double attribute13 = 0d; // 조퇴수당

	// 지각
	private String lateDay; // 지각일자
	private String lateCnt; // 지각횟수
	@Builder.Default
	private Double lateTime = 0d; // 지각시간
	@Builder.Default
	private Double attribute14 = 0d; // 지각수당

	// 외출
	private String outerDay; // 외출일자
	@Builder.Default
	private Double outerTime = 0d; // 외출시간
	@Builder.Default
	private Double attribute17 = 0d; // 외출수당

	// 초과상여/수당
	@Builder.Default
	private Double other02Used = 0d; // 초과상여 사용
	@Builder.Default
	private Double attribute16 = 0d; // 초과상여/수당

	// 보조금(교통비, 식대)
	@Builder.Default
	private Double transpotation = 0d; // 교통비 횟수
	@Builder.Default
	private Double attribute11 = 0d; // 교통비
	@Builder.Default
	private Double meal = 0d; // 식대 횟수
	@Builder.Default
	private Double attribute12 = 0d; // 식대
	/**
	* DTO for PersonalPayroll
	*/
	@Builder.Default
	private Double annualLeaveUsedCnt = 0d; //연차횟수
	private String halfLeaveCnt; //반차횟수
	@Builder.Default
	private Double lateCntNum = 0d; //지각횟수
	@Builder.Default
	private Double absenceCnt = 0d; //결근횟수
	@Builder.Default
	private Double overtimeDay2HCnt = 0d; //평일연장2h
	@Builder.Default
	private Double overTimeDay4H3MCnt = 0d; //철야익일4.5h
	@Builder.Default
	private Double holidaySaturday = 0d; //토요출근시간
	@Builder.Default
	private Double holidaySaturdayDay4HCnt = 0d; //토요4h
	@Builder.Default
	private Double holidaySaturdayDay8HCnt = 0d; //토요8h
	@Builder.Default
	private Double holidaySunday = 0d; //토요출근시간
	@Builder.Default
	private Double holidaySundayDay4HCnt = 0d; //일요4h
	@Builder.Default
	private Double holidaySundayDay8HCnt = 0d; //일요8h
	//시급제
	@Builder.Default
	private Double holidayNightCnt = 0d; //공휴야간횟수
	@Builder.Default
	private Double nightCnt = 0d; //야간일수
	@Builder.Default
	private Double holidaySatTime = 0d; //토요출근시간 (0:00)
	@Builder.Default
	private Double holidaySunTime = 0d; //휴일출근시간 (0:00)
	@Builder.Default
	private Double earlyLeaveUsedCnt = 0d; //조퇴횟수

	@Builder.Default
	private Double totalAmt = 0d; // 합계
	
}
