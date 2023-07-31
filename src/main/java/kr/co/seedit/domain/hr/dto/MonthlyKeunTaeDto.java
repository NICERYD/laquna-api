package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class MonthlyKeunTaeDto {
    private Integer companyId;
    private Integer employeeId;
    private String yyyymm;
    private Double annualLeaveUsed;
    private String annualLeaveUsedDay;
    private Double overTime01;
    private Double overTime02;
    private Double nightShift01;
    private Double nightShift02;
    private Double dayTimeHours;
    private Double nightTimeHours;
    private Double holidaySaturday01;
    private Double holidaySunday01;
    private Double holiday02;
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
}
