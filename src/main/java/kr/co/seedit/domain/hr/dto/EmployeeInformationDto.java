package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class EmployeeInformationDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 5545801112963894581L;

    private String workDate;
    private String estName;
    private String departmentName;
    private String employeeNumber;
    private String koreanName;
    private String definedName;
    private String workStatus;
    private String workStartDate;
    private String workEndDate;
    private String inStatus;
    private String outStatus;
    private String outStartDate;
    private String outEndDate;
    private String outTime;
    private String lateTime;
    private String overTime;
    private String nightTime;
    private String holidayTime;
    private String realWorkTime;
    private String defaultTime;
    private String codeField;
    private Integer codeId;
    private Integer companyId;
    private String attribute1;
    private Integer estId;
    private String yyyymm;
}