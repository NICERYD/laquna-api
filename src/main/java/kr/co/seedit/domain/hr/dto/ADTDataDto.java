package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ADTDataDto {
    private Integer employeeId;
    private String workDate;
    private String workStatus;
    private String workStartDate;
    private String workEndDate;
    private String inStatus;
    private String outStatus;
    private String outTime;
    private String lateTime;
    private String overtime;
    private String nightTime;
    private String holidayTime;
    private String realWorkTime;
    private String defaultTime;
    private String employeeType;
    private String dateType;
    private String dateWeek;
    private String description;
    private String employeeNumber;
}
