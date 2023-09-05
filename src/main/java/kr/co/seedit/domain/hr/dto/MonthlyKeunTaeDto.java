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
    private Double halfLeaveUsed;
    private String halfLeaveUsedDay;
    private Double overtimeDaytime;
    private Double overtimeNighttime;
    private Double nightDaytime;
    private Double nightNighttime;
    private Double holidaySaturday;
    private Double holidaySunday;
    private Double holiday02;
    private Double transportation;
    private Double meal;
    private Double other;
    private String earlyLeaveDay;
    private Double earlyLeaveTime;
    private String lateDay;
    private Integer latecnt;
    private Double lateTime;
    private String outerDay;
    private Double outerTime;
    private Integer loginUserId;
    private Integer absence;
    private Double totalTime;
}
