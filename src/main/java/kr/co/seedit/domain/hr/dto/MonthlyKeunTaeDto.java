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
    private Integer halfLeaveCnt;
    private Double overtimeDaytime;
    private Double overtimeNighttime;
    private Integer overTimeDay2HCnt;
    private Integer overTimeDay4H3MCnt;
    private Double nightDaytime;
    private Double nightNighttime;
    private Double holidaySaturday;
    private Integer holidaySaturdayDay4HCnt;
    private Integer holidaySaturdayDay8HCnt;
    private Double holidaySunday;
    private Integer holidaySundayDay4HCnt;
    private Integer holidaySundayDay8HCnt;
    private Double holiday02;
    private Double transportation;
    private Double meal;
    private Double other;
    private String earlyLeaveDay;
    private Double earlyLeaveTime;
    private Integer earlyLeaveCnt;
    private String lateDay;
    private Integer latecnt;
    private Double lateTime;
    private String outerDay;
    private Double outerTime;
    private Integer loginUserId;
    private Integer absence;
    private Double totalTime;
    private Integer totalDay;
    private Integer juhueDay;
    private Integer NS01DayCnt;

}
