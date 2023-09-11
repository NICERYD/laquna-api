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
	private Double employeeId; // 사원아이디
	private String employeeNumber; // 사번
	private String hireDate;
	private String hireDiv; // RETIRE, NEW, ''
	private String definedName; // 직급
	private String departmentId; // 부서 아이디 //NOT USED
	private String departmentName; // 시급제 부서(ex.머시닝) //NOT USED
	private String koreanName; // 이름
	private Double employeeType; // 100 연봉제
	private Double salaryAmt; // 월급/시급
	private Double salaryAmt2; // 연봉(월급*13)/시급(1hour)
	private Double totalTime; // hdmkt.total_time
	private Double basicAmount; // 기본금액

	// 연장수당
	private Double overtimeDaytime01; // 연장시간01
	private Double overtimeAllowance01; // 연장수당01
	private Double overtimeDaytime02; // 연장시간02
	private Double overtimeAllowance02; // 연장수당02

	// 야간수당
	private Double ntDaytime01; // 야간수당01 : 주간(시급) 시간
	private Double ntDayAllowance01; // 야간수당01 : 주간(시급) 수당
	private Double ntNighttime01; // 야간수당01 : 야간(연봉/시급) 시간
	private Double ntNightAllowance01; // 야간수당01 : 야간(연봉/시급) 수당
	private Double ntNighttime02; // 야간수당02 시간
	private Double ntNightAllowance02; // 야간수당02 수당

	// 휴일수당
	private Double holidaySatTime01; // 휴일수당01 : 토요일 시간
	private Double holidaySatAllowance01; // 휴일수당01 : 토요일 수당
	private Double holidaySumTime01; // 휴일수당01 : 토요일 시간
	private Double holidaySunAllowance01; // 휴일수당01 : 토요일 수당
	private Double holidayTime02; // 휴일수당02 시간
	private Double holidayAllowance02; // 휴일수당02 수당

	// 연차수당
	private String annualLeaveUsedDay; // 연차사용일자
	private String annualLeaveUsed; // 연차사용
	private Double attribute01; // 연차수당
	// 연차정산
	private String annualLeaveCalc; //연차정산횟수 
	private Double attribute24; //연차정산금액
	// 연차정산2

	// 반차
	private String halfLeaveUsedDay; // 반차사용일자
	private Double halfLeaveUsed; // 반차사용
	private Double attribute15; // 반차수당

	// 조퇴
	private String earlyLeaveDay; // 조퇴사용일자
	private Double earlyLeaveTime; // 조퇴사용
	private Double attribute13; // 조퇴수당

	// 지각
	private String lateDay; // 지각일자
	private String lateCnt; // 지각횟수
	private Double lateTime; // 지각시간
	private Double attribute14; // 지각수당

	// 외출
	private String outerDay; // 외출일자
	private Double outerTime; // 외출시간
//	@Builder.Default
//	private Double attribute17 = 0d; // 외출수당
	private Double attribute17; // 외출수당

	// 초과상여/수당
	private Double other02Used; // 초과상여 사용
	private Double attribute16; // 초과상여/수당

	// 보조금(교통비, 식대)
	private Double attribute11; // 교통비
	private Double attribute12; // 식대
	
	/**
	 * DTO for PersonalPayroll
	 */
	private Double ppAnnualLeaveUsed;	//연차횟수
	private Double ppHalfLeaveUsed;		//반차횟수
	private Double ppLateTime;		//지각시간 (0:00)
	private Double ppLateCnt;		//지각횟수
	private Double ppAbsence;		//결근횟수
	
	
	//시급제
	private Double ppOverTime;		//연장시간 (0:00)
	private Double ppHolidayNightCnt;	//공휴야간횟수
	private Double ppNightTime;		//야간근무시간 (0:00)
	private Double ppNightCnt;		//야간일수
	private Double ppNight;			//야간 (0.0시간)
	
	private Double ppHolidaySatTime;	//토요출근시간 (0:00)
	private Double ppHolidaySunTime;	//휴일출근시간 (0:00)
	
	private Double ppEarlyLeaveUsed;	//조퇴횟수

	
}
