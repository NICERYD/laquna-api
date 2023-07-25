package kr.co.seedit.domain.hr.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ADTExcelDto implements Serializable {
    private Integer companyId;
    private String workDate;
    private String estName;
    private String departmentName;
    private String koreanName;
    private String positionCode;
    private String definedName;
    private String workStatus;
    private String workStartDate;
    private String workEndDate;
    private String inStatus;
    private String outStatus;
    private String lateTime;
    private String overTime;
    private String nightTime;
    private String holidayTime;
    private String realWorkTime;
    private String totalTime;
    private String defaultTime;
    private String eventTime;	//(외출,복귀) 발생시간
    private String eventType;	//(외출,복귀) 타입
    private String employeeNumber;
    private String yyyymm;
    private Integer loginUserId;
}
