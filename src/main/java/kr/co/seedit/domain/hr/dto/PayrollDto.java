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
public class PayrollDto implements Serializable {
    
    private String hireDate;
    private String hireDiv; // RETIRE, NEW, ''
    private String definedName; // 직급
    private String departmentId; // 부서 아이디
    private String departmentName; // 시급제 부서(ex.머시닝)
    private String koreanName; // 이름
    private Double employeeType; // 100 연봉제
    private Double monthSalary; // 월급여
    private Double yearSalary; // 년봉
    private Double dayPay; // 일급
    private Double hourlyPay; // 시급
    private Double totalTime; // 총근무시간
    private Double basicSalary; // 기본금
    private Double annualLeaveAmt; // 연차수당
    private Double overtime1; // 연장1 시간
    private Double overtime1Standard; // 연장1 기준시간
    private Double overtime01Amt; // 연장1 수당
    private Double overtime2; // 연장2 시간
    private Double overtime2Standard; // 연장2 기준시간
    private Double overtime02Amt; // 연장2 수당
    private Double nightShift1; // 야간1 시간
    private Double nightShift1Standard; // 야간1 표준시간 
    private Double night01Allowance; // 야간1 수당
    private Double daytimeHours; // 주간조야간
    private Double daytimeHoursAmt; // 주간조야간금액
    private Double nighttimeHours; // 야간조야간
    private Double nighttimeHoursAmt; // 야간조야간금액
    private Double nightShift2; // 야간2 시간
    private Double nightShift2Standard; // 야간2 표준시간
    private Double night02Allowance; // 야간2 수당
    private Double holidaySaturday1; // 휴일(토요일) 근무
    private Double holidaySaturday1Standard; // 휴일(토요일) 근무 표준시간
    private Double holiday01SaturdayAmt; // 휴일(토요일) 수당
    private Double holidaySunday1; // 휴일(일요일) 근무
    private Double holidaySunday1Standard; // 휴일(일요일) 근무 표준시간
    private Double holiday01SundayAmt; // 휴일(일요일) 수당
    private Double holiday2; // 휴일2 근무
    private Double holiday2Standard; // 휴일2 근무 표준시간
    private Double holiday02Amt; // 휴일2 수당 
    private String annualLeaveUsedDay; // 연차사용일자
    private Double annualLeaveUsed; // 연차사용횟수
    private Double transportation; // 교통비제공횟수
    private Double attribute15; // 교통비
    private Double meal; // 식대제공횟수
    private String halfDay; // 반차사용일자
    private Double halfTime; // 반차사용시간
    private Double halfTimeAmt; // 반차사용금액
    private String earlyLeaveDay; // 조퇴일자
    private Double earlyLeaveTime; // 조퇴횟수 
    private Double earlyLeaveAmt; // 조퇴차감금액
    private String lateDay; // 지각일자
    private Double lateTime; // 지각시간
    private Double lateAmt; // 지각차감금액
    private String outerDay; // 외출일자
    private Double outerTime; // 외출시간
    private Double attribute17; // 외출차감금액
    private Double attribute16; // 식대금액
    private Double totalSalary; // 합계
}
