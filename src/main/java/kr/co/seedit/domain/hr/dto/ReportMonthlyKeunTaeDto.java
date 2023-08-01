package kr.co.seedit.domain.hr.dto;





import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ReportMonthlyKeunTaeDto {
    private Integer companyId;
    private Integer employeeId;
    private String yyyymm;
    private Double annualLeaveUsed;
    private String annualLeaveUsedDay;
    private Double overTime1;
    private Double overTime2;
    private Double nightShift1;
    private Double nightShift2;
    private Double dayTimeHours;
    private Double nightTimeHours;
    private Double holidaySaturday1;
    private Double holidaySunday1;
    private Double holiday2;
    private Double transportation;
    private Double meal;
    private Double other;
    private String halfDay;
    private Double halfTime;
    private String earlyLeaveDay;
    private Double earlyLeaveTime;
    private String lateDay;
    private Double lateTime;

    private Integer loginUserId;
    
    private String koreanName;		//성명
    private String definedName;		//직책
    private String departmentName;	//부서명
    private String hireDate; 		//입사일
    
}
