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
	private String hireDate;
	private String hireDiv; // RETIRE, NEW, ''
	private String definedName; // 직급
	private String departmentId; // 부서 아이디
	private String departmentName; // 시급제 부서(ex.머시닝)
	private String koreanName; // 이름
	private Double employeeType; // 100 연봉제
	private Double basicAmount; // 기본금
	private Double hourlyPay; // 시급(시급제 전용)
	private Double annualAllowance; // 연차수당
	private Double annualLeave; // 연차수당
	private Double rtAnnualLeaveUsed; // 연차사용횟수
	private String rtAnnualLeaveUsedDay; // 연차일자
	private Double halfTimeAllowance; // 반차수당
	private Double rtHalfTime; // 반차사용횟수
	private String rtHalfDay; // 반차사용일자
	private Double overtimeAllowance01; // 연장1 수당
	private Double rtOverTime01; // 연장1 수당
	private Double rtOverDayTimeHours; // 연장1 - 주간
	private Double rtOverNightTimeHours; // 연장1 - 야간
	private Double overtimeAllowance02; // 연장2 수당(연봉제 전용)
	private Double nightAllowance01; // 야간1 수당
	private Double rtNSDayTimeHours; // 야간1- 주간
	private Double rtNSNightTimeHours; // 야간1- 야간
	private Double nightAllowance02; // 야간2 수당(연봉제 전용)
	private Double rtNightShift02; // 야간2 수당(연봉제 전용)
	private Double holidayAllowance01; // 휴일1 수당
	private Double rtHolidaySaturday01; // 휴일1-토요일
	private Double rtHolidaySunday01; // 휴일1-일요일
	private Double holidayAllowance02; // 휴일2(연봉제 전용)
	private Double rtHoliday02; // 휴일2(연봉제 전용)
	private Double transportationAmount; // 보조금-교통비(시급제 전용)
	private Double rtTransportation; // 보조금-교통비(시급제 전용)
	private Double mealsAmount; // 보조금-식대(시급제 전용)
	private Double rtMeal; // 보조금-식대(시급제 전용)
	private Double otherAmount; // 기타
	private Double rtOther; // 기타
	private Double lateAmount; // 지각차감금액(시급제 전용)
	private Double rtLateTime; // 지각시간
	private String rtLateDay; // 지각일자
	private Double earlyLeaveAmount; // 조퇴차감금액(시급제 전용)
	private Double rtEarlyLeaveTime; // 조퇴시간(시급제 전용)
	private String rtEarlyLeaveDay; // 조퇴일자(시급제 전용)
	private Double outerAmount; // 외출차감금액(시급제 전용)
	private Double rtOuterTime; // 외출시간(시급제 전용)
	private String rtOuterDay; // 외출일자(시급제 전용)
	private Double rtTotalTime; // 총근무시간
    private Double totalSalary; // 합계
}
